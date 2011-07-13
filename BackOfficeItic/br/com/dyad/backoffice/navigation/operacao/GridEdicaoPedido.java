package br.com.dyad.backoffice.navigation.operacao;

import java.util.ArrayList;
import java.util.Collection;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.RegraPedido;
import br.com.dyad.infrastructure.entity.AbstractEntity;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldMasterDetail;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.DetailGrid;
import br.com.dyad.infrastructure.widget.grid.Grid;
import br.com.dyad.infrastructure.widget.grid.MasterScrollListener;

class GridEdicaoPedido extends DataGrid {

	public RegraPedido regraPedido;
	public FieldMasterDetail fieldPedidos;
	
	public GridEdicaoPedido(Window window, RegraPedido operacaoPedido) throws Exception {
		super(window, "dadosDoPedido");
		this.setTitle("Agrupamento dos Pedidos");
		this.setBeanName( CabecalhoPedido.class.getName() );
		this.regraPedido = operacaoPedido;
		
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
		super(grid, "itens");
		this.gridEdicaoPedido = (GridEdicaoPedido)grid;
	}

	@Override
	protected void initializeDetailGrid(Grid grid) throws Exception{			
		this.detailGrid = new DetailGrid( grid.getWindow(), "datailGrid"){
			@Override
			public AbstractEntity createNewEntity() throws Exception {
				AbstractEntity ent = gridEdicaoPedido.regraPedido.novoItem( (CabecalhoPedido)gridEdicaoPedido.getCurrentEntity()  );
									
				detailGrid.getDataList().newEntity(ent);
				
				return ent; 
			}
			
			protected MasterScrollListener getMasterScrollListener() {
				if ( this.masterScrollListener == null ){
					this.masterScrollListener = new MasterScrollListener(){
						@Override
						protected void filterDetail(DetailGrid detailGrid, AbstractEntity masterEntity) throws Exception {
							Long operacaoId = ((CabecalhoPedido)masterEntity).getOperacaoId();
							Collection<ItemPedido> pedidosDoOperacaoId = gridEdicaoPedido.regraPedido.getPedidosCabecalho( operacaoId );
							ArrayList<ItemPedido> pedidosFiltrados = pedidosDoOperacaoId == null ?  new ArrayList() : new ArrayList( pedidosDoOperacaoId );
							detailGrid.getDataList().setList( pedidosFiltrados );
						}
					};
				}
				return this.masterScrollListener;
			}
		};
		this.setMasterFieldNames("operacaoId");
		this.setDetailFieldNames("operacaoId");
		this.detailGrid.setViewMode(DataGrid.VIEW_MODE_TABLEVIEW);
		this.detailGrid.setMasterField(this);
	}
};