package br.com.dyad.backoffice.entidade.movimentacao.objetos_interface;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.ItemOperacao;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.operacao.consulta.ConsultaTitulo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;

public class RegraTitulo extends RegraAbstrata {
	
	private DataList dataListCabecalhosBaixaPedido;
	private DataList dataListItensBaixaPedido;
	
	private ConsultaTitulo consultaTitulo;

	public RegraTitulo(AppTransaction appTransaction, DataList cabecalhosBaixaPedido, DataList itensBaixaPedido) {
		super(appTransaction);
		
		this.regraId = "TITULO";

		this.dataListCabecalhosBaixaPedido = cabecalhosBaixaPedido;
		this.dataListItensBaixaPedido = itensBaixaPedido;
		
		this.consultaTitulo = new ConsultaTitulo(appTransaction);
	}

	public DataList getDataListItensBaixaPedido() {
		return this.dataListItensBaixaPedido;
	}

	public ConsultaTitulo getConsultaTitulo() {
		return consultaTitulo;
	}

	public void criaTitulos(CabecalhoBaixaPedido cabecalhoBaixaPedido, int quantidadeParcelas) throws Exception {
		this.getItens().empty();

		Collection<AppEntity> itensBaixaPedido = this.dataListItensBaixaPedido.getRangeAsCollection("operacaoId", cabecalhoBaixaPedido.getOperacaoId());

		Entidade entidadeTitulo = null;
		Date emissaoTitulo = null;

		BigDecimal valorOperacao = new BigDecimal(0);
		BigDecimal valorResiduo = new BigDecimal(0);
		BigDecimal valorPrincipalTitulo = new BigDecimal(0);
		BigDecimal valorTotalTitulo = new BigDecimal(0);

		Calendar calendarVencimentoTitulo = new GregorianCalendar();

		entidadeTitulo = cabecalhoBaixaPedido.getEntidade();
		emissaoTitulo = cabecalhoBaixaPedido.getEmissao();		
		calendarVencimentoTitulo.setTime(emissaoTitulo);

		//Encontrando valor total da opera��o
		for (Iterator iterator = itensBaixaPedido.iterator(); iterator.hasNext(); ) {
			ItemBaixaPedido itemBaixaPedido = (ItemBaixaPedido)iterator.next();

			valorOperacao = valorOperacao.add(itemBaixaPedido.getTotal()).setScale(2, RoundingMode.HALF_UP);
		}

		valorResiduo = valorResiduo.add(valorOperacao).setScale(2, RoundingMode.HALF_UP);
		valorPrincipalTitulo = valorOperacao.divide(new BigDecimal(quantidadeParcelas), 2, BigDecimal.ROUND_HALF_UP);

		for (int i = 1; i <= quantidadeParcelas; i++) {
			Titulo titulo = (Titulo)this.novoItem(cabecalhoBaixaPedido);

			titulo.setNumero(new String(i + "/" + quantidadeParcelas));
			titulo.setEntidade(entidadeTitulo);
			titulo.setEmissao(emissaoTitulo);
			titulo.setVencimento(calendarVencimentoTitulo.getTime());
			valorResiduo = valorResiduo.subtract(valorPrincipalTitulo).setScale(2, RoundingMode.HALF_UP);

			if (titulo.getCorrecao() == null) {
				titulo.setCorrecao(titulo.getVencimento());
			}

			if (i == quantidadeParcelas) {
				valorPrincipalTitulo = valorPrincipalTitulo.add(valorResiduo).setScale(2, RoundingMode.HALF_UP);
			}

			valorTotalTitulo = valorPrincipalTitulo;

			titulo.setPrincipal(valorPrincipalTitulo);
			titulo.setTotal(valorTotalTitulo);
			this.dataListItens.save(titulo);

			calendarVencimentoTitulo.add(Calendar.MONTH, 1);
		}
	}

	@Override
	public void abre() throws Exception {
		ArrayList<Titulo> titulos = this.consultaTitulo.pegaTitulosAsList();
		
		if (this.dataListItensBaixaPedido != null && !this.dataListItensBaixaPedido.isEmpty()) {

			ArrayList<Long> operacoesId = new ArrayList<Long>();
			for (Titulo itemTituloBaixaPedido : titulos) {
				operacoesId.add(itemTituloBaixaPedido.getOperacaoId());
			}
			
			this.dataListItens.setLogChanges(false);
			for (Titulo itemTituloBaixaPedido : titulos) {
				this.dataListItens.add(itemTituloBaixaPedido);
			}
			this.dataListItens.setLogChanges(true);
		}
	}

	@Override
	public void fecha() throws Exception {
		this.dataListCabecalhos.setLogChanges(false);
		this.dataListItens.setLogChanges(false);
		this.dataListCabecalhosBaixaPedido.setLogChanges(false);
		this.dataListItensBaixaPedido.setLogChanges(false);
		
		this.dataListCabecalhos.empty();
		this.dataListItens.empty();
		this.dataListCabecalhosBaixaPedido.empty();
		this.dataListItensBaixaPedido.empty();

		this.dataListCabecalhos.setLogChanges(true);
		this.dataListItens.setLogChanges(true);
		this.dataListCabecalhosBaixaPedido.setLogChanges(true);
		this.dataListItensBaixaPedido.setLogChanges(true);

		this.preparado = false;
	}

	@Override
	public void exclui(Long operacaoId) throws Exception {
		//Itens
		Collection<AppEntity> itensOperacao = this.dataListItens.getRangeAsCollection("operacaoId", operacaoId);
		List<AppEntity> listItens = new ArrayList<AppEntity>(itensOperacao);

		for (Iterator iterator = listItens.iterator(); iterator.hasNext(); ) {
			AppEntity appEntity = (AppEntity) iterator.next();
			this.dataListItens.delete(appEntity);
		}		
	}

	@Override
	public void grava() throws Exception {
		throw new Exception("O m�todo grava() dos t�tulos, n�o pode ser chamado isoladamente.");
	}

	@Override
	public CabecalhoOperacaoAbstrato novoCabecalho() throws Exception {
		throw new Exception("Novo cabecalho n�o se aplica a Titulos.");
	}

	@Override
	public ItemOperacao novoItem(CabecalhoOperacaoAbstrato cabecalhoOperacao) throws Exception {
		if ( cabecalhoOperacao == null ){
			throw new Exception("Operacao n�o pode ser nula!");
		}

		if ( !this.dataListCabecalhosBaixaPedido.find("operacaoId", cabecalhoOperacao.getOperacaoId() ) ){
			throw new Exception("Esta opera��o n�o foi encontrada neste objeto!");
		}

		ItemOperacao itemOperacao = new Titulo();
		itemOperacao.createId(this.getAppTransaction().getDatabase());
		itemOperacao.setOperacaoId(cabecalhoOperacao.getOperacaoId());
		itemOperacao.setClasseOperacaoId( this.regraId );

		this.dataListItens.add(itemOperacao);

		return itemOperacao;
	}

	@Override
	public void preparaGravacao() throws Exception {
		this.verificaIntegridadeTitulos();
	}
	
	private void verificaIntegridadeTitulos() throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraTitulo.verificaIntegridadeBaixaTitulos()");

		ArrayList<ItemBaixaTitulo> baixasTitulo = (ArrayList<ItemBaixaTitulo>)this.dataListItens.getList();
		ArrayList<String> erros = new ArrayList(); 

		for (ItemBaixaTitulo baixaTitulo : baixasTitulo) {
			if (baixaTitulo.getId() == null) {
				erros.add("A propriedade \"id\" n�o est� preenchida.");
			}

			if (baixaTitulo.getOperacaoId() == null) {
				erros.add("A propriedade \"operacao\" do pedido: " + baixaTitulo.getId() + " n�o est� preenchida.");
			}

			if (baixaTitulo.getEmissao() == null) {
				erros.add("A propriedade \"emissao\" do pedido: " + baixaTitulo.getId() + " n�o est� preenchida.");
			}

			if (baixaTitulo.getTitulo() == null) {
				erros.add("A propriedade \"titulo\" n�o est� preenchida.");
			}

			if (baixaTitulo.getEntidade() == null) {
				erros.add("A propriedade \"entidade\" do pedido: " + baixaTitulo.getId() + " n�o est� preenchida.");
			}

			if (baixaTitulo.getTotal() == null || baixaTitulo.getTotal().compareTo(new BigDecimal(0)) == 0) {
				erros.add("A propriedade \"total\" do pedido: " + baixaTitulo.getId() + " n�o est� preenchida.");
			}
		}

		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- RegraTitulo.verificaIntegridadeBaixaTitulos() Tempo execu��o->" + ((System.currentTimeMillis() - inicio) /1000));
	}

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
	
	public ArrayList<Titulo> getTitulosCabecalhoBaixaPedido(CabecalhoBaixaPedido cabecalhoBaixaPedido) throws Exception {
		return getTitulosCabecalhoBaixaPedido(cabecalhoBaixaPedido.getOperacaoId());
	}

	public ArrayList<Titulo> getTitulosCabecalhoBaixaPedido(Long operacaoId) throws Exception {
		Collection titulosBaixaPedido = this.dataListItens.getRangeAsCollection("operacaoId", operacaoId);
		
		return (titulosBaixaPedido == null ? null : new ArrayList<Titulo>(titulosBaixaPedido));
	}
}
