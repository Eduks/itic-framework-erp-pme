package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.auxiliares.BalanceteComparativoReportBean;
import br.com.dyad.backoffice.entidade.auxiliares.BalanceteReportBean;
import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class RegraBalancete {
	
	private String dataBase;	
	private PlanoConta planoConta;
	private ContaContabil contaRestricao;
	private ContaContabil contaInicial;
	private ContaContabil contaFinal;
	private Date dataInicial;
	private Date dataFinal;
	private Boolean contaZerada;
	private Boolean comparativo;
	private Boolean houveAlteracao;
	
	private DataList balancetes;	
	private DataList contasContabeis; 
	
	private ArrayList<Date> meses;
	
	private ArrayList<Object> parametrosConsulta;
	
	public ArrayList<Date> getMeses() {
		return meses;
	}
	public PlanoConta getPlanoConta() {
		return planoConta;
	}
	public void setPlanoConta(PlanoConta planoConta) {
		if ( this.planoConta == null || !this.planoConta.equals(planoConta)){
			this.planoConta = planoConta;
			this.setHouveAlteracao(true);
		}		
	}
	public ContaContabil getContaRestricao() {
		return contaRestricao;
	}
	public void setContaRestricao(ContaContabil contaRestricao) {
		if ( this.contaRestricao != null ){
			if (contaRestricao == null) {
				this.setHouveAlteracao(true);
			} else {
				String codigoOriginal = this.contaRestricao.getCodigo();
				String novoCodigo = contaRestricao.getCodigo();
				if ( !novoCodigo.startsWith(codigoOriginal)){
					this.setHouveAlteracao(true);
				}		
			}
		}
		this.contaRestricao = contaRestricao;	
	}
	public ContaContabil getContaInicial() {
		return contaInicial;
	}
	public void setContaInicial(ContaContabil contaInicial) {
		if (contaInicial == null) {
			this.setHouveAlteracao(true);
		} else {
			if ( this.contaInicial != null && contaInicial.getCodigo().compareTo(this.contaInicial.getCodigo()) < 0 ) {
				this.setHouveAlteracao(true);
			}
		}
		this.contaInicial = contaInicial;
	}
	public ContaContabil getContaFinal() {
		return contaFinal;
	}
	public void setContaFinal(ContaContabil contaFinal) {
		if (contaFinal == null){
			this.setHouveAlteracao(true);
		} else { 
			if ( this.contaFinal != null && contaFinal.getCodigo().compareTo(this.contaFinal.getCodigo()) > 0 ) {
				this.setHouveAlteracao(true);
			}
		}
		this.contaFinal = contaFinal;
	}
	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		if (this.dataInicial != null && dataInicial.before(this.dataInicial)){
			this.setHouveAlteracao(true);
		}
		this.dataInicial = dataInicial;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		if (this.dataFinal != null && dataFinal.after(this.dataFinal)){
			this.setHouveAlteracao(true);
		}
		this.dataFinal = dataFinal;
	}
	public Boolean getContaZerada() {
		return contaZerada;
	}
	public void setContaZerada(Boolean contaZerada) {
		if (contaZerada == null){
			this.contaZerada = false;
		} else {
			this.contaZerada = contaZerada;
		}
	}
	public Boolean getComparativo() {
		return comparativo;
	}
	public void setComparativo(Boolean comparativo) {
		this.comparativo = comparativo;
	}
	public String getDataBase() {
		return dataBase;
	}
	public void setDataBase(String dataBase) {
		this.dataBase = dataBase;
	}	
	public Boolean getHouveAlteracao() {
		return houveAlteracao;
	}
	public void setHouveAlteracao(Boolean houveAlteracao) {
		this.houveAlteracao = houveAlteracao;
	}
	/**
	 * @param dataBase
	 * @Descrição
	 * Constructor
	 */
	public RegraBalancete(String dataBase){
		this.setDataBase(dataBase);
		this.balancetes = DataListFactory.newDataList(dataBase);
		this.balancetes.setLogChanges(false);
		this.balancetes.setCommitOnSave(false);
		this.contasContabeis = DataListFactory.newDataList(dataBase);
		this.contasContabeis.setLogChanges(false);
		this.contasContabeis.setCommitOnSave(false);
		this.setHouveAlteracao(false);
	}
	
	private void carregaLancamentos() throws Exception{
		
		if ( this.getHouveAlteracao()) {
		
			this.balancetes.empty();			
			
			String hquery = "from " + ItemLancamento.class.getName();
			
			ArrayList<String> where = this.filtroBalancetes();
			if ( !where.isEmpty()){
				hquery += " where " + StringUtils.join(where, " and ");			
			}
			DataList lancamentos = DataListFactory.newDataList(this.getDataBase());
			
			List lista = PersistenceUtil.executeHql((Session)lancamentos.getTransactionalSession().getSession(), hquery, parametrosConsulta);
			
			lancamentos.setList(lista);
			
			List itens = lancamentos.getList();
			
			for (Iterator iterator = itens.iterator(); iterator.hasNext(); ) {
				ItemLancamento item = (ItemLancamento) iterator.next();	
				
				if ( this.getComparativo() ){
					if (this.meses == null ){		
						this.meses = this.retornaMesesEntrePeriodo(this.getDataInicial(), this.getDataFinal());
					}
					this.balancetes.add(this.criaBalanceteComparativoBean(item));
				}else {
					this.balancetes.add(this.criaBalanceteBean(item));
				}
			}
		} else {			
			ArrayList<String> filtro = this.filtroBalancetes();	
			if (this.getComparativo()){
				this.balancetes.setList(this.balancetes.filter(BalanceteComparativoReportBean.class, " where " + StringUtils.join(filtro, " and ") ));
			} else {
				this.balancetes.setList(this.balancetes.filter(BalanceteReportBean.class, " where " + StringUtils.join(filtro, " and ") ));
			}
		}		
		this.setHouveAlteracao(false);		
	}	
	
	/**
	 * @return ArrayList(String) where da pesquisa dos lançamentos
	 * @Descrição
	 * Método que retorna um ArrayList com as condições designadas pelo filtro do usuário, na grid de variáveis.
	 * Que será usado para filtrar os ItemLancamento.	  
	 */
	private ArrayList<String> filtroBalancetes(){
		ArrayList<String> where = new ArrayList<String>();
		
		parametrosConsulta = new ArrayList<Object>(0);
		
		if (this.getPlanoConta() != null){
			where.add(" contaContabil.planoConta.id = ?");
			parametrosConsulta.add(getPlanoConta().getId());
		}
		if (this.getContaRestricao() != null){
			where.add(" contaContabil.codigo like ?");
			parametrosConsulta.add(getContaRestricao().getCodigo()+"%");
		}
		if (this.getContaInicial() != null){
			where.add(" contaContabil.codigo >= ?");
			parametrosConsulta.add(getContaInicial().getCodigo());
		}
		if (this.getContaFinal() != null){
			where.add(" contaContabil.codigo <= ?");
			parametrosConsulta.add(getContaFinal().getCodigo());
		}
		if (this.getDataInicial() != null){
			where.add(" emissao <= ?");
			parametrosConsulta.add(getDataInicial());
		}
		if (this.getDataFinal() != null){
			where.add(" emissao <= ?");
			parametrosConsulta.add(getDataFinal());
		}
		return where;
	}	
	
	public DataList retornaBalancete() throws Exception{
		
		this.carregaLancamentos();
		this.balancetes.sort("conta");
		
		DataList contasAnaliticas = DataListFactory.newDataList(this.getDataBase());
		
		DataList retornoBalancete = DataListFactory.newDataList(this.getDataBase());
		List itens = this.balancetes.getList();
		
		if (this.getComparativo()){
						
			BalanceteComparativoReportBean contaAnalitica = null;	
			
			for (Iterator iterator = itens.iterator(); iterator.hasNext(); ) {
				BalanceteComparativoReportBean item = (BalanceteComparativoReportBean) iterator.next();
				
				if (contaAnalitica == null || !contaAnalitica.getConta().equals(item.getConta())) {
					if ( contaAnalitica != null ){
						contasAnaliticas.add(contaAnalitica);
					}
					contaAnalitica = item;
					contaAnalitica.setNome(this.identaConta(contaAnalitica.getConta()) +  contaAnalitica.getNome());
				} else {
					contaAnalitica.setSaldoInicial(contaAnalitica.getSaldoInicial().add(item.getSaldoInicial()));
					contaAnalitica.setSaldo_0(contaAnalitica.getSaldo_0().add(item.getSaldo_0()));
					contaAnalitica.setSaldo_1(contaAnalitica.getSaldo_1().add(item.getSaldo_1()));
					contaAnalitica.setSaldo_2(contaAnalitica.getSaldo_2().add(item.getSaldo_2()));
					contaAnalitica.setSaldo_3(contaAnalitica.getSaldo_3().add(item.getSaldo_3()));
					contaAnalitica.setSaldo_4(contaAnalitica.getSaldo_4().add(item.getSaldo_4()));
					contaAnalitica.setSaldo_5(contaAnalitica.getSaldo_5().add(item.getSaldo_5()));
					contaAnalitica.setSaldo_6(contaAnalitica.getSaldo_6().add(item.getSaldo_6()));
					contaAnalitica.setSaldo_7(contaAnalitica.getSaldo_7().add(item.getSaldo_7()));
					contaAnalitica.setSaldo_8(contaAnalitica.getSaldo_8().add(item.getSaldo_8()));
					contaAnalitica.setSaldo_9(contaAnalitica.getSaldo_9().add(item.getSaldo_9()));
					contaAnalitica.setSaldo_10(contaAnalitica.getSaldo_10().add(item.getSaldo_10()));
					contaAnalitica.setSaldo_11(contaAnalitica.getSaldo_11().add(item.getSaldo_11()));
					contaAnalitica.setSaldo_12(contaAnalitica.getSaldo_12().add(item.getSaldo_12()));
					contaAnalitica.setSaldo_13(contaAnalitica.getSaldo_13().add(item.getSaldo_13()));
					contaAnalitica.setSaldo_14(contaAnalitica.getSaldo_14().add(item.getSaldo_14()));
					contaAnalitica.setSaldo_15(contaAnalitica.getSaldo_15().add(item.getSaldo_15()));
					contaAnalitica.setSaldo_16(contaAnalitica.getSaldo_16().add(item.getSaldo_16()));
					contaAnalitica.setSaldo_17(contaAnalitica.getSaldo_17().add(item.getSaldo_17()));
					contaAnalitica.setSaldo_18(contaAnalitica.getSaldo_18().add(item.getSaldo_18()));
					contaAnalitica.setSaldo_19(contaAnalitica.getSaldo_19().add(item.getSaldo_19()));
					contaAnalitica.setSaldo_20(contaAnalitica.getSaldo_20().add(item.getSaldo_20()));
					contaAnalitica.setSaldo_21(contaAnalitica.getSaldo_21().add(item.getSaldo_21()));
					contaAnalitica.setSaldo_22(contaAnalitica.getSaldo_22().add(item.getSaldo_22()));
					contaAnalitica.setSaldo_23(contaAnalitica.getSaldo_23().add(item.getSaldo_23()));									
				}		
			}		
			contasAnaliticas.add(contaAnalitica);
			
			this.geraBeansContaSintetica(contasAnaliticas);		
			
			retornoBalancete = this.adicionaLinhas(contasAnaliticas);
			
			return retornoBalancete;
		} else {		
					
			BalanceteReportBean contaAnalitica = null;	
			
			for (Iterator iterator = itens.iterator(); iterator.hasNext(); ) {
				BalanceteReportBean item = (BalanceteReportBean) iterator.next();
				
				if (contaAnalitica == null || !contaAnalitica.getConta().equals(item.getConta())) {
					if ( contaAnalitica != null ){
						contasAnaliticas.add(contaAnalitica);
					}
					contaAnalitica = item;
					contaAnalitica.setNome(this.identaConta(contaAnalitica.getConta()) +  contaAnalitica.getNome());
				} else {
					contaAnalitica.setSaldoInicial(contaAnalitica.getSaldoInicial().add(item.getSaldoInicial()));
					contaAnalitica.setCredito(contaAnalitica.getCredito().add(item.getCredito()));
					contaAnalitica.setDebito(contaAnalitica.getDebito().add(item.getDebito()));
					contaAnalitica.setSaldoFinal(contaAnalitica.getSaldoFinal().add(item.getSaldoFinal()));				
				}		
			}		
			contasAnaliticas.add(contaAnalitica);
			
			this.geraBeansContaSintetica(contasAnaliticas);		
			
			retornoBalancete = this.adicionaLinhas(contasAnaliticas);
			
			return retornoBalancete;
		}
	}	
	
	/**
	 * @param codigoConta
	 * @return ArrayList(ContaContabil)
	 * @Descrição
	 * Método que retorna um ArrayList contendo todos as Contas Sintéticas que sejam superior, hierarquicamente,  a conta passada como parâmetro.
	 */
	private ArrayList<ContaContabil> retornaContasSinteticas(String codigoConta){
		
		ArrayList<ContaContabil> retornoContas = new ArrayList<ContaContabil>();
		
		if ( this.contasContabeis.isEmpty() ){
			
			String hquery = " from " + ContaContabil.class.getName();
			if (this.getPlanoConta() != null){
				hquery += " where planoConta.id = " + this.getPlanoConta().getId();
			}
			this.contasContabeis.executeQuery(hquery);			
		}
		
		this.contasContabeis.sort("codigo");
		
		List contas = this.contasContabeis.getList();		
		
		for (Iterator iterator = contas.iterator(); iterator.hasNext(); ) {
			ContaContabil conta = (ContaContabil) iterator.next();
			
			if ( codigoConta.startsWith(conta.getCodigo()) && !codigoConta.equals(conta.getCodigo())){
				retornoContas.add(conta);
			}		
		}
		return retornoContas;		
	}
	
	/**
	 * @param codigoConta
	 * @param contas
	 * @Descrição
	 * Método que adiciona a ArrayList contas, todas as contas sintéticas superiores hierarquicamente a codigoConta, 
	 * mas que ainda não existem em contas, evitando uma duplicidade das contas. 
	 */
	private void retornaContasSinteticas( String codigoConta, ArrayList<ContaContabil> contas){
		
		ArrayList<ContaContabil> retornoContas = this.retornaContasSinteticas(codigoConta);				
		for (Iterator iterator = retornoContas.iterator(); iterator.hasNext(); ) {
			ContaContabil conta = (ContaContabil) iterator.next();
			
			if ( codigoConta.startsWith(conta.getCodigo()) && !codigoConta.equals(conta.getCodigo()) && !contas.contains(conta) ){
				contas.add(conta);
			}		
		}		
	}
	
	/**
	 * @param codigoConta
	 * @return String
	 * @Descrição
	 * Método que retorna a identação da conta, dependendo do seu nível hierárquico.
	 * Usado apenas para identar o nome das contas no Relatório de Balancete. 
	 */
	private String identaConta( String codigoConta){
		
		String identacao = "";		
		if ( this.contasContabeis.isEmpty() ){			
			String hquery = " from " + ContaContabil.class.getName();
			if (this.getPlanoConta() != null){
				hquery += " where planoConta.id = " + this.getPlanoConta().getId();
			}
			this.contasContabeis.executeQuery(hquery);			
		}		
		this.contasContabeis.sort("codigo");		
		List contas = this.contasContabeis.getList();	
		for (Iterator iterator = contas.iterator(); iterator.hasNext(); ) {
			ContaContabil conta = (ContaContabil) iterator.next();
			
			if ( codigoConta.startsWith(conta.getCodigo()) && !codigoConta.equals(conta.getCodigo())){
				identacao += "     ";
			}
			if (conta.getCodigo().compareTo(codigoConta) > 0){
				break;
			}			
		}		
		return identacao + " ";		
	}
	
	/**
	 * @param listaBalancete
	 * @return DataList
	 * @throws Exception
	 * @Descrição
	 * Método que adiciona linhas de formatação no relatório de Balancete, sempre após a alteração de
	 * grupo de uma conta Sintética para outra conta Sintética.
	 */
	private DataList adicionaLinhas( DataList listaBalancete ) throws Exception{
		
		DataList retornoBalancete = DataListFactory.newDataList(this.getDataBase());		
		listaBalancete.sort("conta");
		
		List itens = listaBalancete.getList();				
		Long ordem = 0L;
		
		if (this.getComparativo()){
			BalanceteComparativoReportBean auxiliar = null;
			for (Iterator iterator = itens.iterator(); iterator.hasNext(); ) {
				BalanceteComparativoReportBean item = (BalanceteComparativoReportBean) iterator.next();			
				if ( item.getContaContabil().getSintetico() && !ordem.equals( new Long(0))){
					auxiliar = new BalanceteComparativoReportBean();				
					auxiliar.setId(ordem + 10000);
					auxiliar.setOrdem(++ordem);
					retornoBalancete.add(auxiliar);
				}
				item.setOrdem(++ordem);
				retornoBalancete.add(item);			
			}
			
		} else {
			BalanceteReportBean auxiliar = null;
			for (Iterator iterator = itens.iterator(); iterator.hasNext(); ) {
				BalanceteReportBean item = (BalanceteReportBean) iterator.next();			
				if ( item.getContaContabil().getSintetico() && !ordem.equals( new Long(0))){
					auxiliar = new BalanceteReportBean();				
					auxiliar.setId(ordem + 10000);
					auxiliar.setOrdem(++ordem);
					retornoBalancete.add(auxiliar);
				}
				item.setOrdem(++ordem);
				retornoBalancete.add(item);			
			}
		}
		return retornoBalancete;
	}
	
	/**
	 * @param item
	 * @return BalanceteReportBean
	 * @Descrição
	 * Método que cria um bean específico para o relatório de Balancete a partir do ItemLancamento passado como parâmetro.
	 */
	private BalanceteReportBean criaBalanceteBean( ItemLancamento item ){
		
		BalanceteReportBean balanceteBean = new BalanceteReportBean();
		balanceteBean.setContaContabil(item.getContaContabil());
		balanceteBean.setId(item.getId());
		//balanceteBean.setOperacaoId(item.getOperacaoId());
		balanceteBean.setValor(item.getValor());
		balanceteBean.setEmissao(item.getEmissao());
		balanceteBean.setEntidade(item.getEntidade());
		
		balanceteBean.setConta(balanceteBean.getContaContabil().getCodigo());
		balanceteBean.setNome(balanceteBean.getContaContabil().getNome());
		/* Verifica se a emissão do Lançamento é anterior a data Inicial da Pesquisa, se for preenche o principal
		 * no Campo Saldo Inicial, se não for, preenche no campo Débito ou Crédito, dependendo do sinal do principal.
		 */
		if( balanceteBean.getEmissao().before( this.getDataInicial())){
			balanceteBean.setSaldoInicial(balanceteBean.getValor());
			balanceteBean.setDebito(new BigDecimal(0).setScale(2));
			balanceteBean.setCredito(new BigDecimal(0).setScale(2));
		} else {
			balanceteBean.setSaldoInicial(new BigDecimal(0).setScale(2));
			if ( balanceteBean.getValor().signum() < 0 ){
				balanceteBean.setDebito(new BigDecimal(0).setScale(2));
				balanceteBean.setCredito(balanceteBean.getValor().abs());
			} else {
				balanceteBean.setDebito(balanceteBean.getValor().abs());
				balanceteBean.setCredito(new BigDecimal(0).setScale(2));
			}			
		}
		/* Calcula o valor do saldo final a partir dos dados do Saldo Inicial, Débito e Crédito.
		 * SF = SI + D - C
		 */
		balanceteBean.setSaldoFinal(balanceteBean.getSaldoInicial().add(balanceteBean.getDebito().add(balanceteBean.getCredito().negate())));
		
		return balanceteBean;		
	}
	
	/**
	 * @param item
	 * @return BalanceteComparativoReportBean
	 * @Descrição
	 * Método que cria um bean específico para o relatório de Balancete (Modo Comparativo de Valores) a partir do ItemLancamento passado como parâmetro.
	 */	
	private BalanceteComparativoReportBean criaBalanceteComparativoBean( ItemLancamento item ){
		
		BalanceteComparativoReportBean balanceteBean = new BalanceteComparativoReportBean();
		balanceteBean.setContaContabil(item.getContaContabil());
		balanceteBean.setId(item.getId());
		//balanceteBean.setOperacaoId(item.getOperacaoId());
		balanceteBean.setValor(item.getValor());
		balanceteBean.setEmissao(item.getEmissao());
		balanceteBean.setEntidade(item.getEntidade());		
		balanceteBean.setConta(balanceteBean.getContaContabil().getCodigo());						
		balanceteBean.setNome(balanceteBean.getContaContabil().getNome());		
		/* Inicializa os saldos do bean
		 */
		this.inicializaSaldos(balanceteBean);
		/* Verifica se a emissão do Lançamento é anterior a data Inicial da Pesquisa, se for preenche o principal
		 * no Campo Saldo Inicial, se não for, preenche os demais saldos com o valor do principal.
		 */
		if( balanceteBean.getEmissao().before( this.getDataInicial())){
			balanceteBean.setSaldoInicial(balanceteBean.getValor());
			this.replicaSaldos( 0, this.meses.size() - 1, balanceteBean, balanceteBean.getSaldoInicial());
		} else {			
			this.replicaSaldos( 0, this.meses.size() - 1, balanceteBean, balanceteBean.getSaldoInicial());
			int i = 0;
			for (Date mes : this.meses) {				
				Date mesPosterior = (Date)mes.clone();
				Date emissao = balanceteBean.getEmissao();
				CalendarUtil.addMonthsToDate(mesPosterior, 1);
				this.zeraHora(mes);
				this.zeraHora(mesPosterior);				
				if (!emissao.before(mes) && emissao.before(mesPosterior)){					
					this.replicaSaldos(i, this.meses.size() - 1, balanceteBean, balanceteBean.getValor());
					break;
				}
				i++;
			}
		}		
		return balanceteBean;		
	}
		
	/**
	 * @param dataInicial
	 * @param dataFinal
	 * @return ArrayList (Date)
	 * @Descrição
	 * Método que retorna os meses que estão compreendidos entre as datas passadas como parâmetro, 
	 * inclusive o mês em que as datas limites pertençam.
	 * @Exemplo 
	 * dataInicial = '01/01/2010'
	 * <br>dataFinal = '01/04/2010'
	 * <br> Return meses = [ '01/01/2010', '01/02/2010', '01/03/2010', '01/04/2010' ] 
	 */
	private ArrayList<Date> retornaMesesEntrePeriodo( Date dataInicial, Date dataFinal ){	
		
		ArrayList<Date> meses = new ArrayList<Date>();
		Date data = (Date) dataInicial.clone();		
		Date dFinal = (Date) dataFinal.clone();				
		CalendarUtil.setToFirstDayOfMonth(data);
		this.zeraHora(data);
		this.zeraHora(dFinal);
		for ( Date d = data; !d.after(dFinal); CalendarUtil.addMonthsToDate(d, 1)) {			
			meses.add((Date) d.clone());
		}		
		return meses;
	}
	
	/**
	 * @param data
	 * @Descrição
	 * Método que zera a hora de uma data. Útil para fazer comparação entre datas onde não se precise considerar a hora. 
	 */
	@SuppressWarnings("deprecation")
	private void zeraHora (Date data){
		data.setHours(0);
		data.setMinutes(0);
		data.setSeconds(0);
	}
	
	/**
	 * @param balanceteBean
	 * @Descrição
	 * Método que inicializa os saldos de um bean do relatório de Balancete Modo Comparativo. 
	 * Evitando o problema de os campos não estarem definidos, pois o preenchimento dos mesmo é feito de forma acumulativa.  
	 */
	private void inicializaSaldos(BalanceteComparativoReportBean balanceteBean){
		balanceteBean.setSaldoInicial(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_0(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_1(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_2(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_3(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_4(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_5(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_6(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_7(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_8(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_9(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_10(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_11(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_12(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_13(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_14(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_15(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_16(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_17(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_18(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_19(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_20(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_21(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_22(new BigDecimal(0).setScale(2));
		balanceteBean.setSaldo_23(new BigDecimal(0).setScale(2));
	}
	
	/**
	 * @param inicio
	 * @param fim
	 * @param balanceteBean
	 * @param saldo
	 * @Descrição
	 * Método que replica o saldo passado como parâmetro nos campos do bean do Balancete Modo Comparativo 
	 * nos campos limitados pelos índices início e fim. 
	 */
	private void replicaSaldos(int inicio, int fim, BalanceteComparativoReportBean balanceteBean, BigDecimal saldo){
		if ( inicio <= 0 && fim >= 0){
			balanceteBean.setSaldo_0(balanceteBean.getSaldo_0().add(saldo));
		}
		if ( inicio <= 1 && fim >= 1){
			balanceteBean.setSaldo_1(balanceteBean.getSaldo_1().add(saldo));
		}
		if ( inicio <= 2 && fim >= 2){
			balanceteBean.setSaldo_2(balanceteBean.getSaldo_2().add(saldo));
		}
		if ( inicio <= 3 && fim >= 3){
			balanceteBean.setSaldo_3(balanceteBean.getSaldo_3().add(saldo));
		}
		if ( inicio <= 4 && fim >= 4){
			balanceteBean.setSaldo_4(balanceteBean.getSaldo_4().add(saldo));
		}
		if ( inicio <= 5 && fim >= 5){
			balanceteBean.setSaldo_5(balanceteBean.getSaldo_5().add(saldo));
		}
		if ( inicio <= 6 && fim >= 6){
			balanceteBean.setSaldo_6(balanceteBean.getSaldo_6().add(saldo));
		}
		if ( inicio <= 7 && fim >= 7){
			balanceteBean.setSaldo_7(balanceteBean.getSaldo_7().add(saldo));
		}
		if ( inicio <= 8 && fim >= 8){
			balanceteBean.setSaldo_8(balanceteBean.getSaldo_8().add(saldo));
		}
		if ( inicio <= 9 && fim >= 9){
			balanceteBean.setSaldo_9(balanceteBean.getSaldo_9().add(saldo));
		}
		if ( inicio <= 10 && fim >= 10){
			balanceteBean.setSaldo_10(balanceteBean.getSaldo_10().add(saldo));
		}
		if ( inicio <= 11 && fim >= 11){
			balanceteBean.setSaldo_11(balanceteBean.getSaldo_11().add(saldo));
		}
		if ( inicio <= 12 && fim >= 12){
			balanceteBean.setSaldo_12(balanceteBean.getSaldo_12().add(saldo));
		}
		if ( inicio <= 13 && fim >= 13){
			balanceteBean.setSaldo_13(balanceteBean.getSaldo_13().add(saldo));
		}
		if ( inicio <= 14 && fim >= 14){
			balanceteBean.setSaldo_14(balanceteBean.getSaldo_14().add(saldo));
		}
		if ( inicio <= 15 && fim >= 15){
			balanceteBean.setSaldo_15(balanceteBean.getSaldo_15().add(saldo));
		}
		if ( inicio <= 16 && fim >= 16){
			balanceteBean.setSaldo_16(balanceteBean.getSaldo_16().add(saldo));
		}
		if ( inicio <= 17 && fim >= 17){
			balanceteBean.setSaldo_17(balanceteBean.getSaldo_17().add(saldo));
		}
		if ( inicio <= 18 && fim >= 18){
			balanceteBean.setSaldo_18(balanceteBean.getSaldo_18().add(saldo));
		}
		if ( inicio <= 19 && fim >= 19){
			balanceteBean.setSaldo_19(balanceteBean.getSaldo_19().add(saldo));
		}
		if ( inicio <= 20 && fim >= 20){
			balanceteBean.setSaldo_20(balanceteBean.getSaldo_20().add(saldo));
		}
		if ( inicio <= 21 && fim >= 21){
			balanceteBean.setSaldo_21(balanceteBean.getSaldo_21().add(saldo));
		}
		if ( inicio <= 22 && fim >= 22){
			balanceteBean.setSaldo_22(balanceteBean.getSaldo_22().add(saldo));
		}
		if ( inicio <= 23 && fim >= 23){
			balanceteBean.setSaldo_23(balanceteBean.getSaldo_23().add(saldo));
		}
	}
	
	/**
	 * @param contaAnalitica
	 * @param contaSintetica
	 * @return Boolean
	 * @Descrição
	 * Método que retorna se a Conta Analítica é filha da Conta Sintética.
	 */
	private Boolean contaAnaliticaEFilha( ContaContabil contaAnalitica, ContaContabil contaSintetica ){		
		return contaAnalitica.getCodigo().startsWith(contaSintetica.getCodigo()) && (contaSintetica.getCodigo().compareTo(contaAnalitica.getCodigo()) < 0);
	}
	
	/**
	 * @param contasAnaliticas
	 * @throws Exception
	 * @Descrição
	 * Método que cria as Contas Sintéticas a partir das Contas Analíticas que possuem saldo dentro do período.
	 */
	private void geraBeansContaSintetica(DataList contasAnaliticas) throws Exception{
		
		DataList contasSinteticas = DataListFactory.newDataList(this.getDataBase());
		
		List listContasAnaliticas = contasAnaliticas.getList();		
		ArrayList<ContaContabil> listContasSinteticas = new ArrayList<ContaContabil>();		 
		Long id = 0L;		
		
		if (this.getComparativo()){
			/* Para cada Conta Analítica existente no relatório, o sistema verifica quais são as contas Sintéticas do
			 * mesmo e preenche uma lista com as mesmas, sem qua haja a duplicação das contas Sintéticas.  
			 */
			for (Iterator iterator = listContasAnaliticas.iterator(); iterator.hasNext(); ) {
				BalanceteComparativoReportBean contaAnaliticaBean = (BalanceteComparativoReportBean) iterator.next();
				this.retornaContasSinteticas(contaAnaliticaBean.getConta(), listContasSinteticas);			
			}
			/* Para cada Conta Sintética é acumulado os valores dos campos de saldo das Contas Analíticas
			 * que sejam descendente da mesma.   
			 */
			for (Iterator iterator = listContasSinteticas.iterator(); iterator.hasNext(); ) {
				ContaContabil contaSintetica = (ContaContabil) iterator.next();
				
				BalanceteComparativoReportBean balanceteSinteticoBean = new BalanceteComparativoReportBean();
				
				balanceteSinteticoBean.setId(id++);
				balanceteSinteticoBean.setContaContabil(contaSintetica);
				balanceteSinteticoBean.setConta(contaSintetica.getCodigo());						
				balanceteSinteticoBean.setNome(this.identaConta(contaSintetica.getCodigo()) + contaSintetica.getNome());
				this.inicializaSaldos(balanceteSinteticoBean);	
				
				for (Iterator iterator2 = listContasAnaliticas.iterator(); iterator2.hasNext(); ) {
					BalanceteComparativoReportBean contaAnaliticaBean = (BalanceteComparativoReportBean) iterator2.next();
					
					if ( this.contaAnaliticaEFilha(contaAnaliticaBean.getContaContabil(), contaSintetica)){
						balanceteSinteticoBean.setSaldoInicial(balanceteSinteticoBean.getSaldoInicial().add(contaAnaliticaBean.getSaldoInicial()));
						balanceteSinteticoBean.setSaldo_0(balanceteSinteticoBean.getSaldo_0().add(contaAnaliticaBean.getSaldo_0()));
						balanceteSinteticoBean.setSaldo_1(balanceteSinteticoBean.getSaldo_1().add(contaAnaliticaBean.getSaldo_1()));
						balanceteSinteticoBean.setSaldo_2(balanceteSinteticoBean.getSaldo_2().add(contaAnaliticaBean.getSaldo_2()));
						balanceteSinteticoBean.setSaldo_3(balanceteSinteticoBean.getSaldo_3().add(contaAnaliticaBean.getSaldo_3()));
						balanceteSinteticoBean.setSaldo_4(balanceteSinteticoBean.getSaldo_4().add(contaAnaliticaBean.getSaldo_4()));
						balanceteSinteticoBean.setSaldo_5(balanceteSinteticoBean.getSaldo_5().add(contaAnaliticaBean.getSaldo_5()));
						balanceteSinteticoBean.setSaldo_6(balanceteSinteticoBean.getSaldo_6().add(contaAnaliticaBean.getSaldo_6()));
						balanceteSinteticoBean.setSaldo_7(balanceteSinteticoBean.getSaldo_7().add(contaAnaliticaBean.getSaldo_7()));
						balanceteSinteticoBean.setSaldo_8(balanceteSinteticoBean.getSaldo_8().add(contaAnaliticaBean.getSaldo_8()));
						balanceteSinteticoBean.setSaldo_9(balanceteSinteticoBean.getSaldo_9().add(contaAnaliticaBean.getSaldo_9()));
						balanceteSinteticoBean.setSaldo_10(balanceteSinteticoBean.getSaldo_10().add(contaAnaliticaBean.getSaldo_10()));
						balanceteSinteticoBean.setSaldo_11(balanceteSinteticoBean.getSaldo_11().add(contaAnaliticaBean.getSaldo_11()));
						balanceteSinteticoBean.setSaldo_12(balanceteSinteticoBean.getSaldo_12().add(contaAnaliticaBean.getSaldo_12()));
						balanceteSinteticoBean.setSaldo_13(balanceteSinteticoBean.getSaldo_13().add(contaAnaliticaBean.getSaldo_13()));
						balanceteSinteticoBean.setSaldo_14(balanceteSinteticoBean.getSaldo_14().add(contaAnaliticaBean.getSaldo_14()));
						balanceteSinteticoBean.setSaldo_15(balanceteSinteticoBean.getSaldo_15().add(contaAnaliticaBean.getSaldo_15()));
						balanceteSinteticoBean.setSaldo_16(balanceteSinteticoBean.getSaldo_16().add(contaAnaliticaBean.getSaldo_16()));
						balanceteSinteticoBean.setSaldo_17(balanceteSinteticoBean.getSaldo_17().add(contaAnaliticaBean.getSaldo_17()));
						balanceteSinteticoBean.setSaldo_18(balanceteSinteticoBean.getSaldo_18().add(contaAnaliticaBean.getSaldo_18()));
						balanceteSinteticoBean.setSaldo_19(balanceteSinteticoBean.getSaldo_19().add(contaAnaliticaBean.getSaldo_19()));
						balanceteSinteticoBean.setSaldo_20(balanceteSinteticoBean.getSaldo_20().add(contaAnaliticaBean.getSaldo_20()));
						balanceteSinteticoBean.setSaldo_21(balanceteSinteticoBean.getSaldo_21().add(contaAnaliticaBean.getSaldo_21()));
						balanceteSinteticoBean.setSaldo_22(balanceteSinteticoBean.getSaldo_22().add(contaAnaliticaBean.getSaldo_22()));
						balanceteSinteticoBean.setSaldo_23(balanceteSinteticoBean.getSaldo_23().add(contaAnaliticaBean.getSaldo_23()));
					}
					
				}
				contasSinteticas.add(balanceteSinteticoBean);			
			}
			/* Adiciona a DataList de contas Analíticas, as contas Sintéticas criadas. 
			 */
			contasAnaliticas.add(contasSinteticas);
		} else {
			
			/* Para cada Conta Analítica existente no relatório, o sistema verifica quais são as contas Sintéticas do
			 * mesmo e preenche uma lista com as mesmas, sem qua haja a duplicação das contas Sintéticas.  
			 */			
			for (Iterator iterator = listContasAnaliticas.iterator(); iterator.hasNext(); ) {
				BalanceteReportBean contaAnaliticaBean = (BalanceteReportBean) iterator.next();
				this.retornaContasSinteticas(contaAnaliticaBean.getConta(), listContasSinteticas);			
			}
			/* Para cada Conta Sintética é acumulado os valores dos campos de saldo crédito e débito das Contas Analíticas
			 * que sejam descendente da mesma.   
			 */
			for (Iterator iterator = listContasSinteticas.iterator(); iterator.hasNext(); ) {
				ContaContabil contaSintetica = (ContaContabil) iterator.next();
				
				BalanceteReportBean balanceteSinteticoBean = new BalanceteReportBean();
				
				balanceteSinteticoBean.setId(id++);
				balanceteSinteticoBean.setContaContabil(contaSintetica);
				balanceteSinteticoBean.setConta(contaSintetica.getCodigo());						
				balanceteSinteticoBean.setNome(this.identaConta(contaSintetica.getCodigo()) + contaSintetica.getNome());
				balanceteSinteticoBean.setSaldoInicial(new BigDecimal(0).setScale(2));
				balanceteSinteticoBean.setCredito(new BigDecimal(0).setScale(2));
				balanceteSinteticoBean.setDebito(new BigDecimal(0).setScale(2));
				balanceteSinteticoBean.setSaldoFinal(new BigDecimal(0).setScale(2));
				
				for (Iterator iterator2 = listContasAnaliticas.iterator(); iterator2.hasNext(); ) {
					BalanceteReportBean contaAnaliticaBean = (BalanceteReportBean) iterator2.next();
					
					if ( this.contaAnaliticaEFilha(contaAnaliticaBean.getContaContabil(), contaSintetica)){
						balanceteSinteticoBean.setSaldoInicial(balanceteSinteticoBean.getSaldoInicial().add(contaAnaliticaBean.getSaldoInicial()));
						balanceteSinteticoBean.setCredito(balanceteSinteticoBean.getCredito().add(contaAnaliticaBean.getCredito()));
						balanceteSinteticoBean.setDebito(balanceteSinteticoBean.getDebito().add(contaAnaliticaBean.getDebito()));
						balanceteSinteticoBean.setSaldoFinal(balanceteSinteticoBean.getSaldoFinal().add(contaAnaliticaBean.getSaldoFinal()));
					}
					
				}
				contasSinteticas.add(balanceteSinteticoBean);			
			}
			/* Adiciona a DataList de contas Analíticas, as contas Sintéticas criadas. 
			 */
			contasAnaliticas.add(contasSinteticas);
		}		
	}	
}
