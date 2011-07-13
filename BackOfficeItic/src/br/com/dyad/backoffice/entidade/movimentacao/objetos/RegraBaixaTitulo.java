package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.ItemOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.MovDispo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;
import br.com.dyad.backoffice.operacao.consulta.ConsultaBaixaTitulo;
import br.com.dyad.backoffice.operacao.consulta.ConsultaMovDispo;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class RegraBaixaTitulo extends RegraAbstrata {
	
	private ConsultaBaixaTitulo consultaBaixaTitulo;
	private ConsultaMovDispo consultaMovDispo;

	public RegraBaixaTitulo(AppTransaction appTransaction) {
		super(appTransaction, "BAIXA_TITULO");

		this.consultaBaixaTitulo = new ConsultaBaixaTitulo(this.getAppTransaction());
		this.consultaMovDispo = new ConsultaMovDispo(this.getAppTransaction());
	}

	/*
	 * Getter´s e Setter´s
	 */
	public ConsultaBaixaTitulo getConsultaBaixaTitulo() {
		return this.consultaBaixaTitulo;
	}
	
	public ConsultaMovDispo getConsultaMovDispo() {
		return this.consultaMovDispo;
	}
	
	/*
	 * Métodos proprios dessa implementação
	 */
	public MovDispo novoMovDispo(CabecalhoBaixaTitulo cabecalho) throws Exception {
		if ( cabecalho == null ){
			throw new Exception("Operação não pode ser nula!");
		}

		if ( !this.getDataListCabecalhos().find("id", cabecalho.getId()) ){
			throw new Exception("Esta operação não foi encontrada neste objeto!");
		}

		MovDispo movDispo = new MovDispo();
		movDispo.createId(this.getAppTransaction().getDatabase());
		movDispo.setCabecalho(cabecalho);
		movDispo.setClasseOperacaoId( this.regraId );
		
		cabecalho.addMovDispo(movDispo);

		return movDispo;
	}

	public void abre(Long operacaoId, Long entidadeId, Date emissaoInicial, Date emissaoFinal) throws Exception {
		if (!this.preparado) {
			this.preparaRegra();
		}
		
		String select = "from " + CabecalhoBaixaTitulo.class.getName() + " ";
		ArrayList<String> where = new ArrayList<String>();
		ArrayList<Object> params = new ArrayList<Object>();
		
		if (operacaoId != null) {
			where.add(" id = ? ");
			params.add(operacaoId);
		}
		
		if (entidadeId != null) {
			where.add(" id IN (SELECT cabecalho.id FROM " + ItemBaixaTitulo.class.getName() + " WHERE entidade.id = ? )");
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

		
		if (where.size() == 0) {
			throw new AppException("Para buscar baixas você deve informar pelo menos um destes filtros: " +
									"operação, entidade, emissão inicial ou emissão final.");
		}
		
		String query = select + " where " + StringUtils.join(where, " and ");

		DataList dataListCabecalhos = DataListFactory.newDataList(this.getAppTransaction());
		this.getAppTransaction().clear();
		List<?> lista = PersistenceUtil.executeHql((Session)this.getAppTransaction().getSession(), query, params);
		
		dataListCabecalhos.setList(lista);
		
		this.getDataListCabecalhos().add(dataListCabecalhos);
	}

	/**
	 * INICIO DE TRECHO ORGANIZADO 
	 */
	@Override
	public void grava() throws Exception {
		this.preparaGravacao();
		this.verificaIntegridadeItens();

		DataList.commit(this.getDataListCabecalhos());
		
//		contabiliza();
	}

	@Override
	public void preparaGravacao() throws Exception {
		this.calculaOperacoes();
		
		this.sincronizaMovDisposComCabecalhos();
		this.verificaIntegridadeMovDispos();
	}

	@Override
	public void calculaOperacoes() throws Exception {
		List<CabecalhoBaixaTitulo> cabecalhosBaixaTitulo = this.getDataListCabecalhos().getList();

		for (CabecalhoBaixaTitulo cabecalhoBaixaTitulo : cabecalhosBaixaTitulo) {
			this.calculaOperacao(cabecalhoBaixaTitulo);
		}
	}

	/**
	 * @throws Exception 
	 * 
	 */
	@Override
	public void calculaOperacao(Cabecalho cabecalho) throws Exception {
		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = (CabecalhoBaixaTitulo)cabecalho;
		
		this.verificaIntegridadeCabecalho(cabecalhoBaixaTitulo);
		this.sincronizaItensComCabecalho(cabecalhoBaixaTitulo);

		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraTitulo.calculaOperacao()");
		
		System.out.println("FIM --- RegraTitulo.calculaOperacao() --->" + (System.currentTimeMillis() - inicio) / 1000);
	}
	
	/**
	 * Método responsável por verificar se todas as propriedades de preenchimento obrigatório
	 * dos cabecalhos das operações estlo devidamente preenchidas.
	 *  
	 * @throws Exception
	 */
	@Override
	protected void verificaIntegridadeCabecalhos() throws Exception {
		ArrayList<CabecalhoBaixaTitulo> listCabecalhos = (ArrayList<CabecalhoBaixaTitulo>)this.getDataListCabecalhos().getList();
		
		for (CabecalhoBaixaTitulo cabecalhoOperacao : listCabecalhos) {
			this.verificaIntegridadeCabecalho(cabecalhoOperacao);
		}
	}
	
	@Override
	protected void verificaIntegridadeCabecalho(Cabecalho cabecalho) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraBaixaTitulo.verificaIntegridadeOperacoes()");
		
		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = (CabecalhoBaixaTitulo)cabecalho;
		ArrayList<String> erros = new ArrayList(); 
		
		if (cabecalhoBaixaTitulo.getId() == null) {
			erros.add("A propriedade operacao não está preenchida.");
		}

		if (cabecalhoBaixaTitulo.getEmissao() == null) {
			erros.add("A propriedade \"emissão\" da operação: " + cabecalhoBaixaTitulo.getId() + " não está preenchida.");
		}

		if (!erros.isEmpty()) {
			throw new AppException(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- RegraBaixaTitulo.verificaIntegridadeOperacoes()" + (System.currentTimeMillis() - inicio) / 1000);
	}

	@Override
	protected void sincronizaItensComCabecalho(Cabecalho cabecalho) throws Exception{
		Long inicio = System.currentTimeMillis();
		System.out.println("   INICIO --- RegraBaixaTitulo.sincronizaItensComCabecalho()");

		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = (CabecalhoBaixaTitulo)cabecalho;
		Collection<ItemBaixaTitulo> itensBaixaTitulo = cabecalhoBaixaTitulo.getItensBaixaTitulo();
		ArrayList<String> camposCabecalho = new ArrayList<String>(); 

		camposCabecalho.add("emissao"); 
				
		for (ItemBaixaTitulo itemBaixaTitulo : itensBaixaTitulo) {
			for (String nomeDoCampo : camposCabecalho) {
				
				Long inicioAux1 = System.currentTimeMillis();
				System.out.println("   INICIO --- ReflectUtil.setFieldValue(...)");

				ReflectUtil.setFieldValue(itemBaixaTitulo, nomeDoCampo, ReflectUtil.getFieldValue(cabecalho, nomeDoCampo));
				
				System.out.println("   FIM --- ReflectUtil.setFieldValue(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux1) /1000) + "s");
			}
			
			Long inicioAux2 = System.currentTimeMillis();
		}

		System.out.println("   FIM --- RegraBaixaTitulo.sincronizaItensComCabecalho() --- Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	@Override
	protected void verificaIntegridadeItens() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.verificaIntegridadePedidos()");

		ArrayList<CabecalhoBaixaTitulo> cabecalhosBaixaTitulo = (ArrayList<CabecalhoBaixaTitulo>)this.getDataListCabecalhos().getList();
		
		for (CabecalhoBaixaTitulo cabecalhoBaixaTitulo : cabecalhosBaixaTitulo) {
			this.verificaIntegridadeItensCabecalho(cabecalhoBaixaTitulo);
		}
		
		System.out.println("FIM --- OperacaoPedido.verificaIntegridadePedidos() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	@Override
	protected void verificaIntegridadeItensCabecalho(Cabecalho cabecalho) throws Exception {
		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = (CabecalhoBaixaTitulo)cabecalho;
		ArrayList<String> erros = new ArrayList<String>(); 
		
		Collection<ItemBaixaTitulo> itensBaixaTitulo = cabecalhoBaixaTitulo.getItensBaixaTitulo();
		
		for (ItemBaixaTitulo itemBaixaTitulo : itensBaixaTitulo) {
			if (itemBaixaTitulo.getId() == null) {
				erros.add("A propriedade \"id\" não está preenchida.");
			}

			if (itemBaixaTitulo.getCabecalho() == null) {
				erros.add("A propriedade \"cabecalhoBaixaTitulo\" da baixa de título " + itemBaixaTitulo.getCabecalho() + " não está preenchida.");
			}

			if (itemBaixaTitulo.getCabecalho() == null) {
				erros.add("A propriedade \"emissao\" da baixa de título " + itemBaixaTitulo.getCabecalho() + " não está preenchida.");
			}
		}
		
		if (!erros.isEmpty()) {
			throw new AppException(StringUtils.join(erros, "\r\n"));			
		}
	}

	protected void sincronizaMovDisposComCabecalhos() throws Exception {
		ArrayList<CabecalhoBaixaTitulo> cabecalhosBaixaTitulo = (ArrayList<CabecalhoBaixaTitulo>)this.getDataListCabecalhos().getList();
		
		for (CabecalhoBaixaTitulo cabecalhoBaixaTitulo : cabecalhosBaixaTitulo) {
			this.sincronizaMovDisposComCabecalho(cabecalhoBaixaTitulo);
		}
	}

	protected void sincronizaMovDisposComCabecalho(CabecalhoBaixaTitulo cabecalhoBaixaTitulo) throws Exception {
		List<MovDispo> movDispos = (List<MovDispo>)cabecalhoBaixaTitulo.getMovDispos();
		Date emissao = cabecalhoBaixaTitulo.getEmissao();
		
		for (MovDispo movDispo : movDispos) {
			movDispo.setEmissao(emissao);
		}
	}

	protected void verificaIntegridadeMovDispos() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedido.verificaIntegridadeMovDispos()");

		ArrayList<CabecalhoBaixaTitulo> cabecalhosBaixaTitulo = (ArrayList<CabecalhoBaixaTitulo>)this.getDataListCabecalhos().getList();
		
		for (CabecalhoBaixaTitulo cabecalhoBaixaTitulo : cabecalhosBaixaTitulo) {
			this.verificaIntegridadeMovDisposCabecalho(cabecalhoBaixaTitulo);
		}

		System.out.println("FIM --- OperacaoPedido.verificaIntegridadeMovDisposCabecalho() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	protected void verificaIntegridadeMovDisposCabecalho(CabecalhoOperacaoAbstrato cabecalhoOperacao) throws Exception {
		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = (CabecalhoBaixaTitulo)cabecalhoOperacao;
		ArrayList<String> erros = new ArrayList<String>(); 
		
		Collection<ItemBaixaTitulo> itensBaixaTitulo = cabecalhoBaixaTitulo.getItensBaixaTitulo();
		Collection<MovDispo> movDispos = cabecalhoBaixaTitulo.getMovDispos();
		BigDecimal valorBaixa = new BigDecimal(0);
		BigDecimal valorMovDispo = new BigDecimal(0);
		
		for (ItemBaixaTitulo itemBaixaTitulo : itensBaixaTitulo) {
			valorBaixa = valorBaixa.add(itemBaixaTitulo.getTotal());			
		}
		
		for (MovDispo movDispo : movDispos) {
			if (movDispo.getId() == null) {
				erros.add("A propriedade \"id\" não está preenchida.");
			}

			if (movDispo.getCabecalho() == null) {
				erros.add("A propriedade \"cabecalhoBaixaTitulo\" da baixa de título " + movDispo.getCabecalho() + " não está preenchida.");
			}

			if (movDispo.getEntidade() == null) {
				erros.add("A propriedade \"entidade\" da baixa de título " + movDispo.getCabecalho() + " não está preenchida.");
			}

			if (movDispo.getEmissao() == null) {
				erros.add("A propriedade \"emissao\" da baixa de título " + movDispo.getCabecalho() + " não está preenchida.");
			}

			valorMovDispo = valorMovDispo.add(movDispo.getTotal());
		}
		
		if (valorBaixa.compareTo(valorMovDispo) != 0) {
			erros.add("Os valores das baixas e das movdispo´s devem ter os valores iguais.");
		}
		
		if (!erros.isEmpty()) {
			throw new AppException(StringUtils.join(erros, "\r\n"));			
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
	public CabecalhoBaixaTitulo novoCabecalho() throws Exception {
		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = new CabecalhoBaixaTitulo();
		cabecalhoBaixaTitulo.createId(this.getAppTransaction().getDatabase());
		cabecalhoBaixaTitulo.setClasseOperacaoId(this.getRegraId());
		
		this.getDataListCabecalhos().add(cabecalhoBaixaTitulo);
		return cabecalhoBaixaTitulo;
	}

	@Override
	public void excluiCabecalho(Cabecalho cabecalho) throws Exception {
		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = (CabecalhoBaixaTitulo)cabecalho;
		
		Collection<ItemBaixaTitulo> itensBaixaTitulo = cabecalhoBaixaTitulo.getItensBaixaTitulo();
		for (ItemBaixaTitulo itemBaixaTitulo : itensBaixaTitulo ) {
			this.excluiItem(itemBaixaTitulo);
		}

		this.getDataListCabecalhos().delete(cabecalhoBaixaTitulo);
	}

	@Override
	public ItemOperacaoAbstrato novoItem(Cabecalho cabecalho) throws Exception {
		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = (CabecalhoBaixaTitulo)cabecalho;
		
		if ( cabecalhoBaixaTitulo == null ){
			throw new Exception("Operacao não pode ser nula!");
		}

		if ( !this.getDataListCabecalhos().find("id", cabecalhoBaixaTitulo.getId()) ){
			throw new Exception("Esta operação não foi encontrada neste objeto!");
		}

		ItemBaixaTitulo itemBaixaTitulo = new ItemBaixaTitulo();
		itemBaixaTitulo.createId(this.getAppTransaction().getDatabase());
		itemBaixaTitulo.setCabecalho(cabecalhoBaixaTitulo);
		itemBaixaTitulo.setClasseOperacaoId( this.regraId );
		
		cabecalhoBaixaTitulo.addItemBaixaTitulo(itemBaixaTitulo);
		
		return itemBaixaTitulo;
	}

	@Override
	public void excluiItem(Item item) throws Exception {
		ItemBaixaTitulo itemBaixaTitulo = (ItemBaixaTitulo) item;
		itemBaixaTitulo.getCabecalho().removeItemBaixaTitulo(itemBaixaTitulo);
		
		this.getAppTransaction().delete(itemBaixaTitulo);
	}

	public void excluiMovDispo(MovDispo movDispo) throws Exception {
		movDispo.getCabecalho().removeMovDispo(movDispo);
		
		this.getAppTransaction().delete(movDispo);
	}
	
	/*public void contabiliza() throws OperacaoNaoContabilizadaException {
		ArrayList<CabecalhoBaixaTitulo> cabecalhos = (ArrayList<CabecalhoBaixaTitulo>)this.getDataListCabecalhos().getList();

		try {
			//Contabilizando
			RegraBuscaSugestao regraBuscaSugestao = new RegraBuscaSugestao(this.getAppTransaction());
			RegraLancamentoContabilBaixaTitulo regraContabilFiscalBaixaTitulo;
			RegraLancamento regraLancamento = new RegraLancamento(getAppTransaction());

			CabecalhoLancamento cabecalhoLancamento;

			Session session = PersistenceUtil.getSession(getAppTransaction().getDatabase());
			
			for (CabecalhoBaixaTitulo cabecalho : cabecalhos) {
				regraLancamento.removeLancamentoDaOperacao(cabecalho);
			}
			
			for (CabecalhoBaixaTitulo cabecalho : cabecalhos) {
				
				cabecalhoLancamento = (CabecalhoLancamento)regraLancamento.novoCabecalho();
				cabecalhoLancamento.setEmissao(cabecalho.getEmissao());

				cabecalhoLancamento.setCabecalhoOperacao(cabecalho);

				for (MovDispo movDispo : cabecalho.getMovDispos()) {
					Entidade entidade = movDispo.getEntidade();
					Date emissao = movDispo.getEmissao();

					ClasseContabilFiscalEntidade classeContabilFiscalEntidade = entidade.getClasseContabilEntidadeData(emissao);
					if (classeContabilFiscalEntidade == null) {
						throw new AppException("A entidade \"" + entidade.getCodigo() + "\" não possui vínculo com classe contábil.");
					}
					
					regraContabilFiscalBaixaTitulo = regraBuscaSugestao.findRegraContabilBaixaTitulo(classeContabilFiscalEntidade, classeContabilFiscalRecurso, emissao);

					List<VinculoRegraGrupoContabilBaixaTitulo> vinculosRegraGrupoContabilBaixaTitulo = regraContabilFiscalBaixaTitulo.getVinculosRegraGrupoContabilVigentes(emissao);

					if (vinculosRegraGrupoContabilBaixaTitulo.isEmpty()) {
						throw new AppException("A regra \""+regraContabilFiscalBaixaTitulo.getNome()+"\" não possui grupo(s) vigente(s) para esta operação.");
					} else {
						for (VinculoRegraGrupoContabilBaixaTitulo vinculoRegraGrupoContabilBaixaTitulo : vinculosRegraGrupoContabilBaixaTitulo) {

							GrupoLancamentoContabilBaixaTitulo grupo = vinculoRegraGrupoContabilBaixaTitulo.getGrupoLancamentoContabilBaixaTitulo();

							List<DefinicaoLancamentoContabilBaixaTitulo> definicoes = grupo.getDefinicoesVigentes(emissao);

							if (definicoes.isEmpty()) {
								throw new AppException("O grupo \""+grupo.getNome()+"\" não possui definições vigente(s) para esta operação.");
							} else {
								ItemLancamento itemLancamento;

								for (DefinicaoLancamentoContabilBaixaTitulo definicao : definicoes) {

									itemLancamento = (ItemLancamento)regraLancamento.novoItem(cabecalhoLancamento);

									FormulaContaBaixaTitulo fConta = (FormulaContaBaixaTitulo)FormulaUtil.getFormula(definicao.getCodigoFormulaConta(), FormulaContaBaixaTitulo.class);
									itemLancamento.setContaContabil(fConta.calcular(cabecalhoLancamento, session, definicao));

									FormulaDataBaixaTitulo fData = (FormulaDataBaixaTitulo)FormulaUtil.getFormula(definicao.getCodigoFormulaData(), FormulaDataBaixaTitulo.class);
									itemLancamento.setEmissao(fData.calcular(cabecalhoLancamento, movDispo, definicao));

									FormulaEntidadeBaixaTitulo fEntidade = (FormulaEntidadeBaixaTitulo)FormulaUtil.getFormula(definicao.getCodigoFormulaEntidade(), FormulaEntidadeBaixaTitulo.class);
									if (fEntidade != null) {
										itemLancamento.setEntidade(fEntidade.calcular(cabecalhoLancamento, movDispo, definicao));
									}

									FormulaHistoricoBaixaTitulo fHistorico = (FormulaHistoricoBaixaTitulo)FormulaUtil.getFormula(definicao.getCodigoFormulaHistorico(), FormulaHistoricoBaixaTitulo.class);
									itemLancamento.setObservacao(fHistorico.calcular(cabecalhoLancamento, movDispo, definicao));
									
									itemLancamento.setTipoLancamento(definicao.getTipoLancamento());

									FormulaValorBaixaTitulo fValor = (FormulaValorBaixaTitulo)FormulaUtil.getFormula(definicao.getCodigoFormulaValor(), FormulaValorBaixaTitulo.class);
									itemLancamento.setValor(fValor.calcular(cabecalhoLancamento, movDispo, definicao));
								}
							}
						}
					}
				}

				if (cabecalhoLancamento.getItensLancamento().size() < 2) {
					regraLancamento.getDataListCabecalhos().delete(cabecalhoLancamento);
				} else {
					regraLancamento.getDataListCabecalhos().save(cabecalhoLancamento);
				}
			}

			regraLancamento.grava();
		} catch (Exception e) {
			throw new OperacaoNaoContabilizadaException("NENHUMA OPERAÇÃO FOI CONTABILIZADA! "+e.getMessage());
		}
	}*/
}