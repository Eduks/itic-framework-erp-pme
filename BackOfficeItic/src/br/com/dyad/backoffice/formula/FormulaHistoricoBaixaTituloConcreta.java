package br.com.dyad.backoffice.formula;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

@FormulaMetadata(codigo=-89999999999323L, nome="Fórmula Histórico Baixa de Título")
public class FormulaHistoricoBaixaTituloConcreta extends FormulaHistoricoBaixaTitulo {
	
	@Override
	public String calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao) {
		//TODO Implementar
		return null;
	}
	
}
