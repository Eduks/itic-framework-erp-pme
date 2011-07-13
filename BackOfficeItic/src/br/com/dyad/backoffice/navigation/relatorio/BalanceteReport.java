package br.com.dyad.backoffice.navigation.relatorio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.auxiliares.BalanceteComparativoReportBean;
import br.com.dyad.backoffice.entidade.auxiliares.BalanceteReportBean;
import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraBalancete;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.ReportWindow;
import br.com.dyad.infrastructure.widget.field.Field;
import br.com.dyad.infrastructure.widget.field.FieldBoolean;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.field.Link;
import br.com.dyad.infrastructure.widget.grid.Layout;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class BalanceteReport extends ReportWindow {
	
	public DataList balancete;
	public Layout layoutBalancete;
	private RegraBalancete regraBalancete;
	
	private PlanoConta planoConta;
	private Date dataInicial;
	private Date dataFinal;
	private ArrayList<Date> meses;
	
	public ArrayList<Date> getMeses() {
		if ( meses != null){
			return meses;
		} else {
			return this.regraBalancete.getMeses();
		}
	}
	public void setMeses(ArrayList<Date> meses) {
		this.meses = meses;
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
	
	public BalanceteReport (HttpSession httpSession) {
		super(httpSession);
		this.balancete = DataListFactory.newDataList(this.getDatabase());
		this.balancete.setCommitOnSave(false);
		this.balancete.setLogChanges(false);
		this.regraBalancete = new RegraBalancete(this.getDatabase());		
	}
	

	@Override
	public void defineShowLayout(Interaction interaction) throws Exception {
		if ( this.layoutBalancete != null ){
			interaction.remove( this.layoutBalancete );
		}
		Boolean comparativo = (Boolean) vars.getFieldByName("comparativo").getValue();
		if ( comparativo != null && comparativo ){
			this.layoutBalancete = new Layout(this, "balanceteLayout", this.balancete, BalanceteComparativoReportBean.class.getName()){
				@Override
				public void defineGrid() throws Exception{
					super.defineGrid();
				
					BalanceteReport process = (BalanceteReport) this.getWindow(); 
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
					ArrayList<Date> meses = process.getMeses();
					int quantidadeCampos = meses.size(); 					
					for (int i = 0; i < quantidadeCampos; i++) {
						Field f = this.getFieldByName("saldo_" + i);
						f.setVisible(true);
						f.setLabel( dateFormat.format(meses.get(i)));						
					}
				}
			};			
			this.criaLink();			
		} else {
			this.layoutBalancete = new Layout(this, "balanceteLayout", this.balancete, BalanceteReportBean.class.getName());
			Link linkRazao = new Link("linkRazao", RazaoReport.class.getName(), "Show Layout");
			
			linkRazao.setCreateNewTab(true);
			linkRazao.setLabel("Razão");
			linkRazao.addParam("planoContaId", this.getPlanoConta().getId());
			linkRazao.addParam("contaId", "[[contaContabil.id]]");
			linkRazao.addParam("dataInicial", this.getDataInicial());
			linkRazao.addParam("dataFinal", this.getDataFinal());
			this.layoutBalancete.addLink("nome", linkRazao);
		}		
		this.layoutBalancete.setTitle("Balancete");
		this.layoutBalancete.setHeaderMenu(false);
		
		interaction.add(this.layoutBalancete);
		
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
				FieldLookup fldContaInicial = (FieldLookup)vGrid.getFieldByName("contaInicial");
				FieldLookup fldContaFinal = (FieldLookup)vGrid.getFieldByName("contaFinal");
				
				if (planoConta != null){
					String filtro = " AND planoConta.id = " + planoConta.getId();
					
					fldConta.setFilterExpress(filtro);
					fldConta.setReadOnly(false);
					
					fldContaInicial.setFilterExpress(filtro);
					fldContaInicial.setReadOnly(false);
					
					fldContaFinal.setFilterExpress(filtro);
					fldContaFinal.setReadOnly(false);
					
				} else {
					
					fldConta.setReadOnly(true);
					fldContaInicial.setReadOnly(true);
					fldContaFinal.setReadOnly(true);
					
				}
			}			
		};
		
		
		FieldLookup fldConta = new FieldLookup(grid);
		FieldLookup fldContaInicial = new FieldLookup(grid);
		FieldLookup fldContaFinal = new FieldLookup(grid);
		FieldSimpleDate fldDataInicial = new FieldSimpleDate(grid);
		FieldSimpleDate fldDataFinal = new FieldSimpleDate(grid);
		FieldBoolean fldContaZerada = new FieldBoolean(grid);
		FieldBoolean fldComparativo = new FieldBoolean(grid);
		
		
		fldPlanoConta.setBeanName(PlanoConta.class.getName());
		fldPlanoConta.setLabel("Plano de Contas");
		fldPlanoConta.setName("planoConta");
		fldPlanoConta.setRequired(true);		
		fldPlanoConta.setOrder(order++);		
		fldPlanoConta.setFilterExpress(" AND id IN (SELECT DISTINCT(planoConta.id) FROM ContaContabil WHERE sintetico = FALSE) ");
		
		fldConta.setBeanName(ContaContabil.class.getName());
		fldConta.setLabel("Restrição de Conta");
		fldConta.setName("conta");
		fldConta.setReadOnly(true);
		fldConta.setOrder(order++);
		
		fldContaInicial.setBeanName(ContaContabil.class.getName());
		fldContaInicial.setLabel("Conta Inicial");
		fldContaInicial.setName("contaInicial");
		fldContaInicial.setReadOnly(true);
		fldContaInicial.setOrder(order++);
		
		fldContaFinal.setBeanName(ContaContabil.class.getName());
		fldContaFinal.setLabel("Conta Final");
		fldContaFinal.setName("contaFinal");
		fldContaFinal.setReadOnly(true);
		fldContaFinal.setOrder(order++);
		
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
		
		fldComparativo.setLabel("Modo Comparativo");
		fldComparativo.setName("comparativo");
		fldComparativo.setValue(false);
		fldComparativo.setOrder(order++);
		
		fldContaZerada.setLabel("Exibir Contas Zeradas");
		fldContaZerada.setName("contaZerada");
		fldContaZerada.setVisible(false);
		fldContaZerada.setValue(false);
		fldContaZerada.setOrder(order++);
		
	}

	@Override
	public void prepareLayout() throws Exception {
		
		this.setPlanoConta((PlanoConta) vars.getFieldByName("planoConta").getValue());
		this.setDataInicial((Date)vars.getFieldByName("dataInicial").getValue());
		this.setDataFinal((Date)vars.getFieldByName("dataFinal").getValue());
		
		this.regraBalancete.setPlanoConta(this.getPlanoConta());
		this.regraBalancete.setContaRestricao( (ContaContabil) vars.getFieldByName("conta").getValue());
		this.regraBalancete.setContaInicial( (ContaContabil) vars.getFieldByName("contaInicial").getValue());
		this.regraBalancete.setContaFinal( (ContaContabil) vars.getFieldByName("contaFinal").getValue());
		this.regraBalancete.setDataInicial(this.getDataInicial());
		this.regraBalancete.setDataFinal(this.getDataFinal());
		this.regraBalancete.setContaZerada( (Boolean) vars.getFieldByName("contaZerada").getValue());
		this.regraBalancete.setComparativo((Boolean) vars.getFieldByName("comparativo").getValue());		
		this.setMeses(this.regraBalancete.getMeses());
		
		this.balancete.empty();
		this.balancete = this.regraBalancete.retornaBalancete();
		
		if (this.balancete.isEmpty()){
			throw new AppException("Não foram encontrados Lançamentos com as restrições informadas.");			
		} else {
			this.balancete.sort("ordem");
		}
		
	}
	
	public void criaLink() throws Exception{
		
		Link linkRazao = new Link("linkRazao", RazaoReport.class.getName(), "Show Layout");
		
		linkRazao.setCreateNewTab(true);
		linkRazao.setLabel("Razão");
		linkRazao.addParam("planoContaId", this.getPlanoConta().getId());
		linkRazao.addParam("contaId", "[[contaContabil.id]]");
		linkRazao.addParam("dataInicial", this.getDataInicial());
		linkRazao.addParam("dataFinal", this.getDataFinal());
		
		this.layoutBalancete.addLink("nome", linkRazao);
		
		int quantidadeCampos = this.getMeses().size();
		
		for (int i = 0; i < quantidadeCampos; i++) {
			Link linkSaldo = new Link("linkSaldo_" + i, RazaoReport.class.getName(), "Show Layout");
			linkSaldo.setCreateNewTab(true);
			linkSaldo.setLabel("Razão");
			linkSaldo.addParam("planoContaId", this.getPlanoConta().getId());
			linkSaldo.addParam("contaId", "[[contaContabil.id]]");
			linkSaldo.addParam("dataInicial", this.pegarDataInicialPeriodo(i));
			linkSaldo.addParam("dataFinal", this.pegarDataFinalPeriodo(i));
			
			this.layoutBalancete.addLink("saldo_" + i, linkSaldo);			
		}		
	}
	
	private Date pegarDataInicialPeriodo( int coluna ){
		
		Date dataInicial = this.getDataInicial();		
		if (coluna != 0){
			dataInicial = this.getMeses().get(coluna);
		}		
		return dataInicial;
	}
	
	private Date pegarDataFinalPeriodo( int coluna ){
		
		Date dataFinal = this.getDataFinal();		
		if (coluna != this.getMeses().size() - 1 ){
			dataFinal = this.getMeses().get(coluna + 1);
		}		
		return dataFinal;
	}
}