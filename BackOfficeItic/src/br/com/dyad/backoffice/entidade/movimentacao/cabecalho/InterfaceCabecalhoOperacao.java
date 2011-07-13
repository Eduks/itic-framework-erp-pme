package br.com.dyad.backoffice.entidade.movimentacao.cabecalho;

import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;


public interface InterfaceCabecalhoOperacao extends Cabecalho {
	public String getClasseOperacaoId();
	public void setClasseOperacaoId(String classeOperacaoId);
}
