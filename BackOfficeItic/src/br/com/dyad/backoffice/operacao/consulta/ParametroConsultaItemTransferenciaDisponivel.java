package br.com.dyad.backoffice.operacao.consulta;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

public class ParametroConsultaItemTransferenciaDisponivel {
	private final String alias; 
	
	public ParametroId id;
	public ParametroId entidade;
	
	public ParametroConsultaItemTransferenciaDisponivel(String alias) {
		super();
		this.alias = alias;
		
		this.id = new ParametroId(alias + ".id");
		this.entidade = new ParametroId(alias + ".entidade.id");
	}
	
	public String getWhere() {
		ArrayList<String> where = new ArrayList<String>();
		String expr;
		
		expr = this.id.getExpressao();
		if (expr != null) {
			where.add(expr);
		}

		expr = this.entidade.getExpressao();
		if (expr != null) {
			where.add(expr);
		}

		String sql = StringUtils.join(where, " and ");
		
		return sql;
	}
}
