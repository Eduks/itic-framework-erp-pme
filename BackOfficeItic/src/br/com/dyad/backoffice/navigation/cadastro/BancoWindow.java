package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.businessinfrastructure.entidades.tabela.Banco;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class BancoWindow extends GenericEntityBeanWindow {

	public BancoWindow (HttpSession httpSession) {
		super(httpSession);
	}

	@Override 
	public void defineWindow() throws Exception {
		this.setBeanName( Banco.class.getName() );
		this.setGridViewMode(DataGrid.VIEW_MODE_TABLEVIEW);
		this.setIndexFields("codigo");
		super.defineWindow();
	}}