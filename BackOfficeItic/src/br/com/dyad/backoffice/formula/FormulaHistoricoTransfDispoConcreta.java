package br.com.dyad.backoffice.formula;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

@FormulaMetadata(codigo=-89999999999322L, nome="Fórmula Histórico Transf. de Disponíveis")
public class FormulaHistoricoTransfDispoConcreta extends FormulaHistoricoTransfDispo {
	
	@Override
	public String calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao) {
		
		String prefixo = "";
		if (definicao.getPrefixoHistorico() != null) {
			prefixo = definicao.getPrefixoHistorico();
		}
		
		String sufixo = "";
		if (definicao.getSufixoHistorico() != null) {
			sufixo = definicao.getSufixoHistorico();
		}
		
		return prefixo + " " + sufixo;
	}
	
}
