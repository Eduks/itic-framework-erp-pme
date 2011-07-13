package br.com.dyad.backoffice.formula;

import java.math.BigDecimal;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

public abstract class FormulaValorTransfDispo extends FormulaValor {

	@Override
	public abstract BigDecimal calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao);
	
}
