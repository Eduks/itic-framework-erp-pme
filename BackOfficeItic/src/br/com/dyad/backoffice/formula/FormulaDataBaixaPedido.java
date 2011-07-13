package br.com.dyad.backoffice.formula;

import java.util.Date;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

public abstract class FormulaDataBaixaPedido extends FormulaData {

	@Override
	public abstract Date calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao);

}
