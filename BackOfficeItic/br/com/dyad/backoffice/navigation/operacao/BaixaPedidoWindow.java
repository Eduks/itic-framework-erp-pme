package br.com.dyad.backoffice.navigation.operacao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.auxiliares.PedidoAux;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.RegraBaixaPedido;
import br.com.dyad.backoffice.operacao.consulta.ConsultaBaixaPedido;
import br.com.dyad.backoffice.operacao.consulta.ConsultaPedido;
import br.com.dyad.businessinfrastructure.entidades.entidade.Disponivel;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.DyadEvents;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.WidgetListener;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldClassLookup;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.field.FieldString;
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
	public Action actionLocalizarPendencia = new Action(this, "Localizar Pendencias"){		
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
	public Action actionIncluirPedidosPendentes = new Action(this, "Incluir Pendencias"){
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
			process.regraBaixaPedido.getRegraTituloBaixaPedido().criaTitulos(cabecalhoBaixaPedido, 3);
		}
	};
		
	public Action actionGravar = new Action(this, "Gravar"){
		@Override
		public void onClick() throws Exception {
			BaixaPedidoWindow process = (BaixaPedidoWindow)getParent();			
			process.regraBaixaPedido.grava();
			process.regraBaixaPedido.fecha();
			process.variaveis.setDefined(false);
			
			process.setNextInteraction(process.variaveis);
		};
	};
	
	public Action actionLocalizarOutrasPendencias = new Action(this, "Buscar Outras Pendências"){
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
			BaixaPedidoWindow process = (BaixaPedidoWindow)getParent();			
			process.regraBaixaPedido.fecha();
			process.variaveis.setDefined(false);
			
			process.setNextInteraction(process.variaveis);
		};
	};
	
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
			
			add(gridEdicaoBaixaPedido);
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
		this.gridEdicaoBaixaPedido.setDataList(this.regraBaixaPedido.getCabecalhosOperacao());
	}
	
	public void localizarPedidosPendentes() throws Exception{	
		this.dataListExibePedidosPendentes.empty();

		ConsultaPedido consultaPedido = new ConsultaPedido(this.appTransaction);
		consultaPedido.setStatus(ConsultaPedido.STATUS_PENDENTE);
		consultaPedido.setAprovados(true);
		
		if (this.gridVariaveis.fldItem.getValue() != null) {
			consultaPedido.setPedidoId( new Long(this.gridVariaveis.fldItem.getValue()) );
		}
		if (this.gridVariaveis.fldOperacao.getValue() != null) {
			consultaPedido.setOperacaoId( new Long(this.gridVariaveis.fldOperacao.getValue()) );
		}
		if (this.gridVariaveis.fldNumero.getValue() != null) {
			consultaPedido.setNumero( this.gridVariaveis.fldNumero.getValue() );
		}
		
		consultaPedido.getParametroEntidade().setId( this.gridVariaveis.fldEntidade.getValueId() );

		if (this.gridVariaveis.fldClasseDeEntidade.getValue() != null) {
			consultaPedido.getParametroClassesEntidade().setStringId( this.gridVariaveis.fldClasseDeEntidade.getValueId().toString() );
		}

		consultaPedido.getParametroEstabelecimento().setId( this.gridVariaveis.fldEstabelecimento.getValueId() );
		consultaPedido.getParametroRecurso().setId( this.gridVariaveis.fldEstabelecimento.getValueId() );
		consultaPedido.getParametroNucleo().setId( this.gridVariaveis.fldNucleo.getValueId() );
		
		consultaPedido.setEmissaoInicial( this.gridVariaveis.fldEmissaoInicial.getValue() );
		consultaPedido.setEmissaoFinal( this.gridVariaveis.fldEmissaoFinal.getValue() );
		
		List<ItemPedido> itensPedido = consultaPedido.pegaPedidosAsList();
		
		if (!itensPedido.isEmpty()) {
			List<AppEntity> pedidosTemp = new ArrayList<AppEntity>();

			for (ItemPedido p : itensPedido) {
				pedidosTemp.add(p);
			}

			ConsultaBaixaPedido consultaBaixaPedidoAux = new ConsultaBaixaPedido(this.appTransaction);
			consultaBaixaPedidoAux.getParametroPedidoBaixado().setObjetos(pedidosTemp);
			DataList baixas = consultaBaixaPedidoAux.pegaBaixasPedido();

			for (ItemPedido p : itensPedido) {
				Collection<AppEntity> baixasItem = baixas.getRangeAsCollection("PedidoBaixado", p.getId());
				Long quantidadeBaixada = 0L;

				if (baixasItem != null) {					
					for (AppEntity baixaItem : baixasItem) {
						quantidadeBaixada += ((ItemBaixaPedido)baixaItem).getQuantidade();
					}
				}

				PedidoAux pedidoAux = new PedidoAux();
				pedidoAux.setId(p.getId());
				pedidoAux.setOperacaoId(new BigInteger(p.getOperacaoId().toString()));
				pedidoAux.setEmissao(p.getEmissao());
				pedidoAux.setEntidade(p.getEntidade());
				pedidoAux.setEstabelecimento(p.getEstabelecimento());
				pedidoAux.setRecurso(p.getRecurso());
				pedidoAux.setquantidade(new BigInteger(p.getQuantidade().toString()));
				pedidoAux.setquantidadebaixada(new BigInteger(quantidadeBaixada.toString()));
				pedidoAux.setsaldo(new BigInteger(new Long(p.getQuantidade() - quantidadeBaixada).toString()));

				this.dataListExibePedidosPendentes.add(pedidoAux);				
			}
			
			this.gridExibePedidosPendentesLocalizados.firstEntity();
		} else {
			throw new Exception("Não foram encontradas pendências para as variáveis informadas."); 
		}
	}

	public void localizarBaixasPedido() throws Exception {	
		ConsultaBaixaPedido consultaBaixasPedido = this.regraBaixaPedido.getConsultaBaixaPedido();

		if (this.gridVariaveis.fldItem.getValue() != null) {
			consultaBaixasPedido.setPedidoId( new Long(this.gridVariaveis.fldItem.getValue()) );
		}
		if (this.gridVariaveis.fldOperacao.getValue() != null) {
			consultaBaixasPedido.setOperacaoId( new Long(this.gridVariaveis.fldOperacao.getValue()) );
		}
		if (this.gridVariaveis.fldNumero.getValue() != null) {
			consultaBaixasPedido.setNumero( this.gridVariaveis.fldNumero.getValue() );
		}
		
		consultaBaixasPedido.getParametroEntidade().limpaValores();
		consultaBaixasPedido.getParametroEntidade().setId( this.gridVariaveis.fldEntidade.getValueId() );

		consultaBaixasPedido.getParametroClassesEntidade().limpaValores();
		consultaBaixasPedido.getParametroClassesEntidade().setId( this.gridVariaveis.fldClasseDeEntidade.getValueId() );

		consultaBaixasPedido.getParametroEstabelecimento().limpaValores();
		consultaBaixasPedido.getParametroEstabelecimento().setId( this.gridVariaveis.fldEstabelecimento.getValueId() );

		consultaBaixasPedido.getParametroRecurso().limpaValores();
		consultaBaixasPedido.getParametroRecurso().setId( this.gridVariaveis.fldEstabelecimento.getValueId() );

		consultaBaixasPedido.getParametroNucleo().limpaValores();
		consultaBaixasPedido.getParametroNucleo().setId( this.gridVariaveis.fldNucleo.getValueId() );
		
		consultaBaixasPedido.setEmissaoInicial( this.gridVariaveis.fldEmissaoInicial.getValue() );
		consultaBaixasPedido.setEmissaoFinal( this.gridVariaveis.fldEmissaoFinal.getValue() );
		
		this.regraBaixaPedido.abre();
		
		if (this.regraBaixaPedido.getItens().getList().isEmpty()) {
			throw new Exception("Não foram encontradas baixas para as variáveis informadas."); 
		}
	}	

	@SuppressWarnings("unchecked")
	public void incluirPedidosPendentes() throws Exception{
		CabecalhoBaixaPedido cabecalhoBaixaPedido = null;
		
		if (this.cabecalhoEditando != null) {
			cabecalhoBaixaPedido = this.cabecalhoEditando;
		} else {
			cabecalhoBaixaPedido = this.regraBaixaPedido.novoCabecalho();
			cabecalhoBaixaPedido.setEmissao(new Date());
		}
		
		List<AppEntity> pedidosAux = this.dataListExibePedidosPendentes.getList();
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
			itemBaixaPedido.setPedidoBaixado(itemPedido);
			itemBaixaPedido.setNucleo(itemPedido.getNucleo());
			itemBaixaPedido.setRecurso(itemPedido.getRecurso());
			itemBaixaPedido.setUnitario(itemPedido.getUnitario());
			itemBaixaPedido.setQuantidade(itemPedido.getQuantidade());
			itemBaixaPedido.setAprovacao(itemPedido.getAprovacao());
			itemBaixaPedido.setAprovador(itemPedido.getAprovador());
			
			ArrayList pedidosAuxPedido = new ArrayList(this.dataListExibePedidosPendentes.getRange("id", itemPedido.getId()).values());
			itemBaixaPedido.setQuantidade( ((PedidoAux)pedidosAuxPedido.get(0)).getSaldo().longValue() );
		}
		
		this.gridEdicaoBaixaPedido.firstEntity();

		System.out.println("QUANTIDADE DE BAIXAS=>" + this.regraBaixaPedido.getItens().getList().size());
	}
}

/**
 * 
 * @author Dyad
 *
 */
class GridVariaveisBaixaPedido extends VariableGrid {

	public FieldString fldItem = new FieldString(this);
	public FieldString fldOperacao = new FieldString(this);
	public FieldString fldNumero = new FieldString(this);
	public FieldClassLookup fldClasseDeEntidade = new FieldClassLookup(this);
	public FieldLookup fldEntidade = new FieldLookup(this);
	public FieldLookup fldEstabelecimento = new FieldLookup(this);
	public FieldLookup fldRecurso = new FieldLookup(this);
	public FieldLookup fldNucleo = new FieldLookup(this);
	public FieldSimpleDate fldEmissaoInicial = new FieldSimpleDate(this);
	public FieldSimpleDate fldEmissaoFinal = new FieldSimpleDate(this);
	
	public GridVariaveisBaixaPedido(Window window) throws Exception {
		super(window, "variaveis");
	}

	@Override
	public void defineGrid() throws Exception {
		setTitle("Variáveis");
		
		int count = 0;
		
		fldItem.setLabel("item");
		fldItem.setName("item");
		fldItem.setOrder(count++);
		
		fldOperacao.setLabel("Operação");
		fldOperacao.setName("operacao");
		fldOperacao.setOrder(count++);
		
		fldNumero.setLabel("Número");
		fldNumero.setName("numero");
		fldNumero.setOrder(count++);
		
		fldEntidade.setBeanName(Pessoa.class.getName());
		fldEntidade.setName("entidade");
		fldEntidade.setOrder(count++);
		
		fldEntidade.setBeanName(Pessoa.class.getName());
		fldEntidade.setName("entidade");
		fldEntidade.setOrder(count++);

		fldClasseDeEntidade.setBeanName(Pessoa.class.getName());
		fldClasseDeEntidade.setName("classeDaEntidade");
		fldClasseDeEntidade.setOrder(count++);				

		fldEstabelecimento.setBeanName(Disponivel.class.getName());
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