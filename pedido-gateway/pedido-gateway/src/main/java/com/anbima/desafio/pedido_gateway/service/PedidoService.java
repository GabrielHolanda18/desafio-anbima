package com.anbima.desafio.pedido_gateway.service;

import com.anbima.desafio.pedido_gateway.entity.Pedido;
import com.anbima.desafio.pedido_gateway.repository.PedidoRepository;
import com.rabbitmq.client.Return;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private PedidoRepository repository;

    public Pedido tratamentoEntrada(String linha) {

        if (linha == null || linha.length() != 40) {
            throw new RuntimeException("A entrada deve ter exatamente 40 caracteres.");
        }

        // Repartindo a ‘String’ recebida para realizar a montagem do Pedido

        String tipoLache = linha.substring(0,10).trim(); // 1 a 10
        String proteina = linha.substring(10,20).trim(); // 11 a 20
        String acompanhamento =  linha.substring(20,30).trim(); // 21 a 30

        // Verificar se a quantidade veio em forma de numérica

        String quantidadeStr = linha.substring(30,32).trim(); // 31 a 32
        if (!quantidadeStr.matches("\\d{2}")){
            throw new RuntimeException("Entrada deve ser numérica (01 até 99)");
        }
        // Transformar quantidade em Inteiro
        Integer quantidade = Integer.parseInt(quantidadeStr);

        String bebida =  linha.substring(32,40).trim(); // 33 a 40

        // Transformar a 'String' em um Objeto do tipo Pedido

        Pedido pedido = Pedido.builder().
                tipoLanche(tipoLache).proteina(proteina).
                acompanhamento(acompanhamento).
                quantidade(quantidade).
                bebida(bebida).status("RECEBIDO").
                build();

        
        return repository.save(pedido);
    }

    private boolean calcularPreco(Pedido pedido){

        return true;
    }
}
