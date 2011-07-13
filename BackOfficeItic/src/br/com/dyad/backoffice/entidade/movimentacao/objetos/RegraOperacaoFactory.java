package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import br.com.dyad.commons.data.AppTransaction;

public class RegraOperacaoFactory {
	public static final int PEDIDO = 10;
	public static final int BAIXA_PEDIDO = 11;

	public static final int TITULO = 20;
	public static final int BAIXA_TITULO = 21;

	public static final int TRANSFERENCIA_DISPONIVEL = 30;
	public static final int BAIXA_TRANSFERENCIA_DISPONIVEL = 31;

	public static final int TRANSFERENCIA_DEPOSITO = 40;
	public static final int BAIXA_TRANSFERENCIA_DEPOSITO = 41;
	
	public RegraAbstrata getInstance(int regra, AppTransaction appTransaction) {
		RegraAbstrata objetoRegra = null;
		
		switch (regra) {
			case PEDIDO:
				objetoRegra = new RegraPedido(appTransaction);
				break;
			}

		return objetoRegra;
	}
}