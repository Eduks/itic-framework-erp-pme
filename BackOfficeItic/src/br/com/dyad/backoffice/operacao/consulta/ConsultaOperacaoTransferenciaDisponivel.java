package br.com.dyad.backoffice.operacao.consulta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.cadastro.TipoTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoTransferenciaDisponivel;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class ConsultaOperacaoTransferenciaDisponivel{
	
	private AppTransaction appTransaction;
	
	private Long itemId;
	private Long operacaoId;	
	private Date emissaoInicial;
	private Date emissaoFinal;
	private Entidade entidade;
	private TipoTransferenciaDisponivel tipoTransferenciaDisponivel;
	private Pessoa classeDeEntidade;
	
	private ArrayList<Object> parametrosConsulta;
	
	/**
	 * 
	 * @param dataBase
	 */
	public ConsultaOperacaoTransferenciaDisponivel(AppTransaction appTransaction) {
		this.appTransaction = appTransaction;
	}

	private String internoPegaQueryTransferenciaDisponivel() throws Exception {
		
		String query = "from "+CabecalhoTransferenciaDisponivel.class.getName()+" cabecalho";
		ArrayList<String> where = new ArrayList<String>();
		
		parametrosConsulta = new ArrayList<Object>(0);
		
		if (operacaoId != null) {
			where.add(" cabecalho.id = ?");
			parametrosConsulta.add(operacaoId);
		}
		
		if (itemId != null || entidade != null) {
			String filtro = " cabecalho.id IN (SELECT item.cabecalho.id FROM "+ItemTransferenciaDisponivel.class.getName()+" item WHERE 1=1";
			
			if (itemId != null) {
				filtro += " AND item.id = ?";
				parametrosConsulta.add(itemId);
			}
			
			if (entidade != null){
				filtro += " AND item.entidade.id = ?";
				parametrosConsulta.add(entidade.getId());
			}
			
			filtro += ")";
			
			where.add(filtro);
		}
		
//		if (classeDeEntidade != null) {
//			where.add(" cabecalho.id IN " +
//					"(SELECT item.cabecalho.id FROM "+ItemTransferenciaDisponivel.class.getName()+" item" +
//					" WHERE item.entidade.id = "+entidade.getId()+")");
//		}
		
		//Emissão Inicial
		if (emissaoInicial != null) {
			where.add(" cabecalho.emissao >= ?");
			parametrosConsulta.add(emissaoInicial);
		}		
		//Emissão Final
		if (emissaoFinal != null) {
			where.add(" cabecalho.emissao <= ?");
			parametrosConsulta.add(emissaoFinal);
		}
		
		//Verifica se foi preenchido pelo menos um parâmetro para filtrar os lançamentos, e não trazer todos os lançamentos sem filtro
		if (where.isEmpty()){
			throw new AppException("Não foi definido nenhum filtro para a consulta. Para realizar a consulta informe pelo menos um parâmetro.");
		}
		
		return query + " where " + StringUtils.join(where, " and ") + " ORDER BY cabecalho.emissao";
	}
	
	public DataList pegaTransferenciasDisponiveis() throws Exception {
		String query = this.internoPegaQueryTransferenciaDisponivel();
		DataList transferenciasDisponiveis = DataListFactory.newDataList(this.getAppTransaction());
		
		List lista = PersistenceUtil.executeHql((Session)getAppTransaction().getSession(), query, parametrosConsulta);
		
		transferenciasDisponiveis.setList(lista);
		
		return transferenciasDisponiveis;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long item) {
		this.itemId = item;
	}

	public Long getOperacaoId() {
		return operacaoId;
	}

	public void setOperacaoId(Long operacaoId) {
		this.operacaoId = operacaoId;
	}

	public Date getEmissaoInicial() {
		return emissaoInicial;
	}

	public void setEmissaoInicial(Date emissaoInicial) {
		this.emissaoInicial = emissaoInicial;
	}

	public Date getEmissaoFinal() {
		return emissaoFinal;
	}

	public void setEmissaoFinal(Date emissaoFinal) {
		this.emissaoFinal = emissaoFinal;
	}

	public Entidade getEntidade() {
		return entidade;
	}

	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}

	public Pessoa getClasseDeEntidade() {
		return classeDeEntidade;
	}

	public void setClasseDeEntidade(Pessoa classeDeEntidade) {
		this.classeDeEntidade = classeDeEntidade;
	}

	public AppTransaction getAppTransaction() {
		return appTransaction;
	}

	public void setAppTransaction(AppTransaction appTransaction) {
		this.appTransaction = appTransaction;
	}

	public TipoTransferenciaDisponivel getTipoTransferenciaDisponivel() {
		return tipoTransferenciaDisponivel;
	}

	public void setTipoTransferenciaDisponivel(
			TipoTransferenciaDisponivel tipoTransferenciaDisponivel) {
		this.tipoTransferenciaDisponivel = tipoTransferenciaDisponivel;
	}
}