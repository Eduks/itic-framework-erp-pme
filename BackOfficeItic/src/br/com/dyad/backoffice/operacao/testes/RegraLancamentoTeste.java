package br.com.dyad.backoffice.operacao.testes;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraLancamento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.annotations.Test;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.unit.TestCase;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class RegraLancamentoTeste extends TestCase {
	
	private RegraLancamento regraLancamento;
	
	private ArrayList<ContaContabil> contasContabeis; 
	private ArrayList<Entidade> entidades;
	private int quantidadeLancamentos;
	private DataList dlCabecalho;
	private DataList dlItens;
	private Boolean certoErrado;	
	
	public Boolean getCertoErrado() {		
		if (certoErrado == null){
			this.setCertoErrado( new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 2  == 0 );
		}		
		return certoErrado;
	}
	public void setCertoErrado(Boolean certoErrado) {
		this.certoErrado = certoErrado;
	}
	public RegraLancamento getRegraLancamento() {
		return regraLancamento;
	}
	public void setRegraLancamento(RegraLancamento regraLancamento) {
		this.regraLancamento = regraLancamento;
	}
	public ArrayList<ContaContabil> getContasContabeis() {
		return contasContabeis;
	}
	public void setContasContabeis(ArrayList<ContaContabil> contasContabeis) {
		this.contasContabeis = contasContabeis;
	}
	public ArrayList<Entidade> getEntidades() {
		return entidades;
	}
	public void setEntidades(ArrayList<Entidade> entidades) {
		this.entidades = entidades;
	}	
	public int getQuantidadeLancamentos() {
		return quantidadeLancamentos;
	}
	public void setQuantidadeLancamentos(int quantidadeLancamentos) {
		this.quantidadeLancamentos = quantidadeLancamentos;
	}	
	public DataList getDlCabecalho() {
		return dlCabecalho;
	}
	public void setDlCabecalho(DataList dlCabecalho) {
		this.dlCabecalho = dlCabecalho;
	}
	public DataList getDlItens() {
		return dlItens;
	}
	public void setDlItens(DataList dlItens) {
		this.dlItens = dlItens;
	}
	private void inicializaListas(){
		DataList dlEntidade = DataListFactory.newDataList(getDataBase());
		String hquery = "from " + Entidade.class.getName();
		dlEntidade.executeQuery(hquery, 0, 100);
		this.setEntidades(new ArrayList<Entidade>(dlEntidade.getList()));
		
		DataList dlContasContabeis = DataListFactory.newDataList(getDataBase());
		hquery = "from " + ContaContabil.class.getName();
		hquery += " where planoConta.id = 1401893 and sintetico = false ";
		dlContasContabeis.executeQuery(hquery, 0, 100);
		this.setContasContabeis(new ArrayList<ContaContabil>(dlContasContabeis.getList()));
	}
	
	public void criaLancamentos() throws Exception{
		this.inicializaListas();
		int quantidadeLancamentos = this.getQuantidadeLancamentos();
		RegraLancamento regraLancamento = this.getRegraLancamento();
		ArrayList<ContaContabil> contasContabeis = this.getContasContabeis();
		ArrayList<Entidade> entidades = this.getEntidades();
		int quantContas = contasContabeis.size();
		int quantEntidades = entidades.size();
		Boolean certoErrado = this.getCertoErrado();
		
		for (int i = 0; i < quantidadeLancamentos; i++) {
			
			int quantidadeNegativa = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 2 + 1;
			int quantidadePositiva = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 3 + 1;
			int dias = new BigDecimal(Math.random() * 1200).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 120 - 60;
			if (!certoErrado){
				dias -= 360;
			}
			Double valorLancamento = new BigDecimal(Math.random() * 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + 100D;
			
			CabecalhoLancamento cabecalhoLancamento = (CabecalhoLancamento)regraLancamento.novoCabecalho();
			Date emissao = new Date();
			CalendarUtil.addDaysToDate(emissao, dias);
			
			cabecalhoLancamento.setEmissao(emissao);			
			Double valorRestante = valorLancamento;
			int indiceEntidade = 0;
			int indiceConta = 0;
			
			for (int j = 0; j < quantidadeNegativa; j++) {
				Double valorItem = 0D;
				if (j >= quantidadeNegativa - 1){
					valorItem = valorRestante;
				} else {
					valorItem = new BigDecimal(Math.random() * valorRestante).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					valorRestante -= valorItem;
				}
				if (!certoErrado){
					valorItem += 10D;
				}
				indiceConta = new BigDecimal(Math.random() * (quantContas - 1)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				indiceEntidade  = new BigDecimal(Math.random() * (quantEntidades - 1)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
							
				ItemLancamento itemLancamento = (ItemLancamento)regraLancamento.novoItem(cabecalhoLancamento);
				itemLancamento.setEntidade(entidades.get(indiceEntidade));
				if (certoErrado){
					itemLancamento.setContaContabil(contasContabeis.get(indiceConta));
				}
				itemLancamento.setValor(new BigDecimal(valorItem).negate().setScale(2, BigDecimal.ROUND_HALF_UP));
				//regraLancamento.getDataListItens().save(itemLancamento);
			}
			
			valorRestante = valorLancamento;
			
			for (int j = 0; j < quantidadePositiva; j++) {
				Double valorItem = 0D;
				if (j >= quantidadePositiva - 1){
					valorItem = valorRestante;
				} else {
					valorItem = new BigDecimal(Math.random() * valorRestante).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					valorRestante -= valorItem;
				}
				indiceConta = new BigDecimal(Math.random() * (quantContas - 1)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				indiceEntidade  = new BigDecimal(Math.random() * (quantEntidades - 1)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
							
				ItemLancamento itemLancamento = (ItemLancamento)regraLancamento.novoItem(cabecalhoLancamento);
				itemLancamento.setEntidade(entidades.get(indiceEntidade));
				if (certoErrado){
					itemLancamento.setContaContabil(contasContabeis.get(indiceConta));
				}
				itemLancamento.setValor(new BigDecimal(valorItem).setScale(2, BigDecimal.ROUND_HALF_UP));
				//regraLancamento.getDataListItens().save(itemLancamento);
			}			
		} 
		this.setDlCabecalho(regraLancamento.getDataListCabecalhos());
		//this.setDlItens(regraLancamento.getDataListItens());
	}
	
	private void criaRegraLancamento(){
		if ( this.getRegraLancamento() == null ){
			AppTransaction appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(getDataBase()), getDataBase());
			RegraLancamento regraLancamento = new RegraLancamento(appTransaction);
			this.setRegraLancamento(regraLancamento);
		}
	}	
	
	public void teste_regraLancamento_abre_operacaoId() throws Exception {
		this.criaRegraLancamento();
		AppTransaction appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(getDataBase()), getDataBase());
		RegraLancamento regraLancamentoTeste = new RegraLancamento(appTransaction);		
		RegraLancamento regraLancamento = this.getRegraLancamento();		
		DataList dlCabecalho = this.getDlCabecalho();
		DataList dlItens = this.getDlItens();
		List listCabecalhos = dlCabecalho.getList();
		for (Iterator iterator = listCabecalhos.iterator(); iterator.hasNext(); ) {
			CabecalhoLancamento cabecalho = (CabecalhoLancamento) iterator.next();
			Collection<AppEntity> lancamentos = dlItens.getRangeAsCollection("id", cabecalho.getId() );
			//ConsultaLancamento consultaLancamento = regraLancamentoTeste.getConsultaLancamento();
			if (this.getCertoErrado()){
				//consultaLancamento.setOperacaoId(cabecalho.getId());
			} else {
				//consultaLancamento.setOperacaoId(10000L);
			}
			//regraLancamentoTeste.abre();
			List cabecalhosAbertos = regraLancamentoTeste.getDataListCabecalhos().getList();
			//List itensAbertos = regraLancamentoTeste.getDataListItens().getList();
			System.out.println("this.getCertoErrado(): " + this.getCertoErrado());
			RegraLancamentoTeste.assertTrue(this.getCertoErrado() == (cabecalhosAbertos.size() == 1));
			//RegraLancamentoTeste.assertTrue(this.getCertoErrado() == (lancamentos != null && (lancamentos.size() == itensAbertos.size())));
			//RegraLancamentoTeste.assertTrue(this.getCertoErrado() == (lancamentos != null && itensAbertos.containsAll(lancamentos)));			
		}
		
	}	
	
	public void teste_regraLancamento_cria_operacaoId() throws Exception {
		this.criaRegraLancamento();
		int quantidadeLancamentos = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 5 + 2;
		this.setQuantidadeLancamentos(quantidadeLancamentos);
		this.criaLancamentos();
		System.out.println("this.getCertoErrado(): " + this.getCertoErrado());
		RegraLancamentoTeste.assertTrue(this.getQuantidadeLancamentos() == this.getRegraLancamento().getDataListCabecalhos().getList().size());		
	}
	
	public void teste_regraLancamento_valida_operacaoId() throws Exception {
		this.criaRegraLancamento();		
		Boolean validaLancamento = true;
		RegraLancamento regraLancamento = this.getRegraLancamento();		
		List cabecalhos = regraLancamento.getDataListCabecalhos().getList();
		for (Iterator iterator = cabecalhos.iterator(); iterator.hasNext(); ) {
			CabecalhoLancamento cabecalho = (CabecalhoLancamento) iterator.next();
			if (!this.getCertoErrado()){
				int coeficienteErro = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 2;
				if (!this.getCertoErrado()){
					switch (coeficienteErro) {
					case 0:
						cabecalho.setId(null);
						break;				
					default:
						cabecalho.setEmissao(null);
						break;
					}				
				}
			}
			try {
				this.regraLancamento.calculaOperacao(cabecalho);
				validaLancamento = true;
			} catch (Exception e) {
				validaLancamento = false;
				System.out.println(e);
			}
			System.out.println("this.getCertoErrado(): " + this.getCertoErrado());
			RegraLancamentoTeste.assertTrue(this.getCertoErrado() == validaLancamento);			
		}
		if (this.getCertoErrado()){
			//DataList.commit(regraLancamento.getDataListItens());
		}		
	}
	
	@Test
	public void teste_regraLancamento_fluxo_geral_correto() throws Exception {
		this.criaRegraLancamento();
		this.setCertoErrado(true);
		this.getRegraLancamento().fecha();		
		this.teste_regraLancamento_cria_operacaoId();
		this.teste_regraLancamento_valida_operacaoId();
		this.teste_regraLancamento_abre_operacaoId();
	}
	@Test
	public void teste_regraLancamento_fluxo_geral_errado() throws Exception {
		this.criaRegraLancamento();
		this.setCertoErrado(false);
		this.getRegraLancamento().fecha();
		this.teste_regraLancamento_cria_operacaoId();
		this.teste_regraLancamento_valida_operacaoId();
		this.teste_regraLancamento_abre_operacaoId();
	}	
}
