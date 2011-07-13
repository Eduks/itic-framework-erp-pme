package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;
import br.com.dyad.backoffice.operacao.consulta.ConsultaLancamento;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

/**
 * @author João Paulo
 * @since 11/03/2010
 * @Descrição
 * Class......: RegraLancamento
 * <br>Objetivo...: Classe que implementa todas as regras que influenciam no comportamento de um Lançamento, 
 *              desde a criação/exclusão de itens e cabeçalho a implementação de regras de validação. 
 * <br>Sistema....: BackOffice
 * <br>Observação.: (nope) 
 */
public class RegraLancamento extends RegraAbstrata  {

	private ConsultaLancamento consultaLancamento;
	
	/**
	 * @param appTransaction
	 * @Descrição
	 * Constructor
	 */
	public RegraLancamento(AppTransaction appTransaction) {
		super(appTransaction, "LANCAMENTO");		

		this.consultaLancamento = new ConsultaLancamento(appTransaction);
		this.preparado = false;
	}

	public void abre() throws Exception{
		if (consultaLancamento.getPlanoConta() == null && consultaLancamento.getContabilId() == null 
				&& consultaLancamento.getEmissaoInicial() == null && consultaLancamento.getEmissaoFinal() == null
				&& consultaLancamento.getEntidade() == null) {
			throw new AppException("É necessário informar algum dos quatro filtros de localização!");
		}

		DataList dataList = consultaLancamento.pegaLancamentos();

		this.getDataListCabecalhos().setList(dataList.getList());
	}

	public ConsultaLancamento getConsultaLancamento(){
		return consultaLancamento;
	}

	@Override
	public void calculaOperacao(Cabecalho cabecalho) throws Exception {
		CabecalhoLancamento cabecalhoLancamento = (CabecalhoLancamento)cabecalho;
		this.verificaIntegridadeCabecalho(cabecalho);
		this.sincronizaItensComCabecalho(cabecalho);
		this.verificaIntegridadeItensCabecalho(cabecalho);

		BigDecimal saldo = new BigDecimal(0).setScale(2);

		Long planoContaId = null;
		PlanoConta planoConta = null;

		Collection<ItemLancamento> itensLancamento = cabecalhoLancamento.getItensLancamento();

		if ( itensLancamento == null || itensLancamento.isEmpty() )
			throw new AppException("O lançamento " + cabecalhoLancamento.getId() + " não pode ser salvo pois não possui itens.");

		for (ItemLancamento itemLancamento : itensLancamento) {
			if ( itemLancamento.getValor() == null || itemLancamento.getValor().compareTo(new BigDecimal(0)) == 0) {
				throw new AppException("O item " + itemLancamento.getId() + ", do lançamento "+cabecalhoLancamento.getId()+", não possui um valor definido.");
			} else {
				saldo = saldo.add( itemLancamento.getValor() );
			}

			planoConta = itemLancamento.getContaContabil().getPlanoConta();

			if ( planoContaId != null && !planoContaId.equals(planoConta.getId()) ) {
				throw new AppException("O lançamento " + cabecalhoLancamento.getId() + " não pode ser salvo, pois possui Contas pertencentes a diferentes Planos de Contas.");
			} else {
				planoContaId = planoConta.getId();
			}			
		}		

		if ( saldo.compareTo( new BigDecimal(0) ) != 0 ){
			throw new AppException("O lançamento " + cabecalhoLancamento.getId() + " não pode ser salvo pois possui saldo igual a R$ " + saldo + ". O mesmo deve ser igual a R$ 0,00.");
		}
		
		if (consultaLancamento.getTravamento(cabecalhoLancamento) != null) {
			throw new AppException("Não é possível salvar o lançamento "+cabecalhoLancamento.getId()+", pois a sua data de emissão é anterior à última data de travamento do Plano de Contas " + planoConta.toString());
		}

	}
	
	@Override
	public void calculaOperacoes() throws Exception {
		List<CabecalhoLancamento> cabecalhosLancamento = this.getDataListCabecalhos().getList();

		for (CabecalhoLancamento cabecalhoLancamento : cabecalhosLancamento) {
			this.calculaOperacao(cabecalhoLancamento);
		}
	}

	@Override
	public void grava() throws Exception {
		preparaGravacao();
		
		DataList.commit(this.getDataListCabecalhos());
	}
	
	@Override
	protected void preparaGravacao() throws Exception {
		this.calculaOperacoes();
	}

	@Override
	protected void sincronizaItensComCabecalho(Cabecalho cabecalho) throws Exception {
		CabecalhoLancamento cabecalhoLancamento = (CabecalhoLancamento)cabecalho;

		Collection<ItemLancamento> lancamentos = cabecalhoLancamento.getItensLancamento();
		ArrayList<String> camposCabecalho = new ArrayList<String>(); 

		camposCabecalho.add("emissao");

		for (ItemLancamento itemLancamento : lancamentos) {
			for (String nomeDoCampo : camposCabecalho) {
				ReflectUtil.setFieldValue(itemLancamento, nomeDoCampo, ReflectUtil.getFieldValue(cabecalhoLancamento, nomeDoCampo));
			}
		}
	}

	@Override
	protected void verificaIntegridadeCabecalho(Cabecalho cabecalho) throws Exception {
		CabecalhoLancamento cabecalhoLancamento = (CabecalhoLancamento) cabecalho;

		ArrayList<String> erros = new ArrayList<String>(); 

		if (cabecalhoLancamento.getId() == null) {
			erros.add("A propriedade operacao não está preenchida.");
		}

		if (cabecalhoLancamento.getEmissao() == null) {
			erros.add("A propriedade \"emissão\" do lançamento: " + cabecalhoLancamento.getId() + " não está preenchida.");
		}

		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}
	}

	@Override
	protected void verificaIntegridadeCabecalhos() throws Exception {
		ArrayList<CabecalhoLancamento> listCabecalhos = (ArrayList<CabecalhoLancamento>)this.getDataListCabecalhos().getList();

		for (CabecalhoLancamento cabecalhoLancamento : listCabecalhos) {
			verificaIntegridadeCabecalho(cabecalhoLancamento);
		}
	}

	@Override
	protected void verificaIntegridadeItens() throws Exception {
		List<CabecalhoLancamento> cabecalhosLancamento = this.getDataListCabecalhos().getList();

		for (CabecalhoLancamento cabecalhoLancamento : cabecalhosLancamento) {
			this.verificaIntegridadeItensCabecalho(cabecalhoLancamento);			
		}
	}

	@Override
	protected void verificaIntegridadeItensCabecalho(Cabecalho cabecalho) throws Exception {
		CabecalhoLancamento cabecalhoLancamento = (CabecalhoLancamento)cabecalho;

		Collection<ItemLancamento> listLancamentos = cabecalhoLancamento.getItensLancamento();
		ArrayList<String> erros = new ArrayList<String>(); 

		for (ItemLancamento itemLancamento : listLancamentos) {
			if (itemLancamento.getId() == null) {
				erros.add("A propriedade \"id\" não está preenchida.");
			}
			if (itemLancamento.getContaContabil() == null) {
				erros.add("A propriedade \"Conta Contábil\", do item "+itemLancamento.getId()+", pertencente ao lançamento "+cabecalhoLancamento.getId()+", está inválida.");
			}
			if (itemLancamento.getValor() == null) {
				erros.add("A propriedade \"Valor\", do item "+itemLancamento.getId()+", pertencente ao lançamento "+cabecalhoLancamento.getId()+", está inválida.");
			}
		}

		if (!erros.isEmpty()) {
			throw new AppException(StringUtils.join(erros, "\r\n"));
		}
	}

	@Override
	public void excluiCabecalho(Cabecalho cabecalho) throws Exception {
		CabecalhoLancamento cabecalhoLancamento = (CabecalhoLancamento)cabecalho;

		ArrayList<ItemLancamento> itensLancamentos = (ArrayList<ItemLancamento>) cabecalhoLancamento.getItensLancamento();
		for (ItemLancamento itemLancamento : itensLancamentos ) {
			this.excluiItem(itemLancamento);
		}

		this.getDataListCabecalhos().delete((CabecalhoTransferenciaDisponivel)cabecalho);
	}

	@Override
	public void excluiItem(Item item) throws Exception {
		ItemLancamento itemLancamento = (ItemLancamento) item;
		itemLancamento.getCabecalho().removeItemLancamento(itemLancamento);

		this.getAppTransaction().delete(itemLancamento);
	}

	@Override
	public void fecha() throws Exception {
		
		this.getDataListCabecalhos().setLogChanges(false);
		this.getDataListCabecalhos().empty();
		this.getDataListCabecalhos().setLogChanges(true);
		
		this.preparado = false;
	}

	@Override
	public void nova() {
		// TODO Auto-generated method stub

	}

	@Override
	public Cabecalho novoCabecalho() throws Exception {
		CabecalhoLancamento cabecalho = new CabecalhoLancamento();
		cabecalho.createId(getAppTransaction().getDatabase());

		this.getDataListCabecalhos().add(cabecalho);

		return cabecalho;
	}

	@Override
	public Item novoItem(Cabecalho cabecalho) throws Exception {
		
		CabecalhoLancamento cabecalhoLancamento = (CabecalhoLancamento)cabecalho;
		
		if ( cabecalhoLancamento == null ) {
			throw new Exception("Operacao não pode ser nula!");
		}

		if ( !this.getDataListCabecalhos().findId( cabecalhoLancamento ) ) {
			throw new Exception("Este lançamento não foi encontrado neste objeto!");
		}

		ItemLancamento itemLancamento = new ItemLancamento();
		itemLancamento.createId(getAppTransaction().getDatabase());
		itemLancamento.setCabecalho(cabecalhoLancamento);

		cabecalhoLancamento.addItemLancamento(itemLancamento);

		return itemLancamento;
	}

	@Override
	public void preparaRegra() {
		// TODO Auto-generated method stub

	}
	
	public void removeLancamentoDaOperacao(CabecalhoOperacaoAbstrato cabecalhoOperacao) throws Exception{
		Session sessionExcluirLancamento = PersistenceUtil.getSession(getAppTransaction().getDatabase());
		
		sessionExcluirLancamento.beginTransaction();
		
		List lancamentoExcluir = sessionExcluirLancamento.createQuery("FROM CabecalhoLancamento c WHERE c.cabecalhoOperacao.id = "+cabecalhoOperacao.getId()).list();
		
		CabecalhoLancamento cabecalhoExcluir = lancamentoExcluir.isEmpty() ? new CabecalhoLancamento() : (CabecalhoLancamento)lancamentoExcluir.get(0);
		
		sessionExcluirLancamento.delete(cabecalhoExcluir);
		
		sessionExcluirLancamento.getTransaction().commit();
	}
}