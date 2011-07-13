package br.com.dyad.backoffice.formula;

import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;

public abstract class FormulaContaTransfDispo extends FormulaConta {

	@Override
	public abstract ContaContabil calcular(Cabecalho cabecalho, Session session, DefinicaoLancamentoContabil definicao) throws Exception;
	
}
