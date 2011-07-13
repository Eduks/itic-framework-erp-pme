package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.movimentacao.ItemOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;
import br.com.dyad.backoffice.exception.OperacaoIlegalPedidoAprovadoException;
import br.com.dyad.backoffice.operacao.consulta.ConsultaPedido;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.aspect.UserInfoAspect;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

/**
 * @author Franklin Kaswiner
 *
 */
public class RegraPedido extends RegraAbstrata implements InterfaceAprovavel, InterfaceCancelavel {

	private ConsultaPedido consultaPedido;
	
	/**
	 * Class constructor.
	 * 
	 * @param database
	 */
	public RegraPedido(AppTransaction appTransaction) {
		super(appTransaction, "PEDIDO");
		
		this.consultaPedido = new ConsultaPedido(appTransaction);
		
		this.preparado = false;
	}

	/**
	 * Getter's e Setter's
	 */
	public ConsultaPedido getConsultaPedido() {
		return consultaPedido;
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
	public CabecalhoPedido novoCabecalho() throws Exception {
		CabecalhoPedido cabecalho = new CabecalhoPedido();
		cabecalho.createId(this.getAppTransaction().getDatabase());
		cabecalho.setClasseOperacaoId(this.getRegraId());
		
		this.getDataListCabecalhos().add(cabecalho);

		return cabecalho;
	}

	@Override
	public void excluiCabecalho(Cabecalho cabecalho) throws OperacaoIlegalPedidoAprovadoException, Exception {	
		CabecalhoPedido cabecalhoPedido = (CabecalhoPedido) cabecalho;
		
		if (cabecalhoPedido.getAprovacao() != null || cabecalhoPedido.getAprovador() != null) {
			throw new OperacaoIlegalPedidoAprovadoException("Não é possível excluir um pedido já aprovado.");
		}
		
		List<ItemPedido> itensPedido = (List<ItemPedido>) cabecalhoPedido.getItensPedido();
		
		for (ItemPedido itemPedido : itensPedido) {
			this.excluiItem(itemPedido);
		}

		this.getDataListCabecalhos().delete(cabecalhoPedido);
	}

	@Override
	public ItemOperacaoAbstrato novoItem(Cabecalho cabecalho) throws OperacaoIlegalPedidoAprovadoException, Exception {
		if ( cabecalho == null ){
			throw new OperacaoIlegalPedidoAprovadoException("Operacao não pode ser nula!");
		}

		if ( !this.getDataListCabecalhos().find( "id", cabecalho.getId() ) ){
			throw new OperacaoIlegalPedidoAprovadoException("Esta operação não foi encontrada neste objeto!");
		}

		CabecalhoPedido cabecalhoPedido = (CabecalhoPedido) cabecalho;
		
//		if (cabecalhoPedido.getAprovacao() != null || cabecalhoPedido.getAprovador() != null) {
//			throw new OperacaoIlegalPedidoAprovadoException("Não é possível adicionar um item a um pedido já aprovado.");
//		}
		
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.createId(this.getAppTransaction().getDatabase());
		itemPedido.setCabecalho(cabecalhoPedido);
		itemPedido.setClasseOperacaoId( this.regraId );

		cabecalhoPedido.addItemPedido(itemPedido);

		return itemPedido;
	}

	@Override
	public void excluiItem( Item item ) throws OperacaoIlegalPedidoAprovadoException {
		ItemPedido itemPedido = (ItemPedido) item;
		
//		if (itemPedido.getCabecalho().getAprovacao() != null || itemPedido.getCabecalho().getAprovacao() != null) {
//			throw new OperacaoIlegalPedidoAprovadoException("Não é possível excluir um pedido já aprovado.");
//		}
		
		itemPedido.getCabecalho().removeItemPedido(itemPedido);

		this.getAppTransaction().delete(itemPedido);
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
	public void abre(Long idOperacao, Long idItem, String numero, Long entidadeId, Long estabelecimentoId, Long recursoId, Long nucleoId, Date emissaoInicial, Date emissaoFinal) throws Exception {
		
		ArrayList<String> where = new ArrayList<String>();
		ArrayList<Object> params = new ArrayList<Object>();
		String select = "from " + CabecalhoPedido.class.getName() + " ";
				
		if (!this.preparado) {
			this.preparaRegra();
		}
		
		if (idOperacao != null) {
			where.add(" id = ? ");
			params.add(idOperacao);
		}
		
		if (idItem != null) {
			where.add(" id IN (SELECT cabecalho.id FROM "+ItemPedido.class.getName()+" WHERE id = ? )");
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
			params.add(estabelecimentoId);
		}
		
		if (recursoId != null) {
			where.add(" id IN (SELECT cabecalho.id FROM "+ItemPedido.class.getName()+" WHERE recurso.id = ? )");
			params.add(recursoId);
		}
		
		if (nucleoId != null) {
			where.add(" id IN (SELECT cabecalho.id FROM "+ItemPedido.class.getName()+" WHERE nucleo.id = ? )");
			params.add(nucleoId);
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
			throw new RuntimeException("Deve ser informada pelo menos uma restrição para a busca.");
		}

		String query = select + " where " + StringUtils.join(where, " and ");
		
		DataList dataListCabecalhos = DataListFactory.newDataList(this.getAppTransaction());
		this.getAppTransaction().clear();
		
		dataListCabecalhos.setList(PersistenceUtil.executeHql((Session)this.getAppTransaction().getSession(), query, params));
		
		this.getDataListCabecalhos().add(dataListCabecalhos);
		
	}

	@Override
	public void aprova(Long operacaoId) throws Exception {
		CabecalhoPedido cabecalhoPedido = (CabecalhoPedido)this.getDataListCabecalhos().getOne("id", operacaoId);
		
		cabecalhoPedido.setAprovacao(new Date());
		cabecalhoPedido.setAprovador(UserInfoAspect.getUserReference());

		this.calculaOperacao(cabecalhoPedido);

		this.getDataListCabecalhos().save(cabecalhoPedido);
	}

	@Override
	public void desaprova(Long operacaoId) throws Exception {
		CabecalhoPedido cabecalho = (CabecalhoPedido)this.getDataListCabecalhos().getOne("id", operacaoId);

		cabecalho.setAprovacao(null);
		cabecalho.setAprovador(null);

		this.getDataListCabecalhos().save(cabecalho);
	}

	@Override
	public void cancela(Long operacaoId) throws Exception {
		CabecalhoPedido cabecalho = (CabecalhoPedido)this.getDataListCabecalhos().getOne("id", operacaoId);

		cabecalho.setCancelamento(new Date());
		cabecalho.setCancelador(UserInfoAspect.getUserReference());

		this.getDataListCabecalhos().save(cabecalho);
	}

	@Override
	public void descancela(Long operacaoId) throws Exception {
		CabecalhoPedido cabecalho = (CabecalhoPedido)this.getDataListCabecalhos().getOne("id", operacaoId);

		cabecalho.setCancelamento(null);
		cabecalho.setCancelador(null);

		this.getDataListCabecalhos().save(cabecalho);
	}

	/**
	 * INICIO DE TRECHO PADRONIZADO
	 */
	@Override
	public void grava() throws Exception {
		this.preparaGravacao();

		DataList.commit(this.getDataListCabecalhos());
	}

	@Override
	public void preparaGravacao() throws Exception {
		this.calculaOperacoes();
		this.verificaIntegridadeItens();
	}

	/**
	 * @throws Exception 
	 * 
	 */
	@Override
	public void calculaOperacoes() throws Exception {
		List<CabecalhoPedido> cabecalhosPedido = this.getDataListCabecalhos().getList();

		for (CabecalhoPedido cabecalhoPedido : cabecalhosPedido) {
			this.calculaOperacao(cabecalhoPedido);
		}
	}

	/**
	 * @throws Exception 
	 * 
	 */
	@Override
	public void calculaOperacao(Cabecalho cabecalho) throws Exception {
		CabecalhoPedido cabecalhoPedido = (CabecalhoPedido)cabecalho;
		
		this.verificaIntegridadeCabecalho(cabecalhoPedido);
		this.sincronizaItensComCabecalho(cabecalhoPedido);

		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.calculaPedidos()");

		List<ItemPedido> itensPedido = (List<ItemPedido>) cabecalhoPedido.getItensPedido();

		if ( itensPedido == null || itensPedido.isEmpty()){
			throw new AppException("O Pedido " + cabecalhoPedido.getId() + " não pode ser calculado pois não possui itens.");
		}
		
		for (ItemPedido itemPedido : itensPedido) {
			
			if (itemPedido.getQuantidade() == null) {
				itemPedido.setQuantidade(new Long(0));
			}
			if (itemPedido.getUnitario() == null) {
				itemPedido.setUnitario(new BigDecimal(0));
			}
			if (itemPedido.getDescontoItem() == null) {
				itemPedido.setDescontoItem(new BigDecimal(0));
			}
			
			BigDecimal unitario = itemPedido.getUnitario();
			BigDecimal quantidade = new BigDecimal(itemPedido.getQuantidade());
			BigDecimal total = quantidade.multiply(unitario);
			
			itemPedido.setTotal(total);
		}
		
		this.rateiaValoresCabecalhoNosItens(cabecalhoPedido);
		System.out.println("FIM --- OperacaoPedido.calculaPedidos() Tempo execução->" + ((System.currentTimeMillis() - inicio) / 1000) + "s");
	}
	
	/**
	 * Método responsável por verificar se todas as propriedades de preenchimento obrigatório
	 * dos cabecalhos das operações estão devidamente preenchidas.
	 * @throws Exception 
	 */
	@Override
	protected void verificaIntegridadeCabecalhos() throws Exception {
		ArrayList<CabecalhoPedido> listCabecalhos = (ArrayList<CabecalhoPedido>)this.getDataListCabecalhos().getList();

		for (CabecalhoPedido cabecalhoOperacao : listCabecalhos) {
			this.verificaIntegridadeCabecalho(cabecalhoOperacao);
		}
	}
	
	@Override
	protected void verificaIntegridadeCabecalho(Cabecalho cabecalho) throws Exception {
		CabecalhoPedido cabecalhoPedido = (CabecalhoPedido)cabecalho;
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraPedido.verificaIntegridadeCabecalho()");

		ArrayList<String> erros = new ArrayList<String>(); 

		if (cabecalhoPedido.getId() == null) {
			erros.add("A propriedade operacao não está preenchida.");
		}

		if (cabecalhoPedido.getEntidade() == null) {
			erros.add("A propriedade \"entidade\" da operação: " + cabecalhoPedido.getId() + " não está preenchida.");
		}

		if (cabecalhoPedido.getEstabelecimento() == null) {
			erros.add("A propriedade \"estabelecimento\" da operação: " + cabecalhoPedido.getId() + " não está preenchida.");
		}

		if (cabecalhoPedido.getEmissao() == null) {
			erros.add("A propriedade \"emissão\" da operação: " + cabecalhoPedido.getId() + " não está preenchida.");
		}

		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- RegraPedido.verificaIntegridadeCabecalho()" + (System.currentTimeMillis() - inicio) / 1000);
	}
	
	@Override
	protected void sincronizaItensComCabecalho(Cabecalho cabecalho) throws Exception{
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraPedido.sincronizaItensComCabecalho()");

		CabecalhoPedido cabecalhoPedido = (CabecalhoPedido) cabecalho;
		
		List<ItemPedido> itensPedido = (List<ItemPedido>) cabecalhoPedido.getItensPedido();
		ArrayList<String> camposCabecalho = new ArrayList<String>(); 

		camposCabecalho.add("tipoPedido");
		camposCabecalho.add("entidade");
		camposCabecalho.add("estabelecimento"); 
		camposCabecalho.add("emissao"); 
		camposCabecalho.add("numero"); 
		camposCabecalho.add("aprovacao"); 
		camposCabecalho.add("aprovador"); 
		camposCabecalho.add("cancelamento"); 
		camposCabecalho.add("cancelador"); 

		for (ItemPedido itemPedido : itensPedido) {
			for (String nomeDoCampo : camposCabecalho) {

				Long inicioAux1 = System.currentTimeMillis();
				System.out.println("   INICIO --- ReflectUtil.setFieldValue(...)");

				ReflectUtil.setFieldValue(itemPedido, nomeDoCampo, ReflectUtil.getFieldValue(cabecalhoPedido, nomeDoCampo));

				System.out.println("   FIM --- ReflectUtil.setFieldValue(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux1) /1000) + "s");
			}
		}

		System.out.println("FIM --- OperacaoPedido.sincronizaItensComCabecalho() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	@Override
	protected void verificaIntegridadeItens() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.verificaIntegridadePedidos()");

		ArrayList<CabecalhoPedido> cabecalhosPedido = (ArrayList<CabecalhoPedido>)this.getDataListCabecalhos().getList();
		
		for (CabecalhoPedido cabecalhoPedido : cabecalhosPedido) {
			this.verificaIntegridadeItensCabecalho(cabecalhoPedido);
		}

		System.out.println("FIM --- OperacaoPedido.verificaIntegridadePedidos() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	@Override
	protected void verificaIntegridadeItensCabecalho(Cabecalho cabecalhoAbstrato) throws Exception {
		CabecalhoPedido cabecalhoPedido = (CabecalhoPedido) cabecalhoAbstrato;
		
		List<ItemPedido> itensBaixaPedido = (List<ItemPedido>) cabecalhoPedido.getItensPedido();
		ArrayList<String> erros = new ArrayList<String>(); 
		
		for (ItemPedido itemPedido : itensBaixaPedido) {
			if (itemPedido.getId() == null) {
				erros.add("A propriedade \"id\" não está preenchida.");
			}

			if (itemPedido.getCabecalho() == null) {
				erros.add("A propriedade \"operacao\" do pedido: " + itemPedido.getId() + " não está preenchida.");
			}

			if (itemPedido.getEntidade() == null) {
				erros.add("A propriedade \"entidade\" do pedido: " + itemPedido.getId() + " não está preenchida.");
			}

			if (itemPedido.getEstabelecimento() == null) {
				erros.add("A propriedade \"estabelecimento\" do pedido: " + itemPedido.getId() + " não está preenchida.");
			}

			if (itemPedido.getEmissao() == null) {
				erros.add("A propriedade \"emissao\" do pedido: " + itemPedido.getId() + " não está preenchida.");
			}

			if (itemPedido.getRecurso() == null) {
				erros.add("A propriedade \"recurso\" do pedido: " + itemPedido.getId() + " não está preenchida.");
			}

			if (itemPedido.getNucleo() == null) {
				erros.add("A propriedade \"nucleo\" do pedido: " + itemPedido.getId() + " não está preenchida.");
			}
		}
		
		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}
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

	/**
	 * 
	 * @param cabecalho
	 * @throws Exception 
	 */
	private void rateiaValoresCabecalhoNosItens(CabecalhoPedido cabecalho) throws Exception {
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
		
		List<ItemPedido> itensPedido = (List<ItemPedido>) cabecalho.getItensPedido();
		if (itensPedido != null) {
			valorFreteOperacao = valorFreteOperacao == null ? new BigDecimal(0) : valorFreteOperacao;
			valorDescontoOperacao = valorDescontoOperacao == null ? new BigDecimal(0) : valorDescontoOperacao;
			valorIrOperacao = valorIrOperacao == null ? new BigDecimal(0) : valorIrOperacao;
			valorCsllOperacao = valorCsllOperacao == null ? new BigDecimal(0) : valorCsllOperacao;
			valorCofinsOperacao = valorCofinsOperacao == null ? new BigDecimal(0) : valorCofinsOperacao;
			valorIssOperacao = valorIssOperacao == null ? new BigDecimal(0) : valorIssOperacao;
			valorPisOperacao = valorPisOperacao == null ? new BigDecimal(0) : valorPisOperacao;
			valorInssOperacao = valorInssOperacao == null ? new BigDecimal(0) : valorInssOperacao;
			
			for (ItemPedido itemPedido : itensPedido) {
				valorOperacao = valorOperacao.add( itemPedido.getTotal() );				
			}
			
			valorResiduoFrete = valorFreteOperacao;
			valorResiduoDesconto = valorDescontoOperacao;
			valorResiduoIr = valorIrOperacao;
			valorResiduoCsll = valorCsllOperacao;
			valorResiduoCofins = valorCofinsOperacao;
			valorResiduoIss = valorIssOperacao;
			valorResiduoPis = valorPisOperacao;
			valorResiduoInss = valorInssOperacao;
			
			for (Iterator<ItemPedido> iterator = itensPedido.iterator(); iterator.hasNext(); ) {
				ItemPedido itemPedido = (ItemPedido)iterator.next();
				
				if (itemPedido.getCabecalho().getTipoPedido().getId() != -89999999999294L) {
					
					BigDecimal fator = itemPedido.getTotal().divide(valorOperacao, 9, BigDecimal.ROUND_HALF_UP);
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
					
					itemPedido.setFrete(valorFreteRateado);
					itemPedido.setDesconto(valorDescontoRateado);
					itemPedido.setIr(valorIrRateado);
					itemPedido.setCsll(valorCsllRateado);
					itemPedido.setCofins(valorCofinsRateado);
					itemPedido.setIss(valorIssRateado);
					itemPedido.setPis(valorPisRateado);
					itemPedido.setInss(valorInssRateado);
					
					itemPedido.setBase(itemPedido.getTotal()
							.add(itemPedido.getFrete())
							.subtract(itemPedido.getDesconto())
							.subtract(itemPedido.getDescontoItem() == null ? new BigDecimal(0) : itemPedido.getDescontoItem()));
					
					itemPedido.setPrincipal(itemPedido.getBase()
							.add(itemPedido.getIr())
							.add(itemPedido.getCsll())
							.add(itemPedido.getCofins())
							.add(itemPedido.getIss())
							.add(itemPedido.getPis())
							.add(itemPedido.getInss()));
				} else {
					itemPedido.setFrete(new BigDecimal(0));
					itemPedido.setDesconto(new BigDecimal(0));
					itemPedido.setBase(new BigDecimal(0));
					itemPedido.setPrincipal(new BigDecimal(0));
				}
			}
		}
	}
}

