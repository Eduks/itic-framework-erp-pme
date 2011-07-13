package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;


public interface InterfaceRegra {
	public void nova();
	public void preparaRegra();
	public void grava() throws Exception;
	public void fecha() throws Exception;

	public Cabecalho novoCabecalho() throws Exception;
	public void excluiCabecalho(Cabecalho cabecalho) throws Exception;

	public Item novoItem(Cabecalho cabecalho) throws Exception;
	public void excluiItem(Item item) throws Exception;
}
