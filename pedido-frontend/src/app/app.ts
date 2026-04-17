import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PedidoService } from './pedido.service';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule,RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit{
  protected readonly title = signal('pedido-frontend');

  // Variaveis do Pedido

  tipoLanche = '';
  proteina = '';
  acompanhamento = '';
  quantidade = 1;
  bebida = '';

  pedidos: any[] = [];

  // Injetando o Service
  constructor(private pedidoService: PedidoService) {}

  ngOnInit() {
    this.atualizarTabela();
  }

  enviar() {
    // Gera a string de 40 caracteres
    const linha = this.pedidoService.formatarPosicional(
      this.lanche, this.proteina, this.acompanhamento, this.quantidade, this.bebida
    );

    this.pedidoService.enviarPedido(linha).subscribe({
      next: () => {
        alert('Pedido enviado com sucesso!');
        this.atualizarTabela(); // Atualiza a lista após enviar
      },
      error: (err) => alert('Erro ao enviar: ' + err.message)
    });
  }

  atualizarTabela() {
    this.pedidoService.listarTodos().subscribe(data => {
      this.pedidos = data;
    });
  }
}
