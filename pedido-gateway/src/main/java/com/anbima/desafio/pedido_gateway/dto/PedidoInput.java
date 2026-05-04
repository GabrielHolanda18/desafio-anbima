package com.anbima.desafio.pedido_gateway.dto;

import com.anbima.desafio.pedido_gateway.entity.TipoLanche;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record PedidoInput(String tipoLanche, String proteina, String acompanhamento,
                          int quantidade, String bebida) {

    public static PedidoInput tratamento(String linha) {
        if (null == linha || linha.length() != 40) {
            throw new IllegalArgumentException("Linha deve ter exatamente 40 caracteres" + linha.length());
        }

       
        return new PedidoInput(
                linha.substring(0, 10).trim().toUpperCase(),
                linha.substring(10, 20).trim().toUpperCase(),
                linha.substring(20, 30).trim().toUpperCase(),
                Integer.parseInt(linha.substring(30, 32)),
                linha.substring(32, 40).trim().toUpperCase()
        );

    }

    public BigDecimal calcularValorTotal() {
        TipoLanche tipoLanche = TipoLanche.fromString(this.tipoLanche);
        BigDecimal total = tipoLanche.getPrecoBase().multiply(BigDecimal.
                valueOf(quantidade));

        // Regra do Desconto (Hamburguer + Carne + Salada)
        if (tipoLanche == TipoLanche.HAMBURGUER &&
                proteina.equalsIgnoreCase("CARNE") &&
                acompanhamento.equalsIgnoreCase("SALADA")) {
            return total.multiply(new BigDecimal("0.90")).
                    setScale(2, RoundingMode.HALF_UP);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

}
