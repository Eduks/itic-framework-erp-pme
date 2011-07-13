package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.businessinfrastructure.entidades.tabela.RegiaoEstado;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class RegiaoEstadoWindow extends GenericEntityBeanWindow {

	public RegiaoEstadoWindow (HttpSession httpSession) {
		super(httpSession);
	}

	@Override 
	public void defineWindow() throws Exception {
		this.setBeanName( RegiaoEstado.class.getName() );
		this.setGridViewMode(DataGrid.VIEW_MODE_FORMVIEW);
		super.defineWindow();
	}}