package br.com.dyad.backoffice.formula;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

public abstract class FormulaHistoricoBaixaPedido extends FormulaHistorico {

	@Override
	public abstract String calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao);
	
}
