package br.com.dyad.backoffice.navigation.operacao;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraPedido;
import br.com.dyad.backoffice.navigation.relatorio.EspelhoPedidoHTML;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.Label;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldInteger;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.field.FieldString;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

/**
 * @author Franklin Kaswiner
 *
 */
public class PedidoWindow extends Window {

	private AppTransaction appTransaction;

	private GridVariaveisPedido gridVariaveisPedido;
	private GridEdicaoPedido gridEdicaoPedido;

	public RegraPedido regraPedido;

	public PedidoWindow(HttpSession httpSession) {
		super(httpSession);
	}

	/**
	 * Actions
	 * 
	 * O termo "Localizar" foi escolhido por ser um "padrão". Verificamos que o
	 * MSOffice, o Mozilla Firefox e outros softwares de referência, usam este
	 * termo.
	 */
	public Action actionLocalizar = new Action(this, "Localizar Pedidos") {
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow) getParent();

			process.localizarPedidos();
			edicaoPedido.setDefined(false);
			process.setNextInteraction(edicaoPedido);
		}
	};

	public Action actionCalcular = new Action(this, "Calcular") {
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow) getParent();

			CabecalhoPedido cabecalho = (CabecalhoPedido) process.gridEdicaoPedido.getCurrentEntity();
			process.regraPedido.calculaOperacao(cabecalho);
		}
	};

	public Action actionNova = new Action(this, "Novo") {
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow) getParent();
			
			gridEdicaoPedido.dispatchInsert();
			
			process.setNextInteraction(edicaoPedido);
		}
	};

	public Action actionAprovar = new Action(this, "Aprovar") {
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow) getParent();
			CabecalhoPedido cabecalho = (CabecalhoPedido) process.gridEdicaoPedido.getCurrentEntity();
			process.regraPedido.aprova(cabecalho.getId());
		}
	};

	public Action actionDesaprovar = new Action(this, "Desaprovar") {
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow) getParent();
			CabecalhoPedido cabecalho = (CabecalhoPedido) process.gridEdicaoPedido.getCurrentEntity();
			process.regraPedido.desaprova(cabecalho.getId());
		}
	};

	public Action actionCancelar = new Action(this, "Cancelar") {
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow) getParent();
			CabecalhoPedido cabecalhoOperacao = (CabecalhoPedido) process.gridEdicaoPedido.getCurrentEntity();
			process.regraPedido.cancela(cabecalhoOperacao.getId());
		}
	};

	public Action actionDescancelar = new Action(this, "Descancelar") {
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow) getParent();

			CabecalhoPedido cabecalhoPedido = (CabecalhoPedido) process.gridEdicaoPedido.getCurrentEntity();
			process.regraPedido.descancela(cabecalhoPedido.getId());
		}
	};

	public Action actionGravar = new Action(this, "Gravar") {
		@Override
		public void onClick() throws Exception {
			confirm("confirmGravar", "Deseja gravar as operações?");
		};
	};

	public Action actionFechar = new Action(this, "Fechar") {
		@Override
		public void onClick() throws Exception {
			confirm("confirmFechar", "Deseja fechar as operações?");
		};
	};

	public Action actionImprimir = new Action(this, "Imprimir") {
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow) getParent();
			
			CabecalhoPedido cabecalhoPedido = (CabecalhoPedido)process.gridEdicaoPedido.getCurrentEntity();
			
			if (cabecalhoPedido.getEstabelecimento() == null || cabecalhoPedido.getEntidade() == null || cabecalhoPedido.getEmissao() == null) {
				throw new AppException("Preencha os campos obrigatórios do agrupamento!");
			}
			
			process.setNextInteraction(process.espelhoPedido);
		};
	};

	/*
	 * Métodos relacionadas às chamadas que devem ser feitas através de "Confirm´s" 
	 */
	public void confirmGravar(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			this.regraPedido.grava();
			this.regraPedido.fecha();
			this.variaveis.setDefined(false);
			
			this.setNextInteraction(this.variaveis);
		}
	}
	
	public void confirmFechar(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			this.regraPedido.fecha();
			this.variaveis.setDefined(false);
			
			this.setNextInteraction(this.variaveis);
		}
	}
	
	/**
	 * Interação que é exibida quando o usuário entra no processo.
	 */
	Interaction variaveis = new Interaction(this, "variaveis") {
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions(actionLocalizar);
			this.enableAndShowActions(actionNova);

			add(gridVariaveisPedido);
		}
	};

	/**
	 * Interação usada para editar dados do pedido. Ela é usada tanto para
	 * Criação de novos pedidos como para edição de pedidos já existentes.
	 */
	public Interaction edicaoPedido = new Interaction(this, "edicaoPedido") {
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions(actionCalcular);
			this.enableAndShowActions(actionAprovar);
			this.enableAndShowActions(actionDesaprovar);
			this.enableAndShowActions(actionCancelar);
			this.enableAndShowActions(actionDescancelar);
			this.enableAndShowActions(actionGravar);
			this.enableAndShowActions(actionFechar);
			this.enableAndShowActions(actionImprimir);
			gridEdicaoPedido.first();
			add(gridEdicaoPedido);
		}
	};

	/**
	 * Interação usada para exibir o espelho do pedido
	 */
	public Interaction espelhoPedido = new Interaction(this, "espelhoPedido") {
		@Override
		public void defineInteraction() throws Exception {
			EspelhoPedidoHTML espelhoPedido = new EspelhoPedidoHTML();
			PedidoWindow process = (PedidoWindow) getParent();

			CabecalhoPedido cabecalhoPedido = (CabecalhoPedido) process.gridEdicaoPedido.getCurrentEntity();
			espelhoPedido.setCabecalhoPedido(cabecalhoPedido);
			espelhoPedido.setItensPedido((List<ItemPedido>) cabecalhoPedido.getItensPedido());

			Label html = new Label("");
			html.setText(espelhoPedido.preparaHtml());
			setTitle("EspelhoPedido");
			add(html);
		}
	};

	@Override
	public void defineWindow() throws Exception {
		this.appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDatabase()), this.getDatabase());

		this.regraPedido = new RegraPedido(this.appTransaction);
		this.regraPedido.preparaRegra();

		this.gridVariaveisPedido = new GridVariaveisPedido(this);
		this.gridEdicaoPedido = new GridEdicaoPedido(this, this.regraPedido);

		this.gridEdicaoPedido.setDataList(this.regraPedido.getDataListCabecalhos());
		
		this.actionFechar.setValidateLastInteraction(false);
	}

	public void localizarPedidos() throws Exception {
		Long idOperacao = null;
		Long idItem = null;
		String numero = null;
		Long entidadeId = null;
		Long estabelecimentoId = null;
		Long recursoId = null;
		Long nucleoId = null;
		Date emissaoInicial = null;
		Date emissaoFinal = null;

		if (this.gridVariaveisPedido.fldIdOperacao.getValue() != null) {
			idOperacao = (Long)this.gridVariaveisPedido.fldIdOperacao.getValue();
		}
		
		if (this.gridVariaveisPedido.fldIdItem.getValue() != null) {
			idItem = (Long)this.gridVariaveisPedido.fldIdItem.getValue();
		}

		if (this.gridVariaveisPedido.fldNumero.getValue() != null) {
			numero = this.gridVariaveisPedido.fldNumero.getValue();
		}

		if (this.gridVariaveisPedido.fldEntidade.getValue() != null) {
			entidadeId = this.gridVariaveisPedido.fldEntidade.getValueId();
		}

		if (this.gridVariaveisPedido.fldEstabelecimento.getValue() != null) {
			estabelecimentoId = this.gridVariaveisPedido.fldEstabelecimento.getValueId();
		}
		
		if (this.gridVariaveisPedido.fldRecurso.getValue() != null) {
			recursoId = this.gridVariaveisPedido.fldRecurso.getValueId();
		}
		
		if (this.gridVariaveisPedido.fldNucleo.getValue() != null) {
			nucleoId = this.gridVariaveisPedido.fldNucleo.getValueId();
		}

		emissaoInicial = this.gridVariaveisPedido.fldEmissaoInicial.getValue();
		emissaoFinal = this.gridVariaveisPedido.fldEmissaoFinal.getValue();

		this.regraPedido.abre(idOperacao, idItem, numero, entidadeId, estabelecimentoId, recursoId, nucleoId, emissaoInicial, emissaoFinal);
		if (this.regraPedido.getDataListCabecalhos().isEmpty()) {
			throw new AppException("Não foram encontrados pedidos com as restrições informadas.");
		}

	}
}

/**
 * 
 * @author Dyad
 * 
 */
class GridVariaveisPedido extends VariableGrid {

	public FieldInteger fldIdOperacao = new FieldInteger(this);
	public FieldInteger fldIdItem = new FieldInteger(this);
	public FieldString fldNumero = new FieldString(this);
	public FieldLookup fldEntidade = new FieldLookup(this);
	public FieldLookup fldEstabelecimento = new FieldLookup(this);
	public FieldLookup fldRecurso = new FieldLookup(this);
	public FieldLookup fldNucleo = new FieldLookup(this);
	public FieldSimpleDate fldEmissaoInicial = new FieldSimpleDate(this);
	public FieldSimpleDate fldEmissaoFinal = new FieldSimpleDate(this);

	public GridVariaveisPedido(Window window) throws Exception {
		super(window, "variaveis");
	}

	@Override
	public void defineGrid() throws Exception {
		setTitle("Variáveis");

		int count = 0;

		fldIdOperacao.setLabel("Operação");
		fldIdOperacao.setName("idOperacao");
		fldIdOperacao.setOrder(count++);
		
		fldIdItem.setLabel("Item");
		fldIdItem.setName("item");
		fldIdItem.setOrder(count++);
		
		fldNumero.setLabel("Número");
		fldNumero.setName("numero");
		fldNumero.setOrder(count++);

		fldEntidade.setBeanName(Entidade.class.getName());
		fldEntidade.setName("entidade");
		fldEntidade.setOrder(count++);

		fldEstabelecimento.setBeanName(Estabelecimento.class.getName());
		fldEstabelecimento.setName("estabelecimento");
		fldEstabelecimento.setOrder(count++);
		
		fldRecurso.setBeanName(Recurso.class.getName());
		fldRecurso.setName("recurso");
		fldRecurso.setOrder(count++);

		fldNucleo.setBeanName(Nucleo.class.getName());
		fldNucleo.setLabel("Núcleo");
		fldNucleo.setName("nucleo");
		fldNucleo.setOrder(count++);

		fldEmissaoInicial.setLabel("Emissão Inicial");
		fldEmissaoInicial.setName("emissaoInicial");
		fldEmissaoInicial.setOrder(count++);

		fldEmissaoFinal.setLabel("Emissão Final");
		fldEmissaoFinal.setName("emissaoFinal");
		fldEmissaoFinal.setOrder(count++);
	}
};