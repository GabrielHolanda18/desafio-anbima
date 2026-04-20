package com.anbima.desafio.pedido_gateway.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue filaPedidos() {
        return new Queue("pedidos.recebidos", true);
    }
}
