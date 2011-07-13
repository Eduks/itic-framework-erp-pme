package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class EstabelecimentoWindow extends GenericEntityBeanWindow {

	public EstabelecimentoWindow (HttpSession httpSession) {
		super(httpSession);
	}
	
	@Override
	public void defineWindow() throws Exception {
		this.setBeanName( Estabelecimento.class.getName() );
		this.setGridViewMode(DataGrid.VIEW_MODE_FORMVIEW);
		super.defineWindow();
	}
}		