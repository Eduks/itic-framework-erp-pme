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
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoBaixaPedidoDesconto;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoBaixaPedidoFrete;
import br.com.dyad.backoffice.operacao.consulta.ConsultaBaixaPedido;
import br.com.dyad.businessinfrastructure.entidades.evento.Evento;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;

public class RegraBaixaPedido extends RegraAbstrata {
	
	private RegraTitulo regraTitulo;

	private static HashMap<String,ArrayList<String>> cacheDeCamposPorClasse = new HashMap<String,ArrayList<String>>(); 

	private DataList dataListEventos;

	private ConsultaBaixaPedido consultaBaixasPedido;
	
	/**
	 * Class constructor.
	 * 
	 * @param database
	 */
	public RegraBaixaPedido(AppTransaction appTransaction) {
		super(appTransaction);
		//TODO Mudar o ID para uma chave
		this.regraId = "BAIXA_PEDIDO";
		
		this.regraTitulo = new RegraTitulo(this.getAppTransaction(), this.getCabecalhosOperacao(), this.getItens());
		
		this.dataListEventos = DataListFactory.newDataList(this.getAppTransaction());

		this.consultaBaixasPedido = new ConsultaBaixaPedido(this.getAppTransaction());
		
		this.preparado = false;
	}

	/**
	 * Getter´s e Setter´s
	 */
	public RegraTitulo getRegraTituloBaixaPedido() {
		return this.regraTitulo; 
	}

	public DataList getDataListEventos() {
		return this.dataListEventos;
	}

	public ConsultaBaixaPedido getConsultaBaixaPedido() {
		return this.consultaBaixasPedido;
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
	public CabecalhoBaixaPedido novoCabecalho() throws Exception {
		CabecalhoBaixaPedido cabecalhoBaixaPedido = new CabecalhoBaixaPedido(this.getAppTransaction().getDatabase());
		cabecalhoBaixaPedido.setClasseOperacaoId(this.getRegraId());
		
		this.dataListCabecalhos.add(cabecalhoBaixaPedido);

		return cabecalhoBaixaPedido;
	}

	public void removeCabecalho( CabecalhoBaixaPedido cabecalhoBaixaPedido ) throws Exception {	
		Collection<ItemBaixaPedido> itensBaixaPedido = this.getBaixasPedidoCabecalho(cabecalhoBaixaPedido);
		for (ItemBaixaPedido itemBaixaPedido : itensBaixaPedido ) {
			this.removeItem(itemBaixaPedido);
		}

		this.dataListCabecalhos.delete(cabecalhoBaixaPedido);
	}
	
	@Override
	public ItemOperacao novoItem(CabecalhoOperacaoAbstrato cabecalhoOperacao) throws Exception {
		if ( cabecalhoOperacao == null ){
			throw new Exception("Operacao não pode ser nula!");
		}

		if ( !this.dataListCabecalhos.find("operacaoId", cabecalhoOperacao.getOperacaoId() ) ){
			throw new Exception("Esta operação não foi encontrada neste objeto!");
		}

		ItemOperacao itemOperacao = new ItemBaixaPedido();
		itemOperacao.createId(this.getAppTransaction().getDatabase());
		itemOperacao.setOperacaoId(cabecalhoOperacao.getOperacaoId());
		itemOperacao.setClasseOperacaoId( this.regraId );

		this.dataListItens.add(itemOperacao);

		return itemOperacao;
	}

	public void removeItem( ItemBaixaPedido itemBaixaPedido ) throws Exception {
		this.dataListItens.delete(itemBaixaPedido);
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
		ArrayList<ItemBaixaPedido> itensBaixaPedido = this.consultaBaixasPedido.pegaBaixasPedidoAsList();
		
		if (itensBaixaPedido != null && !itensBaixaPedido.isEmpty()) {

			ArrayList<Long> operacoesId = new ArrayList<Long>();
			
			this.dataListItens.setLogChanges(false);
			for (ItemBaixaPedido itemBaixaPedido : itensBaixaPedido) {
				this.dataListItens.add(itemBaixaPedido);

				if (!operacoesId.contains(itemBaixaPedido.getOperacaoId())) {
					operacoesId.add(itemBaixaPedido.getOperacaoId());
				}
			}
			this.dataListItens.setLogChanges(true);

			//TODO transformar em um método
			ArrayList<EventoCabecalhoBaixaPedido> eventos = this.consultaBaixasPedido.pegaEventosCabecalhoOperacaoIdAsList(operacoesId);
			if (!eventos.isEmpty()) {
				this.dataListEventos.setLogChanges(false);
				for (EventoCabecalhoBaixaPedido evento : eventos) {
					this.dataListEventos.add(evento);
				}
				this.dataListEventos.setLogChanges(true);
			}
			
			this.regraTitulo.getConsultaTitulo().getParametroOperacoesId().limpaValores();
			this.regraTitulo.getConsultaTitulo().getParametroOperacoesId().setListaDeIds(operacoesId);
			this.regraTitulo.abre();
			
			this.preparaCabecalhos();
		}
	}
	
	private void preparaCabecalhos() throws Exception {
		Field[] camposCabecalho = CabecalhoBaixaPedido.class.getDeclaredFields();
		Set<String> operacoesId = this.dataListItens.getDistinctValues("operacaoId");

		for (String operacaoId : operacoesId) {
			Long operacaoIdLong = new Long(operacaoId);

			ArrayList<ItemBaixaPedido> itensBaixaPedido = new ArrayList<ItemBaixaPedido>(this.getBaixasCabecalho(operacaoIdLong));
			CabecalhoBaixaPedido cabecalhoOperacao = new CabecalhoBaixaPedido(this.getAppTransaction().getDatabase(), operacaoIdLong);

			ArrayList<Field> camposReplica = new ArrayList<Field>();
			
			for (Field campoCabecalho : camposCabecalho) {
				Cabecalho annotationCabecalho = campoCabecalho.getAnnotation(Cabecalho.class);

				if (annotationCabecalho != null) {
					if ( annotationCabecalho.value() == TipoCampoCabecalho.REPLICA) {
						camposReplica.add(campoCabecalho);
					}
				}
			}
			
			ItemBaixaPedido baixaPedidoReplica = itensBaixaPedido.get(0);
			for (Field campo : camposReplica ) {
				Object objTemp = ReflectUtil.getFieldValue(baixaPedidoReplica, campo.getName());

				if (objTemp != null) {
					ReflectUtil.setFieldValue(cabecalhoOperacao, campo.getName(), objTemp);
				}
			}
			
			//*********************
			
			EventoCabecalhoBaixaPedidoDesconto eventoDesconto = (EventoCabecalhoBaixaPedidoDesconto)this.dataListEventos.getOne("operacaoId;classId", operacaoIdLong, "-99999899999207" /* EventoCabecalhoBaixaPedidoDesconto */ );
			if (eventoDesconto != null) {
				cabecalhoOperacao.setDesconto(eventoDesconto.getValor());
			}
			
			EventoCabecalhoBaixaPedidoFrete eventoFrete = (EventoCabecalhoBaixaPedidoFrete)this.dataListEventos.getOne("operacaoId;classId", operacaoIdLong, "-99999899999206" /* EventoCabecalhoBaixaPedidoFrete */ );
			if (eventoFrete != null) {
				cabecalhoOperacao.setFrete(eventoFrete.getValor());
			}
			
			//*********************

			this.dataListCabecalhos.add(cabecalhoOperacao);
		}
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

		DataList.commit(this.dataListItens, this.dataListEventos, this.getRegraTituloBaixaPedido().dataListItens);
	}
	 
	@Override
	public void preparaGravacao() throws Exception {
		this.calculaOperacoes();
		
		this.criaTitulosOperacoes();
		
		this.criaEventosCabecalhoBaixaPedido();

		this.verificaIntegridadeBaixasPedido();

		this.verificaIntegridadeEventos();

		this.verificaIntegridadeTitulos();
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public void calculaOperacoes() throws Exception {
		List<CabecalhoBaixaPedido> cabecalhosBaixaPedido = this.dataListCabecalhos.getList();

		for (CabecalhoBaixaPedido cabecalhoBaixaPedido : cabecalhosBaixaPedido) {
			this.calculaOperacao(cabecalhoBaixaPedido);
		}
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public void calculaOperacao(CabecalhoBaixaPedido cabecalhoBaixaPedido) throws Exception {
		this.verificaIntegridadeCabecalho(cabecalhoBaixaPedido);
		this.sincronizaItensComCabecalho(cabecalhoBaixaPedido);

		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.calculaBaixasPedidos()");
		
		Collection<AppEntity> itensBaixaPedido = this.dataListItens.getRangeAsCollection("operacaoId", cabecalhoBaixaPedido.getOperacaoId());
		
		for (Iterator iterator = itensBaixaPedido.iterator(); iterator.hasNext(); ) {
			ItemBaixaPedido itemBaixaPedido = (ItemBaixaPedido)iterator.next();

			BigDecimal unitario = itemBaixaPedido.getUnitario();
			BigDecimal quantidade = new BigDecimal(itemBaixaPedido.getQuantidade());
			BigDecimal total = quantidade.multiply(unitario);

			itemBaixaPedido.setTotal(total);
			this.dataListItens.save(itemBaixaPedido);
		}
		
		this.rateiaValoresCabecalhoNosItens(cabecalhoBaixaPedido);

		System.out.println("FIM --- OperacaoBaixaPedido.calculaBaixasPedidos() --->" + (System.currentTimeMillis() - inicio) / 1000);
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void criaTitulosOperacoes() throws Exception {
		List<CabecalhoBaixaPedido> cabecalhosBaixaPedido = this.dataListCabecalhos.getList();

		for (CabecalhoBaixaPedido cabecalhoBaixaPedido : cabecalhosBaixaPedido) {
			this.regraTitulo.criaTitulos(cabecalhoBaixaPedido, 3);
		}
	}
	
	/**
	 * Cria os eventos relacionados ao pedido
	 * @throws Exception		
	 */
	private void criaEventosCabecalhoBaixaPedido() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.criaEventosCabecalhoBaixaPedido()");

		Date dataHoje = new Date();
		
		ArrayList<CabecalhoBaixaPedido> listOperacoes = (ArrayList<CabecalhoBaixaPedido>)this.dataListCabecalhos.getList();
		for (CabecalhoBaixaPedido cabecalhoOperacao : listOperacoes) {

			//Cria Evento de Frete
			Collection<AppEntity> eventosFrete = this.dataListEventos.getRangeAsCollection("operacaoId;classId", cabecalhoOperacao.getOperacaoId(), -99999899999206L /* EventoCabecalhoBaixaPedidoFrete */ );

			if (cabecalhoOperacao.getFrete() == null || cabecalhoOperacao.getFrete().compareTo( new BigDecimal(0)) == 0 ) {
				if (eventosFrete != null) {
					
					for (Iterator iterator = eventosFrete.iterator(); iterator.hasNext(); ) {
						EventoCabecalhoBaixaPedidoFrete eventoFrete = (EventoCabecalhoBaixaPedidoFrete) iterator.next();
						this.dataListEventos.delete(eventoFrete);
					}
				}
			} else {
				
				if (eventosFrete == null) {
					EventoCabecalhoBaixaPedidoFrete eventoFrete = new EventoCabecalhoBaixaPedidoFrete();
					
					eventoFrete.createId(this.getAppTransaction().getDatabase());
					eventoFrete.setOperacaoId(cabecalhoOperacao.getOperacaoId());
					eventoFrete.setData(dataHoje);
					eventoFrete.setHora(dataHoje);
					eventoFrete.setValor(cabecalhoOperacao.getFrete());

					this.dataListEventos.addAndSave(eventoFrete);
				} else {
					boolean freteCriado = false;

					for (Iterator iterator = eventosFrete.iterator(); iterator.hasNext(); ) {
						EventoCabecalhoBaixaPedidoFrete eventoFrete = (EventoCabecalhoBaixaPedidoFrete) iterator.next();

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
			Collection<AppEntity> eventosDesconto = this.dataListEventos.getRangeAsCollection("operacaoId;classId", cabecalhoOperacao.getOperacaoId(), -99999899999207L /* EventoCabecalhoBaixaPedidoDesconto */ );
			
			if (cabecalhoOperacao.getDesconto() == null || cabecalhoOperacao.getDesconto().compareTo( new BigDecimal(0)) == 0 ) {
				if (eventosDesconto != null) {

					for (Iterator iterator = eventosDesconto.iterator(); iterator.hasNext(); ) {
						EventoCabecalhoBaixaPedidoDesconto eventoDesconto = (EventoCabecalhoBaixaPedidoDesconto) iterator.next();
						this.dataListEventos.delete(eventoDesconto);
					}
				}
			} else {

				if (eventosDesconto == null) {
					EventoCabecalhoBaixaPedidoDesconto eventoDesconto = new EventoCabecalhoBaixaPedidoDesconto();

					eventoDesconto.createId(this.getAppTransaction().getDatabase());
					eventoDesconto.setOperacaoId(cabecalhoOperacao.getOperacaoId());
					eventoDesconto.setData(dataHoje);
					eventoDesconto.setHora(dataHoje);
					eventoDesconto.setValor(cabecalhoOperacao.getDesconto());

					this.dataListEventos.addAndSave(eventoDesconto);

				} else {
					boolean descontoCriado = false;

					for (Iterator iterator = eventosDesconto.iterator(); iterator.hasNext(); ) {
						EventoCabecalhoBaixaPedidoDesconto eventoDesconto = (EventoCabecalhoBaixaPedidoDesconto) iterator.next();

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
		System.out.println("FIM --- OperacaoPedido.criaEventosCabecalhoBaixaPedido() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000));
	}
	
	/**
	 * Método responsável por verificar se todas as propriedades necessárias para o cálculo dos pedidos,
	 * estão devidamente preenchidas.
	 *  
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void verificaIntegridadeBaixasPedido() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoBaixaPedido.verificaIntegridadePedidos()");

		ArrayList<ItemBaixaPedido> baixasPedido = (ArrayList<ItemBaixaPedido>)this.dataListItens.getList();
		ArrayList<String> erros = new ArrayList(); 
		
		for (ItemBaixaPedido itemBaixaPedido : baixasPedido) {
			if (itemBaixaPedido.getId() == null) {
				erros.add("A propriedade \"id\" nï¿½o estï¿½ preenchida.");
			}

			if (itemBaixaPedido.getOperacaoId() == null) {
				erros.add("A propriedade \"operacao\" do pedido: " + itemBaixaPedido.getId() + " nï¿½o estï¿½ preenchida.");
			}

			if (itemBaixaPedido.getEntidade() == null) {
				erros.add("A propriedade \"entidade\" do pedido: " + itemBaixaPedido.getId() + " nï¿½o estï¿½ preenchida.");
			}

			if (itemBaixaPedido.getEstabelecimento() == null) {
				erros.add("A propriedade \"estabelecimento\" do pedido: " + itemBaixaPedido.getId() + " nï¿½o estï¿½ preenchida.");
			}

			if (itemBaixaPedido.getEmissao() == null) {
				erros.add("A propriedade \"emissao\" do pedido: " + itemBaixaPedido.getId() + " nï¿½o estï¿½ preenchida.");
			}

			if (itemBaixaPedido.getRecurso() == null) {
				erros.add("A propriedade \"recurso\" do pedido: " + itemBaixaPedido.getId() + " nï¿½o estï¿½ preenchida.");
			}

			if (itemBaixaPedido.getNucleo() == null) {
				erros.add("A propriedade \"nucleo\" do pedido: " + itemBaixaPedido.getId() + " nï¿½o estï¿½ preenchida.");
			}
		}
		
		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- OperacaoBaixaPedido.verificaIntegridadePedidos() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000));
	}

	private void verificaIntegridadeTitulos() {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.verificaIntegridadeTitulos()");

		// TODO A IMPLEMENTAR

		System.out.println("FIM --- OperacaoPedido.verificaIntegridadeTitulos() ->" + ((System.currentTimeMillis() - inicio) /1000));
	}

	private void verificaIntegridadeEventos() throws Exception {
		List<CabecalhoBaixaPedido> cabecalhos = this.dataListCabecalhos.getList();
		
		for (CabecalhoBaixaPedido cabecalho : cabecalhos) {
		
			Long operacaoId = cabecalho.getOperacaoId();
			Collection<ItemBaixaPedido> baixasPedido = (Collection<ItemBaixaPedido>) this.getBaixasCabecalho(operacaoId);
			Collection<Evento> eventos = (Collection)this.dataListEventos.getRangeAsCollection("operacaoId", operacaoId);
			
			
			if (eventos != null) {
				for (Evento evento : eventos) {

					if (EventoCabecalhoBaixaPedidoFrete.class.isInstance(evento)) {
						EventoCabecalhoBaixaPedidoFrete eventoFrete = (EventoCabecalhoBaixaPedidoFrete)evento;
						BigDecimal valorFretePedidos = new BigDecimal(0).setScale(2);

						for (ItemBaixaPedido itemBaixaPedido : baixasPedido) {
							if (itemBaixaPedido.getFrete() != null) {
								valorFretePedidos = valorFretePedidos.add(itemBaixaPedido.getFrete());
							}
						}
						if (valorFretePedidos.compareTo(eventoFrete.getValor()) != 0) {
							throw new Exception("Valor do Evento de Frete está divergente do valor nos Pedidos.");
						}

					} else if (EventoCabecalhoBaixaPedidoDesconto.class.isInstance(evento)) {
						EventoCabecalhoBaixaPedidoDesconto eventoDesconto = (EventoCabecalhoBaixaPedidoDesconto)evento;
						BigDecimal valorDescontoPedidos = new BigDecimal(0).setScale(2);

						for (ItemBaixaPedido itemBaixaPedido : baixasPedido) {
							if (itemBaixaPedido.getDesconto() != null) {
								valorDescontoPedidos = valorDescontoPedidos.add(itemBaixaPedido.getDesconto());
							}
						}
						if (valorDescontoPedidos.compareTo(eventoDesconto.getValor()) != 0) {
							throw new Exception("Valor do Evento de Desconto está divergente do valor nos Pedidos.");
						}
					}
				}
			}
		}
	}

	/**
	 * Método responsável por verificar se todas as propriedades de preenchimento obrigatório
	 * dos cabecalhos das operações estlo devidamente preenchidas.
	 *  
	 * @throws Exception
	 */
	private void verificaIntegridadeCabecalhos() throws Exception {
		ArrayList<CabecalhoBaixaPedido> listCabecalhos = (ArrayList<CabecalhoBaixaPedido>)this.dataListCabecalhos.getList();
		
		for (CabecalhoBaixaPedido cabecalhoOperacao : listCabecalhos) {
			this.verificaIntegridadeCabecalho(cabecalhoOperacao);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void verificaIntegridadeCabecalho(CabecalhoBaixaPedido cabecalhoBaixaPedido) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoBaixaPedido.verificaIntegridadeOperacoes()");

		ArrayList<String> erros = new ArrayList(); 
		
		if (cabecalhoBaixaPedido.getOperacaoId() == null) {
			erros.add("A propriedade operacao não está preenchida.");
		}

		if (cabecalhoBaixaPedido.getEntidade() == null) {
			erros.add("A propriedade \"entidade\" da operação: " + cabecalhoBaixaPedido.getOperacaoId() + " nï¿½o estï¿½ preenchida.");
		}

		if (cabecalhoBaixaPedido.getEstabelecimento() == null) {
			erros.add("A propriedade \"estabelecimento\" da operação: " + cabecalhoBaixaPedido.getOperacaoId() + " nï¿½o estï¿½ preenchida.");
		}

		if (cabecalhoBaixaPedido.getEmissao() == null) {
			erros.add("A propriedade \"emissão\" da operação: " + cabecalhoBaixaPedido.getOperacaoId() + " nï¿½o estï¿½ preenchida.");
		}

		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- OperacaoBaixaPedido.verificaIntegridadeOperacoes()" + (System.currentTimeMillis() - inicio) / 1000);
	}

	private void sincronizaItensComCabecalho(CabecalhoBaixaPedido cabecalhoBaixaPedido) throws Exception{
		ArrayList<ItemBaixaPedido> baixasPedido = this.getBaixasPedidoCabecalho( cabecalhoBaixaPedido );

		Long inicio = System.currentTimeMillis();
		System.out.println("   INICIO --- replicaCamposDoCabecalhoNasBaixasDePedidoPelaClasseDaOperacao");
		
		ArrayList<String> camposCabecalho = cacheDeCamposPorClasse.get( cabecalhoBaixaPedido.getClass().getName() ); 
		if ( camposCabecalho == null ) {
			camposCabecalho = new ArrayList<String>();
			
			Field[] fields = cabecalhoBaixaPedido.getClass().getDeclaredFields();
			for (Field field : fields) {
				Cabecalho annotationCabecalho = field.getAnnotation(Cabecalho.class);
				
				if (annotationCabecalho != null && annotationCabecalho.value() == TipoCampoCabecalho.REPLICA) {
					camposCabecalho.add(field.getName());
				}
			}
			cacheDeCamposPorClasse.put(cabecalhoBaixaPedido.getClass().getName(), camposCabecalho);
		} 
		
		for (ItemBaixaPedido itemBaixaPedido : baixasPedido) {
			for (String nomeDoCampo : camposCabecalho) {
				
				Long inicioAux1 = System.currentTimeMillis();
				System.out.println("   INICIO --- ReflectUtil.setFieldValue(...)");

				ReflectUtil.setFieldValue(itemBaixaPedido, nomeDoCampo, ReflectUtil.getFieldValue(cabecalhoBaixaPedido, nomeDoCampo));
				
				System.out.println("   FIM --- ReflectUtil.setFieldValue(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux1) /1000) + "s");
			}
			
			Long inicioAux2 = System.currentTimeMillis();
			System.out.println("   INICIO --- dataListItens.save(...)");
			
			this.dataListItens.save(itemBaixaPedido);

			System.out.println("   FIM --- dataListItens.save(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux2) /1000) + "s");
		}

		System.out.println("   FIM --- replicaCamposDoCabecalhoNasBaixasDePedidoPelaClasseDaOperacao --- Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	/**
	 * 
	 * @param cabecalhoBaixaPedido
	 * @throws Exception 
	 */
	private void rateiaValoresCabecalhoNosItens(CabecalhoBaixaPedido cabecalhoBaixaPedido) throws Exception {
		BigDecimal valorFreteOperacao = cabecalhoBaixaPedido.getFrete();
		BigDecimal valorDescontoOperacao = cabecalhoBaixaPedido.getDesconto();
		BigDecimal valorResiduoFrete = new BigDecimal(0);
		BigDecimal valorResiduoDesconto = new BigDecimal(0);
		BigDecimal valorOperacao = new BigDecimal(0);	
		
		Collection<AppEntity> itensPedido = this.dataListItens.getRangeAsCollection("operacaoId", cabecalhoBaixaPedido.getOperacaoId());
		if (itensPedido != null) {
			valorFreteOperacao = valorFreteOperacao == null ? new BigDecimal(0) : valorFreteOperacao;
			valorDescontoOperacao = valorDescontoOperacao == null ? new BigDecimal(0) : valorDescontoOperacao;

			for (Iterator iterator = itensPedido.iterator(); iterator.hasNext(); ) {
				ItemBaixaPedido itemBaixaPedido = (ItemBaixaPedido)iterator.next();

				valorOperacao = valorOperacao.add( itemBaixaPedido.getTotal() );				
			}
			
			valorResiduoFrete = valorFreteOperacao;
			valorResiduoDesconto = valorDescontoOperacao;
			for (Iterator iterator = itensPedido.iterator(); iterator.hasNext(); ) {
				ItemBaixaPedido itemBaixaPedido = (ItemBaixaPedido)iterator.next();
				BigDecimal fator = itemBaixaPedido.getTotal().divide(valorOperacao, 9, BigDecimal.ROUND_HALF_UP);
				BigDecimal valorFreteRateado = valorFreteOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal valorDescontoRateado = valorDescontoOperacao.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);

				valorResiduoFrete = valorResiduoFrete.subtract(valorFreteRateado);
				valorResiduoDesconto = valorResiduoDesconto.subtract(valorDescontoRateado);
				
				if (!iterator.hasNext()) {
					valorFreteRateado = valorFreteRateado.add(valorResiduoFrete);
					valorDescontoRateado = valorDescontoRateado.add(valorResiduoDesconto);
				}
				
				itemBaixaPedido.setFrete(valorFreteRateado);
				itemBaixaPedido.setDesconto(valorDescontoRateado);
				
				this.dataListItens.save(itemBaixaPedido);
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
		
		this.regraTitulo.fecha();

		this.preparado = false;
	}

	public ArrayList<ItemBaixaPedido> getBaixasPedidoCabecalho( CabecalhoOperacaoAbstrato cabecalhoOperacao ) throws Exception{
		return this.getBaixasCabecalho(cabecalhoOperacao.getOperacaoId());
	}
	
	public ArrayList<ItemBaixaPedido> getBaixasCabecalho( Long operacaoId ) throws Exception{
		Collection<AppEntity> baixas = this.dataListItens.getRangeAsCollection("operacaoId", operacaoId );
		
		if ( baixas != null ){
			return new ArrayList(baixas);
		} else {
			return new ArrayList<ItemBaixaPedido>();
		}
	}

	public ArrayList<Titulo> getTitulosCabecalhoBaixaPedido( CabecalhoOperacaoAbstrato cabecalhoOperacao ) throws Exception{
		return this.getTitulosCabecalhoBaixaPedido(cabecalhoOperacao.getOperacaoId());
	}
	
	public ArrayList<Titulo> getTitulosCabecalhoBaixaPedido( Long operacaoId ) throws Exception{
		Collection<AppEntity> titulos = this.getRegraTituloBaixaPedido().dataListItens.getRangeAsCollection("operacaoId", operacaoId );
		
		if ( titulos != null ){
			return new ArrayList(titulos);
		} else {
			return new ArrayList<Titulo>();
		}
	}

}