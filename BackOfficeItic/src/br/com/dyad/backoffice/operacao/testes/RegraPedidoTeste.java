package br.com.dyad.backoffice.operacao.testes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraPedido;
import br.com.dyad.businessinfrastructure.entidades.entidade.Almoxarifado;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.annotations.Test;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.unit.TestCase;

public class RegraPedidoTeste extends TestCase {
	
	public void criaPedidos(RegraPedido regraPedido, int quantidadePedidos) throws Exception {
		DataList dlEntidade = DataListFactory.newDataList(getDataBase());
		dlEntidade.executeQuery("from " + Entidade.class.getName(), 0, 100);
		ArrayList<Pessoa> listPessoas = new ArrayList<Pessoa>(dlEntidade.getList());

		DataList dlEstabelecimento = DataListFactory.newDataList(getDataBase());
		dlEstabelecimento.executeQuery("from " + Estabelecimento.class.getName(), 0, 100);
		ArrayList<Pessoa> listEstabelecimentos = new ArrayList<Pessoa>(dlEstabelecimento.getList());

		DataList dlAlmoxarifado = DataListFactory.newDataList(getDataBase());
		dlAlmoxarifado.executeQuery("from " + Almoxarifado.class.getName(), 0, 100);
		ArrayList<Pessoa> listAlmoxarifados = new ArrayList<Pessoa>(dlAlmoxarifado.getList());
		
		DataList dlRecurso = DataListFactory.newDataList(getDataBase());
		dlRecurso.executeQuery("from " + Recurso.class.getName(), 0, 100);
		ArrayList<Pessoa> listRecursos = new ArrayList<Pessoa>(dlRecurso.getList());
		
		for (int i = 0; i < quantidadePedidos; i++) {
			int quantidadeItens = 0;
			BigDecimal decimalFrete = new BigDecimal(Math.random() * 100).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal decimalDesconto = new BigDecimal(Math.random() * 100).setScale(2, BigDecimal.ROUND_HALF_UP);
			Date dataEmissao = new Date();
			GregorianCalendar calendarAux = new GregorianCalendar();
			int diasEmissao = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 30;

			while (quantidadeItens == 0) {
				quantidadeItens = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 5;
			}

			calendarAux.setTime(dataEmissao);
			calendarAux.add(Calendar.DAY_OF_MONTH, diasEmissao);
			dataEmissao.setTime(calendarAux.getTime().getTime());
			
			int entidadePos = (int)(Math.round(Math.random() * 100) % dlEntidade.getList().size());
			int estabelecimentoPos = (int)(Math.round(Math.random() * 100) % dlEstabelecimento.getList().size());
			
			Entidade entidadePedido = (Entidade)dlEntidade.getList().get( entidadePos );
			Estabelecimento estabelecimentoPedido = (Estabelecimento)dlEstabelecimento.getList().get( estabelecimentoPos );

			CabecalhoPedido cabecalhoPedido = (CabecalhoPedido)regraPedido.novoCabecalho();
			cabecalhoPedido.setEmissao(dataEmissao);
			cabecalhoPedido.setEntidade(entidadePedido);
			cabecalhoPedido.setEstabelecimento(estabelecimentoPedido);
			cabecalhoPedido.setFrete(decimalFrete);
			cabecalhoPedido.setDesconto(decimalDesconto);

			for (Long j = 0L; j < quantidadeItens; j++) {
				int recursoPos = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % dlRecurso.getList().size();
				int nucleoPos = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % dlAlmoxarifado.getList().size();
				BigDecimal decimalQuantidade = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP);
				BigDecimal decimalUnitario = new BigDecimal(Math.random() * 100).setScale(2, BigDecimal.ROUND_HALF_UP);

				ItemPedido itemPedido = (ItemPedido)regraPedido.novoItem(cabecalhoPedido);

				itemPedido.setNucleo((Nucleo)(dlAlmoxarifado.getList()).get(nucleoPos));
				itemPedido.setRecurso((Recurso)(dlRecurso.getList()).get(recursoPos));
				itemPedido.setQuantidade(decimalQuantidade.longValue());
				itemPedido.setUnitario(decimalUnitario);
				//regraPedido.getDataListItens().save(itemPedido);
			}

		}
	}
	
	@Test
	public void teste_regraPedido_abre_operacaoId() throws Exception {

		AppTransaction appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(getDataBase()), getDataBase());
		int quantidadeOperacoes = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		
		RegraPedido regraPedidoGrava = new RegraPedido(appTransaction);
		
		this.criaPedidos(regraPedidoGrava, quantidadeOperacoes);
		ArrayList<Long> operacoesId = new ArrayList(regraPedidoGrava.getDataListCabecalhos().getDistinctValues("operacaoId"));
		
		regraPedidoGrava.grava();
		
		RegraPedido regraPedidoAbre = new RegraPedido(appTransaction);
		regraPedidoAbre.getConsultaPedido().setOperacoesIds(operacoesId);
		//regraPedidoAbre.abre();
		this.assertTrue(regraPedidoAbre.getDataListCabecalhos().getList().size() == operacoesId.size());
	}
}
