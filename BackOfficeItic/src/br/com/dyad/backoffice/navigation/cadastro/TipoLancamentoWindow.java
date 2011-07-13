package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.cadastro.TipoLancamento;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class TipoLancamentoWindow extends GenericEntityBeanWindow {
	
	public TipoLancamentoWindow(HttpSession httpSession) {
		super(httpSession);
	}

	@Override 
	public void defineWindow() throws Exception {
		this.setBeanName( TipoLancamento.class.getName() );
		super.defineWindow();
		this.setGridViewMode(DataGrid.VIEW_MODE_FORMVIEW);
	}
}
