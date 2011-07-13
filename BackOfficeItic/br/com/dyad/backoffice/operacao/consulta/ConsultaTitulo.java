package br.com.dyad.backoffice.operacao.consulta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
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

public class ConsultaTitulo extends ConsultaOperacao {

	public static final Integer STATUS_PENDENTE = 1;
	public static final Integer STATUS_BAIXADO = 2;
	
	private static final String ALIAS_CLASSE_BAIXAPEDIDO = "BaixaPedido";
	private static final String ALIAS_CLASSE_TITULO = "Titulo";
	private static final String ALIAS_CLASSE_ENTIDADE = "Entidade";
	private static final String ALIAS_CLASSE_ESTABELECIMENTO = "Estabelecimento";
	private static final String ALIAS_CLASSE_RECURSO = "Recurso";
	private static final String ALIAS_CLASSE_NUCLEO = "Nucleo";
	
	/**
	 * Id de títulos
	 */
	private Long tituloId;

	/**
	 * Ids de títulos
	 */
	//private ArrayList<Long> titulosIds;

	/**
	 * Id de Operações
	 */
	private Long operacaoId;

	/**
	 * Id de Operações
	 */
	private String numero;

	/**
	 * Variáveis de 
	 */
	private ParametroDeConsultaDeBeans parametroId = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_TITULO + ".id" );

	/**
	 * Variáveis de Pedidos Baixados
	 */
	private ParametroDeConsultaDeBeans parametroOperacoesId = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_TITULO + ".operacaoId" );

	/**
	 * Variáveis de Entidades
	 */
	private ParametroDeConsultaDeBeans parametroEntidade = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_TITULO + ".entidade" );

	/**
	 * Variáveis de Estabelecimentos
	 */
	private ParametroDeConsultaDeBeans parametroEstabelecimento = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_TITULO + ".estabelecimento" );

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

	private Integer status;

	//***************************************************************************
	//***************************   FIM   ***************************************
	//***************************************************************************
	
	/**
	 * Métodos Acessores
	 */
	public Long getTituloId() {
		return tituloId;
	}

	public void setTituloId(Long tituloId) {
		this.tituloId = tituloId;
	}
/*
	public ArrayList<Long> getTitulosIds() {
		return titulosIds;
	}

	public void setTitulosIds(ArrayList<Long> titulosIds) {
		this.titulosIds = titulosIds;
	}
*/
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

	public ParametroDeConsultaDeBeans getParametroId() {
		return parametroId;
	}

	public ParametroDeConsultaDeBeans getParametroOperacoesId() {
		return parametroOperacoesId;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @param dataBase
	 */
	public ConsultaTitulo(AppTransaction appTransaction) {
		super(appTransaction);
	}
	
	private String internoPegaQueryTitulo() throws Exception {
		String query = "from " + Titulo.class.getName() + " as " + ALIAS_CLASSE_TITULO;
		
		//Pegando parâmetros de consulta de beans
		String whereAux = new String();
		String joinsAux = new String();
		
		ArrayList<String> where = new ArrayList<String>();
		ArrayList<String> joins = new ArrayList<String>();
		
		if (this.getTituloId() != null) {
			where.add(" id = " + this.getTituloId());
		}
		
		if (this.getOperacaoId() != null) {
			where.add(" operacaoId = " + this.getOperacaoId() );
		}

		if (this.getNumero() != null) {
			where.add(" numero = \'" + this.getNumero() + "\'" );
		}
		
		whereAux = this.parametroId.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}
		
		whereAux = this.parametroOperacoesId.pegaFiltro();
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
			joins.add( " join " + Entidade.class.getName() + " as " + ALIAS_CLASSE_ENTIDADE + " on " + ALIAS_CLASSE_ENTIDADE + ".id = " + ALIAS_CLASSE_BAIXAPEDIDO + ".entidadeId " );
			where.add( joinsAux );
		}
		
		joinsAux = this.parametroClassesEstabelecimento.pegaFiltro();
		if ( ! joinsAux.isEmpty() ){
			joins.add( " join " + Estabelecimento.class.getName() + " as " + ALIAS_CLASSE_ESTABELECIMENTO + " on " + ALIAS_CLASSE_ESTABELECIMENTO + ".id = " + ALIAS_CLASSE_BAIXAPEDIDO + ".estabelecimentoId " );
			where.add( joinsAux );
		}
		
		SimpleDateFormat formatador = new SimpleDateFormat("MM/dd/yyyy");
		
		if (this.getEmissaoInicial() != null) {
			where.add(" emissao >= \'" + formatador.format(this.emissaoInicial) + "\'");
		}
		
		if (this.getEmissaoFinal() != null) {
			where.add(" emissao <= \'" + formatador.format(this.emissaoFinal) + "\'");
		}
		
		if (this.getStatus() != null) {
			if (this.getStatus() == ConsultaPedido.STATUS_PENDENTE) {
				where.add( 
						"(  " +
						"   Not Exists (" +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa " +
						"      Where " +
						"         Baixa.titulo = Titulo " +
						"   ) OR ( " +
						"      Select " +
						"         sum(Baixa2.total) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa2 " +
						"      Where " +
						"         Baixa2.titulo = Titulo " +
						"   ) < Titulo.total" +
						") "
				);
			} else if (this.getStatus() == ConsultaPedido.STATUS_BAIXADO) {
				where.add( 
						"   ( " +
						"      Select " +
						"         sum(Baixa2.quantidade) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa2 " +
						"      Where " +
						"         Baixa2.titulo = Titulo " +
						"   ) => Titulo.total"
				);
			}
		}

		return query + StringUtils.join(joins, " ") + " where " + StringUtils.join(where, " and ");
	}
	
	public ArrayList<Titulo> pegaTitulosAsList() throws Exception {

		DataList titulos = this.pegaTitulos();
		ArrayList<Titulo> listTitulos;

		if (titulos.isEmpty()) {
			listTitulos = new ArrayList<Titulo>();			
		} else {
			listTitulos = (ArrayList<Titulo>)titulos.getList();			
		}

		return listTitulos;
	}

	public DataList pegaTitulos() throws Exception {
		String query = this.internoPegaQueryTitulo();
		
		DataList titulos = DataListFactory.newDataList(this.getAppTransaction());
		titulos.executeQuery( query );
		
		return titulos;
	}

}