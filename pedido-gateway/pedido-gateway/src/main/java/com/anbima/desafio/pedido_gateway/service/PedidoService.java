package com.anbima.desafio.pedido_gateway.service;

import com.anbima.desafio.pedido_gateway.entity.Pedido;
import com.anbima.desafio.pedido_gateway.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public Pedido tratamentoEntrada(String linha) {

        if (linha == null || linha.length() != 40) {
            throw new RuntimeException("A entrada deve ter exatamente 40 caracteres." + linha.length());
        }

        // Repartindo a ‘String’ recebida para realizar a montagem do Pedido

        String tipoLache = linha.substring(0,10).trim().toUpperCase(); // 1 a 10
        String proteina = linha.substring(10,20).trim().toUpperCase(); // 11 a 20
        String acompanhamento =  linha.substring(20,30).trim().toUpperCase(); // 21 a 30

        // Verificar se a quantidade veio em forma de numérica

        String quantidadeStr = linha.substring(30,32).trim().toUpperCase(); // 31 a 32
        if (!quantidadeStr.matches("\\d{2}")){
            throw new RuntimeException("Entrada deve ser numérica (01 até 99)");
        }
        // Transformar quantidade em Inteiro
        Integer quantidade = Integer.parseInt(quantidadeStr);

        String bebida =  linha.substring(32,40).trim().toUpperCase(); // 33 a 40

        // Transformar a 'String' em um Objeto do tipo Pedido

        Pedido pedido = Pedido.builder().
                tipoLanche(tipoLache).proteina(proteina).
                acompanhamento(acompanhamento).
                quantidade(quantidade).
                bebida(bebida).status("RECEBIDO").
                build();

        BigDecimal valorPedido = calcularPreco(pedido);

        pedido.setValor(valorPedido);

        // Primeiro salvar o pedido para gerar o ID e depois enviar para fila

        Pedido pedidoSalvo = repository.save(pedido);

        //Montar a mensagem da fila pedidos.recebidos

        String mensagem = String.format("{\"pedidoId\": %d}", pedidoSalvo.getId());

        rabbitTemplate.convertAndSend("pedidos.recebidos", mensagem);

        return pedidoSalvo;
    }

    /*
    Preço base:
    HAMBURGUER → R$ 20,00
    PASTEL → R$ 15,00
    Outros → R$ 12,00
    Se for HAMBURGUER + CARNE + SALADA → Aplicar 10% de desconto
    valorTotal = precoBase * quantidade - desconto
     */
    private BigDecimal calcularPreco(Pedido pedido){

        BigDecimal precoBase;

        if (pedido.getTipoLanche().equals("HAMBURGUER")) {
             precoBase = new BigDecimal("20.00");
        } else if (pedido.getTipoLanche().equals("PASTEL")) {
            precoBase = new BigDecimal("15.00");
        } else {precoBase = new BigDecimal("12.00");}

        // Calculo do valor Total

        BigDecimal valorTotal = precoBase.multiply(new BigDecimal(pedido.getQuantidade()));

        //Calcular o Desconto

        if (comboHamburger(pedido)){
            return valorTotal.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
        }
        return valorTotal;
    }

    private boolean comboHamburger(Pedido pedido){
        return pedido.getTipoLanche().equals("HAMBURGUER") &&
                pedido.getProteina().equals("CARNE") &&
                pedido.getAcompanhamento().equals("SALADA");
    }
}
