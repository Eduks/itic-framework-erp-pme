package br.com.dyad.backoffice.operacao.consulta;

import br.com.dyad.commons.data.AppTransaction;


public abstract class ConsultaOperacao {
	private AppTransaction appTransaction;

	public AppTransaction getAppTransaction() {
		return this.appTransaction;
	}
	
	public void setAppTransaction(AppTransaction appTransaction) {
		this.appTransaction = appTransaction;
	}

	public ConsultaOperacao(AppTransaction appTransaction) {
		this.appTransaction = appTransaction;
	}
	
	public Boolean todosOsParametrosEstaoVazios(){
		Boolean parametrosVazios = false;
		//TODO esta faltando implementar!!!
		return parametrosVazios;
	};
	
	public void limpaParametros(){		
		//TODO esta faltando implementar!!!
	};
}
