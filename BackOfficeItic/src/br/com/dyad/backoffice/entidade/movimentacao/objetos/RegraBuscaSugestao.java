package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoContabilBaixaPedido;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoContabilBaixaTitulo;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoContabilTransfDispo;
import br.com.dyad.backoffice.entidade.cadastro.TipoPedido;
import br.com.dyad.businessinfrastructure.entidades.tabela.ClasseContabilFiscalEntidade;
import br.com.dyad.businessinfrastructure.entidades.tabela.ClasseContabilFiscalRecurso;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class RegraBuscaSugestao {
	private AppTransaction appTransaction;
	
	public RegraBuscaSugestao(AppTransaction appTransaction) {
		this.appTransaction = appTransaction;		
	}

	public RegraLancamentoContabilBaixaPedido findRegraContabil(TipoPedido tipoPedido, ClasseContabilFiscalEntidade classeContabilFiscalEntidade, ClasseContabilFiscalRecurso classeContabilFiscalRecurso, Date dataVigencia) throws Exception {
		RegraLancamentoContabilBaixaPedido regra = null;

		DataList dlRegras = DataListFactory.newDataList(this.appTransaction);
		String hquery = "from " + RegraLancamentoContabilBaixaPedido.class.getName() + " where inicio <= ? and (fim is null or fim >= ?)";
		
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(dataVigencia);
		params.add(dataVigencia);
		
		dlRegras.setList(PersistenceUtil.executeHql((Session)this.appTransaction.getSession(), hquery, params));

		//Buscando por Tipos de Pedidos
		Collection<?> regras = dlRegras.getRangeAsCollection("TipoPedido", tipoPedido);
		
//		while (regras == null && tipoPedido != null) {
//			tipoPedido = tipoPedido.getTipoPedido();

			regras = dlRegras.getRangeAsCollection("TipoPedido", tipoPedido);
//		}
		dlRegras.setList(new ArrayList(regras));
				
		//Classe Contábil Entidade
		Collection regrasAux1 = null;
		if (dlRegras.find("ClasseContabilFiscalEntidade", classeContabilFiscalEntidade)) {
			regrasAux1 = dlRegras.getRangeAsCollection("ClasseContabilFiscalEntidade", classeContabilFiscalEntidade);
		} else if (dlRegras.find("ClasseContabilFiscalEntidade", null)) {
			regrasAux1 = dlRegras.getRangeAsCollection("ClasseContabilFiscalEntidade", null);
		}
		
		DataList dlRegrasAux2 = DataListFactory.newDataList(this.appTransaction);
		ArrayList regras2 = new ArrayList(regrasAux1); 
		dlRegrasAux2.setList(regras2);
		
		//Classe Contábil Recurso
		Collection regrasAux2 = null;
		if (dlRegrasAux2.find("ClasseContabilFiscalRecurso", classeContabilFiscalRecurso)) {
			regrasAux2 = dlRegrasAux2.getRangeAsCollection("ClasseContabilFiscalRecurso", classeContabilFiscalRecurso);
		} else if (dlRegrasAux2.find("ClasseContabilFiscalRecurso", null)) {
			regrasAux2 = dlRegrasAux2.getRangeAsCollection("ClasseContabilFiscalRecurso", null);
		}
		
		DataList dlRegrasAux3 = DataListFactory.newDataList(this.appTransaction);
		ArrayList regras3 = new ArrayList(regrasAux2);
		
		if (regras3 != null && !regras3.isEmpty()) {
			regra = (RegraLancamentoContabilBaixaPedido) regras3.get(0);
		}

		return regra;
	}
	
	public RegraLancamentoContabilTransfDispo findRegraContabilTransfDispo(ClasseContabilFiscalEntidade classeContabilFiscalEntidade, Date dataVigencia) throws Exception {
		RegraLancamentoContabilTransfDispo regra = null;

		DataList dlRegras = DataListFactory.newDataList(this.appTransaction);
		String hquery = "from " + RegraLancamentoContabilTransfDispo.class.getName() + " where inicio <= ? and (fim is null or fim >= ?)";
		
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(dataVigencia);
		params.add(dataVigencia);
		
		dlRegras.setList(PersistenceUtil.executeHql((Session)this.appTransaction.getSession(), hquery, params));

		//Classe Contábil Entidade
		Collection regrasAux1 = null;
		if (dlRegras.find("ClasseContabilFiscalEntidade", classeContabilFiscalEntidade)) {
			regrasAux1 = dlRegras.getRangeAsCollection("ClasseContabilFiscalEntidade", classeContabilFiscalEntidade);
		} else if (dlRegras.find("ClasseContabilFiscalEntidade", null)) {
			regrasAux1 = dlRegras.getRangeAsCollection("ClasseContabilFiscalEntidade", null);
		}
		
		ArrayList regras2 = new ArrayList(regrasAux1); 
		
		if (regras2 != null && !regras2.isEmpty()) {
			regra = (RegraLancamentoContabilTransfDispo) regras2.get(0);
		}

		return regra;
	}
	
	public RegraLancamentoContabilBaixaTitulo findRegraContabilBaixaTitulo(ClasseContabilFiscalEntidade classeContabilFiscalEntidade, /*ClasseContabilFiscalRecurso classeContabilFiscalRecurso,*/ Date dataVigencia) throws Exception {
		RegraLancamentoContabilBaixaTitulo regra = null;

		DataList dlRegras = DataListFactory.newDataList(this.appTransaction);
		String hquery = "from " + RegraLancamentoContabilBaixaTitulo.class.getName() + " where inicio <= ? and (fim is null or fim >= ?)";
		
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(dataVigencia);
		params.add(dataVigencia);
		
		dlRegras.setList(PersistenceUtil.executeHql((Session)this.appTransaction.getSession(), hquery, params));

		//Classe Contábil Entidade
		Collection regrasAux1 = null;
		if (dlRegras.find("ClasseContabilFiscalEntidade", classeContabilFiscalEntidade)) {
			regrasAux1 = dlRegras.getRangeAsCollection("ClasseContabilFiscalEntidade", classeContabilFiscalEntidade);
		} else if (dlRegras.find("ClasseContabilFiscalEntidade", null)) {
			regrasAux1 = dlRegras.getRangeAsCollection("ClasseContabilFiscalEntidade", null);
		}
		
		ArrayList regras2 = new ArrayList(regrasAux1); 
		
		if (regras2 != null && !regras2.isEmpty()) {
			regra = (RegraLancamentoContabilBaixaTitulo) regras2.get(0);
		}

		return regra;
	}
	
}