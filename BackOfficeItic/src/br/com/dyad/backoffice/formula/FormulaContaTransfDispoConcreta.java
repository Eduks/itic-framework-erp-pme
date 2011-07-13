package br.com.dyad.backoffice.formula;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabil;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;

@FormulaMetadata(codigo=-89999999999328L, nome="Fórmula Conta Transf. de Disponível")
public class FormulaContaTransfDispoConcreta extends FormulaContaTransfDispo {
	
	@Override
	public ContaContabil calcular(Cabecalho cabecalho, Session session, DefinicaoLancamentoContabil definicao) throws Exception {
		
		String prefixo = definicao.getPrefixoConta();
		
		String sufixo = definicao.getSufixoConta();
		
		String codigoConta = prefixo;
		
		if (sufixo != null && !sufixo.equals("")) {
			codigoConta += "."+sufixo;
		}
		
		Query query = session.createQuery("FROM ContaContabil c WHERE c.codigo = '"+codigoConta+"'");
		
		List lista = query.list();
		
		ContaContabil contaContabil = null;
		
		if (!lista.isEmpty()) {
			contaContabil = (ContaContabil)query.list().get(0); 
		}
		
		return contaContabil;
	}
	
}
