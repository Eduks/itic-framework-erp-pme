package br.com.dyad.backoffice.navigation.operacao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.auxiliares.TituloAux;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraBaixaTitulo;
import br.com.dyad.backoffice.operacao.consulta.ConsultaBaixaTitulo;
import br.com.dyad.backoffice.operacao.consulta.ConsultaTitulo;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.entity.AbstractEntity;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

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
	public Action actionLocalizarPendencia = new Action(this, "Localizar Pendências"){		
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
	public Action actionIncluirPedidosPendentes = new Action(this, "Incluir Pendências"){
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
			confirm("confirmGravar", "Deseja gravar a operação atual?");
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
			confirm("confirmAbandonarBaixa", "Deseja abandonar a operação atual?");
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
		this.gridExibeTitulosPendentesLocalizados.setViewMode(DataGrid.VIEW_MODE_TABLEVIEW);
		this.gridEdicaoBaixaTitulo = new GridEdicaoBaixaTitulo(this, this.regraBaixaTitulo);
		
		this.dataListExibeTituloPendentes = DataListFactory.newDataList(this.appTransaction);
		
		this.gridExibeTitulosPendentesLocalizados.setDataList(this.dataListExibeTituloPendentes);
		this.gridEdicaoBaixaTitulo.setDataList(this.regraBaixaTitulo.getDataListCabecalhos());
	}
	
	public void confirmGravar(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			this.regraBaixaTitulo.grava();
			this.regraBaixaTitulo.fecha();
			this.variaveis.setDefined(false);
			
			this.setNextInteraction(this.variaveis);
		}
	}
	
	public void confirmAbandonarBaixa(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			this.regraBaixaTitulo.fecha();
			this.variaveis.setDefined(false);
			
			this.setNextInteraction(variaveis);
		}
	}

	public void localizarTitulosPendentes() throws Exception{
		
		if (this.gridVariaveis.fldOperacao.getValue() != null ||
			this.gridVariaveis.fldItem.getValue() != null ||	
			this.gridVariaveis.fldNumero.getValue() != null ||
			this.gridVariaveis.fldEntidade.getValue() != null ||
			this.gridVariaveis.fldEmissaoInicial.getValue() != null ||
			this.gridVariaveis.fldEmissaoFinal.getValue() != null || 
			this.gridVariaveis.fldVencimentoInicial.getValue() != null ||
			this.gridVariaveis.fldVencimentoFinal.getValue() != null) {
			
			if( this.gridVariaveis.fldEmissaoInicial.getValue() != null &&
				this.gridVariaveis.fldEmissaoFinal.getValue() != null &&
				this.gridVariaveis.fldEmissaoInicial.getValue().after( this.gridVariaveis.fldEmissaoFinal.getValue())){
				SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");				
				throw new AppException("A data de Emissão Final '" + dataFormat.format(this.gridVariaveis.fldEmissaoFinal.getValue()) + "' deve ser maior ou igual a Data de Emissão Inicial '" + dataFormat.format(this.gridVariaveis.fldEmissaoInicial.getValue()) + "'.");				
			}

			if( this.gridVariaveis.fldVencimentoInicial.getValue() != null &&
				this.gridVariaveis.fldVencimentoFinal.getValue() != null &&
				this.gridVariaveis.fldVencimentoInicial.getValue().after( this.gridVariaveis.fldVencimentoFinal.getValue())){
				SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");				
				throw new AppException("A data de Vencimento Final '" + dataFormat.format(this.gridVariaveis.fldVencimentoFinal.getValue()) + "' deve ser maior ou igual a Data de Vencimento Inicial '" + dataFormat.format(this.gridVariaveis.fldVencimentoInicial.getValue()) + "'.");				
			}

			this.dataListExibeTituloPendentes.empty();

			ConsultaTitulo consultaTitulo = new ConsultaTitulo(this.appTransaction);
			consultaTitulo.setStatus(ConsultaTitulo.STATUS_PENDENTE);

			consultaTitulo.getParametroId().setId((Long)this.gridVariaveis.fldItem.getValue());
			consultaTitulo.getParametroOperacoesId().setId((Long)this.gridVariaveis.fldOperacao.getValue());
			consultaTitulo.setNumero( this.gridVariaveis.fldNumero.getValue() );
			
			consultaTitulo.getParametroEntidade().setId( this.gridVariaveis.fldEntidade.getValueId() );
	
			consultaTitulo.setEmissaoInicial( this.gridVariaveis.fldEmissaoInicial.getValue() );
			consultaTitulo.setEmissaoFinal( this.gridVariaveis.fldEmissaoFinal.getValue() );
			consultaTitulo.setVencimentoInicial( this.gridVariaveis.fldVencimentoInicial.getValue() );
			consultaTitulo.setVencimentoFinal( this.gridVariaveis.fldVencimentoFinal.getValue() );			
			
			ArrayList<Titulo> titulos = consultaTitulo.pegaTitulosAsList();
			
			if (!titulos.isEmpty()) {
				List<AppEntity> titulosTemp = new ArrayList<AppEntity>();
	
				for (Titulo titulo : titulos) {
					titulosTemp.add(titulo);
				}
	
				ConsultaBaixaTitulo consultaBaixaTituloAux = new ConsultaBaixaTitulo(this.appTransaction);
				consultaBaixaTituloAux.getParametroTituloBaixado().setObjetos(titulosTemp);
				DataList dataListBaixas = consultaBaixaTituloAux.pegaBaixasTitulo();
	
				for (Titulo titulo : titulos) {
					Collection<AppEntity> baixasTitulo = dataListBaixas.getRangeAsCollection("tituloBaixado", titulo.getId());
					BigDecimal totalBaixado = new BigDecimal(0);
	
					if (baixasTitulo != null) {					
						for (AppEntity baixaTitulo : baixasTitulo) {
							totalBaixado = totalBaixado.add( ((ItemBaixaTitulo)baixaTitulo).getTotal() ); 
						}
					}
	
					TituloAux tituloAux = new TituloAux();
					tituloAux.setId(titulo.getId());
					tituloAux.setOperacaoId(titulo.getCabecalho().getId());
					tituloAux.setEntidade(titulo.getEntidade());
					tituloAux.setEstabelecimento(titulo.getEstabelecimento());
					tituloAux.setEmissao(titulo.getEmissao());
					tituloAux.setVencimento(titulo.getVencimento());
					tituloAux.setCorrecao(titulo.getCorrecao());
					tituloAux.setValor(titulo.getTotal().setScale(2));
					tituloAux.setValorBaixado(totalBaixado.setScale(2));
					tituloAux.setSaldo(tituloAux.getValor().subtract(tituloAux.getValorBaixado()).setScale(2));
					tituloAux.setTitulo(titulo);
	
					this.dataListExibeTituloPendentes.add(tituloAux);				
				}
				
				this.gridExibeTitulosPendentesLocalizados.firstEntity();
			} else {
				throw new AppException("Não foram encontradas Pendências para as variáveis informadas."); 
			}
		} else{
			throw new AppException("Não foi definido nenhum filtro para a consulta. Para realizar a consulta informe pelo menos um parâmetro.");
		}	
	}

	public void localizarBaixasTitulos() throws Exception{
		Long operacaoId = null;
		Long entidadeId = null;
		Date emissaoInicial = null;
		Date emissaoFinal = null;
		
		if (this.gridVariaveis.fldOperacao.getValue() != null) {
			operacaoId = (Long)this.gridVariaveis.fldOperacao.getValue();
		}
		
		if (this.gridVariaveis.fldEntidade.getValue() != null) {
			entidadeId = this.gridVariaveis.fldEntidade.getValueId();
		}

		emissaoInicial = this.gridVariaveis.fldEmissaoInicial.getValue();
		emissaoFinal = this.gridVariaveis.fldEmissaoFinal.getValue();
		
		this.regraBaixaTitulo.abre(operacaoId, entidadeId, emissaoInicial, emissaoFinal);
		if (this.regraBaixaTitulo.getDataListCabecalhos().isEmpty()) {
			throw new AppException("Não foram encontrados pedidos com as restrições informadas.");
		}
		
	}	

	public void incluirTitulosPendentes() throws Exception{
		
		ArrayList<AbstractEntity> titulosAux = this.gridExibeTitulosPendentesLocalizados.getSelectedRecords();
		
		if (titulosAux.isEmpty()){
			throw new AppException("É necessário que se escolha pelo menos um Título Pendente para poder gerar uma baixa de título.");
		}
		
		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = null;
		
		if (this.cabecalhoEditando != null) {
			cabecalhoBaixaTitulo = this.cabecalhoEditando;
		} else {
			cabecalhoBaixaTitulo = this.regraBaixaTitulo.novoCabecalho();
			cabecalhoBaixaTitulo.setEmissao(new Date());
		}
		
		for (AbstractEntity abstratctAux : titulosAux) {
			TituloAux tituloAux = (TituloAux)abstratctAux;
			
			ItemBaixaTitulo itemBaixaTitulo = (ItemBaixaTitulo) this.regraBaixaTitulo.novoItem(cabecalhoBaixaTitulo);
			itemBaixaTitulo.setTituloBaixado(tituloAux.getTitulo());
			itemBaixaTitulo.setEntidade(tituloAux.getEntidade());
			itemBaixaTitulo.setEstabelecimento(tituloAux.getEstabelecimento());
			itemBaixaTitulo.setTotal(tituloAux.getSaldo()); 
		} 
		
		this.gridEdicaoBaixaTitulo.firstEntity();
	}
}