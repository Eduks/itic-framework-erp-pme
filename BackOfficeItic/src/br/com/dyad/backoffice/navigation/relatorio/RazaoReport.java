package br.com.dyad.backoffice.navigation.relatorio;

import java.util.Date;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.auxiliares.RazaoReportBean;
import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraRazao;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.ReportWindow;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.grid.Layout;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class RazaoReport extends ReportWindow {

	public DataList dataListRazao;
	private RegraRazao regraRazao;	

	private PlanoConta planoConta;
	private ContaContabil conta;
	private Date dataInicial;
	private Date dataFinal;

	public PlanoConta getPlanoConta() {
		return planoConta;
	}
	public void setPlanoConta(PlanoConta planoConta) {
		this.planoConta = planoConta;
	}	
	public ContaContabil getConta() {
		return conta;
	}
	public void setConta(ContaContabil conta) {
		this.conta = conta;
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

	public RazaoReport(HttpSession httpSession) {
		super(httpSession);

		this.setTitle("Razão");

		this.regraRazao = new RegraRazao(this.getDatabase());				
	}

	@Override
	public void defineVars(VariableGrid grid) throws Exception {

		vars.setTitle("Variáveis");

		int order = 0;

		FieldLookup fldPlanoConta = new FieldLookup(grid){
			@Override
			public void onAfterChange() {
				super.onAfterChange();

				VariableGrid vGrid = (VariableGrid) this.getGrid();				
				PlanoConta planoConta = (PlanoConta)this.getValue();

				FieldLookup fldConta = (FieldLookup)vGrid.getFieldByName("conta");				

				if (planoConta != null){
					String filtro = " AND planoConta.id = " + planoConta.getId();

					fldConta.setFilterExpress(filtro);
					fldConta.setReadOnly(false);					
				} else {				
					fldConta.setReadOnly(true);					
				}
			}			
		};

		FieldLookup fldConta = new FieldLookup(grid);	
		FieldSimpleDate fldDataInicial = new FieldSimpleDate(grid);
		FieldSimpleDate fldDataFinal = new FieldSimpleDate(grid);		

		fldPlanoConta.setBeanName(PlanoConta.class.getName());
		fldPlanoConta.setLabel("Plano de Contas");
		fldPlanoConta.setName("planoConta");
		fldPlanoConta.setRequired(true);		
		fldPlanoConta.setOrder(order++);		

		fldConta.setBeanName(ContaContabil.class.getName());
		fldConta.setLabel("Conta");
		fldConta.setName("conta");
		fldConta.setReadOnly(true);
		fldConta.setRequired(true);
		fldConta.setOrder(order++);		

		fldDataInicial.setLabel("Data Inicial");
		fldDataInicial.setName("dataInicial");
		fldDataInicial.setRequired(true);
		fldDataInicial.setWidth(150);
		fldDataInicial.setOrder(order++);

		fldDataFinal.setLabel("Data Final");
		fldDataFinal.setName("dataFinal");
		fldDataFinal.setRequired(true);
		fldDataFinal.setWidth(150);
		fldDataFinal.setOrder(order++);		

	}

	@Override
	public void prepareLayout() throws Exception {
		this.setPlanoConta((PlanoConta) vars.getFieldByName("planoConta").getValue());
		this.setConta((ContaContabil) vars.getFieldByName("conta").getValue());
		this.setDataInicial((Date) vars.getFieldByName("dataInicial").getValue());
		this.setDataFinal((Date) vars.getFieldByName("dataFinal").getValue());

		showLayout.setDefined(false);
	}

	@Override
	public void defineShowLayout(Interaction interaction) throws Exception {
		this.regraRazao.setPlanoConta(this.getPlanoConta());
		this.regraRazao.setContaRestricao(this.getConta());		
		this.regraRazao.setDataInicial(this.getDataInicial());
		this.regraRazao.setDataFinal(this.getDataFinal());

		this.dataListRazao = this.regraRazao.retornaRazao();

		if (this.dataListRazao.isEmpty()){
			throw new AppException("Não foram encontrados Lançamentos com as restrições informadas.");			
		} else {
			this.dataListRazao.sort("contaContabil");			
		}

		Layout layoutRazao = new Layout(this, "razaoLayout", this.dataListRazao, RazaoReportBean.class.getName());
		layoutRazao.setTitle("Razão");		
		layoutRazao.setGroupBy("contaContabil");

		interaction.add(layoutRazao);
	}
}