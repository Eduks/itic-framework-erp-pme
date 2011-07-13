package br.com.dyad.backoffice.navigation.operacao;

import java.util.ArrayList;
import java.util.Collection;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.RegraBaixaPedido;
import br.com.dyad.infrastructure.entity.AbstractEntity;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldMasterDetail;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.DetailGrid;
import br.com.dyad.infrastructure.widget.grid.Grid;
import br.com.dyad.infrastructure.widget.grid.MasterScrollListener;

class GridEdicaoBaixaPedido extends DataGrid {

	public RegraBaixaPedido regraBaixaPedido;
	public FieldMasterDetail fieldBaixasPedido;
	public FieldMasterDetail fieldTitulosBaixaPedido;
	
	public GridEdicaoBaixaPedido(Window window, RegraBaixaPedido regraBaixaPedido) throws Exception {
		super(window, "dadosDaBaixaDoPedido");
		this.setTitle("Agrupamento dos Pedidos");
		this.setBeanName( CabecalhoBaixaPedido.class.getName() );
		this.regraBaixaPedido = regraBaixaPedido;
		
		fieldBaixasPedido = new FieldMasterDetail(this, "baixas"){
			@Override
			protected void initializeDetailGrid(Grid grid) throws Exception{			
				this.detailGrid = new DetailGrid( grid.getWindow(), "datailGridBaixasPedido"){
					@Override
					public AbstractEntity createNewEntity() throws Exception {
						AbstractEntity ent = GridEdicaoBaixaPedido.this.regraBaixaPedido.novoItem((CabecalhoBaixaPedido)GridEdicaoBaixaPedido.this.getCurrentEntity()  );
						detailGrid.getDataList().add(ent);
						return ent; 
					}
					
					protected MasterScrollListener getMasterScrollListener() {
						if ( this.masterScrollListener == null ){
							this.masterScrollListener = new MasterScrollListener(){
								@Override
								protected void filterDetail(DetailGrid detailGrid, AbstractEntity masterEntity) throws Exception {
									Long operacaoId = ((CabecalhoBaixaPedido)masterEntity).getOperacaoId();
									Collection<ItemBaixaPedido> baixasPedidoOperacaoId = GridEdicaoBaixaPedido.this.regraBaixaPedido.getBaixasCabecalho(operacaoId);
									ArrayList<ItemBaixaPedido> baixasPedidoFiltrados = baixasPedidoOperacaoId == null ?  new ArrayList() : new ArrayList( baixasPedidoOperacaoId );
									detailGrid.getDataList().setList( baixasPedidoFiltrados );
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
				this.detailGrid.setDataList(GridEdicaoBaixaPedido.this.regraBaixaPedido.getItens());
			}
		};
		fieldBaixasPedido.setLabel("Baixas");
		fieldBaixasPedido.setVisible( true );
		fieldBaixasPedido.setOrder( 50 );
		fieldBaixasPedido.setRequired( false );
		fieldBaixasPedido.setBeanName( ItemBaixaPedido.class.getName() );
		//((DataGrid)fieldBaixasPedido.getGrid()).setDataList(this.regraBaixaPedido.getItens());
		
		
		fieldTitulosBaixaPedido = new FieldMasterDetail(this, "titulos"){
			@Override
			protected void initializeDetailGrid(Grid grid) throws Exception {			
				this.detailGrid = new DetailGrid( grid.getWindow(), "datailGridTitulos"){
					@Override
					public AbstractEntity createNewEntity() throws Exception {
						AbstractEntity ent = (AbstractEntity)GridEdicaoBaixaPedido.this.regraBaixaPedido.getRegraTituloBaixaPedido().novoItem((CabecalhoBaixaPedido)GridEdicaoBaixaPedido.this.getCurrentEntity());
						detailGrid.getDataList().add(ent);
						return ent; 
					}
					
					protected MasterScrollListener getMasterScrollListener() {
						if ( this.masterScrollListener == null ){
							this.masterScrollListener = new MasterScrollListener(){
								@Override
								protected void filterDetail(DetailGrid detailGrid, AbstractEntity masterEntity) throws Exception {
									Long operacaoId = ((CabecalhoBaixaPedido)masterEntity).getOperacaoId();
									ArrayList<Titulo> titulosOperacaoId = GridEdicaoBaixaPedido.this.regraBaixaPedido.getRegraTituloBaixaPedido().getTitulosCabecalhoBaixaPedido(operacaoId);
									ArrayList<Titulo> titulosFiltrados = titulosOperacaoId == null ?  new ArrayList() : new ArrayList( titulosOperacaoId );
									detailGrid.getDataList().setList( titulosFiltrados );
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
				this.detailGrid.setDataList(GridEdicaoBaixaPedido.this.regraBaixaPedido.getRegraTituloBaixaPedido().getItens());
			}
		};		
		fieldTitulosBaixaPedido.setLabel("Títulos");
		fieldTitulosBaixaPedido.setVisible( true );
		fieldTitulosBaixaPedido.setOrder( 60 );
		fieldTitulosBaixaPedido.setRequired( false );
		fieldTitulosBaixaPedido.setBeanName( Titulo.class.getName() );
		//((DataGrid)fieldTitulosBaixaPedido.getGrid()).setDataList(this.regraBaixaPedido.getRegraTituloBaixaPedido().getItens());
	}

	@Override
	public AbstractEntity createNewEntity() throws Exception {
		return this.regraBaixaPedido.novoCabecalho(); 
	}
	
	@Override
	public void defineGrid() throws Exception {
		super.defineGrid();
	}
};