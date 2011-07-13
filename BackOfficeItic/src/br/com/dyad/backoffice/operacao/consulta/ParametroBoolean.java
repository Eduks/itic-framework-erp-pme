package br.com.dyad.backoffice.operacao.consulta;

public class ParametroBoolean {
	private String expressao;
	
	private boolean valor = false;
	
	/**
	 * 
	 * @param propriedade
	 */
	public ParametroBoolean(String expressao) {
		super();
		this.expressao = expressao;		
	}
	
	public void setValorAsBoolean(boolean valor, String expressao) {
		this.valor = valor;
		this.expressao = expressao;
	}

	public String getExpressao() {
		String where = "";
		
		if (this.valor) {
			where = this.expressao;
		}
		return where;
	}
}