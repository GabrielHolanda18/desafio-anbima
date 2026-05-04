package com.anbima.desafio.pedido_gateway.service;

import com.anbima.desafio.pedido_gateway.dto.PedidoInput;
import com.anbima.desafio.pedido_gateway.entity.Pedido;
import com.anbima.desafio.pedido_gateway.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public Pedido tratamentoEntrada(String linha) {

        // entrada vira um Record (Portador dos dados)
        PedidoInput input = PedidoInput.tratamento(linha);

        //Converte Record para Entity (Lombok)
        Pedido pedido = Pedido.builder()
                .tipoLanche(input.tipoLanche())
                .proteina(input.proteina())
                .acompanhamento(input.acompanhamento())
                .quantidade(input.quantidade())
                .bebida(input.bebida())
                .valor(input.calcularValorTotal()) // O Record faz a conta!
                .status("RECEBIDO")
                .build();


        Pedido pedidoSalvo = repository.save(pedido);

        // config tem um metodo que transforma qualquer Objeto no caso o nosso Record em Json
        record FilaEvent(Long pedidoId) {}
        rabbitTemplate.convertAndSend("pedidos.recebidos", new FilaEvent(pedidoSalvo.getId()));

        return pedidoSalvo;
    }
}
