package com.anbima.desafio.pedido_gateway.repository;

import com.anbima.desafio.pedido_gateway.service.PedidoService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<PedidoService,Integer> {

}
