package br.com.dyad.backoffice.navigation.relatorio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.auxiliares.CabecalhoPedidoAux;
import br.com.dyad.backoffice.entidade.auxiliares.TituloReportAux;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.operacao.consulta.ConsultaSaldoDisponivel;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.entity.BaseEntity;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.ReportWindow;
import br.com.dyad.infrastructure.widget.field.FieldClassLookup;
import br.com.dyad.infrastructure.widget.field.FieldCombo;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.grid.Layout;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class TituloReport extends ReportWindow {
	
	private DataList dataListTitulos; 
	private Layout layout;
	
	public TituloReport(HttpSession httpSession) {
		super(httpSession);
		// TODO Auto-generated constructor stub
		
		ConsultaSaldoDisponivel c = new ConsultaSaldoDisponivel(DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDatabase()), this.getDatabase()));
		try {
			DataList d = c.pegaSaldoDisponivel();
			
			ArrayList list = new ArrayList(d.getList());
			for (Object object : list) {
				System.out.println(object);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.dataListTitulos = DataListFactory.newDataList(this.getDatabase());
	}

	@Override
	public void defineVars(VariableGrid grid) throws Exception {
		FieldCombo fieldSituacao = new FieldCombo( grid );
		fieldSituacao.setName("situacao");
		fieldSituacao.setLabel("Situação");
		fieldSituacao.setVisible(true);
		fieldSituacao.setWidth(150);
		fieldSituacao.setOptions(BaseEntity.getOptions( 1, "Pendentes", 2, "Baixados" ));
		
		FieldCombo fieldModalidade = new FieldCombo( grid );
		fieldModalidade.setName("modalidade");
		fieldModalidade.setLabel("Modalidade");
		fieldModalidade.setVisible(true);
		fieldModalidade.setWidth(150);
		fieldModalidade.setOptions(BaseEntity.getOptions( 1, "Pagamentos", 2, "Recebimentos" ));
		
		FieldClassLookup fieldClasseEntidade = new FieldClassLookup(grid);
		fieldClasseEntidade.setName("classeEntidade");
		fieldClasseEntidade.setLabel("Classes de Entidades Pedido");
		fieldClasseEntidade.setBeanName(Entidade.class.getName());
		
		FieldLookup fieldEntidade = new FieldLookup(grid);
		fieldEntidade.setName("entidade");
		fieldEntidade.setLabel("Entidades Pedido");
		fieldEntidade.setBeanName(Entidade.class.getName());
		
		FieldSimpleDate fieldEmissaoInicial = new FieldSimpleDate(grid);
		fieldEmissaoInicial.setName("emissaoInicial");
		fieldEmissaoInicial.setLabel("Emissão Inicial");
		
		FieldSimpleDate fieldEmissaoFinal = new FieldSimpleDate(grid);
		fieldEmissaoFinal.setName("emissaoFinal");
		fieldEmissaoFinal.setLabel("Emissão Final");
		
		FieldSimpleDate fieldVencimentoInicial = new FieldSimpleDate(grid);
		fieldVencimentoInicial.setName("vencimentoInicial");
		fieldVencimentoInicial.setLabel("Vencimento Inicial");
		
		FieldSimpleDate fieldVencimentoFinal = new FieldSimpleDate(grid);
		fieldVencimentoFinal.setName("vencimentoFinal");
		fieldVencimentoFinal.setLabel("Vencimento Final");
		
		FieldSimpleDate fieldCorrecaoInicial = new FieldSimpleDate(grid);
		fieldCorrecaoInicial.setName("correcaoInicial");
		fieldCorrecaoInicial.setLabel("Correção Inicial");
		
		FieldSimpleDate fieldCorrecaoFinal = new FieldSimpleDate(grid);
		fieldCorrecaoFinal.setName("correcaoFinal");
		fieldCorrecaoFinal.setLabel("Correção Final");
		
		FieldSimpleDate fieldAprovacaoInicial = new FieldSimpleDate(grid);
		fieldAprovacaoInicial.setName("aprovacaoInicial");
		fieldAprovacaoInicial.setLabel("Aprovação Inicial");
		
		FieldSimpleDate fieldAprovacaoFinal = new FieldSimpleDate(grid);
		fieldAprovacaoFinal.setName("aprovacaoFinal");
		fieldAprovacaoFinal.setLabel("Aprovação Final");
	}

	public String prepareWhereExpression() {
		ArrayList<ParameterFilter> listParametros = new ArrayList<ParameterFilter>();
		HashMap<String, String[]> hashFilter = new HashMap<String, String[]>();
		
		Integer situacao = (Integer)this.vars.getFieldByName("situacao").getValue();
		Integer modalidade = (Integer)this.vars.getFieldByName("modalidade").getValue();
		Long classeEntidade = (Long)this.vars.getFieldByName("classeEntidade").getValue();
		Long entidade = (Long)this.vars.getFieldByName("entidade").getValue();
		Date emissaoInicial = (Date)this.vars.getFieldByName("emissaoInicial").getValue();
		Date emissaoFinal = (Date)this.vars.getFieldByName("emissaoFinal").getValue();
		Date vencimentoInicial = (Date)this.vars.getFieldByName("vencimentoInicial").getValue();
		Date vencimentoFinal = (Date)this.vars.getFieldByName("vencimentoFinal").getValue();
		Date correcaoInicial = (Date)this.vars.getFieldByName("correcaoInicial").getValue();
		Date correcaoFinal = (Date)this.vars.getFieldByName("correcaoFinal").getValue();
		Date aprovacaoInicial = (Date)this.vars.getFieldByName("aprovacaoInicial").getValue();
		Date aprovacaoFinal = (Date)this.vars.getFieldByName("aprovacaoFinal").getValue();
		
		if (situacao != null && (situacao == 1 || situacao == 2)) {
			ParameterFilter parameterFilter = new ParameterFilter();

			if (situacao.equals(1 /* Pendentes */) )  {
				parameterFilter.setWhere(
						"(  " +
						"   Not Exists (" +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa " +
						"      Where " +
						"         Baixa.titulo = Titulo " +
						"   ) OR ( " +
						"      Select " +
						"         sum(Baixa2.total) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa2 " +
						"      Where " +
						"         Baixa2.titulo = Titulo " +
						"   ) < Titulo.total" +
						") "
				);
				
			} else if (situacao.equals(2 /* Baixados */)) {
				parameterFilter.setWhere(
						"   ( " +
						"      Select " +
						"         sum(Baixa2.quantidade) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa2 " +
						"      Where " +
						"         Baixa2.titulo = Titulo " +
						"   ) => Titulo.total"
				);
				
			}
			
			listParametros.add(parameterFilter);
		}
		
		if (modalidade != null && (modalidade.equals(1) && modalidade.equals(2))) {
			ParameterFilter parameterFilter = new ParameterFilter();

			if (modalidade.equals(1 /* Pagamentos */)) {
				parameterFilter.setWhere("titulo.total < 0");
				
			} else if (modalidade.equals(2 /* Recebimentos */)) {
				parameterFilter.setWhere("titulo.total > 0");
				
			}
			
			listParametros.add(parameterFilter);
		}
		
		if (entidade != null) {
			ParameterFilter parameterFilter = new ParameterFilter();
			
			parameterFilter.setWhere("titulo.entidade in (:entidadesId)");
			parameterFilter.setClasse(String.class);
			parameterFilter.setNomeParametro("entidadesId");
			parameterFilter.setValor(entidade);
			
			listParametros.add(parameterFilter);
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

		//Emissao
		if (emissaoInicial != null) {
			ParameterFilter parameterFilter = new ParameterFilter();
			
			parameterFilter.setWhere("titulo.emissao >= :emissaoInicial");
			parameterFilter.setClasse(Date.class);
			parameterFilter.setNomeParametro("emissaoInicial");
			parameterFilter.setValor(emissaoInicial);
			
			listParametros.add(parameterFilter);
		} 
		if (emissaoFinal != null) {
			ParameterFilter parameterFilter = new ParameterFilter();
			
			parameterFilter.setWhere("titulo.emissao <= :emissaoFinal");
			parameterFilter.setClasse(Date.class);
			parameterFilter.setNomeParametro("emissaoFinal");
			parameterFilter.setValor(emissaoFinal);
			
			listParametros.add(parameterFilter);
		} 

		//Vencimento
		if (vencimentoInicial != null) {
			ParameterFilter parameterFilter = new ParameterFilter();
			
			parameterFilter.setWhere("titulo.emissao >= :vencimentoInicial");
			parameterFilter.setClasse(Date.class);
			parameterFilter.setNomeParametro("vencimentoInicial");
			parameterFilter.setValor(vencimentoInicial);
			
			listParametros.add(parameterFilter);
		} 
		if (vencimentoFinal != null) {
			ParameterFilter parameterFilter = new ParameterFilter();
			
			parameterFilter.setWhere("titulo.vencimento <= :vencimentoFinal");
			parameterFilter.setClasse(Date.class);
			parameterFilter.setNomeParametro("vencimentoFinal");
			parameterFilter.setValor(vencimentoFinal);
			
			listParametros.add(parameterFilter);
		} 

		//Correcao
		if (correcaoInicial != null) {
			ParameterFilter parameterFilter = new ParameterFilter();
			
			parameterFilter.setWhere("titulo.correcao >= :correcaoInicial");
			parameterFilter.setClasse(Date.class);
			parameterFilter.setNomeParametro("correcaoInicial");
			parameterFilter.setValor(correcaoInicial);
			
			listParametros.add(parameterFilter);
		} 
		if (correcaoFinal != null) {
			ParameterFilter parameterFilter = new ParameterFilter();
			
			parameterFilter.setWhere("titulo.correcao <= :correcaoFinal");
			parameterFilter.setClasse(Date.class);
			parameterFilter.setNomeParametro("correcaoFinal");
			parameterFilter.setValor(correcaoFinal);
			
			listParametros.add(parameterFilter);
		} 

		//Aprovacao
		if (aprovacaoInicial != null) {
			ParameterFilter parameterFilter = new ParameterFilter();
			
			parameterFilter.setWhere("cabecalho.aprovacao >= :aprovacaoInicial");
			parameterFilter.setClasse(Date.class);
			parameterFilter.setNomeParametro("aprovacaoInicial");
			parameterFilter.setValor(aprovacaoInicial);
			
			listParametros.add(parameterFilter);
		} 
		if (aprovacaoFinal != null) {
			ParameterFilter parameterFilter = new ParameterFilter();
			
			parameterFilter.setWhere("cabecalho.aprovacao <= :aprovacaoFinal");
			parameterFilter.setClasse(Date.class);
			parameterFilter.setNomeParametro("aprovacaoFinal");
			parameterFilter.setValor(aprovacaoFinal);
			
			listParametros.add(parameterFilter);
		} 

		return processaListaParametros("", listParametros); 
	}
	
	public String processaListaParametros(String sql, ArrayList<ParameterFilter> parametros) {
		ArrayList<String> whereExpression = new ArrayList<String>();
		
		
		for (ParameterFilter parametro : parametros) {
			whereExpression.add(parametro.getWhere());
		}
		
		return StringUtils.join(whereExpression, " and ");
	}
	
	@Override
	public void prepareLayout() throws Exception {
		ArrayList<String> listWhereCabecalho = new ArrayList<String>();
		String stringWhereCabecalho = new String();

		ArrayList<String> listWhereTitulo = new ArrayList<String>();
		ArrayList<String> listJoinTitulo = new ArrayList<String>();
		String stringWhereTitulo = new String();
		String stringJoinTitulo = new String();
		
		Date aprovacaoInicial = (Date)this.vars.getFieldByName("aprovacaoInicial").getValue();
		Date aprovacaoFinal = (Date)this.vars.getFieldByName("aprovacaoFinal").getValue();
		
		Integer situacao = (Integer)this.vars.getFieldByName("situacao").getValue();
		Integer modalidade = (Integer)this.vars.getFieldByName("modalidade").getValue();
		Long classeEntidade = this.vars.getFieldByName("classeEntidade").getValue() != null 
								? Long.parseLong(this.vars.getFieldByName("classeEntidade").getValue().toString())
								: null;
		Long entidade = this.vars.getFieldByName("entidade").getValue() != null 
							? ((Pessoa)this.vars.getFieldByName("entidade").getValue()).getId()
							: null;
		Date emissaoInicial = (Date)this.vars.getFieldByName("emissaoInicial").getValue();
		Date emissaoFinal = (Date)this.vars.getFieldByName("emissaoFinal").getValue();
		Date vencimentoInicial = (Date)this.vars.getFieldByName("vencimentoInicial").getValue();
		Date vencimentoFinal = (Date)this.vars.getFieldByName("vencimentoFinal").getValue();
		Date correcaoInicial = (Date)this.vars.getFieldByName("correcaoInicial").getValue();
		Date correcaoFinal = (Date)this.vars.getFieldByName("correcaoFinal").getValue();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

		//Where para CABECALHO
		if (aprovacaoInicial != null) {
			listWhereCabecalho.add("cabecalho.aprovacao >= '" + simpleDateFormat.format(aprovacaoInicial) + "'");

		} 
		if (aprovacaoFinal != null) {
			listWhereCabecalho.add("cabecalho.aprovacao <= '" + simpleDateFormat.format(aprovacaoFinal) + "'");

		} 

		//Where para TITULO
		if (situacao != null && (situacao == 1 || situacao == 2)) {
			if (situacao.equals(1 /* Pendentes */) )  {
				listWhereTitulo.add(
						"(  " +
						"   Not Exists (" +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa " +
						"      Where " +
						"         Baixa.titulo = Titulo " +
						"   ) OR ( " +
						"      Select " +
						"         sum(Baixa2.total) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa2 " +
						"      Where " +
						"         Baixa2.titulo = Titulo " +
						"   ) < Titulo.total" +
						") "
				);
				
			} else if (situacao.equals(2 /* Baixados */)) {
				listWhereTitulo.add(
						"   ( " +
						"      Select " +
						"         sum(Baixa2.quantidade) " +
						"      From " +
						"         br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo Baixa2 " +
						"      Where " +
						"         Baixa2.titulo = Titulo " +
						"   ) => Titulo.total"
				);
				
			}
		}
		
		if (modalidade != null && (modalidade.equals(1) && modalidade.equals(2))) {
			if (modalidade.equals(1 /* Pagamentos */)) {
				listWhereTitulo.add("titulo.total < 0");
				
			} else if (modalidade.equals(2 /* Recebimentos */)) {
				listWhereTitulo.add("titulo.total > 0");
				
			}

		}
		
		if (classeEntidade != null) {
			listWhereTitulo.add("entidade.classId = '" + classeEntidade + "'");
			listJoinTitulo.add("INNER JOIN Entidade ON (titulo.entidadeId = Entidade.id)");			
		}

		if (entidade != null) {
			listWhereTitulo.add("titulo.entidadeId in (" + entidade + ")");
			
		}

		//Emissao
		if (emissaoInicial != null) {
			listWhereTitulo.add("titulo.emissao >= '" + simpleDateFormat.format(emissaoInicial) + "'");

		} 
		if (emissaoFinal != null) {
			listWhereTitulo.add("titulo.emissao <= '" + simpleDateFormat.format(emissaoFinal) + "'");

		} 

		//Vencimento
		if (vencimentoInicial != null) {
			listWhereTitulo.add("titulo.vencimento >= '" + simpleDateFormat.format(vencimentoInicial) + "'");

		} 
		if (vencimentoFinal != null) {
			listWhereTitulo.add("titulo.vencimento <= '" + simpleDateFormat.format(vencimentoFinal) + "'");

		} 

		//Correcao
		if (correcaoInicial != null) {
			listWhereTitulo.add("titulo.correcao >= '" + simpleDateFormat.format(correcaoInicial) + "'");

		} 
		if (correcaoInicial != null) {
			listWhereTitulo.add("titulo.correcao <= '" + simpleDateFormat.format(correcaoFinal) + "'");

		} 

		stringWhereCabecalho = (listWhereCabecalho.isEmpty() ? "" : (" and " + StringUtils.join(listWhereCabecalho, " and "))); 
		stringWhereTitulo = (listWhereTitulo.isEmpty() ? "" : (" and " + StringUtils.join(listWhereTitulo, " and ")));
		stringJoinTitulo = (listJoinTitulo.isEmpty() ? "" : (StringUtils.join(listJoinTitulo, " ")));
				
		String sql = "" +
		"SELECT " +
		"   {cabecalho.*}, " +
		"   {titulo.*}, " +
		"   ( " +
		"   SELECT " +
		"      MAX(emissao) " +
		"   FROM " +
		"      Operacao " +
		
		"   WHERE" +
		"      Operacao.classId = '-89999999999723' AND Operacao.baixadoId = Titulo.id" +
		"   ) as dataBaixa " +
		"FROM " +
		"   ( " +
		"   SELECT " +
		"      operacaoId, " +
		"      entidadeId, " +
		"      estabelecimentoId, " +
		"      emissao, " +
		"      aprovacao, " +
		"      aprovadorId, " +
		"      cancelamento, " +
		"	   canceladorId " +
		"   FROM " +
		"      Operacao " +
		"   WHERE " +
        "      Operacao.classId = '-99999899999218' " + stringWhereCabecalho +
		"   GROUP BY " +
		"      operacaoId,entidadeId,estabelecimentoId,emissao,aprovacao,aprovadorId,cancelamento,canceladorId " +
		"   ) Cabecalho, " +
		"   Operacao Titulo " + stringJoinTitulo +
		" WHERE " +
		"   titulo.operacaoId = cabecalho.operacaoId AND " +
        "   titulo.classId = '-89999999999724' " + stringWhereTitulo;
		
		Session session = PersistenceUtil.getSession(this.getDatabase());

		SQLQuery sqlQuery = session.createSQLQuery( sql );
		sqlQuery.addEntity("cabecalho", CabecalhoPedidoAux.class);
		sqlQuery.addEntity("titulo", Titulo.class);
		sqlQuery.addScalar("dataBaixa", Hibernate.DATE);
		
		//this.dataListTitulos.getList().clear();
		this.dataListTitulos = DataListFactory.newDataList(this.getDatabase());
		
		ArrayList list = new ArrayList(sqlQuery.list());
		for (Object object : list) {
			Object[] objectAux = (Object[])object;
			CabecalhoPedidoAux cabecalhoPedidoAux = (CabecalhoPedidoAux) objectAux[0];
			Titulo titulo = (Titulo) objectAux[1];
			Date dataBaixa = (Date) objectAux[2]; 
			
			TituloReportAux tituloReportAux = new TituloReportAux();
			
			tituloReportAux.setId(titulo.getId());
			tituloReportAux.setOperacaoId(titulo.getCabecalho().getId());
			tituloReportAux.setNumero(titulo.getNumero());
			tituloReportAux.setEstabelecimento(cabecalhoPedidoAux.getEstabelecimento());
			tituloReportAux.setEntidade(titulo.getEntidade());
			tituloReportAux.setEmissao(titulo.getEmissao());
			tituloReportAux.setVencimento(titulo.getVencimento());
			tituloReportAux.setBaixa(dataBaixa);
			tituloReportAux.setCorrecao(titulo.getCorrecao());
			tituloReportAux.setPrincipal(titulo.getPrincipal());
			tituloReportAux.setTotal(titulo.getTotal());
			
			this.dataListTitulos.add(tituloReportAux);
		}
	}

	@Override
	public void defineShowLayout(Interaction interaction) throws Exception {
		
		if ( this.layout != null ){
			interaction.remove( this.layout );
		}
		
		layout = new Layout(this, "logLayout", this.dataListTitulos, TituloReportAux.class.getName());
		layout.setTitle("Relatório de Títulos");
		
		interaction.add(layout);
	}
}

class ParameterFilter {
	String where;
	Class classe;
	String parametro;
	Object valor;

	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public Class getClasse() {
		return classe;
	}
	public void setClasse(Class classe) {
		this.classe = classe;
	}
	public String getNomeParametro() {
		return 	"";//nomeParametro;
	}
	public void setNomeParametro(String nomeParametro) {
		//this.nomeParametro = nomeParametro;
	}
	public Object getValor() {
		return valor;
	}
	public void setValor(Object valor) {
		this.valor = valor;
	}
	
}