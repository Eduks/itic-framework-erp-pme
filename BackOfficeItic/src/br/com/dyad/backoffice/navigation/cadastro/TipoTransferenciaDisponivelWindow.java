package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.cadastro.TipoTransferenciaDisponivel;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class TipoTransferenciaDisponivelWindow extends GenericEntityBeanWindow {
	
	public TipoTransferenciaDisponivelWindow(HttpSession httpSession) {
		super(httpSession);
	}

	@Override 
	public void defineWindow() throws Exception {
		this.setBeanName( TipoTransferenciaDisponivel.class.getName() );
		super.defineWindow();
		this.setGridViewMode(DataGrid.VIEW_MODE_FORMVIEW);
	}
}
