package br.com.dyad.backoffice.navigation.operacao;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.cadastro.TipoTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraTransferenciaDisponivel;
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
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class TransferenciaDisponivelWindow extends Window {

	private AppTransaction appTransaction;

	private GridVariaveisTransferenciaDisponivel gridVariaveis;
	private GridEdicaoTransferenciaDisponivel gridEdicaoTransferenciaDisponivel;

	public RegraTransferenciaDisponivel regraTransferenciaDisponivel;

	public TransferenciaDisponivelWindow (HttpSession httpSession) {
		super(httpSession);
	}
	
	public Action actionNova = new Action(this, "Nova"){
		public void onClick() throws Exception {
			TransferenciaDisponivelWindow process = (TransferenciaDisponivelWindow)getParent();
			
			gridEdicaoTransferenciaDisponivel.dispatchInsert();
			
			process.setNextInteraction(edicaoTransferenciaDisponivel);
		}
	};

	/**
	 * Actions
	 * 
	 * O termo "Localizar" foi escolhido por ser um "padrão".
	 * Verificamos que o MSOffice, o Mozilla Firefox e outros
	 * softwares de referência, usam este termo. 
	 */
	public Action actionLocalizar = new Action(this, "Localizar"){		
		@Override
		public void onClick() throws Exception {
			TransferenciaDisponivelWindow process = (TransferenciaDisponivelWindow)getParent();

			edicaoTransferenciaDisponivel.setDefined(false);
			
			regraTransferenciaDisponivel.getConsultaOperacaoTransferenciaDisponivel().setItemId((Long)gridVariaveis.getFldItem().getValue());
			regraTransferenciaDisponivel.getConsultaOperacaoTransferenciaDisponivel().setOperacaoId((Long)gridVariaveis.getFldOperacao().getValue());
			regraTransferenciaDisponivel.getConsultaOperacaoTransferenciaDisponivel().setEntidade((Entidade)gridVariaveis.getFldEntidade().getValue());
			regraTransferenciaDisponivel.getConsultaOperacaoTransferenciaDisponivel().setTipoTransferenciaDisponivel((TipoTransferenciaDisponivel)gridVariaveis.getFldTipoTransferencia().getValue());
			regraTransferenciaDisponivel.getConsultaOperacaoTransferenciaDisponivel().setEmissaoInicial((Date)gridVariaveis.getFldEmissaoInicial().getValue());
			regraTransferenciaDisponivel.getConsultaOperacaoTransferenciaDisponivel().setEmissaoFinal((Date)gridVariaveis.getFldEmissaoFinal().getValue());
			
			regraTransferenciaDisponivel.abre((Long)gridVariaveis.fldOperacao.getValue());
			if (regraTransferenciaDisponivel.getDataListCabecalhos().isEmpty()) {
				throw new AppException("Não foram encontradas Transferências de Disponíveis com as restrições informadas.");
			}
			
			process.setNextInteraction(edicaoTransferenciaDisponivel);
		}
	};

	public Action actionGravar = new Action(this, "Gravar"){
		public void onClick() throws Exception{
			confirm("confirmGravar", "Deseja gravar a operação atual?");			
		}
	};

	public void confirmGravar(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			regraTransferenciaDisponivel.grava();
			regraTransferenciaDisponivel.fecha();
			
			setNextInteraction(variaveis);
		}
	}
	
	public Action actionFechar = new Action(this, "Fechar"){
		public void onClick() throws Exception{
			confirm("confirmFechar", "Deseja fechar a operação?");
		}
	};

	public void confirmFechar(Object response) throws Exception{
		if (((String)response).equals("yes")) {
			regraTransferenciaDisponivel.fecha();
			
			setNextInteraction(variaveis);
		}
	}

	/**
	 * Interação que é exibida quando o usuário entra no processo.
	 */
	Interaction variaveis = new Interaction(this, "variaveis"){
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions( actionNova );
			this.enableAndShowActions( actionLocalizar );

			add(gridVariaveis);
		}

	};

	/**
	 * Interação usada para editar dados do pedido.
	 * Ela é usada tanto para Criação de novos pedidos como para
	 * edição de pedidos já existentes.
	 */
	public Interaction edicaoTransferenciaDisponivel = new Interaction(this, "edicaoTransferencia"){
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions( actionGravar );
			this.enableAndShowActions( actionFechar );

			gridEdicaoTransferenciaDisponivel.first();

			add(gridEdicaoTransferenciaDisponivel);
		}
	};

	@Override
	public void defineWindow() throws Exception {
		this.appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDatabase()), this.getDatabase());

		this.regraTransferenciaDisponivel = new RegraTransferenciaDisponivel(this.appTransaction);
//		this.regraTransferenciaDisponivel.preparaRegra();

		this.gridVariaveis = new GridVariaveisTransferenciaDisponivel(this);
		this.gridEdicaoTransferenciaDisponivel = new GridEdicaoTransferenciaDisponivel(this, this.regraTransferenciaDisponivel);

		this.gridEdicaoTransferenciaDisponivel.setDataList( this.regraTransferenciaDisponivel.getDataListCabecalhos() );

		((DetailGridItensTransferenciaDisponivel)gridEdicaoTransferenciaDisponivel.getItensTransferenciaDisponivel()).resetaDataList();
	}
}

/**
 * 
 * @author Dyad
 *
 */
class GridVariaveisTransferenciaDisponivel extends VariableGrid {

	public FieldInteger fldItem;
	public FieldInteger fldOperacao;
	public FieldLookup fldEntidade;
	public FieldLookup fldTipoTransferencia;
	public FieldSimpleDate fldEmissaoInicial;
	public FieldSimpleDate fldEmissaoFinal;

	public GridVariaveisTransferenciaDisponivel(Window window) throws Exception {
		super(window, "variaveis");
		setTitle("Filtro para Localização de Transferencias");
		
		fldOperacao = new FieldInteger(this);
		fldItem = new FieldInteger(this);
		fldEntidade = new FieldLookup(this);
		fldTipoTransferencia = new FieldLookup(this);
		
		fldEmissaoInicial = new FieldSimpleDate(this) {

			@Override
			public void onAfterChange() {
				super.onAfterChange();

				VariableGrid vGrid = (VariableGrid) this.getGrid();
				Date emissaoInicial = (Date) this.getValue();

				FieldSimpleDate fldEmissaoFinal = (FieldSimpleDate) vGrid.getFieldByName("emissaoFinal");

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
		
		fldEmissaoFinal = new FieldSimpleDate(this) {

			@Override
			public void onAfterChange() {
				super.onAfterChange();

				VariableGrid vGrid = (VariableGrid) this.getGrid();
				Date emissaoFinal = (Date) this.getValue();

				FieldSimpleDate fldEmissaoInicial = (FieldSimpleDate) vGrid.getFieldByName("emissaoInicial");

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

		fldItem.setName("item");
		fldItem.setLabel("Item");

		fldOperacao.setName("operacao");
		fldOperacao.setLabel("Operação");

		fldEntidade.setName("entidade");
		fldEntidade.setLabel("Entidade");
		fldEntidade.setBeanName(Entidade.class.getName());

		fldTipoTransferencia.setName("tipoTransferenciaDisponivel");
		fldTipoTransferencia.setLabel("Tipo de Transferência");
		fldTipoTransferencia.setBeanName(TipoTransferenciaDisponivel.class.getName());

		fldEmissaoInicial.setName("emissaoInicial");
		fldEmissaoInicial.setLabel("Data de Emissão - Início");

		fldEmissaoFinal.setName("emissaoFinal");
		fldEmissaoFinal.setLabel("Data de Emissão - Final");
	}

	public FieldInteger getFldItem() {
		return fldItem;
	}

	public void setFldItem(FieldInteger fldItem) {
		this.fldItem = fldItem;
	}

	public FieldInteger getFldOperacao() {
		return fldOperacao;
	}

	public void setFldOperacao(FieldInteger fldOperacao) {
		this.fldOperacao = fldOperacao;
	}

	public FieldLookup getFldEntidade() {
		return fldEntidade;
	}

	public void setFldEntidade(FieldLookup fldEntidade) {
		this.fldEntidade = fldEntidade;
	}

	public FieldSimpleDate getFldEmissaoInicial() {
		return fldEmissaoInicial;
	}

	public void setFldEmissaoInicial(FieldSimpleDate fldEmissaoInicial) {
		this.fldEmissaoInicial = fldEmissaoInicial;
	}

	public FieldSimpleDate getFldEmissaoFinal() {
		return fldEmissaoFinal;
	}

	public void setFldEmissaoFinal(FieldSimpleDate fldEmissaoFinal) {
		this.fldEmissaoFinal = fldEmissaoFinal;
	}

	public FieldLookup getFldTipoTransferencia() {
		return fldTipoTransferencia;
	}

	public void setFldTipoTransferencia(FieldLookup fldTipoTransferencia) {
		this.fldTipoTransferencia = fldTipoTransferencia;
	}
}