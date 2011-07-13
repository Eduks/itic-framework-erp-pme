/*
   --------------------------------------------------------------------
   Modifica��es:
   
   Desen.  Data      Requisito       Mudanca
   --------------------------------------------------------------------
   Haron 28/07/2010                   Altera��o na classe
   --------------------------------------------------------------------  

*/
package br.com.dyad.backoffice.navigation.relatorio;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.createclass.DynamicClassFactory;
import br.com.dyad.infrastructure.createclass.PropertyDescriptor;
import br.com.dyad.infrastructure.entity.BaseEntity;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.Label;
import br.com.dyad.infrastructure.widget.ReportWindow;
import br.com.dyad.infrastructure.widget.field.FieldCombo;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.grid.Layout;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

/**
 * @author Haron
 * @since 28/07/2010
 * <hr>
 * <pre>
 * Funcao.....:
 * Sistema....: BackOffice
 * Requisito..:
 * Observa��o.:
 * </pre>
 */

public class FluxoDeCaixa extends ReportWindow{
	
	//boolean jaImprimiuLegenda = false;
	
	private Calendar dataASerAnalisada;
	private List listRealizado;
	private List listFuturo;
	private DataList dataListDinamico;
	private SimpleDateFormat formatadorData;
	private Layout relatorioFluxoCaixa;
	private List listObjetosDinamicos;
	private List agrupamento;
	private List propriedadesDinamicas;
	private Date dataFim;
	private Date dataInicio;
	private Date dataInicioRealizado;
	private Date dataFimRealizado;
	private Date dataInicioFuturo;
	private Date dataFimFuturo;
	//private Label explicacaoFluxoCaixa;
	private Class classeDinamica;
	
	private String periodo;
	private String agrupamento1;
	
	private HashMap<Long, HashMap<String,BigDecimal>> titulos;
	private HashMap<String,BigDecimal> tituloPorEntidade;
	
	private HashMap<Long, HashMap<String,BigDecimal>> realizados;
	private HashMap<String,BigDecimal> realizadoPorEntidade;
	/**
	 * @param httpSession
	 */
	public FluxoDeCaixa(HttpSession httpSession) {
		super(httpSession);
		// TODO Auto-generated constructor stub
		this.dataASerAnalisada =  new GregorianCalendar();
		this.formatadorData = new SimpleDateFormat();
		
	}
	
	@Override
	public void defineWindow() throws Exception {

		super.defineWindow();
		
		dataListDinamico = DataListFactory.newDataList(this.getDatabase());
		listFuturo = new ArrayList();
		listRealizado = new ArrayList();
		listObjetosDinamicos =  new ArrayList();
		agrupamento = new ArrayList();
		
		
		
	}
	
	@Override
	public void defineVars(VariableGrid grid) throws Exception {
		grid.setTitle("Filtros");
		
		FieldCombo periodo = new FieldCombo(grid);
		periodo.setRequired(true);
		periodo.setOptions(BaseEntity.getOptions("A","Anual" ,
												 "D","Diário",
												 "M","Mensal"));
		
		periodo.setGroup("Datas");
		periodo.setName("periodo");
		
		FieldSimpleDate dataInicio =  new FieldSimpleDate(grid);
		dataInicio.setRequired(true);
		dataInicio.setGroup("Datas");
		dataInicio.setColumn(0);
		dataInicio.setName("dataInicio");
		
		FieldSimpleDate dataFim =  new FieldSimpleDate(grid);
		dataFim.setGroup("Datas");
		dataFim.setName("dataFim");
		dataFim.setColumn(1);
		
		FieldCombo agrupamento1 = new FieldCombo(grid);
		
		agrupamento1.setGroup("Agrupamento");
		agrupamento1.setOptions(BaseEntity.getOptions("entidade","Pessoa"));
		agrupamento1.setRequired(true);
		agrupamento1.setName("agrupamento1");
		
	}
	
	@Override
	public void prepareLayout() throws Exception {
		System.out.println("****************************************prepareLayout: " + new Date());
		formatadorData.applyPattern("dd/MM/yyyy");
		dataInicio = (Date) vars.getFieldByName("dataInicio").getValue();
		dataFim    = (Date) vars.getFieldByName("dataFim").getValue();
		Date dataAtual = formatadorData.parse(formatadorData.format(new Date()));
		
		if (dataFim == null){
			dataFim = new Date();
		}
		
		Date dtIni = formatadorData.parse(formatadorData.format(dataInicio));
		Date dtFim = formatadorData.parse(formatadorData.format(dataFim));
		
		dataInicioRealizado = null;
		dataFimRealizado = null; 
		dataInicioFuturo = null;
		dataFimFuturo = null;
		
		
		if(dtFim.compareTo(dataAtual) < 0){
			dataInicioRealizado = dtIni;
			dataFimRealizado = dtFim;
			dataASerAnalisada.setTime(dataFimRealizado);
			dataASerAnalisada.add(Calendar.DAY_OF_MONTH, 1);
			dataInicioFuturo = dataASerAnalisada.getTime();
			dataFimFuturo = dataAtual;
			
		}else{
			if(dtFim.compareTo(dataAtual)==0){
				dataInicioRealizado = dtIni;
				dataFimRealizado = dtFim;
				dataASerAnalisada.setTime(dataFimRealizado);
				dataASerAnalisada.add(Calendar.DAY_OF_MONTH, 1);
				dataInicioFuturo = dataASerAnalisada.getTime();
			}else{
				dataInicioRealizado = dtIni;
				dataFimRealizado = dataAtual;
				dataASerAnalisada.setTime(dataFimRealizado);
				dataASerAnalisada.add(Calendar.DAY_OF_MONTH, 1);
				dataInicioFuturo = dataASerAnalisada.getTime();
				dataFimFuturo = dataAtual;
			}
		}
		
		
		
		
		periodo = (String)vars.getFieldByName("periodo").getValue();
		agrupamento1 = (String)vars.getFieldByName("agrupamento1").getValue(); 
		
		
		
		if( agrupamento1 != null && ! agrupamento.contains(agrupamento1)){
			agrupamento.add(agrupamento1);
		}
		
		String mensagemErro = validarParametrosDeEntrada(dataInicio, dataFim, periodo, agrupamento1);
		
		Timestamp dataIni = new Timestamp(dataInicio.getTime());
		Timestamp dataFinal = new Timestamp(dataFim.getTime());
		
		
		
		if( ! mensagemErro.isEmpty()){
			throw new AppException(mensagemErro);
		}
		
		try {
			String queryRealizado = " select baixaTitulo.entidade.id as codBaixaTitulo," +
									"        baixaTitulo.entidade.nome as nomeEntidade," +	
									"        baixaTitulo.emissao as emissaoBaixaTitulo," +
									"        sum(baixaTitulo.total) as totalBaixaTitulo" +
									" from "+ItemBaixaTitulo.class.getName()+ " baixaTitulo " +
									" where baixaTitulo.emissao between ? and ? " +
									" group by ";
									for( int i = 0; i < agrupamento.size() ; i++ ){
										if(agrupamento.get(i).equals("entidade")){
											queryRealizado += "baixaTitulo."+agrupamento.get(i)+".id,";
										}
									}
									queryRealizado += " baixaTitulo.entidade.nome,";
									queryRealizado += " baixaTitulo.emissao" +
									" order by   baixaTitulo.emissao";
			
			String queryFuturo = " select tit.entidade.id as codTitula," +
								 "		  tit.entidade.nome as nomeEntidade, " +
								 "		  tit.emissao as emissaoTitulo, " +
								 "		  sum(tit.total) as totalTitulo" +
								 " from "+ Titulo.class.getName()+ " tit "+
								 " where ";
								 if(dtFim.compareTo(dataAtual) == 0){
									queryFuturo += "	tit.emissao >  ? ";
								 }else{
									queryFuturo += "    tit.emissao  between ? and ? "; 
								 }
								 queryFuturo+=" group by ";
								 for( int i = 0; i < agrupamento.size() ; i++ ){
									if(agrupamento.get(i).equals("entidade")){
										queryFuturo += "tit."+agrupamento.get(i)+".id,";
									}
								 }
								 queryFuturo += " tit.entidade.nome,";
								 queryFuturo += " tit.emissao" +
								 " order by tit.emissao" ;
			
			Session session = PersistenceUtil.getSession(this.getDatabase());
			
			formatadorData.applyPattern("dd/MM/yyyy");
		
			
			listRealizado = PersistenceUtil.executeHql(session, queryRealizado, new Object[]{dataInicioRealizado,dataFimRealizado});
			
			
			if(dtFim.compareTo(dataAtual) == 0){
				listFuturo = PersistenceUtil.executeHql(session, queryFuturo, new Object[]{dataInicioFuturo});
			}else{
				listFuturo = PersistenceUtil.executeHql(session, queryFuturo, new Object[]{dataInicioFuturo,dataFimFuturo});
			}
			
			//if(relatorioFluxoCaixa == null){
			//	relatorioFluxoCaixa =  new Layout(this, "relatorioFluxoCaixa");
			//}
			
			if ( dataListDinamico == null){
				dataListDinamico =  DataListFactory.newDataList(this.getDatabase());
			}
			
			
			
			listObjetosDinamicos = new ArrayList();
			
			
			classeDinamica = montarClasseBaseadoNoPeriodo(periodo, dataInicio, dataFim);
			
			construirListObjetosDinamicos(classeDinamica);
			
			//definirLayoutDinamico(periodo, classeDinamica, relatorioFluxoCaixa);
			
			dataListDinamico.setList(listObjetosDinamicos);
		
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		

		
	}
	
	
	@Override
	public void defineShowLayout(Interaction interaction) throws Exception {
		System.out.println("****************************************defineShowLayout: " + new Date());
		/*String periodo = (String) vars.getFieldByName("periodo").getValue();
		Date inicio = (Date)vars.getFieldByName("dataInicio").getValue();
		Date fim	= (Date)vars.getFieldByName("dataFim").getValue();*/
		
		if (relatorioFluxoCaixa != null){
			interaction.remove(relatorioFluxoCaixa);
		}
		
		//if (explicacaoFluxoCaixa != null){
		//	interaction.remove(explicacaoFluxoCaixa);
		//}
		
		relatorioFluxoCaixa =  new Layout(this, "relatorioFluxoCaixa");
		relatorioFluxoCaixa.setTitle("Fluxo de Caixa");
		relatorioFluxoCaixa.setBeanName(classeDinamica.getName());
		relatorioFluxoCaixa.setDataList(dataListDinamico);
		
		definirLayoutDinamico(periodo, classeDinamica, relatorioFluxoCaixa);
		
		//if (explicacaoFluxoCaixa == null){
		//	explicacaoFluxoCaixa = new Label(" <font color=red>Obs:</font> <br> " +
		//										"<b>R - Realizado;</b> <br> <b>F - Futuro;</b>");
		//}
		
		interaction.add(relatorioFluxoCaixa);
		//	interaction.add(explicacaoFluxoCaixa);
	}
	
	public void definirLayoutDinamico(String periodo, Class classeDinamica, Layout layout) throws Exception{
		int order = 0;
		StringBuilder label = null;
		
		
		for (int i = 0; i < agrupamento.size(); i++) {
			layout.defineField(classeDinamica.getName(),
							   (String)agrupamento.get(i), 
							   MetaField.order, order,
							   MetaField.tableViewWidth, 350);
			order++;
		}
		
		for (int i = agrupamento.size(); i < propriedadesDinamicas.size() - 1; i++) {
			PropertyDescriptor propriedade = (PropertyDescriptor)propriedadesDinamicas.get(i);
			boolean visivel = false;
			String nomePropriedade = propriedade.getName();
			String fluxo = StringUtils.contains(nomePropriedade, "Realizado") ? "Realizado:" : "Futuro:";
			if( periodo.equalsIgnoreCase("A") ){
				label = new StringBuilder(StringUtils.contains(nomePropriedade, "anoRealizado") ? 
											StringUtils.remove(nomePropriedade, "anoRealizado"):
											StringUtils.remove(nomePropriedade, "anoFuturo")) ;
	   					
				
				
			}else if ( periodo.equalsIgnoreCase("D") ){
				label = new StringBuilder(StringUtils.contains(nomePropriedade, "diarioRealizado") ?
											 StringUtils.remove(nomePropriedade, "diarioRealizado") :
										     StringUtils.remove(nomePropriedade, "diarioFuturo"));
				
				label.insert(2, "/");
				label.insert(5, "/");
				
			}else if ( periodo.equalsIgnoreCase("M") ){
				label = new StringBuilder(StringUtils.contains(nomePropriedade, "competenciaRealizado") ?
						 					StringUtils.remove(nomePropriedade, "competenciaRealizado") :
						 					StringUtils.remove(nomePropriedade, "competenciaFuturo"));
				
				label.insert(2, "/");
				
			}else if ( periodo.equalsIgnoreCase("S") ){
				label = new StringBuilder(StringUtils.contains(nomePropriedade, "semestreRealizado") ?
	 								StringUtils.remove(nomePropriedade, "semestreRealizado") :
	 								StringUtils.remove(nomePropriedade, "semestreFuturo"));
				label.insert(2, "/");
				label.insert(7, "-");
				label.insert(10, "/");
				
			}
			
			for (int j = 0; j < listObjetosDinamicos.size(); j++) {
				Object objectoDinamico = listObjetosDinamicos.get(j);
				BigDecimal valorCampo = (BigDecimal) ReflectUtil.getFieldValue(objectoDinamico, nomePropriedade);
				if(valorCampo != null){
					visivel = true;
					break;
				}
			}
			
			layout.defineField(classeDinamica.getName(),
					   nomePropriedade,
					   MetaField.label, (fluxo + label.toString()),
					   MetaField.order, order,
					   MetaField.tableViewWidth, 150,
					   MetaField.visible, visivel);
			
			order++;
		}
		
		layout.defineField(classeDinamica.getName(),
						   "total",
						   MetaField.tableViewWidth, 150,
						   MetaField.order, order);
	}
	
	private String validarParametrosDeEntrada(Date dataInicio, Date dataFim, String periodo, String agrupamento1){
		String mensagem = "";
		
		if (dataInicio == null){
			mensagem = "Data inicial nula. Preencha o campo corretamente.";
		}
		
		if (dataInicio.compareTo(dataFim) > 0){
			mensagem = "A data inicial deve ser menor que a data fim";
		}
		
		if (periodo == null | periodo.isEmpty()){
			mensagem = "Campo período está nulo. Preencha o campo corretamente.";
		}
		
		if( agrupamento1 == null | agrupamento1.isEmpty() ){
			mensagem = "Campo agrupamento1 está nulo. Preencha o campo corretamente.";
		}
		
		return mensagem;
	}
	
	private Class montarClasseBaseadoNoPeriodo(String periodo, Date inicio, Date fim) throws Exception{
		
		Class classeDinamica = null;
		
		if (periodo.equals("A")) { //Anual
			classeDinamica = retornarClasseConstruidaPeriodoAnual(inicio, fim);
		}else if (periodo.equals("D")){//Di�rio
			classeDinamica = retornarClasseConstruidaPeriodoDiario(inicio, fim);
		}else if (periodo.equals("M")){//Mensal
			classeDinamica = retornarClasseConstruidaPeriodoMensal(inicio, fim);
		}else if (periodo.equals("S")){//Semestral
			classeDinamica = retornarClasseConstruidaPeriodoSemestral(inicio, fim);
		}
		
		return classeDinamica;
	}
	
	private Class retornarClasseConstruidaPeriodoAnual(Date inicio, Date fim) throws Exception{
		int anoInicio = 0;
		int anoFim = 0;
		
		dataASerAnalisada.setTime(inicio);
		anoInicio = dataASerAnalisada.get(Calendar.YEAR);
		dataASerAnalisada.setTime(fim);
		anoFim = dataASerAnalisada.get(Calendar.YEAR);
		
		propriedadesDinamicas = new ArrayList();
		
		List<Object[]> listaRealizados = listRealizado;
		List<Object[]> listaFututo = listFuturo;
		
		for (int i = 0; i < agrupamento.size(); i++) {
			propriedadesDinamicas.add(new PropertyDescriptor((String)agrupamento.get(i), String.class));
		}
		
		for (int i = 0; i < listaRealizados.size(); i++) {
			
			Object[] baixaTitulo = listaRealizados.get(i);
			int index = baixaTitulo.length - 2;
			Date emissaoBaixaTitulo = (Date)baixaTitulo[index];
			
			if (emissaoBaixaTitulo.compareTo(dataInicioRealizado) >= 0 & emissaoBaixaTitulo.compareTo(dataFimRealizado) <= 0){
				dataASerAnalisada.setTime(emissaoBaixaTitulo);
				int anoEmissao = dataASerAnalisada.get(Calendar.YEAR);
			
				PropertyDescriptor propriedade = new PropertyDescriptor("anoRealizado"+anoEmissao, BigDecimal.class);
				
				if(contemPropriedade(propriedade)){
					continue;
				}else{
					propriedadesDinamicas.add(propriedade);
				}
			}	
		} 
			
		for (int j = 0; j < listaFututo.size(); j++) {
			Object[] titulo = listaFututo.get(j);
			
			Long codigoEntidadeTitulo = (Long) titulo[0];			
			Date emissaoTitulo = (Date)titulo[1];
			BigDecimal totalTitulo = (BigDecimal)titulo[2];
			
			
			if (emissaoTitulo.compareTo(dataInicioFuturo) >= 0 & emissaoTitulo.compareTo(dataFimFuturo) <= 0){
				dataASerAnalisada.setTime(emissaoTitulo);
				int anoEmissao = dataASerAnalisada.get(Calendar.YEAR);
			
				PropertyDescriptor propriedade = new PropertyDescriptor("anoFuturo"+anoEmissao, BigDecimal.class);
				
				if(contemPropriedade(propriedade)){
					continue;
				}else{
					propriedadesDinamicas.add(propriedade);
				}
			}
		}
		
		
		propriedadesDinamicas.add(new PropertyDescriptor("total", BigDecimal.class));
		
		/*this.titulos = futuro;
		this.tituloPorEntidade = tituloEntidade;
		
		this.realizados = realizado;
		this.realizadoPorEntidade = realizadoEntidade;
		*/
		return  DynamicClassFactory.getInstance().createDynamicClass(propriedadesDinamicas);
	}

	private Class retornarClasseConstruidaPeriodoDiario(Date inicio, Date fim) throws Exception{
		
		 propriedadesDinamicas = new ArrayList();
		
		List<Object[]> listaRealizados = listRealizado;
		List<Object[]> listaFututo = listFuturo;
		
		formatadorData.applyPattern("dd/MM/yyyy");
		
		for (int i = 0; i < agrupamento.size(); i++) {
			propriedadesDinamicas.add(new PropertyDescriptor((String)agrupamento.get(i), String.class));
		}
		
		for (int i = 0; i < listaRealizados.size(); i++) {
			
			Object[] baixaTitulo = listaRealizados.get(i);
			int index = baixaTitulo.length - 2;
			Date emissaoBaixaTitulo = (Date)baixaTitulo[index];
			
			if(emissaoBaixaTitulo.compareTo(dataInicioRealizado)>= 0 & emissaoBaixaTitulo.compareTo(dataFimRealizado) <= 0){
				String dataEmissao = formatadorData.format(emissaoBaixaTitulo);
				
				PropertyDescriptor propriedade = new PropertyDescriptor("diarioRealizado"+StringUtils.remove(dataEmissao, "/"), BigDecimal.class);
				
				if (contemPropriedade(propriedade)){
					continue;
				}else{
					propriedadesDinamicas.add(propriedade);
				}
			}
		} 
		
		for (int j = 0; j < listaFututo.size(); j++) {
			Object[] titulo =  listaFututo.get(j);
			int index = titulo.length - 2;
			Date emissaoTitulo = (Date)titulo[index];
			
			if (emissaoTitulo.compareTo(dataInicioFuturo) >= 0 & emissaoTitulo.compareTo(dataFimFuturo) <= 0){
				String dataEmissao = formatadorData.format(emissaoTitulo);
				
				PropertyDescriptor propriedade = new PropertyDescriptor("diarioFuturo"+StringUtils.remove(dataEmissao, "/"), BigDecimal.class);
				
				if (contemPropriedade(propriedade)){
					continue;
				}else{
					propriedadesDinamicas.add(propriedade);
				}
				
			}
		}
		
		propriedadesDinamicas.add(new PropertyDescriptor("total", BigDecimal.class));
		
		/*this.titulos = futuro;
		this.tituloPorEntidade = tituloEntidade;
		
		this.realizados = realizado;
		this.realizadoPorEntidade = realizadoEntidade;
		*/
		return  DynamicClassFactory.getInstance().createDynamicClass(propriedadesDinamicas);
	}

	private Class retornarClasseConstruidaPeriodoMensal(Date inicio, Date fim) throws Exception{
		this.propriedadesDinamicas = new ArrayList();
		
		List<Object[]> listaRealizados = listRealizado;
		List<Object[]> listaFututo = listFuturo;
		
		formatadorData.applyPattern("MM/yyyy");
		
		for (int i = 0; i < agrupamento.size(); i++) {
			this.propriedadesDinamicas.add(new PropertyDescriptor((String)agrupamento.get(i), String.class));
		}
		
		for (int i = 0; i < listaRealizados.size(); i++) {
			
			Object[] baixaTitulo = listaRealizados.get(i);
			int index = baixaTitulo.length - 2;
			Date emissaoBaixaTitulo = (Date)baixaTitulo[index];
		
			if( emissaoBaixaTitulo.compareTo(dataInicioRealizado) >= 0 & emissaoBaixaTitulo.compareTo(dataFimRealizado) <= 0 ){
				String competenciaEmissao = formatadorData.format(emissaoBaixaTitulo);
				
				PropertyDescriptor propriedade = new PropertyDescriptor("competenciaRealizado"+StringUtils.remove(competenciaEmissao, "/"), BigDecimal.class);
				
				if(contemPropriedade(propriedade)){
					continue;
				}else{
					propriedadesDinamicas.add(propriedade);
				}
			}
		} 

		for (int j = 0; j < listaFututo.size(); j++) {
			Object[] titulo = listaFututo.get(j);
			int index = titulo.length - 2;
			Date emissaoTitulo = (Date)titulo[index];
			
			if(emissaoTitulo.compareTo(dataInicioFuturo) >= 0 & emissaoTitulo.compareTo(dataFimFuturo) <= 0){
				String competenciaEmissao = formatadorData.format(emissaoTitulo);
				
				PropertyDescriptor propriedade = new PropertyDescriptor("competenciaFuturo"+StringUtils.remove(competenciaEmissao, "/"), BigDecimal.class);
				
				if(contemPropriedade(propriedade)){
					continue;
				}else{
					propriedadesDinamicas.add(propriedade);
				}
			}
		}
		
		
		propriedadesDinamicas.add(new PropertyDescriptor("total", BigDecimal.class));
		
		/*this.titulos = futuro;
		this.tituloPorEntidade = tituloEntidade;
		
		this.realizados = realizado;
		this.realizadoPorEntidade = realizadoEntidade;*/
		
		return  DynamicClassFactory.getInstance().createDynamicClass(propriedadesDinamicas);

	}

	private Class retornarClasseConstruidaPeriodoSemestral(Date inicio, Date fim) throws Exception{
		
		propriedadesDinamicas = new ArrayList();
		
		List<Object[]> listaRealizados = listRealizado;
		List<Object[]> listaFututo = listFuturo;
		
		
		formatadorData.applyPattern("MM/yyyy");
		
		for (int i = 0; i < agrupamento.size(); i++) {
			propriedadesDinamicas.add(new PropertyDescriptor((String)agrupamento.get(i), String.class));
		}
		
		for (int i = 0; i < listaRealizados.size(); i++) {
			
			Object[] baixaTitulo = listaRealizados.get(i);
			int index = baixaTitulo.length - 2;
			Date emissaoBaixaTitulo = (Date)baixaTitulo[index];
			
			Date inicioCompetencia = inicio;  
			Date fimCompetencia = null;
			dataASerAnalisada.setTime(inicioCompetencia);
			dataASerAnalisada.add(Calendar.MONTH, 6);
			fimCompetencia = dataASerAnalisada.getTime();
			dataASerAnalisada.setTime(fimCompetencia);
			dataASerAnalisada.add(Calendar.DAY_OF_MONTH, -1);
			fimCompetencia = dataASerAnalisada.getTime();
			
			if (fimCompetencia.compareTo(fim) > 0){
				fimCompetencia = fim;
			}
			
			if (emissaoBaixaTitulo.compareTo(inicioCompetencia) >= 0 & emissaoBaixaTitulo.compareTo(fimCompetencia) <= 0){
				String intervaloCompetencia = formatadorData.format(inicioCompetencia) + " - " +  formatadorData.format(fimCompetencia);
				
				PropertyDescriptor propriedade = new PropertyDescriptor("semestreRealizado"+StringUtils.remove("-", StringUtils.remove(intervaloCompetencia, "/")), BigDecimal.class);
				
				if(contemPropriedade(propriedade)){
					continue;
				}else{
					propriedadesDinamicas.add(propriedade);
				}
			}
		} 
		

		for (int j = 0; j < listaFututo.size(); j++) {
			
			Object[] titulo = listaFututo.get(j);
			int index = titulo.length - 2;
			Date emissaoTitulo = (Date)titulo[index];
			
			Date inicioCompetencia = inicio;  
			Date fimCompetencia = null;
			dataASerAnalisada.setTime(inicioCompetencia);
			dataASerAnalisada.add(Calendar.MONTH, 6);
			fimCompetencia = dataASerAnalisada.getTime();
			dataASerAnalisada.setTime(fimCompetencia);
			dataASerAnalisada.add(Calendar.DAY_OF_MONTH, -1);
			fimCompetencia = dataASerAnalisada.getTime();
			
			if (fimCompetencia.compareTo(fim) > 0){
				fimCompetencia = fim;
			}
			
			if (emissaoTitulo.compareTo(inicioCompetencia) >= 0 & emissaoTitulo.compareTo(fimCompetencia) <= 0){
				String intervaloCompetencia = formatadorData.format(inicioCompetencia) + " - " +  formatadorData.format(fimCompetencia);
			
				PropertyDescriptor propriedade = new PropertyDescriptor("semestreFuturo"+StringUtils.remove("-", StringUtils.remove(intervaloCompetencia, "/")), BigDecimal.class);
				
				if(contemPropriedade(propriedade)){
					continue;
				}else{
					propriedadesDinamicas.add(propriedade);
				}
			}
		}
	
		propriedadesDinamicas.add(new PropertyDescriptor("total", BigDecimal.class));
		
		/*this.titulos = futuro;
		this.tituloPorEntidade = tituloEntidade;
		
		this.realizados = realizado;
		this.realizadoPorEntidade = realizadoEntidade;
		*/
		return  DynamicClassFactory.getInstance().createDynamicClass(propriedadesDinamicas);

	}
	
	private void construirListObjetosDinamicos(Class classeDinamica) throws Exception{
		
		int qtdCompatibilidadesRealizadosFuturos = 0;
		boolean alterandoObjeto = false;
		boolean terminouProcessoConstrucao = false;
		int index = 0;
		
		if(listRealizado.isEmpty() & listFuturo.isEmpty()){
			return;
		}
		
		Object objetoDinamico = null;
		Object objectDinamico2 = null;
		while ( index < listRealizado.size() | index < listFuturo.size() ){
			
			if(index < listRealizado.size()){
				Object[] arrayRealizado = (Object[]) listRealizado.get(index);
				
				Object[] arrayRealizadoAux = new Object[3];
				
				arrayRealizadoAux[0] = arrayRealizado[1];
				arrayRealizadoAux[1] = arrayRealizado[2];
				arrayRealizadoAux[2] = arrayRealizado[3];
				
				objetoDinamico = retornarObjetoExistente(arrayRealizadoAux);
				
				if(objetoDinamico == null){
					objetoDinamico = classeDinamica.newInstance();
					
					for (int j = 0; j < agrupamento.size(); j++) {
						ReflectUtil.setFieldValue(objetoDinamico,(String)agrupamento.get(j), arrayRealizadoAux[j].toString());
					}
				}
				
				preencherPropriedadesRealizados(objetoDinamico, arrayRealizadoAux);
				preencherColunaTotal(objetoDinamico);


				if( ! listObjetosDinamicos.contains(objetoDinamico) ){
					listObjetosDinamicos.add(objetoDinamico);
				}
			}
			
			if(index < listFuturo.size()){
				boolean compativel = false;
				Object[] arrayFuturo = (Object[])listFuturo.get(index);
				
				Object[] arrayFuturoAux = new Object[3];
				
				arrayFuturoAux[0] = arrayFuturo[1];
				arrayFuturoAux[1] = arrayFuturo[2];
				arrayFuturoAux[2] = arrayFuturo[3];
				
				objectDinamico2 = retornarObjetoExistente(arrayFuturoAux);
				
				if(objetoDinamico != null){
					compativel = verificarCompatibilidadeObjectRealizadoFuturo(objetoDinamico, arrayFuturoAux);
				}
				
				if (compativel){
					preencherPropriedadesFuturos(objetoDinamico, arrayFuturoAux);
					preencherColunaTotal(objetoDinamico);
				}else{
					if(objectDinamico2 == null){
						objectDinamico2 = classeDinamica.newInstance();
						
						for (int j = 0; j < agrupamento.size(); j++) {
							ReflectUtil.setFieldValue(objectDinamico2,(String)agrupamento.get(j), arrayFuturoAux[j].toString());
						}
						
						preencherPropriedadesFuturos(objectDinamico2, arrayFuturoAux);
						preencherColunaTotal(objectDinamico2);
						
					}else{
						preencherPropriedadesFuturos(objectDinamico2, arrayFuturoAux);
						preencherColunaTotal(objectDinamico2);
					}
				}
				
				
				if( ! listObjetosDinamicos.contains(objectDinamico2)){
					listObjetosDinamicos.add(objectDinamico2);
				}
				
			}
			
			index++;
			
		}
		
	}
	
	private boolean verificarCompatibilidadeObjectRealizadoFuturo(Object objeto, Object[] arrayObject){
		int qtdTotalColunasAgrupadas = 0;
		
		for (int i = 0; i < agrupamento.size(); i++) {
			String atributoRealizado = (String) ReflectUtil.getFieldValue(objeto, (String)agrupamento.get(i));
			String atributoFuturo	 =  arrayObject[i].toString();
			
			if(atributoFuturo!= null & atributoRealizado!= null){
				if(atributoRealizado.equals(atributoFuturo)){
					qtdTotalColunasAgrupadas++;
				}
			}
		}
		
		return qtdTotalColunasAgrupadas == agrupamento.size();
	}
	
	private Object retornarObjetoExistente(Object[] arrayObjeto){
		
		if ( listObjetosDinamicos.isEmpty() ){
			return null;
		}
		
		int qtdTotalColunasAgrupadas = 0;
		Object obj = null;
		
		for (int i = 0; i < listObjetosDinamicos.size(); i++) {
			Object objetoDinamico = listObjetosDinamicos.get(i);
			
			for (int j = 0; j < agrupamento.size(); j++) {
				String propriedade = (String) agrupamento.get(j);
				if(ReflectUtil.getFieldValue(objetoDinamico, propriedade).equals(arrayObjeto[j].toString()) ){
					qtdTotalColunasAgrupadas++;
				}
			}
			
			if(qtdTotalColunasAgrupadas == agrupamento.size()){
				obj = objetoDinamico;
				break;
			}
		}
		
		return obj;
	}
	private void preencherPropriedadesRealizados(Object objetoDinamico, Object[] arrayRealizado)throws Exception{
		for(int  i = agrupamento.size(); i < propriedadesDinamicas.size() - 1; i++){
			String propriedade = ((PropertyDescriptor)propriedadesDinamicas.get(i)).getName();
			
			if(StringUtils.contains(propriedade, "Futuro")){
				continue;
			}
			
			Date dataEmissao = (Date) arrayRealizado[agrupamento.size()]; 
			BigDecimal valorAtributo = (BigDecimal)ReflectUtil.getFieldValue(objetoDinamico, propriedade);
			
			if (periodo.equals("A")){
				dataASerAnalisada.setTime(dataEmissao);
				String anoEmissao = String.valueOf(dataASerAnalisada.get(Calendar.YEAR));
				
				if(anoEmissao.equalsIgnoreCase(StringUtils.remove(propriedade, "anoRealizado"))){
					if(valorAtributo == null){
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, (BigDecimal)arrayRealizado[arrayRealizado.length - 1]);
					}else{
						BigDecimal somaAux = (BigDecimal)arrayRealizado[arrayRealizado.length - 1];
						BigDecimal somaTotal = valorAtributo.add(somaAux);
						//somaTotal = somaTotal.add(somaAux);
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, somaTotal);
					}
				}
			}else if (periodo.equals("D")){
				formatadorData.applyPattern("dd/MM/yyyy");
				String dataEmissaoRealizado = formatadorData.format(dataEmissao);
				StringBuilder dataEmissaoRealizado2 = new StringBuilder(StringUtils.remove(propriedade, "diarioRealizado"));
				dataEmissaoRealizado2.insert(2, "/");
				dataEmissaoRealizado2.insert(5, "/");
				if(dataEmissaoRealizado.endsWith(dataEmissaoRealizado2.toString())){
					if(valorAtributo == null){
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, (BigDecimal)arrayRealizado[arrayRealizado.length - 1]);
					}else{
						BigDecimal somaAux = (BigDecimal)arrayRealizado[arrayRealizado.length - 1];
						BigDecimal somaTotal = valorAtributo.add(somaAux);
						//somaTotal = somaTotal.add(somaAux);
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, somaTotal);
					}
				}
			}else if (periodo.equals("M")){
				formatadorData.applyPattern("MM/yyyy");
				String competencia = formatadorData.format(dataEmissao);
				StringBuilder competencia2 = new StringBuilder(StringUtils.remove(propriedade, "competenciaRealizado"));
				competencia2.insert(2, "/");
				
				if(competencia.equalsIgnoreCase(competencia2.toString())){
					if(valorAtributo == null){
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, (BigDecimal)arrayRealizado[arrayRealizado.length - 1]);
					}else{
						BigDecimal somaAux = (BigDecimal)arrayRealizado[arrayRealizado.length - 1];
						BigDecimal somaTotal = valorAtributo.add(somaAux);
						//somaTotal = somaTotal.add(somaAux);
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, somaTotal);
					}
				}
			}else if (periodo.equals("S")){
				StringBuilder intervaloCompetencia = new StringBuilder(StringUtils.remove(propriedade, "semestreRealizado"));
				formatadorData.applyPattern("MM/yyyy");
				intervaloCompetencia.insert(2, "/");
				intervaloCompetencia.insert(7, "-");
				intervaloCompetencia.insert(10, "/");
				
				String[] arrayCompetencias = StringUtils.split(intervaloCompetencia.toString(), "-");
				
				Date competenciaInicial = formatadorData.parse(arrayCompetencias[0]);
				Date competenciaFim		= formatadorData.parse(arrayCompetencias[1]);
				
				if(dataEmissao.compareTo(competenciaInicial) >= 0 & dataEmissao.compareTo(competenciaFim) <=0 ){
					if(valorAtributo == null){
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, (BigDecimal)arrayRealizado[arrayRealizado.length - 1]);
					}else{
						BigDecimal somaAux = (BigDecimal)arrayRealizado[arrayRealizado.length - 1];
						BigDecimal somaTotal = valorAtributo.add(somaAux);
						//somaTotal = somaTotal.add(somaAux);
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, somaTotal);
					}
				}
			}
		}
	}
	private void preencherColunaTotal(Object objetoDinamico){
		
		BigDecimal total = new BigDecimal(0);
		BigDecimal somaAux = null;
		for(int i = agrupamento.size(); i < propriedadesDinamicas.size()-1; i++){
			String propriedade = ((PropertyDescriptor) propriedadesDinamicas.get(i)).getName();
			somaAux = (BigDecimal) ReflectUtil.getFieldValue(objetoDinamico, propriedade);
			if(somaAux != null){
				total = total.add(somaAux);
			}
						
		}
		if(objetoDinamico != null){
			ReflectUtil.setFieldValue(objetoDinamico,"total", total);
		}
	}
	private void preencherPropriedadesFuturos(Object objetoDinamico, Object[] arrayFuturo)throws Exception{
		for(int  i = agrupamento.size(); i < propriedadesDinamicas.size() - 1; i++){
			String propriedade = ((PropertyDescriptor)propriedadesDinamicas.get(i)).getName();
			
			if(StringUtils.contains(propriedade, "Realizado")){
				continue;
			}
			
			Date dataEmissao = (Date) arrayFuturo[agrupamento.size()]; 
			BigDecimal valorAtributo = (BigDecimal)ReflectUtil.getFieldValue(objetoDinamico, propriedade);
			
			if (periodo.equals("A")){
				dataASerAnalisada.setTime(dataEmissao);
				String anoEmissao = String.valueOf(dataASerAnalisada.get(Calendar.YEAR));
				
				if(anoEmissao.equalsIgnoreCase(StringUtils.remove(propriedade, "anoFuturo"))){
					if(valorAtributo == null){
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, (BigDecimal)arrayFuturo[arrayFuturo.length - 1]);
					}else{
						BigDecimal somaAux = (BigDecimal)arrayFuturo[arrayFuturo.length - 1];
						BigDecimal somaTotal = valorAtributo.add(somaAux);
						//somaTotal = somaTotal.add(somaAux);
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, somaTotal);
					}
				}
			}else if (periodo.equals("D")){
				formatadorData.applyPattern("dd/MM/yyyy");
				String dataEmissaoFuturo = formatadorData.format(dataEmissao);
				StringBuilder dataEmissaoFuturo2 = new StringBuilder(StringUtils.remove(propriedade, "diarioFuturo"));
				dataEmissaoFuturo2.insert(2, "/");
				dataEmissaoFuturo2.insert(5, "/");
				if(dataEmissaoFuturo.equals(dataEmissaoFuturo2.toString())){
					if(valorAtributo == null){
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, (BigDecimal)arrayFuturo[arrayFuturo.length - 1]);
					}else{
						BigDecimal somaAux = (BigDecimal)arrayFuturo[arrayFuturo.length - 1];
						BigDecimal somaTotal = valorAtributo.add(somaAux);
						//somaTotal = somaTotal.add(somaAux);
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, somaTotal);
					}
				}
			}else if (periodo.equals("M")){
				formatadorData.applyPattern("MM/yyyy");
				String competencia = formatadorData.format(dataEmissao);
				StringBuilder competencia2 = new StringBuilder(StringUtils.remove(propriedade, "competenciaFuturo"));
				competencia2.insert(2, "/");
				
				if(competencia.equalsIgnoreCase(competencia2.toString())){
					if(valorAtributo == null){
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, (BigDecimal)arrayFuturo[arrayFuturo.length - 1]);
					}else{
						BigDecimal somaAux = (BigDecimal)arrayFuturo[arrayFuturo.length - 1];
						BigDecimal somaTotal = valorAtributo.add(somaAux);
						//somaTotal = somaTotal.add(somaAux);
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, somaTotal);
					}
				}
			}else if (periodo.equals("S")){
				StringBuilder intervaloCompetencia = new StringBuilder(StringUtils.remove(propriedade, "semestreRealizado"));
				formatadorData.applyPattern("MM/yyyy");
				intervaloCompetencia.insert(2, "/");
				intervaloCompetencia.insert(7, "-");
				intervaloCompetencia.insert(10, "/");
				
				String[] arrayCompetencias = StringUtils.split(intervaloCompetencia.toString(), "-");
				
				Date competenciaInicial = formatadorData.parse(arrayCompetencias[0]);
				Date competenciaFim		= formatadorData.parse(arrayCompetencias[1]);
				
				if(dataEmissao.compareTo(competenciaInicial) >= 0 & dataEmissao.compareTo(competenciaFim) <=0 ){
					if(valorAtributo == null){
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, (BigDecimal)arrayFuturo[arrayFuturo.length - 1]);
					}else{
						BigDecimal somaAux = (BigDecimal)arrayFuturo[arrayFuturo.length - 1];
						BigDecimal somaTotal = valorAtributo.add(somaAux);
						//somaTotal = somaTotal.add(somaAux);
						ReflectUtil.setFieldValue(objetoDinamico, propriedade, somaTotal);
					}
				}
			}
		}
	}
	
	public boolean contemPropriedade(PropertyDescriptor propriedade){
		boolean contem = false;
		
		for (int i = 0; i < propriedadesDinamicas.size(); i++) {
			PropertyDescriptor prop = (PropertyDescriptor)propriedadesDinamicas.get(i);
			
			if (prop.getName().equalsIgnoreCase(propriedade.getName())){
				contem = true;
				break;
			}
		}
		
		return contem;
	}
}
