package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.ItemOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;
import br.com.dyad.backoffice.operacao.consulta.ConsultaPedidoBaixaAutomatica;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.aspect.UserInfoAspect;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class RegraBaixaAutomatica extends RegraAbstrata implements InterfaceAprovavel, InterfaceCancelavel {
	
	private RegraTitulo regraTitulo;
	private ConsultaPedidoBaixaAutomatica consultaPedidoBaixaAutomatica;
	
	private ArrayList<Object> parametrosConsulta;
	
	public RegraBaixaAutomatica(AppTransaction appTransaction) {
		super(appTransaction, "PEDIDO_BAIXA_AUTOMATICA");
		
		this.regraTitulo = new RegraTitulo(this.getAppTransaction());
		this.consultaPedidoBaixaAutomatica = new ConsultaPedidoBaixaAutomatica(appTransaction);
		this.preparado = false;
	}

	public ConsultaPedidoBaixaAutomatica getConsultaPedidoBaixaAutomatica() {
		return consultaPedidoBaixaAutomatica;
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
	public CabecalhoBaixaAutomatica novoCabecalho() throws Exception {
		CabecalhoBaixaAutomatica cabecalho = new CabecalhoBaixaAutomatica();
		cabecalho.createId(this.getAppTransaction().getDatabase());
		cabecalho.setClasseOperacaoId(this.getRegraId());
		
		this.getDataListCabecalhos().add(cabecalho);

		return cabecalho;
	}

	@Override
	public ItemOperacaoAbstrato novoItem(Cabecalho cabecalhoAbstrato) throws Exception {
		if ( cabecalhoAbstrato == null ){
			throw new Exception("Operacao não pode ser nula!");
		}

		if ( !this.getDataListCabecalhos().findId(cabecalhoAbstrato.getId()) ){
			throw new Exception("Esta operação não foi encontrada neste objeto!");
		}

		CabecalhoBaixaAutomatica cabecalho = (CabecalhoBaixaAutomatica) cabecalhoAbstrato;
		
		ItemBaixaAutomatica item = new ItemBaixaAutomatica();
		item.createId(this.getAppTransaction().getDatabase());
		item.setCabecalho((CabecalhoBaixaAutomatica)cabecalho);
		item.setClasseOperacaoId( this.regraId );
		
		cabecalho.addItemBaixaAutomatica(item);
		
		return item;
	}

	@Override
	public void excluiItem(Item item) throws Exception {
		ItemBaixaAutomatica itemBaixaAutomatica = (ItemBaixaAutomatica) item;
		itemBaixaAutomatica.getCabecalho().removeItemBaixaAutomatica(itemBaixaAutomatica);
		
		this.getAppTransaction().delete(itemBaixaAutomatica);
	}

	public Titulo novoTitulo(CabecalhoOperacaoAbstrato cabecalhoAbstrato) throws Exception {
		Titulo titulo = this.regraTitulo.novoTitulo(cabecalhoAbstrato, this.regraId);
		titulo.setClasseOperacaoId( this.regraId );
		
		return titulo;
	}
	
	public void excluiTitulo(Titulo titulo) throws Exception {
		CabecalhoBaixaAutomatica cabecalho = (CabecalhoBaixaAutomatica)titulo.getCabecalho();
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
	 **/
	public void abre(Long idCabecalho, Long idItem, String numero, Long entidadeId, Long estabelecimentoId, Long recursoId, Long nucleoId, Date emissaoInicial, Date emissaoFinal) throws Exception {
		if (!this.preparado) {
			this.preparaRegra();
		}
		
		parametrosConsulta = new ArrayList<Object>(0);
		
		String select = "from " + CabecalhoBaixaAutomatica.class.getName() + " ";
		ArrayList<String> where = new ArrayList<String>();
		
		if (idCabecalho != null) {
			where.add(" id = ? ");
			parametrosConsulta.add(idCabecalho);
		}
		
		if (idItem != null) {
			where.add(" id IN (SELECT cabecalho.id FROM "+ItemBaixaAutomatica.class.getName()+" WHERE id = ? )");
			parametrosConsulta.add(idItem);
		}
		
		if (numero != null) {
			where.add(" numero = ? ");
			parametrosConsulta.add(numero);
		}

		if (entidadeId != null) {
			where.add(" entidade.id = ? ");
			parametrosConsulta.add(entidadeId);
		}

		if (estabelecimentoId != null) {
			where.add(" estabelecimento.id = ? ");
			parametrosConsulta.add(estabelecimentoId);
		}
		
		if (recursoId != null) {
			where.add(" id IN (SELECT cabecalho.id FROM "+ItemBaixaAutomatica.class.getName()+" WHERE recurso.id = ? )");
			parametrosConsulta.add(recursoId);
		}
		
		if (nucleoId != null) {
			where.add(" id IN (SELECT cabecalho.id FROM "+ItemBaixaAutomatica.class.getName()+" WHERE nucleo.id = ? )");
			parametrosConsulta.add(nucleoId);
		}

		if (emissaoInicial != null) {
			where.add(" emissao >= ? ");
			parametrosConsulta.add(emissaoInicial);
		}

		if (emissaoFinal != null) {
			where.add(" emissao <= ? ");
			parametrosConsulta.add(emissaoFinal);
		}

		String query = select + " where " + StringUtils.join(where, " and ");
		
		DataList dataListCabecalhos = DataListFactory.newDataList(this.getAppTransaction());
		dataListCabecalhos.setList(PersistenceUtil.executeHql((Session)getAppTransaction().getSession(), query, parametrosConsulta));
		
		this.getDataListCabecalhos().add(dataListCabecalhos);
	}

	@Override
	public void aprova(Long operacaoId) throws Exception {
		CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica = (CabecalhoBaixaAutomatica)this.getDataListCabecalhos().getOne("id", operacaoId);
		
		cabecalhoBaixaAutomatica.setAprovacao(new Date());
		cabecalhoBaixaAutomatica.setAprovador(UserInfoAspect.getUserReference());
		
		this.calculaOperacao(cabecalhoBaixaAutomatica);
		this.criaTitulosOperacao(cabecalhoBaixaAutomatica);
		
		this.getDataListCabecalhos().save(cabecalhoBaixaAutomatica);
	}

	@Override
	public void desaprova(Long operacaoId) throws Exception {
		CabecalhoBaixaAutomatica cabecalho = (CabecalhoBaixaAutomatica)this.getDataListCabecalhos().getOne("id", operacaoId);

		cabecalho.setAprovacao(null);
		cabecalho.setAprovador(null);
		this.getDataListCabecalhos().save(cabecalho);
	}

	@Override
	public void cancela(Long operacaoId) throws Exception {
		CabecalhoBaixaAutomatica cabecalho = (CabecalhoBaixaAutomatica)this.getDataListCabecalhos().getOne("id", operacaoId);

		cabecalho.setCancelamento(new Date());
		cabecalho.setCancelador(UserInfoAspect.getUserReference());
		this.getDataListCabecalhos().save(cabecalho);
	}

	@Override
	public void descancela(Long operacaoId) throws Exception {
		CabecalhoBaixaAutomatica cabecalho = (CabecalhoBaixaAutomatica)this.getDataListCabecalhos().getOne("id", operacaoId);

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
	 */
	@Override
	public void calculaOperacoes() throws Exception {
		List<CabecalhoBaixaAutomatica> cabecalhosPedido = this.getDataListCabecalhos().getList();

		for (CabecalhoBaixaAutomatica cabecalhoPedido : cabecalhosPedido) {
			this.calculaOperacao(cabecalhoPedido);
		}
	}

	/**
	 * @throws Exception 
	 * 
	 */
	@Override
	public void calculaOperacao(Cabecalho cabecalho) throws Exception {
		CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica = (CabecalhoBaixaAutomatica)cabecalho;
		
		this.verificaIntegridadeCabecalho(cabecalhoBaixaAutomatica);
		this.sincronizaItensComCabecalho(cabecalhoBaixaAutomatica);
		
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraPedido.calculaPedidos()");

		List<ItemBaixaAutomatica> itensBaixaAutomatica = (List<ItemBaixaAutomatica>) cabecalhoBaixaAutomatica.getItensBaixaAutomatica();

		if ( itensBaixaAutomatica == null || itensBaixaAutomatica.isEmpty()){
			throw new AppException("A Baixa Automática " + cabecalhoBaixaAutomatica.getId() + " não pode ser calculada pois não possui itens.");
		}
		
		for (ItemBaixaAutomatica itemBaixaAutomatica : itensBaixaAutomatica) {
			if (itemBaixaAutomatica.getQuantidade() == null) {
				itemBaixaAutomatica.setQuantidade(new Long(0));
			}
			if (itemBaixaAutomatica.getUnitario() == null) {
				itemBaixaAutomatica.setUnitario(new BigDecimal(0));
			}
			if (itemBaixaAutomatica.getDescontoItem() == null) {
				itemBaixaAutomatica.setDescontoItem(new BigDecimal(0));
			}
			
			BigDecimal unitario = itemBaixaAutomatica.getUnitario();
			BigDecimal quantidade = new BigDecimal(itemBaixaAutomatica.getQuantidade());
			BigDecimal total = quantidade.multiply(unitario);
			
			itemBaixaAutomatica.setTotal(total);
		}
		
		this.rateiaValoresCabecalhoNosItens(cabecalhoBaixaAutomatica);
		System.out.println("FIM --- RegraPedido.calculaPedidos() Tempo execução->" + ((System.currentTimeMillis() - inicio) / 1000) + "s");
	}
	
	/**
	 * Método responsável por verificar se todas as propriedades de preenchimento obrigatório
	 * dos cabecalhos das operações estlo devidamente preenchidas.
	 *  
	 * @throws Exception
	 */
	@Override
	protected void verificaIntegridadeCabecalhos() throws Exception {
		ArrayList<CabecalhoBaixaAutomatica> cabecalhos = (ArrayList<CabecalhoBaixaAutomatica>)this.getDataListCabecalhos().getList();

		for (CabecalhoBaixaAutomatica cabecalhoOperacao : cabecalhos) {
			this.verificaIntegridadeCabecalho(cabecalhoOperacao);
		}
	}
	
	@Override
	protected void verificaIntegridadeCabecalho(Cabecalho cabecalhoOperacao) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraPedido.verificaIntegridadeCabecalho()");

		ArrayList<String> erros = new ArrayList<String>(); 
		CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica = (CabecalhoBaixaAutomatica)cabecalhoOperacao;

		if (cabecalhoBaixaAutomatica.getId() == null) {
			erros.add("A propriedade operacao não está preenchida.");
		}

		if (cabecalhoBaixaAutomatica.getEntidade() == null) {
			erros.add("A propriedade \"entidade\" da operação: " + cabecalhoBaixaAutomatica.getId() + " não está preenchida.");
		}

		if (cabecalhoBaixaAutomatica.getEstabelecimento() == null) {
			erros.add("A propriedade \"estabelecimento\" da operação: " + cabecalhoBaixaAutomatica.getId() + " não está preenchida.");
		}

		if (cabecalhoBaixaAutomatica.getEmissao() == null) {
			erros.add("A propriedade \"emissão\" da operação: " + cabecalhoBaixaAutomatica.getId() + " não está preenchida.");
		}

		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- RegraPedido.verificaIntegridadeCabecalho()" + (System.currentTimeMillis() - inicio) / 1000);
	}

	@Override
	protected void sincronizaItensComCabecalho(Cabecalho cabecalhoAbstrato) throws Exception{
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.sincronizaItensComCabecalho()");
		
		CabecalhoBaixaAutomatica cabecalho = (CabecalhoBaixaAutomatica) cabecalhoAbstrato;
		ArrayList<ItemBaixaAutomatica> itensBaixasAutomatica = new ArrayList<ItemBaixaAutomatica>(cabecalho.getItensBaixaAutomatica());
		ArrayList<String> camposCabecalho = new ArrayList<String>(); 

		camposCabecalho.add("tipoPedido");
		camposCabecalho.add("entidade");
		camposCabecalho.add("estabelecimento"); 
		camposCabecalho.add("emissao"); 
		camposCabecalho.add("numero"); 
		camposCabecalho.add("aprovacao"); 
		camposCabecalho.add("aprovador"); 
		
		for (ItemBaixaAutomatica itemPedido : itensBaixasAutomatica) {
			for (String nomeDoCampo : camposCabecalho) {

				Long inicioAux1 = System.currentTimeMillis();
				System.out.println("   INICIO --- ReflectUtil.setFieldValue(...)");

				ReflectUtil.setFieldValue(itemPedido, nomeDoCampo, ReflectUtil.getFieldValue(cabecalho, nomeDoCampo));

				System.out.println("   FIM --- ReflectUtil.setFieldValue(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux1) /1000) + "s");
			}
		}

		System.out.println("FIM --- OperacaoPedido.sincronizaItensComCabecalho() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
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
		System.out.println("INICIO --- OperacaoPedidoBaixaAutomatica.verificaIntegridadeItens()");

		ArrayList<CabecalhoBaixaAutomatica> cabecalhosPedidoBaixaAutomatica = (ArrayList<CabecalhoBaixaAutomatica>)this.getDataListCabecalhos().getList();
		
		for (CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica : cabecalhosPedidoBaixaAutomatica) {
			this.verificaIntegridadeItensCabecalho(cabecalhoBaixaAutomatica);
		}

		System.out.println("FIM --- OperacaoPedidoBaixaAutomatica.verificaIntegridadeItens() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	@Override
	protected void verificaIntegridadeItensCabecalho(Cabecalho cabecalhoOperacao) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraBaixaAutomatica.verificaIntegridadeItensCabecalho()");
		
		CabecalhoBaixaAutomatica cabecalho = (CabecalhoBaixaAutomatica)cabecalhoOperacao;

		List<ItemBaixaAutomatica> listPedidos = (List<ItemBaixaAutomatica>)cabecalho.getItensBaixaAutomatica();
		ArrayList<String> erros = new ArrayList<String>(); 

		for (ItemBaixaAutomatica itemBaixaAutomatica : listPedidos) {
			if (itemBaixaAutomatica.getId() == null) {
				erros.add("A propriedade \"id\" não está preenchida.");
			}

			if (itemBaixaAutomatica.getCabecalho() == null) {
				erros.add("A propriedade \"cabecalho\" do pedido: " + itemBaixaAutomatica.getId() + " não está preenchida.");
			}

			if (itemBaixaAutomatica.getEntidade() == null) {
				erros.add("A propriedade \"entidade\" do pedido: " + itemBaixaAutomatica.getId() + " não está preenchida.");
			}

			if (itemBaixaAutomatica.getEstabelecimento() == null) {
				erros.add("A propriedade \"estabelecimento\" do pedido: " + itemBaixaAutomatica.getId() + " não está preenchida.");
			}

			if (itemBaixaAutomatica.getEmissao() == null) {
				erros.add("A propriedade \"emissao\" do pedido: " + itemBaixaAutomatica.getId() + " não está preenchida.");
			}

			if (itemBaixaAutomatica.getRecurso() == null) {
				erros.add("A propriedade \"recurso\" do pedido: " + itemBaixaAutomatica.getId() + " não está preenchida.");
			}

			if (itemBaixaAutomatica.getNucleo() == null) {
				erros.add("A propriedade \"nucleo\" do pedido: " + itemBaixaAutomatica.getId() + " não está preenchida.");
			}
		}

		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- RegraBaixaAutomatica.verificaIntegridadeItensCabecalho() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	protected void sincronizaTitulosComCabecalhos() throws Exception {
		ArrayList<CabecalhoBaixaAutomatica> cabecalhosPedidoBaixaAutomatica = (ArrayList<CabecalhoBaixaAutomatica>)this.getDataListCabecalhos().getList();
		
		for (CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica : cabecalhosPedidoBaixaAutomatica) {
			this.regraTitulo.sincronizaTitulosComCabecalho(cabecalhoBaixaAutomatica);
		}
	}

	protected void verificaIntegridadeTitulos() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedidoBaixaAutomatica.verificaIntegridadeItens()");

		ArrayList<CabecalhoBaixaAutomatica> cabecalhosPedidoBaixaAutomatica = (ArrayList<CabecalhoBaixaAutomatica>)this.getDataListCabecalhos().getList();
		
		for (CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica : cabecalhosPedidoBaixaAutomatica) {
			this.verificaIntegridadeTitulosCabecalho(cabecalhoBaixaAutomatica);
		}

		System.out.println("FIM --- OperacaoPedidoBaixaAutomatica.verificaIntegridadeItens() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	protected void verificaIntegridadeTitulosCabecalho(CabecalhoOperacaoAbstrato cabecalhoAbstrato) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.verificaIntegridadePedidos()");

		CabecalhoBaixaAutomatica cabecalho = (CabecalhoBaixaAutomatica) cabecalhoAbstrato;
		List<ItemBaixaAutomatica> itens = (List<ItemBaixaAutomatica>) cabecalho.getItensBaixaAutomatica();
		List<Titulo> titulos = (List<Titulo>) cabecalho.getTitulos();
		
		BigDecimal valorItens = new BigDecimal(0);
		BigDecimal valorTitulos = new BigDecimal(0);

		for (ItemBaixaAutomatica item : itens) {
			if (item.getCabecalho().getTipoPedido().getId().longValue() != -89999999999294L) {
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

		System.out.println("FIM --- OperacaoPedido.verificaIntegridadePedidos() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	@Override
	public void excluiCabecalho(Cabecalho cabecalho) throws Exception {
		CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica = (CabecalhoBaixaAutomatica) cabecalho;
		ArrayList<ItemBaixaAutomatica> itensBaixaAutomatica = new ArrayList<ItemBaixaAutomatica>(cabecalhoBaixaAutomatica.getItensBaixaAutomatica()); 
		
		for (ItemOperacaoAbstrato itemBaixaAutomatica : itensBaixaAutomatica) {
			this.excluiItem(itemBaixaAutomatica);
		}

		this.getDataListCabecalhos().delete(cabecalhoBaixaAutomatica);
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
		List<CabecalhoBaixaAutomatica> cabecalhosBaixaAutomatica = this.getDataListCabecalhos().getList();

		for (CabecalhoBaixaAutomatica cabecalhoBaixaAutomatica : cabecalhosBaixaAutomatica) {
			this.criaTitulosOperacao(cabecalhoBaixaAutomatica);
		}
	}
	
	public void criaTitulosOperacao(CabecalhoBaixaAutomatica cabecalho) throws Exception {
		if (cabecalho.getTitulos().isEmpty()) {
			BigDecimal valorOperacao = new BigDecimal(0);

			List<ItemBaixaAutomatica> itensBaixaAutomatica = (List<ItemBaixaAutomatica>)cabecalho.getItensBaixaAutomatica();

			for (ItemBaixaAutomatica itemBaixaAutomatica : itensBaixaAutomatica) {
				if (itemBaixaAutomatica.getCabecalho().getTipoPedido().getId().longValue() != -89999999999294L) {
					valorOperacao = valorOperacao.add(itemBaixaAutomatica.getPrincipal());
				}
			}
			
			if (!valorOperacao.equals(new BigDecimal(0))) {
				ArrayList<Titulo> titulos = this.regraTitulo.criaTitulos(cabecalho, cabecalho.getEmissao(), valorOperacao, this.regraId);
				cabecalho.getTitulos().clear();
				cabecalho.getTitulos().addAll(titulos);
			}
		}
	}
	
	/**
	 * 
	 * @param cabecalho
	 * @throws Exception 
	 */
	private void rateiaValoresCabecalhoNosItens(CabecalhoBaixaAutomatica cabecalho) throws Exception {
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
		
		List<ItemBaixaAutomatica> itensBaixaAutomatica = (List<ItemBaixaAutomatica>) cabecalho.getItensBaixaAutomatica();
		if (itensBaixaAutomatica != null) {
			valorFreteOperacao = valorFreteOperacao == null ? new BigDecimal(0) : valorFreteOperacao;
			valorDescontoOperacao = valorDescontoOperacao == null ? new BigDecimal(0) : valorDescontoOperacao;
			valorIrOperacao = valorIrOperacao == null ? new BigDecimal(0) : valorIrOperacao;
			valorCsllOperacao = valorCsllOperacao == null ? new BigDecimal(0) : valorCsllOperacao;
			valorCofinsOperacao = valorCofinsOperacao == null ? new BigDecimal(0) : valorCofinsOperacao;
			valorIssOperacao = valorIssOperacao == null ? new BigDecimal(0) : valorIssOperacao;
			valorPisOperacao = valorPisOperacao == null ? new BigDecimal(0) : valorPisOperacao;
			valorInssOperacao = valorInssOperacao == null ? new BigDecimal(0) : valorInssOperacao;
			
			for (ItemBaixaAutomatica itemBaixaAutomatica : itensBaixaAutomatica) {
				valorOperacao = valorOperacao.add( itemBaixaAutomatica.getTotal() );				
			}
			
			valorResiduoFrete = valorFreteOperacao;
			valorResiduoDesconto = valorDescontoOperacao;
			valorResiduoIr = valorIrOperacao;
			valorResiduoCsll = valorCsllOperacao;
			valorResiduoCofins = valorCofinsOperacao;
			valorResiduoIss = valorIssOperacao;
			valorResiduoPis = valorPisOperacao;
			valorResiduoInss = valorInssOperacao;
			
			for (Iterator<ItemBaixaAutomatica> iterator = itensBaixaAutomatica.iterator(); iterator.hasNext(); ) {
				ItemBaixaAutomatica itemBaixaAutomatica = (ItemBaixaAutomatica)iterator.next();
				
				if (itemBaixaAutomatica.getCabecalho().getTipoPedido().getId().longValue() != -89999999999294L) {
				
					BigDecimal fator = itemBaixaAutomatica.getTotal().divide(valorOperacao, 9, BigDecimal.ROUND_HALF_UP);
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
					
					itemBaixaAutomatica.setFrete(valorFreteRateado);
					itemBaixaAutomatica.setDesconto(valorDescontoRateado);
					itemBaixaAutomatica.setIr(valorIrRateado);
					itemBaixaAutomatica.setCsll(valorCsllRateado);
					itemBaixaAutomatica.setCofins(valorCofinsRateado);
					itemBaixaAutomatica.setIss(valorIssRateado);
					itemBaixaAutomatica.setPis(valorPisRateado);
					itemBaixaAutomatica.setInss(valorInssRateado);
					
					itemBaixaAutomatica.setBase(itemBaixaAutomatica.getTotal()
							.add(itemBaixaAutomatica.getFrete())
							.subtract(itemBaixaAutomatica.getDesconto())
							.subtract(itemBaixaAutomatica.getDescontoItem() == null ? new BigDecimal(0) : itemBaixaAutomatica.getDescontoItem()));
					
					itemBaixaAutomatica.setPrincipal(itemBaixaAutomatica.getBase()
							.add(itemBaixaAutomatica.getIr())
							.add(itemBaixaAutomatica.getCsll())
							.add(itemBaixaAutomatica.getCofins())
							.add(itemBaixaAutomatica.getIss())
							.add(itemBaixaAutomatica.getPis())
							.add(itemBaixaAutomatica.getInss()));
				} else {
					itemBaixaAutomatica.setFrete(new BigDecimal(0));
					itemBaixaAutomatica.setDesconto(new BigDecimal(0));
					itemBaixaAutomatica.setBase(new BigDecimal(0));
					itemBaixaAutomatica.setPrincipal(new BigDecimal(0));
				}
			}
		}
	}
}