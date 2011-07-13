package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.auxiliares.DiarioReportBean;
import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoLancamento;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class RegraDiario {
	
	private String dataBase;
	private PlanoConta planoConta;
	private ContaContabil contaRestricao;
	private ContaContabil contaInicial;
	private ContaContabil contaFinal;
	private Date dataInicial;
	private Date dataFinal;
	private DataList dataListDiario;
	
	private ArrayList<Object> parametrosConsulta;
	
	public RegraDiario(String dataBase){
		this.dataBase = dataBase;
		dataListDiario = DataListFactory.newDataList(dataBase);
		dataListDiario.setLogChanges(false);
		dataListDiario.setCommitOnSave(false);
	}
	
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
	public DataList getDataListDiario() {
		return dataListDiario;
	}
	public void setDataListDiario(DataList dataListDiario) {
		this.dataListDiario = dataListDiario;
	}
	
	public void carregaDiario() throws Exception{
		
		dataListDiario.empty();
		
		String hquery = "from " + CabecalhoLancamento.class.getName() + " cabecalho ";

		ArrayList<String> where = this.filtroDiario();

		if ( !where.isEmpty()){
			hquery += " where " + StringUtils.join(where, " and ");			
		}
		hquery += " order by emissao";
		
		dataListDiario = DataListFactory.newDataList(getDataBase());
		
		dataListDiario.setList(PersistenceUtil.executeHql((Session)dataListDiario.getTransactionalSession().getSession(), hquery, parametrosConsulta));
	}
	
	private ArrayList<String> filtroDiario(){
		ArrayList<String> where = new ArrayList<String>();
		
		parametrosConsulta = new ArrayList<Object>();
		
		String filtro = "";
		
		if (getPlanoConta() != null || getContaRestricao() != null || getContaInicial() != null || getContaFinal() != null) {
			filtro += " cabecalho.id IN (SELECT item.cabecalho.id FROM "+ItemLancamento.class.getName()+" item WHERE 1=1";
			
			if (getPlanoConta() != null) {
				filtro += " AND item.contaContabil.planoConta.id = ?";
				parametrosConsulta.add(getPlanoConta().getId());
			}
			
			if (getContaRestricao() != null) {
				filtro += " AND item.contaContabil.codigo LIKE ?";
				parametrosConsulta.add(getContaRestricao().getCodigo()+"%");
			}
			
			if (getContaInicial() != null) {
				filtro += " AND item.contaContabil.codigo >= ?";
				parametrosConsulta.add(getContaInicial().getCodigo());
			}
			
			if (getContaFinal() != null) {
				filtro += " AND item.contaContabil.codigo <= ?";
				parametrosConsulta.add(getContaFinal().getCodigo());
			}
			
			filtro += ")";
			
			where.add(filtro);
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
	
	public DataList retornaDiario() throws Exception {
		this.carregaDiario();
		
		List<CabecalhoLancamento> cabecalhos = dataListDiario.getList();
		
		List<DiarioReportBean> diario = new ArrayList<DiarioReportBean>(0);
		
		Date dateAux = new Date();
		
		DiarioReportBean diarioReportBeanAux;
		
		Long idAux = 0L;
		
		int qtdeLancamentos = 0;
		
		BigDecimal totalDebito = new BigDecimal(0);
		BigDecimal totalCredito = new BigDecimal(0);
		
		for (CabecalhoLancamento cabecalho : cabecalhos) {
			
			if (dateAux.compareTo(cabecalho.getEmissao()) != 0) {
				
				if (qtdeLancamentos > 0) {
					diarioReportBeanAux = new DiarioReportBean();
					diarioReportBeanAux.setData("TOTAL");
					diarioReportBeanAux.setIdCabecalho(""+qtdeLancamentos);
					diarioReportBeanAux.setDebito(totalDebito);
					diarioReportBeanAux.setCredito(totalCredito);
					diario.add(diarioReportBeanAux);
					
					diarioReportBeanAux = new DiarioReportBean();
					diario.add(diarioReportBeanAux);
					
					qtdeLancamentos = 0;
					totalCredito = new BigDecimal(0);
					totalDebito = new BigDecimal(0);
				}
				
				diarioReportBeanAux = new DiarioReportBean();
				diarioReportBeanAux.setData(new SimpleDateFormat("dd/MM/yyyy").format(cabecalho.getEmissao()));
				diario.add(diarioReportBeanAux);
			} else if (idAux != cabecalho.getId()) {
				diarioReportBeanAux = new DiarioReportBean();
				diario.add(diarioReportBeanAux);
			}
			
			for (ItemLancamento item : cabecalho.getItensLancamento()) {
				DiarioReportBean bean = new DiarioReportBean();
				
				bean.setIdCabecalho(""+cabecalho.getId());
				bean.setIdItem(""+item.getId());
				bean.setContaContabil(item.getContaContabil());
				bean.setEntidade(item.getEntidade());
				
				if (item.getValor().intValue() > 0) {
					totalCredito = totalCredito.add(item.getValor());
					bean.setCredito(item.getValor());
				} else {
					totalDebito = totalDebito.add(item.getValor().multiply(new BigDecimal(-1)));
					bean.setDebito(item.getValor().multiply(new BigDecimal(-1)));
				}
				
				diario.add(bean);
			}
			
			qtdeLancamentos += cabecalho.getItensLancamento().size(); 
			
			dateAux = cabecalho.getEmissao();
			idAux = cabecalho.getId();
		}
		
		if (qtdeLancamentos > 0) {
			diarioReportBeanAux = new DiarioReportBean();
			diarioReportBeanAux.setData("TOTAL");
			diarioReportBeanAux.setIdCabecalho(""+qtdeLancamentos);
			diarioReportBeanAux.setDebito(totalDebito);
			diarioReportBeanAux.setCredito(totalCredito);
			diario.add(diarioReportBeanAux);
			
			diarioReportBeanAux = new DiarioReportBean();
			diario.add(diarioReportBeanAux);
			
			qtdeLancamentos = 0;
			totalCredito = new BigDecimal(0);
			totalDebito = new BigDecimal(0);
		}
		
		DataList dataListAux = DataListFactory.newDataList(dataBase);
		dataListAux.setList(diario);
		
		return dataListAux;
	}

	public ContaContabil getContaRestricao() {
		return contaRestricao;
	}

	public void setContaRestricao(ContaContabil contaRestricao) {
		this.contaRestricao = contaRestricao;
	}

	public ContaContabil getContaInicial() {
		return contaInicial;
	}

	public void setContaInicial(ContaContabil contaInicial) {
		this.contaInicial = contaInicial;
	}

	public ContaContabil getContaFinal() {
		return contaFinal;
	}

	public void setContaFinal(ContaContabil contaFinal) {
		this.contaFinal = contaFinal;
	}
	
}
