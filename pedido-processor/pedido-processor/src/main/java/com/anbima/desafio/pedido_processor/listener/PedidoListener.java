package com.anbima.desafio.pedido_processor.listener;

import com.anbima.desafio.pedido_processor.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoListener {

    private final PedidoService pedidoService;
    private final ObjectMapper objectMapper; // para ler o Json

    @RabbitListener(queues = "pedidos.recebidos")
    public void processarMensagem(String mensagem) {

        log.info(mensagem);
        try {
            JsonNode jsonNode = objectMapper.readTree(mensagem);
            Long id = jsonNode.get("pedidoId").asLong();

            // Adicionei um tempo de 10 s para conseguir ver o Status de Recebido no Frontend
            Thread.sleep(10000);

            pedidoService.processarPedido(id);
        }  catch (Exception e) {
            log.error("Erro ao fazer o parsing do JSON: {}", e.getMessage());
        }
    }
}
