package br.com.dyad.backoffice.navigation.operacao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.auxiliares.PedidoAux;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraBaixaPedido;
import br.com.dyad.backoffice.navigation.relatorio.EspelhoBaixaPedidoHTML;
import br.com.dyad.backoffice.operacao.consulta.ConsultaPedido;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.entity.AbstractEntity;
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
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class BaixaPedidoWindow extends Window {
	
	private AppTransaction appTransaction;

	private GridVariaveisBaixaPedido gridVariaveis;
	private GridEdicaoBaixaPedido gridEdicaoBaixaPedido;
	private GridExibePedidosPendentesLocalizados gridExibePedidosPendentesLocalizados;
	private DataList dataListExibePedidosPendentes;
	
	private CabecalhoBaixaPedido cabecalhoEditando;

	public RegraBaixaPedido regraBaixaPedido;
	
	public BaixaPedidoWindow (HttpSession httpSession) {
		super(httpSession);
	}
	
	/**
	 * Actions
	 * 
	 * O termo "Localizar" foi escolhido por ser um "padrão".
	 * Verificamos que o MSOffice, o Mozilla Firefox e outros
	 * softwares de referência, usam este termo. 
	 */
	public Action actionLocalizarPendencia = new Action(this, "Localizar Pendências"){		
		@Override
		public void onClick() throws Exception {
			BaixaPedidoWindow process = (BaixaPedidoWindow)getParent();
			process.localizarPedidosPendentes();
			
			process.setNextInteraction(process.exibePedidosPendentes);
		}
	};
		
	public Action actionLocalizarBaixa = new Action(this, "Localizar Baixas"){		
		@Override
		public void onClick() throws Exception {
			BaixaPedidoWindow process = (BaixaPedidoWindow)getParent();
			
			process.localizarBaixasPedido();
			process.edicaoBaixaPedido.setDefined(false);
			
			process.setNextInteraction(process.edicaoBaixaPedido);
		}
	};
		
	/**
	 * Actions da Interaction ExibePedidosPendentes
	 */
	public Action actionIncluirPedidosPendentes = new Action(this, "Incluir Pendências"){
		@Override
		public void onClick() throws Exception {
			BaixaPedidoWindow process = (BaixaPedidoWindow)getParent();
			
			process.incluirPedidosPendentes();
			
			process.edicaoBaixaPedido.setDefined(false);
			
			process.setNextInteraction(process.edicaoBaixaPedido);
		};
	};

	public Action actionVoltarVariaveis = new Action(this, "Voltar às Variáveis"){
		@Override
		public void onClick() throws Exception {
			BaixaPedidoWindow process = (BaixaPedidoWindow)this.getParent();
			
			process.setNextInteraction(process.variaveis);
		}
	};
	
	public Action actionVoltarEdicaoBaixa = new Action(this, "Voltar à Edição de Baixa"){
		@Override
		public void onClick() throws Exception {
			BaixaPedidoWindow process = (BaixaPedidoWindow)getParent();			
			
			process.edicaoBaixaPedido.setDefined(false);

			process.setNextInteraction(process.edicaoBaixaPedido);
		}
	};
	
	/**
	 * Actions da Interaction Edição de Baixa de Pedido
	 */
	public Action actionCalcular = new Action(this, "Calcular"){		
		@Override
		public void onClick() throws Exception {
			BaixaPedidoWindow process = (BaixaPedidoWindow)this.getParent();
			
			CabecalhoBaixaPedido cabecalhoBaixaPedido = (CabecalhoBaixaPedido) process.gridEdicaoBaixaPedido.getCurrentEntity();
			process.regraBaixaPedido.calculaOperacao(cabecalhoBaixaPedido);
			process.regraBaixaPedido.criaTitulosOperacao(cabecalhoBaixaPedido);
			
			if (process.gridEdicaoBaixaPedido.fieldTitulosBaixaPedido.getDataList().getList().size() > 0) {
			
				DataList dataListTitulos = DataListFactory.newDataList(appTransaction);
				
				dataListTitulos.setList(process.gridEdicaoBaixaPedido.fieldTitulosBaixaPedido.getDataList().getList());
				
				process.gridEdicaoBaixaPedido.getDetails().get(1).setDataList(dataListTitulos);
			
			}
//			process.gridEdicaoBaixaPedido.fieldTitulosBaixaPedido.getDetailGrid().setCurrentEntity(cabecalhoBaixaPedido.getTitulos().get(0));
//			process.gridEdicaoBaixaPedido.fieldTitulosBaixaPedido.getDataList().setList(process.gridEdicaoBaixaPedido.fieldTitulosBaixaPedido.getDataList().getList());
		}
	};
		
	public Action actionGravar = new Action(this, "Gravar"){
		@Override
		public void onClick() throws Exception {
			confirm("confirmGravar", "Deseja gravar a operação atual?");
		};
	};
	
	public Action actionLocalizarOutrasPendencias = new Action(this, "Localizar Outras Pendências"){
		@Override
		public void onClick() throws Exception {
			BaixaPedidoWindow process = (BaixaPedidoWindow)getParent();
			process.cabecalhoEditando = (CabecalhoBaixaPedido)process.gridEdicaoBaixaPedido.getCurrentEntity();
			process.variaveis.setDefined(false);
			
			process.setNextInteraction(process.variaveis);
		};
	};
	
	public Action actionAbandonarBaixaPedido = new Action(this, "Abandonar Baixa Atual"){
		@Override
		public void onClick() throws Exception {
			confirm("confirmAbandonarBaixa", "Deseja abandonar a baixa atual?");
		};
	};
	
	public Action actionImprimir = new Action(this, "Imprimir") {
		@Override
		public void onClick() throws Exception {
			BaixaPedidoWindow process = (BaixaPedidoWindow) getParent();
			
			CabecalhoBaixaPedido cabecalhoBaixaPedido = (CabecalhoBaixaPedido)process.gridEdicaoBaixaPedido.getCurrentEntity();
			
			if (cabecalhoBaixaPedido.getEstabelecimento() == null || cabecalhoBaixaPedido.getEntidade() == null || cabecalhoBaixaPedido.getEmissao() == null) {
				throw new AppException("Preencha os campos obrigatórios do agrupamento!");
			}
			
			if (cabecalhoBaixaPedido.getTitulos() == null || cabecalhoBaixaPedido.getTitulos().isEmpty()) {
				throw new AppException("Insira os títulos da baixa de pedido!");
			}
			
			process.setNextInteraction(process.espelhoPedido);
		};
	};

	/*
	 * Métodos relacionadas às chamadas que devem ser feitas através de "Confirm´s" 
	 */
	public void confirmGravar(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			this.regraBaixaPedido.grava();
			this.regraBaixaPedido.fecha();
			this.variaveis.setDefined(false);
			
			this.setNextInteraction(this.variaveis);
		}
	}
	
	public void confirmAbandonarBaixa(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			this.regraBaixaPedido.fecha();
			this.variaveis.setDefined(false);
			
			this.setNextInteraction(this.variaveis);
		}
	}
	
	/**
	 * Interação que é exibida quando o usuário entra no processo.
	 */
	public Interaction variaveis = new Interaction(this, "variaveis"){
		BaixaPedidoWindow process = (BaixaPedidoWindow)this.getParent();

		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions( process.actionLocalizarPendencia );
			this.enableAndShowActions( process.actionLocalizarBaixa );
			
			if (process.cabecalhoEditando != null) {
				this.enableAndShowActions( process.actionVoltarEdicaoBaixa);
			}
			
			add(gridVariaveis);
		}
	};
	
	/**
	 * Interação usada para editar dados do pedido.
	 * Ela é usada tanto para Criação de novos pedidos como para
	 * edição de pedidos já existentes.
	 */
	public Interaction exibePedidosPendentes = new Interaction(this, "exibePedidosPendentes"){
		BaixaPedidoWindow process = (BaixaPedidoWindow)this.getParent();
		
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions( process.actionIncluirPedidosPendentes );
			this.enableAndShowActions( process.actionVoltarVariaveis );
			
			if (process.cabecalhoEditando != null) {
				this.enableAndShowActions( process.actionVoltarEdicaoBaixa );
			}
			
			add(gridExibePedidosPendentesLocalizados);
		}
	};

	/**
	 * Interação usada para editar dados do pedido.
	 * Ela é usada tanto para Criação de novos pedidos como para
	 * edição de pedidos já existentes.
	 */
	public Interaction edicaoBaixaPedido = new Interaction(this, "edicaoBaixaPedido"){
		BaixaPedidoWindow process = (BaixaPedidoWindow)this.getParent();
		
		@Override
		public void defineInteraction() throws Exception {
			process.cabecalhoEditando = null;
			
			this.enableAndShowActions( process.actionCalcular );
			this.enableAndShowActions( process.actionGravar );
			this.enableAndShowActions( process.actionLocalizarOutrasPendencias );
			this.enableAndShowActions( process.actionAbandonarBaixaPedido );
			this.enableAndShowActions( process.actionImprimir );
			
			add(gridEdicaoBaixaPedido);
		}
	};
	
	/**
	 * Interação usada para exibir o espelho do pedido
	 */
	public Interaction espelhoPedido = new Interaction(this, "espelhoBaixaPedido") {
		@Override
		public void defineInteraction() throws Exception {
			EspelhoBaixaPedidoHTML espelhoBaixaPedido = new EspelhoBaixaPedidoHTML();
			BaixaPedidoWindow process = (BaixaPedidoWindow) getParent();

			CabecalhoBaixaPedido cabecalhoBaixaPedido = (CabecalhoBaixaPedido) process.gridEdicaoBaixaPedido.getCurrentEntity();
			espelhoBaixaPedido.setCabecalhoBaixaPedido(cabecalhoBaixaPedido);
			espelhoBaixaPedido.setItensBaixaPedido((List<ItemBaixaPedido>) cabecalhoBaixaPedido.getItensBaixaPedido());
			espelhoBaixaPedido.setTitulos((List<Titulo>) cabecalhoBaixaPedido.getTitulos());

			Label html = new Label("");
			html.setText(espelhoBaixaPedido.preparaHtml());
			setTitle("EspelhoPedido");
			add(html);
		}
	};

	@Override
	public void defineWindow() throws Exception {
		this.appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDatabase()), this.getDatabase());
		
		this.regraBaixaPedido = new RegraBaixaPedido(this.appTransaction);
		this.regraBaixaPedido.preparaRegra();
		
		this.gridVariaveis = new GridVariaveisBaixaPedido(this);
		this.gridExibePedidosPendentesLocalizados = new GridExibePedidosPendentesLocalizados(this);
		this.gridEdicaoBaixaPedido = new GridEdicaoBaixaPedido(this, this.regraBaixaPedido);
		
		this.dataListExibePedidosPendentes = DataListFactory.newDataList(this.appTransaction);
		
		this.gridExibePedidosPendentesLocalizados.setDataList(this.dataListExibePedidosPendentes);
		this.gridEdicaoBaixaPedido.setDataList(this.regraBaixaPedido.getDataListCabecalhos());
	}
	
	public void localizarPedidosPendentes() throws Exception{
		
		if (this.gridVariaveis.fldIdOperacao.getValue() == null &&
			this.gridVariaveis.fldIdItem.getValue() == null &&
			this.gridVariaveis.fldNumero.getValue() == null &&
			this.gridVariaveis.fldEntidade.getValue() == null &&
			this.gridVariaveis.fldEstabelecimento.getValue() == null &&
			this.gridVariaveis.fldRecurso.getValue() == null &&
			this.gridVariaveis.fldNucleo.getValue() == null &&
			this.gridVariaveis.fldEmissaoInicial.getValue() == null &&
			this.gridVariaveis.fldEmissaoFinal.getValue() == null) {	

			throw new AppException("Não foi definido nenhum filtro para a consulta. Para realizar a consulta informe pelo menos um parâmetro.");
		} else{
			
			if( this.gridVariaveis.fldEmissaoInicial.getValue() != null &&
				this.gridVariaveis.fldEmissaoFinal.getValue() != null &&
				this.gridVariaveis.fldEmissaoInicial.getValue().after( this.gridVariaveis.fldEmissaoFinal.getValue())){
				SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");				
				throw new AppException("A data de Emissão Final '" + dataFormat.format(this.gridVariaveis.fldEmissaoFinal.getValue()) + "' deve ser maior ou igual a Data de Emissão Inicial '" + dataFormat.format(this.gridVariaveis.fldEmissaoInicial.getValue()) + "'.");				
			}
		
			this.dataListExibePedidosPendentes.empty();
						
			Long idOperacao = (Long)this.gridVariaveis.fldIdOperacao.getValue();
			Long idItem = (Long)this.gridVariaveis.fldIdItem.getValue();
			String numero = this.gridVariaveis.fldNumero.getValue();
			Long entidadeId = this.gridVariaveis.fldEntidade.getValueId();
			Long estabelecimentoId = this.gridVariaveis.fldEstabelecimento.getValueId();
			Long recursoId = this.gridVariaveis.fldRecurso.getValueId();
			Long nucleoId = this.gridVariaveis.fldNucleo.getValueId();
			Date emissaoInicial = this.gridVariaveis.fldEmissaoInicial.getValue(); 
			Date emissaoFinal = this.gridVariaveis.fldEmissaoFinal.getValue(); 
			
			//*************************************************************************			
			//*************************************************************************			
			//*************************************************************************			
			//*************************************************************************			
			//*************************************************************************			
			//*************************************************************************			
			String select = "from " + ItemPedido.class.getName() + " pedido ";
			ArrayList<String> where = new ArrayList<String>();
			ArrayList<Object> params = new ArrayList<Object>(0);
			
			if (idOperacao != null) {
				where.add(" cabecalho.id = ? ");
				params.add(idOperacao);
			}
			
			if (idItem != null) {
				where.add(" id = ? ");
				params.add(idItem);
			}
			
			if (numero != null) {
				where.add(" numero = ? ");
				params.add(numero);
			}

			if (entidadeId != null) {
				where.add(" entidade.id = ? ");
				params.add(entidadeId);
			}

			if (estabelecimentoId != null) {
				where.add(" estabelecimento.id = ? ");
				params.add(estabelecimentoId);
			}

			if (recursoId != null) {
				where.add(" recurso.id = ? ");
				params.add(recursoId);
			}

			if (nucleoId != null) {
				where.add(" nucleo.id = ? ");
				params.add(nucleoId);
			}

			if (emissaoInicial != null) {
				where.add(" emissao >= ? ");
				params.add(emissaoInicial);
			}

			if (emissaoFinal != null) {
				where.add(" emissao <= ? ");
				params.add(emissaoFinal);
			}
			
			where.add(" aprovacao IS NOT NULL ");

			where.add( 
					"(  " +
					"   Not Exists (" +
					"      From " +
					"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido baixa " +
					"      Where " +
					"         baixa.itemPedidoBaixado = pedido " +
					"   ) OR ( " +
					"      Select " +
					"         sum(baixa2.quantidade) " +
					"      From " +
					"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido baixa2 " +
					"      Where " +
					"         baixa2.itemPedidoBaixado = pedido " +
					"   ) < pedido.quantidade" +
					") ");
			
			String query = select + " where " + StringUtils.join(where, " and ");
			
			DataList dataListPedidosPendentes = DataListFactory.newDataList(this.appTransaction);
			this.appTransaction.clear();
			List<ItemPedido> itensPedido = PersistenceUtil.executeHql((Session)this.appTransaction.getSession(), query, params);
			dataListPedidosPendentes.setList(itensPedido);

			if (!itensPedido.isEmpty()) {
	
				for (ItemPedido itemPedido : itensPedido) {
					PedidoAux pedidoAux = new PedidoAux();
					pedidoAux.setId(itemPedido.getId());
					pedidoAux.setOperacaoId(itemPedido.getCabecalho().getId());
					pedidoAux.setEmissao(itemPedido.getEmissao());
					pedidoAux.setEntidade(itemPedido.getEntidade());
					pedidoAux.setEstabelecimento(itemPedido.getEstabelecimento());
					pedidoAux.setRecurso(itemPedido.getRecurso());
					pedidoAux.setQuantidade(itemPedido.getQuantidade());
					pedidoAux.setQuantidadeBaixada(itemPedido.getQuantidadeBaixada());
					pedidoAux.setSaldo(pedidoAux.getQuantidade() - pedidoAux.getQuantidadeBaixada());
	
					this.dataListExibePedidosPendentes.add(pedidoAux);				
				}
				this.gridExibePedidosPendentesLocalizados.setViewMode(DataGrid.VIEW_MODE_TABLEVIEW);
				this.gridExibePedidosPendentesLocalizados.setIndexFields("operacaoId;id");
				this.gridExibePedidosPendentesLocalizados.firstEntity();
			} else {
				throw new AppException("Não foram encontradas Pendências para as restrições informadas."); 
			}
		}
	}

	public void localizarBaixasPedido() throws Exception {	
		Long idOperacao = null;
		Long idItem = null;
		String numero = null;
		Long entidadeId = null;
		Long estabelecimentoId = null;
		Date emissaoInicial = null;
		Date emissaoFinal = null;

		if (this.gridVariaveis.fldIdOperacao.getValue() != null) {
			idOperacao = (Long)this.gridVariaveis.fldIdOperacao.getValue();
		}
		
		if (this.gridVariaveis.fldIdItem.getValue() != null) {
			idItem = (Long)this.gridVariaveis.fldIdItem.getValue();
		}

		if (this.gridVariaveis.fldNumero.getValue() != null) {
			numero = this.gridVariaveis.fldNumero.getValue();
		}

		if (this.gridVariaveis.fldEntidade.getValue() != null) {
			entidadeId = this.gridVariaveis.fldEntidade.getValueId();
		}

		if (this.gridVariaveis.fldEstabelecimento.getValue() != null) {
			estabelecimentoId = this.gridVariaveis.fldEstabelecimento.getValueId();
		}

		emissaoInicial = this.gridVariaveis.fldEmissaoInicial.getValue();
		emissaoFinal = this.gridVariaveis.fldEmissaoFinal.getValue();

		this.regraBaixaPedido.abre(idOperacao, idItem, numero, entidadeId, estabelecimentoId, emissaoInicial, emissaoFinal);
		if (this.regraBaixaPedido.getDataListCabecalhos().isEmpty()) {
			throw new AppException("Não foram encontrados pedidos com as restrições informadas.");
		}
	}	

	@SuppressWarnings("unchecked")
	public void incluirPedidosPendentes() throws Exception{
		ArrayList<AbstractEntity> pedidosAux = this.gridExibePedidosPendentesLocalizados.getSelectedRecords();
		
		if (pedidosAux.isEmpty()){
			throw new AppException("É necessário que se escolha pelo menos um Pedido Pendente para poder gerar um Pedido de Baixa.");
		}
		
		CabecalhoBaixaPedido cabecalhoBaixaPedido = null;
		
		if (this.cabecalhoEditando != null) {
			cabecalhoBaixaPedido = this.cabecalhoEditando;
		} else {
			cabecalhoBaixaPedido = (CabecalhoBaixaPedido)this.regraBaixaPedido.novoCabecalho();
			cabecalhoBaixaPedido.setEmissao(new Date());
		}
		
		ArrayList<Long> pedidosIds = new ArrayList();
		
		for (AppEntity pedidoAux : pedidosAux) {
			pedidosIds.add(pedidoAux.getId());			
		}
		
		//TODO verificar se transforma o consultaPedido em global???
		ConsultaPedido consultaPedido = new ConsultaPedido(this.appTransaction);
		consultaPedido.setPedidosIds(pedidosIds);
		ArrayList<ItemPedido> itemPedidos = consultaPedido.pegaPedidosAsList();
		
		for (ItemPedido itemPedido : itemPedidos) {
			ItemBaixaPedido itemBaixaPedido = (ItemBaixaPedido) this.regraBaixaPedido.novoItem(cabecalhoBaixaPedido);
			itemBaixaPedido.setItemPedidoBaixado(itemPedido);
			itemBaixaPedido.setTipoPedido(itemPedido.getTipoPedido());
			itemBaixaPedido.setEntidade(itemPedido.getEntidade());
			itemBaixaPedido.setNucleo(itemPedido.getNucleo());
			itemBaixaPedido.setRecurso(itemPedido.getRecurso());
			itemBaixaPedido.setUnitario(itemPedido.getUnitario());
			
			ArrayList pedidosAuxPedido = new ArrayList(this.dataListExibePedidosPendentes.getRange("id", itemPedido.getId()).values());
			itemBaixaPedido.setQuantidade( ((PedidoAux)pedidosAuxPedido.get(0)).getSaldo().longValue() );
		}
		
		//Preenchendo campos do Cabecalho basedo nos itens
		ItemPedido itemPedido = itemPedidos.get(0);
		if (cabecalhoBaixaPedido.getEntidade() == null) {
			cabecalhoBaixaPedido.setEntidade(itemPedido.getEntidade());
		}
		
		this.gridEdicaoBaixaPedido.firstEntity();

		System.out.println("QUANTIDADE DE BAIXAS=>" + cabecalhoBaixaPedido.getItensBaixaPedido().size());
	}
}

/**
 * 
 * @author Dyad
 *
 */
class GridVariaveisBaixaPedido extends VariableGrid {

	public FieldInteger fldIdOperacao = new FieldInteger(this);
	public FieldInteger fldIdItem = new FieldInteger(this);	
	public FieldString fldNumero = new FieldString(this);
	public FieldLookup fldEntidade = new FieldLookup(this);
	public FieldLookup fldEstabelecimento = new FieldLookup(this);
	public FieldLookup fldNucleo = new FieldLookup(this);
	public FieldLookup fldRecurso = new FieldLookup(this);	
	public FieldSimpleDate fldEmissaoInicial = new FieldSimpleDate(this);	
	public FieldSimpleDate fldEmissaoFinal = new FieldSimpleDate(this);
	
	public GridVariaveisBaixaPedido(Window window) throws Exception {
		super(window, "variaveis");
	}

	@Override
	public void defineGrid() throws Exception {
		setTitle("Filtro para Localização de Pendências ou Baixas de Pedido");
		
		int count = 0;
		
		fldIdOperacao.setLabel("Operação");
		fldIdOperacao.setName("operacao");
		fldIdOperacao.setWidth(150);
		fldIdOperacao.setOrder(count++);
		
		fldIdItem.setLabel("Item");
		fldIdItem.setName("item");
		fldIdItem.setWidth(150);
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
		
		fldNucleo.setBeanName(Nucleo.class.getName());
		fldNucleo.setLabel("Núcleo");
		fldNucleo.setName("nucleo");
		fldNucleo.setOrder(count++);
		
		fldRecurso.setBeanName(Recurso.class.getName());
		fldRecurso.setName("recurso");
		fldRecurso.setOrder(count++);
		
		fldEmissaoInicial.setLabel("Emissão Inicial");
		fldEmissaoInicial.setName("emissaoInicial");
		fldEmissaoInicial.setWidth(150);
		fldEmissaoInicial.setOrder(count++);

		fldEmissaoFinal.setLabel("Emissão Final");
		fldEmissaoFinal.setName("emissaoFinal");
		fldEmissaoFinal.setWidth(150);
		fldEmissaoFinal.setOrder(count++);
	}
};