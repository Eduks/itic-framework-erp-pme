package br.com.dyad.backoffice.navigation.operacao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.MovDispo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraBaixaTitulo;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.exceptions.CommitException;
import br.com.dyad.commons.exceptions.DeleteException;
import br.com.dyad.infrastructure.entity.AbstractEntity;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldMasterDetail;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.DetailGrid;
import br.com.dyad.infrastructure.widget.grid.Grid;
import br.com.dyad.infrastructure.widget.grid.MasterScrollListener;

public class GridEdicaoBaixaTitulo extends DataGrid {

	private RegraBaixaTitulo regraBaixaTitulo;
	public DetailGridItensBaixaTitulo fieldBaixasTitulo;
	public DetailGridMovDisposBaixaTitulo fieldMovDisposBaixasTitulo;
	
	public GridEdicaoBaixaTitulo(Window window, RegraBaixaTitulo regraBaixaTitulo) throws Exception {
		super(window, "dadosDaBaixaDoTitulo");
		this.setTitle("Agrupamento dos Títulos");
		this.setBeanName( CabecalhoBaixaTitulo.class.getName() );
		this.regraBaixaTitulo = regraBaixaTitulo;

		this.fieldBaixasTitulo = new DetailGridItensBaixaTitulo(this);
		this.fieldBaixasTitulo.setName("gridTitulos");
		this.fieldBaixasTitulo.setLabel("Baixas");
		this.fieldBaixasTitulo.setVisible( true );
		this.fieldBaixasTitulo.setOrder( 10 );
		this.fieldBaixasTitulo.setRequired( false );
		this.fieldBaixasTitulo.setBeanName( ItemBaixaTitulo.class.getName() );

		this.fieldMovDisposBaixasTitulo = new DetailGridMovDisposBaixaTitulo(this);
		this.fieldMovDisposBaixasTitulo.setName("gridMovDispos");
		this.fieldMovDisposBaixasTitulo.setLabel("Movimentações de Disponíveis");
		this.fieldMovDisposBaixasTitulo.setVisible( true );
		this.fieldMovDisposBaixasTitulo.setOrder( 20 );
		this.fieldMovDisposBaixasTitulo.setRequired( false );
		this.fieldMovDisposBaixasTitulo.setBeanName( MovDispo.class.getName() );
		
	}

	@Override
	public AbstractEntity createNewEntity() throws Exception {
		return this.regraBaixaTitulo.novoCabecalho(); 
	}

	@Override
	public void defineGrid() throws Exception {
		super.defineGrid();
	}

	public RegraBaixaTitulo getRegraBaixaTitulo() {
		return regraBaixaTitulo;
	}

	public void setRegraBaixaTitulo(RegraBaixaTitulo regraBaixaTitulo) {
		this.regraBaixaTitulo = regraBaixaTitulo;
	}
};

class DetailGridItensBaixaTitulo extends FieldMasterDetail {
	private GridEdicaoBaixaTitulo gridEdicaoBaixaTitulo;
	
	public DetailGridItensBaixaTitulo(GridEdicaoBaixaTitulo grid) throws Exception {
		super(grid, "gridTitulos", new DataList());
		this.gridEdicaoBaixaTitulo = grid;
	}

	@Override
	protected void initializeDetailGrid(Grid grid) throws Exception{			
		this.detailGrid = new DetailGrid( grid.getWindow(), "detailGridBaixasPedido", this.getDataList()){
			@Override
			public AbstractEntity createNewEntity() throws Exception {
				AbstractEntity ent = gridEdicaoBaixaTitulo.getRegraBaixaTitulo().novoItem((CabecalhoBaixaTitulo)gridEdicaoBaixaTitulo.getCurrentEntity());				

				return ent; 
			}
			@Override
			protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
				int index = dataList.getList().indexOf( abstractEntity ); 
				if ( index == -1 ){
					throw new Exception("Erro interno: Não foi possível encontrar o registro: " + abstractEntity.toString() );
				}
				
				try {
					gridEdicaoBaixaTitulo.getRegraBaixaTitulo().excluiItem((ItemBaixaTitulo) abstractEntity);
					this.getDataList().delete(abstractEntity);
				} catch (CommitException e) {
					e.printStackTrace();
					rollBackListOnDeleteException();
					
					if ( e.getCause() != null && e.getCause() instanceof ConstraintViolationException ){
						SQLException sqlException = ((ConstraintViolationException)e.getCause()).getSQLException();
						throw new DeleteException("Não foi possivel deletar o registro " + abstractEntity.toString() + "<br><br>" + 
								sqlException.getMessage(), e);
					} else {
						throw e;
					}
				}
			}

			protected MasterScrollListener getMasterScrollListener() {
				if ( this.masterScrollListener == null ){
					this.masterScrollListener = new MasterScrollListener(){
						@Override
						protected void filterDetail(DetailGrid detailGrid, AbstractEntity masterEntity) throws Exception {
							CabecalhoBaixaTitulo cabecalho = ((CabecalhoBaixaTitulo)masterEntity);
							detailGrid.getDataList().setList( (List) cabecalho.getItensBaixaTitulo() );
						}
					};
				}
				return this.masterScrollListener;
			}
		};
		//Adiciona a gridDetail como listener do datalist do objeto
		this.getDataList().addDataListListener(this.detailGrid);
		
		this.setMasterFieldNames("id");
		this.setDetailFieldNames("cabecalho");
		this.detailGrid.setViewMode(DataGrid.VIEW_MODE_TABLEVIEW);
		this.detailGrid.setMasterField(this);
	}
};	

class DetailGridMovDisposBaixaTitulo extends FieldMasterDetail {
	private GridEdicaoBaixaTitulo gridEdicaoBaixaTitulo;
	
	public DetailGridMovDisposBaixaTitulo(GridEdicaoBaixaTitulo grid) throws Exception {
		super(grid, "gridMovDispos", new DataList());
		this.gridEdicaoBaixaTitulo = grid;
	}

	@Override
	protected void initializeDetailGrid(Grid grid) throws Exception{			
		this.detailGrid = new DetailGrid( grid.getWindow(), "detailGridMovDisposBaixasPedido", this.getDataList()){
			@Override
			public AbstractEntity createNewEntity() throws Exception {
				AbstractEntity ent = gridEdicaoBaixaTitulo.getRegraBaixaTitulo().novoMovDispo((CabecalhoBaixaTitulo)gridEdicaoBaixaTitulo.getCurrentEntity());
				
				return ent; 
			}
			@Override
			protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
				int index = dataList.getList().indexOf( abstractEntity ); 
				if ( index == -1 ){
					throw new Exception("Erro interno: Não foi possível encontrar o registro: " + abstractEntity.toString() );
				}
				
				try {
					this.getDataList().delete(abstractEntity);
					gridEdicaoBaixaTitulo.getRegraBaixaTitulo().excluiMovDispo((MovDispo) abstractEntity);
				} catch (CommitException e) {
					e.printStackTrace();
					rollBackListOnDeleteException();
					
					if ( e.getCause() != null && e.getCause() instanceof ConstraintViolationException ){
						SQLException sqlException = ((ConstraintViolationException)e.getCause()).getSQLException();
						throw new DeleteException("Não foi possivel deletar o registro " + abstractEntity.toString() + "<br><br>" + 
								sqlException.getMessage(), e);
					} else {
						throw e;
					}
				}
			}

			protected MasterScrollListener getMasterScrollListener() {
				if ( this.masterScrollListener == null ){
					this.masterScrollListener = new MasterScrollListener(){
						@Override
						protected void filterDetail(DetailGrid detailGrid, AbstractEntity masterEntity) throws Exception {
							CabecalhoBaixaTitulo cabecalho = ((CabecalhoBaixaTitulo)masterEntity);
							detailGrid.getDataList().setList( (List) cabecalho.getMovDispos() );
						}
					};
				}
				return this.masterScrollListener;
			}
		};
		//Adiciona a gridDetail como listener do datalist do objeto
		this.getDataList().addDataListListener(this.detailGrid);
		
		this.setMasterFieldNames("id");
		this.setDetailFieldNames("cabecalho");
		this.detailGrid.setViewMode(DataGrid.VIEW_MODE_TABLEVIEW);
		this.detailGrid.setMasterField(this);
	}
};	
