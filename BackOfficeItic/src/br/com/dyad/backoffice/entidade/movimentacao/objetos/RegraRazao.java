package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.auxiliares.RazaoReportBean;
import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class RegraRazao {
	
	private String dataBase;
	private PlanoConta planoConta;
	private ContaContabil contaRestricao;
	private Date dataInicial;
	private Date dataFinal;
	private DataList razao;
	
	ArrayList<Object> parametrosConsulta;
	
	public String getDataBase() {
		return dataBase;
	}
	public void setDataBase(String dataBase) {
		this.dataBase = dataBase;
	}
	public PlanoConta getPlanoConta() {
		return planoConta;
	}
	public void setPlanoConta(PlanoConta planoConta) {
		this.planoConta = planoConta;
	}
	public ContaContabil getContaRestricao() {
		return contaRestricao;
	}
	public void setContaRestricao(ContaContabil contaRestricao) {
		this.contaRestricao = contaRestricao;
	}
	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public RegraRazao(String dataBase){
		this.setDataBase(dataBase);
		this.razao = DataListFactory.newDataList(dataBase);
		this.razao.setLogChanges(false);
		this.razao.setCommitOnSave(false);		
	}

	private void carregaRazao() throws Exception{
		
		this.razao.empty();

		String hquery = "from " + ItemLancamento.class.getName();

		ArrayList<String> where = this.filtroRazao();

		if ( !where.isEmpty()){
			hquery += " where " + StringUtils.join(where, " and ");			
		}
		hquery += " order by contaContabil, emissao ";
		
		BigDecimal saldo = new BigDecimal(0).setScale(2);
		ContaContabil contaContabilAux = null;
		
		List itens = PersistenceUtil.executeHql((Session)razao.getTransactionalSession().getSession(), hquery, parametrosConsulta);

		for (Iterator iterator = itens.iterator(); iterator.hasNext(); ) {
			ItemLancamento item = (ItemLancamento) iterator.next();

			if ( contaContabilAux == null || !contaContabilAux.getId().equals(item.getContaContabil().getId()) ){
				saldo = new BigDecimal(0).setScale(2);
			} 
			contaContabilAux = item.getContaContabil();			
			
			RazaoReportBean razaoReportBean = new RazaoReportBean();
			razaoReportBean.setContaContabil(item.getContaContabil());
			razaoReportBean.setId(item.getId());
			//razaoReportBean.setOperacaoId(item.getOperacaoId());
			razaoReportBean.setValor(item.getValor());
			razaoReportBean.setEmissao(item.getEmissao());
			razaoReportBean.setEntidade(item.getEntidade());
			
			if ( razaoReportBean.getValor().signum() < 0 ){
				razaoReportBean.setDebito(new BigDecimal(0).setScale(2));
				razaoReportBean.setCredito(razaoReportBean.getValor().abs());					
			} else {
				razaoReportBean.setDebito(razaoReportBean.getValor().abs());
				razaoReportBean.setCredito(new BigDecimal(0).setScale(2));					
			}
			saldo = saldo.add(razaoReportBean.getDebito().add(razaoReportBean.getCredito().negate()));
			razaoReportBean.setSaldoFinal(saldo);

			this.razao.add(razaoReportBean);

		}		
		
	}
	
	private ArrayList<String> filtroRazao(){
		ArrayList<String> where = new ArrayList<String>();
		
		parametrosConsulta = new ArrayList<Object>();
		
		if (this.getPlanoConta() != null){
			where.add(" contaContabil.planoConta.id = ?");
			parametrosConsulta.add(getPlanoConta().getId());
		}		
		if (this.getContaRestricao() != null){
			where.add(" contaContabil.codigo like ?");
			parametrosConsulta.add(getContaRestricao().getCodigo()+"%");
		}		
		if (this.getDataInicial() != null){
			where.add(" emissao >= ?");
			parametrosConsulta.add(getDataInicial());
		}
		if (this.getDataFinal() != null){
			where.add(" emissao <= ?");
			parametrosConsulta.add(getDataFinal());
		}
		
		return where;
	}

	public DataList retornaRazao() throws Exception{		
		
		this.carregaRazao();
		
		DataList retornoRazao = DataListFactory.newDataList(this.getDataBase());
		List itens = this.razao.getList();		
		
		for (Iterator iterator = itens.iterator(); iterator.hasNext(); ) {
			RazaoReportBean item = (RazaoReportBean) iterator.next();
			retornoRazao.add(item);		
		}
		
		retornoRazao.sort("contaContabil", "emissao");
		
		return retornoRazao;
		
	}

}
