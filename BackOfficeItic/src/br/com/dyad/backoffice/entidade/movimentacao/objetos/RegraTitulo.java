package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.commons.data.AppTransaction;

public class RegraTitulo {
	
	private AppTransaction appTransaction;

	public RegraTitulo(AppTransaction appTransaction) {
		super();

		this.appTransaction = appTransaction;
	};
	
	public AppTransaction getAppTransaction() {
		return appTransaction;
	}

	public ArrayList<Titulo> criaTitulos(CabecalhoOperacaoAbstrato cabecalhoOperacaoAbstrato, Date emissao, BigDecimal valorOperacao, String regraId) throws Exception {
		
		ArrayList<Titulo> titulos = new ArrayList<Titulo>(); 
		int quantidadeParcelas = 1;

		Calendar calendarVencimentoTitulo = new GregorianCalendar();

		BigDecimal valorResiduo = new BigDecimal(0);
		BigDecimal valorPrincipalTitulo = new BigDecimal(0);
		BigDecimal valorTotalTitulo = new BigDecimal(0);

		calendarVencimentoTitulo.setTime(emissao);

		valorResiduo = valorResiduo.add(valorOperacao).setScale(2, RoundingMode.HALF_UP);
		valorPrincipalTitulo = valorOperacao.divide(new BigDecimal(quantidadeParcelas), 2, BigDecimal.ROUND_HALF_UP);

		for (int i = 1; i <= quantidadeParcelas; i++) {
			Titulo titulo = (Titulo)this.novoTitulo(cabecalhoOperacaoAbstrato, regraId);

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
			
			titulos.add(titulo);
			
			calendarVencimentoTitulo.add(Calendar.MONTH, 1);
		}
		
		sincronizaTitulosComCabecalho(cabecalhoOperacaoAbstrato);
		
		return titulos;
	}		
	
	public Titulo novoTitulo(CabecalhoOperacaoAbstrato cabecalhoAbstrato, String regraId) throws Exception {
		if ( cabecalhoAbstrato == null ){
			throw new Exception("Operacao não pode ser nula!");
		}
		
		Titulo titulo = new Titulo();
		titulo.createId(this.getAppTransaction().getDatabase());
		titulo.setCabecalho(cabecalhoAbstrato);
		titulo.setClasseOperacaoId(regraId);
		
		if (cabecalhoAbstrato instanceof CabecalhoBaixaAutomatica) {
			((CabecalhoBaixaAutomatica)cabecalhoAbstrato).addTitulo(titulo);
		} else if (cabecalhoAbstrato instanceof CabecalhoBaixaPedido) {
			((CabecalhoBaixaPedido)cabecalhoAbstrato).addTitulo(titulo);
		}

		return titulo;
	}

	protected void sincronizaTitulosComCabecalho(CabecalhoOperacaoAbstrato cabecalhoOperacaoAbstrato) throws Exception {
		List<Titulo> titulos = null;
		Date emissao = null;
		Entidade entidade = null;
		Estabelecimento estabelecimento = null;
		
		if (cabecalhoOperacaoAbstrato instanceof CabecalhoBaixaAutomatica) {
			CabecalhoBaixaAutomatica cabecalho = ((CabecalhoBaixaAutomatica)cabecalhoOperacaoAbstrato); 
			titulos = cabecalho.getTitulos();
			emissao = cabecalho.getEmissao();
			entidade = cabecalho.getEntidade();
			estabelecimento = cabecalho.getEstabelecimento();
		} else if (cabecalhoOperacaoAbstrato instanceof CabecalhoBaixaPedido) {
			CabecalhoBaixaPedido cabecalho = ((CabecalhoBaixaPedido)cabecalhoOperacaoAbstrato); 
			titulos = cabecalho.getTitulos();
			emissao = cabecalho.getEmissao();
			entidade = cabecalho.getEntidade();
			estabelecimento = cabecalho.getEstabelecimento();
		}

		for (Titulo titulo : titulos) {
			titulo.setEmissao(emissao);
			titulo.setEntidade(entidade);
			titulo.setEstabelecimento(estabelecimento);
		}
		
		RegraTitulo.preencheCampoNumero(titulos);
	}

	public static void preencheCampoNumero(List<Titulo> titulos) throws Exception {
		int i = 0;
		for (Titulo titulo : titulos) {
			i++;
			titulo.setNumero(new String(i + "/" + titulos.size()));
		}		
	}
}
