package br.com.dyad.backoffice.operacao.consulta;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoTravamento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.navigation.persistence.HibernateUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

/**  
 * @author João Paulo
 * @since 11/03/2010 
 * @Descrição
 * Class......: ConsultaLancamento 
 * <br>Objetivo...: Classe que serve para realizar a consulta dos Lançamentos, filtrando pelos parâmetros informados
 *              na grid de variáveis da LancamentoWindow.
 * <br>Sistema....: BackOffice
 * <br>Requisito..: 3007191 - Definir e criar tabela Contabil
 * <br>Observação.: (nope)
 * <br>  
 */
public class ConsultaLancamento{	
	
	private AppTransaction appTransaction;

	private PlanoConta planoConta;
	private Entidade entidade;
	private Long contabilId;	
	private Date emissaoInicial;
	private Date emissaoFinal;
	
	private ArrayList<Object> parametrosConsulta;
	
	private HashMap<HashMap<Date,PlanoConta>, EventoTravamento> travamentosArmazenados;
	
	/*
	 * Getters and Setters
	 */
	
	public AppTransaction getAppTransaction() {
		return this.appTransaction;
	}
	
	public void setAppTransaction(AppTransaction appTransaction) {
		this.appTransaction = appTransaction;
	}
	
	public Long getContabilId() {
		return contabilId;
	}

	public void setContabilId(Long contabilId) {
		this.contabilId = contabilId;
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

	public PlanoConta getPlanoConta() {
		return planoConta;
	}

	public void setPlanoConta(PlanoConta planoConta) {
		this.planoConta = planoConta;
	}	
	
	/**	 
	 * @param appTransaction
	 * @Descrição	 
	 * Constructor 
	 */
	public ConsultaLancamento(AppTransaction appTransaction) {
		this.appTransaction = appTransaction;
		
		travamentosArmazenados = new HashMap<HashMap<Date,PlanoConta>,EventoTravamento>(0);
	}
	
	/**
	 * @return query com o filtro definido na grid de Variáveis
	 * @throws Exception
	 * @Descrição
	 * Método que serve para montar a query que irá filtrar os Lançamentos a partir dos parâmetros
	 * informados na grid de Variáveis do process.
	 */
	private String internoPegaQueryLancamento() throws Exception {
		
		String query = "from "+CabecalhoLancamento.class.getName()+" cabecalho";
		ArrayList<String> where = new ArrayList<String>();
		
		parametrosConsulta = new ArrayList<Object>();
		
		String filtro = "";
		
		if (getPlanoConta() != null || getEntidade() != null) {
			
			filtro += " cabecalho.id IN (SELECT item.cabecalho.id FROM "+ItemLancamento.class.getName()+" item WHERE 1=1";
			
			if (getPlanoConta() != null) {
				filtro += " AND item.contaContabil.planoConta.id = ?";
				parametrosConsulta.add(getPlanoConta().getId());
			}
			
			if (getEntidade() != null) {
				filtro += " AND item.entidade.id = ?";
				parametrosConsulta.add(getEntidade().getId());
			}
			
			filtro += ")";
			
			where.add(filtro);
		}
		
		//Emissão Inicial
		if (getEmissaoInicial() != null) {
			where.add(" emissao >= ?");
			parametrosConsulta.add(emissaoInicial);
		}		
		//Emissão Final
		if (getEmissaoFinal() != null) {
			where.add(" emissao <= ?");
			parametrosConsulta.add(emissaoFinal);
		}
		
		if (this.contabilId != null) {
			where.add(" id = "+contabilId);
			parametrosConsulta.add(contabilId);
		}
		
		//Verifica se foi preenchido pelo menos um parâmetro para filtrar os lançamentos, e não trazer todos os lançamentos sem filtro
		if (where.isEmpty()){
			throw new AppException("Não foi definido nenhum filtro para a consulta. Para realizar a consulta informe pelo menos um parâmetro.");
		}
		
		query = query + " where " + StringUtils.join(where, " and ") + " ORDER BY cabecalho.emissao";

		return query;
	}
			
	/**
	 * @return DataList com os lançamentos
	 * @throws Exception
	 * @Descrição
	 * Método que retorna o DataList com os Lançamentos Filtrados pelos parâmetros da Grid de variáveis.	 
	 */
	public DataList pegaLancamentos() throws Exception {
		String query = this.internoPegaQueryLancamento();	
		
		DataList lancamentos = DataListFactory.newDataList(this.getAppTransaction());
		lancamentos.setList(PersistenceUtil.executeHql((Session)getAppTransaction().getSession(), query, parametrosConsulta));
		
		return lancamentos;
	}
		
	/**
	 * @return ArrayList(ItemLancamento) dos lançamentos
	 * @throws Exception
	 * @Descrição
	 * Método que retorna um Array com os Lançamentos Filtrados pelos parâmetros da Grid de Variáveis.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<CabecalhoLancamento> pegaLancamentosAsList() throws Exception {
		DataList lancamentos = this.pegaLancamentos();
		ArrayList<CabecalhoLancamento> listLancamentos;

		if (lancamentos.isEmpty()) {
			listLancamentos = new ArrayList<CabecalhoLancamento>();			
		} else {
			listLancamentos = (ArrayList<CabecalhoLancamento>)lancamentos.getList();			
		}
		
		return listLancamentos;
	}
	
	public EventoTravamento getTravamento(CabecalhoLancamento cabecalhoLancamento) throws Exception{
//		DataList dl = DataListFactory.newDataList(this.getAppTransaction().getDatabase());
		
		EventoTravamento evento = new EventoTravamento();
		evento.setPlanoConta(cabecalhoLancamento.getItensLancamento().get(0).getContaContabil().getPlanoConta());
		evento.setData(cabecalhoLancamento.getEmissao());
		
		HashMap<Date,PlanoConta> dataPlano = new HashMap<Date,PlanoConta>(0);
		
		dataPlano.put(cabecalhoLancamento.getEmissao(), cabecalhoLancamento.getItensLancamento().get(0).getContaContabil().getPlanoConta());
		
		boolean cabecalhoJaVerificado = travamentosArmazenados.containsKey(dataPlano);
		
		if (cabecalhoJaVerificado) {
			evento = travamentosArmazenados.get(dataPlano);
		} else {
		
			String hquery = "from " + EventoTravamento.class.getName(); 
			String childrenString = HibernateUtil.getInstance(this.getAppTransaction().getDatabase()).getChildrenString(EventoTravamento.class);
			hquery += " where classId in (" + childrenString + ") ";			
	
			hquery += " and planoConta.id = ? and data < ?";
			
			parametrosConsulta = new ArrayList<Object>(0);
			
			parametrosConsulta.add(cabecalhoLancamento.getItensLancamento().get(0).getContaContabil().getPlanoConta().getId());
			
			parametrosConsulta.add(cabecalhoLancamento.getEmissao());
			
			hquery += " order by data desc";
			
			List<?> eventos = PersistenceUtil.executeHql((Session)getAppTransaction().getSession(), hquery, parametrosConsulta);
			
			if (eventos.isEmpty()) {
				evento = null;
			} else {
				evento = (EventoTravamento)eventos.get(0);
			}
			
			travamentosArmazenados.put(dataPlano, evento);
		}
		
		return evento;
		
//		dl.executeQuery(hquery, 0, 1);
//		if ( ! dl.isEmpty()){
//			EventoTravamento object = (EventoTravamento) dl.getList().get(0);
//			if ( !cabecalhoLancamento.getEmissao().after( object.getData() ) ) {
//				throw new AppException("Não é possível salvar este lançamento, pois a sua data de emissão é anterior à última data de travamento do Plano de Contas " + planoConta.toString());
//			}			
//		}
	}

	public Entidade getEntidade() {
		return entidade;
	}

	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}

}
