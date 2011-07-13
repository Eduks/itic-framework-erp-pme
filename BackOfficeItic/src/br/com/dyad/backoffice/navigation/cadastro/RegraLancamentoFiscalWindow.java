package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoFiscal;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoFiscalCOFINS;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoFiscalCSLL;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoFiscalINSS;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoFiscalIR;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoFiscalISS;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoFiscalPIS;
import br.com.dyad.infrastructure.navigation.persistence.HibernateUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.DyadEvents;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.WidgetListener;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldClassLookup;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class RegraLancamentoFiscalWindow extends Window {
	
	private GridVariaveisRegraLancamentoFiscal gridVariaveisRegraLancamentoFiscal;

	private DataGrid gridRegrasCOFINS;
	private DataGrid gridRegrasCSLL;
	private DataGrid gridRegrasINSS;
	private DataGrid gridRegrasIR;
	private DataGrid gridRegrasISS;
	private DataGrid gridRegrasPIS;
	
	public RegraLancamentoFiscalWindow(HttpSession httpSession) {
		super(httpSession);
	}

	@Override
	public void defineWindow() throws Exception {
		gridVariaveisRegraLancamentoFiscal = new GridVariaveisRegraLancamentoFiscal(this, "gridVariaveisRegraLancamento");
	}
	
	public Interaction interactionInicio = new Interaction(this, "interactionInicio"){
		@Override
		public void defineInteraction() throws Exception {
			
			add(gridVariaveisRegraLancamentoFiscal);

			FieldClassLookup regrasLancamento = gridVariaveisRegraLancamentoFiscal.getRegrasLancamento();

			regrasLancamento.addWidgetListener(DyadEvents.onAfterChange, new WidgetListener() {
				@Override
				public void handleEvent(Object sender) throws Exception {
					FieldClassLookup f = (FieldClassLookup)sender;
					
					if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(RegraLancamentoFiscalCOFINS.class.getName())) {
						
						gridRegrasCOFINS = new DataGrid(getWindow(), "gridRegrasCOFINS");
						gridRegrasCOFINS.setTitle("Regras Fiscais de COFINS");
						gridRegrasCOFINS.setBeanName(RegraLancamentoFiscalCOFINS.class.getName());
						gridRegrasCOFINS.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridRegrasCOFINS.getDataList().executeQuery("FROM RegraLancamentoFiscalCOFINS");
						gridRegrasCOFINS.getDataList().setCommitOnSave(true);
						
						setNextInteraction(interactionCOFINS);
						
					} else if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(RegraLancamentoFiscalCSLL.class.getName())) {
						
						gridRegrasCSLL = new DataGrid(getWindow(), "gridRegrasCSLL");
						gridRegrasCSLL.setTitle("Regras Fiscais de CSLL");
						gridRegrasCSLL.setBeanName(RegraLancamentoFiscalCSLL.class.getName());
						gridRegrasCSLL.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridRegrasCSLL.getDataList().executeQuery("FROM RegraLancamentoFiscalCSLL");
						gridRegrasCSLL.getDataList().setCommitOnSave(true);
					
						setNextInteraction(interactionCSLL);
						
					} else if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(RegraLancamentoFiscalINSS.class.getName())) {
						
						gridRegrasINSS = new DataGrid(getWindow(), "gridRegrasINSS");
						gridRegrasINSS.setTitle("Regras Fiscais de INSS");
						gridRegrasINSS.setBeanName(RegraLancamentoFiscalINSS.class.getName());
						gridRegrasINSS.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridRegrasINSS.getDataList().executeQuery("FROM RegraLancamentoFiscalINSS");
						gridRegrasINSS.getDataList().setCommitOnSave(true);
					
						setNextInteraction(interactionINSS);
						
					} else if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(RegraLancamentoFiscalIR.class.getName())) {
						
						gridRegrasIR = new DataGrid(getWindow(), "gridRegrasIR");
						gridRegrasIR.setTitle("Regras Fiscais de IR");
						gridRegrasIR.setBeanName(RegraLancamentoFiscalIR.class.getName());
						gridRegrasIR.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridRegrasIR.getDataList().executeQuery("FROM RegraLancamentoFiscalIR");
						gridRegrasIR.getDataList().setCommitOnSave(true);
					
						setNextInteraction(interactionIR);
						
					} else if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(RegraLancamentoFiscalISS.class.getName())) {
						
						gridRegrasISS = new DataGrid(getWindow(), "gridRegrasISS");
						gridRegrasISS.setTitle("Regras Fiscais de ISS");
						gridRegrasISS.setBeanName(RegraLancamentoFiscalISS.class.getName());
						gridRegrasISS.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridRegrasISS.getDataList().executeQuery("FROM RegraLancamentoFiscalISS");
						gridRegrasISS.getDataList().setCommitOnSave(true);
					
						setNextInteraction(interactionISS);
						
					} else if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(RegraLancamentoFiscalPIS.class.getName())) {
						
						gridRegrasPIS = new DataGrid(getWindow(), "gridRegrasPIS");
						gridRegrasPIS.setTitle("Regras Fiscais de PIS");
						gridRegrasPIS.setBeanName(RegraLancamentoFiscalPIS.class.getName());
						gridRegrasPIS.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridRegrasPIS.getDataList().executeQuery("FROM RegraLancamentoFiscalPIS");
						gridRegrasPIS.getDataList().setCommitOnSave(true);
					
						setNextInteraction(interactionPIS);
					}
				}
			});
		}
	};
	
	public Action actionFechar = new Action(this, "Voltar"){
		public void onClick() throws Exception{
			confirm("confirmFechar", "Deseja voltar para a tela inicial?");
		}
	};

	public void confirmFechar(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			
			gridRegrasCOFINS = null;
			gridRegrasCSLL = null;
			gridRegrasINSS = null;
			gridRegrasIR = null;
			gridRegrasISS = null;
			gridRegrasPIS = null;
			
			setNextInteraction(interactionInicio);
		}
	}
	
	public Interaction interactionCOFINS = new Interaction(this, "interactionCOFINS"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridRegrasCOFINS);
		}
	};
	
	public Interaction interactionCSLL = new Interaction(this, "interactionCSLL"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridRegrasCSLL);
		}
	};
	
	public Interaction interactionINSS = new Interaction(this, "interactionINSS"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridRegrasINSS);
		}
	};
	
	public Interaction interactionIR = new Interaction(this, "interactionIR"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridRegrasIR);
		}
	};
	
	public Interaction interactionISS = new Interaction(this, "interactionISS"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridRegrasISS);
		}
	};
	
	public Interaction interactionPIS = new Interaction(this, "interactionPIS"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridRegrasPIS);
		}
	};
};

class GridVariaveisRegraLancamentoFiscal extends VariableGrid {

	private FieldClassLookup regrasLancamento;

	public GridVariaveisRegraLancamentoFiscal(Window window, String name)
	throws Exception {
		super(window, name);

		setTitle("Tipos de Regra de Lançamento Fiscal");

		regrasLancamento = new FieldClassLookup(this);
	}

	@Override
	public void defineGrid() throws Exception {

		regrasLancamento.setName("regrasLancamento");
		regrasLancamento.setLabel("Tipo de Regra");
		regrasLancamento.setWidth(250);
		regrasLancamento.setBeanName(RegraLancamentoFiscal.class.getName());
		
		String childrenString = HibernateUtil.getInstance(getWindow().getDatabase()).getChildrenString(RegraLancamentoFiscal.class);
		
		childrenString = childrenString.replaceFirst("-89999999999318", "");
		
		regrasLancamento.setFilterExpress(childrenString);
	}

	public FieldClassLookup getRegrasLancamento() {
		return regrasLancamento;
	}

	public void setRegrasLancamento(FieldClassLookup regrasLancamento) {
		this.regrasLancamento = regrasLancamento;
	}

}