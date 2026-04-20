package com.anbima.desafio.pedido_gateway.service;

import com.anbima.desafio.pedido_gateway.entity.Pedido;
import com.anbima.desafio.pedido_gateway.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public Pedido tratamentoEntrada(String linha) {

        if (linha == null || linha.length() > 40) {
            throw new RuntimeException("Entrada invalida !!!");
        }

        String[] partes = linha.trim().split("\\s+");

        String tipoLanche;
        String proteina;
        String acompanhamento;
        int quantidade;
        String bebida;

        // Caso a String venha da Requisição via API
        if (partes.length >= 4) {
            //Recorta a entrada nos tipos e preenche com espaço a direita se for menor que 10
            tipoLanche = StringUtils.rightPad(StringUtils.substring(partes[0], 0, 10), 10, " ");
            proteina = StringUtils.rightPad(StringUtils.substring(partes[1], 0, 10), 10, " ");
            acompanhamento = StringUtils.rightPad(StringUtils.substring(partes[2], 0, 10), 10, " ");

            // Pega a quantidade + bebida e junta ["02", "COCA"] para ["02COCA"]
            StringBuilder restoBuilder = new StringBuilder();

            for (int i = 3; i < partes.length; i++) {
                restoBuilder.append(partes[i]);
            }
            String resto = restoBuilder.toString();
            //log.info("Resto do Pedido:" + resto);

            // Pega apenas os números do resto
            String apenasNumeros = resto.replaceAll("\\D", "");
            //log.info("Teste 0: " + apenasNumeros);

            // Pega no máximo os 2 primeiros caracteres usando o StringUtils para segurança
            String qtdOriginal = StringUtils.substring(apenasNumeros, 0, 2);
            //log.info("Teste 1 :" + qtdOriginal);

            // leftPad preenche com zeros à esquerda (ex: "2" vira "02")
            String quantidadeStr = StringUtils.leftPad(qtdOriginal, 2, "0");
            //log.info("Teste 2 :" + quantidadeStr);

            if (!quantidadeStr.matches("\\d{2}") || quantidadeStr.equals("00")) {
                throw new IllegalArgumentException("Entrada da quantidade deve ser numérica (01 até 99)." +
                        " Valor recebido: " + qtdOriginal);
            }
            quantidade = Integer.parseInt(quantidadeStr);

            // Remove a quantidade da string para sobrar só o texto que seria a bebida
            String bebidaOriginal = resto.replaceFirst(qtdOriginal, "");

            // Corta no máximo em 8 e preenche com espaços à direita
            bebida = StringUtils.rightPad(StringUtils.substring(bebidaOriginal, 0, 8), 8, " ");
        } else {

            // Caso a String venha do Front end já formatada

            tipoLanche = linha.substring(0, 10); // 1 a 10
            proteina = linha.substring(10, 20); // 11 a 20
            acompanhamento = linha.substring(20, 30); // 21 a 30

            // Verificar se a quantidade veio em forma de numérica
            String quantidadeStr = linha.substring(30, 32); // 31 a 32
            if (!quantidadeStr.matches("\\d{2}")) {
                throw new RuntimeException("Entrada deve ser numérica (01 até 99)");
            }
            // Transformar quantidade em Inteiro
            quantidade = Integer.parseInt(quantidadeStr);

            bebida = linha.substring(32, 40); // 33 a 40


        }


        tipoLanche = tipoLanche.toUpperCase();
        proteina = proteina.toUpperCase();
        acompanhamento = acompanhamento.toUpperCase();
        bebida = bebida.toUpperCase();


        // Transformar a 'String' em um Objeto do tipo Pedido
        Pedido pedido = Pedido.builder().
                tipoLanche(tipoLanche).proteina(proteina).
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
    BigDecimal calcularPreco(Pedido pedido){

        BigDecimal precoBase;

        if (pedido.getTipoLanche().trim().equalsIgnoreCase("HAMBURGUER")) {
             precoBase = new BigDecimal("20.00");
        } else if (pedido.getTipoLanche().trim().equalsIgnoreCase("PASTEL")) {
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
        return pedido.getTipoLanche().trim().equalsIgnoreCase("HAMBURGUER") &&
                pedido.getProteina().trim().equalsIgnoreCase("CARNE") &&
                pedido.getAcompanhamento().trim().equalsIgnoreCase("SALADA");
    }
}
