package br.com.dyad.backoffice.navigation.operacao;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraBaixaAutomatica;
import br.com.dyad.backoffice.navigation.relatorio.EspelhoBaixaAutomaticaHTML;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
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
public class BaixaAutomaticaWindow extends Window {

	private AppTransaction appTransaction;

	private GridVariaveisPedidoBaixaAutomatica gridVariaveisPedidoBaixaAutomatica;
	private GridEdicaoBaixaAutomatica gridEdicaoBaixaAutomatica;

	public RegraBaixaAutomatica regraBaixaAutomatica;
	
	public BaixaAutomaticaWindow (HttpSession httpSession) {
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
			BaixaAutomaticaWindow process = (BaixaAutomaticaWindow)getParent();
			
			process.localizarPedidos();
			process.setNextInteraction(edicaoPedido);
		}
	};
		
	public Action actionCalcular = new Action(this, "Calcular"){		
		@Override
		public void onClick() throws Exception {
			BaixaAutomaticaWindow process = (BaixaAutomaticaWindow)getParent();
			
			CabecalhoBaixaAutomatica cabecalho = (CabecalhoBaixaAutomatica)process.gridEdicaoBaixaAutomatica.getCurrentEntity();
			process.regraBaixaAutomatica.calculaOperacao(cabecalho);
			process.regraBaixaAutomatica.criaTitulosOperacao(cabecalho);
			
			process.gridEdicaoBaixaAutomatica.detailGridTitulosBaixaAutomatica.getDetailGrid().setCurrentEntity(cabecalho.getTitulos().get(0));
			process.gridEdicaoBaixaAutomatica.detailGridTitulosBaixaAutomatica.getDataList().setList(process.gridEdicaoBaixaAutomatica.detailGridTitulosBaixaAutomatica.getDataList().getList());
		}
	};
		
	public Action actionNova = new Action(this, "Nova"){
		public void onClick() throws Exception {
			BaixaAutomaticaWindow process = (BaixaAutomaticaWindow)getParent();			
			
			gridEdicaoBaixaAutomatica.dispatchInsert();
			
			process.setNextInteraction(edicaoPedido);
		}
	};
	
	public Action actionAprovar = new Action(this, "Aprovar"){
		@Override
		public void onClick() throws Exception {
			BaixaAutomaticaWindow process = (BaixaAutomaticaWindow)getParent();			
			CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica = (CabecalhoBaixaAutomatica)process.gridEdicaoBaixaAutomatica.getCurrentEntity();
			process.regraBaixaAutomatica.aprova(cabecalhoBaixaAutomatica.getId());
			
			if (process.gridEdicaoBaixaAutomatica.detailGridTitulosBaixaAutomatica.getDataList().getList().size() > 0) {
				
				DataList dataListTitulos = DataListFactory.newDataList(appTransaction);
				
				dataListTitulos.setList(process.gridEdicaoBaixaAutomatica.detailGridTitulosBaixaAutomatica.getDataList().getList());
				
				process.gridEdicaoBaixaAutomatica.getDetails().get(1).setDataList(dataListTitulos);
				
//				process.gridEdicaoBaixaAutomatica.detailGridTitulosBaixaAutomatica.getDetailGrid().setCurrentEntity(cabecalhoBaixaAutomatica.getTitulos().get(0));
//				process.gridEdicaoBaixaAutomatica.detailGridTitulosBaixaAutomatica.getDataList().setList(process.gridEdicaoBaixaAutomatica.detailGridTitulosBaixaAutomatica.getDataList().getList());
			}
			
		}
	};
	
	public Action actionDesaprovar = new Action(this, "Desaprovar"){
		@Override
		public void onClick() throws Exception {
			BaixaAutomaticaWindow process = (BaixaAutomaticaWindow)getParent();			
			CabecalhoBaixaAutomatica cabecalhoPedido = (CabecalhoBaixaAutomatica)process.gridEdicaoBaixaAutomatica.getCurrentEntity();
			process.regraBaixaAutomatica.desaprova(cabecalhoPedido.getId());
		}
	};
	
	public Action actionCancelar = new Action(this, "Cancelar"){
		@Override
		public void onClick() throws Exception {
			BaixaAutomaticaWindow process = (BaixaAutomaticaWindow)getParent();			
			CabecalhoBaixaAutomatica cabecalhoOperacao = (CabecalhoBaixaAutomatica)process.gridEdicaoBaixaAutomatica.getCurrentEntity();
			process.regraBaixaAutomatica.cancela(cabecalhoOperacao.getId());
		}
	};
	
	public Action actionDescancelar = new Action(this, "Descancelar"){
		@Override
		public void onClick() throws Exception {
			BaixaAutomaticaWindow process = (BaixaAutomaticaWindow)getParent();			
			CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica = (CabecalhoBaixaAutomatica)process.gridEdicaoBaixaAutomatica.getCurrentEntity();
			process.regraBaixaAutomatica.descancela(cabecalhoBaixaAutomatica.getId());
		}
	};
	
	public Action actionGravar = new Action(this, "Gravar"){
		@Override
		public void onClick() throws Exception {
			confirm("confirmGravar", "Deseja gravar as operações?");
		};
	};
	
	public Action actionFechar = new Action(this, "Fechar"){
		@Override
		public void onClick() throws Exception {
			confirm("confirmFechar", "Deseja fechar as operações?");
		};
	};
	
	public Action actionImprimir = new Action(this, "Imprimir") {
		@Override
		public void onClick() throws Exception {
			BaixaAutomaticaWindow process = (BaixaAutomaticaWindow) getParent();
			
			CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica = (CabecalhoBaixaAutomatica)process.gridEdicaoBaixaAutomatica.getCurrentEntity();
			
			if (cabecalhoBaixaAutomatica.getEstabelecimento() == null || cabecalhoBaixaAutomatica.getEntidade() == null || cabecalhoBaixaAutomatica.getEmissao() == null) {
				throw new AppException("Preencha os campos obrigatórios do agrupamento!");
			}
			
			if (cabecalhoBaixaAutomatica.getTitulos() == null || cabecalhoBaixaAutomatica.getTitulos().isEmpty()) {
				throw new AppException("Insira os títulos da baixa automática!");
			}
			
			process.setNextInteraction(process.espelhoBaixaAutomatica);
		};
	};

	/*
	 * Métodos relacionadas às chamadas que devem ser feitas através de "Confirm´s" 
	 */
	public void confirmGravar(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			this.regraBaixaAutomatica.grava();
			this.regraBaixaAutomatica.fecha();

			this.setNextInteraction(variaveis);
		}
	}
	
	public void confirmFechar(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			this.regraBaixaAutomatica.fecha();

			setNextInteraction(variaveis);
		}
	}
	
	/**
	 * Interação que é exibida quando o usuário entra no processo.
	 */
	Interaction variaveis = new Interaction(this, "variaveis"){
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions( actionLocalizar );
			this.enableAndShowActions( actionNova );
			
			add(gridVariaveisPedidoBaixaAutomatica);
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
			this.enableAndShowActions( actionGravar );
			this.enableAndShowActions( actionFechar );
			this.enableAndShowActions( actionImprimir );
			gridEdicaoBaixaAutomatica.first();
			add(gridEdicaoBaixaAutomatica);
		}
	};
	
	/**
	 * Interação usada para exibir o espelho do pedido
	 */
	public Interaction espelhoBaixaAutomatica = new Interaction(this, "espelhoPedido") {
		@Override
		public void defineInteraction() throws Exception {
			EspelhoBaixaAutomaticaHTML espelhoBaixaAutomatica = new EspelhoBaixaAutomaticaHTML();
			BaixaAutomaticaWindow process = (BaixaAutomaticaWindow) getParent();

			CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica = (CabecalhoBaixaAutomatica) process.gridEdicaoBaixaAutomatica.getCurrentEntity();
			espelhoBaixaAutomatica.setCabecalhoBaixaAutomatica(cabecalhoBaixaAutomatica);
			espelhoBaixaAutomatica.setItensBaixaAutomatica((List<ItemBaixaAutomatica>) cabecalhoBaixaAutomatica.getItensBaixaAutomatica());
			espelhoBaixaAutomatica.setTitulos((List<Titulo>) cabecalhoBaixaAutomatica.getTitulos());

			Label html = new Label("");
			html.setText(espelhoBaixaAutomatica.preparaHtml());
			setTitle("EspelhoBaixaAutomatica");
			add(html);
		}
	};

	@Override
	public void defineWindow() throws Exception {
		this.appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDatabase()), this.getDatabase());
		
		this.regraBaixaAutomatica = new RegraBaixaAutomatica(this.appTransaction);
		this.regraBaixaAutomatica.preparaRegra();
		
		this.gridVariaveisPedidoBaixaAutomatica = new GridVariaveisPedidoBaixaAutomatica(this);
		this.gridEdicaoBaixaAutomatica = new GridEdicaoBaixaAutomatica(this, this.regraBaixaAutomatica);
		
		this.gridEdicaoBaixaAutomatica.setDataList( this.regraBaixaAutomatica.getDataListCabecalhos() );
	}

	public void localizarPedidos() throws Exception{
		Long idOperacao = null;
		Long idItem = null;
		String numero = null;
		Long entidadeId = null;
		Long estabelecimentoId = null;
		Long recursoId = null;
		Long nucleoId = null;
		Date emissaoInicial = null;
		Date emissaoFinal = null;

		if (this.gridVariaveisPedidoBaixaAutomatica.fldIdOperacao.getValue() != null) {
			idOperacao = (Long)this.gridVariaveisPedidoBaixaAutomatica.fldIdOperacao.getValue();
		}
		
		if (this.gridVariaveisPedidoBaixaAutomatica.fldIdItem.getValue() != null) {
			idItem = (Long)this.gridVariaveisPedidoBaixaAutomatica.fldIdItem.getValue();
		}

		if (this.gridVariaveisPedidoBaixaAutomatica.fldNumero.getValue() != null) {
			numero = this.gridVariaveisPedidoBaixaAutomatica.fldNumero.getValue();
		}

		if (this.gridVariaveisPedidoBaixaAutomatica.fldEntidade.getValue() != null) {
			entidadeId = this.gridVariaveisPedidoBaixaAutomatica.fldEntidade.getValueId();
		}

		if (this.gridVariaveisPedidoBaixaAutomatica.fldEstabelecimento.getValue() != null) {
			estabelecimentoId = this.gridVariaveisPedidoBaixaAutomatica.fldEstabelecimento.getValueId();
		}
		
		if (this.gridVariaveisPedidoBaixaAutomatica.fldRecurso.getValue() != null) {
			recursoId = this.gridVariaveisPedidoBaixaAutomatica.fldRecurso.getValueId();
		}
		
		if (this.gridVariaveisPedidoBaixaAutomatica.fldNucleo.getValue() != null) {
			nucleoId = this.gridVariaveisPedidoBaixaAutomatica.fldNucleo.getValueId();
		}

		emissaoInicial = this.gridVariaveisPedidoBaixaAutomatica.fldEmissaoInicial.getValue();
		emissaoFinal = this.gridVariaveisPedidoBaixaAutomatica.fldEmissaoFinal.getValue();

		this.regraBaixaAutomatica.abre(idOperacao, idItem, numero, entidadeId, estabelecimentoId, recursoId, nucleoId, emissaoInicial, emissaoFinal);
		if (this.regraBaixaAutomatica.getDataListCabecalhos().isEmpty()) {
			throw new AppException("Não foram encontrados pedidos com as restrições informadas.");
		}
	}	
}

/**
 * 
 * @author Dyad
 *
 */
class GridVariaveisPedidoBaixaAutomatica extends VariableGrid {

	public FieldInteger fldIdOperacao = new FieldInteger(this);
	public FieldInteger fldIdItem = new FieldInteger(this);
	public FieldString fldNumero = new FieldString(this);
	public FieldLookup fldEntidade = new FieldLookup(this);
	public FieldLookup fldEstabelecimento = new FieldLookup(this);
	public FieldLookup fldRecurso = new FieldLookup(this);
	public FieldLookup fldNucleo = new FieldLookup(this);
	public FieldSimpleDate fldEmissaoInicial = new FieldSimpleDate(this);
	public FieldSimpleDate fldEmissaoFinal = new FieldSimpleDate(this);
	
	public GridVariaveisPedidoBaixaAutomatica(Window window) throws Exception {
		super(window, "variaveis");
	}

	@Override
	public void defineGrid() throws Exception {
		setTitle("Variáveis");
		
		int count = 0;
		
		fldIdOperacao.setLabel("Operação");
		fldIdOperacao.setName("operacao");
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