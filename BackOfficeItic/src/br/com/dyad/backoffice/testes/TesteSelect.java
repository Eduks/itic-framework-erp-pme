package br.com.dyad.backoffice.testes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class TesteSelect {
	
	public static void main(String[] args) {
		ArrayList<ArrayList> cartelas = new ArrayList<ArrayList>();
		
		for (int i = 1; i <= 1000; i++) {
			cartelas.add(geraCartela());
		}
		
		ArrayList numerosSorteados = new ArrayList();
		ArrayList cartelasVencedoras = new ArrayList();
		
		while (cartelasVencedoras.isEmpty()) {
			numerosSorteados.add(numeroAleatorio(numerosSorteados));
			
			if (numerosSorteados.size() >= 15) {
				
				for (ArrayList cartela : cartelas) {
					int numerosCartelados = 0;
					Iterator iteratorCartela = cartela.iterator();
					
					while (iteratorCartela.hasNext()) {
						Integer numero = (Integer) iteratorCartela.next();
						
						if (numerosSorteados.contains(numero)) {
							numerosCartelados++;
						}
					}
					
					if (numerosCartelados == 15) {
						cartelasVencedoras.add(cartela);
					}
				}
			}
		}
		
		System.out.println(cartelasVencedoras);
	}
	
	
	public static ArrayList<Integer> geraCartela() {
		ArrayList<Integer> cartela = new ArrayList<Integer>();
		
		for (int i = 1; i <= 15; i++) {
			cartela.add(numeroAleatorio(cartela));			
		}
		
		Collections.sort(cartela);
		
		return cartela;		
	}
	
	public static int numeroAleatorio(ArrayList exclusao) {
		boolean ok = false;
		int numero = 0;
		
		while (!ok) {
			numero = (int) ((Math.random() * 1000) % 91);
			
			if (!exclusao.contains(numero)) {
				ok = true;
			}
		}
		 
		return numero;
	}
	
	
	
}
