package br.com.dyad.backoffice.formula;

import java.math.BigDecimal;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

@FormulaMetadata(codigo=-89999999999319L, nome="Fórmula Conta Transf. de Disponíveis")
public class FormulaValorTransfDispoConcreta extends FormulaValorTransfDispo {
	
	@Override
	public BigDecimal calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao) {
		
		BigDecimal fatorMultiplicacao = new BigDecimal(1);
		
		if (definicao.getFatorMultiplicacaoValor() != null) {
			fatorMultiplicacao = definicao.getFatorMultiplicacaoValor();
		}
		
		if (item instanceof ItemTransferenciaDisponivel) {
			return ((ItemTransferenciaDisponivel)item).getTotal().multiply(fatorMultiplicacao);
		}
		
		return new BigDecimal(0);
	}
	
}
