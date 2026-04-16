package com.anbima.desafio.pedido_processor.service;

import com.anbima.desafio.pedido_processor.entity.Pedido;
import com.anbima.desafio.pedido_processor.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    @Transactional
    public void processarPedido(Long pedidoId) {

        //log.info(String.valueOf(pedidoId));
        // verificar se existe o pedido com o ID no BD e alterar o status
        pedidoRepository.findById(pedidoId).ifPresentOrElse(pedido -> {
            log.info("Processando entrega do pedido ID: {}", pedidoId);

            pedido.setStatus("ENTREGUE");
            pedidoRepository.save(pedido);

            log.info("Pedido {} atualizado para ENTREGUE.", pedidoId);}
                ,() -> log.error("Pedido ID {} não encontrado no banco de dados!", pedidoId));
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }
}

/*
    if (pedido!= null) {
        log.info("");
        pedido.setStatus("ENTREGUE");
        repository.save(pedido);
    } else {
        log.error("Pedido não encontrado");
    }
 */
