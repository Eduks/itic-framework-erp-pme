package br.com.dyad.backoffice.formula;

import java.math.BigDecimal;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

public abstract class FormulaValor implements Formula<BigDecimal,Cabecalho,Item,DefinicaoLancamentoContabil> {

	@Override
	public abstract BigDecimal calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao);
	
}
