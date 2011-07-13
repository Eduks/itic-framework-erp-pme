package br.com.dyad.backoffice.navigation.operacao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraPedido;
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

class GridEdicaoPedido extends DataGrid {

	private RegraPedido regraPedido;
	public DetailGridItensPedido fieldPedidos;
	
	public GridEdicaoPedido(Window window, RegraPedido regraPedido) throws Exception {
		super(window, "dadosDoPedido");
		this.setFormFieldLabelAlign(Grid.FORMFIELD_LABEL_ALIGN_TOP);
		this.setTitle("Agrupamento dos Pedidos");
		this.setBeanName( CabecalhoPedido.class.getName() );
		this.regraPedido = regraPedido;
		
		this.fieldPedidos = new DetailGridItensPedido(this);
		this.fieldPedidos.setLabel("Itens");
		this.fieldPedidos.setVisible( true );
		this.fieldPedidos.setOrder( 50 );
		this.fieldPedidos.setRequired( false );
		this.fieldPedidos.setBeanName( ItemPedido.class.getName() );
	}

	@Override
	public AbstractEntity createNewEntity() throws Exception {
		return this.regraPedido.novoCabecalho(); 
	}
		
	@Override
	public void defineGrid() throws Exception {
		super.defineGrid();
	}

	public RegraPedido getRegraPedido() {
		return regraPedido;
	}
};

/**
 * Classe que representa a grid "Detail", onde são exibidos os itens pertencentes a 
 * determinado pedido. 	
 * 
 * @author Dyad
 *
 */
class DetailGridItensPedido extends FieldMasterDetail {
	private GridEdicaoPedido gridEdicaoPedido;

	public DetailGridItensPedido(Grid grid) throws Exception {
		super(grid, "itens", new DataList());
		this.gridEdicaoPedido = (GridEdicaoPedido)grid;
	}

	@Override
	protected void initializeDetailGrid(Grid grid) throws Exception{	
		this.detailGrid = new DetailGrid( grid.getWindow(), "datailGrid", this.getDataList()){

			@Override
			public AbstractEntity createNewEntity() throws Exception {
				AbstractEntity ent = (ItemPedido)gridEdicaoPedido.getRegraPedido().novoItem((CabecalhoPedido)gridEdicaoPedido.getCurrentEntity());									
				
				return ent; 
			}
			
			@Override
			protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
				int index = dataList.getList().indexOf( abstractEntity ); 
				if ( index == -1 ){
					throw new Exception("Erro interno: Não foi possível encontrar o registro: " + abstractEntity.toString() );
				}
				
				try {
					gridEdicaoPedido.getRegraPedido().excluiItem((ItemPedido) abstractEntity);
					this.getDataList().delete(abstractEntity);
				} catch (CommitException e) {
					e.printStackTrace();
					rollBackListOnDeleteException();
					
					if ( e.getCause() != null && e.getCause() instanceof ConstraintViolationException ){
						SQLException sqlException = ((ConstraintViolationException)e.getCause()).getSQLException();
						throw new DeleteException("Não foi possível deletar o registro " + abstractEntity.toString() + "<br><br>" + 
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
							CabecalhoPedido cabecalho = ((CabecalhoPedido)masterEntity);
							detailGrid.getDataList().setList( (List) cabecalho.getItensPedido() );
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