package br.com.dyad.backoffice.navigation.operacao;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraLancamento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldInteger;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.field.FieldString;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

/**
 * @author Vitor Rafael
 *
 */
public class LancamentoWindow extends Window {

	private AppTransaction appTransaction;

	private GridFiltroLancamento gridFiltroLancamento;
	private GridEdicaoLancamento gridEdicaoLancamento;
	private GridPlanoContaSelecionado gridPlanoContaSelecionado;

	public RegraLancamento regraLancamento;
	
	public LancamentoWindow (HttpSession httpSession) throws Exception {
		super(httpSession);

		gridFiltroLancamento = new GridFiltroLancamento(this);
	}

	@Override
	public void defineWindow() throws Exception {
		appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDatabase()), this.getDatabase());
		regraLancamento = new RegraLancamento(appTransaction);

		gridEdicaoLancamento = new GridEdicaoLancamento(this, regraLancamento);
		gridEdicaoLancamento.setDataList(regraLancamento.getDataListCabecalhos());

		((DetailGridItensLancamento)gridEdicaoLancamento.getDetailGridItensLancamento()).resetaDataList();

		gridPlanoContaSelecionado = new GridPlanoContaSelecionado(this);
	}

	public Interaction interactionInicio = new Interaction(this, "interactionInicio"){
		@Override
		public void defineInteraction() throws Exception {
			add(gridFiltroLancamento);

			enableAndShowActions(actionLocalizar);
			enableAndShowActions(actionNovo);
		}
	};

	public Action actionNovo = new Action(this, "Novo"){
		public void onClick() throws Exception{
			LancamentoWindow process = (LancamentoWindow)this.getParent();

			if (gridFiltroLancamento.getPlanoConta().getValue() == null)
				throw new AppException("Selecione um Plano de Contas para iniciar o lan�amento!");

			//TODO A linha abaixo está marcada para que, caso haja uma solução melhor, este código seja mudado para
			//ficar mais elegante. OBS: ESTE "TODO_" ESTÁ ANOTADO NO COMPUTADOR DO RAFAEL, NUM ARQUIVO DE PENDÊNCIAS,
			//NO DESKTOP.
			gridEdicaoLancamento.getDetails().get(0).setDataList(DataListFactory.newDataList(appTransaction));
			
			PlanoConta planoConta = (PlanoConta)process.gridFiltroLancamento.getPlanoConta().getValue();

			gridPlanoContaSelecionado.setPlanoConta(planoConta.getNome());
			
			gridEdicaoLancamento.dispatchInsert();
			
			process.setNextInteraction(interactionCadastro);
		}
	};
	

	public Interaction interactionCadastro = new Interaction(this, "interactionCadastro"){
		@Override
		public void defineInteraction() throws Exception {

			enableAndShowActions(actionFechar);
			enableAndShowActions(actionGravar);

			gridEdicaoLancamento.first();

			add(gridPlanoContaSelecionado);

			add(gridEdicaoLancamento);
		}
	};

	public Action actionGravar = new Action(this, "Gravar"){
		public void onClick() throws Exception{
			confirm("confirmGravar", "Deseja gravar a operação atual?");			
		}
	};

	public void confirmGravar(Object response) throws Exception{
		if (((String)response).equals("yes")) {
			regraLancamento.grava();
			regraLancamento.fecha();

			setNextInteraction(interactionInicio);
		}
	}

	public Action actionLocalizar = new Action(this, "Localizar"){
		public void onClick() throws Exception{

			gridEdicaoLancamento.setViewMode(DataGrid.VIEW_MODE_TABLEVIEW);

			regraLancamento.getConsultaLancamento().setContabilId((Long)gridFiltroLancamento.getContabilId().getValue());
			regraLancamento.getConsultaLancamento().setPlanoConta((PlanoConta)gridFiltroLancamento.getPlanoConta().getValue());
			regraLancamento.getConsultaLancamento().setEntidade((Entidade)gridFiltroLancamento.getEntidade().getValue());
			regraLancamento.getConsultaLancamento().setEmissaoInicial(gridFiltroLancamento.getDataEmissaoInicial().getValue());
			regraLancamento.getConsultaLancamento().setEmissaoFinal(gridFiltroLancamento.getDataEmissaoFinal().getValue());

			interactionCadastro.setDefined(false);

			PlanoConta planoConta = (PlanoConta)gridFiltroLancamento.getPlanoConta().getValue();

			gridPlanoContaSelecionado.setPlanoConta(planoConta.getNome());

			regraLancamento.abre();

			if (regraLancamento.getDataListCabecalhos().isEmpty()) {
				throw new AppException("Não foram encontrados Lançamentos com as restrições informadas.");
			}

			setNextInteraction(interactionCadastro);
		}
	};

	public Action actionFechar = new Action(this, "Fechar"){
		public void onClick() throws Exception{
			confirm("confirmFechar", "Deseja fechar a operação?");
		}
	};
	
	public void confirmFechar(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			regraLancamento.fecha();

			setNextInteraction(interactionInicio);
		}
	}
	
	public GridFiltroLancamento getGridFiltroLancamento(){
		return gridFiltroLancamento;
	}

	public GridEdicaoLancamento getGridEdicaoLancamento() {
		return gridEdicaoLancamento;
	}

	public void setGridEdicaoLancamento(GridEdicaoLancamento gridEdicaoLancamento) {
		this.gridEdicaoLancamento = gridEdicaoLancamento;
	}

}

class GridFiltroLancamento extends VariableGrid{
	
	private LancamentoWindow process;
	
	private FieldInteger contabilId;
	private FieldLookup planoConta;
	private FieldLookup entidade;
	private FieldSimpleDate dataEmissaoInicial;
	private FieldSimpleDate dataEmissaoFinal;

	public GridFiltroLancamento(Window window) throws Exception {
		super(window, "interactionInicio");

		setTitle("Filtro para Localização de Lançamentos");
		
		process = (LancamentoWindow)window;
		
		contabilId = new FieldInteger(this);
		
		planoConta = new FieldLookup(this) {
			@Override
			public void onAfterChange() {
				super.onAfterChange();
				
				DetailGridItensLancamento detail = process.getGridEdicaoLancamento().getDetailGridItensLancamento();
				PlanoConta planoConta = (PlanoConta)this.getValue();
				
				if (planoConta != null) {
					String filtro = " AND (sintetico = FALSE OR sintetico IS NULL) AND planoConta.id = " + planoConta.getId();
					
					detail.setFiltroContaContabilString(filtro);
				}
			}
		};
		
		entidade = new FieldLookup(this);

		dataEmissaoInicial = new FieldSimpleDate(this) {

			@Override
			public void onAfterChange() {
				super.onAfterChange();

				VariableGrid vGrid = (VariableGrid) this.getGrid();
				Date emissaoInicial = (Date) this.getValue();

				FieldSimpleDate fldEmissaoFinal = (FieldSimpleDate) vGrid.getFieldByName("dataEmissaoFinal");

				if (emissaoInicial != null && fldEmissaoFinal.getValue() != null
						&& emissaoInicial.after(fldEmissaoFinal.getValue())) {
					SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
					throw new AppException("A data de Emissão Inicial '"
							+ dataFormat.format(emissaoInicial)
							+ "' deve ser menor ou igual a Data de Emissão Final'"
							+ dataFormat.format(fldEmissaoFinal.getValue()) + "'.");
				}

			}
		};

		dataEmissaoFinal = new FieldSimpleDate(this) {

			@Override
			public void onAfterChange() {
				super.onAfterChange();

				VariableGrid vGrid = (VariableGrid) this.getGrid();
				Date emissaoFinal = (Date) this.getValue();

				FieldSimpleDate fldEmissaoInicial = (FieldSimpleDate) vGrid.getFieldByName("dataEmissaoInicial");

				if (emissaoFinal != null && fldEmissaoInicial.getValue() != null
						&& emissaoFinal.before(fldEmissaoInicial.getValue())) {
					SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
					throw new AppException("A data de Emissão Inicial '"
							+ dataFormat.format(emissaoFinal)
							+ "' deve ser menor ou igual a Data de Emissão Final'"
							+ dataFormat.format(fldEmissaoInicial.getValue()) + "'.");
				}

			}
		};
	}

	@Override
	public void defineGrid() throws Exception {

		contabilId.setName("unificador");
		contabilId.setLabel("Id da Operação");

		planoConta.setName("planoConta");
		planoConta.setLabel("Plano de Contas");
		planoConta.setRequired(true);
		planoConta.setWidth(300);
		planoConta.setBeanName(PlanoConta.class.getName());
		planoConta.setFilterExpress(" AND id IN (SELECT DISTINCT(planoConta.id) FROM ContaContabil WHERE sintetico = FALSE) ");

		entidade.setName("entidade");
		entidade.setLabel("Entidade");
		entidade.setWidth(300);
		entidade.setBeanName(Entidade.class.getName());

		dataEmissaoInicial.setName("dataEmissaoInicial");
		dataEmissaoInicial.setLabel("Data de Emissão - Início");

		dataEmissaoFinal.setName("dataEmissaoFinal");
		dataEmissaoFinal.setLabel("Data de Emissão - Final");
	}

	public FieldLookup getPlanoConta() {
		return planoConta;
	}

	public void setPlanoConta(FieldLookup planoConta) {
		this.planoConta = planoConta;
	}

	public FieldSimpleDate getDataEmissaoInicial() {
		return dataEmissaoInicial;
	}

	public void setDataEmissaoInicial(FieldSimpleDate dataEmissaoInicial) {
		this.dataEmissaoInicial = dataEmissaoInicial;
	}

	public FieldSimpleDate getDataEmissaoFinal() {
		return dataEmissaoFinal;
	}

	public void setDataEmissaoFinal(FieldSimpleDate dataEmissaoFinal) {
		this.dataEmissaoFinal = dataEmissaoFinal;
	}

	public FieldInteger getContabilId() {
		return contabilId;
	}

	public void setContabilId(FieldInteger contabilId) {
		this.contabilId = contabilId;
	}

	public FieldLookup getEntidade() {
		return entidade;
	}

	public void setEntidade(FieldLookup entidade) {
		this.entidade = entidade;
	}
};

class GridPlanoContaSelecionado extends VariableGrid{

	private FieldString planoConta;

	public GridPlanoContaSelecionado(Window window) throws Exception {
		super(window, "interactionCadastro");
		setTitle("Plano de Contas Selecionado");

		planoConta = new FieldString(this);
	}

	@Override
	public void defineGrid() throws Exception {
		planoConta.setName("planoConta");
		planoConta.setReadOnly(true);
	}

	public FieldString getPlanoConta() {
		return planoConta;
	}

	public void setPlanoConta(String planoConta) throws Exception {
		this.planoConta.setValue(planoConta);
	}
};
