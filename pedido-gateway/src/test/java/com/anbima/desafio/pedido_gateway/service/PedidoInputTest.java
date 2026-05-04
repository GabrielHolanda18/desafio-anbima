package com.anbima.desafio.pedido_gateway.service;

import com.anbima.desafio.pedido_gateway.dto.PedidoInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PedidoInputTest {


    @Test
    @DisplayName("Deve calcular o valor total sem desconto")
    void deveCalcularPrecoTotal() {

        PedidoInput input = new PedidoInput("PASTEL", "FRANGO",
                "BACON", 2, "SUCO");

        assertThat(input.calcularValorTotal()).isEqualByComparingTo("30.00");
    }

    @Test
    @DisplayName("Deve calcular o valor com Desconto (HAMBURGUER + CARNE + SALADA - 10%)")
    void deveCalcularDesconto() {
        // Arrange

        PedidoInput lanche = new PedidoInput("HAMBURGUER", "CARNE",
                "SALADA", 1, "COCA");

        assertThat(lanche.calcularValorTotal()).isEqualByComparingTo("18.00");
    }

    @Test
    @DisplayName("Deve retornar valor 0 se a quantidade for zero ou negativa")
    void deveValidarQuantidadeInvalida() {

        PedidoInput lanche = new PedidoInput("HAMBURGUER", "CARNE",
                "SALADA", 0, "COCA");


        assertThat(lanche.calcularValorTotal()).isEqualByComparingTo("0.00");
    }
}
