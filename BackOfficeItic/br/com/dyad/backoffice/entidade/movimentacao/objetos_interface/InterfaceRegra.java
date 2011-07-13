package br.com.dyad.backoffice.entidade.movimentacao.objetos_interface;

import br.com.dyad.backoffice.entidade.movimentacao.ItemOperacao;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;


public interface InterfaceRegra {
	public void nova();
	public void preparaRegra();
	public void abre() throws Exception;
	public void grava() throws Exception;
	public void fecha() throws Exception;
	public void exclui(Long operacaoId) throws Exception;

	public CabecalhoOperacaoAbstrato novoCabecalho() throws Exception;
	public ItemOperacao novoItem(CabecalhoOperacaoAbstrato cabecalhoOperacao) throws Exception;
	//public void removeCabecalho(CabecalhoOperacaoAbstrato cabecalhoOperacao) throws Exception;
	//public void removeItem(ItemOperacaoAbstrato itemOperacao) throws Exception;
}
