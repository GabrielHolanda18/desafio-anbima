package com.anbima.desafio.pedido_gateway.service;

import com.anbima.desafio.pedido_gateway.entity.Pedido;
import com.anbima.desafio.pedido_gateway.repository.PedidoRepository;
import org.junit.jupiter.api.Test;           // Marca o metodo como um teste
import org.junit.jupiter.api.DisplayName;    // Dá um nome ao teste
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    //Usa mock para simular objetos para os testes

    @Mock
    private PedidoRepository repository; // Simula um repository

    @Mock
    private RabbitTemplate rabbitTemplate;  // Simula um rabbitTemplate

    //Cria o Service e "injeta" as simulações lá dentro automaticamente
    @InjectMocks
    private PedidoService service;

    @Test
    @DisplayName("Deve salvar o pedido e enviar para a fila com sucesso")
    void processarPedidoCompleto(){

        // ARRANGE - Configuração do cenário
        String stringValida = "HAMBURGUER CARNE     SALADA   01COCA    ";

        // simular que o banco gerou o ID 1
        Pedido pedidoSalvo = Pedido.builder().id(1L).status("RECEBIDO").build();

        // Quando o repository tentar salvar QUALQUER pedido, retorne o nosso pedido com ID 1
        when(repository.save(any(Pedido.class))).thenReturn(pedidoSalvo);

        // ACT - Execução da ação
        Pedido resultado = service.tratamentoEntrada(stringValida);

        // ASSERT - Verificações com Mockito

        // Verifica se o repository.save foi chamado
        verify(repository).save(any(Pedido.class));

        // Verifica se a mensagem foi enviada para a fila correta com o ID certo
        // O verify garante que o "contrato" de envio foi cumprido
        verify(rabbitTemplate).convertAndSend(eq("pedidos.recebidos"), any(Object.class));

        // Verifica se o retorno do metodo é o pedido que o banco "salvou"
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getStatus()).isEqualTo("RECEBIDO");
    }

}
