package br.com.dyad.backoffice.navigation.operacao;

import br.com.dyad.backoffice.entidade.auxiliares.TituloAux;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class GridExibeTitulosPendentesLocalizados extends DataGrid {

	public GridExibeTitulosPendentesLocalizados(Window window) throws Exception {
		super(window, "titulos");
		this.setTitle("Agrupamento dos Títulos");
		this.setBeanName( TituloAux.class.getName() );
	}

	@Override
	public void defineGrid() throws Exception {
		super.defineGrid();
		
	}
}
