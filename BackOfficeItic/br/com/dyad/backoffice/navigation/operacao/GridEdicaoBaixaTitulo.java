package br.com.dyad.backoffice.navigation.operacao;

import java.util.ArrayList;
import java.util.Collection;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.RegraBaixaTitulo;
import br.com.dyad.infrastructure.entity.AbstractEntity;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldMasterDetail;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.DetailGrid;
import br.com.dyad.infrastructure.widget.grid.Grid;
import br.com.dyad.infrastructure.widget.grid.MasterScrollListener;

public class GridEdicaoBaixaTitulo extends DataGrid {

	public RegraBaixaTitulo regraBaixaTitulo;

	public GridEdicaoBaixaTitulo(Window window, RegraBaixaTitulo regraBaixaTitulo) throws Exception {
		super(window, "dadosDaBaixaDoTitulo");
		this.setTitle("Agrupamento dos Títulos");
		this.setBeanName( CabecalhoBaixaTitulo.class.getName() );
		this.regraBaixaTitulo = regraBaixaTitulo;

		fieldBaixasTitulo.setLabel("Baixas");
		fieldBaixasTitulo.setVisible( true );
		fieldBaixasTitulo.setOrder( 50 );
		fieldBaixasTitulo.setRequired( false );
		fieldBaixasTitulo.setBeanName( ItemBaixaTitulo.class.getName() );
	}

	@Override
	public AbstractEntity createNewEntity() throws Exception {
		return this.regraBaixaTitulo.novoCabecalho(); 
	}

	public FieldMasterDetail fieldBaixasTitulo = new FieldMasterDetail(this, "baixas"){
		@Override
		protected void initializeDetailGrid(Grid grid) throws Exception{			
			this.detailGrid = new DetailGrid( grid.getWindow(), "datailGridBaixasPedido"){
				@Override
				public AbstractEntity createNewEntity() throws Exception {
					AbstractEntity ent = regraBaixaTitulo.novoItem((CabecalhoBaixaTitulo)GridEdicaoBaixaTitulo.this.getCurrentEntity());
					detailGrid.getDataList().add(ent);
					return ent; 
				}

				protected MasterScrollListener getMasterScrollListener() {
					if ( this.masterScrollListener == null ){
						this.masterScrollListener = new MasterScrollListener(){
							@Override
							protected void filterDetail(DetailGrid detailGrid, AbstractEntity masterEntity) throws Exception {
								Long operacaoId = ((CabecalhoBaixaTitulo)masterEntity).getOperacaoId();
								Collection<ItemBaixaTitulo> baixasPedidoOperacaoId = regraBaixaTitulo.getBaixasCabecalho(operacaoId);
								ArrayList<ItemBaixaTitulo> baixasPedidoFiltrados = baixasPedidoOperacaoId == null ?  new ArrayList() : new ArrayList( baixasPedidoOperacaoId );
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
		}
	};	

	@Override
	public void defineGrid() throws Exception {
		super.defineGrid();
	}
};
