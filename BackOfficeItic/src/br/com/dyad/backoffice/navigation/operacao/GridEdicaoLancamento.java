package br.com.dyad.backoffice.navigation.operacao;

import java.sql.SQLException;

import org.hibernate.exception.ConstraintViolationException;

import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraLancamento;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.exceptions.CommitException;
import br.com.dyad.commons.exceptions.DeleteException;
import br.com.dyad.infrastructure.entity.AbstractEntity;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldMasterDetail;
import br.com.dyad.infrastructure.widget.grid.DataGrid;
import br.com.dyad.infrastructure.widget.grid.DetailGrid;
import br.com.dyad.infrastructure.widget.grid.Grid;
import br.com.dyad.infrastructure.widget.grid.MasterScrollListener;

public class GridEdicaoLancamento extends DataGrid {

	private RegraLancamento regraLancamento;

	private DetailGridItensLancamento detailGridItensLancamento;
	
	public GridEdicaoLancamento(Window window, RegraLancamento regraLancamento) throws Exception {
		super(window, "lancamentos");
		
		setTitle("Lançamentos");
		setViewMode(DataGrid.VIEW_MODE_TABLEVIEW);
		setBeanName(CabecalhoLancamento.class.getName());

		this.regraLancamento = regraLancamento;

		detailGridItensLancamento = new DetailGridItensLancamento(this);
		detailGridItensLancamento.setBeanName(ItemLancamento.class.getName());
	}

	public DetailGridItensLancamento getDetailGridItensLancamento() {
		return detailGridItensLancamento;
	}

	public void setDetailGridItensLancamento(DetailGridItensLancamento detailGridItensLancamento) {
		this.detailGridItensLancamento = detailGridItensLancamento;
	}

	public RegraLancamento getRegraLancamento() {
		return regraLancamento;
	}

	public void setRegraLancamento(RegraLancamento regraLancamento) {
		this.regraLancamento = regraLancamento;
	}

	public AbstractEntity createNewEntity() throws Exception {
		return (CabecalhoLancamento)regraLancamento.novoCabecalho(); 
	}

	@Override
	protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
		CabecalhoLancamento cabecalhoLancamento = (CabecalhoLancamento)abstractEntity;
		regraLancamento.excluiCabecalho(cabecalhoLancamento);
	}
};

/**
 * @author João Paulo
 * @since 11/03/2010
 * @Descrição
 * Class......: DetailGridItensLancamento
 * <br>Objetivo...: Classe que define a detailGrid dos Lançamentos Contábeis. 
 * <br>Sistema....: BackOffice
 * <br>Requisito..: 3007191 - Definir e criar tabela Contabil
 * <br>Observação.: (nope) 
 */
class DetailGridItensLancamento extends FieldMasterDetail {
	
	private String filtroContaContabilString;
	
	public GridEdicaoLancamento gridEdicaoLancamento;	
	
	
	/**
	 * @param grid
	 * @throws Exception
	 * @Descrição
	 * Constructor - passado como parâmetro a grid ao qual essa detail pertence, devido ser um process que trabalha 
	 * com cabeçalho e itens.
	 */
	public DetailGridItensLancamento(Grid grid) throws Exception {
		super(grid, "itens", new DataList());
		this.gridEdicaoLancamento = (GridEdicaoLancamento)grid;
	}
	
	public String getFiltroContaContabilString() {
		return filtroContaContabilString;
	}

	public void setFiltroContaContabilString(String filtro) {
		this.filtroContaContabilString = filtro;
	}
	
	/**
	 * @param filtroContaContabil
	 * @Descrição
	 * Método que serve para definir o filtro das Contas Contábeis que podem ser selecionadas,
	 * limitando as contas para as que pertençam ao Plano de Contas definido na Grid de Parâmetros, 
	 * e assim evitando que se tenha, no mesmo lançamento, contas pertencentes a diferentes Planos de Contas.  
	 */
	public void setFiltroContaContabil() {
		((FieldLookup)this.detailGrid.getFieldByName("contaContabil")).setFilterExpress(filtroContaContabilString);
	}

	public void resetaDataList() throws Exception {		
		this.detailGrid.getDataList().empty();
	}

	/**
	 * @param grid
	 * @throws Exception
	 * @Descrição
	 * Método que inicializa a detailGrid, setando as propriedades necessárias relacionadas aos dados 
	 * Master/Detail de um Lançamento: Cabeçalho/Itens
	 */
	@Override
	protected void initializeDetailGrid(Grid grid) throws Exception{			
		this.detailGrid = new DetailGrid( grid.getWindow(), "detailGrid", this.getDataList()){

			@Override
			public AbstractEntity createNewEntity() throws Exception {
				AbstractEntity ent = (ItemLancamento)gridEdicaoLancamento.getRegraLancamento().novoItem((CabecalhoLancamento)gridEdicaoLancamento.getCurrentEntity());
				
				setFiltroContaContabil();
				
				return ent; 
			}

			@Override
			protected void deleteEntity(AbstractEntity abstractEntity) throws Exception {
				int index = dataList.getList().indexOf( abstractEntity ); 
				if ( index == -1 ){
					throw new Exception("Erro interno: Não foi possível encontrar o registro: " + abstractEntity.toString() );
				}

				try {
					gridEdicaoLancamento.getRegraLancamento().excluiItem((ItemLancamento) abstractEntity);
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
							CabecalhoLancamento cabecalhoLancamento = (CabecalhoLancamento)masterEntity;
							detailGrid.getDataList().setList(cabecalhoLancamento.getItensLancamento());
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
