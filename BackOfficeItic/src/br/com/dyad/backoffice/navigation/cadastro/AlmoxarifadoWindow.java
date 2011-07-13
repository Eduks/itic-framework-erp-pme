package br.com.dyad.backoffice.navigation.cadastro;

import javax.servlet.http.HttpSession;

import br.com.dyad.businessinfrastructure.entidades.entidade.Almoxarifado;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

/**
 * @author Franklin Kaswiner
 *
 */
public class AlmoxarifadoWindow extends GenericEntityBeanWindow {

	public AlmoxarifadoWindow (HttpSession httpSession) {
		super(httpSession);
		this.setBeanName( Almoxarifado.class.getName() );
	}

	@Override 
	public void defineWindow() throws Exception {
		super.defineWindow();		

		this.setGridViewMode(DataGrid.VIEW_MODE_FORMVIEW);
		this.setIndexFields("codigo");
		this.setAutoColumn(2);
		this.setAutoWidth(150);
	}}