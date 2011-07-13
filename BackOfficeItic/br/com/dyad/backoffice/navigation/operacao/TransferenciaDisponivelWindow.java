package br.com.dyad.backoffice.navigation.operacao;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.RegraTransferenciaDisponivel;
import br.com.dyad.backoffice.operacao.consulta.ConsultaTransferenciaDisponivel;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
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

public class TransferenciaDisponivelWindow extends Window {

	private AppTransaction appTransaction;

	private GridVariaveisTransferenciaDisponivel gridVariaveisTransferenciaDisponivel;
	private GridEdicaoTransferenciaDisponivel gridEdicaoTransferenciaDisponivel;

	public RegraTransferenciaDisponivel regraTransferenciaDisponivel;
	
	public TransferenciaDisponivelWindow (HttpSession httpSession) {
		super(httpSession);
	}

	/**
	 * Actions
	 * 
	 * O termo "Localizar" foi escolhido por ser um "padrão".
	 * Verificamos que o MSOffice, o Mozilla Firefox e outros
	 * softwares de referência, usam este termo. 
	 */
	public Action actionLocalizar = new Action(this, "Localizar Transferências de Disponíveis"){		
		@Override
		public void onClick() throws Exception {
			TransferenciaDisponivelWindow process = (TransferenciaDisponivelWindow)getParent();
			
			process.localizarTransferenciasDisponiveis();
			process.setNextInteraction(edicaoTransferenciaDisponivel);
		}
	};
		
	public Action actionCalcular = new Action(this, "Calcular"){		
		@Override
		public void onClick() throws Exception {
			TransferenciaDisponivelWindow process = (TransferenciaDisponivelWindow)getParent();
			
			CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)process.gridEdicaoTransferenciaDisponivel.getCurrentEntity();
			process.regraTransferenciaDisponivel.calculaOperacao(cabecalhoTransferenciaDisponivel);
		}
	};
		
	public Action actionNova = new Action(this, "Nova"){
		public void onClick() throws Exception {
			TransferenciaDisponivelWindow process = (TransferenciaDisponivelWindow)getParent();			
			process.setNextInteraction(edicaoTransferenciaDisponivel);
		}
	};
	
	public Action actionExcluir = new Action(this, "Excluir"){
		@Override
		public void onClick() throws Exception {
			TransferenciaDisponivelWindow process = (TransferenciaDisponivelWindow)getParent();			
		
			CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)process.gridEdicaoTransferenciaDisponivel.getCurrentEntity();
			
			process.regraTransferenciaDisponivel.exclui(cabecalhoTransferenciaDisponivel.getOperacaoId());
		};
	};
	
	public Action actionGravar = new Action(this, "Gravar"){
		@Override
		public void onClick() throws Exception {
			TransferenciaDisponivelWindow process = (TransferenciaDisponivelWindow)getParent();			
		
			process.regraTransferenciaDisponivel.grava();
			process.regraTransferenciaDisponivel.fecha();
			
			process.setNextInteraction(process.variaveis);
		};
	};
	
	public Action actionFechar = new Action(this, "Fechar"){
		@Override
		public void onClick() throws Exception {
			TransferenciaDisponivelWindow process = (TransferenciaDisponivelWindow)getParent();			
		
			process.regraTransferenciaDisponivel.fecha();
			
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
			
			add(gridVariaveisTransferenciaDisponivel);
		}
		
	};
	
	/**
	 * Interação usada para editar dados do pedido.
	 * Ela é usada tanto para Criação de novos pedidos como para
	 * edição de pedidos já existentes.
	 */
	public Interaction edicaoTransferenciaDisponivel = new Interaction(this, "edicaoPedido"){
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions( actionCalcular );
			this.enableAndShowActions( actionExcluir );
			this.enableAndShowActions( actionGravar );
			this.enableAndShowActions( actionFechar );
			
			add(gridEdicaoTransferenciaDisponivel);
		}
	};
	
	@Override
	public void defineWindow() throws Exception {
		this.appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDatabase()), this.getDatabase());
		
		this.regraTransferenciaDisponivel = new RegraTransferenciaDisponivel(this.appTransaction);
		this.regraTransferenciaDisponivel.preparaRegra();
		
		this.gridVariaveisTransferenciaDisponivel = new GridVariaveisTransferenciaDisponivel(this);
		this.gridEdicaoTransferenciaDisponivel = new GridEdicaoTransferenciaDisponivel(this, this.regraTransferenciaDisponivel);
		
		this.gridEdicaoTransferenciaDisponivel.setDataList( this.regraTransferenciaDisponivel.getCabecalhosOperacao() );
	}

	public void localizarTransferenciasDisponiveis() throws Exception {	
		ConsultaTransferenciaDisponivel consultaTransferenciaDisponivel = this.regraTransferenciaDisponivel.getConsultaTransferenciaDisponivel();

		if (this.gridVariaveisTransferenciaDisponivel.fldItem.getValue() != null) {
			consultaTransferenciaDisponivel.setItemTransferenciaDisponivelId( new Long(this.gridVariaveisTransferenciaDisponivel.fldItem.getValue()) );
		}
		if (this.gridVariaveisTransferenciaDisponivel.fldOperacao.getValue() != null) {
			consultaTransferenciaDisponivel.setOperacaoId( new Long(this.gridVariaveisTransferenciaDisponivel.fldOperacao.getValue()) );
		}
		
		consultaTransferenciaDisponivel.getParametroEntidade().setId( this.gridVariaveisTransferenciaDisponivel.fldEntidade.getValueId() );

		if (this.gridVariaveisTransferenciaDisponivel.fldClasseDeEntidade.getValueId() != null) {
			consultaTransferenciaDisponivel.getParametroClassesEntidade().setStringId( this.gridVariaveisTransferenciaDisponivel.fldClasseDeEntidade.getValueId().toString() );
		}
		
		consultaTransferenciaDisponivel.setEmissaoInicial( this.gridVariaveisTransferenciaDisponivel.fldEmissaoInicial.getValue() );
		consultaTransferenciaDisponivel.setEmissaoFinal( this.gridVariaveisTransferenciaDisponivel.fldEmissaoFinal.getValue() );
		
		this.regraTransferenciaDisponivel.abre();
		if (this.regraTransferenciaDisponivel.getItens().isEmpty()) {
			throw new Exception("Não foram encontrados pedidos com as restrições informadas.");
		}
	}	
}

/**
 * 
 * @author Dyad
 *
 */
class GridVariaveisTransferenciaDisponivel extends VariableGrid {

	public FieldString fldItem = new FieldString(this);
	public FieldString fldOperacao = new FieldString(this);
	public FieldClassLookup fldClasseDeEntidade = new FieldClassLookup(this);
	public FieldLookup fldEntidade = new FieldLookup(this);
	public FieldSimpleDate fldEmissaoInicial = new FieldSimpleDate(this);
	public FieldSimpleDate fldEmissaoFinal = new FieldSimpleDate(this);
	
	public GridVariaveisTransferenciaDisponivel(Window window) throws Exception {
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
		
		fldEntidade.setBeanName(Entidade.class.getName());
		fldEntidade.setName("entidade");
		fldEntidade.setOrder(count++);
		
		fldClasseDeEntidade.setBeanName(Pessoa.class.getName());
		fldClasseDeEntidade.setName("classeDaEntidade");
		fldClasseDeEntidade.setOrder(count++);				

		fldEmissaoInicial.setLabel("Emissão Inicial");
		fldEmissaoInicial.setName("emissaoInicial");
		fldEmissaoInicial.setOrder(count++);

		fldEmissaoFinal.setLabel("Emissão Final");
		fldEmissaoFinal.setName("emissaoFinal");
		fldEmissaoFinal.setOrder(count++);
	}
};