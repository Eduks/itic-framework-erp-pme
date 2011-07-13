package br.com.dyad.backoffice.navigation.cadastro;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraPlanoConta;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.DyadEvents;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.WidgetListener;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldUpload;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.Grid;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class PlanoContaWindow extends Window {

	private GridVariaveisCarregarArquivoPlanoConta gridCarregarArquivoPlanoConta;

	private DataGrid gridPlanoConta;

	private DataList dataListPlanoConta;

	private RegraPlanoConta regraPlanoConta;

	private AppTransaction appTransaction;

	public PlanoContaWindow(HttpSession httpSession) {
		super(httpSession);
	}

	@Override 
	public void defineWindow() throws Exception {

		gridCarregarArquivoPlanoConta = new GridVariaveisCarregarArquivoPlanoConta(this, "gridCarregarArquivoPlanoConta");

		dataListPlanoConta = DataListFactory.newDataList(getDatabase());
		dataListPlanoConta.executeQuery("FROM PlanoConta");

		gridPlanoConta = new DataGrid(this, "gridPlanoConta");
		gridPlanoConta.setTitle("Planos de Contas");
		gridPlanoConta.setBeanName(PlanoConta.class.getName());
		gridPlanoConta.setDataList(dataListPlanoConta);
		gridPlanoConta.setViewMode(DataGrid.VIEW_MODE_TABLEVIEW);

		gridPlanoConta.addWidgetListener(DyadEvents.onBeforeDelete, new WidgetListener(){
			@Override
			public void handleEvent(Object sender) throws Exception {
				PlanoConta planoConta = (PlanoConta)((DataGrid)sender).getCurrentEntity();

				for (ContaContabil contaContabil : planoConta.getContasContabeis()) {
					if (regraPlanoConta.validaExclusao(contaContabil)) {
						appTransaction.delete(contaContabil);
					}
				}
			}
		});

		appTransaction = gridPlanoConta.getDataList().getTransactionalSession();

		regraPlanoConta = new RegraPlanoConta(appTransaction);
	}

	@Override
	public Boolean add(Grid grid) throws Exception {

		if (grid instanceof DataGrid) {

			if (((DataGrid)grid).getBeanName().equalsIgnoreCase(ContaContabil.class.getName())){

				grid.addWidgetListener(DyadEvents.onBeforePost, new WidgetListener(){
					@Override
					public void handleEvent(Object sender) throws Exception {
						ContaContabil contaContabil = (ContaContabil)((DataGrid)sender).getCurrentEntity();
						PlanoConta planoConta = contaContabil.getPlanoConta();

						regraPlanoConta.validaCodigoUnico(contaContabil);

						regraPlanoConta.validaAnalitico(contaContabil);

						if (!planoConta.getContasContabeis().contains(contaContabil)) {
							planoConta.addContaContabil(contaContabil);
						}
					}
				});

				grid.addWidgetListener(DyadEvents.onBeforeDelete, new WidgetListener(){
					@Override
					public void handleEvent(Object sender) throws Exception {
						ContaContabil contaContabil = (ContaContabil)((DataGrid)sender).getCurrentEntity();
						PlanoConta planoConta = contaContabil.getPlanoConta();

						regraPlanoConta.validaExclusao((ContaContabil)((DataGrid)sender).getCurrentEntity());

						planoConta.getContasContabeis().remove(contaContabil);
					}
				});
			}
		}

		return super.add(grid);
	}

	public Action actionDuplicarPlano = new Action(this, "Duplicar Plano de Contas"){
		@Override
		public void onClick() throws Exception {
			PlanoConta planoConta;

			if (gridPlanoConta.getSelectedRecords().size() != 1) {
				throw new AppException("Selecione um e apenas um plano de contas a ser duplicado.");
			}

			planoConta = (PlanoConta)gridPlanoConta.getSelectedRecords().get(0);

			for (Iterator<?> iterator = gridPlanoConta.getDataList().getList().iterator(); iterator.hasNext(); ) {
				PlanoConta p = (PlanoConta) iterator.next();

				if (p.getCodigo().equalsIgnoreCase(planoConta.getCodigo()+" (cópia)")) {
					throw new AppException("Este plano de contas já está duplicado! Operação não realizada.");
				}
			}

			confirm("confirmDuplicarPlano", "Deseja duplicar o plano de contas selecionado?");
		}
	};

	public void confirmDuplicarPlano(Object response) throws Exception{
		if (((String)response).equals("yes")){
			duplicarPlanoDeContas();
		}
	}

	public Action actionCarregarArquivoPlanoConta = new Action(this, "Importação de Plano de Contas"){
		@Override
		public void onClick() throws Exception {
			setNextInteraction(interactionCarregarArquivoPlanoContas);
		}
	};
	
	public Action actionImportar = new Action(this, "Importar"){
		@Override
		public void onClick() throws Exception {
			confirm("confirmImportarPlanoContas", "Deseja importar o Plano de Contas do arquivo selecionado?");
		}
	};
	
	public void confirmImportarPlanoContas(Object response) throws Exception{
		if (((String)response).equals("yes")){
			FieldUpload arquivo = (FieldUpload)gridCarregarArquivoPlanoConta.getFieldByName("arquivo");
			
			File file = new File(arquivo.getFullPath());
			
			regraPlanoConta.importarPlanoContas(file);
			
			setNextInteraction(interactionInicio);
		}
	}
	
	public Action actionInformacaoImportacao = new Action(this, "Informação"){
		@Override
		public void onClick() throws Exception {
			alert("O arquivo de importação deve conter linhas (uma para cada conta contábil), na ordem de hierarquia " +
				"do plano de contas, com os seguintes campos separados por ';' (ponto e vírgula): código;nome");
		}
	};

	public Action actionFechar = new Action(this, "Fechar"){
		@Override
		public void onClick() throws Exception {
			setNextInteraction(interactionInicio);
		}
	};

	public Interaction interactionInicio = new Interaction(this, "interactionInicio"){
		@Override
		public void defineInteraction() throws Exception {

			enableAndShowActions(actionDuplicarPlano);
			enableAndShowActions(actionCarregarArquivoPlanoConta);

			add(gridPlanoConta);
		}
	};


	public void duplicarPlanoDeContas() throws Exception{

		DataGrid gridDetail = gridPlanoConta.getDetails().get(0);

		PlanoConta planoConta = (PlanoConta)gridPlanoConta.getSelectedRecords().get(0);

		PlanoConta novoPlano = (PlanoConta)gridPlanoConta.getDataList().newEntity(PlanoConta.class.getName());
		novoPlano.setCodigo(planoConta.getCodigo()+" (cópia)");
		novoPlano.setNome(planoConta.getNome()+" (cópia)");

		gridPlanoConta.dispatchPost();

		DataList.commit(gridPlanoConta.getDataList());

		ArrayList<ContaContabil> novasContas = new ArrayList<ContaContabil>(planoConta.getContasContabeis());

		DataList.sort(novasContas, "codigo DESC");

		ContaContabil novaConta;

		for (ContaContabil contaContabil : novasContas) {
			novaConta = (ContaContabil)gridDetail.getDataList().newEntity(ContaContabil.class.getName());
			novaConta.setPlanoConta(novoPlano);
			novaConta.setApelido(contaContabil.getApelido());
			novaConta.setCodigo(contaContabil.getCodigo());
			novaConta.setNome(contaContabil.getNome());
			novaConta.setSintetico(contaContabil.getSintetico());

			novoPlano.getContasContabeis().add(novaConta);
		}
		gridDetail.dispatchPost();
	}

	public Interaction interactionCarregarArquivoPlanoContas = new Interaction(this, "interactionCarregarrArquivoPlanoConta"){
		@Override
		public void defineInteraction() throws Exception {
			enableAndShowActions(actionImportar);
			enableAndShowActions(actionFechar);
			enableAndShowActions(actionInformacaoImportacao);

			add(gridCarregarArquivoPlanoConta);
		}
	};

};

class GridVariaveisCarregarArquivoPlanoConta extends VariableGrid {

	private FieldUpload arquivo;

	public GridVariaveisCarregarArquivoPlanoConta(Window window, String name) throws Exception {
		super(window, name);

		setTitle("Configurações");

		arquivo = new FieldUpload(this);
	}

	@Override
	public void defineGrid() throws Exception {

		arquivo.setLabel("Arquivo");
		arquivo.setName("arquivo");
		arquivo.setTempFile(true);
		arquivo.setRequired(true);
		arquivo.setWidth(300);
	}

	public FieldUpload getArquivo() {
		return arquivo;
	}

	public void setArquivo(FieldUpload arquivo) {
		this.arquivo = arquivo;
	}
};