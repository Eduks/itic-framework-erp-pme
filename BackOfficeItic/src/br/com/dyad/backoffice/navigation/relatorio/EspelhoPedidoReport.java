package br.com.dyad.backoffice.navigation.relatorio;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraPedido;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.Label;
import br.com.dyad.infrastructure.widget.ReportWindow;
import br.com.dyad.infrastructure.widget.field.FieldInteger;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class EspelhoPedidoReport extends ReportWindow {

	private RegraPedido regraPedido;
	public Label html = new Label("");
	
	private EspelhoPedidoHTML espelhoPedido;
	
	public EspelhoPedidoReport (HttpSession httpSession) {
		super(httpSession);
	}

	@Override
	public void defineVars(VariableGrid grid) throws Exception {
		FieldInteger fieldOperacaoId = new FieldInteger( grid );
		fieldOperacaoId.setName("operacao");
		fieldOperacaoId.setLabel("Operação");
		fieldOperacaoId.setVisible(true);
		fieldOperacaoId.setWidth(150);
	}

	@Override
	public void prepareLayout() throws Exception {
		
		AppTransaction appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(this.getDatabase()), this.getDatabase());
		this.regraPedido = new RegraPedido(appTransaction);
		
		if( this.vars.getFieldByName("operacao").getValue() == null ){
			throw new AppException("Informe uma operação a ser visualizada!");
		}
		
		Long id = (Long)this.vars.getFieldByName("operacao").getValue();
		this.regraPedido.abre(id, null, null, null, null, null, null, null, null);
		
		if ( this.regraPedido.getDataListCabecalhos().getList().isEmpty() ){
			throw new AppException("Operação não encontrada");
		}
	}

	@Override
	public void defineShowLayout(Interaction interaction) throws Exception {
		
		interaction.remove(html);
		
		espelhoPedido = new EspelhoPedidoHTML();
		
		CabecalhoPedido cabecalhoPedido = (CabecalhoPedido)this.regraPedido.getDataListCabecalhos().getList().get(0);
		espelhoPedido.setCabecalhoPedido(cabecalhoPedido);
		espelhoPedido.setItensPedido(cabecalhoPedido.getItensPedido());
		
		
//	    html = new Label("");
		html.setText(espelhoPedido.preparaHtml());
		this.setTitle("EspelhoPedido");
		interaction.add(html);
	}
	
}