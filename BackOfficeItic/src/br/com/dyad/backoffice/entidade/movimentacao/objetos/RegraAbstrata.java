package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;

public abstract class RegraAbstrata implements InterfaceRegra {
	
	protected String regraId;
	protected boolean preparado;
	private AppTransaction appTransaction;

	private DataList dataListCabecalhos;
	/**
	 * Constructor
	 * @param appTransaction
	 */
	public RegraAbstrata(AppTransaction appTransaction, String regraId) {
		this.appTransaction = appTransaction;
		this.regraId = regraId;
		
		this.dataListCabecalhos = DataListFactory.newDataList(this.appTransaction);
	}
	
	public AppTransaction getAppTransaction() {
		return this.appTransaction;
	}
	
	public boolean isPreparado() {
		return preparado;
	}

	public DataList getDataListCabecalhos() {
		return dataListCabecalhos;
	}

	public String getRegraId() {
		return this.regraId;
	}

	public abstract void grava() throws Exception;

	protected abstract void preparaGravacao() throws Exception;

	public abstract void calculaOperacoes() throws Exception;
	
	public abstract void calculaOperacao(Cabecalho cabecalho) throws Exception;
	
	protected abstract void sincronizaItensComCabecalho(Cabecalho cabecalho) throws Exception;

	protected abstract void verificaIntegridadeCabecalhos() throws Exception;

	protected abstract void verificaIntegridadeCabecalho(Cabecalho cabecalho) throws Exception;

	protected abstract void verificaIntegridadeItensCabecalho(Cabecalho cabecalho) throws Exception;

	protected abstract void verificaIntegridadeItens() throws Exception;
}