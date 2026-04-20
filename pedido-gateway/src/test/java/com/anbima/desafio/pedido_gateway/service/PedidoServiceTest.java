package com.anbima.desafio.pedido_gateway.service;

import com.anbima.desafio.pedido_gateway.entity.Pedido;
import com.anbima.desafio.pedido_gateway.repository.PedidoRepository;
import org.junit.jupiter.api.Test;           // Marca o método como um teste
import org.junit.jupiter.api.DisplayName;    // Dá um nome amigável ao teste
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*; // Importa as validações (assertEquals, etc)

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
    @DisplayName("Deve calcular o valor total sem desconto")
    void deveCalcularPrecoTotal() {
        Pedido pedido = Pedido.builder()
                .tipoLanche("PASTEL")
                .proteina("FRANGO")
                .acompanhamento("BACON")//
                .bebida("SUCO")
                .quantidade(2)
                .build();

        assertThat(service.calcularPreco(pedido)).isEqualByComparingTo("30.00");
    }

    @Test
    @DisplayName("Deve calcular o valor com Desconto")
    void deveCalcularDesconto() {
        // Arrange
        Pedido pedido = Pedido.builder()
                .tipoLanche("HAMBURGUER")
                .proteina("CARNE")
                .acompanhamento("SALADA")
                .bebida("COCA")
                .quantidade(1)
                .build();

        assertThat(service.calcularPreco(pedido)).isEqualByComparingTo("18.00");
    }

    @Test
    @DisplayName("Deve retornar valor 0 se a quantidade for zero ou negativa")
    void deveValidarQuantidadeInvalida() {
        Pedido pedido = Pedido.builder()
                .tipoLanche("HAMBURGUER")
                .proteina("CARNE")
                .acompanhamento("SALADA")
                .quantidade(0)
                .build();

        assertThat(service.calcularPreco(pedido)).isEqualByComparingTo("0.00");
    }
}
