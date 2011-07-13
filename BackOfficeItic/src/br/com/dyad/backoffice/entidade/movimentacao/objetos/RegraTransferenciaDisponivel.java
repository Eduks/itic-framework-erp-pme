package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.cadastro.DefinicaoLancamentoContabilTransfDispo;
import br.com.dyad.backoffice.entidade.cadastro.GrupoLancamentoContabilTransfDispo;
import br.com.dyad.backoffice.entidade.cadastro.RegraLancamentoContabilTransfDispo;
import br.com.dyad.backoffice.entidade.cadastro.VinculoRegraGrupoContabilTransfDispo;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;
import br.com.dyad.backoffice.exception.OperacaoNaoContabilizadaException;
import br.com.dyad.backoffice.formula.FormulaContaTransfDispo;
import br.com.dyad.backoffice.formula.FormulaDataTransfDispo;
import br.com.dyad.backoffice.formula.FormulaEntidadeTransfDispo;
import br.com.dyad.backoffice.formula.FormulaHistoricoTransfDispo;
import br.com.dyad.backoffice.formula.FormulaUtil;
import br.com.dyad.backoffice.formula.FormulaValorTransfDispo;
import br.com.dyad.backoffice.operacao.consulta.ConsultaOperacaoTransferenciaDisponivel;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.tabela.ClasseContabilFiscalEntidade;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class RegraTransferenciaDisponivel extends RegraAbstrata {
	
	private ConsultaOperacaoTransferenciaDisponivel consultaOperacaoTransferenciaDisponivel;
	
	public RegraTransferenciaDisponivel(AppTransaction appTransaction) {
		super(appTransaction, "TRANSFERENCIA_DISPONIVEL");
		
		consultaOperacaoTransferenciaDisponivel = new ConsultaOperacaoTransferenciaDisponivel(appTransaction);
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
		CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = new CabecalhoTransferenciaDisponivel();
		cabecalhoTransferenciaDisponivel.createId(getAppTransaction().getDatabase());
		cabecalhoTransferenciaDisponivel.setClasseOperacaoId(this.getRegraId());

		this.getDataListCabecalhos().add(cabecalhoTransferenciaDisponivel);

		return cabecalhoTransferenciaDisponivel;
	}

	@Override
	public void excluiCabecalho( Cabecalho cabecalho ) throws Exception {
		CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)cabecalho;

		ArrayList<ItemTransferenciaDisponivel> itensTransfenciaDisponivel = (ArrayList<ItemTransferenciaDisponivel>) cabecalhoTransferenciaDisponivel.getItensTransferenciaDisponivel();
		for (ItemTransferenciaDisponivel itemTransferenciaDisponivel : itensTransfenciaDisponivel ) {
			this.excluiItem(itemTransferenciaDisponivel);
		}

		this.getDataListCabecalhos().delete((CabecalhoTransferenciaDisponivel)cabecalho);
	}

	@Override
	public Item novoItem(Cabecalho cabecalho) throws Exception {

		CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)cabecalho;

		if ( cabecalhoTransferenciaDisponivel == null ) {
			throw new Exception("Operacao não pode ser nula!");
		}

		if ( !this.getDataListCabecalhos().findId(cabecalhoTransferenciaDisponivel) ) {
			throw new Exception("Esta operação não foi encontrada neste objeto!");
		}

		ItemTransferenciaDisponivel item = new ItemTransferenciaDisponivel();
		item.createId(this.getAppTransaction().getDatabase());
		item.setCabecalho(cabecalhoTransferenciaDisponivel);
		item.setClasseOperacaoId(this.getRegraId());

		cabecalhoTransferenciaDisponivel.addItemTransferenciaDisponivel(item);

		return item;
	}

	@Override
	public void excluiItem( Item item ) throws Exception {
		ItemTransferenciaDisponivel itemTransferenciaDisponivel = (ItemTransferenciaDisponivel) item;
		itemTransferenciaDisponivel.getCabecalho().removeItemTransferenciaDisponivel(itemTransferenciaDisponivel);

		this.getAppTransaction().delete(itemTransferenciaDisponivel);
	}

	public void abre(Long idOperacao) throws Exception {
		if (!this.preparado) {
			this.preparaRegra();
		}

		this.getAppTransaction().clear();
		DataList dataListCabecalhos = consultaOperacaoTransferenciaDisponivel.pegaTransferenciasDisponiveis();

		this.getDataListCabecalhos().add(dataListCabecalhos);
	}

	/**
	 * INICIO DO TRECHO ORGANIZADO
	 */
	@Override
	public void grava() throws Exception {
		preparaGravacao();

		DataList.commit(this.getDataListCabecalhos());

//		contabiliza();
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
		List<CabecalhoTransferenciaDisponivel> cabecalhosTransferenciaDisponivel = this.getDataListCabecalhos().getList();

		for (CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel : cabecalhosTransferenciaDisponivel) {
			this.calculaOperacao(cabecalhoTransferenciaDisponivel);
		}
	}

	@Override
	public void calculaOperacao(Cabecalho cabecalho) throws Exception {
		CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)cabecalho;

		this.verificaIntegridadeCabecalho(cabecalhoTransferenciaDisponivel);
		this.sincronizaItensComCabecalho(cabecalhoTransferenciaDisponivel);
	}

	@Override
	protected void verificaIntegridadeCabecalhos() throws Exception {
		ArrayList<CabecalhoTransferenciaDisponivel> cabecalhos = (ArrayList<CabecalhoTransferenciaDisponivel>)this.getDataListCabecalhos().getList();

		for (CabecalhoTransferenciaDisponivel cabecalhoOperacao : cabecalhos) {
			this.verificaIntegridadeCabecalho(cabecalhoOperacao);
		}
	}

	@Override
	protected void verificaIntegridadeCabecalho(Cabecalho cabecalho) throws Exception {
		ArrayList<String> erros = new ArrayList<String>();
		CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)cabecalho;

		if (cabecalhoTransferenciaDisponivel.getId() == null) {
			erros.add("A propriedade \"Operação\" não está preenchida.");
		}
		if (cabecalhoTransferenciaDisponivel.getEmissao() == null) {
			erros.add("A propriedade \"emissão\" da operação: " + cabecalhoTransferenciaDisponivel.getId() + " não está preenchida.");
		}
		if (!erros.isEmpty()) {
			throw new AppException(StringUtils.join(erros, "\r\n"));			
		}
	}

	@Override
	protected void sincronizaItensComCabecalho(Cabecalho cabecalho) throws Exception{
		CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)cabecalho;

		Collection<ItemTransferenciaDisponivel> transferencias = cabecalhoTransferenciaDisponivel.getItensTransferenciaDisponivel();
		ArrayList<String> camposCabecalho = new ArrayList<String>(); 

		camposCabecalho.add("emissao");

		for (ItemTransferenciaDisponivel itemTransferenciaDisponivel : transferencias) {
			for (String nomeDoCampo : camposCabecalho) {
				ReflectUtil.setFieldValue(itemTransferenciaDisponivel, nomeDoCampo, ReflectUtil.getFieldValue(cabecalhoTransferenciaDisponivel, nomeDoCampo));
			}
		}
	}

	@Override
	protected void verificaIntegridadeItens() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoPedidoBaixaAutomatica.verificaIntegridadeItens()");

		ArrayList<CabecalhoTransferenciaDisponivel> cabecalhosTransferenciaDisponivel = (ArrayList<CabecalhoTransferenciaDisponivel>)this.getDataListCabecalhos().getList();

		for (CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel : cabecalhosTransferenciaDisponivel) {
			this.verificaIntegridadeItensCabecalho(cabecalhoTransferenciaDisponivel);
		}

		System.out.println("FIM --- OperacaoPedidoBaixaAutomatica.verificaIntegridadeItens() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	@Override
	protected void verificaIntegridadeItensCabecalho(Cabecalho cabecalho) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraTransferenciaDisponivel.verificaIntegridadeItensCabecalho()");

		CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)cabecalho;

		Collection<ItemTransferenciaDisponivel> listTransferenciasDisponiveis = cabecalhoTransferenciaDisponivel.getItensTransferenciaDisponivel();
		ArrayList<String> erros = new ArrayList<String>(); 

		BigDecimal somaTotal = new BigDecimal(0);
		for (ItemTransferenciaDisponivel itemTransferenciaDisponivel : listTransferenciasDisponiveis) {
			somaTotal = somaTotal.add( itemTransferenciaDisponivel.getTotal() );
		}

		if (somaTotal.compareTo(new BigDecimal(0)) != 0) {
			erros.add("A soma do valor dos itens da operação, deve ser igual a 0");
		}

		if (!erros.isEmpty()) {
			throw new AppException(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- RegraTransferenciaDisponivel.verificaIntegridadeItensCabecalho() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
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

	/*public void contabiliza() throws OperacaoNaoContabilizadaException {
		ArrayList<CabecalhoTransferenciaDisponivel> cabecalhos = (ArrayList<CabecalhoTransferenciaDisponivel>)this.getDataListCabecalhos().getList();

		try {
			//Contabilizando
			RegraBuscaSugestao regraBuscaSugestao = new RegraBuscaSugestao(this.getAppTransaction());
			RegraLancamentoContabilTransfDispo regraContabilFiscalTransfDispo;
			RegraLancamento regraLancamento = new RegraLancamento(getAppTransaction());

			CabecalhoLancamento cabecalhoLancamento;

			Session session = PersistenceUtil.getSession(getAppTransaction().getDatabase());
			
			for (CabecalhoTransferenciaDisponivel cabecalho : cabecalhos) {
				regraLancamento.removeLancamentoDaOperacao(cabecalho);
			}
			
			for (CabecalhoTransferenciaDisponivel cabecalho : cabecalhos) {
				
				cabecalhoLancamento = (CabecalhoLancamento)regraLancamento.novoCabecalho();
				cabecalhoLancamento.setEmissao(cabecalho.getEmissao());

				cabecalhoLancamento.setCabecalhoOperacao(cabecalho);

				for (ItemTransferenciaDisponivel item : cabecalho.getItensTransferenciaDisponivel()) {
					Entidade entidade = item.getEntidade();
					Date emissao = item.getEmissao();

					ClasseContabilFiscalEntidade classeContabilFiscalEntidade = entidade.getClasseContabilEntidadeData(emissao);
					if (classeContabilFiscalEntidade == null) {
						throw new AppException("A entidade \"" + entidade.getCodigo() + "\" não possui vínculo com classe contábil.");
					}

					regraContabilFiscalTransfDispo = regraBuscaSugestao.findRegraContabilTransfDispo(classeContabilFiscalEntidade, classeContabilFiscalRecurso, emissao);

					List<VinculoRegraGrupoContabilTransfDispo> vinculosRegraGrupoContabilTransfDispo = regraContabilFiscalTransfDispo.getVinculosRegraGrupoContabilVigentes(emissao);

					if (vinculosRegraGrupoContabilTransfDispo.isEmpty()) {
						throw new AppException("A regra \""+regraContabilFiscalTransfDispo.getNome()+"\" não possui grupo(s) vigente(s) para esta operação.");
					} else {
						for (VinculoRegraGrupoContabilTransfDispo vinculoRegraGrupoContabilTransfDispo : vinculosRegraGrupoContabilTransfDispo) {

							GrupoLancamentoContabilTransfDispo grupo = vinculoRegraGrupoContabilTransfDispo.getGrupoLancamentoContabilTransfDispo();

							List<DefinicaoLancamentoContabilTransfDispo> definicoes = grupo.getDefinicoesVigentes(emissao);

							if (definicoes.isEmpty()) {
								throw new AppException("O grupo \""+grupo.getNome()+"\" não possui definições vigente(s) para esta operação.");
							} else {
								ItemLancamento itemLancamento;

								for (DefinicaoLancamentoContabilTransfDispo definicao : definicoes) {

									itemLancamento = (ItemLancamento)regraLancamento.novoItem(cabecalhoLancamento);

									FormulaContaTransfDispo fConta = (FormulaContaTransfDispo)FormulaUtil.getFormula(definicao.getCodigoFormulaConta(), FormulaContaTransfDispo.class);
									itemLancamento.setContaContabil(fConta.calcular(cabecalhoLancamento, session, definicao));

									FormulaDataTransfDispo fData = (FormulaDataTransfDispo)FormulaUtil.getFormula(definicao.getCodigoFormulaData(), FormulaDataTransfDispo.class);
									itemLancamento.setEmissao(fData.calcular(cabecalhoLancamento, item, definicao));

									FormulaEntidadeTransfDispo fEntidade = (FormulaEntidadeTransfDispo)FormulaUtil.getFormula(definicao.getCodigoFormulaEntidade(), FormulaEntidadeTransfDispo.class);
									if (fEntidade != null) {
										itemLancamento.setEntidade(fEntidade.calcular(cabecalhoLancamento, item, definicao));
									}

									FormulaHistoricoTransfDispo fHistorico = (FormulaHistoricoTransfDispo)FormulaUtil.getFormula(definicao.getCodigoFormulaHistorico(), FormulaHistoricoTransfDispo.class);
									itemLancamento.setObservacao(fHistorico.calcular(cabecalhoLancamento, item, definicao));
									
									itemLancamento.setTipoLancamento(definicao.getTipoLancamento());

									FormulaValorTransfDispo fValor = (FormulaValorTransfDispo)FormulaUtil.getFormula(definicao.getCodigoFormulaValor(), FormulaValorTransfDispo.class);
									itemLancamento.setValor(fValor.calcular(cabecalhoLancamento, item, definicao));
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
	
	public ConsultaOperacaoTransferenciaDisponivel getConsultaOperacaoTransferenciaDisponivel() {
		return consultaOperacaoTransferenciaDisponivel;
	}

	public void setConsultaOperacaoTransferenciaDisponivel(
			ConsultaOperacaoTransferenciaDisponivel consultaOperacaoTransferenciaDisponivel) {
		this.consultaOperacaoTransferenciaDisponivel = consultaOperacaoTransferenciaDisponivel;
	}
}
