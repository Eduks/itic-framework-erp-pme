package br.com.dyad.backoffice.navigation.operacao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraBaixaPedido;
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

class GridEdicaoBaixaPedido extends DataGrid {

	private RegraBaixaPedido regraBaixaPedido;
	public DetailGridItensBaixaPedido fieldBaixasPedido;
	public DetailGridTitulosBaixaPedido fieldTitulosBaixaPedido;
	
	public GridEdicaoBaixaPedido(Window window, RegraBaixaPedido regraBaixaPedido) throws Exception {
		super(window, "baixaPedido");
		this.setTitle("Agrupamento das Baixas de Pedido");
		this.setBeanName( CabecalhoBaixaPedido.class.getName() );
		this.regraBaixaPedido = regraBaixaPedido;
		
		this.fieldBaixasPedido = new DetailGridItensBaixaPedido(this);
		this.fieldBaixasPedido.setName("gridItens");
		this.fieldBaixasPedido.setLabel("Itens de Pedidos");
		this.fieldBaixasPedido.setVisible( true );
		this.fieldBaixasPedido.setOrder( 50 );
		this.fieldBaixasPedido.setRequired( false );
		this.fieldBaixasPedido.setBeanName( ItemBaixaPedido.class.getName() );

		this.fieldTitulosBaixaPedido = new DetailGridTitulosBaixaPedido(this);
		this.fieldTitulosBaixaPedido.setName("gridTitulos");
		this.fieldTitulosBaixaPedido.setLabel("Títulos");
		this.fieldTitulosBaixaPedido.setVisible( true );
		this.fieldTitulosBaixaPedido.setOrder( 60 );
		this.fieldTitulosBaixaPedido.setRequired( false );
		this.fieldTitulosBaixaPedido.setBeanName( Titulo.class.getName() );
	}
	
	@Override
	public AbstractEntity createNewEntity() throws Exception {
		return (CabecalhoBaixaPedido)this.regraBaixaPedido.novoCabecalho(); 
	}
	
	@Override
	public void defineGrid() throws Exception {
		super.defineGrid();
	}

	public RegraBaixaPedido getRegraBaixaPedido() {
		return regraBaixaPedido;
	}

	public void setRegraBaixaPedido(RegraBaixaPedido regraBaixaPedido) {
		this.regraBaixaPedido = regraBaixaPedido;
	}
};


class DetailGridItensBaixaPedido extends FieldMasterDetail {
	private GridEdicaoBaixaPedido gridEdicaoBaixaPedido;
	
	public DetailGridItensBaixaPedido(GridEdicaoBaixaPedido grid) throws Exception {
		super(grid, "gridItens", new DataList());
		this.gridEdicaoBaixaPedido = grid;
	}

	@Override
	protected void initializeDetailGrid(Grid grid) throws Exception{			
		this.detailGrid = new DetailGrid( grid.getWindow(), "datailGridBaixasPedido", this.getDataList()){
			@Override
			public AbstractEntity createNewEntity() throws Exception {
				AbstractEntity ent = (ItemBaixaPedido)gridEdicaoBaixaPedido.getRegraBaixaPedido().novoItem((CabecalhoBaixaPedido)gridEdicaoBaixaPedido.getCurrentEntity());
				
				return ent; 
			}

			@Override
			protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
				int index = dataList.getList().indexOf( abstractEntity ); 
				if ( index == -1 ){
					throw new Exception("Erro interno: Não foi possível encontrar o registro: " + abstractEntity.toString() );
				}
				
				try {
					gridEdicaoBaixaPedido.getRegraBaixaPedido().excluiItem((ItemBaixaPedido)abstractEntity);
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
							CabecalhoBaixaPedido cabecalho = ((CabecalhoBaixaPedido)masterEntity);
							detailGrid.getDataList().setList( (List) cabecalho.getItensBaixaPedido() );
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
}

class DetailGridTitulosBaixaPedido extends FieldMasterDetail {
	private GridEdicaoBaixaPedido gridEdicaoBaixaPedido;
	
	public DetailGridTitulosBaixaPedido(GridEdicaoBaixaPedido grid) throws Exception {
		super(grid, "gridTitulos", new DataList());
		this.gridEdicaoBaixaPedido = grid;
	}
	
	@Override
	protected void initializeDetailGrid(Grid grid) throws Exception {			
		this.detailGrid = new DetailGrid( grid.getWindow(), "datailGridTitulos", this.getDataList()){
			@Override
			public AbstractEntity createNewEntity() throws Exception {
				AbstractEntity ent = gridEdicaoBaixaPedido.getRegraBaixaPedido().novoTitulo((CabecalhoOperacaoAbstrato) gridEdicaoBaixaPedido.getCurrentEntity());
				
				return ent; 
			}

			@Override
			protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
				int index = dataList.getList().indexOf( abstractEntity ); 
				if ( index == -1 ){
					throw new Exception("Erro interno: Não foi possível encontrar o registro: " + abstractEntity.toString() );
				}
				
				try {
					gridEdicaoBaixaPedido.getRegraBaixaPedido().excluiTitulo((Titulo)abstractEntity);
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
							CabecalhoBaixaPedido cabecalho = ((CabecalhoBaixaPedido)masterEntity);
							detailGrid.getDataList().setList( (List) cabecalho.getTitulos() );
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

}
