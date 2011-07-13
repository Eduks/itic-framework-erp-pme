package br.com.dyad.backoffice.operacao.consulta;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

public class ParametroConsultaCabecalhoTransferenciaDisponivel {
	private String alias;
	
	public ParametroId operacaoId;
	public ParametroDate emissaoInicial;
	public ParametroDate emissaoFinal;
	
	public ParametroConsultaCabecalhoTransferenciaDisponivel(String alias) {
		super();
		this.alias = alias;
		
		this.operacaoId = new ParametroId(alias + ".operacaoId");
		this.emissaoInicial = new ParametroDate(alias + ".emissao");
		this.emissaoFinal = new ParametroDate(alias + ".emissao");
	}
	
	public String getWhere() {
		String expr;
		ArrayList<String> where = new ArrayList<String>();
				
		expr = this.operacaoId.getExpressao();
		if (expr != null) {
			where.add(expr);
		}
		expr = this.emissaoInicial.getExpressao();
		if (expr != null) {
			where.add(expr);
		}

		expr = this.emissaoFinal.getExpressao();
		if (expr != null) {
			where.add(expr);
		}
		
		return StringUtils.join(where, " and ");
	}
}
