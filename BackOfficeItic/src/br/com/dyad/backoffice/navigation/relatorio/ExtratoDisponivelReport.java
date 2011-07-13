package br.com.dyad.backoffice.navigation.relatorio;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.auxiliares.ExtratoDisponivelAux;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.ReportWindow;
import br.com.dyad.infrastructure.widget.field.FieldBoolean;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.grid.Layout;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class ExtratoDisponivelReport extends ReportWindow {
	
	private DataList dataListExtratoDisponivel;
	private Layout layout;

	public ExtratoDisponivelReport (HttpSession httpSession) {
		super(httpSession);
		
		this.dataListExtratoDisponivel = DataListFactory.newDataList(this.getDatabase());
	}

	@Override
	public void defineVars(VariableGrid grid) throws Exception {
		
		vars.setTitle("Variáveis");
		
		FieldLookup fieldEntidade = new FieldLookup(grid);
		fieldEntidade.setName("entidade");
		fieldEntidade.setLabel("Entidade");
		fieldEntidade.setBeanName(Entidade.class.getName());
		
		FieldSimpleDate fieldEmissaoInicial = new FieldSimpleDate(grid);
		fieldEmissaoInicial.setName("emissaoInicial");
		fieldEmissaoInicial.setLabel("Data de Emissão - Inicial");
		
		FieldSimpleDate fieldEmissaoFinal = new FieldSimpleDate(grid);
		fieldEmissaoFinal.setName("emissaoFinal");
		fieldEmissaoFinal.setLabel("Data de Emissão - Final");
		
		FieldBoolean fieldRelacionadoTitulos = new FieldBoolean(grid);
		fieldRelacionadoTitulos.setName("relacionadoTitulos");
		fieldRelacionadoTitulos.setLabel("Relacionado a Títulos");
		fieldRelacionadoTitulos.setValue(true);
		
		FieldBoolean fieldRelacionadoTransferencias = new FieldBoolean(grid);
		fieldRelacionadoTransferencias.setName("relacionadoTransferencias");
		fieldRelacionadoTransferencias.setLabel("Relacionado a Transferências");
		fieldRelacionadoTransferencias.setValue(true);
	}

	@Override
	public void prepareLayout() throws Exception {
		
		if (vars.getFieldByName("entidade").getValue() == null && vars.getFieldByName("emissaoInicial").getValue() == null
				&& vars.getFieldByName("emissaoFinal").getValue() == null)
			throw new AppException("É necessário informar alguma das três variáveis (Entidade, Data de Emissão - Inicial ou Data de Emissão - Final)!");
		
		this.dataListExtratoDisponivel.empty();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		
		//SALDO INICIAL
		Session session = PersistenceUtil.getSession(this.getDatabase());
		
		ArrayList<String> where = new ArrayList<String>(0);
		
		if (vars.getFieldByName("entidade").getValue() != null)
			where.add(" entidade.id = " + ((Entidade)this.vars.getFieldByName("entidade").getValue()).getId());
		
		if (vars.getFieldByName("emissaoInicial").getValue() != null)
			where.add(" emissao >= '"+simpleDateFormat.format((Date)vars.getFieldByName("emissaoInicial").getValue())+"'");
		
		if (vars.getFieldByName("emissaoFinal").getValue() != null)
			where.add(" emissao <= '"+simpleDateFormat.format((Date)vars.getFieldByName("emissaoFinal").getValue())+"'");
		
//		String sqlSumBaixaTitulo = "Select Sum(total) as Soma from " + ItemBaixaTitulo.class.getName() + 
//		" where entidade.id = " + ((Entidade)this.vars.getFieldByName("entidade").getValue()).getId() + 
//		" and emissao < '" + simpleDateFormat.format((Date)this.vars.getFieldByName("emissaoInicial").getValue()) + "'";
		
//		String sqlSumTransferenciaDisponivel = "Select Sum(total) as soma from " + ItemTransferenciaDisponivel.class.getName() + " itemTransferenciaDisponivel " +
//		" where entidade.id = " + ((Entidade)this.vars.getFieldByName("entidade").getValue()).getId() + 
//		" and emissao < '" + simpleDateFormat.format((Date)this.vars.getFieldByName("emissaoInicial").getValue()) + "'";
		
		ArrayList<ItemBaixaTitulo> listBaixasTitulos = new ArrayList<ItemBaixaTitulo>(0);
		ArrayList<ItemTransferenciaDisponivel> listTransferenciaDisponivels = new ArrayList<ItemTransferenciaDisponivel>(0);
		BigDecimal saldoInicialBaixaTitulo = new BigDecimal(0);
		BigDecimal saldoInicialTransfenciaDisponivel = new BigDecimal(0);
		
		if (Boolean.parseBoolean(vars.getFieldByName("relacionadoTitulos").getValue().toString())){
			String sqlSumBaixaTitulo = "Select Sum(total) as Soma from " + ItemBaixaTitulo.class.getName();
			sqlSumBaixaTitulo += " where " + StringUtils.join(where, " and ");
			
			Query querySaldoInicialBaixaTitulo = session.createQuery( sqlSumBaixaTitulo );
			
			saldoInicialBaixaTitulo = (BigDecimal)querySaldoInicialBaixaTitulo.uniqueResult();
			saldoInicialBaixaTitulo = saldoInicialBaixaTitulo == null ? new BigDecimal(0) : saldoInicialBaixaTitulo;
			
			String sqlBaixaTitulo = "from " + ItemBaixaTitulo.class.getName();
			sqlBaixaTitulo += " where " + StringUtils.join(where, " and ");
			
			DataList dataListBaixasTitulos = DataListFactory.newDataList(this.getDatabase());
			dataListBaixasTitulos.executeQuery(sqlBaixaTitulo);
			
			listBaixasTitulos = new ArrayList<ItemBaixaTitulo>(dataListBaixasTitulos.getList());
		}
		
		if (Boolean.parseBoolean(vars.getFieldByName("relacionadoTransferencias").getValue().toString())){
			String sqlSumTransferenciaDisponivel = "Select Sum(total) as soma from " + ItemTransferenciaDisponivel.class.getName();
			sqlSumTransferenciaDisponivel += " where " + StringUtils.join(where, " and ");
			
			Query querySaldoInicialTransferenciaDisponivel = session.createQuery( sqlSumTransferenciaDisponivel );
					
			saldoInicialTransfenciaDisponivel = (BigDecimal)querySaldoInicialTransferenciaDisponivel.uniqueResult();
			saldoInicialTransfenciaDisponivel = saldoInicialTransfenciaDisponivel == null ? new BigDecimal(0) : saldoInicialTransfenciaDisponivel;
			
			String sqlTransferenciaDisponivel = "from " + ItemTransferenciaDisponivel.class.getName();
			sqlTransferenciaDisponivel += " where " + StringUtils.join(where, " and ");
			
			DataList dataListTransferenciaDisponivel = DataListFactory.newDataList(this.getDatabase());
			dataListTransferenciaDisponivel.executeQuery(sqlTransferenciaDisponivel);
			
			listTransferenciaDisponivels = new ArrayList<ItemTransferenciaDisponivel>(dataListTransferenciaDisponivel.getList());
		}
		
		BigDecimal saldoInicial = saldoInicialBaixaTitulo.add(saldoInicialTransfenciaDisponivel); 

		//BAIXAS DE TITULOS
//		String sqlBaixaTitulo = "from " + ItemBaixaTitulo.class.getName() + 
//		" where entidade.id = " + ((Entidade)this.vars.getFieldByName("entidade").getValue()).getId() + 
//		" and emissao >= '" + simpleDateFormat.format((Date)this.vars.getFieldByName("emissaoInicial").getValue()) + "'" + 
//		" and emissao <= '" + simpleDateFormat.format((Date)this.vars.getFieldByName("emissaoFinal").getValue()) + "'";
		
		//TRANSFERENCIAS ENTRE DISPONIVEIS
//		String sqlTransferenciaDisponivel = "from " + ItemTransferenciaDisponivel.class.getName() + 
//		" where entidade.id = " + ((Entidade)this.vars.getFieldByName("entidade").getValue()).getId() + 
//		" and emissao >= '" + simpleDateFormat.format((Date)this.vars.getFieldByName("emissaoInicial").getValue()) + "'" + 
//		" and emissao <= '" + simpleDateFormat.format((Date)this.vars.getFieldByName("emissaoFinal").getValue()) + "'";

		
		//DataList que armazenar� temporariamente os beans ExtratoDisponivelAux 
		DataList dataListTemp = DataListFactory.newDataList(this.getDatabase()); 
			
		//Listando BAIXAS DE T�TULOS
		for (ItemBaixaTitulo itemBaixaTitulo : listBaixasTitulos) {
			ExtratoDisponivelAux extratoDisponivelAux = new ExtratoDisponivelAux();
			
			extratoDisponivelAux.setEntidade(itemBaixaTitulo.getEntidade());
			extratoDisponivelAux.setEmissao(itemBaixaTitulo.getEmissao());
			
			if (itemBaixaTitulo.getTotal().signum() == -1) {
				extratoDisponivelAux.setDebito(itemBaixaTitulo.getTotal());
			}  else if (itemBaixaTitulo.getTotal().signum() == 1) {
				extratoDisponivelAux.setCredito(itemBaixaTitulo.getTotal());
			}

			dataListTemp.add(extratoDisponivelAux);
		}

		//Listando TRANSFERENCIAS DE DISPONIVEIS
		for (ItemTransferenciaDisponivel itemTransferenciaDisponivel : listTransferenciaDisponivels) {
			ExtratoDisponivelAux extratoDisponivelAux = new ExtratoDisponivelAux();
			
			extratoDisponivelAux.setEntidade(itemTransferenciaDisponivel.getEntidade());
			extratoDisponivelAux.setEmissao(itemTransferenciaDisponivel.getEmissao());
			
			if (itemTransferenciaDisponivel.getTotal().signum() == -1) {
				extratoDisponivelAux.setDebito(itemTransferenciaDisponivel.getTotal());
			}  else if (itemTransferenciaDisponivel.getTotal().signum() == 1) {
				extratoDisponivelAux.setCredito(itemTransferenciaDisponivel.getTotal());
			}
			
			dataListTemp.add(extratoDisponivelAux);
		}
		
		dataListTemp.sort("emissao");
		
		ArrayList<ExtratoDisponivelAux> listTemp = new ArrayList<ExtratoDisponivelAux>(dataListTemp.getList());
		
		//Criando Linha de SALDO INICIAL
		ExtratoDisponivelAux extratoDisponivelAuxSaldoInicial = new ExtratoDisponivelAux();
		extratoDisponivelAuxSaldoInicial.setId(1L);
        extratoDisponivelAuxSaldoInicial.setSaldo( saldoInicial );
		extratoDisponivelAuxSaldoInicial.setHistorico("Saldo Inicial");

		this.dataListExtratoDisponivel.add(extratoDisponivelAuxSaldoInicial);
		
		long cont = 2;
		
		for (ExtratoDisponivelAux extratoDisponivelAux : listTemp) {
			ExtratoDisponivelAux extratoFinal = new ExtratoDisponivelAux();
			extratoFinal.setId(cont++);
			extratoFinal.setEntidade(extratoDisponivelAux.getEntidade());
			extratoFinal.setEmissao(extratoDisponivelAux.getEmissao());
			
			if (extratoDisponivelAux.getDebito() != null) {
				extratoFinal.setDebito(extratoDisponivelAux.getDebito());
				saldoInicial = saldoInicial.add(extratoDisponivelAux.getDebito());
			}
			
			if (extratoDisponivelAux.getCredito() != null) {
				extratoFinal.setCredito(extratoDisponivelAux.getCredito());
				saldoInicial = saldoInicial.add(extratoDisponivelAux.getCredito());
			}

			extratoFinal.setSaldo(saldoInicial);
			
			this.dataListExtratoDisponivel.add(extratoFinal);
		}
	}

	@Override
	public void defineShowLayout(Interaction interaction) throws Exception {
		
		if ( this.layout != null ){
			interaction.remove( this.layout );
		}
		
		this.layout = new Layout(this, "logLayout", this.dataListExtratoDisponivel, ExtratoDisponivelAux.class.getName());
		this.layout.setTitle("Extrato de Disponíveis");
		
		interaction.add(this.layout);
	}
}
