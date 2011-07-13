package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;
import br.com.dyad.backoffice.operacao.consulta.ConsultaBaixaPedido;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class RegraBaixaPedido extends RegraAbstrata {

	private RegraTitulo regraTitulo;
	private DataList dataListEventos;
	private ConsultaBaixaPedido consultaBaixasPedido;

	/**
	 * Class constructor.
	 * 
	 * @param database
	 */
	public RegraBaixaPedido(AppTransaction appTransaction) {
		super(appTransaction, "BAIXA_PEDIDO");

		this.regraTitulo = new RegraTitulo(this.getAppTransaction());
		this.consultaBaixasPedido = new ConsultaBaixaPedido(this.getAppTransaction());
		this.dataListEventos = DataListFactory.newDataList(this.getAppTransaction());
		this.preparado = false;
	}

	/**
	 * Getter's e Setter's
	 */
	public ConsultaBaixaPedido getConsultaBaixaPedido() {
		return this.consultaBaixasPedido;
	}

	public DataList getDataListEventos() {
		return this.dataListEventos;
	}

	/**
	 * Métodos
	 */
	@Override
	public void nova() {
		if (!this.preparado) {
			this.preparaRegra();
		}
	}

	@Override
	public void preparaRegra() {
		this.preparado = true;
	}

	/**
	 * @throws Exception
	 */
	@Override
	public Cabecalho novoCabecalho() throws Exception {
		CabecalhoBaixaPedido cabecalho = new CabecalhoBaixaPedido();
		cabecalho.createId(this.getAppTransaction().getDatabase());
		cabecalho.setClasseOperacaoId(this.getRegraId());
		
		this.getDataListCabecalhos().add(cabecalho);

		return cabecalho;
	}

	@Override
	public void excluiCabecalho( Cabecalho cabecalho ) throws Exception {
		CabecalhoBaixaPedido cabecalhoBaixaPedido = (CabecalhoBaixaPedido)cabecalho;

		ArrayList<ItemBaixaPedido> itensBaixaPedido = (ArrayList<ItemBaixaPedido>) cabecalhoBaixaPedido.getItensBaixaPedido();
		for (ItemBaixaPedido itemBaixaPedido : itensBaixaPedido ) {
			this.excluiItem(itemBaixaPedido);
		}

		this.getDataListCabecalhos().delete(cabecalhoBaixaPedido);
	}

	@Override
	public Item novoItem(Cabecalho cabecalho) throws Exception {
		CabecalhoBaixaPedido cabecalhoBaixaPedido = (CabecalhoBaixaPedido)cabecalho;

		if ( cabecalhoBaixaPedido == null ){
			throw new Exception("Operacao não pode ser nula!");
		}

		if ( !this.getDataListCabecalhos().find("id", cabecalhoBaixaPedido.getId()) ){
			throw new Exception("Esta operação não foi encontrada neste objeto!");
		}

		ItemBaixaPedido item = new ItemBaixaPedido();
		item.createId(this.getAppTransaction().getDatabase());
		item.setCabecalho((CabecalhoBaixaPedido)cabecalho);
		item.setClasseOperacaoId( this.regraId );

		cabecalhoBaixaPedido.addItemBaixaPedido(item);

		return item;
	}

	@Override
	public void excluiItem( Item item ) throws Exception {
		ItemBaixaPedido itemBaixaPedido = (ItemBaixaPedido) item;
		itemBaixaPedido.getCabecalho().removeItemBaixaPedido(itemBaixaPedido);
		
		this.getAppTransaction().delete(itemBaixaPedido);
	}

	public Titulo novoTitulo(CabecalhoOperacaoAbstrato cabecalhoAbstrato) throws Exception {
		Titulo titulo = this.regraTitulo.novoTitulo(cabecalhoAbstrato, this.regraId);
		titulo.setClasseOperacaoId( this.regraId );

		return titulo;
	}

	public void excluiTitulo( Titulo titulo ) throws Exception {
		CabecalhoBaixaPedido cabecalho = (CabecalhoBaixaPedido)titulo.getCabecalho();
		cabecalho.removeTitulo(titulo);

		this.getAppTransaction().delete(titulo);
	}

	/**
	 * Exemplo: 
	 * Desejo abrir todos os pedidos da pessoa 12324L cuja emissao esteja entre 01/01/2008 a 01/01/2009. 
	 * OperacaoPedido opPedido = new OperacaoPedido();
	 * opPedido.getConsulta().setEntidadeId( 12324L );
	 * opPedido.getConsulta().setEmissaoInicio( 01/01/2008 );
	 * opPedido.getConsulta().setEmissaoFim( 01/01/2009 );
	 * opPedido.abre();
	 * @throws Exception 
	 **/
	public void abre(Long idOperacao, Long idItem, String numero, Long entidadeId, Long estabelecimentoId, Date emissaoInicial, Date emissaoFinal) throws Exception {

		ArrayList<String> where = new ArrayList<String>();
		ArrayList<Object> params = new ArrayList<Object>();
		String select = "from " + CabecalhoBaixaPedido.class.getName() + " ";
				
		if (!this.preparado) {
			this.preparaRegra();
		}

		if (idOperacao != null) {
			where.add(" id = ? ");
			params.add(idOperacao);
		}
		
		if (idItem != null) {
			where.add(" id IN (SELECT i.cabecalho.id FROM " + ItemBaixaPedido.class.getName() + " i WHERE i.id = ?) ");
			params.add(idItem);
		}

		if (numero != null) {
			where.add(" numero = ? ");
			params.add(numero);
		}

		if (entidadeId != null) {
			where.add(" entidade.id = ? ");
			params.add(entidadeId);
		}

		if (estabelecimentoId != null) {
			where.add(" estabelecimento.id = ? ");
			params.add(entidadeId);
		}

		if (emissaoInicial != null) {
			where.add(" emissao >= ? ");
			params.add(emissaoInicial);
		}

		if (emissaoFinal != null) {
			where.add(" emissao <= ? ");
			params.add(emissaoFinal);
		}
		
		if (params.isEmpty()) {
			throw new AppException("Deve ser informada pelo menos uma restrição para a busca.");
		}

		String query = select + " where " + StringUtils.join(where, " and ");

		DataList dataListCabecalhos = DataListFactory.newDataList(this.getAppTransaction());
		this.getAppTransaction().clear();
		List<?> lista = PersistenceUtil.executeHql((Session)this.getAppTransaction().getSession(), query, params);
		
		dataListCabecalhos.setList(lista);
		
		this.getDataListCabecalhos().add(dataListCabecalhos);
	}

	/**
	 * INICIO DE TRECHO PADRONIZADO
	 */
	@Override
	public void grava() throws Exception {
		this.preparaGravacao();
		this.verificaIntegridadeItens();

		DataList.commit(this.getDataListCabecalhos());
	}

	@Override
	public void preparaGravacao() throws Exception {
		this.calculaOperacoes();

		this.criaTitulosOperacoes();
		this.sincronizaTitulosComCabecalhos();
		this.verificaIntegridadeTitulos();
	}

	/**
	 * @throws Exception 
	 * 
	 */
	@Override
	public void calculaOperacoes() throws Exception {
		List<CabecalhoBaixaPedido> cabecalhosBaixaPedido = this.getDataListCabecalhos().getList();

		for (CabecalhoBaixaPedido cabecalhoBaixaPedido : cabecalhosBaixaPedido) {
			this.calculaOperacao(cabecalhoBaixaPedido);
		}
	}

	/**
	 * @throws Exception 
	 * 
	 */
	@Override
	public void calculaOperacao(Cabecalho cabecalho) throws Exception {
		CabecalhoBaixaPedido cabecalhoBaixaPedido = (CabecalhoBaixaPedido)cabecalho;

		this.verificaIntegridadeCabecalho(cabecalhoBaixaPedido);
		this.sincronizaItensComCabecalho(cabecalhoBaixaPedido);

		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.calculaBaixasPedidos()");

		Collection<ItemBaixaPedido> itensBaixaPedido = cabecalhoBaixaPedido.getItensBaixaPedido();

		if ( itensBaixaPedido == null || itensBaixaPedido.isEmpty()){
			throw new AppException("A Baixa de Pedido " + cabecalhoBaixaPedido.getId() + " não pode ser calculada pois não possui itens.");
		}

		for (ItemBaixaPedido itemBaixaPedido : itensBaixaPedido) {
			if (itemBaixaPedido.getQuantidade() == null) {
				itemBaixaPedido.setQuantidade(new Long(0));
			}
			if (itemBaixaPedido.getUnitario() == null) {
				itemBaixaPedido.setUnitario(new BigDecimal(0));
			}
			if (itemBaixaPedido.getDescontoItem() == null) {
				itemBaixaPedido.setDescontoItem(new BigDecimal(0));
			}
			
			BigDecimal unitario = itemBaixaPedido.getUnitario();
			BigDecimal quantidade = new BigDecimal(itemBaixaPedido.getQuantidade());
			BigDecimal total = quantidade.multiply(unitario);

			itemBaixaPedido.setTotal(total);
		}

		this.rateiaValoresCabecalhoNosItens(cabecalhoBaixaPedido);

		System.out.println("FIM --- OperacaoBaixaPedido.calculaBaixasPedidos() --->" + (System.currentTimeMillis() - inicio) / 1000);
	}

	/**
	 * Método responsável por verificar se todas as propriedades de preenchimento obrigatório
	 * dos cabecalhos das operações estlo devidamente preenchidas.
	 *  
	 * @throws Exception
	 */
	@Override
	protected void verificaIntegridadeCabecalhos() throws Exception {
		ArrayList<CabecalhoBaixaPedido> cabecalhosOperacao = (ArrayList<CabecalhoBaixaPedido>)this.getDataListCabecalhos().getList();

		for (CabecalhoBaixaPedido cabecalhoOperacao : cabecalhosOperacao) {
			this.verificaIntegridadeCabecalho(cabecalhoOperacao);
		}
	}

	@Override
	public void verificaIntegridadeCabecalho(Cabecalho cabecalho) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoBaixaPedido.verificaIntegridadeOperacoes()");

		CabecalhoBaixaPedido cabecalhoBaixaPedido = (CabecalhoBaixaPedido)cabecalho;
		ArrayList<String> erros = new ArrayList(); 

		if (cabecalhoBaixaPedido == null) {
			erros.add("A propriedade operacao não está preenchida.");
		}

		if (cabecalhoBaixaPedido.getEntidade() == null) {
			erros.add("A propriedade \"entidade\" da operação: " + cabecalhoBaixaPedido.getId() + " não está preenchida.");
		}

		if (cabecalhoBaixaPedido.getEstabelecimento() == null) {
			erros.add("A propriedade \"estabelecimento\" da operação: " + cabecalhoBaixaPedido.getId() + " não está preenchida.");
		}

		if (cabecalhoBaixaPedido.getEmissao() == null) {
			erros.add("A propriedade \"emissão\" da operação: " + cabecalhoBaixaPedido.getId() + " não está preenchida.");
		}

		if (!erros.isEmpty()) {
			throw new AppException(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- OperacaoBaixaPedido.verificaIntegridadeOperacoes()" + (System.currentTimeMillis() - inicio) / 1000);
	}

	@Override
	public void sincronizaItensComCabecalho(Cabecalho cabecalho) throws Exception{
		CabecalhoBaixaPedido cabecalhoBaixaPedido = (CabecalhoBaixaPedido)cabecalho;
		Collection<ItemBaixaPedido> itensBaixasPedido = cabecalhoBaixaPedido.getItensBaixaPedido();
		ArrayList<String> camposCabecalho = new ArrayList<String>(); 

		Long inicio = System.currentTimeMillis();
		System.out.println("   INICIO --- sincronizaItensComCabecalho");

		camposCabecalho.add("estabelecimento");
		camposCabecalho.add("entidade");
		camposCabecalho.add("emissao"); 
		camposCabecalho.add("numero"); 

		for (ItemBaixaPedido itemBaixaPedido : itensBaixasPedido) {
			for (String nomeDoCampo : camposCabecalho) {

				Long inicioAux1 = System.currentTimeMillis();
				System.out.println("   INICIO --- ReflectUtil.setFieldValue(...)");

				ReflectUtil.setFieldValue(itemBaixaPedido, nomeDoCampo, ReflectUtil.getFieldValue(cabecalhoBaixaPedido, nomeDoCampo));

				System.out.println("   FIM --- ReflectUtil.setFieldValue(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux1) /1000) + "s");
			}

			Long inicioAux2 = System.currentTimeMillis();
		}

		System.out.println("   FIM --- sincronizaItensComCabecalho --- Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	/**
	 * Método responsável por verificar se todas as propriedades necessárias para o cálculo dos pedidos,
	 * estão devidamente preenchidas.
	 *  
	 * @throws Exception
	 */
	@Override
	protected void verificaIntegridadeItens() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoBaixaPedido.verificaIntegridadeItens()");

		ArrayList<CabecalhoBaixaPedido> cabecalhos = (ArrayList<CabecalhoBaixaPedido>)this.getDataListCabecalhos().getList();

		for (CabecalhoBaixaPedido cabecalhoBaixaPedido : cabecalhos) {
			this.verificaIntegridadeItensCabecalho(cabecalhoBaixaPedido);
		}

		System.out.println("FIM --- OperacaoBaixaPedido.verificaIntegridadeItens() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000));
	}

	@Override
	protected void verificaIntegridadeItensCabecalho(Cabecalho cabecalho) throws Exception {
		CabecalhoBaixaPedido cabecalhoBaixaPedido = (CabecalhoBaixaPedido) cabecalho;
		Collection<ItemBaixaPedido> itensBaixaPedido = cabecalhoBaixaPedido.getItensBaixaPedido();
		ArrayList<String> erros = new ArrayList<String>(); 

		for (ItemBaixaPedido baixaPedido : itensBaixaPedido) {
			if (baixaPedido.getId() == null) {
				erros.add("A propriedade \"id\" não está preenchida.");
			}

			if (baixaPedido.getCabecalho() == null) {
				erros.add("A propriedade \"operacao\" do pedido: " + baixaPedido.getId() + " não está preenchida.");
			}

			if (baixaPedido.getEntidade() == null) {
				erros.add("A propriedade \"entidade\" do pedido: " + baixaPedido.getId() + " não está preenchida.");
			}

			if (baixaPedido.getEstabelecimento() == null) {
				erros.add("A propriedade \"estabelecimento\" do pedido: " + baixaPedido.getId() + " não está preenchida.");
			}

			if (baixaPedido.getEmissao() == null) {
				erros.add("A propriedade \"emissao\" do pedido: " + baixaPedido.getId() + " não está preenchida.");
			}

			if (baixaPedido.getRecurso() == null) {
				erros.add("A propriedade \"recurso\" do pedido: " + baixaPedido.getId() + " não está preenchida.");
			}

			if (baixaPedido.getNucleo() == null) {
				erros.add("A propriedade \"nucleo\" do pedido: " + baixaPedido.getId() + " não está preenchida.");
			}
		}

		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}
	}

	protected void sincronizaTitulosComCabecalhos() throws Exception {
		ArrayList<CabecalhoBaixaPedido> cabecalhosBaixaPedido = (ArrayList<CabecalhoBaixaPedido>)this.getDataListCabecalhos().getList();
		
		for (CabecalhoBaixaPedido cabecalhoBaixaPedido : cabecalhosBaixaPedido) {
			this.regraTitulo.sincronizaTitulosComCabecalho(cabecalhoBaixaPedido);
		}
	}

	protected void verificaIntegridadeTitulos() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraBaixaPedido.verificaIntegridadeTitulos");

		ArrayList<CabecalhoBaixaPedido> cabecalhosBaixaPedido = (ArrayList<CabecalhoBaixaPedido>)this.getDataListCabecalhos().getList();

		for (CabecalhoOperacaoAbstrato cabecalhoBaixaPedido : cabecalhosBaixaPedido) {
			this.verificaIntegridadeTitulosCabecalho(cabecalhoBaixaPedido);
		}

		System.out.println("FIM --- RegraBaixaPedido.verificaIntegridadeTitulos() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	protected void verificaIntegridadeTitulosCabecalho(CabecalhoOperacaoAbstrato cabecalhoAbstrato) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraBaixaPedido.verificaIntegridadeTitulosCabecalhos()");

		CabecalhoBaixaPedido cabecalho = (CabecalhoBaixaPedido) cabecalhoAbstrato;
		Collection<ItemBaixaPedido> itens = cabecalho.getItensBaixaPedido();
		Collection<Titulo> titulos = cabecalho.getTitulos();

		BigDecimal valorItens = new BigDecimal(0);
		BigDecimal valorTitulos = new BigDecimal(0);

		for (ItemBaixaPedido item : itens) {
			if (item.getItemPedidoBaixado().getCabecalho().getTipoPedido().getId().longValue() != -89999999999294L) {
				valorItens = valorItens.add(item.getPrincipal());
			}
		}

		for (Titulo titulo : titulos) {
			if (titulo.getEmissao() != null && titulo.getVencimento() != null) {
				GregorianCalendar emissao = new GregorianCalendar();
				GregorianCalendar vencimento = new GregorianCalendar();
				
				emissao.setTime(titulo.getEmissao());
				vencimento.setTime(titulo.getVencimento());
				
				emissao.set(emissao.get(Calendar.YEAR), emissao.get(Calendar.MONTH), emissao.get(Calendar.DAY_OF_MONTH),0,0,0);
				emissao.set(Calendar.MILLISECOND, 0);
				vencimento.set(vencimento.get(Calendar.YEAR), vencimento.get(Calendar.MONTH), vencimento.get(Calendar.DAY_OF_MONTH),0,0,0);
				vencimento.set(Calendar.MILLISECOND, 0);
				
				if (vencimento.before(emissao)) {
					throw new RuntimeException("A data de vencimento não pode ser inferior à data de emissão.");
				}
			}
			
			
			valorTitulos = valorTitulos.add(titulo.getPrincipal());
		}

		if (valorItens.compareTo(valorTitulos) != 0) {
			throw new Exception("A soma dos valores dos itens (" + valorItens.toString() + ") da operacao " + cabecalho.getId() + " estão diferentes da soma dos valores dos títulos (" + valorTitulos.toString() + ").");			
		}

		System.out.println("FIM --- RegraBaixaPedido.verificaIntegridadeTitulosCabecalhos() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	@Override
	public void fecha() throws Exception {
		this.getDataListCabecalhos().setLogChanges(false);
		this.getDataListCabecalhos().empty();
		this.getDataListCabecalhos().setLogChanges(true);

		this.preparado = false;
	}
	/**
	 * FIM DE TRECHO PADRONIZADO
	 */

	public void criaTitulosOperacoes() throws Exception {
		List<CabecalhoBaixaPedido> cabecalhosBaixaPedido = this.getDataListCabecalhos().getList();

		for (CabecalhoBaixaPedido cabecalhoBaixaPedido : cabecalhosBaixaPedido) {
			this.criaTitulosOperacao(cabecalhoBaixaPedido);
		}
	}

	public void criaTitulosOperacao(CabecalhoBaixaPedido cabecalho) throws Exception {
		if (cabecalho.getTitulos().isEmpty()){
			
			String pedidosDeConsumo = "";
			
			BigDecimal valorOperacao = new BigDecimal(0);
			
			ArrayList<ItemBaixaPedido> itensBaixaPedido = new ArrayList(cabecalho.getItensBaixaPedido());

			for (ItemBaixaPedido itemBaixaPedido : itensBaixaPedido) {
				if (itemBaixaPedido.getItemPedidoBaixado().getCabecalho().getTipoPedido().getId().longValue() != -89999999999294L) {
					valorOperacao = valorOperacao.add(itemBaixaPedido.getPrincipal());
				} else {
					pedidosDeConsumo += "Aviso: O pedido " + itemBaixaPedido.getId() + " é um pedido de consumo e por isso não gerará títulos.\n";
				}
			}
			
			if (!valorOperacao.equals(new BigDecimal(0))) {
				ArrayList<Titulo> titulos = this.regraTitulo.criaTitulos(cabecalho, cabecalho.getEmissao(), valorOperacao, this.regraId);
				cabecalho.getTitulos().clear();
				cabecalho.getTitulos().addAll(titulos);
			}
			
			if (!pedidosDeConsumo.equals("")) {
				throw new AppException(pedidosDeConsumo);
			}
		}
	}

	/**
	 * 
	 * @param cabecalhoBaixaPedido
	 * @throws Exception 
	 */
	private void rateiaValoresCabecalhoNosItens(CabecalhoBaixaPedido cabecalho) throws Exception {
		//Valores de Cabecalho
		BigDecimal valorFreteOperacao = cabecalho.getFrete();
		BigDecimal valorDescontoOperacao = cabecalho.getDesconto();
		BigDecimal valorIrOperacao = cabecalho.getIr();
		BigDecimal valorCsllOperacao = cabecalho.getCsll();
		BigDecimal valorCofinsOperacao = cabecalho.getCofins();
		BigDecimal valorIssOperacao = cabecalho.getIss();
		BigDecimal valorPisOperacao = cabecalho.getPis();
		BigDecimal valorInssOperacao = cabecalho.getInss();

		//Residuos
		BigDecimal valorResiduoFrete = new BigDecimal(0);
		BigDecimal valorResiduoDesconto = new BigDecimal(0);
		BigDecimal valorResiduoIr = new BigDecimal(0);
		BigDecimal valorResiduoCsll = new BigDecimal(0);
		BigDecimal valorResiduoCofins = new BigDecimal(0);
		BigDecimal valorResiduoIss = new BigDecimal(0);
		BigDecimal valorResiduoPis = new BigDecimal(0);
		BigDecimal valorResiduoInss = new BigDecimal(0);

		BigDecimal valorOperacao = new BigDecimal(0);	
		
		List<ItemBaixaPedido> itensBaixaPedido = (List<ItemBaixaPedido>) cabecalho.getItensBaixaPedido();
		if (itensBaixaPedido != null) {
			valorFreteOperacao = valorFreteOperacao == null ? new BigDecimal(0) : valorFreteOperacao;
			valorDescontoOperacao = valorDescontoOperacao == null ? new BigDecimal(0) : valorDescontoOperacao;
			valorIrOperacao = valorIrOperacao == null ? new BigDecimal(0) : valorIrOperacao;
			valorCsllOperacao = valorCsllOperacao == null ? new BigDecimal(0) : valorCsllOperacao;
			valorCofinsOperacao = valorCofinsOperacao == null ? new BigDecimal(0) : valorCofinsOperacao;
			valorIssOperacao = valorIssOperacao == null ? new BigDecimal(0) : valorIssOperacao;
			valorPisOperacao = valorPisOperacao == null ? new BigDecimal(0) : valorPisOperacao;
			valorInssOperacao = valorInssOperacao == null ? new BigDecimal(0) : valorInssOperacao;
			
			for (ItemBaixaPedido itemBaixaPedido : itensBaixaPedido) {
				valorOperacao = valorOperacao.add( itemBaixaPedido.getTotal() );				
			}
			
			valorResiduoFrete = valorFreteOperacao;
			valorResiduoDesconto = valorDescontoOperacao;
			valorResiduoIr = valorIrOperacao;
			valorResiduoCsll = valorCsllOperacao;
			valorResiduoCofins = valorCofinsOperacao;
			valorResiduoIss = valorIssOperacao;
			valorResiduoPis = valorPisOperacao;
			valorResiduoInss = valorInssOperacao;
			
			for (Iterator<ItemBaixaPedido> iterator = itensBaixaPedido.iterator(); iterator.hasNext(); ) {
				ItemBaixaPedido itemBaixaPedido = (ItemBaixaPedido)iterator.next();
				
				if (itemBaixaPedido.getItemPedidoBaixado().getCabecalho().getTipoPedido().getId().longValue() != -89999999999294L) {
				
					BigDecimal fator = itemBaixaPedido.getTotal().divide(valorOperacao, 9, BigDecimal.ROUND_HALF_UP);
					BigDecimal valorFreteRateado = valorFreteOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal valorDescontoRateado = valorDescontoOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal valorIrRateado = valorIrOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal valorCsllRateado = valorCsllOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal valorCofinsRateado = valorCofinsOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal valorIssRateado = valorIssOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal valorPisRateado = valorPisOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal valorInssRateado = valorInssOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
	
					valorResiduoFrete = valorResiduoFrete.subtract(valorFreteRateado);
					valorResiduoDesconto = valorResiduoDesconto.subtract(valorDescontoRateado);
					valorResiduoIr = valorResiduoIr.subtract(valorIrRateado);
					valorResiduoCsll = valorResiduoCsll.subtract(valorCsllRateado);
					valorResiduoCofins = valorResiduoCofins.subtract(valorCofinsRateado);
					valorResiduoIss = valorResiduoIss.subtract(valorIssRateado);
					valorResiduoPis = valorResiduoPis.subtract(valorPisRateado);
					valorResiduoInss = valorResiduoInss.subtract(valorInssRateado);
					
					if (!iterator.hasNext()) {
						valorFreteRateado = valorFreteRateado.add(valorResiduoFrete);
						valorDescontoRateado = valorDescontoRateado.add(valorResiduoDesconto);
						valorIrRateado = valorIrRateado.add(valorResiduoIr);
						valorCsllRateado = valorCsllRateado.add(valorResiduoCsll);
						valorCofinsRateado = valorCofinsRateado.add(valorResiduoCofins);
						valorIssRateado = valorIssRateado.add(valorResiduoIss);
						valorPisRateado = valorPisRateado.add(valorResiduoPis);
						valorInssRateado = valorInssRateado.add(valorResiduoInss);
					}
					
					itemBaixaPedido.setFrete(valorFreteRateado);
					itemBaixaPedido.setDesconto(valorDescontoRateado);
					itemBaixaPedido.setIr(valorIrRateado);
					itemBaixaPedido.setCsll(valorCsllRateado);
					itemBaixaPedido.setCofins(valorCofinsRateado);
					itemBaixaPedido.setIss(valorIssRateado);
					itemBaixaPedido.setPis(valorPisRateado);
					itemBaixaPedido.setInss(valorInssRateado);
					
					itemBaixaPedido.setBase(itemBaixaPedido.getTotal()
							.add(itemBaixaPedido.getFrete())
							.subtract(itemBaixaPedido.getDesconto())
							.subtract(itemBaixaPedido.getDescontoItem() == null ? new BigDecimal(0) : itemBaixaPedido.getDescontoItem()));
					
					itemBaixaPedido.setPrincipal(itemBaixaPedido.getBase()
							.add(itemBaixaPedido.getIr())
							.add(itemBaixaPedido.getCsll())
							.add(itemBaixaPedido.getCofins())
							.add(itemBaixaPedido.getIss())
							.add(itemBaixaPedido.getPis())
							.add(itemBaixaPedido.getInss()));
				}
			}
		}
	}
}