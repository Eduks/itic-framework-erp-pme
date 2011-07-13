package br.com.dyad.backoffice.navigation.operacao;

import java.util.ArrayList;
import java.util.Collection;

import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.RegraTransferenciaDisponivel;
import br.com.dyad.infrastructure.entity.AbstractEntity;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldMasterDetail;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.DetailGrid;
import br.com.dyad.infrastructure.widget.grid.Grid;
import br.com.dyad.infrastructure.widget.grid.MasterScrollListener;

class GridEdicaoTransferenciaDisponivel extends DataGrid {

	public RegraTransferenciaDisponivel regraTransferenciaDisponivel;
	
	public GridEdicaoTransferenciaDisponivel(Window window, RegraTransferenciaDisponivel regraTransferenciaDisponivel) throws Exception {
		super(window, "dadosDoPedido");
		this.setTitle("Agrupamento dos Pedidos");
		this.setBeanName( CabecalhoTransferenciaDisponivel.class.getName() );
		this.regraTransferenciaDisponivel = regraTransferenciaDisponivel;
		
		fieldPedidos.setLabel("Itens");
		fieldPedidos.setVisible( true );
		fieldPedidos.setOrder( 50 );
		fieldPedidos.setRequired( false );
		fieldPedidos.setBeanName( ItemTransferenciaDisponivel.class.getName() );
	}

	@Override
	public AbstractEntity createNewEntity() throws Exception {
		return this.regraTransferenciaDisponivel.novoCabecalho(); 
	}
	
	public FieldMasterDetail fieldPedidos = new FieldMasterDetail(this, "itens") {
		@Override
		protected void initializeDetailGrid(Grid grid) throws Exception{			
			this.detailGrid = new DetailGrid( grid.getWindow(), "datailGrid"){
				@Override
				public AbstractEntity createNewEntity() throws Exception {
					AbstractEntity ent = regraTransferenciaDisponivel.novoItem( (CabecalhoTransferenciaDisponivel)GridEdicaoTransferenciaDisponivel.this.getCurrentEntity() );
										
					detailGrid.getDataList().newEntity(ent);
					
					return ent; 
				}
				
				protected MasterScrollListener getMasterScrollListener() {
					if ( this.masterScrollListener == null ){
						this.masterScrollListener = new MasterScrollListener(){
							@Override
							protected void filterDetail(DetailGrid detailGrid, AbstractEntity masterEntity) throws Exception {
								Long operacaoId = ((CabecalhoTransferenciaDisponivel)masterEntity).getOperacaoId();
								Collection<ItemTransferenciaDisponivel> transferenciasDisponivelOperacaoId = regraTransferenciaDisponivel.getItensCabecalho( operacaoId );
								ArrayList<ItemTransferenciaDisponivel> transferenciasDisponivelFiltrados = transferenciasDisponivelOperacaoId == null ?  new ArrayList() : new ArrayList( transferenciasDisponivelOperacaoId );
								detailGrid.getDataList().setList( transferenciasDisponivelFiltrados );
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
	
	@Override
	public void defineGrid() throws Exception {
		super.defineGrid();
		
		/*addWidgetListener(DyadEvents.onAfterScroll, new WidgetListener() {			
			@Override
			public void handleEvent(Object sender) throws Exception {
				//DataGrid grid = (DataGrid)sender;
				//grid.setReadOnly( grid.getFieldByName("aprovacao").getValue() != null );
			}
		});*/
	}
};