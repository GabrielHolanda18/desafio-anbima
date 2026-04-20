package com.anbima.desafio.pedido_gateway.repository;

import com.anbima.desafio.pedido_gateway.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido,Long> {

}
