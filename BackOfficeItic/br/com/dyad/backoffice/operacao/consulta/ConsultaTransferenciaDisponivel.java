package br.com.dyad.backoffice.operacao.consulta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoPedido;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.evento.Evento;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.navigation.persistence.HibernateUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;

public class ConsultaTransferenciaDisponivel extends ConsultaOperacao {

	private static final String ALIAS_CLASSE_TRANSFERENCIADISPONIVEL = "ItemTransferenciaDisponivel";
	private static final String ALIAS_CLASSE_ENTIDADE = "ENTIDADE";
	
	public static String DESIGNADOR_QUERY_PEDIDO = "PEDIDO"; 

	/**
	 * Id de pedidos
	 */
	private Long itemTransferenciaDisponivelId;

	/**
	 * Ids de pedidos
	 */
	private ArrayList<Long> itensTransferenciaDisponivelIds;

	/**
	 * Id de Operações
	 */
	private Long operacaoId;

	/**
	 * Variáveis de Entidades
	 */
	private ParametroDeConsultaDeBeans parametroEntidade = new ParametroDeConsultaDeBeans( ALIAS_CLASSE_TRANSFERENCIADISPONIVEL + ".entidade" );

	/**
	 * Variáveis de Emissao
	 */
	private Date emissaoInicial;
	private Date emissaoFinal;
	
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

	//***************************************************************************
	//***************************************************************************
	//***************************   FIM   ***************************************
	//***************************************************************************
	//***************************************************************************
	
	/**
	 * Métodos Acessores
	 */
	public Long getItemTransferenciaDisponivelId() {
		return itemTransferenciaDisponivelId;
	}

	public void setItemTransferenciaDisponivelId(Long itemTransferenciaDisponivelId) {
		this.itemTransferenciaDisponivelId = itemTransferenciaDisponivelId;
	}

	public ArrayList<Long> getItensTransferenciaDisponivelIds() {
		return itensTransferenciaDisponivelIds;
	}

	public void setItensTransferenciaDisponivelIds(ArrayList<Long> itensTransferenciaDisponivelIds) {
		this.itensTransferenciaDisponivelIds = itensTransferenciaDisponivelIds;
	}

	public Long getOperacaoId() {
		return operacaoId;
	}

	public void setOperacaoId(Long operacaoId) {
		this.operacaoId = operacaoId;
	}

	public ParametroDeConsultaDeBeans getParametroEntidade() {
		return parametroEntidade;
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

	/**
	 * 
	 * @param dataBase
	 */
	public ConsultaTransferenciaDisponivel(AppTransaction appTransaction) {
		super(appTransaction);
	}

	private String internoPegaQueryTransferenciaDisponivel() throws Exception {
		String query = "from " + ItemTransferenciaDisponivel.class.getName() + " Transferencia ";
		
		//Pegando parâmetros de consulta de beans
		String whereAux = new String();
		String joinsAux = new String();
		
		ArrayList<String> where = new ArrayList<String>();
		ArrayList<String> joins = new ArrayList<String>();
		
		if (this.getItemTransferenciaDisponivelId() != null) {
			where.add(" id = " + this.getItemTransferenciaDisponivelId());
		}
		
		if (this.getItensTransferenciaDisponivelIds() != null) {
			where.add(" id in (" + StringUtils.join(this.getItensTransferenciaDisponivelIds(), ",") + ")");
		}

		if (this.getOperacaoId() != null) {
			where.add(" operacaoId = " + this.getOperacaoId() );
		}

		whereAux = this.parametroEntidade.pegaFiltro();
		if ( ! whereAux.isEmpty()) {
			where.add(whereAux);
		}
		
		joinsAux = this.parametroClassesEntidade.pegaFiltro();
		if ( ! joinsAux.isEmpty() ){
			joins.add( " join " + Entidade.class.getName() + " " + ALIAS_CLASSE_ENTIDADE + " on " + ALIAS_CLASSE_ENTIDADE + ".id = " + ALIAS_CLASSE_TRANSFERENCIADISPONIVEL + ".entidadeId " );
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
	
	public DataList pegaTransferenciasDisponiveis() throws Exception {
		String query = this.internoPegaQueryTransferenciaDisponivel();
		DataList transferenciasDisponiveis = DataListFactory.newDataList(this.getAppTransaction());
		transferenciasDisponiveis.executeQuery( query );
		
		return transferenciasDisponiveis;
	}

	public ArrayList<ItemTransferenciaDisponivel> pegaTransferenciasDisponiveisAsList() throws Exception {
		DataList transferenciasDisponiveis = this.pegaTransferenciasDisponiveis();
		ArrayList<ItemTransferenciaDisponivel> listTransferenciasDisponiveis;

		if (transferenciasDisponiveis.isEmpty()) {
			listTransferenciasDisponiveis = new ArrayList<ItemTransferenciaDisponivel>();			
		} else {
			listTransferenciasDisponiveis = (ArrayList<ItemTransferenciaDisponivel>)transferenciasDisponiveis.getList();			
		}
		
		return listTransferenciasDisponiveis;
	}
}