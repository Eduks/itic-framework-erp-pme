package br.com.dyad.backoffice.formula;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;

public abstract class FormulaEntidadeBaixaTitulo extends FormulaEntidade {

	@Override
	public abstract Entidade calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao);
	
}
