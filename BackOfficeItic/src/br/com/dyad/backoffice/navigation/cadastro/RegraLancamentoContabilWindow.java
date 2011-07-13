package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoContabil;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoContabilBaixaPedido;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoContabilBaixaTitulo;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoContabilTransfDispo;
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

public class RegraLancamentoContabilWindow extends Window {
	
	private GridVariaveisRegraLancamentoContabil gridVariaveisRegraLancamentoContabil;

	private DataGrid gridRegrasBaixaPedido;
	private DataGrid gridRegrasBaixaTitulo;
	private DataGrid gridRegrasTransfDispo;

	public RegraLancamentoContabilWindow(HttpSession httpSession) {
		super(httpSession);
	}

	@Override
	public void defineWindow() throws Exception {
		gridVariaveisRegraLancamentoContabil = new GridVariaveisRegraLancamentoContabil(this, "gridVariaveisRegraLancamento");
	}
	
	public Interaction interactionInicio = new Interaction(this, "interactionInicio"){
		@Override
		public void defineInteraction() throws Exception {
			
			add(gridVariaveisRegraLancamentoContabil);

			FieldClassLookup regrasLancamento = gridVariaveisRegraLancamentoContabil.getRegrasLancamento();

			regrasLancamento.addWidgetListener(DyadEvents.onAfterChange, new WidgetListener() {
				@Override
				public void handleEvent(Object sender) throws Exception {
					FieldClassLookup f = (FieldClassLookup)sender;
					
					if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(RegraLancamentoContabilBaixaPedido.class.getName())) {
						
						gridRegrasBaixaPedido = new DataGrid(getWindow(), "gridRegrasBaixaPedido");
						gridRegrasBaixaPedido.setTitle("Regras Contábeis de Baixas de Pedidos");
						gridRegrasBaixaPedido.setBeanName(RegraLancamentoContabilBaixaPedido.class.getName());
						gridRegrasBaixaPedido.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridRegrasBaixaPedido.getDataList().executeQuery("FROM RegraLancamentoContabilBaixaPedido");
						gridRegrasBaixaPedido.getDataList().setCommitOnSave(true);
						
						setNextInteraction(interactionBaixaPedido);
						
					} else if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(RegraLancamentoContabilBaixaTitulo.class.getName())) {
						
						gridRegrasBaixaTitulo = new DataGrid(getWindow(), "gridRegrasBaixaTitulo");
						gridRegrasBaixaTitulo.setTitle("Regras Contábeis de Baixas de Títulos");
						gridRegrasBaixaTitulo.setBeanName(RegraLancamentoContabilBaixaTitulo.class.getName());
						gridRegrasBaixaTitulo.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridRegrasBaixaTitulo.getDataList().executeQuery("FROM RegraLancamentoContabilBaixaTitulo");
						gridRegrasBaixaTitulo.getDataList().setCommitOnSave(true);
						
						setNextInteraction(interactionBaixaTitulo);
						
					} else if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(RegraLancamentoContabilTransfDispo.class.getName())) {
						
						gridRegrasTransfDispo = new DataGrid(getWindow(), "gridRegrasTransfDispo");
						gridRegrasTransfDispo.setTitle("Regras Contábeis de Transferências de Disponíveis");
						gridRegrasTransfDispo.setBeanName(RegraLancamentoContabilTransfDispo.class.getName());
						gridRegrasTransfDispo.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridRegrasTransfDispo.getDataList().executeQuery("FROM RegraLancamentoContabilTransfDispo");
						gridRegrasTransfDispo.getDataList().setCommitOnSave(true);
						
						setNextInteraction(interactionTransfDispo);
						
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
			
			gridRegrasBaixaPedido = null;
			gridRegrasBaixaTitulo = null;
			gridRegrasTransfDispo = null;
			
			setNextInteraction(interactionInicio);
		}
	}
	
	public Interaction interactionBaixaPedido = new Interaction(this, "interactionBaixaPedido"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridRegrasBaixaPedido);
		}
	};
	
	public Interaction interactionBaixaTitulo = new Interaction(this, "interactionBaixaTitulo"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridRegrasBaixaTitulo);
		}
	};
	
	public Interaction interactionTransfDispo = new Interaction(this, "interactionTransfDispo"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridRegrasTransfDispo);
		}
	};
	
};

class GridVariaveisRegraLancamentoContabil extends VariableGrid {

	private FieldClassLookup regrasLancamento;

	public GridVariaveisRegraLancamentoContabil(Window window, String name)
	throws Exception {
		super(window, name);

		setTitle("Tipos de Regra de Lançamento Contábil");

		regrasLancamento = new FieldClassLookup(this);
	}

	@Override
	public void defineGrid() throws Exception {

		regrasLancamento.setName("regrasLancamento");
		regrasLancamento.setLabel("Tipo de Regra");
		regrasLancamento.setWidth(250);
		regrasLancamento.setBeanName(RegraLancamentoContabil.class.getName());
		
		String childrenString = HibernateUtil.getInstance(getWindow().getDatabase()).getChildrenString(RegraLancamentoContabil.class);
		
		childrenString = childrenString.replaceFirst("-89999999999302", "");
		
		regrasLancamento.setFilterExpress(childrenString);
	}

	public FieldClassLookup getRegrasLancamento() {
		return regrasLancamento;
	}

	public void setRegrasLancamento(FieldClassLookup regrasLancamento) {
		this.regrasLancamento = regrasLancamento;
	}

}