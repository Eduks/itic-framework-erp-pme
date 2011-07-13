package br.com.dyad.backoffice.navigation.relatorio;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.auxiliares.DiarioReportBean;
import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraDiario;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.widget.DyadEvents;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.ReportWindow;
import br.com.dyad.infrastructure.widget.WidgetListener;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.grid.Layout;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class DiarioReport extends ReportWindow {
	
	private DataList diarioDataList;
	private RegraDiario regraDiario;	

	private PlanoConta planoConta;
	private ContaContabil conta;
	private Date dataInicial;
	private Date dataFinal;
	
	private Layout layoutDiario;
	
	public DiarioReport(HttpSession session){
		super(session);
		diarioDataList = DataListFactory.newDataList(this.getDatabase());
		diarioDataList.setCommitOnSave(false);
		diarioDataList.setLogChanges(false);
		regraDiario = new RegraDiario(this.getDatabase());
	}
	
	@Override
	public void defineShowLayout(Interaction interaction) throws Exception {
		if ( this.layoutDiario != null ){
			interaction.remove( this.layoutDiario );
		}
		
		layoutDiario = new Layout(this, "diarioLayout", diarioDataList, DiarioReportBean.class.getName());
		
		layoutDiario.setTitle("Diário");
		layoutDiario.setHeaderMenu(false);
		
		interaction.add(layoutDiario);
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
		
		fldDataInicial.addWidgetListener(DyadEvents.onAfterChange, new WidgetListener() {
			
			@Override
			public void handleEvent(Object sender) throws Exception {
				
				VariableGrid vGrid = (VariableGrid)((FieldSimpleDate)sender).getGrid();

				Date emissaoInicial = ((FieldSimpleDate)sender).getValue();
				
				FieldSimpleDate fldEmissaoFinal = (FieldSimpleDate) vGrid.getFieldByName("fldDataFinal");

				if (emissaoInicial != null && fldEmissaoFinal.getValue() != null
						&& emissaoInicial.after(fldEmissaoFinal.getValue())) {
					SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
					throw new AppException("A data de Emissão Inicial '"
							+ dataFormat.format(emissaoInicial)
							+ "' deve ser menor ou igual a Data de Emissão Final'"
							+ dataFormat.format(fldEmissaoFinal.getValue()) + "'.");
				}
			}
		});
		
		fldDataFinal.addWidgetListener(DyadEvents.onAfterChange, new WidgetListener() {
			
			@Override
			public void handleEvent(Object sender) throws Exception {
				
				VariableGrid vGrid = (VariableGrid)((FieldSimpleDate)sender).getGrid();

				Date emissaoFinal = ((FieldSimpleDate)sender).getValue();
				
				FieldSimpleDate fldEmissaoInicial = (FieldSimpleDate) vGrid.getFieldByName("fldDataInicial");

				if (emissaoFinal != null && fldEmissaoInicial.getValue() != null
						&& emissaoFinal.before(fldEmissaoInicial.getValue())) {
					SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
					throw new AppException("A data de Emissão Inicial '"
							+ dataFormat.format(emissaoFinal)
							+ "' deve ser menor ou igual a Data de Emissão Final'"
							+ dataFormat.format(fldEmissaoInicial.getValue()) + "'.");
				}
			}
		});
		
		fldDataInicial.setLabel("Data Inicial");
		fldDataInicial.setName("fldDataInicial");
		fldDataInicial.setRequired(true);
		fldDataInicial.setWidth(150);
		fldDataInicial.setOrder(order++);

		fldDataFinal.setLabel("Data Final");
		fldDataFinal.setName("fldDataFinal");		
		fldDataFinal.setRequired(true);
		fldDataFinal.setWidth(150);
		fldDataFinal.setOrder(order++);
	}

	@Override
	public void prepareLayout() throws Exception {
		this.setPlanoConta((PlanoConta) vars.getFieldByName("planoConta").getValue());
		this.setDataInicial((Date)vars.getFieldByName("fldDataInicial").getValue());
		this.setDataFinal((Date)vars.getFieldByName("fldDataFinal").getValue());
		
		this.regraDiario.setPlanoConta(this.getPlanoConta());
		this.regraDiario.setContaRestricao( (ContaContabil) vars.getFieldByName("conta").getValue());
		this.regraDiario.setContaInicial( (ContaContabil) vars.getFieldByName("contaInicial").getValue());
		this.regraDiario.setContaFinal( (ContaContabil) vars.getFieldByName("contaFinal").getValue());
		this.regraDiario.setDataInicial(this.getDataInicial());
		this.regraDiario.setDataFinal(this.getDataFinal());
		
		this.diarioDataList.getList().clear();
		
		this.diarioDataList = this.regraDiario.retornaDiario();
		
		if (this.diarioDataList.isEmpty()){
			throw new AppException("Não foram encontrados Lançamentos com as restrições informadas.");			
		}/* else {
			this.diarioDataList.sort("ordem");
		}*/
	}

	public DataList getDiarioDataList() {
		return diarioDataList;
	}

	public void setDiarioDataList(DataList diarioDataList) {
		this.diarioDataList = diarioDataList;
	}

	public RegraDiario getRegraDiario() {
		return regraDiario;
	}

	public void setRegraDiario(RegraDiario regraDiario) {
		this.regraDiario = regraDiario;
	}

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

}
