package br.com.dyad.backoffice.operacao.testes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraBaixaPedido;
import br.com.dyad.backoffice.operacao.consulta.ConsultaBaixaPedido;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.annotations.Test;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.unit.TestCase;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class RegraBaixaPedidoTeste extends TestCase {
	
	private RegraBaixaPedido regraBaixaPedido;
	 
	private ArrayList<Entidade> entidades;
	private ArrayList<Nucleo> nucleos; 
	private ArrayList<Recurso> recursos;
	private ArrayList<Estabelecimento> estabelecimentos; 
	
	private int quantidadeBaixaPedido;
	private DataList dlCabecalho;
	private DataList dlItens;
	private Boolean certoErrado;
	private HashMap<Long, Long> quantidadePedido = new HashMap<Long, Long>();
	
	public Boolean getCertoErrado() {		
		if (certoErrado == null){
			this.setCertoErrado( new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 2  == 0 );
		}		
		return certoErrado;
	}
	public void setCertoErrado(Boolean certoErrado) {
		this.certoErrado = certoErrado;
	}	
	public RegraBaixaPedido getRegraBaixaPedido() {
		return regraBaixaPedido;
	}
	public void setRegraBaixaPedido(RegraBaixaPedido regraBaixaPedido) {
		this.regraBaixaPedido = regraBaixaPedido;
	}	
	public ArrayList<Entidade> getEntidades() {
		return entidades;
	}
	public void setEntidades(ArrayList<Entidade> entidades) {
		this.entidades = entidades;
	}	
	public ArrayList<Nucleo> getNucleos() {
		return nucleos;
	}
	public void setNucleos(ArrayList<Nucleo> nucleos) {
		this.nucleos = nucleos;
	}
	public ArrayList<Recurso> getRecursos() {
		return recursos;
	}
	public void setRecursos(ArrayList<Recurso> recursos) {
		this.recursos = recursos;
	}
	public ArrayList<Estabelecimento> getEstabelecimentos() {
		return estabelecimentos;
	}
	public void setEstabelecimentos(ArrayList<Estabelecimento> estabelecimentos) {
		this.estabelecimentos = estabelecimentos;
	}		
	public int getQuantidadeBaixaPedido() {
		return quantidadeBaixaPedido;
	}
	public void setQuantidadeBaixaPedido(int quantidadeBaixaPedido) {
		this.quantidadeBaixaPedido = quantidadeBaixaPedido;
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
		
		DataList dlRecurso = DataListFactory.newDataList(getDataBase());
		hquery = "from " + Recurso.class.getName();
		dlRecurso.executeQuery(hquery, 0, 100);
		this.setRecursos(new ArrayList<Recurso>(dlRecurso.getList()));
		
		DataList dlNucleo = DataListFactory.newDataList(getDataBase());
		hquery = "from " + Nucleo.class.getName();		
		dlNucleo.executeQuery(hquery, 0, 100);
		this.setNucleos(new ArrayList<Nucleo>(dlNucleo.getList()));
		
		DataList dlEstabelecimento = DataListFactory.newDataList(getDataBase());
		hquery = "from " + Estabelecimento.class.getName();
		dlEstabelecimento.executeQuery(hquery, 0, 100);
		this.setEstabelecimentos(new ArrayList<Estabelecimento>(dlEstabelecimento.getList()));		
		
	}
	
	public void criaBaixaPedidos() throws Exception{
		this.inicializaListas();
		int quantidadeBaixaPedido = this.getQuantidadeBaixaPedido();
		RegraBaixaPedido regraBaixaPedido = this.getRegraBaixaPedido();		
		ArrayList<Entidade> entidades = this.getEntidades();
		ArrayList<Recurso> recursos = this.getRecursos();
		ArrayList<Nucleo> nucleos = this.getNucleos();
		ArrayList<Estabelecimento> estabelecimentos = this.getEstabelecimentos();		
		int quantEntidades = entidades.size() - 1;
		int quantRecursos = recursos.size() - 1;
		int quantNucleos = nucleos.size() - 1;
		int quantEstabelecimentos = estabelecimentos.size() - 1;
		Boolean certoErrado = this.getCertoErrado();
		
		int indiceEntidade = 0;
		int indiceRecurso = 0;
		int indiceNucleo = 0;
		int indiceEstabelecimento = 0;
		
		for (int i = 0; i < quantidadeBaixaPedido; i++) {
			
			int quantidadeItens = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 5 + 1;			
			int dias = new BigDecimal(Math.random() * 1200).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 120 - 60;
			if (!certoErrado){
				dias -= 360;
			}
			Long quantidadeUnitario = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).longValue() % 20 + 10;
			
			indiceEntidade  = new BigDecimal(Math.random() * (quantEntidades)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			indiceRecurso = new BigDecimal(Math.random() * (quantRecursos)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			indiceNucleo = new BigDecimal(Math.random() * (quantNucleos)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			indiceEstabelecimento = new BigDecimal(Math.random() * (quantEstabelecimentos)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			
			Double valorUnitario = new BigDecimal(Math.random() * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			
			Date emissao = new Date();
			CalendarUtil.addDaysToDate(emissao, dias);
			
			CabecalhoBaixaPedido cabecalhoBaixaPedido = (CabecalhoBaixaPedido)regraBaixaPedido.novoCabecalho();
			cabecalhoBaixaPedido.setEstabelecimento(estabelecimentos.get(indiceEstabelecimento));
			cabecalhoBaixaPedido.setEntidade(entidades.get(indiceEntidade));
			cabecalhoBaixaPedido.setEmissao(emissao);
			
			for (int j = 0; j < quantidadeItens; j++) {
				ItemBaixaPedido itemBaixaPedido = (ItemBaixaPedido) regraBaixaPedido.novoItem(cabecalhoBaixaPedido);				
				itemBaixaPedido.setUnitario(new BigDecimal(valorUnitario).setScale(2, BigDecimal.ROUND_HALF_UP));
				itemBaixaPedido.setQuantidade(quantidadeUnitario);
				quantidadePedido.put(itemBaixaPedido.getId(), quantidadeUnitario);
			}
		}	
		this.setDlCabecalho(regraBaixaPedido.getDataListCabecalhos());
		//this.setDlItens(regraBaixaPedido.getDataListItens());
	}
	
	private void criaRegraBaixaPedido(){
		if ( this.getRegraBaixaPedido() == null ){
			AppTransaction appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(getDataBase()), getDataBase());
			RegraBaixaPedido regraBaixaPedido = new RegraBaixaPedido(appTransaction);
			this.setRegraBaixaPedido(regraBaixaPedido);
		}
	}	
	
	public void teste_regraBaixaPedido_abre_operacaoId() throws Exception {
		this.criaRegraBaixaPedido();
		AppTransaction appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(getDataBase()), getDataBase());
		RegraBaixaPedido regraBaixaPedidoTeste = new RegraBaixaPedido(appTransaction);		
		DataList dlCabecalho = this.getDlCabecalho();
		DataList dlItens = this.getDlItens();
		List listCabecalhos = dlCabecalho.getList();
		for (Iterator iterator = listCabecalhos.iterator(); iterator.hasNext(); ) {
			CabecalhoBaixaPedido cabecalho = (CabecalhoBaixaPedido) iterator.next();
			Collection<AppEntity> baixaPedidos = dlItens.getRangeAsCollection("id", cabecalho.getId() );
			ConsultaBaixaPedido consultaBaixaPedido = regraBaixaPedidoTeste.getConsultaBaixaPedido();
			if (this.getCertoErrado()){
				consultaBaixaPedido.setOperacaoId(cabecalho.getId());
			} else {
				consultaBaixaPedido.setOperacaoId(10000L);
			}
			//regraBaixaPedidoTeste.abre();
			List cabecalhosAbertos = regraBaixaPedidoTeste.getDataListCabecalhos().getList();
			//List itensAbertos = regraBaixaPedidoTeste.getDataListItens().getList();
			System.out.println("this.getCertoErrado(): " + this.getCertoErrado());
			RegraLancamentoTeste.assertTrue(this.getCertoErrado() == (cabecalhosAbertos.size() == 1));
			//RegraLancamentoTeste.assertTrue(this.getCertoErrado() == (baixaPedidos != null && (baixaPedidos.size() == itensAbertos.size())));
			//RegraLancamentoTeste.assertTrue(this.getCertoErrado() == (baixaPedidos != null && itensAbertos.containsAll(baixaPedidos)));			
		}
		
	}
		
	public void teste_regraBaixaPedido_cria_operacaoId() throws Exception {
		this.criaRegraBaixaPedido();
		int quantidadeBaixaPedidos = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 5 + 2;
		this.setQuantidadeBaixaPedido(quantidadeBaixaPedidos);
		this.criaBaixaPedidos();
		System.out.println("this.getCertoErrado(): " + this.getCertoErrado());
		RegraLancamentoTeste.assertTrue(this.getQuantidadeBaixaPedido() == this.getRegraBaixaPedido().getDataListCabecalhos().getList().size());		
	}
	
	public void teste_regraBaixaPedido_valida_operacaoId() throws Exception {
		this.criaRegraBaixaPedido();
		Boolean integridade = true;		
		int coeficienteErro = 0;
		RegraBaixaPedido regraBaixaPedido = this.getRegraBaixaPedido();		
		List cabecalhos = regraBaixaPedido.getDataListCabecalhos().getList();		
		for (Iterator iterator = cabecalhos.iterator(); iterator.hasNext(); ) {
			coeficienteErro = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 3;
			CabecalhoBaixaPedido cabecalho = (CabecalhoBaixaPedido) iterator.next();
			if (!this.getCertoErrado()){
				switch (coeficienteErro) {
				case 0:
					cabecalho.setEntidade(null);
					break;
				case 1:
					cabecalho.setEstabelecimento(null);
					break;
				case 2:
					cabecalho.setId(null);
					break;
				default:
					cabecalho.setEmissao(null);
					break;
				}				
			}
			try {
				regraBaixaPedido.calculaOperacao(cabecalho);
				integridade = true;
			} catch (Exception e) {
				integridade = false;
				System.out.println(e);
			}
			System.out.println("this.getCertoErrado(): " + this.getCertoErrado());
			RegraLancamentoTeste.assertTrue(this.getCertoErrado() == integridade);			
		}				
		if (this.getCertoErrado()){
			//DataList.commit(regraBaixaPedido.getDataListItens());
		}		
	}
	
	@Test
	public void teste_regraBaixaPedido_fluxo_geral_correto() throws Exception {
		this.criaRegraBaixaPedido();
		this.setCertoErrado(true);
		this.getRegraBaixaPedido().fecha();
		this.teste_regraBaixaPedido_cria_operacaoId();
		this.teste_regraBaixaPedido_valida_operacaoId();
		this.teste_regraBaixaPedido_abre_operacaoId();
	}
	@Test
	public void teste_regraBaixaPedido_fluxo_geral_errado() throws Exception {
		this.criaRegraBaixaPedido();
		this.setCertoErrado(false);
		this.getRegraBaixaPedido().fecha();
		this.teste_regraBaixaPedido_cria_operacaoId();
		this.teste_regraBaixaPedido_valida_operacaoId();
		this.teste_regraBaixaPedido_abre_operacaoId();
	}
}
