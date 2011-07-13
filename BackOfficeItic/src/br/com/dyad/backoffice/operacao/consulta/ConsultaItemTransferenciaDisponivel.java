package br.com.dyad.backoffice.operacao.consulta;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;

public class ConsultaItemTransferenciaDisponivel extends ConsultaOperacao {

	private final String ALIAS = "movdispo";

	public ParametroId id = new ParametroId("id");
	public ParametroId operacao = new ParametroId("operacaoId");
	public ParametroId entidade = new ParametroId("entidade.id");
	public ParametroDate emissaoInicial = new ParametroDate("emissao");
	public ParametroDate emissaoFinal = new ParametroDate("emissao");

	private String expressao;		
	
	public String getExpressao() {
		return expressao;
	}

	public void setExpressao(String expressao) {
		this.expressao = expressao;
	}

	/**
	 * 
	 * @param dataBase
	 */
	public ConsultaItemTransferenciaDisponivel(AppTransaction appTransaction) {
		super(appTransaction);
	}

	private String internoPegaQueryTransferenciaDisponivel() throws Exception {
		String query = "from " + ItemTransferenciaDisponivel.class.getName() + " " + ALIAS + " ";
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

		expr = this.entidade.getExpressao();
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

		String sql = query + " where " + StringUtils.join(where, " and ");
		
		return sql;
	}
	
	public DataList pegaTransferenciasDisponiveis() throws Exception {
		String query = this.internoPegaQueryTransferenciaDisponivel();
		DataList transferenciasDisponiveis = DataListFactory.newDataList(this.getAppTransaction());
		transferenciasDisponiveis.executeQuery( query );
		
		return transferenciasDisponiveis;
	}

	public ArrayList<ItemTransferenciaDisponivel> pegaTransferenciasDisponiveisAsList() throws Exception {
		DataList transferenciasDisponiveis = this.pegaTransferenciasDisponiveis();
		ArrayList<ItemTransferenciaDisponivel> listTransferenciasDisponiveis;

		if (transferenciasDisponiveis.isEmpty()) {
			listTransferenciasDisponiveis = new ArrayList<ItemTransferenciaDisponivel>();			
		} else {
			listTransferenciasDisponiveis = (ArrayList<ItemTransferenciaDisponivel>)transferenciasDisponiveis.getList();			
		}
		
		return listTransferenciasDisponiveis;
	}
}