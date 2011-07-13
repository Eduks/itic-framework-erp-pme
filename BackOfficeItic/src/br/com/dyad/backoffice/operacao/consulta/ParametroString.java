package br.com.dyad.backoffice.operacao.consulta;

public class ParametroString {
	public final int LIKE_NENHUM = 1;
	public final int LIKE_ANTES = 2;
	public final int LIKE_DEPOIS = 3;
	public final int LIKE_ANTES_DEPOIS = 4;
	
	private String propriedade;
	
	private int like;
	
	private String valor;
	
	/**
	 * 
	 * @param propriedade
	 */
	public ParametroString(String propriedade) {
		super();
		this.propriedade = propriedade;		
	}
	
	public void setValorAsString(String valor) {
		this.setValorAsString(valor, LIKE_NENHUM);
	}
	
	public void setValorAsString(String valor, int like) {
		this.valor = valor;
		this.like = like;
	}
	
	public String getExpressao() {
		String where = "";
		
		if (this.valor != null) {
			
			switch (this.like) {
				case LIKE_NENHUM:
					where = this.propriedade + " " + "'" + this.valor + "'";
					break;
			
				case LIKE_ANTES:
					where = this.propriedade + " LIKE " + "'%" + this.valor + "'";
					break;
			
				case LIKE_DEPOIS:
					where = this.propriedade + " LIKE " + "'" + this.valor + "%'";
					break;
			
				case LIKE_ANTES_DEPOIS:
					where = this.propriedade + " LIKE " + "'%" + this.valor + "%'";
					break;
			}
		}
		
		return where;
	}
}