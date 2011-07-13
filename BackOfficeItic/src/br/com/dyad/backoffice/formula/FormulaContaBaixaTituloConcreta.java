package br.com.dyad.backoffice.formula;

import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;

@FormulaMetadata(codigo=-89999999999329L, nome="Fórmula Conta Baixa de Título")
public class FormulaContaBaixaTituloConcreta extends FormulaContaBaixaTitulo {
	
	@Override
	public ContaContabil calcular(Cabecalho cabecalho, Session session, DefinicaoLancamentoContabil definicao) {
		//TODO Implementar
		return null;
	}
	
}
