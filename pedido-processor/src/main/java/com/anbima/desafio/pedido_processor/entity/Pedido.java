package com.anbima.desafio.pedido_processor.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pedidos")
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_lanche", length = 20)
    private String tipoLanche;

    @Column(name = "proteina", length = 20)
    private String proteina;

    @Column(name = "acompanhamento", length = 20)
    private String acompanhamento;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "bebida", length = 20)
    private String bebida;

    @Column(name = "valor", precision = 10, scale = 2) // Valor total calculado
    private BigDecimal valor;

    @Column(name = "status", length = 20) // Recebido ou Entregue
    private String status;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    // É executado automaticamente antes de salvar no BD e salva a data de criação
    @PrePersist
    protected void prepararDataCriacao() {
        this.criadoEm = LocalDateTime.now();
    }
}
