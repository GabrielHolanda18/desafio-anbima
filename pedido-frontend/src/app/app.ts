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
  telaAtual: string = 'novo'; // Controla se estamos na tela 'novo' ou 'lista'

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
    if (this.linhaPosicional.length !== 40) {
      alert(`Erro: A linha deve ter exatamente 40 caracteres. Atualmente tem ${this.linhaPosicional.length}.`);
      return;
    }

    this.pedidoService.enviarPedido(this.linhaPosicional).subscribe({
      next: (resposta) => {
        alert('Pedido enviado com sucesso para o Módulo A!');
        this.linhaPosicional = ''; // Limpa o textarea
        this.mudarTela('lista'); // Pula automaticamente para a tela da tabela
      },
      error: (err) => alert('Erro ao enviar pedido: ' + err.message)
    });
  }

  atualizarTabela() {
    this.pedidoService.listarTodos().subscribe({
      next: (dados) => {
        this.pedidos = dados;
        this.filtrarPedidos(); // Aplica o filtro assim que os dados chegam
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
