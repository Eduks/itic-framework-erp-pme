package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.cadastro.GrupoLancamentoContabil;
import br.com.dyad.backoffice.entidade.cadastro.GrupoLancamentoContabilBaixaPedido;
import br.com.dyad.backoffice.entidade.cadastro.GrupoLancamentoContabilBaixaTitulo;
import br.com.dyad.backoffice.entidade.cadastro.GrupoLancamentoContabilTransfDispo;
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

public class GrupoLancamentoContabilWindow extends Window {

	private GridVariaveisGrupoLancamento gridVariaveisGrupoLancamento;

	private DataGrid gridGruposBaixaPedido;
	private DataGrid gridGruposBaixaTitulo;
	private DataGrid gridGruposTransfDispo;

	public GrupoLancamentoContabilWindow(HttpSession httpSession) throws Exception {
		super(httpSession);
	}

	@Override
	public void defineWindow() throws Exception {
		gridVariaveisGrupoLancamento = new GridVariaveisGrupoLancamento(this, "gridVariaveisGrupoLancamento");
	}

	public Interaction interactionInicio = new Interaction(this, "interactionInicio"){
		@Override
		public void defineInteraction() throws Exception {
			
			add(gridVariaveisGrupoLancamento);

			FieldClassLookup gruposLancamento = gridVariaveisGrupoLancamento.getGruposLancamento();

			gruposLancamento.addWidgetListener(DyadEvents.onAfterChange, new WidgetListener() {
				@Override
				public void handleEvent(Object sender) throws Exception {
					FieldClassLookup f = (FieldClassLookup)sender;
					
					if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(GrupoLancamentoContabilBaixaPedido.class.getName())) {
						
						gridGruposBaixaPedido = new DataGrid(getWindow(), "gridGruposBaixaPedido");
						gridGruposBaixaPedido.setTitle("Grupos Contábeis de Baixas de Pedidos");
						gridGruposBaixaPedido.setBeanName(GrupoLancamentoContabilBaixaPedido.class.getName());
						gridGruposBaixaPedido.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridGruposBaixaPedido.getDataList().executeQuery("FROM GrupoLancamentoContabilBaixaPedido");
						gridGruposBaixaPedido.getDataList().setCommitOnSave(true);
						
						setNextInteraction(interactionBaixaPedido);
						
					} else if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(GrupoLancamentoContabilBaixaTitulo.class.getName())) {
						
						gridGruposBaixaTitulo = new DataGrid(getWindow(), "gridGruposBaixaTitulo");
						gridGruposBaixaTitulo.setTitle("Grupos Contábeis de Baixas de Títulos");
						gridGruposBaixaTitulo.setBeanName(GrupoLancamentoContabilBaixaTitulo.class.getName());
						gridGruposBaixaTitulo.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridGruposBaixaTitulo.getDataList().executeQuery("FROM GrupoLancamentoContabilBaixaTitulo");
						gridGruposBaixaTitulo.getDataList().setCommitOnSave(true);
						
						setNextInteraction(interactionBaixaTitulo);
						
					} else if (f.getDataClassEntity().getBeanName().equalsIgnoreCase(GrupoLancamentoContabilTransfDispo.class.getName())) {
						
						gridGruposTransfDispo = new DataGrid(getWindow(), "gridGruposTransfDispo");
						gridGruposTransfDispo.setTitle("Grupos Contábeis de Transferências de Disponíveis");
						gridGruposTransfDispo.setBeanName(GrupoLancamentoContabilTransfDispo.class.getName());
						gridGruposTransfDispo.setDataList(DataListFactory.newDataList(getDatabase()));
						
						gridGruposTransfDispo.getDataList().executeQuery("FROM GrupoLancamentoContabilTransfDispo");
						gridGruposTransfDispo.getDataList().setCommitOnSave(true);
						
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
			
			gridGruposBaixaPedido = null;
			gridGruposBaixaTitulo = null;
			gridGruposTransfDispo = null;
			
			setNextInteraction(interactionInicio);
		}
	}
	
	public Interaction interactionBaixaPedido = new Interaction(this, "interactionBaixaPedido"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridGruposBaixaPedido);
		}
	};
	
	public Interaction interactionBaixaTitulo = new Interaction(this, "interactionBaixaTitulo"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridGruposBaixaTitulo);
		}
	};
	
	public Interaction interactionTransfDispo = new Interaction(this, "interactionTransfDispo"){
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionFechar);
			
			add(gridGruposTransfDispo);
		}
	};

	public GridVariaveisGrupoLancamento getGridVariaveisGrupoLancamento() {
		return gridVariaveisGrupoLancamento;
	}

	public void setGridVariaveisGrupoLancamento(
			GridVariaveisGrupoLancamento gridVariaveisGrupoLancamento) {
		this.gridVariaveisGrupoLancamento = gridVariaveisGrupoLancamento;
	}

};

class GridVariaveisGrupoLancamento extends VariableGrid {

	private FieldClassLookup gruposLancamento;

	public GridVariaveisGrupoLancamento(Window window, String name)
	throws Exception {
		super(window, name);

		setTitle("Tipos de Grupo de Lançamento");

		gruposLancamento = new FieldClassLookup(this);
	}

	@Override
	public void defineGrid() throws Exception {

		gruposLancamento.setName("gruposLancamento");
		gruposLancamento.setLabel("Tipo de Grupo");
		gruposLancamento.setWidth(250);
		gruposLancamento.setBeanName(GrupoLancamentoContabil.class.getName());
		
		String childrenString = HibernateUtil.getInstance(getWindow().getDatabase()).getChildrenString(GrupoLancamentoContabil.class);
		
		childrenString = childrenString.replaceFirst("-89999999999353", "");
		
		gruposLancamento.setFilterExpress(childrenString);
	}

	public FieldClassLookup getGruposLancamento() {
		return gruposLancamento;
	}

	public void setGruposLancamento(FieldClassLookup gruposLancamento) {
		this.gruposLancamento = gruposLancamento;
	}

}
