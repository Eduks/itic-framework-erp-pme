package br.com.dyad.backoffice.navigation.operacao;

import br.com.dyad.backoffice.entidade.auxiliares.PedidoAux;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class GridExibePedidosPendentesLocalizados extends DataGrid {

	public GridExibePedidosPendentesLocalizados(Window window) throws Exception {
		super(window, "pedidos");
		this.setTitle("Agrupamento dos Pedidos");
		this.setBeanName( PedidoAux.class.getName() );
	}

	@Override
	public void defineGrid() throws Exception {
		super.defineGrid();
	}
}
