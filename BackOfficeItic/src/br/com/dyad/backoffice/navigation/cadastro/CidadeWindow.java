package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.businessinfrastructure.entidades.tabela.Localidade;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class CidadeWindow extends GenericEntityBeanWindow {

	public CidadeWindow (HttpSession httpSession) {
		super(httpSession);
	}

	@Override 
	public void defineWindow() throws Exception {
		this.setBeanName( Localidade.class.getName() );
		this.setGridViewMode(DataGrid.VIEW_MODE_FORMVIEW);
		super.defineWindow();
	}}