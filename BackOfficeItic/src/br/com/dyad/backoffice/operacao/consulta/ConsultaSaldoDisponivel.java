package br.com.dyad.backoffice.operacao.consulta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class ConsultaSaldoDisponivel extends ConsultaOperacao {

	private static final String ALIAS_CLASSE_PEDIDO = "Pedido";
	
	public static String DESIGNADOR_QUERY_PEDIDO = "PEDIDO"; 
	public static String DESIGNADOR_QUERY_EVENTO = "EVENTO";

	public static final Integer STATUS_PENDENTE = 1;
	public static final Integer STATUS_BAIXADO = 2;

	/**
	 * Vari�veis de Estabelecimentos
	 */
	private ParametroDeConsultaDeBeans parametroDisponivel = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_PEDIDO + ".estabelecimento" );

	/**
	 * Vari�veis de Emissao
	 */
	private Date emissaoInicial;
	private Date emissaoFinal;
	
	private String expressao;		
	
	/**
	 * M�todos Acessores
	 */
	public ParametroDeConsultaDeBeans getParametroEstabelecimento() {
		return parametroDisponivel;
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

	/**
	 * 
	 * @param dataBase
	 */
	public ConsultaSaldoDisponivel(AppTransaction appTransaction) {
		super(appTransaction);
	}

	private String internoPegaQuerySaldoDisponivel() throws Exception {
		String query = "SELECT " +
				       "   new ResultadoSaldoDisponivel( itemTransferenciaDisponivel.entidade, SUM(itemTransferenciaDisponivel.total) ) " +
				       "FROM " +
				       "   ItemTransferenciaDisponivel itemTransferenciaDisponivel " +
				       "WHERE " +
				       "   itemTransferenciaDisponivel IS NOT NULL " +
				       "GROUP BY " +
				       "   itemTransferenciaDisponivel.entidade";
		
		//Pegando par�metros de consulta de beans
		String whereAux = new String();
		
		ArrayList<String> where = new ArrayList<String>();
		
		whereAux = this.parametroDisponivel.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}
		
		SimpleDateFormat formatador = new SimpleDateFormat("MM/dd/yyyy");
		
		if (this.getEmissaoInicial() != null) {
			where.add(" emissao >= \'" + formatador.format(this.emissaoInicial) + "\'");
		}
		
		if (this.getEmissaoFinal() != null) {
			where.add(" emissao <= \'" + formatador.format(this.emissaoFinal) + "\'");
		}
		
		return query + StringUtils.join(where, " and ");
	}
	
	public DataList pegaSaldoDisponivel() throws Exception {
		String query = this.internoPegaQuerySaldoDisponivel();
		
		Session session = PersistenceUtil.getSession(this.getAppTransaction().getDatabase());

		SQLQuery sqlQuery = session.createSQLQuery( query );
		ArrayList list = new ArrayList(sqlQuery.list());

		
		
		DataList pedidosBaixaAutomatica = DataListFactory.newDataList(this.getAppTransaction());
		pedidosBaixaAutomatica.executeQuery( query );
		
		return pedidosBaixaAutomatica;
	}
}