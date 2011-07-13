package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class RecursoWindow extends GenericEntityBeanWindow {

	public RecursoWindow (HttpSession httpSession) {
		super(httpSession);
	}

	@Override 
	public void defineWindow() throws Exception {
		this.setBeanName( Recurso.class.getName() );
		this.setGridViewMode(DataGrid.VIEW_MODE_FORMVIEW);
		super.defineWindow();
	}}