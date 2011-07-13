package br.com.dyad.backoffice.formula;

import java.math.BigDecimal;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

@FormulaMetadata(codigo=-89999999999321L, nome="Fórmula Valor Baixa de Pedido")
public class FormulaValorBaixaPedidoConcreta extends FormulaValorBaixaPedido {
	
	@Override
	public BigDecimal calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao) {
		//TODO Implementar
		return null;
	}
	
}
