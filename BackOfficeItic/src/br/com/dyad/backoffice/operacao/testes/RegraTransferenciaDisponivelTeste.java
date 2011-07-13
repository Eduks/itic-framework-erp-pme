package br.com.dyad.backoffice.operacao.testes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraTransferenciaDisponivel;
import br.com.dyad.backoffice.operacao.consulta.ConsultaOperacaoTransferenciaDisponivel;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.annotations.Test;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.unit.TestCase;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class RegraTransferenciaDisponivelTeste extends TestCase {
	
	private RegraTransferenciaDisponivel regraTransferenciaDisponivel;
	 
	private ArrayList<Entidade> entidades;	
	
	private int quantidadeTransferenciaDisponivel;
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
	public RegraTransferenciaDisponivel getRegraTransferenciaDisponivel() {
		return regraTransferenciaDisponivel;
	}
	public void setRegraTransferenciaDisponivel(RegraTransferenciaDisponivel regraTransferenciaDisponivel) {
		this.regraTransferenciaDisponivel = regraTransferenciaDisponivel;
	}	
	public ArrayList<Entidade> getEntidades() {
		return entidades;
	}
	public void setEntidades(ArrayList<Entidade> entidades) {
		this.entidades = entidades;
	}
			
	public int getQuantidadeTransferenciaDisponivel() {
		return quantidadeTransferenciaDisponivel;
	}
	public void setQuantidadeTransferenciaDisponivel(int quantidadeTransferenciaDisponivel) {
		this.quantidadeTransferenciaDisponivel = quantidadeTransferenciaDisponivel;
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
	}
	
	public void criaTransferenciaDisponiveis() throws Exception{
		this.inicializaListas();
		int quantidadeTransferenciaDisponivel = this.getQuantidadeTransferenciaDisponivel();
		RegraTransferenciaDisponivel regraTransferenciaDisponivel = this.getRegraTransferenciaDisponivel();		
		ArrayList<Entidade> entidades = this.getEntidades();			
		int quantEntidades = entidades.size() - 1;		
		Boolean certoErrado = this.getCertoErrado();		
		int indiceEntidade = 0;		
		
		for (int i = 0; i < quantidadeTransferenciaDisponivel; i++) {
			
			int quantidadeNegativa = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 2 + 1;
			int quantidadePositiva = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 3 + 1;			
			int dias = new BigDecimal(Math.random() * 1200).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 120 - 60;
			if (!certoErrado){
				dias -= 360;
			}		
						
			Double valorTransferencia = new BigDecimal(Math.random() * 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + 100D;
			
			Date emissao = new Date();
			CalendarUtil.addDaysToDate(emissao, dias);
			
			CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)regraTransferenciaDisponivel.novoCabecalho();			
			cabecalhoTransferenciaDisponivel.setEmissao(emissao);
			Double valorRestante = valorTransferencia;
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
				indiceEntidade  = new BigDecimal(Math.random() * (quantEntidades)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
							
				ItemTransferenciaDisponivel itemTransferenciaDisponivel = (ItemTransferenciaDisponivel)regraTransferenciaDisponivel.novoItem(cabecalhoTransferenciaDisponivel);
				itemTransferenciaDisponivel.setEntidade(entidades.get(indiceEntidade));				
				itemTransferenciaDisponivel.setTotal(new BigDecimal(valorItem).negate().setScale(2, BigDecimal.ROUND_HALF_UP));
				//regraTransferenciaDisponivel.getDataListItens().save(itemTransferenciaDisponivel);
			}
			
			valorRestante = valorTransferencia;
			
			for (int j = 0; j < quantidadePositiva; j++) {
				Double valorItem = 0D;
				if (j >= quantidadePositiva - 1){
					valorItem = valorRestante;
				} else {
					valorItem = new BigDecimal(Math.random() * valorRestante).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					valorRestante -= valorItem;
				}				
				indiceEntidade  = new BigDecimal(Math.random() * (quantEntidades - 1)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
							
				ItemTransferenciaDisponivel itemTransferenciaDisponivel = (ItemTransferenciaDisponivel)regraTransferenciaDisponivel.novoItem(cabecalhoTransferenciaDisponivel);
				itemTransferenciaDisponivel.setEntidade(entidades.get(indiceEntidade));	
				itemTransferenciaDisponivel.setTotal(new BigDecimal(valorItem).setScale(2, BigDecimal.ROUND_HALF_UP));
				//regraTransferenciaDisponivel.getDataListItens().save(itemTransferenciaDisponivel);
			}
		}	
		this.setDlCabecalho(regraTransferenciaDisponivel.getDataListCabecalhos());
		//this.setDlItens(regraTransferenciaDisponivel.getDataListItens());
	}
	
	private void criaRegraTransferenciaDisponivel(){
		if ( this.getRegraTransferenciaDisponivel() == null ){
			AppTransaction appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(getDataBase()), getDataBase());
			RegraTransferenciaDisponivel regraTransferenciaDisponivel = new RegraTransferenciaDisponivel(appTransaction);
			this.setRegraTransferenciaDisponivel(regraTransferenciaDisponivel);
		}
	}	
	
	public void teste_regraTransferenciaDisponivel_abre_operacaoId() throws Exception {
		this.criaRegraTransferenciaDisponivel();
		AppTransaction appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(getDataBase()), getDataBase());
		RegraTransferenciaDisponivel regraTransferenciaDisponivelTeste = new RegraTransferenciaDisponivel(appTransaction);		
		DataList dlCabecalho = this.getDlCabecalho();
		DataList dlItens = this.getDlItens();
		List listCabecalhos = dlCabecalho.getList();
		for (Iterator iterator = listCabecalhos.iterator(); iterator.hasNext(); ) {
			CabecalhoTransferenciaDisponivel cabecalho = (CabecalhoTransferenciaDisponivel) iterator.next();
			Collection<AppEntity> transferenciaDisponivel = dlItens.getRangeAsCollection("id", cabecalho.getId() );
			//ConsultaOperacaoTransferenciaDisponivel consultaOperacaoTransferenciaDisponivel = regraTransferenciaDisponivelTeste.getConsultaOperacaoTransferenciaDisponivel();
			if (this.getCertoErrado()){
				//consultaOperacaoTransferenciaDisponivel.parametroCabecalho.operacaoId.setValorAsLong(cabecalho.getId());
			} else {
				//consultaOperacaoTransferenciaDisponivel.parametroCabecalho.operacaoId.setValorAsLong(10000L);				
			}
			//regraTransferenciaDisponivelTeste.abre();
			List cabecalhosAbertos = regraTransferenciaDisponivelTeste.getDataListCabecalhos().getList();
			//List itensAbertos = regraTransferenciaDisponivelTeste.getDataListItens().getList();
			System.out.println("this.getCertoErrado(): " + this.getCertoErrado());
			RegraLancamentoTeste.assertTrue(this.getCertoErrado() == (cabecalhosAbertos.size() == 1));
			//RegraLancamentoTeste.assertTrue(this.getCertoErrado() == (transferenciaDisponivel != null && (transferenciaDisponivel.size() == itensAbertos.size())));
			//RegraLancamentoTeste.assertTrue(this.getCertoErrado() == (transferenciaDisponivel != null && itensAbertos.containsAll(transferenciaDisponivel)));			
		}
		
	}
		
	public void teste_regraTransferenciaDisponivel_cria_operacaoId() throws Exception {
		this.criaRegraTransferenciaDisponivel();
		int quantidadeTransferenciaDisponivel = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 5 + 2;
		this.setQuantidadeTransferenciaDisponivel(quantidadeTransferenciaDisponivel);
		this.criaTransferenciaDisponiveis();		
		System.out.println("this.getCertoErrado(): " + this.getCertoErrado());
		RegraLancamentoTeste.assertTrue(this.getQuantidadeTransferenciaDisponivel() == this.getRegraTransferenciaDisponivel().getDataListCabecalhos().getList().size());		
	}
	
	public void teste_regraTransferenciaDisponivel_valida_operacaoId() throws Exception {
		this.criaRegraTransferenciaDisponivel();
		Boolean integridade = true;		
		int coeficienteErro = 0;
		RegraTransferenciaDisponivel regraTransferenciaDisponivel = this.getRegraTransferenciaDisponivel();		
		List cabecalhos = regraTransferenciaDisponivel.getDataListCabecalhos().getList();		
		for (Iterator iterator = cabecalhos.iterator(); iterator.hasNext(); ) {
			coeficienteErro = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 2;
			CabecalhoTransferenciaDisponivel cabecalho = (CabecalhoTransferenciaDisponivel) iterator.next();
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
			try {
				regraTransferenciaDisponivel.calculaOperacao(cabecalho);
				integridade= true;
			} catch (Exception e) {
				integridade= false;
				System.out.println(e);
			}
			System.out.println("this.getCertoErrado(): " + this.getCertoErrado());
			RegraLancamentoTeste.assertTrue(this.getCertoErrado() == integridade);			
		}	
		if (this.getCertoErrado()){
			//DataList.commit(regraTransferenciaDisponivel.getDataListItens());
		}			
	}
	
	@Test
	public void teste_regraTransferenciaDisponivel_fluxo_geral_correto() throws Exception {
		this.criaRegraTransferenciaDisponivel();
		this.setCertoErrado(true);
		this.getRegraTransferenciaDisponivel().fecha();
		this.teste_regraTransferenciaDisponivel_cria_operacaoId();
		this.teste_regraTransferenciaDisponivel_valida_operacaoId();
		this.teste_regraTransferenciaDisponivel_abre_operacaoId();
	}
	@Test
	public void teste_regraTransferenciaDisponivel_fluxo_geral_errado() throws Exception {
		this.criaRegraTransferenciaDisponivel();
		this.setCertoErrado(false);
		this.getRegraTransferenciaDisponivel().fecha();
		this.teste_regraTransferenciaDisponivel_cria_operacaoId();
		this.teste_regraTransferenciaDisponivel_valida_operacaoId();
		this.teste_regraTransferenciaDisponivel_abre_operacaoId();
	}

}
