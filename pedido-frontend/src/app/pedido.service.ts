import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private urlModuloA = 'http://localhost:8080/pedidos/posicional';
  private urlModuloB = 'http://localhost:8081/pedidos';

  constructor(private http: HttpClient) { }

  // Recebe a string direto do textarea e manda pro Gateway
  enviarPedido(linhaPosicional: string): Observable<any> {
    return this.http.post(this.urlModuloA, linhaPosicional, { responseType: 'text' });
  }

  // Busca do Processor (que tem o Banco de Dados com Valor, Lanche, etc)
  listarTodos(): Observable<any[]> {
    return this.http.get<any[]>(this.urlModuloB);
  }
}
