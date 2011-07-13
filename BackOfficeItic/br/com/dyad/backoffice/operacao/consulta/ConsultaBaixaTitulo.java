package br.com.dyad.backoffice.operacao.consulta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoBaixaPedido;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.evento.Evento;
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

public class ConsultaBaixaTitulo extends ConsultaOperacao {

	private static final String ALIAS_CLASSE_BAIXATITULO = "BaixaTitulo";
	private static final String ALIAS_CLASSE_PEDIDO = "Pedido";
	private static final String ALIAS_CLASSE_ENTIDADE = "Entidade";
	private static final String ALIAS_CLASSE_ESTABELECIMENTO = "Estabelecimento";
	private static final String ALIAS_CLASSE_RECURSO = "Recurso";
	private static final String ALIAS_CLASSE_NUCLEO = "Nucleo";

	public static String DESIGNADOR_QUERY_PEDIDO = "PEDIDO"; 
	public static String DESIGNADOR_QUERY_TITULO = "TITULO";
	public static String DESIGNADOR_QUERY_EVENTO = "EVENTO";

	private AppTransaction transactionalSession;

	/**
	 * Id de pedidos
	 */
	private Long pedidoId;

	/**
	 * Id de Operações
	 */
	private Long operacaoId;

	/**
	 * Id de Operações
	 */
	private String numero;

	/**
	 * Variáveis de Pedidos Baixados
	 */
	private ParametroDeConsultaDeBeans parametroTituloBaixado = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_BAIXATITULO + ".titulo" );

	/**
	 * Variáveis de Entidades
	 */
	private ParametroDeConsultaDeBeans parametroEntidade = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_BAIXATITULO + ".entidade" );

	/**
	 * Variáveis de Estabelecimentos
	 */
	private ParametroDeConsultaDeBeans parametroEstabelecimento = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_BAIXATITULO + ".estabelecimento" );

	/**
	 * Variáveis de Emissao
	 */
	private Date emissaoInicial;
	private Date emissaoFinal;

	private String expressao;		

	//***************************************************************************
	//*************************** INICIO ****************************************
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
	//***************************   FIM   ***************************************
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

	public ParametroDeConsultaDeBeans getParametroTituloBaixado() {
		return parametroTituloBaixado;
	}

	public ParametroDeConsultaDeBeans getParametroEntidade() {
		return parametroEntidade;
	}

	public ParametroDeConsultaDeBeans getParametroEstabelecimento() {
		return parametroEstabelecimento;
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

	public AppTransaction getTransactionalSession() {
		return transactionalSession;
	}

	public void setTransactionalSession(AppTransaction transactionalSession) {
		this.transactionalSession = transactionalSession;
	}

	/**
	 * 
	 * @param dataBase
	 */
	public ConsultaBaixaTitulo(AppTransaction appTransaction ) {
		super(appTransaction);
	}

	private String internoPegaQueryBaixaTitulo() throws Exception {
		String query = "from " + ItemBaixaTitulo.class.getName() + " as BaixaTitulo ";

		//Pegando parâmetros de consulta de beans
		String whereAux = new String();
		String joinsAux = new String();

		ArrayList<String> where = new ArrayList<String>();
		ArrayList<String> joins = new ArrayList<String>();

		if (this.getPedidoId() != null) {
			where.add(" id = " + this.getPedidoId());
		}

		if (this.getOperacaoId() != null) {
			where.add(" operacaoId = " + this.getOperacaoId() );
		}

		if (this.getNumero() != null) {
			where.add(" numeroId = \'" + this.getNumero() + "\'" );
		}

		whereAux = this.parametroTituloBaixado.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}

		whereAux = this.parametroEntidade.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}

		whereAux = this.parametroEstabelecimento.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}

		joinsAux = this.parametroClassesEntidade.pegaFiltro();
		if ( ! joinsAux.isEmpty() ){
			joins.add( " join " + Entidade.class.getName() + " as " + ALIAS_CLASSE_ENTIDADE + " on " + ALIAS_CLASSE_ENTIDADE + ".id = " + ALIAS_CLASSE_BAIXATITULO + ".entidadeId " );
			where.add( joinsAux );
		}

		joinsAux = this.parametroClassesEstabelecimento.pegaFiltro();
		if ( ! joinsAux.isEmpty() ){
			joins.add( " join " + Estabelecimento.class.getName() + " as " + ALIAS_CLASSE_ESTABELECIMENTO + " on " + ALIAS_CLASSE_ESTABELECIMENTO + ".id = " + ALIAS_CLASSE_BAIXATITULO + ".estabelecimentoId " );
			where.add( joinsAux );
		}

		joinsAux = this.parametroClassesNucleo.pegaFiltro();
		if ( ! joinsAux.isEmpty() ){
			joins.add( " join " + Entidade.class.getName() + " as " + ALIAS_CLASSE_NUCLEO + " on " + ALIAS_CLASSE_NUCLEO + ".id = " + ALIAS_CLASSE_BAIXATITULO + ".nucleoId " );
			where.add( joinsAux );
		}

		joinsAux = this.parametroClassesRecurso.pegaFiltro();
		if ( ! joinsAux.isEmpty() ){
			joins.add( " join " + Entidade.class.getName() + " as " + ALIAS_CLASSE_RECURSO + " on " + ALIAS_CLASSE_RECURSO + ".id = " + ALIAS_CLASSE_BAIXATITULO + ".recursoId " );
			where.add( joinsAux );
		}

		SimpleDateFormat formatador = new SimpleDateFormat("MM/dd/yyyy");

		if (this.getEmissaoInicial() != null) {
			where.add(" emissao >= \'" + formatador.format(this.emissaoInicial) + "\'");
		}

		if (this.getEmissaoFinal() != null) {
			where.add(" emissao <= \'" + formatador.format(this.emissaoFinal) + "\'");
		}

		return query + StringUtils.join(joins, " ")+ " where " + StringUtils.join(where, " and ");
	}

	public DataList pegaBaixasTitulo() throws Exception {
		String query = this.internoPegaQueryBaixaTitulo();

		DataList baixasTitulos = DataListFactory.newDataList(this.getAppTransaction());

		baixasTitulos.executeQuery( query );

		return baixasTitulos;
	}

	public ArrayList<ItemBaixaTitulo> pegaBaixasTituloAsList() throws Exception {
		DataList baixasTitulo = this.pegaBaixasTitulo();
		ArrayList<ItemBaixaTitulo> listBaixas;

		if (baixasTitulo.isEmpty()) {
			listBaixas = new ArrayList<ItemBaixaTitulo>();			
		} else {
			listBaixas = (ArrayList<ItemBaixaTitulo>)baixasTitulo.getList();			
		}

		return listBaixas;
	}
}
