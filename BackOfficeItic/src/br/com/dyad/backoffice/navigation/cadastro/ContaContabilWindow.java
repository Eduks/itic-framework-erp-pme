package br.com.dyad.backoffice.navigation.cadastro;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTempEntity;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.entity.AbstractEntity;
import br.com.dyad.infrastructure.entity.BaseEntity;
import br.com.dyad.infrastructure.navigation.persistence.HibernateUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class ContaContabilWindow extends Window {
	
	private GridPlanoConta gridVariaveisPlanoConta;
	protected String beanName = ContaContabil.class.getName();
	
	protected Object valueToFind;
	
	protected String indexFields;
	protected Long idToFind;
	protected String columnToFind;
	protected PlanoConta planoConta;	

	protected ArrayList valuesToFilterQuery;
	protected ArrayList<String> columnsToFilterQuery;
	
	protected Integer gridViewMode = DataGrid.VIEW_MODE_TABLEVIEW;
	
	public DataList dataList;
	public DataGrid grid = null;
	public InteractionShowData showData;

	public ContaContabilWindow (HttpSession httpSession) {
		super(httpSession);
		dataList = DataListFactory.newDataList(getDatabase());
	}
	
	public Action actionLocalizar = new Action(this, "Contas Contábeis"){		
		@Override
		public void onClick() throws Exception {
			ContaContabilWindow process = (ContaContabilWindow)getParent();
			process.setPlanoConta( (PlanoConta) process.gridVariaveisPlanoConta.fldPlanoConta.getValue());
			process.setNextInteraction(edicaoConta);
		}
	};
	
	
	
	public void setDataToDataList( Class clazz ) throws Exception{
		if (ReflectUtil.inheritsFrom(clazz, AppTempEntity.class)){
			dataList.setCommitOnSave(false);
		} else if (dataList == null || dataList.getList() == null || dataList.getList().size() == 0) {			
			this.executeQuery();
			dataList.setCommitOnSave(true);
		}
	}
	
	public void executeQuery() throws Exception{
		String query = "from " + this.getBeanName(); 
		String discriminator = AbstractEntity.getClassIdentifier( Class.forName(this.getBeanName()) );
		if ( ( discriminator != null && ! discriminator.equals("") ) || this.valuesToFilterQuery != null ){
			query += " where ";
			boolean usarAnd = false;
			if ( discriminator != null && ! discriminator.equals("") ){
				String childrenString = HibernateUtil.getInstance(getDatabase()).getChildrenString(Class.forName(this.getBeanName()));
				query += " classId in (" + childrenString + ") ";
				usarAnd = true;
			}		
			
			if ( this.valuesToFilterQuery != null ){
				for (int i = 0; i < this.valuesToFilterQuery.size(); i++) {
					Object value = this.valuesToFilterQuery.get(i);
					Object columnName = this.columnsToFilterQuery.get(i);								
					if ( usarAnd ){
						query += " and ";
					}
					
					if ( value instanceof String ){
						value = "'" + value + "'"; 
					} //TODO faltando implementar o tratamento para os demais tipos.
					
					query += columnName + " = " + value;
					
					usarAnd = true;
				}
			}
			
		}		
		
		query += " and planoConta.id = " + this.getPlanoConta().getId();
		
		query += " order by id";
		
		dataList.executeQuery( query );		
	}
	
	/**
	 * Interação que é exibida quando o usuário entra no processo.
	 */
	Interaction variaveis = new Interaction(this, "variaveis"){
		@Override
		public void defineInteraction() throws Exception {
			this.enableAndShowActions( actionLocalizar );
			add(gridVariaveisPlanoConta);
		}
		
	};
	
	public Interaction edicaoConta = new Interaction(this, "edicaoConta"){
		@Override
		public void defineInteraction() throws Exception {
			
			ContaContabilWindow process = (ContaContabilWindow)getParent();
			
			Class clazz = null;		
			clazz = Class.forName(getBeanName());
			process.setDataToDataList(clazz);
			
			if (ReflectUtil.inheritsFrom(clazz, AppTempEntity.class)){
				dataList.setCommitOnSave(false);
			} else {			
				process.executeQuery();
			}
			process.setNextInteraction(getShowData());
			//process.add( getShowData() );
		}
	};

	@Override 
	public void defineWindow() throws Exception {		

		
		this.gridVariaveisPlanoConta = new GridPlanoConta(this);
		this.setHelp( "Esta window é utilizada para cadastrar as Contas Contábeis." );
		
		this.setGridViewMode(DataGrid.VIEW_MODE_TABLEVIEW);

//		grid = new DataGrid( this, "dataGrid" ){
//			@Override
//			public void onAfterInsert() throws Exception {
//				super.onAfterInsert();
//				ContaContabilWindow.this.onAfterInsert();
//			}
//			
//			@Override
//			public void onAfterPost() throws Exception {
//				super.onAfterPost();
//				ContaContabilWindow.this.onAfterPost();
//			}
//		};		
//		grid.setIndexFields("codigo");
		
//		add( this.getShowData() );
		
//		if (ReflectUtil.inheritsFrom(clazz, AppTempEntity.class)){
//			dataList.setCommitOnSave(false);
//		} else {			
//			this.executeQuery();
//		}
	}
	
	public Interaction getShowData(){	
		if ( this.showData == null ){
			this.showData = new InteractionShowData( this, "showData" );	
		}
		return (Interaction)this.showData;
	}
	public Object getValueToFind() {
		return valueToFind;
	}

	public void setValueToFind(Object valueToFind) {
		this.valueToFind = valueToFind;
	}
	
	public PlanoConta getPlanoConta() {
		return planoConta;
	}

	public void setPlanoConta(PlanoConta planoConta) {
		this.planoConta = planoConta;
	}
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public String getIndexFields() {
		return indexFields;
	}

	public void setIndexFields(String indexFields) {
		this.indexFields = indexFields;
	}

	public Long getIdToFind() {
		return idToFind;
	}

	public void setIdToFind(Long idToFind) {
		this.idToFind = idToFind;
	}

	public String getColumnToFind() {
		return columnToFind;
	}

	public void setColumnToFind(String columnToFind) {
		this.columnToFind = columnToFind;
	}

	public ArrayList getValuesToFilterQuery() {
		return valuesToFilterQuery;
	}

	public void setValuesToFilterQuery(ArrayList valuesToFilterQuery) {
		this.valuesToFilterQuery = valuesToFilterQuery;
	}

	public ArrayList<String> getColumnsToFilterQuery() {
		return columnsToFilterQuery;
	}

	public void setColumnsToFilterQuery(ArrayList<String> columnsToFilterQuery) {
		this.columnsToFilterQuery = columnsToFilterQuery;
	}

	public Integer getGridViewMode() {
		return gridViewMode;
	}

	public void setGridViewMode(Integer gridViewMode) {
		this.gridViewMode = gridViewMode;
	}

	public void onAfterInsert() throws Exception {		
		this.grid.getFieldByName("sintetico").setValue(true);
		this.grid.getFieldByName("planoConta").setValue(this.getPlanoConta());
	}
	
	public void onBeforePost() throws Exception {
		String cod = (String) this.grid.getFieldByName("codigo").getValue();
		PlanoConta planoConta = (PlanoConta) this.grid.getFieldByName("planoConta").getValue(); 
		
		
		DataList dl = DataListFactory.newDataList(getDatabase());
		String hquery = "from " + ContaContabil.class.getName(); 
		String discriminator = AbstractEntity.getClassIdentifier( ContaContabil.class );
		
		if ( discriminator != null && ! discriminator.equals("") ){
			String childrenString = HibernateUtil.getInstance(getDatabase()).getChildrenString(Class.forName(this.getBeanName()));
			hquery += " where classId in (" + childrenString + ") ";			
		}
		int len = cod.length();
		hquery += " and codigo < '" + cod + "' and length(codigo) < " + len + " and planoConta.id = " + planoConta.getId();
		hquery += "order by codigo desc";
		dl.executeQuery(hquery, 0, 1);
		if ( ! dl.isEmpty()){
			ContaContabil object = (ContaContabil) dl.getList().get(0);
			if ( ! object.getSintetico() ) {
				throw new AppException("Não é possível inserir uma nova conta filha em uma Conta Analítica.");
			}			
		} else {
			if( ! (Boolean) this.grid.getFieldByName("sintetico").getValue()){
				throw new AppException("Só é possível inserir uma Conta Analítica se existir uma Conta Sintética atrelada à mesma.");
			}
			
		}
	}
	
	
	public class InteractionShowData extends Interaction{
		public InteractionShowData(Window window, String string) {
			super(window, string);
		}
		
		@Override
		public void defineInteraction() throws Exception {
			ContaContabilWindow window = (ContaContabilWindow) this.getWindow();
			
			//TODO deve ser retirado apos a correcao da Grid.
			if ( grid != null ){
				this.remove( grid );
			}
			
			grid = new DataGrid( this.getWindow(), "dataGrid" ){
				@Override
				public void onAfterInsert() throws Exception {					
					super.onAfterInsert();
					ContaContabilWindow.this.onAfterInsert();
				}
				
				@Override
				public void onBeforePost() throws Exception {
					super.onBeforePost();
					ContaContabilWindow.this.onBeforePost();
				}
			};			
			
			grid.setHelp("This grid is a general grid to show entities from a class.");
			grid.setViewMode( gridViewMode );
			grid.setBeanName( window.getBeanName() );
			grid.setIndexFields("codigo");			
			grid.setDataList(dataList);
			grid.setTitle( window.getTitle() );
			grid.inheritBeanEvents();
			
			this.add(grid);

			Long idToFind = window.getIdToFind();
			if ( idToFind != null && dataList.findId( idToFind ) ){
				BaseEntity foundObject = (BaseEntity)dataList.getObjectById( idToFind );				
				grid.goToEntity( foundObject );
			}
			
			Object valueToFind = window.getValueToFind();
			String columnToFind = window.getColumnToFind(); 
			if ( valueToFind != null && columnToFind != null && dataList.find( columnToFind, valueToFind ) ){
				AbstractEntity foundObject = (AbstractEntity)dataList.getOne(columnToFind, valueToFind );
				if ( foundObject != null ){
					grid.goToEntity( foundObject );
				}
			}
			
		}
	}
}

class GridPlanoConta extends VariableGrid {
	
	public FieldLookup fldPlanoConta = new FieldLookup(this);

	public GridPlanoConta(Window window) throws Exception {
		super(window, "Variáveis");		
	}
	
	@Override
	public void defineGrid() throws Exception {
		setTitle("Variáveis");	
		fldPlanoConta.setBeanName(PlanoConta.class.getName());
		fldPlanoConta.setLabel("Plano de Contas");
		fldPlanoConta.setName("planoConta");
		fldPlanoConta.setOrder(1);
		
	}
}