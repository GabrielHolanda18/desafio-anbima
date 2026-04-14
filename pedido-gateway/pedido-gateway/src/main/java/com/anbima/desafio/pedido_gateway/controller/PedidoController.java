package com.anbima.desafio.pedido_gateway.controller;

import com.anbima.desafio.pedido_gateway.service.PedidoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "/pedidos")
public class PedidoController {


    @PostMapping(value = "/posicional", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<PedidoService> recebePedido(@RequestBody String linha){

        //Validações
    }
}
