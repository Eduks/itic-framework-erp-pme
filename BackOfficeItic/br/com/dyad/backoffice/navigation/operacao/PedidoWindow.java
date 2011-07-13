package br.com.dyad.backoffice.navigation.operacao;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.RegraPedido;
import br.com.dyad.backoffice.operacao.consulta.ConsultaPedido;
import br.com.dyad.businessinfrastructure.entidades.entidade.Disponivel;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldClassLookup;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.field.FieldString;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class PedidoWindow extends Window {

	private AppTransaction appTransaction;

	private GridVariaveisPedido gridVariaveisPedido;
	private GridEdicaoPedido gridEdicaoPedido;

	public RegraPedido regraPedido;
	
	public PedidoWindow (HttpSession httpSession) {
		super(httpSession);
	}

	/**
	 * Actions
	 * 
	 * O termo "Localizar" foi escolhido por ser um "padrão".
	 * Verificamos que o MSOffice, o Mozilla Firefox e outros
	 * softwares de referência, usam este termo. 
	 */
	public Action actionLocalizar = new Action(this, "Localizar Pedidos"){		
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow)getParent();
			
			process.localizarPedidos();
			process.setNextInteraction(edicaoPedido);
		}
	};
		
	public Action actionCalcular = new Action(this, "Calcular"){		
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow)getParent();
			
			CabecalhoPedido cabecalho = (CabecalhoPedido)process.gridEdicaoPedido.getCurrentEntity();
			process.regraPedido.calculaOperacao(cabecalho);
		}
	};
		
	public Action actionNova = new Action(this, "Nova"){
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow)getParent();			
			process.setNextInteraction(edicaoPedido);
		}
	};
	
	public Action actionAprovar = new Action(this, "Aprovar"){
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow)getParent();			
			CabecalhoPedido cabecalhoOperacao = (CabecalhoPedido)process.gridEdicaoPedido.getCurrentEntity();
			process.regraPedido.aprova(cabecalhoOperacao.getOperacaoId());
		}
	};
	
	public Action actionDesaprovar = new Action(this, "Desaprovar"){
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow)getParent();			
			CabecalhoPedido cabecalhoPedido = (CabecalhoPedido)process.gridEdicaoPedido.getCurrentEntity();
			process.regraPedido.desaprova(cabecalhoPedido.getOperacaoId());
		}
	};
	
	public Action actionCancelar = new Action(this, "Cancelar"){
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow)getParent();			
			CabecalhoPedido cabecalhoOperacao = (CabecalhoPedido)process.gridEdicaoPedido.getCurrentEntity();
			process.regraPedido.cancela(cabecalhoOperacao.getOperacaoId());
		}
	};
	
	public Action actionDescancelar = new Action(this, "Descancelar"){
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow)getParent();			

			CabecalhoPedido cabecalhoPedido = (CabecalhoPedido)process.gridEdicaoPedido.getCurrentEntity();
			process.regraPedido.descancela(cabecalhoPedido.getOperacaoId());
		}
	};
	
	public Action actionExcluir = new Action(this, "Excluir"){
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow)getParent();			
		
			CabecalhoPedido cabecalhoPedido = (CabecalhoPedido)process.gridEdicaoPedido.getCurrentEntity();
			
			process.regraPedido.exclui(cabecalhoPedido.getOperacaoId());
		};
	};
	
	public Action actionGravar = new Action(this, "Gravar"){
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow)getParent();			
		
			process.regraPedido.grava();
			process.regraPedido.fecha();
			
			process.setNextInteraction(process.variaveis);
		};
	};
	
	public Action actionFechar = new Action(this, "Fechar"){
		@Override
		public void onClick() throws Exception {
			PedidoWindow process = (PedidoWindow)getParent();			
		
			process.regraPedido.fecha();
			
			process.setNextInteraction(process.variaveis);
		};
	};
	
	/**
	 * Interação que é exibida quando o usuário entra no processo.
	 */
	Interaction variaveis = new Interaction(this, "variaveis"){
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions( actionLocalizar );
			this.enableAndShowActions( actionNova );
			
			add(gridVariaveisPedido);
		}
		
	};
	
	/**
	 * Interação usada para editar dados do pedido.
	 * Ela é usada tanto para Criação de novos pedidos como para
	 * edição de pedidos já existentes.
	 */
	public Interaction edicaoPedido = new Interaction(this, "edicaoPedido"){
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions( actionCalcular );
			this.enableAndShowActions( actionAprovar );
			this.enableAndShowActions( actionDesaprovar );
			this.enableAndShowActions( actionCancelar );
			this.enableAndShowActions( actionDescancelar );
			this.enableAndShowActions( actionExcluir );
			this.enableAndShowActions( actionGravar );
			this.enableAndShowActions( actionFechar );
			
			add(gridEdicaoPedido);
		}
	};
	
	@Override
	public void defineWindow() throws Exception {
		this.appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDatabase()), this.getDatabase());
		
		this.regraPedido = new RegraPedido(this.appTransaction);
		this.regraPedido.preparaRegra();
		
		this.gridVariaveisPedido = new GridVariaveisPedido(this);
		this.gridEdicaoPedido = new GridEdicaoPedido(this, this.regraPedido);
		
		this.gridEdicaoPedido.setDataList( this.regraPedido.getCabecalhosOperacao() );
	}

	public void localizarPedidos() throws Exception{	
		ConsultaPedido consultaPedido = this.regraPedido.getConsultaPedido();

		if (this.gridVariaveisPedido.fldItem.getValue() != null) {
			consultaPedido.setPedidoId( new Long(this.gridVariaveisPedido.fldItem.getValue()) );
		}
		if (this.gridVariaveisPedido.fldOperacao.getValue() != null) {
			consultaPedido.setOperacaoId( new Long(this.gridVariaveisPedido.fldOperacao.getValue()) );
		}
		if (this.gridVariaveisPedido.fldNumero.getValue() != null) {
			consultaPedido.setNumero( this.gridVariaveisPedido.fldNumero.getValue() );
		}
		
		consultaPedido.getParametroEntidade().setId( this.gridVariaveisPedido.fldEntidade.getValueId() );

		if (this.gridVariaveisPedido.fldClasseDeEntidade.getValueId() != null) {
			consultaPedido.getParametroClassesEntidade().setStringId( this.gridVariaveisPedido.fldClasseDeEntidade.getValueId().toString() );
		}
		consultaPedido.getParametroEstabelecimento().setId( this.gridVariaveisPedido.fldEstabelecimento.getValueId() );
		consultaPedido.getParametroRecurso().setId( this.gridVariaveisPedido.fldEstabelecimento.getValueId() );
		consultaPedido.getParametroNucleo().setId( this.gridVariaveisPedido.fldNucleo.getValueId() );
		
		consultaPedido.setEmissaoInicial( this.gridVariaveisPedido.fldEmissaoInicial.getValue() );
		consultaPedido.setEmissaoFinal( this.gridVariaveisPedido.fldEmissaoFinal.getValue() );
		
		this.regraPedido.abre();
		if (this.regraPedido.getItens().isEmpty()) {
			throw new Exception("Não foram encontrados pedidos com as restrições informadas.");
		}
	}	
}

/**
 * 
 * @author Dyad
 *
 */
class GridVariaveisPedido extends VariableGrid {

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
	
	public GridVariaveisPedido(Window window) throws Exception {
		super(window, "variaveis");
	}

	@Override
	public void defineGrid() throws Exception {
		setTitle("Variáveis");
		
		int count = 0;
		
		fldItem.setLabel("Item");
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