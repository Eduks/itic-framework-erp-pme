package br.com.dyad.backoffice.navigation.relatorio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.auxiliares.InventarioFisicoEstoqueReportBean;
import br.com.dyad.backoffice.entidade.cadastro.TipoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemOperacaoAbstrato;
import br.com.dyad.businessinfrastructure.entidades.tabela.Material;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.DyadEvents;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.ReportWindow;
import br.com.dyad.infrastructure.widget.WidgetListener;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.grid.Layout;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class InventarioFisicoEstoqueReport extends ReportWindow {
	
	private DataList inventarioDataList;
	
	private Material recurso;
	private Date dataInicial;
	private Date dataFinal;
	
	private Layout layoutInventario;
	
	private ArrayList<Object> parametrosConsulta;
	
	private final Long ID_TIPO_ENTRADA = -99999899999144L;
	private final Long ID_TIPO_SAIDA = -99999899999143L;
	private final Long ID_TIPO_CONSUMO = -89999999999294L;
	
	public InventarioFisicoEstoqueReport(HttpSession httpSession) {
		super(httpSession);
		
		inventarioDataList = DataListFactory.newDataList(this.getDatabase());
		inventarioDataList.setCommitOnSave(false);
		inventarioDataList.setLogChanges(false);
	}
	
	@Override
	public void defineShowLayout(Interaction interaction) throws Exception {
		if ( this.layoutInventario != null ){
			interaction.remove( this.layoutInventario );
		}
		
		layoutInventario = new Layout(this, "inventarioLayout", inventarioDataList, InventarioFisicoEstoqueReportBean.class.getName());
		
		layoutInventario.setTitle("Inventário Físico de Estoque");
		layoutInventario.setHeaderMenu(false);
		
		interaction.add(layoutInventario);
		
	}
	@Override
	public void defineVars(VariableGrid grid) throws Exception {
		vars.setTitle("Variáveis");
		
		int order = 0;
		
		FieldLookup fldMaterial = new FieldLookup(grid);
		FieldSimpleDate fldDataInicial = new FieldSimpleDate(grid);
		FieldSimpleDate fldDataFinal = new FieldSimpleDate(grid);
		
		fldMaterial.setBeanName(Recurso.class.getName());
		fldMaterial.setLabel("Recurso");
		fldMaterial.setName("material");
		fldMaterial.setWidth(300);
		fldMaterial.setOrder(order++);
		fldMaterial.setFilterExpress(" AND classid = '"+(-89999999999291L)+"'"); //Material
		
		fldDataInicial.addWidgetListener(DyadEvents.onAfterChange, new WidgetListener() {
			
			@Override
			public void handleEvent(Object sender) throws Exception {
				
				VariableGrid vGrid = (VariableGrid)((FieldSimpleDate)sender).getGrid();

				Date emissaoInicial = ((FieldSimpleDate)sender).getValue();
				
				FieldSimpleDate fldEmissaoFinal = (FieldSimpleDate) vGrid.getFieldByName("fldDataFinal");

				if (emissaoInicial != null && fldEmissaoFinal.getValue() != null
						&& emissaoInicial.after(fldEmissaoFinal.getValue())) {
					SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
					throw new AppException("A data de Emissão Inicial '"
							+ dataFormat.format(emissaoInicial)
							+ "' deve ser menor ou igual a Data de Emissão Final'"
							+ dataFormat.format(fldEmissaoFinal.getValue()) + "'.");
				}
			}
		});
		
		fldDataFinal.addWidgetListener(DyadEvents.onAfterChange, new WidgetListener() {
			
			@Override
			public void handleEvent(Object sender) throws Exception {
				
				VariableGrid vGrid = (VariableGrid)((FieldSimpleDate)sender).getGrid();

				Date emissaoFinal = ((FieldSimpleDate)sender).getValue();
				
				FieldSimpleDate fldEmissaoInicial = (FieldSimpleDate) vGrid.getFieldByName("fldDataInicial");

				if (emissaoFinal != null && fldEmissaoInicial.getValue() != null
						&& emissaoFinal.before(fldEmissaoInicial.getValue())) {
					SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
					throw new AppException("A data de Emissão Inicial '"
							+ dataFormat.format(emissaoFinal)
							+ "' deve ser menor ou igual a Data de Emissão Final'"
							+ dataFormat.format(fldEmissaoInicial.getValue()) + "'.");
				}
			}
		});
		
		fldDataInicial.setLabel("Data Inicial");
		fldDataInicial.setName("fldDataInicial");
		fldDataInicial.setRequired(true);
		fldDataInicial.setWidth(150);
		fldDataInicial.setOrder(order++);

		fldDataFinal.setLabel("Data Final");
		fldDataFinal.setName("fldDataFinal");		
		fldDataFinal.setRequired(true);
		fldDataFinal.setWidth(150);
		fldDataFinal.setOrder(order++);
	}
	@Override
	public void prepareLayout() throws Exception {
		
		this.setMaterial((Material) vars.getFieldByName("material").getValue());
		this.setDataInicial((Date)vars.getFieldByName("fldDataInicial").getValue());
		this.setDataFinal((Date)vars.getFieldByName("fldDataFinal").getValue());
		
		this.inventarioDataList.getList().clear();
		
		this.inventarioDataList = retornaInventario();
		
		if (this.inventarioDataList.isEmpty()){
			throw new AppException("Não foram encontrados registros com as restrições informadas.");			
		}
	}
	
	public DataList carregaInventario() throws Exception {
		
		ArrayList<ItemOperacaoAbstrato> listaItens = new ArrayList<ItemOperacaoAbstrato>(0);
		
		DataList inventarioDataList = DataListFactory.newDataList(getDatabase());
		
		String hquery = "from " + ItemBaixaPedido.class.getName() + " item ";

		ArrayList<String> where = this.filtroInventario();

		if ( !where.isEmpty()){
			hquery += " where " + StringUtils.join(where, " and ");
		}
		
		listaItens.addAll(PersistenceUtil.executeHql((Session)inventarioDataList .getTransactionalSession().getSession(), hquery, parametrosConsulta));
		
		hquery = "from " + ItemBaixaAutomatica.class.getName() + " item ";

		where = this.filtroInventario();

		if ( !where.isEmpty()){
			hquery += " where " + StringUtils.join(where, " and ");
		}
		
		listaItens.addAll(PersistenceUtil.executeHql((Session)inventarioDataList .getTransactionalSession().getSession(), hquery, parametrosConsulta));
		
		inventarioDataList.setList(listaItens);
		
		return inventarioDataList;
	}
	
	private ArrayList<String> filtroInventario(){
		ArrayList<String> where = new ArrayList<String>();
		
		parametrosConsulta = new ArrayList<Object>();
		
		String filtro = "";
		
		if (getMaterial() != null) {
			filtro += " item.recurso.id = ? ";
			
			parametrosConsulta.add(getMaterial().getId());
		} else {
			filtro += " item.recurso.classId = '"+(-89999999999291L)+"'"; //Material
		}
		
		where.add(filtro);
		
		if (this.getDataInicial() != null){
			where.add(" item.emissao >= ? ");
			parametrosConsulta.add(getDataInicial());
		}
		
		if (this.getDataFinal() != null){
			where.add(" item.emissao <= ? ");
			parametrosConsulta.add(getDataFinal());
		}
		
		return where;
	}
	
	public DataList retornaInventario() throws Exception {
		List<ItemOperacaoAbstrato> items = carregaInventario().getList();
		
		List<InventarioFisicoEstoqueReportBean> listaInventario = new ArrayList<InventarioFisicoEstoqueReportBean>(0);
		
		InventarioFisicoEstoqueReportBean inventarioAtual = null;
		
		long qtdeEntrada = 0, qtdeConsumo = 0, qtde = 0;
		
		TipoPedido tipoPedido = null;
		
		for (ItemOperacaoAbstrato item : items) {
			inventarioAtual = new InventarioFisicoEstoqueReportBean();
			
			if (item instanceof ItemBaixaPedido) {
				inventarioAtual.setRecurso(((ItemBaixaPedido)item).getItemPedidoBaixado().getRecurso().getNome());
			} else if (item instanceof ItemBaixaAutomatica) {
				inventarioAtual.setRecurso(((ItemBaixaAutomatica)item).getRecurso().getNome());
			}
			
			if (item instanceof ItemBaixaPedido) {
				tipoPedido = getPai(((ItemBaixaPedido)item).getItemPedidoBaixado().getTipoPedido());
			} else if (item instanceof ItemBaixaAutomatica) {
				tipoPedido = getPai(((ItemBaixaAutomatica)item).getTipoPedido());
			}
			
			if (tipoPedido != null) {
				
				if (item instanceof ItemBaixaPedido) {
					qtde = ((ItemBaixaPedido)item).getQuantidade();
				} else if (item instanceof ItemBaixaAutomatica) {
					qtde = ((ItemBaixaAutomatica)item).getQuantidade();
				}
				
				if (isEntrada(tipoPedido)) {
					qtdeEntrada = qtde;
				} else if (isSaida(tipoPedido) || isConsumo(tipoPedido)) {
					qtdeConsumo = qtde;
				}
				
				inventarioAtual.setQtdeEntrada(qtdeEntrada);
				inventarioAtual.setQtdeConsumo(qtdeConsumo);
				
				qtdeEntrada = 0;
				qtdeConsumo = 0;
				
				listaInventario.add(inventarioAtual);
			}
			
		}
		
		DataList.sort(listaInventario, "recurso");
		
		for (int i = 1; i <= listaInventario.size(); i++) {
			
			if (i == listaInventario.size() || !listaInventario.get(i).equals(listaInventario.get(i-1))) {
				listaInventario.get(i-1).addQtdeEntrada(qtdeEntrada);
				listaInventario.get(i-1).addQtdeConsumo(qtdeConsumo);
				
				listaInventario.get(i-1).calculaSaldo();
				
				qtdeEntrada = 0;
				qtdeConsumo = 0;
			} else {
				
				inventarioAtual = listaInventario.remove(i);
				
				qtdeEntrada += inventarioAtual.getQtdeEntrada().longValue();
				
				qtdeConsumo += inventarioAtual.getQtdeConsumo().longValue();
				
				i--;
			}
			
		}
		
		DataList dataListAux = DataListFactory.newDataList(getDatabase());
		dataListAux.setList(listaInventario);
		
		return dataListAux;
	}
	
	public TipoPedido getPai(TipoPedido tipoPedido) {
		
		if (tipoPedido.getTipoPedido() == null) {
			return tipoPedido;
		} else {
			return getPai(tipoPedido.getTipoPedido());
		}
		
	}

	public Material getMaterial() {
		return recurso;
	}

	public void setMaterial(Material recurso) {
		this.recurso = recurso;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public boolean isEntrada(TipoPedido tipoPedido){
		
		if (tipoPedido.getId().longValue() == ID_TIPO_ENTRADA.longValue()) {
			return true;
		} else if (tipoPedido.getTipoPedido() != null){
			return isEntrada(tipoPedido.getTipoPedido());
		}
		
		return false;
	}
	
	public boolean isSaida(TipoPedido tipoPedido){
		
		if (tipoPedido.getId().longValue() == ID_TIPO_SAIDA.longValue()) {
			return true;
		} else if (tipoPedido.getTipoPedido() != null){
			return isSaida(tipoPedido.getTipoPedido());
		}
		
		return false;
	}
	
	public boolean isConsumo(TipoPedido tipoPedido){
		
		if (tipoPedido.getId().longValue() == ID_TIPO_CONSUMO.longValue()) {
			return true;
		} else if (tipoPedido.getTipoPedido() != null){
			return isSaida(tipoPedido.getTipoPedido());
		}
		
		return false;
	}
	
	public Comparator createComparator(){
		Comparator comp = new Comparator(){
			@Override
			public int compare(Object o1, Object o2) {
				ArrayList<String> reg1 = (ArrayList<String>)o1;
				ArrayList<String> reg2 = (ArrayList<String>)o2;
				
				if (reg1.get(1).equals("Entrada") || reg1.get(1).equals("Saída")) {
					return -1;
				} else {
					return reg1.get(1).compareToIgnoreCase(reg2.get(1));
				}
			}			
		};
		return comp;
	}
	
}
