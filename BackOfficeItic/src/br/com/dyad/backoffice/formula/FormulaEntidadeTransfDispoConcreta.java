package br.com.dyad.backoffice.formula;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;

@FormulaMetadata(codigo=-89999999999325L, nome="Fórmula Entidade Transf. de Disponíveis")
public class FormulaEntidadeTransfDispoConcreta extends FormulaEntidadeTransfDispo {
	
	@Override
	public Entidade calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao) {
		
		if (item instanceof ItemTransferenciaDisponivel) {
			return ((ItemTransferenciaDisponivel)item).getEntidade();
		}
		
		return null;
	}
	
}
