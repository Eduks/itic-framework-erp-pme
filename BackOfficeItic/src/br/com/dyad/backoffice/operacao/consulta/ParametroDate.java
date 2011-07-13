package br.com.dyad.backoffice.operacao.consulta;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ParametroDate {
	public static final String NULL = "IS NULL";
	public static final String IGUAL = "=";
	public static final String MAIOR = ">";
	public static final String MENOR = "<";
	public static final String MAIOR_IGUAL = ">=";
	public static final String MENOR_IGUAL = "<=";
	public static final String DIFERENTE = "<>";
	
	private String propriedade;
	
	private String operador;
	
	private Date valor;
	
	/**
	 * 
	 * @param propriedade
	 */
	public ParametroDate(String propriedade) {
		super();
		this.propriedade = propriedade;		
	}
	
	public void setValorAsDate(Date valor, String operador) {
		this.valor = valor;
		this.operador = operador;
	}

	public String getExpressao() {
		String where = null;
		
		if (this.valor != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			where = this.propriedade + " " + operador + " " + formatter.format(this.valor);
		}
		return where;
	}
}