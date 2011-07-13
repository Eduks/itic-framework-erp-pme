package br.com.dyad.backoffice.entidade.movimentacao.objetos_interface;

import br.com.dyad.backoffice.entidade.movimentacao.ItemOperacao;
import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;

public abstract class RegraAbstrata implements InterfaceRegra {
	
	protected String regraId;
	protected boolean preparado;
	private AppTransaction appTransaction;

	protected DataList dataListCabecalhos;
	protected DataList dataListItens;
	
	public RegraAbstrata(AppTransaction appTransaction) {
		this.appTransaction = appTransaction;
		
		this.dataListCabecalhos = DataListFactory.newDataList(this.appTransaction);
		this.dataListItens = DataListFactory.newDataList(this.appTransaction);
	}
	
	public AppTransaction getAppTransaction() {
		return this.appTransaction;
	}
	
	public boolean isPreparado() {
		return this.preparado;
	}

	public DataList getCabecalhosOperacao() {
		return this.dataListCabecalhos;
	}

	public DataList getItens() {
		return this.dataListItens;
	}

	public String getRegraId() {
		return this.regraId;
	}

	public abstract void preparaGravacao() throws Exception;

	public abstract ItemOperacao novoItem(CabecalhoOperacaoAbstrato cabecalhoOperacao) throws Exception;
}