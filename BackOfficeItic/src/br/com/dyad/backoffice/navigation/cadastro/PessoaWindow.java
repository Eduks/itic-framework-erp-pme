package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class PessoaWindow extends GenericEntityBeanWindow {

	public PessoaWindow (HttpSession httpSession) {
		super(httpSession);

		this.setBeanName( Pessoa.class.getName() );
	}

	@Override 
	public void defineWindow() throws Exception {
		super.defineWindow();		

		this.setGridViewMode(DataGrid.VIEW_MODE_FORMVIEW);
		this.setIndexFields("codigo");
		this.setAutoWidth(150);
	}
}