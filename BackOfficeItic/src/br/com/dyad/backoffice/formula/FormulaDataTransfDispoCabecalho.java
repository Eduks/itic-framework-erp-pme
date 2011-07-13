package br.com.dyad.backoffice.formula;

import java.util.Date;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

@FormulaMetadata(codigo=-89999999999332L, nome="Fórmula Data Transf. de Disponíveis")
public class FormulaDataTransfDispoCabecalho extends FormulaDataTransfDispo {
	
	@Override
	public Date calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao) {
		if (cabecalho instanceof CabecalhoTransferenciaDisponivel) {
			return ((CabecalhoTransferenciaDisponivel)cabecalho).getEmissao();
		} else {
			return new Date();
		}
	}
	
}
