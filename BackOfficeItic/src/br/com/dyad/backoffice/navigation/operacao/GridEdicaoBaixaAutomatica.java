package br.com.dyad.backoffice.navigation.operacao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraBaixaAutomatica;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.exceptions.CommitException;
import br.com.dyad.commons.exceptions.DeleteException;
import br.com.dyad.infrastructure.entity.AbstractEntity;
import br.com.dyad.infrastructure.widget.DyadEvents;
import br.com.dyad.infrastructure.widget.WidgetListener;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldMasterDetail;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.DetailGrid;
import br.com.dyad.infrastructure.widget.grid.Grid;
import br.com.dyad.infrastructure.widget.grid.MasterScrollListener;

class GridEdicaoBaixaAutomatica extends DataGrid {

	private RegraBaixaAutomatica regraBaixaAutomatica;
	public DetailGridItensBaixaAutomatica detailGridItensBaixaAutomatica;
	public DetailGridTitulosBaixaAutomatica detailGridTitulosBaixaAutomatica;
	
	public GridEdicaoBaixaAutomatica(Window window, RegraBaixaAutomatica regraBaixaAutomatica) throws Exception {
		super(window, "dadosDoPedido");
		this.setFormFieldLabelAlign(Grid.FORMFIELD_LABEL_ALIGN_TOP);
		this.setTitle("Agrupamento dos Pedidos");
		this.setBeanName( CabecalhoBaixaAutomatica.class.getName() );
		this.regraBaixaAutomatica = regraBaixaAutomatica;
		
		this.detailGridItensBaixaAutomatica = new DetailGridItensBaixaAutomatica(this);
		this.detailGridItensBaixaAutomatica.setLabel("Itens");
		this.detailGridItensBaixaAutomatica.setVisible( true );
		this.detailGridItensBaixaAutomatica.setOrder( 50 );
		this.detailGridItensBaixaAutomatica.setRequired( false );
		this.detailGridItensBaixaAutomatica.setBeanName( ItemBaixaAutomatica.class.getName() );

		this.detailGridTitulosBaixaAutomatica = new DetailGridTitulosBaixaAutomatica(this);
		this.detailGridTitulosBaixaAutomatica.setLabel("Títulos");
		this.detailGridTitulosBaixaAutomatica.setVisible( true );
		this.detailGridTitulosBaixaAutomatica.setOrder( 60 );
		this.detailGridTitulosBaixaAutomatica.setRequired( false );
		this.detailGridTitulosBaixaAutomatica.setBeanName( Titulo.class.getName() );
		
	}

	@Override
	public AbstractEntity createNewEntity() throws Exception {
		AbstractEntity ent = this.regraBaixaAutomatica.novoCabecalho();
		
		return ent;
	}
		
	@Override
	public void defineGrid() throws Exception {
		super.defineGrid();		
	}

	public RegraBaixaAutomatica getRegraBaixaAutomatica() {
		return regraBaixaAutomatica;
	}
};

/**
 * Classe que representa a grid "Detail", onde são exibidos os itens pertencentes a 
 * determinado pedido. 	
 * 
 * @author Dyad
 *
 */
class DetailGridItensBaixaAutomatica extends FieldMasterDetail {
	private GridEdicaoBaixaAutomatica gridEdicaoBaixaAutomatica;

	public DetailGridItensBaixaAutomatica(Grid grid) throws Exception {
		super(grid, "itens", new DataList());
		this.gridEdicaoBaixaAutomatica = (GridEdicaoBaixaAutomatica)grid;
	}

	@Override
	protected void initializeDetailGrid(Grid grid) throws Exception{			
		this.detailGrid = new DetailGrid( grid.getWindow(), "datailGrid", this.getDataList()){
			@Override
			public AbstractEntity createNewEntity() throws Exception {
				AbstractEntity ent = gridEdicaoBaixaAutomatica.getRegraBaixaAutomatica().novoItem( ((CabecalhoBaixaAutomatica)gridEdicaoBaixaAutomatica.getCurrentEntity()));
									
				return ent; 
			}
			
			@Override
			protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
				int index = dataList.getList().indexOf( abstractEntity ); 
				if ( index == -1 ){
					throw new Exception("Erro interno: Não foi possível encontrar o registro: " + abstractEntity.toString() );
				}
				
				try {
					gridEdicaoBaixaAutomatica.getRegraBaixaAutomatica().excluiItem((ItemBaixaAutomatica) abstractEntity);
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
							System.out.println("DetailGridItens===>MASTER SCROLL LISTENER");

							CabecalhoBaixaAutomatica cabecalho = ((CabecalhoBaixaAutomatica)masterEntity);
							detailGrid.getDataList().setList( (List) cabecalho.getItensBaixaAutomatica() );
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

/**
 * Grid detalhe responsável pela exibição dos títulos da operação
 *  
 * @author Dyad
 */
class DetailGridTitulosBaixaAutomatica extends FieldMasterDetail {
	private GridEdicaoBaixaAutomatica gridEdicaoBaixaAutomatica;
	
	public DetailGridTitulosBaixaAutomatica(GridEdicaoBaixaAutomatica grid) throws Exception {
		super(grid, "titulos", new DataList());
		this.gridEdicaoBaixaAutomatica = grid;
		
		this.addWidgetListener(DyadEvents.onBeforePost, new WidgetListener() {
			@Override
			public void handleEvent(Object sender) throws Exception {
				
				System.out.println("CHEGOU NO BEFORE POST()");
				
			}
		});

		this.addWidgetListener(DyadEvents.onAfterPost, new WidgetListener() {
			@Override
			public void handleEvent(Object sender) throws Exception {
				
				System.out.println("CHEGOU NO AFTER POST()");
				
				//DataGrid grid = (DataGrid)sender;
				//Titulo titulo = (Titulo)grid.getCurrentEntity();
				
				//titulo.setTotal(titulo.getPrincipal().add(titulo.getJuro()).add(titulo.getMulta()).add(titulo.getTarifa()));
			}
		});
	}
	
	@Override
	protected void initializeDetailGrid(Grid grid) throws Exception {			
		this.detailGrid = new DetailGrid( grid.getWindow(), "datailGridTitulos", this.getDataList()){
			@Override
			public AbstractEntity createNewEntity() throws Exception {
				AbstractEntity ent = gridEdicaoBaixaAutomatica.getRegraBaixaAutomatica().novoTitulo((CabecalhoOperacaoAbstrato) gridEdicaoBaixaAutomatica.getCurrentEntity());
				
				return ent;
			}

			@Override
			protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
				int index = dataList.getList().indexOf( abstractEntity ); 
				if ( index == -1 ){
					throw new Exception("Erro interno: Não foi possível encontrar o registro: " + abstractEntity.toString() );
				}
				
				try {
					gridEdicaoBaixaAutomatica.getRegraBaixaAutomatica().excluiTitulo((Titulo) abstractEntity);
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
			
			@Override
			protected MasterScrollListener getMasterScrollListener() {
				if ( this.masterScrollListener == null ){
					this.masterScrollListener = new MasterScrollListener(){
						@Override
						protected void filterDetail(DetailGrid detailGrid, AbstractEntity masterEntity) throws Exception {
							System.out.println("DetailGridTitulos===>MASTER SCROLL LISTENER");
							
							CabecalhoBaixaAutomatica cabecalho = ((CabecalhoBaixaAutomatica)masterEntity);
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
