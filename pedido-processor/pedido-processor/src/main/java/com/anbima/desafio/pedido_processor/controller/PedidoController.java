package com.anbima.desafio.pedido_processor.controller;

import com.anbima.desafio.pedido_processor.entity.Pedido;
import com.anbima.desafio.pedido_processor.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Pedido>> buscarTodos(){
        List<Pedido>  pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id){
        return pedidoService.buscarPorId(id).
                map(ResponseEntity::ok). // Se tiver retorna 200
                orElse(ResponseEntity.notFound().build()); // Senão retorna 404
    }
}
