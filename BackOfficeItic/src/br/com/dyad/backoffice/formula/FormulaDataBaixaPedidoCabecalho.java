package br.com.dyad.backoffice.formula;

import java.util.Date;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

@FormulaMetadata(codigo=-89999999999334L, nome="Fórmula Data Baixa de Pedido")
public class FormulaDataBaixaPedidoCabecalho extends FormulaDataBaixaPedido {
	
	@Override
	public Date calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao) {
		if (cabecalho instanceof CabecalhoBaixaPedido) {
			return ((CabecalhoBaixaPedido)cabecalho).getEmissao();
		} else {
			return new Date();
		}
	}
	
}
