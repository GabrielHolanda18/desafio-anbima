package com.anbima.desafio.pedido_processor.repository;

import com.anbima.desafio.pedido_processor.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido,Long> {

}
