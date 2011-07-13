package br.com.dyad.backoffice.testes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;

public class TestesBuscaBinaria {
	
	ArrayList<ItemPedido> itemPedidos = new ArrayList<ItemPedido>();
	ItemPedido pedidoProcurado = new ItemPedido();
	
	public void testa() {
		for (Long i = 1L; i <= 10000L; i++) {
			ItemPedido p = new ItemPedido();
			p.setId(i);
			itemPedidos.add(p);
			
			if (i % 1000 == 0) {
				int x = 0;
				x++;
			}
		}
		
		this.pedidoProcurado.setId(705L);
		
		PedidoComparator comparator = new PedidoComparator();
		
		
		
		Collections.sort(this.itemPedidos, comparator);
		
		int i = Collections.binarySearch(this.itemPedidos, this.pedidoProcurado, comparator);

		System.out.println(i);
	}
}

class PedidoComparator implements Comparator<ItemPedido> {

	@Override
	public int compare(ItemPedido pedido0, ItemPedido pedido1) {
		int res = 0;
		
		if (pedido0.getId() < pedido1.getId()) {
			res = -1;			
		} else if (pedido0.getId() > pedido1.getId()){
			res = 1;		
		} 
		
		return res;
	}
}

class Cliente {
	public String nome;
	
	public Cliente(String nome) {
		this.nome = nome;
	}
}

interface Subject {
	public void addObservador(Observador o);
	public void removeObservador(Observador o);
	public void notifyObservador(Observador o);
		
}

interface Observador {
	public void update();
}

