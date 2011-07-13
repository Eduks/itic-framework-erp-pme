package br.com.dyad.backoffice.navigation.operacao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.auxiliares.TituloAux;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.RegraBaixaTitulo;
import br.com.dyad.backoffice.operacao.consulta.ConsultaBaixaTitulo;
import br.com.dyad.backoffice.operacao.consulta.ConsultaTitulo;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.Window;

public class BaixaTituloWindow extends Window {

	private AppTransaction appTransaction;

	private GridVariaveisBaixaTitulo gridVariaveis;
	private GridEdicaoBaixaTitulo gridEdicaoBaixaTitulo;
	private GridExibeTitulosPendentesLocalizados gridExibeTitulosPendentesLocalizados;
	private DataList dataListExibeTituloPendentes;
	
	private CabecalhoBaixaTitulo cabecalhoEditando;

	public RegraBaixaTitulo regraBaixaTitulo;
	
	public BaixaTituloWindow (HttpSession httpSession) {
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
			BaixaTituloWindow process = (BaixaTituloWindow)getParent();
			process.localizarTitulosPendentes();
			
			process.setNextInteraction(process.exibePedidosPendentes);
		}
	};
		
	public Action actionLocalizarBaixa = new Action(this, "Localizar Baixas"){		
		@Override
		public void onClick() throws Exception {
			BaixaTituloWindow process = (BaixaTituloWindow)getParent();
			
			process.localizarBaixasTitulos();
			process.edicaoBaixaTitulo.setDefined(false);
			
			process.setNextInteraction(process.edicaoBaixaTitulo);
		}
	};
		
	/**
	 * Actions da Interaction ExibePedidosPendentes
	 */
	public Action actionIncluirPedidosPendentes = new Action(this, "Incluir Pendencias"){
		@Override
		public void onClick() throws Exception {
			BaixaTituloWindow process = (BaixaTituloWindow)getParent();
			
			process.incluirTitulosPendentes();
			process.edicaoBaixaTitulo.setDefined(false);
			
			process.setNextInteraction(process.edicaoBaixaTitulo);
		};
	};

	public Action actionVoltarVariaveis = new Action(this, "Voltar às Variáveis"){
		@Override
		public void onClick() throws Exception {
			BaixaTituloWindow process = (BaixaTituloWindow)this.getParent();
			
			process.setNextInteraction(process.variaveis);
		}
	};
	
	public Action actionVoltarEdicaoBaixa = new Action(this, "Voltar à Edição de Baixa"){
		@Override
		public void onClick() throws Exception {
			BaixaTituloWindow process = (BaixaTituloWindow)getParent();			
			
			process.edicaoBaixaTitulo.setDefined(false);

			process.setNextInteraction(process.edicaoBaixaTitulo);
		}
	};
	
	/**
	 * Actions da Interaction Edição de Baixa de Pedido
	 */
	public Action actionGravar = new Action(this, "Gravar"){
		@Override
		public void onClick() throws Exception {
			BaixaTituloWindow process = (BaixaTituloWindow)getParent();			
			process.regraBaixaTitulo.grava();
			process.regraBaixaTitulo.fecha();
			process.variaveis.setDefined(false);
			
			process.setNextInteraction(process.variaveis);
		};
	};
	
	public Action actionLocalizarOutrasPendencias = new Action(this, "Localizar Outras Pendências"){
		@Override
		public void onClick() throws Exception {
			BaixaTituloWindow process = (BaixaTituloWindow)getParent();
			process.cabecalhoEditando = (CabecalhoBaixaTitulo)process.gridEdicaoBaixaTitulo.getCurrentEntity();
			process.variaveis.setDefined(false);
			
			process.setNextInteraction(process.variaveis);
		};
	};
	
	public Action actionAbandonarBaixaTitulo = new Action(this, "Abandonar Baixa Atual"){
		@Override
		public void onClick() throws Exception {
			BaixaTituloWindow process = (BaixaTituloWindow)getParent();			
			process.regraBaixaTitulo.fecha();
			process.variaveis.setDefined(false);
			
			process.setNextInteraction(process.variaveis);
		};
	};
	
	
	/**
	 * Interação que é exibida quando o usuário entra no processo.
	 */
	public Interaction variaveis = new Interaction(this, "variaveis"){
		BaixaTituloWindow process = (BaixaTituloWindow)this.getParent();

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
		BaixaTituloWindow process = (BaixaTituloWindow)this.getParent();
		
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions( process.actionIncluirPedidosPendentes );
			this.enableAndShowActions( process.actionVoltarVariaveis );
			
			if (process.cabecalhoEditando != null) {
				this.enableAndShowActions( process.actionVoltarEdicaoBaixa );
			}
			
			add(gridExibeTitulosPendentesLocalizados);
		}
	};

	/**
	 * Interação usada para editar dados do pedido.
	 * Ela é usada tanto para Criação de novos pedidos como para
	 * edição de pedidos já existentes.
	 */
	public Interaction edicaoBaixaTitulo = new Interaction(this, "edicaoBaixaTitulo"){
		BaixaTituloWindow process = (BaixaTituloWindow)this.getParent();
		
		@Override
		public void defineInteraction() throws Exception {
			process.cabecalhoEditando = null;
			
			this.enableAndShowActions( process.actionGravar );
			this.enableAndShowActions( process.actionLocalizarOutrasPendencias );
			this.enableAndShowActions( process.actionAbandonarBaixaTitulo );
			
			add(gridEdicaoBaixaTitulo);
		}
	};
	
	@Override 
	public void defineWindow() throws Exception {
		this.appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDatabase()), this.getDatabase());
		
		this.regraBaixaTitulo = new RegraBaixaTitulo(this.appTransaction);
		this.regraBaixaTitulo.preparaRegra();
		
		this.gridVariaveis = new GridVariaveisBaixaTitulo(this);
		this.gridExibeTitulosPendentesLocalizados = new GridExibeTitulosPendentesLocalizados(this);
		this.gridEdicaoBaixaTitulo = new GridEdicaoBaixaTitulo(this, this.regraBaixaTitulo);
		
		this.dataListExibeTituloPendentes = DataListFactory.newDataList(this.appTransaction);
		
		this.gridExibeTitulosPendentesLocalizados.setDataList(this.dataListExibeTituloPendentes);
		this.gridEdicaoBaixaTitulo.setDataList(this.regraBaixaTitulo.getCabecalhosOperacao());
	}

	public void localizarTitulosPendentes() throws Exception{	
		this.dataListExibeTituloPendentes.empty();

		ConsultaTitulo consultaTitulo = new ConsultaTitulo(this.appTransaction);
		consultaTitulo.setStatus(ConsultaTitulo.STATUS_PENDENTE);
		
		if (this.gridVariaveis.fldItem.getValue() != null) {
			consultaTitulo.setTituloId(new Long(this.gridVariaveis.fldItem.getValue()) );
		}
		if (this.gridVariaveis.fldOperacao.getValue() != null) {
			consultaTitulo.setOperacaoId( new Long(this.gridVariaveis.fldOperacao.getValue()) );
		}
		if (this.gridVariaveis.fldNumero.getValue() != null) {
			consultaTitulo.setNumero( this.gridVariaveis.fldNumero.getValue() );
		}
		
		consultaTitulo.getParametroEntidade().setId( this.gridVariaveis.fldEntidade.getValueId() );

		if (this.gridVariaveis.fldClasseDeEntidade.getValue() != null) {
			consultaTitulo.getParametroClassesEntidade().setStringId( this.gridVariaveis.fldClasseDeEntidade.getValueId().toString() );
		}

		consultaTitulo.setEmissaoInicial( this.gridVariaveis.fldEmissaoInicial.getValue() );
		consultaTitulo.setEmissaoFinal( this.gridVariaveis.fldEmissaoFinal.getValue() );
		
		ArrayList<Titulo> titulos = consultaTitulo.pegaTitulosAsList();
		
		if (!titulos.isEmpty()) {
			List<AppEntity> titulosTemp = new ArrayList<AppEntity>();

			for (Titulo p : titulos) {
				titulosTemp.add(p);
			}

			ConsultaBaixaTitulo consultaBaixaTituloAux = new ConsultaBaixaTitulo(this.appTransaction);
			consultaBaixaTituloAux.getParametroTituloBaixado().setObjetos(titulosTemp);
			DataList dataListBaixas = consultaBaixaTituloAux.pegaBaixasTitulo();

			for (Titulo t : titulos) {
				Collection<AppEntity> baixasTitulo = dataListBaixas.getRangeAsCollection("Titulo", t.getId());
				BigDecimal totalBaixado = new BigDecimal(0);

				if (baixasTitulo != null) {					
					for (AppEntity baixaTitulo : baixasTitulo) {
						totalBaixado = totalBaixado.add( ((ItemBaixaTitulo)baixaTitulo).getTotal() ); 
					}
				}

				TituloAux tituloAux = new TituloAux();
				tituloAux.setId(t.getId());
				tituloAux.setOperacaoId(new BigInteger(t.getOperacaoId().toString()));
				tituloAux.setEntidade(t.getEntidade());				
				tituloAux.setEmissao(t.getEmissao());
				tituloAux.setVencimento(t.getVencimento());
				tituloAux.setCorrecao(t.getCorrecao());
				tituloAux.setValor(new BigDecimal(t.getTotal().toString()));
				tituloAux.setValorBaixado(new BigDecimal(totalBaixado.toString()));
				tituloAux.setSaldo(t.getTotal().subtract(totalBaixado));

				this.dataListExibeTituloPendentes.add(tituloAux);				
			}
			
			this.gridExibeTitulosPendentesLocalizados.firstEntity();
		} else {
			throw new Exception("Não foram encontradas pendências para as variáveis informadas."); 
		}
	}

	public void localizarBaixasTitulos() throws Exception {	
		ConsultaBaixaTitulo consultaBaixasTitulo = this.regraBaixaTitulo.getConsultaBaixaTitulo();

		if (this.gridVariaveis.fldItem.getValue() != null) {
			consultaBaixasTitulo.setPedidoId( new Long(this.gridVariaveis.fldItem.getValue()) );
		}
		if (this.gridVariaveis.fldOperacao.getValue() != null) {
			consultaBaixasTitulo.setOperacaoId( new Long(this.gridVariaveis.fldOperacao.getValue()) );
		}
		if (this.gridVariaveis.fldNumero.getValue() != null) {
			consultaBaixasTitulo.setNumero( this.gridVariaveis.fldNumero.getValue() );
		}
		
		consultaBaixasTitulo.getParametroEntidade().limpaValores();
		consultaBaixasTitulo.getParametroEntidade().setId( this.gridVariaveis.fldEntidade.getValueId() );

		consultaBaixasTitulo.setEmissaoInicial( this.gridVariaveis.fldEmissaoInicial.getValue() );
		consultaBaixasTitulo.setEmissaoFinal( this.gridVariaveis.fldEmissaoFinal.getValue() );
		
		this.regraBaixaTitulo.abre();
		
		if (this.regraBaixaTitulo.getItens().getList().isEmpty()) {
			throw new Exception("Não foram encontradas baixas para as variáveis informadas."); 
		}
	}	

	@SuppressWarnings("unchecked")
	public void incluirTitulosPendentes() throws Exception{
		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = null;
		
		if (this.cabecalhoEditando != null) {
			cabecalhoBaixaTitulo = this.cabecalhoEditando;
		} else {
			cabecalhoBaixaTitulo = this.regraBaixaTitulo.novoCabecalho();
			cabecalhoBaixaTitulo.setEmissao(new Date());
		}
		
		List<AppEntity> titulosAux = this.dataListExibeTituloPendentes.getList();
		ArrayList<Long> titulosIds = new ArrayList();
		
		for (AppEntity tituloAux : titulosAux) {
			titulosIds.add(tituloAux.getId());			
		}
		
		//TODO verificar se transforma o consultaTitulo em global???
		ConsultaTitulo consultaTitulo = new ConsultaTitulo(this.appTransaction);
		consultaTitulo.getParametroId().setListaDeIds(titulosIds);
		ArrayList<Titulo> itemTitulos = consultaTitulo.pegaTitulosAsList();
		
		for (Titulo titulo : itemTitulos) {
			ItemBaixaTitulo itemBaixaTitulo = (ItemBaixaTitulo) this.regraBaixaTitulo.novoItem(cabecalhoBaixaTitulo);
			itemBaixaTitulo.setTitulo(titulo);
			
			ArrayList titulosAuxTitulo = new ArrayList(this.dataListExibeTituloPendentes.getRange("id", titulo.getId()).values());
			itemBaixaTitulo.setTotal( ((TituloAux)titulosAuxTitulo.get(0)).getSaldo() );
		}
		
		this.gridEdicaoBaixaTitulo.firstEntity();

		System.out.println("QUANTIDADE DE BAIXAS=>" + this.regraBaixaTitulo.getItens().getList().size());
	}
}