package com.anbima.desafio.pedido_gateway.controller;

import com.anbima.desafio.pedido_gateway.entity.Pedido;
import com.anbima.desafio.pedido_gateway.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200") // Tirar o conflito do Frontend com o back no envio do pedido
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping(value = "/posicional", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Pedido> recebePedido(@RequestBody String linha){

        log.info("Pegou a String:");

        // Chamamos o service e guardamos o pedido que ele salvou e calculou
        Pedido pedidoSalvo = pedidoService.tratamentoEntrada(linha);

        // Retornamos o status 201 (Created) e o pedido no corpo da resposta
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoSalvo);

    }
}
