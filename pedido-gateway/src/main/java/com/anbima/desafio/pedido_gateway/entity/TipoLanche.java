package com.anbima.desafio.pedido_gateway.entity;

import java.math.BigDecimal;

public enum TipoLanche {
    HAMBURGUER(20.0),
    PASTEL(15.0),
    OUTROS(12.0);

    private final double precoBase;

    TipoLanche(double precoBase) {
        this.precoBase = precoBase;
    }

    public static TipoLanche fromString(String texto) {
        return switch (texto.trim().toUpperCase()) {
            case "HAMBURGUER" -> HAMBURGUER;
            case "PASTEL" -> PASTEL;
            default -> OUTROS;
        };
    }

    public BigDecimal getPrecoBase() {
        return BigDecimal.valueOf(precoBase);
    }
}
