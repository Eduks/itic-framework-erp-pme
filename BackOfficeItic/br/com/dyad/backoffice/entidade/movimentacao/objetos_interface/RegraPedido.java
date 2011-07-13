package br.com.dyad.backoffice.entidade.movimentacao.objetos_interface;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.annotations.Cabecalho;
import br.com.dyad.backoffice.annotations.TipoCampoCabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemOperacao;
import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoPedidoDesconto;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoPedidoFrete;
import br.com.dyad.backoffice.operacao.consulta.ConsultaPedido;
import br.com.dyad.businessinfrastructure.entidades.evento.Evento;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;

public class RegraPedido extends RegraAbstrata implements InterfaceAprovavel, InterfaceCancelavel {

	private static HashMap<String,ArrayList<String>> cacheDeCamposPorClasse = new HashMap<String,ArrayList<String>>(); 

	private DataList dataListEventos;
	private ConsultaPedido consultaPedido;

	/**
	 * Class constructor.
	 * 
	 * @param database
	 */
	public RegraPedido(AppTransaction appTransaction) {
		super(appTransaction);
		//TODO Mudar o ID para uma chave
		this.regraId = "PEDIDO";

		this.dataListEventos = DataListFactory.newDataList(appTransaction);

		this.consultaPedido = new ConsultaPedido(appTransaction);
		
		this.preparado = false;
	}

	/**
	 * Getter´s e Setter´s
	 */
	public DataList getDataListEventos() {
		return this.dataListEventos;
	}

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
		CabecalhoPedido cabecalhoPedido = new CabecalhoPedido(this.getAppTransaction().getDatabase());
		cabecalhoPedido.setClasseOperacaoId(this.getRegraId());
		
		this.dataListCabecalhos.add(cabecalhoPedido);

		return cabecalhoPedido;
	}

	public void removeCabecalho( CabecalhoPedido cabecalhoPedido ) throws Exception {	
		Collection<ItemPedido> itensPedidos = this.getPedidosCabecalho(cabecalhoPedido);
		for (ItemPedido itemPedido : itensPedidos ) {
			this.removeItem(itemPedido);
		}

		this.dataListCabecalhos.delete(cabecalhoPedido);
	}

	@Override
	public ItemOperacao novoItem(CabecalhoOperacaoAbstrato cabecalhoOperacao) throws Exception {
		if ( cabecalhoOperacao == null ){
			throw new Exception("Operacao não pode ser nula!");
		}

		if ( !this.dataListCabecalhos.findId( cabecalhoOperacao ) ){
			throw new Exception("Esta operação não foi encontrada neste objeto!");
		}

		ItemOperacao itemOperacao = new ItemPedido();
		itemOperacao.createId(this.getAppTransaction().getDatabase());
		itemOperacao.setOperacaoId(cabecalhoOperacao.getOperacaoId());
		itemOperacao.setClasseOperacaoId( this.regraId );

		this.dataListItens.add(itemOperacao);

		return itemOperacao;
	}

	public void removeItem( ItemPedido itemPedido ) throws Exception {
		this.dataListItens.delete(itemPedido);
	}

	@Override
	/**
	 * Exemplo: 
	 * Desejo abrir todos os pedidos da pessoa 12324L cuja emissao esteja entre 01/01/2008 a 01/01/2009. 
	 * OperacaoPedido opPedido = new OperacaoPedido();
	 * opPedido.getConsulta().setEntidadeId( 12324L );
	 * opPedido.getConsulta().setEmissaoInicio( 01/01/2008 );
	 * opPedido.getConsulta().setEmissaoFim( 01/01/2009 );
	 * opPedido.abre();
	 **/
	public void abre() throws Exception {
		if (!this.preparado) {
			this.preparaRegra();
		}

		//TODO transformar em um método
		ArrayList<ItemPedido> itensPedidos = this.consultaPedido.pegaPedidosAsList();
		
		if (itensPedidos != null && !itensPedidos.isEmpty()) {

			this.dataListItens.setLogChanges(false);
			for (ItemPedido itemPedido : itensPedidos) {
				this.dataListItens.add(itemPedido);
			}
			this.dataListItens.setLogChanges(true);

			//TODO transformar em um método
			ArrayList<EventoCabecalhoPedido> eventos = this.consultaPedido.pegaEventosCabecalhoPedidos(itensPedidos);
			if (!eventos.isEmpty()) {
				this.dataListEventos.setLogChanges(false);
				for (EventoCabecalhoPedido evento : eventos) {
					this.dataListEventos.add(evento);
				}
				this.dataListEventos.setLogChanges(true);
			}

			this.preparaCabecalhos();
		}
	}

	private void preparaCabecalhos() throws Exception{
		Field[] camposCabecalho = CabecalhoPedido.class.getDeclaredFields();
		Set<String> operacoesId = this.dataListItens.getDistinctValues("operacaoId");

		for (String operacaoId : operacoesId) {
			Long operacaoIdLong = new Long(operacaoId);

			ArrayList<ItemPedido> itensPedidos = new ArrayList<ItemPedido>(this.getPedidosCabecalho(operacaoIdLong));
			CabecalhoPedido cabecalhoOperacao = new CabecalhoPedido(this.getAppTransaction().getDatabase(), operacaoIdLong);

			ArrayList<Field> camposReplica = new ArrayList<Field>();

			for (Field campoCabecalho : camposCabecalho) {
				Cabecalho annotationCabecalho = campoCabecalho.getAnnotation(Cabecalho.class);

				if (annotationCabecalho != null) {
					if ( annotationCabecalho.value() == TipoCampoCabecalho.REPLICA) {
						camposReplica.add(campoCabecalho);
					}
				}
			}

			ItemPedido pedidoReplica = itensPedidos.get(0);
			for (Field campo : camposReplica ) {
				Object objTemp = ReflectUtil.getFieldValue(pedidoReplica, campo.getName());

				if (objTemp != null) {
					ReflectUtil.setFieldValue(cabecalhoOperacao, campo.getName(), objTemp);
				}
			}

			//*********************

			EventoCabecalhoPedidoDesconto eventoDesconto = (EventoCabecalhoPedidoDesconto)this.dataListEventos.getOne("operacaoId;classId", operacaoIdLong, "-99999899999919" /* EventoCabecalhoPedidoDesconto */ );
			if (eventoDesconto != null) {
				cabecalhoOperacao.setDesconto(eventoDesconto.getValor());
			}

			EventoCabecalhoPedidoFrete eventoFrete = (EventoCabecalhoPedidoFrete)this.dataListEventos.getOne("operacaoId;classId", operacaoIdLong, "-99999899999920" /* EventoCabecalhoPedidoFrete */ );
			if (eventoFrete != null) {
				cabecalhoOperacao.setFrete(eventoFrete.getValor());
			}

			//*********************

			this.dataListCabecalhos.add(cabecalhoOperacao);
		}
	}

	@Override
	public void aprova(Long operacaoId) throws Exception {
		CabecalhoPedido cabecalhoPedido = (CabecalhoPedido)this.dataListCabecalhos.getOne("operacaoId", operacaoId);
		
		this.calculaOperacao(cabecalhoPedido);

		cabecalhoPedido.setAprovacao(new Date());

		this.dataListCabecalhos.save(cabecalhoPedido);
	}

	@Override
	public void desaprova(Long operacaoId) throws Exception {
		CabecalhoPedido cabecalho = (CabecalhoPedido)this.dataListCabecalhos.getOne("operacaoId", operacaoId);

		cabecalho.setAprovacao(null);

		this.dataListCabecalhos.save(cabecalho);
	}

	@Override
	public void cancela(Long operacaoId) throws Exception {
		CabecalhoPedido cabecalho = (CabecalhoPedido)this.dataListCabecalhos.getOne("operacaoId", operacaoId);

		cabecalho.setCancelamento(new Date());

		this.dataListCabecalhos.save(cabecalho);
	}

	@Override
	public void descancela(Long operacaoId) throws Exception {
		CabecalhoPedido cabecalho = (CabecalhoPedido)this.dataListCabecalhos.getOne("operacaoId", operacaoId);

		cabecalho.setCancelamento(null);

		this.dataListCabecalhos.save(cabecalho);
	}

	@Override
	public void exclui(Long operacaoId) throws Exception {
		
		//Cabecalho
		this.dataListCabecalhos.delete(this.dataListCabecalhos.getOne("operacaoId", operacaoId));

		//Itens
		Collection<AppEntity> itensOperacao = this.dataListItens.getRangeAsCollection("operacaoId", operacaoId);
		List<AppEntity> listItens = new ArrayList<AppEntity>(itensOperacao);

		for (Iterator iterator = listItens.iterator(); iterator.hasNext(); ) {
			AppEntity appEntity = (AppEntity) iterator.next();
			this.dataListItens.delete(appEntity);
		}		

		//Eventos
		Collection<AppEntity> eventosOperacao = this.dataListEventos.getRangeAsCollection("operacaoId", operacaoId);
		List<AppEntity> listEventos = new ArrayList<AppEntity>(eventosOperacao);
		
		for (Iterator iterator = listEventos.iterator(); iterator.hasNext(); ) {
			AppEntity appEntity = (AppEntity) iterator.next();
			this.dataListEventos.delete(appEntity);
		}
	}

	@Override
	public void grava() throws Exception {
		this.preparaGravacao();

		DataList.commit(this.dataListItens, this.dataListEventos);
	}

	@Override
	public void preparaGravacao() throws Exception {
		this.calculaOperacoes();

		this.criaEventosCabecalhoPedido();

		this.verificaIntegridadePedidos();

		this.verificaIntegridadeEventos();
	}

	/**
	 * Cria os eventos relacionados ao pedido
	 * @throws Exception		
	 */
	private void criaEventosCabecalhoPedido() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.criaEventosCabecalhoPedido()");

		Date dataHoje = new Date();

		ArrayList<CabecalhoPedido> listOperacoes = (ArrayList<CabecalhoPedido>)this.dataListCabecalhos.getList();
		for (CabecalhoPedido cabecalhoOperacao : listOperacoes) {

			//Cria Evento de Frete
			Collection<AppEntity> eventosFrete = this.dataListEventos.getRangeAsCollection("operacaoId;classId", cabecalhoOperacao.getOperacaoId(), -99999899999920L /* EventoCabecalhoPedidoFrete */ );

			if (cabecalhoOperacao.getFrete() == null || cabecalhoOperacao.getFrete().compareTo( new BigDecimal(0)) == 0 ) {
				if (eventosFrete != null) {
					
					for (Iterator iterator = eventosFrete.iterator(); iterator.hasNext(); ) {
						EventoCabecalhoPedidoFrete eventoFrete = (EventoCabecalhoPedidoFrete) iterator.next();
						this.dataListEventos.delete(eventoFrete);
					}
				}
			} else {
				if (eventosFrete == null) {
					EventoCabecalhoPedidoFrete eventoFrete = new EventoCabecalhoPedidoFrete();

					eventoFrete.createId(this.getAppTransaction().getDatabase());
					eventoFrete.setOperacaoId(cabecalhoOperacao.getOperacaoId());
					eventoFrete.setData(dataHoje);
					eventoFrete.setHora(dataHoje);
					eventoFrete.setValor(cabecalhoOperacao.getFrete());

					this.dataListEventos.addAndSave(eventoFrete);
				} else {
					boolean freteCriado = false;

					for (Iterator iterator = eventosFrete.iterator(); iterator.hasNext(); ) {
						EventoCabecalhoPedidoFrete eventoFrete = (EventoCabecalhoPedidoFrete) iterator.next();

						if (!freteCriado) {
							eventoFrete.setValor(cabecalhoOperacao.getFrete());
							eventoFrete.setData(new Date());
							this.dataListEventos.save(eventoFrete);
							freteCriado = true;
						} else {
							this.dataListEventos.delete(eventoFrete);
						}
					}
				}
			}

			//Cria Evento de Desconto
			Collection<AppEntity> eventosDesconto = this.dataListEventos.getRangeAsCollection("operacaoId;classId", cabecalhoOperacao.getOperacaoId(), -99999899999919L /* EventoCabecalhoPedidoDesconto */ );
			
			if (cabecalhoOperacao.getDesconto() == null || cabecalhoOperacao.getDesconto().compareTo( new BigDecimal(0)) == 0 ) {
				if (eventosDesconto != null) {
					
					for (Iterator iterator = eventosDesconto.iterator(); iterator.hasNext(); ) {
						EventoCabecalhoPedidoDesconto eventoDesconto = (EventoCabecalhoPedidoDesconto) iterator.next();
						this.dataListEventos.delete(eventoDesconto);
					}
				}
			} else {

				if (eventosDesconto == null) {
					EventoCabecalhoPedidoDesconto eventoDesconto = new EventoCabecalhoPedidoDesconto();

					eventoDesconto.createId(this.getAppTransaction().getDatabase());
					eventoDesconto.setOperacaoId(cabecalhoOperacao.getOperacaoId());
					eventoDesconto.setData(dataHoje);
					eventoDesconto.setHora(dataHoje);
					eventoDesconto.setValor(cabecalhoOperacao.getDesconto());

					this.dataListEventos.addAndSave(eventoDesconto);

				} else {
					boolean descontoCriado = false;

					for (Iterator iterator = eventosDesconto.iterator(); iterator.hasNext(); ) {
						EventoCabecalhoPedidoDesconto eventoDesconto = (EventoCabecalhoPedidoDesconto) iterator.next();

						if (!descontoCriado) {
							eventoDesconto.setValor(cabecalhoOperacao.getDesconto());
							eventoDesconto.setData(new Date());
							this.dataListEventos.save(eventoDesconto);
							descontoCriado = true;
						} else {
							this.dataListEventos.delete(eventoDesconto);
						}
					}
				}
			}
		}
		System.out.println("FIM --- OperacaoPedido.criaEventosCabecalhoPedido() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	/**
	 * Método responsável por verificar se todas as propriedades necessárias para o cálculo dos pedidos,
	 * estão devidamente preenchidas.
	 *  
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void verificaIntegridadePedidos() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.verificaIntegridadePedidos()");

		ArrayList<ItemPedido> listPedidos =	(ArrayList<ItemPedido>)this.dataListItens.getList();
		ArrayList<String> erros = new ArrayList(); 

		for (ItemPedido itemPedido : listPedidos) {
			if (itemPedido.getId() == null) {
				erros.add("A propriedade \"id\" não está preenchida.");
			}

			if (itemPedido.getOperacaoId() == null) {
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

		System.out.println("FIM --- OperacaoPedido.verificaIntegridadePedidos() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	private void verificaIntegridadeEventos() throws Exception {
		List<CabecalhoPedido> cabecalhos = this.dataListCabecalhos.getList();

		for (CabecalhoPedido cabecalho : cabecalhos) {

			Long operacaoId = cabecalho.getOperacaoId();
			Collection<ItemPedido> itemPedidos = (Collection<ItemPedido>) this.getPedidosCabecalho(operacaoId);
			Collection<Evento> eventos = (Collection)this.dataListEventos.getRangeAsCollection("operacaoId", operacaoId);

			if (eventos != null) {
				for (Evento evento : eventos) {

					if (EventoCabecalhoPedidoFrete.class.isInstance(evento)) {
						EventoCabecalhoPedidoFrete eventoFrete = (EventoCabecalhoPedidoFrete)evento;
						BigDecimal valorFretePedidos = new BigDecimal(0).setScale(2);

						for (ItemPedido itemPedido : itemPedidos) {
							if (itemPedido.getFrete() != null) {
								valorFretePedidos = valorFretePedidos.add(itemPedido.getFrete());
							}
						}
						if (valorFretePedidos.compareTo(eventoFrete.getValor()) != 0) {
							throw new Exception("Valor do Evento de Frete está divergente do valor nos Pedidos.");
						}

					} else if (EventoCabecalhoPedidoDesconto.class.isInstance(evento)) {
						EventoCabecalhoPedidoDesconto eventoDesconto = (EventoCabecalhoPedidoDesconto)evento;
						BigDecimal valorDescontoPedidos = new BigDecimal(0).setScale(2);

						for (ItemPedido itemPedido : itemPedidos) {
							if (itemPedido.getDesconto() != null) {
								valorDescontoPedidos = valorDescontoPedidos.add(itemPedido.getDesconto());
							}
						}
						if (valorDescontoPedidos.compareTo(eventoDesconto.getValor()) != 0 ) {
							throw new Exception("Val or do Evento de Desconto está divergente do valor nos Pedidos.");
						}
					}
				}
			}
		}
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public void calculaOperacoes() throws Exception {
		List<CabecalhoPedido> cabecalhosPedido = this.dataListCabecalhos.getList();

		for (CabecalhoPedido cabecalhoPedido : cabecalhosPedido) {
			this.calculaOperacao(cabecalhoPedido);
		}
	}

	/**
	 * Método responsável por verificar se todas as propriedades de preenchimento obrigatório
	 * dos cabecalhos das operações estlo devidamente preenchidas.
	 *  
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void verificaIntegridadeCabecalhos() throws Exception {
		ArrayList<CabecalhoPedido> listCabecalhos = (ArrayList<CabecalhoPedido>)this.dataListCabecalhos.getList();

		for (CabecalhoPedido cabecalhoOperacao : listCabecalhos) {
			verificaIntegridadeCabecalho(cabecalhoOperacao);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void verificaIntegridadeCabecalho(CabecalhoPedido cabecalhoPedido) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraPedido.verificaIntegridadeCabecalho()");

		ArrayList<String> erros = new ArrayList(); 

		if (cabecalhoPedido.getOperacaoId() == null) {
			erros.add("A propriedade operacao não está preenchida.");
		}

		if (cabecalhoPedido.getEntidade() == null) {
			erros.add("A propriedade \"entidade\" da operação: " + cabecalhoPedido.getOperacaoId() + " nï¿½o estï¿½ preenchida.");
		}

		if (cabecalhoPedido.getEstabelecimento() == null) {
			erros.add("A propriedade \"estabelecimento\" da operação: " + cabecalhoPedido.getOperacaoId() + " nï¿½o estï¿½ preenchida.");
		}

		if (cabecalhoPedido.getEmissao() == null) {
			erros.add("A propriedade \"emissão\" da operação: " + cabecalhoPedido.getOperacaoId() + " nï¿½o estï¿½ preenchida.");
		}

		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- RegraPedido.verificaIntegridadeCabecalho()" + (System.currentTimeMillis() - inicio) / 1000);
	}

	private void sincronizaItensComCabecalho(CabecalhoPedido cabecalhoOperacao) throws Exception{

		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.sincronizaItensComCabecalho()");

		ArrayList<ItemPedido> pedidos = this.getPedidosCabecalho( cabecalhoOperacao );
		ArrayList<String> camposCabecalho = cacheDeCamposPorClasse.get( cabecalhoOperacao.getClass().getName() ); 

		if ( camposCabecalho == null ) {
			camposCabecalho = new ArrayList<String>();

			Field[] fields = cabecalhoOperacao.getClass().getDeclaredFields();
			for (Field field : fields) {
				Cabecalho annotationCabecalho = field.getAnnotation(Cabecalho.class);

				if (annotationCabecalho != null && annotationCabecalho.value() == TipoCampoCabecalho.REPLICA) {
					camposCabecalho.add(field.getName());
				}
			}
			cacheDeCamposPorClasse.put(cabecalhoOperacao.getClass().getName(), camposCabecalho);
		} 

		for (ItemPedido itemPedido : pedidos) {
			for (String nomeDoCampo : camposCabecalho) {

				Long inicioAux1 = System.currentTimeMillis();
				System.out.println("   INICIO --- ReflectUtil.setFieldValue(...)");

				ReflectUtil.setFieldValue(itemPedido, nomeDoCampo, ReflectUtil.getFieldValue(cabecalhoOperacao, nomeDoCampo));

				System.out.println("   FIM --- ReflectUtil.setFieldValue(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux1) /1000) + "s");
			}

			Long inicioAux2 = System.currentTimeMillis();
			System.out.println("   INICIO --- dataListItens.save(...)");
			
			this.dataListItens.save(itemPedido);
			
			System.out.println("   FIM --- dataListItens.save(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux2) /1000) + "s");
		}

		System.out.println("FIM --- OperacaoPedido.sincronizaItensComCabecalho() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public void calculaOperacao(CabecalhoPedido cabecalhoPedido) throws Exception {
		this.verificaIntegridadeCabecalho(cabecalhoPedido);
		this.sincronizaItensComCabecalho(cabecalhoPedido);

		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.calculaPedidos()");

		Collection<AppEntity> itensPedido = this.dataListItens.getRangeAsCollection("operacaoId", cabecalhoPedido.getOperacaoId());

		for (Iterator iterator = itensPedido.iterator(); iterator.hasNext(); ) {
			ItemPedido itemPedido = (ItemPedido)iterator.next();
			
			BigDecimal unitario = itemPedido.getUnitario();
			BigDecimal quantidade = new BigDecimal(itemPedido.getQuantidade());
			BigDecimal total = quantidade.multiply(unitario);
			
			itemPedido.setTotal(total);
			this.dataListItens.save(itemPedido);
		}
		
		this.rateiaValoresCabecalhoNosItens(cabecalhoPedido);
		System.out.println("FIM --- OperacaoPedido.calculaPedidos() Tempo execução->" + ((System.currentTimeMillis() - inicio) / 1000) + "s");
	}
	
	/**
	 * 
	 * @param cabecalhoPedido
	 * @throws Exception 
	 */
	private void rateiaValoresCabecalhoNosItens(CabecalhoPedido cabecalhoPedido) throws Exception {
		BigDecimal valorFreteOperacao = cabecalhoPedido.getFrete();
		BigDecimal valorDescontoOperacao = cabecalhoPedido.getDesconto();
		BigDecimal valorResiduoFrete = new BigDecimal(0);
		BigDecimal valorResiduoDesconto = new BigDecimal(0);
		BigDecimal valorOperacao = new BigDecimal(0);	
		
		Collection<AppEntity> itensPedido = this.dataListItens.getRangeAsCollection("operacaoId", cabecalhoPedido.getOperacaoId());
		if (itensPedido != null) {
			valorFreteOperacao = valorFreteOperacao == null ? new BigDecimal(0) : valorFreteOperacao;
			valorDescontoOperacao = valorDescontoOperacao == null ? new BigDecimal(0) : valorDescontoOperacao;
			
			for (Iterator iterator = itensPedido.iterator(); iterator.hasNext(); ) {
				ItemPedido itemPedido = (ItemPedido)iterator.next();
				
				valorOperacao = valorOperacao.add( itemPedido.getTotal() );				
			}
			
			valorResiduoFrete = valorFreteOperacao;
			valorResiduoDesconto = valorDescontoOperacao;
			for (Iterator iterator = itensPedido.iterator(); iterator.hasNext(); ) {
				ItemPedido itemPedido = (ItemPedido)iterator.next();
				BigDecimal fator = itemPedido.getTotal().divide(valorOperacao, 9, BigDecimal.ROUND_HALF_UP);
				BigDecimal valorFreteRateado = valorFreteOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal valorDescontoRateado = valorDescontoOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);

				valorResiduoFrete = valorResiduoFrete.subtract(valorFreteRateado);
				valorResiduoDesconto = valorResiduoDesconto.subtract(valorDescontoRateado);
				
				if (!iterator.hasNext()) {
					valorFreteRateado = valorFreteRateado.add(valorResiduoFrete);
					valorDescontoRateado = valorDescontoRateado.add(valorResiduoDesconto);
				}
				
				itemPedido.setFrete(valorFreteRateado);
				itemPedido.setDesconto(valorDescontoRateado);
				
				this.dataListItens.save(itemPedido);
			}
		}
	}

	@Override
	public void fecha() throws Exception {
		this.dataListCabecalhos.setLogChanges(false);
		this.dataListItens.setLogChanges(false);
		this.dataListEventos.setLogChanges(false);
		
		this.dataListCabecalhos.empty();
		this.dataListItens.empty();
		this.dataListEventos.empty();

		this.dataListCabecalhos.setLogChanges(true);
		this.dataListItens.setLogChanges(true);
		this.dataListEventos.setLogChanges(true);

		this.preparado = false;
	}

	public ArrayList<ItemPedido> getPedidosCabecalho( CabecalhoOperacaoAbstrato cabecalhoPedido ) throws Exception{
		return getPedidosCabecalho( cabecalhoPedido.getOperacaoId() );
	}

	@SuppressWarnings("unchecked")
	public ArrayList<ItemPedido> getPedidosCabecalho( Long operacaoId ) throws Exception{
		Collection<AppEntity> pedidos = this.dataListItens.getRangeAsCollection("operacaoId", operacaoId );
		
		if ( pedidos != null ){
			return new ArrayList(pedidos);
		} else {
			return new ArrayList<ItemPedido>();
		}
	}
}

