package br.com.dyad.backoffice.formula;


public interface Formula<R,C,I,D> {
	
	public R calcular (C cabecalho, I item, D definicao) throws Exception;
	
}
