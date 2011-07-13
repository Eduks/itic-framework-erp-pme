package br.com.dyad.backoffice.navigation.cadastro;

import java.util.Date;

import javax.servlet.http.HttpSession;

import com.extjs.gxt.ui.client.event.WidgetListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;


import br.com.dyad.businessinfrastructure.entidades.evento.Numeradores;
import br.com.dyad.infrastructure.i18n.Translation;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow;
import br.com.dyad.infrastructure.navigation.GenericEntityBeanWindow.InteractionShowData;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.DyadEvents;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.grid.DataGrid;

public class NumeradorWindow extends GenericEntityBeanWindow {
	
	/**
	 * @param httpSession
	 */
	public NumeradorWindow(HttpSession httpSession) {
		super(httpSession);
	}

	@Override
	public void defineWindow() throws Exception {
		
		this.setBeanName( Numeradores.class.getName() );
		
		super.defineWindow();
		
		WidgetListener insertListener = new WidgetListener() {
			
			public void handleEvent(Object sender) throws Exception {
				DataGrid grid = (DataGrid) sender;
				Numeradores numerador = (Numeradores) grid.getCurrentEntity();
				numerador.setData(new Date());
				numerador.setHora(new Date());
			}
		};		
		

	}

	public Action gerarNumeradorAction = new Action(this,"Gerar Numerador"){
		@Override
		public void onClick() throws Exception {
			Numeradores numeradorPosicionado = (Numeradores)grid.getCurrentEntity();
			NumeradorWindow.this.alert( "Numerador gerado: " + Numeradores.geraNumerador( numeradorPosicionado.getId(), grid.getDatabase() ) );
		}
	};

	public Interaction getShowData(){	
		if ( this.showData == null ){
			this.showData = new InteractionShowDataNumeradorWindow( this, "showData" );	
		}	
		return (Interaction)this.showData;
	}		
	
	public class InteractionShowDataNumeradorWindow extends InteractionShowData{
		public InteractionShowDataNumeradorWindow(Window window, String string) {
			super(window, string);
		}
		
		@Override
		public void defineInteraction() throws Exception {
			super.defineInteraction();
			this.enableAndShowActions( gerarNumeradorAction );
		}
	}

}

	