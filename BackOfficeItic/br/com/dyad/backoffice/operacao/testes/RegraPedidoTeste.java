package br.com.dyad.backoffice.operacao.testes;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.RegraPedido;
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

public class RegraPedidoTeste extends TestCase{

	@SuppressWarnings({ "static-access" })
	@Test
	public void testa_regraCriaOperacao_criacaoPedido() throws Exception{
		AppTransaction appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDataBase()), this.getDataBase());
		
		//int quantidadeOperacoes = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int quantidadeOperacoes = 60;

		DataList dlPessoa = DataListFactory.newDataList(this.getDataBase());
		dlPessoa.executeQuery("from " + Pessoa.class.getName(), 0, 100);

		DataList dlEstabelecimento = DataListFactory.newDataList(this.getDataBase());
		dlEstabelecimento.executeQuery("from " + Estabelecimento.class.getName(), 0, 100);

		DataList dlAlmoxarifado = DataListFactory.newDataList(this.getDataBase());
		dlAlmoxarifado.executeQuery("from " + Almoxarifado.class.getName(), 0, 100);

		DataList dlRecurso = DataListFactory.newDataList(this.getDataBase());
		dlRecurso.executeQuery("from " + Recurso.class.getName(), 0, 100);
		
		RegraPedido regraPedido = new RegraPedido(appTransaction);

		for (int i = 0; i < quantidadeOperacoes; i++) {
			int quantidadePedidos = 0;
			BigDecimal decimalFrete = new BigDecimal(Math.random() * 100).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal decimalDesconto = new BigDecimal(Math.random() * 100).setScale(2, BigDecimal.ROUND_HALF_UP);
			Date dataEmissao = new Date();
			GregorianCalendar calendarAux = new GregorianCalendar();
			int dias = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 30;

			while (quantidadePedidos == 0) {
				quantidadePedidos = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % 5;
			}

			calendarAux.setTime(dataEmissao);
			calendarAux.add(Calendar.DAY_OF_MONTH, dias);
			dataEmissao.setTime(calendarAux.getTime().getTime());
			
			Entidade entidadePedido = null;
			Estabelecimento estabelecimentoPedido = null;
			int entidadePos = (int)(Math.round(Math.random() * 100) % dlPessoa.getList().size());
			int estabelecimentoPos = (int)(Math.round(Math.random() * 100) % dlEstabelecimento.getList().size());
			
			entidadePedido = (Pessoa)dlPessoa.getList().get( entidadePos );
			estabelecimentoPedido = (Estabelecimento)dlEstabelecimento.getList().get( estabelecimentoPos );			
			
			CabecalhoPedido cabecalho = regraPedido.novoCabecalho();
			cabecalho.setEmissao(dataEmissao);
			cabecalho.setEntidade(entidadePedido);
			cabecalho.setEstabelecimento(estabelecimentoPedido);
			cabecalho.setFrete(decimalFrete);
			cabecalho.setDesconto(decimalDesconto);

			for (Long j = 0L; j < quantidadePedidos; j++) {
				int recursoPos = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % dlRecurso.getList().size();
				int nucleoPos = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() % dlAlmoxarifado.getList().size();
				BigDecimal decimalQuantidade = new BigDecimal(Math.random() * 100).setScale(0, BigDecimal.ROUND_HALF_UP);
				BigDecimal decimalUnitario = new BigDecimal(Math.random() * 100).setScale(2, BigDecimal.ROUND_HALF_UP);

				ItemPedido itemPedido = (ItemPedido)regraPedido.novoItem(cabecalho);

				itemPedido.setNucleo((Nucleo)(dlAlmoxarifado.getList()).get(nucleoPos));
				itemPedido.setRecurso((Recurso)(dlRecurso.getList()).get(recursoPos));
				itemPedido.setQuantidade(decimalQuantidade.longValue());
				itemPedido.setUnitario(decimalUnitario);
				regraPedido.getItens().save(itemPedido);
			}

		}
		regraPedido.grava();
	}
}
