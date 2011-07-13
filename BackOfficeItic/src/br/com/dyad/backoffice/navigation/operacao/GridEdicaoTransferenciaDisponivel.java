package br.com.dyad.backoffice.navigation.operacao;

import java.sql.SQLException;

import org.hibernate.exception.ConstraintViolationException;

import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraTransferenciaDisponivel;
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

public class GridEdicaoTransferenciaDisponivel extends DataGrid {

	private RegraTransferenciaDisponivel regraTransferenciaDisponivel;
	
	private DetailGridItensTransferenciaDisponivel itensTransferenciaDisponivel;
	
	public GridEdicaoTransferenciaDisponivel(Window window, RegraTransferenciaDisponivel regraTransferenciaDisponivel) throws Exception {
		super(window, "transferencias");
		
		this.setTitle("Transferência entre Disponíveis");
		setViewMode(DataGrid.VIEW_MODE_TABLEVIEW);
		this.setBeanName( CabecalhoTransferenciaDisponivel.class.getName() );
		
		this.regraTransferenciaDisponivel = regraTransferenciaDisponivel;
		
		this.itensTransferenciaDisponivel = new DetailGridItensTransferenciaDisponivel(this);
		this.itensTransferenciaDisponivel.setBeanName( ItemTransferenciaDisponivel.class.getName() );
	}

	@Override
	public AbstractEntity createNewEntity() throws Exception {
		return (CabecalhoTransferenciaDisponivel)this.regraTransferenciaDisponivel.novoCabecalho();
	}
	
	@Override
	protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
		CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)abstractEntity;
		regraTransferenciaDisponivel.excluiCabecalho(cabecalhoTransferenciaDisponivel);
		
		itensTransferenciaDisponivel.getDataList().getList().clear();
	}

	public RegraTransferenciaDisponivel getRegraTransferenciaDisponivel() {
		return regraTransferenciaDisponivel;
	}

	public void setRegraTransferenciaDisponivel(RegraTransferenciaDisponivel regraTransferenciaDisponivel) {
		this.regraTransferenciaDisponivel = regraTransferenciaDisponivel;
	}

	public DetailGridItensTransferenciaDisponivel getItensTransferenciaDisponivel() {
		return itensTransferenciaDisponivel;
	}

	public void setItensTransferenciaDisponivel(
			DetailGridItensTransferenciaDisponivel itensTransferenciaDisponivel) {
		this.itensTransferenciaDisponivel = itensTransferenciaDisponivel;
	}
};

class DetailGridItensTransferenciaDisponivel extends FieldMasterDetail {
	private GridEdicaoTransferenciaDisponivel gridEdicaoTransfereciaDisponivel;
	
	public void resetaDataList() throws Exception {		
		this.detailGrid.getDataList().empty();
	}
	
	public DetailGridItensTransferenciaDisponivel(Grid grid) throws Exception {
		super(grid, "transferencias", new DataList());
		this.gridEdicaoTransfereciaDisponivel = (GridEdicaoTransferenciaDisponivel)grid;
	}

	@Override
	protected void initializeDetailGrid(Grid grid) throws Exception{			
		this.detailGrid = new DetailGrid( grid.getWindow(), "detailGrid", this.getDataList()){
			@Override
			public AbstractEntity createNewEntity() throws Exception {
				AbstractEntity ent = (ItemTransferenciaDisponivel)gridEdicaoTransfereciaDisponivel.getRegraTransferenciaDisponivel().novoItem( (CabecalhoTransferenciaDisponivel)gridEdicaoTransfereciaDisponivel.getCurrentEntity());
									
				return ent; 
			}
			@Override
			protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
				int index = dataList.getList().indexOf( abstractEntity ); 
				if ( index == -1 ){
					throw new Exception("Erro interno: Não foi possível encontrar o registro: " + abstractEntity.toString() );
				}
				
				try {
					gridEdicaoTransfereciaDisponivel.getRegraTransferenciaDisponivel().excluiItem((ItemTransferenciaDisponivel) abstractEntity);
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
							CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = (CabecalhoTransferenciaDisponivel)masterEntity;
							detailGrid.getDataList().setList( cabecalhoTransferenciaDisponivel.getItensTransferenciaDisponivel() );
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