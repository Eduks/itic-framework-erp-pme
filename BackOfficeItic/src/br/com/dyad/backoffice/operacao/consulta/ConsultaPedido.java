package br.com.dyad.backoffice.operacao.consulta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoPedido;
import br.com.dyad.businessinfrastructure.entidades.evento.Evento;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.navigation.persistence.HibernateUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;

/**
 * @author ${user}
 * @since ${date}
 * <hr>
 * <pre>
 * Funcao.....:
 * Sistema....: BackOffice
 * Requisito..:
 * Observação.:
 * </pre>
 */

public class ConsultaPedido extends ConsultaOperacao {

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
	 * Ids de Operações
	 */
	private ArrayList<Long> operacoesIds;

	/**
	 * Id de Operações
	 */
	private String numero;

	/**
	 * Variáveis de Classe de Entidades
	 */
	private ParametroDeConsultaDeBeans parametroClassesEntidade = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + ".entidade" + ".classId" );

	/**
	 * Variáveis de Entidades
	 */
	private ParametroDeConsultaDeBeans parametroEntidade = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + ".entidade" );

	/**
	 * Variáveis de Classe de Estabelecimentos
	 */
	private ParametroDeConsultaDeBeans parametroClassesEstabelecimento = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + "." + ALIAS_CLASSE_ESTABELECIMENTO + ".classId" );
	
	/**
	 * Variáveis de Estabelecimentos
	 */
	private ParametroDeConsultaDeBeans parametroEstabelecimento = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + ".estabelecimento" );

	/**
	 * Variáveis de Classe de Nucleos
	 */
	private ParametroDeConsultaDeBeans parametroClassesNucleo = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + "." + ALIAS_CLASSE_NUCLEO + ".classId" ); 

	/**
	 * Variáveis de Núcleos
	 */
	private ParametroDeConsultaDeBeans parametroNucleo = new ParametroDeConsultaDeBeans( "pedidoNucleo.nucleo.id" );
	
	/**
	 * Variáveis de Classe de Recursos
	 */
	private ParametroDeConsultaDeBeans parametroClassesRecurso = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + "." + ALIAS_CLASSE_RECURSO + ".classId" );

	/**
	 * Variáveis de Recurso
	 */
	private ParametroDeConsultaDeBeans parametroRecurso = new ParametroDeConsultaDeBeans( "pedidoRecurso.recurso.id" );
	
	/**
	 * Variáveis de Emissão
	 */
	private Date emissaoInicial;
	private Date emissaoFinal;
	private Boolean aprovados;
	private Boolean cancelados;
	private Integer status;
	
	private String expressao;		
	
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

	public ArrayList<Long> getOperacoesIds() {
		return operacoesIds;
	}

	public void setOperacoesIds(ArrayList<Long> operacoesIds) {
		this.operacoesIds = operacoesIds;
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

	public ParametroDeConsultaDeBeans getParametroClassesEntidade() {
		return parametroClassesEntidade;
	}

	public ParametroDeConsultaDeBeans getParametroClassesEstabelecimento() {
		return parametroClassesEstabelecimento;
	}

	public ParametroDeConsultaDeBeans getParametroClassesRecurso() {
		return parametroClassesRecurso;
	}

	public ParametroDeConsultaDeBeans getParametroClassesNucleo() {
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
	public ConsultaPedido(AppTransaction appTransaction) {
		super(appTransaction);
	}

	private String internoPegaQueryPedido() throws Exception {
		if( this.getEmissaoInicial()!= null &&
				this.getEmissaoFinal() != null &&
				this.getEmissaoInicial().after( this.getEmissaoFinal())){

			SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");				
			throw new AppException("A data de Emissão Final '" + dataFormat.format(this.getEmissaoFinal()) + "' deve ser maior ou igual a Data de Emissão Inicial '" + dataFormat.format(this.getEmissaoInicial()) + "'.");				
		}

		String query = "from " + ItemPedido.class.getName() + " " + ALIAS_CLASSE_PEDIDO;

		//Pegando parâmetros de consulta de beans
		String whereAux = new String();

		ArrayList<String> where = new ArrayList<String>();

		whereAux = " id = " + this.getPedidoId();
		if ( this.getPedidoId() != null ) {
			where.add(" exists ( from " + ItemPedido.class.getName() + " pedidoId where Pedido.operacaoId = pedidoId.operacaoId and " + whereAux + " ) ");
		}
		
		if (this.getPedidosIds() != null) {
			where.add(" id in (" + StringUtils.join(this.getPedidosIds(), ",") + ")");
		}

		if (this.getOperacaoId() != null) {
			where.add(" operacaoId = " + this.getOperacaoId() );
		}

		if (this.getOperacoesIds() != null) {
			where.add(" operacaoId in (" + StringUtils.join(this.getOperacoesIds(), ",") + ") " );
		}

		if (this.getNumero() != null) {
			where.add(" numero = \'" + this.getNumero() + "\'" );
		}
		
		whereAux = this.parametroClassesEntidade.pegaFiltro();
		if ( ! whereAux.isEmpty() ){
			where.add( whereAux );
		}
		
		whereAux = this.parametroEntidade.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}
		
		whereAux = this.parametroClassesEstabelecimento.pegaFiltro();
		if ( ! whereAux.isEmpty() ){
			where.add( whereAux );
		}

		whereAux = this.parametroEstabelecimento.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}
		
		whereAux = this.parametroClassesNucleo.pegaFiltro();
		if ( ! whereAux.isEmpty() ){
			where.add( whereAux );
		}
		
		whereAux = this.parametroNucleo.pegaFiltro();
		if ( ! whereAux.isEmpty() ){
			where.add( " exists ( from " + ItemPedido.class.getName() + " pedidoNucleo where Pedido.operacaoId = pedidoNucleo.operacaoId and " + whereAux + " ) ");
		}
		
		whereAux = this.parametroClassesRecurso.pegaFiltro();
		if ( ! whereAux.isEmpty() ){
			where.add( whereAux );
		}
		
		whereAux = this.parametroRecurso.pegaFiltro();
		if ( ! whereAux.isEmpty() ){
			where.add( " exists ( from " + ItemPedido.class.getName() + " pedidoRecurso where Pedido.operacaoId = pedidoRecurso.operacaoId and " + whereAux + " ) ");
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
						"         Baixa.itemPedidoBaixado = Pedido " +
						"   ) OR ( " +
						"      Select " +
						"         sum(Baixa2.quantidade) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido Baixa2 " +
						"      Where " +
						"         Baixa2.itemPedidoBaixado = Pedido " +
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
						"         Baixa2.itemPedidoBaixado = Pedido " +
						"   ) => Pedido.quantidade"
				);
			}
		}
		
		if (where.isEmpty()){
			throw new AppException("Não foi definido nenhum filtro para a consulta. Para realizar a consulta informe pelo menos um parâmetro.");
		}
		
		return query + " where " + StringUtils.join(where, " and ");
	}
	
	//******************************************
	public DataList pegaPedidos() throws Exception {
		String query = this.internoPegaQueryPedido();
		DataList pedidos = DataListFactory.newDataList(this.getAppTransaction());
		pedidos.executeQuery( query );
		
		return pedidos;
	}

	public ArrayList<ItemPedido> pegaPedidosAsList() throws Exception {
		DataList pedidos = this.pegaPedidos();
		ArrayList<ItemPedido> listPedidos;

		if (pedidos.isEmpty()) {
			listPedidos = new ArrayList<ItemPedido>();			
		} else {
			listPedidos = (ArrayList<ItemPedido>)pedidos.getList();			
		}
		
		return listPedidos;
	}

	public ArrayList<EventoCabecalhoPedido> pegaEventosCabecalhoPedidos(List<ItemPedido> itemPedidos) throws Exception {
		ArrayList<Long> pedidosId = new ArrayList<Long>();
		for (ItemPedido itemPedido : itemPedidos) {
			//pedidosId.add(itemPedido.getOperacaoId());
		}
		
		String query = "from " + Evento.class.getName() + " Evento where operacaoId in (" + StringUtils.join(pedidosId, ",") + ") and classId in (" + HibernateUtil.getInstance().getChildrenString( EventoCabecalhoPedido.class ) + ")";	
		
		ArrayList<EventoCabecalhoPedido> eventos;

		DataList eventosTemp = DataListFactory.newDataList(this.getAppTransaction());
		
		eventosTemp.executeQuery( query );
		
		if (eventosTemp.isEmpty()) {
			eventos = new ArrayList<EventoCabecalhoPedido>();			
		} else {
			eventos = (ArrayList<EventoCabecalhoPedido>)eventosTemp.getList();			
		}
		
		return eventos;
	}
	//******************************************

	@Override
	public void limpaParametros() {
		pedidoId = null;
		pedidosIds = null;
		operacaoId = null;
		operacoesIds = null;
		numero = null;
		parametroClassesEntidade.limpaValores();
		parametroEntidade.limpaValores();
		parametroClassesEstabelecimento.limpaValores();
		parametroEstabelecimento.limpaValores();
		parametroClassesNucleo.limpaValores();
		parametroNucleo.limpaValores();
		parametroClassesRecurso.limpaValores();
		parametroRecurso.limpaValores();
		emissaoInicial = null;
		emissaoFinal = null;
		aprovados = null;
		cancelados = null;
		status = null;
		expressao = null;
	}
}