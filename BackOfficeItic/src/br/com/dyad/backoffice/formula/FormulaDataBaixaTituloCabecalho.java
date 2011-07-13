package br.com.dyad.backoffice.formula;

import java.util.Date;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

@FormulaMetadata(codigo=-89999999999333L, nome="Fórmula Data Baixa de Título")
public class FormulaDataBaixaTituloCabecalho extends FormulaDataBaixaTitulo {
	
	@Override
	public Date calcular(Cabecalho cabecalho, Item item, DefinicaoLancamentoContabil definicao) {
		if (cabecalho instanceof CabecalhoBaixaTitulo) {
			return ((CabecalhoBaixaTitulo)cabecalho).getEmissao();
		} else {
			return new Date();
		}
	}
	
}
