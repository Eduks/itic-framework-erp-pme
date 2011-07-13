package br.com.dyad.backoffice.operacao.consulta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoBaixaAutomatica;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.evento.Evento;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.navigation.persistence.HibernateUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;

public class ConsultaPedidoBaixaAutomatica extends ConsultaOperacao {

	private static final String ALIAS_CLASSE_PEDIDO = "Pedido";
	private static final String ALIAS_CLASSE_ENTIDADE = "Entidade";
	private static final String ALIAS_CLASSE_ESTABELECIMENTO = "Estabelecimento";
	private static final String ALIAS_CLASSE_RECURSO = "Recurso";
	private static final String ALIAS_CLASSE_NUCLEO = "Nucleo";
	
	public static String DESIGNADOR_QUERY_PEDIDO = "PEDIDO"; 
	public static String DESIGNADOR_QUERY_EVENTO = "EVENTO";

	public static final Integer STATUS_PENDENTE = 1;
	public static final Integer STATUS_BAIXADO = 2;

	/**
	 * Id de pedidos
	 */
	private Long pedidoId;

	/**
	 * Ids de pedidos
	 */
	private ArrayList<Long> pedidosIds;

	/**
	 * Id de Operações
	 */
	private Long operacaoId;

	/**
	 * Id de Operações
	 */
	private String numero;

	/**
	 * Variáveis de Entidades
	 */
	private ParametroDeConsultaDeBeans parametroEntidade = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + ".entidade" );

	/**
	 * Variáveis de Estabelecimentos
	 */
	private ParametroDeConsultaDeBeans parametroEstabelecimento = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + ".estabelecimento" );

	/**
	 * Variáveis de Núcleos
	 */
	private ParametroDeConsultaDeBeans parametroNucleo = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + ".nucleo" );
	
	/**
	 * Variáveis de Recurso
	 */
	private ParametroDeConsultaDeBeans parametroRecurso = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + ".recurso" );
	
	/**
	 * Variáveis de Emissão
	 */
	private Date emissaoInicial;
	private Date emissaoFinal;
	private Boolean aprovados;
	private Boolean cancelados;
	private Integer status;
	
	private String expressao;		
	
	//***************************************************************************
	//***************************************************************************
	//***************************** INICIO **************************************
	//***************************************************************************
	//***************************************************************************
	
	/**
	 * CLASSE DE ENTIDADE
	 */
	private ParametroDeConsultaDeBeansDeOutrasTabelas parametroClassesEntidade = new ParametroDeConsultaDeBeansDeOutrasTabelas( ALIAS_CLASSE_ENTIDADE + ".classId" );

	/**
	 * CLASSE DE ESTABELECIMENTO
	 */
	private ParametroDeConsultaDeBeansDeOutrasTabelas parametroClassesEstabelecimento = new ParametroDeConsultaDeBeansDeOutrasTabelas( ALIAS_CLASSE_ESTABELECIMENTO + ".classId" );
	
	/**
	 * CLASSE DE RECURSO
	 */
	private ParametroDeConsultaDeBeansDeOutrasTabelas parametroClassesRecurso = new ParametroDeConsultaDeBeansDeOutrasTabelas( ALIAS_CLASSE_RECURSO + ".classId" );

	/**
	 * CLASSE DE NUCLEO
	 */
	private ParametroDeConsultaDeBeansDeOutrasTabelas parametroClassesNucleo = new ParametroDeConsultaDeBeansDeOutrasTabelas( ALIAS_CLASSE_NUCLEO + ".classId" ); 

	//***************************************************************************
	//***************************************************************************
	//***************************   FIM   ***************************************
	//***************************************************************************
	//***************************************************************************
	
	/**
	 * Métodos Acessores
	 */
	public Long getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(Long pedidoId) {
		this.pedidoId = pedidoId;
	}

	public ArrayList<Long> getPedidosIds() {
		return pedidosIds;
	}

	public void setPedidosIds(ArrayList<Long> pedidosIds) {
		this.pedidosIds = pedidosIds;
	}

	public Long getOperacaoId() {
		return operacaoId;
	}

	public void setOperacaoId(Long operacaoId) {
		this.operacaoId = operacaoId;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public ParametroDeConsultaDeBeans getParametroEntidade() {
		return parametroEntidade;
	}

	public ParametroDeConsultaDeBeans getParametroEstabelecimento() {
		return parametroEstabelecimento;
	}

	public ParametroDeConsultaDeBeans getParametroNucleo() {
		return parametroNucleo;
	}
	
	public Date getEmissaoInicial() {
		return emissaoInicial;
	}

	public void setEmissaoInicial(Date emissaoInicial) {
		this.emissaoInicial = emissaoInicial;
	}

	public Date getEmissaoFinal() {
		return emissaoFinal;
	}

	public void setEmissaoFinal(Date emissaoFinal) {
		this.emissaoFinal = emissaoFinal;
	}
	
	public String getExpressao() {
		return expressao;
	}

	public void setExpressao(String expressao) {
		this.expressao = expressao;
	}

	public ParametroDeConsultaDeBeans getParametroRecurso() {
		return parametroRecurso;
	}

	public ParametroDeConsultaDeBeansDeOutrasTabelas getParametroClassesEntidade() {
		return parametroClassesEntidade;
	}

	public ParametroDeConsultaDeBeansDeOutrasTabelas getParametroClassesEstabelecimento() {
		return parametroClassesEstabelecimento;
	}

	public ParametroDeConsultaDeBeansDeOutrasTabelas getParametroClassesRecurso() {
		return parametroClassesRecurso;
	}

	public ParametroDeConsultaDeBeansDeOutrasTabelas getParametroClassesNucleo() {
		return parametroClassesNucleo;
	}
	
	public Boolean getAprovados() {
		return aprovados;
	}

	public void setAprovados(Boolean aprovados) {
		this.aprovados = aprovados;
	}

	public Boolean getCancelados() {
		return cancelados;
	}

	public void setCancelados(Boolean cancelados) {
		this.cancelados = cancelados;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 
	 * @param dataBase
	 */
	public ConsultaPedidoBaixaAutomatica(AppTransaction appTransaction) {
		super(appTransaction);
	}

	private String internoPegaQueryPedido() throws Exception {
		String query = "from " + ItemBaixaAutomatica.class.getName() + " Pedido ";
		
		//Pegando parâmetros de consulta de beans
		String whereAux = new String();
		String joinsAux = new String();
		
		ArrayList<String> where = new ArrayList<String>();
		ArrayList<String> joins = new ArrayList<String>();
		
		if (this.getPedidoId() != null) {
			where.add(" id = " + this.getPedidoId());
		}
		
		if (this.getPedidosIds() != null) {
			where.add(" id in (" + StringUtils.join(this.getPedidosIds(), ",") + ")");
		}

		if (this.getOperacaoId() != null) {
			where.add(" operacaoId = " + this.getOperacaoId() );
		}

		if (this.getNumero() != null) {
			where.add(" numeroId = \'" + this.getNumero() + "\'" );
		}
		
		whereAux = this.parametroEntidade.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}
		
		whereAux = this.parametroEstabelecimento.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}
		
		whereAux = this.parametroNucleo.pegaFiltro();
		if ( ! whereAux.isEmpty() ) {
			where.add(whereAux);
		}
		
		joinsAux = this.parametroClassesEntidade.pegaFiltro();
		if ( ! joinsAux.isEmpty() ){
			joins.add( " join " + Entidade.class.getName() + " " + ALIAS_CLASSE_ENTIDADE + " on " + ALIAS_CLASSE_ENTIDADE + ".id = " + ALIAS_CLASSE_PEDIDO + ".entidadeId " );
			where.add( joinsAux );
		}
		
		joinsAux = this.parametroClassesEstabelecimento.pegaFiltro();
		if ( ! joinsAux.isEmpty() ){
			joins.add( " join " + Estabelecimento.class.getName() + " " + ALIAS_CLASSE_ESTABELECIMENTO + " on " + ALIAS_CLASSE_ESTABELECIMENTO + ".id = " + ALIAS_CLASSE_PEDIDO + ".estabelecimentoId " );
			where.add( joinsAux );
		}

		joinsAux = this.parametroClassesNucleo.pegaFiltro();
		if ( ! joinsAux.isEmpty() ){
			joins.add( " join " + Entidade.class.getName() + " " + ALIAS_CLASSE_NUCLEO + " on " + ALIAS_CLASSE_NUCLEO + ".id = " + ALIAS_CLASSE_PEDIDO + ".nucleoId " );
			where.add( joinsAux );
		}

		joinsAux = this.parametroClassesRecurso.pegaFiltro();
		if ( ! joinsAux.isEmpty() ){
			joins.add( " join " + Entidade.class.getName() + " " + ALIAS_CLASSE_RECURSO + " on " + ALIAS_CLASSE_RECURSO + ".id = " + ALIAS_CLASSE_PEDIDO + ".recursoId " );
			where.add( joinsAux );
		}
		
		SimpleDateFormat formatador = new SimpleDateFormat("MM/dd/yyyy");
		
		if (this.getEmissaoInicial() != null) {
			where.add(" emissao >= \'" + formatador.format(this.emissaoInicial) + "\'");
		}
		
		if (this.getEmissaoFinal() != null) {
			where.add(" emissao <= \'" + formatador.format(this.emissaoFinal) + "\'");
		}
		
		if (this.getAprovados() != null) {
			where.add(" aprovacao is " + (this.getAprovados() ? " not " : "") + " null");
		}

		if (this.getCancelados() != null) {
			where.add(" cancelamento is " + (this.getCancelados() ? " not " : "") + " null");
		}
		
		if (this.getStatus() != null) {
			if (this.getStatus() == ConsultaPedido.STATUS_PENDENTE) {
				where.add( 
						"(  " +
						"   Not Exists (" +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido Baixa " +
						"      Where " +
						"         Baixa.pedidoBaixado = Pedido " +
						"   ) OR ( " +
						"      Select " +
						"         sum(Baixa2.quantidade) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido Baixa2 " +
						"      Where " +
						"         Baixa2.pedidoBaixado = Pedido " +
						"   ) < Pedido.quantidade" +
						") "
				);
			} else if (this.getStatus() == ConsultaPedido.STATUS_BAIXADO) {
				where.add( 
						"   ( " +
						"      Select " +
						"         sum(Baixa2.quantidade) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacoes.ItemBaixaPedido Baixa2 " +
						"      Where " +
						"         Baixa2.pedidoBaixado = Pedido " +
						"   ) => Pedido.quantidade"
				);
			}
		}
		
		return query + StringUtils.join(joins, " ")+ " where " + StringUtils.join(where, " and ");
	}
	
	private String internoPegaQueryEvento( String queryPedido ) throws Exception {
		String query = "from " + Evento.class.getName() + " Evento where operacaoId in ( select operacaoId " + queryPedido + ") and classId in (" + HibernateUtil.getInstance().getChildrenString( EventoCabecalhoPedido.class ) + ")";	
		return query;
	}

	//******************************************
	public DataList pegaPedidosBaixaAutomatica() throws Exception {
		String query = this.internoPegaQueryPedido();
		DataList pedidosBaixaAutomatica = DataListFactory.newDataList(this.getAppTransaction());
		pedidosBaixaAutomatica.executeQuery( query );
		
		return pedidosBaixaAutomatica;
	}

	public ArrayList<ItemBaixaAutomatica> pegaPedidosAsList() throws Exception {
		DataList pedidosBaixaAutomatica = this.pegaPedidosBaixaAutomatica();
		ArrayList<ItemBaixaAutomatica> listPedidos;

		if (pedidosBaixaAutomatica.isEmpty()) {
			listPedidos = new ArrayList<ItemBaixaAutomatica>();			
		} else {
			listPedidos = (ArrayList<ItemBaixaAutomatica>)pedidosBaixaAutomatica.getList();			
		}
		
		return listPedidos;
	}

	public ArrayList<EventoCabecalhoBaixaAutomatica> pegaEventosCabecalhoPedidosBaixaAutomatica(List<ItemBaixaAutomatica> itensPedidosBaixaAutomatica) throws Exception {
		
		ArrayList<Long> pedidosId = new ArrayList<Long>();
		for (ItemBaixaAutomatica itemPedidoBaixaUnited : itensPedidosBaixaAutomatica) {
			//pedidosId.add(itemPedidoBaixaUnited.getOperacaoId());
		}
		
		String query = "from " + Evento.class.getName() + " Evento where operacaoId in (" + StringUtils.join(pedidosId, ",") + ") and classId in (" + HibernateUtil.getInstance().getChildrenString( EventoCabecalhoBaixaAutomatica.class ) + ")";	
		
		ArrayList<EventoCabecalhoBaixaAutomatica> eventos;

		DataList eventosTemp = DataListFactory.newDataList(this.getAppTransaction());
		
		eventosTemp.executeQuery( query );
		
		if (eventosTemp.isEmpty()) {
			eventos = new ArrayList<EventoCabecalhoBaixaAutomatica>();			
		} else {
			eventos = (ArrayList<EventoCabecalhoBaixaAutomatica>)eventosTemp.getList();			
		}
		
		return eventos;
	}
	//******************************************
}