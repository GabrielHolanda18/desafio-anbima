import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PedidoService } from './pedido.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html'
})
export class App implements OnInit {
  telaAtual: string = 'novo'; // Verifica qual tela esta na novo pedido ou lista dos pedidos

  // Campo do Textarea
  linhaPosicional: string = '';

  // Dados da Tabela e Filtro
  pedidos: any[] = [];
  pedidosFiltrados: any[] = [];
  filtroStatus: string = 'TODOS';

  constructor(private pedidoService: PedidoService) {}

  ngOnInit() {
    this.atualizarTabela();
  }

  mudarTela(tela: string) {
    this.telaAtual = tela;
    if (tela === 'lista') {
      this.atualizarTabela();
    }
  }

  enviar() {
    let input = this.linhaPosicional;

    // 1. Se já tem 40, envia direto.
    /*
    if (input.length === 40) {
      this.processarEnvio(input);
      return;
    }
    */

    if (input.length > 40) {
      alert("Erro: String maior que 40 caracteres.");
      return;
    }

    // Tratar entradas que sejam menores que 40 e adiciona o espaço a direita e o 0 a esquerda onde for numero

    // Remove espaços extras e pega as palavras ['HAMBURGUER', 'CARNE', 'SALADA', '02', 'COCA']
    const partes = input.trim().split(/\s+/);

    //console.log(partes);

    if (partes.length >= 4) {

      // Preenche o comprimento da string até ficar do tamanho 10
      const lanche   = partes[0].substring(0, 10).padEnd(10, ' ');
      const proteina = partes[1].substring(0, 10).padEnd(10, ' ');
      const acompanhamento  = partes[2].substring(0, 10).padEnd(10, ' ');

      /*
      exemplo entrada: HAMBURGUER·CARNE·····SALADA·····01COCA····
      junta o final["02COCA"] ou ["2", "COCA"], nós juntamos tudo.
      */
      let resto = partes.slice(3).join("");

      //console.log("Resto: " + resto);
      // QTD: Pega os 2 primeiros caracteres do que sobrou que no caso deve ser numero
      let qtdOriginal = resto.replace(/\D/g, '').substring(0, 2);

      //console.log("Como esta: " + qtdOriginal);
      const qtd = qtdOriginal.padStart(2, '0');

      //console.log("Como ficou: " + qtd);

      //Pega a bebida escolhida
      let bebidaOriginal = resto.replace(qtdOriginal, '');

      //console.log("Bebida enviada: " + resto.replace(qtdOriginal, 'Trocou'))
      const bebida = bebidaOriginal.substring(0, 8).padEnd(8, ' ');

      //console.log("Bebida tratada: " + bebida);
      const stringFinal = lanche + proteina + acompanhamento + qtd + bebida;

      this.processarEnvio(stringFinal);
    } else {
      alert("Erro: Digite os campos separados por espaço (Ex: HAMBURGUER CARNE SALADA 02COCA)");
    }
  }


  processarEnvio(stringFinal: string) {
    /*
    console.log("Verifica a String no console");
    console.log("Conteúdo final: '" + stringFinal + "'");
    console.log("Total de caracteres: " + stringFinal.length);
     */

    this.pedidoService.enviarPedido(stringFinal).subscribe({
      next: () => {
        alert('Pedido enviado e ajustado com sucesso!');
        this.linhaPosicional = '';
        //this.mudarTela('lista');
      },
      error: (err) => alert('Erro: ' + err.message)
    });
  }

  atualizarTabela() {
    this.pedidoService.listarTodos().subscribe({
      next: (dados) => {
        this.pedidos = dados;
        this.filtrarPedidos(); // Aplica o filtro assim é enviado
      },
      error: (err) => console.error('Erro ao buscar pedidos', err)
    });
  }

  filtrarPedidos() {
    if (this.filtroStatus === 'TODOS') {
      this.pedidosFiltrados = [...this.pedidos];
    } else {
      this.pedidosFiltrados = this.pedidos.filter(p => p.status === this.filtroStatus);
    }
  }
}
