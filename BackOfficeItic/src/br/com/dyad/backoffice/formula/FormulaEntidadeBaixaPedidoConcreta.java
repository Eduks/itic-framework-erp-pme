package br.com.dyad.backoffice.formula;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;

@FormulaMetadata(codigo=-89999999999327L, nome="Fórmula Entidade Baixa de Pedido")
public class FormulaEntidadeBaixaPedidoConcreta extends FormulaEntidadeBaixaPedido {
	
	@Override
	public Entidade calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao) {
		//TODO Implementar
		return null;
	}
	
}
