package br.com.dyad.backoffice.operacao.consulta;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.entidade.movimentacao.MovDispo;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;

public class ConsultaMovDispo extends ConsultaOperacao {
	
	private final String ALIAS = "movdispo";

	public ParametroId id = new ParametroId("id");
	public ParametroId operacao = new ParametroId("operacaoId");
	public ParametroId entidade = new ParametroId("entidade.id");
	public ParametroDate emissaoInicial = new ParametroDate("emissao");
	public ParametroDate emissaoFinal = new ParametroDate("emissao");
	public ParametroDate conciliacaoInicial = new ParametroDate("conciliacao");
	public ParametroDate conciliacaoFinal = new ParametroDate("conciliacao");
	
	public ConsultaMovDispo(AppTransaction appTransaction ) {
		super(appTransaction);
	}
	
	private String internoPegaQuery() throws Exception {
		String query = "from " + MovDispo.class.getName() + " " + ALIAS + " ";
		ArrayList<String> where = new ArrayList<String>();
		String expr;
		
		expr = this.id.getExpressao();
		if (expr != null) {
			where.add(expr);
		}

		expr = this.operacao.getExpressao();
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
		expr = this.conciliacaoInicial.getExpressao();
		if (expr != null) {
			where.add(expr);
		}
		expr = this.conciliacaoFinal.getExpressao();
		if (expr != null) {
			where.add(expr);
		}
		
		String sql = query + " where " + StringUtils.join(where, " and ");
		
		return sql;
	}

	public DataList pegaMovDispos() throws Exception {
		String query = this.internoPegaQuery();

		DataList movDispos = DataListFactory.newDataList(this.getAppTransaction());

		movDispos.executeQuery( query );

		return movDispos;
	}

	public ArrayList<MovDispo> pegaMovDisposAsList() throws Exception {
		DataList movDispos = this.pegaMovDispos();
		ArrayList<MovDispo> listBaixas;

		if (movDispos.isEmpty()) {
			listBaixas = new ArrayList<MovDispo>();			
		} else {
			listBaixas = (ArrayList<MovDispo>)movDispos.getList();			
		}

		return listBaixas;
	}
}