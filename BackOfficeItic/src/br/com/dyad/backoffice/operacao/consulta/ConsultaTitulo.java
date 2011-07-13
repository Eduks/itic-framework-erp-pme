package br.com.dyad.backoffice.operacao.consulta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.client.AppException;
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
	
	public static final Integer MODALIDADE_PAGAMENTO = 1;
	public static final Integer MODALIDADE_RECEBIMENTO = 2;
	
	private static final String ALIAS_CLASSE_BAIXAPEDIDO = "BaixaPedido";
	private static final String ALIAS_CLASSE_TITULO = "Titulo";
	private static final String ALIAS_CLASSE_ENTIDADE = "Entidade";
	/**
	 * Variáveis de 
	 */
	private ParametroDeConsultaDeBeans parametroId = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_TITULO + ".id" );

	/**
	 * Variáveis de Pedidos Baixados
	 */
	private ParametroDeConsultaDeBeans parametroOperacaoId = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_TITULO + ".cabecalho.id" );

	/**
	 * Variáveis de Entidades
	 */
	private ParametroDeConsultaDeBeans parametroEntidade = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_TITULO + ".entidade.id" );

	/**
	 * Variáveis de Estabelecimentos
	 */
	private ParametroDeConsultaDeBeans parametroEstabelecimento = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_TITULO + ".estabelecimento" );

	/**
	 * CLASSE DE ENTIDADE
	 */
	private ParametroDeConsultaDeBeansDeOutrasTabelas parametroClassesEntidade = new ParametroDeConsultaDeBeansDeOutrasTabelas( ALIAS_CLASSE_ENTIDADE + ".classId" );
	

	/**
	 * Número do título
	 */
	private String numero;
	private Date emissaoInicial; 
	private Date emissaoFinal;
	private Date vencimentoInicial; 
	private Date vencimentoFinal;
	private Integer status;
	private Integer modalidade;

	public ParametroDeConsultaDeBeans getParametroId() {
		return parametroId;
	}

	public ParametroDeConsultaDeBeans getParametroOperacoesId() {
		return parametroOperacaoId;
	}

	public ParametroDeConsultaDeBeans getParametroEntidade() {
		return parametroEntidade;
	}

	public ParametroDeConsultaDeBeans getParametroEstabelecimento() {
		return parametroEstabelecimento;
	}

	public ParametroDeConsultaDeBeansDeOutrasTabelas getParametroClassesEntidade() {
		return parametroClassesEntidade;
	}
	

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
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
		
	public Date getVencimentoInicial() {
		return vencimentoInicial;
	}

	public void setVencimentoInicial(Date vencimentoInicial) {
		this.vencimentoInicial = vencimentoInicial;
	}

	public Date getVencimentoFinal() {
		return vencimentoFinal;
	}

	public void setVencimentoFinal(Date vencimentoFinal) {
		this.vencimentoFinal = vencimentoFinal;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getModalidade() {
		return modalidade;
	}

	public void setModalidade(Integer modalidade) {
		this.modalidade = modalidade;
	}

	/**
	 * @param dataBase
	 */
	public ConsultaTitulo(AppTransaction appTransaction) {
		super(appTransaction);
	}
	
	private String internoPegaQueryTitulo() throws Exception {
		
		if( this.getEmissaoInicial()!= null &&
			this.getEmissaoFinal() != null &&
			this.getEmissaoInicial().after( this.getEmissaoFinal())){

			SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");				
			throw new AppException("A data de Emissão Final '" + dataFormat.format(this.getEmissaoFinal()) + "' deve ser maior ou igual a Data de Emissão Inicial '" + dataFormat.format(this.getEmissaoInicial()) + "'.");				
		}
		
		if( this.getVencimentoInicial()!= null &&
			this.getVencimentoFinal() != null &&
			this.getVencimentoInicial().after( this.getVencimentoFinal())){

			SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");				
			throw new AppException("A data de Vencimento Final '" + dataFormat.format(this.getVencimentoFinal()) + "' deve ser maior ou igual a Data de Vencimento Inicial '" + dataFormat.format(this.getVencimentoInicial()) + "'.");				
		}
		
		String query = "from " + Titulo.class.getName() + " as " + ALIAS_CLASSE_TITULO;
		
		//Pegando parâmetros de consulta de beans
		String whereAux = new String();
		String joinsAux = new String();
		
		ArrayList<String> where = new ArrayList<String>();
		ArrayList<String> joins = new ArrayList<String>();
		
		whereAux = this.parametroId.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}
		
		whereAux = this.parametroOperacaoId.pegaFiltro();
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
			joins.add( " join " + Entidade.class.getName() + " as " + ALIAS_CLASSE_ENTIDADE + " on " + ALIAS_CLASSE_ENTIDADE + ".id = " + ALIAS_CLASSE_TITULO + ".entidade.id " );
			where.add( joinsAux );
		}
				
		
		if (this.getNumero() != null) {
			where.add(" numero = '" + this.getNumero() + "'" );
		}
		
		SimpleDateFormat formatador = new SimpleDateFormat("MM/dd/yyyy");
		
		if (this.getEmissaoInicial() != null) {
			where.add(" emissao >= '" + formatador.format(this.getEmissaoInicial()) + "'");
		}
		
		if (this.getEmissaoFinal() != null) {
			where.add(" emissao <= '" + formatador.format(this.getEmissaoFinal()) + "'");
		}
		
		if (this.getVencimentoInicial() != null) {
			where.add(" vencimento >= '" + formatador.format(this.getVencimentoInicial()) + "'");
		}
		
		if (this.getVencimentoFinal() != null) {
			where.add(" vencimento <= '" + formatador.format(this.getVencimentoFinal()) + "'");
		}
		
		if ( this.getModalidade() == MODALIDADE_PAGAMENTO  ) {
			where.add(" total < 0 ");
		} else if ( this.getModalidade() == MODALIDADE_RECEBIMENTO  ) {
			where.add(" total > 0 ");
		}
		
		if (this.getStatus() != null) {
			if (this.getStatus() == STATUS_PENDENTE) {
				where.add( 
						"(  " +
						"   Not Exists (" +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa " +
						"      Where " +
						"         Baixa.tituloBaixado = Titulo " +
						"   ) OR ( " +
						"      Select " +
						"         sum(Baixa2.total) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa2 " +
						"      Where " +
						"         Baixa2.tituloBaixado = Titulo " +
						"   ) < Titulo.total" +
						") "
				);
			} else if (this.getStatus() == STATUS_BAIXADO) {
				where.add( 
						"   ( " +
						"      Select " +
						"         sum(Baixa2.quantidade) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa2 " +
						"      Where " +
						"         Baixa2.tituloBaixado = Titulo " +
						"   ) => Titulo.total"
				);
			}
		}
		if (where.isEmpty()){
			throw new AppException("Não foi definido nenhum filtro para a consulta. Para realizar a consulta informe pelo menos um parâmetro.");
		}
		
		String queryFinal = query + StringUtils.join(joins, " ") + " where " + StringUtils.join(where, " and ");
		
		return queryFinal;
	}
	
	public ArrayList<Titulo> pegaTitulosAsList() throws Exception {

		DataList titulos = this.pegaTitulos();
		ArrayList<Titulo> listTitulos;
		
		Titulo t = new Titulo();
		
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