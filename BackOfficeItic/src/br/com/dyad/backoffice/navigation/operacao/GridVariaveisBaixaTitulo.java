package br.com.dyad.backoffice.navigation.operacao;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.client.AppException;
import br.com.dyad.infrastructure.entity.BaseEntity;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldClassLookup;
import br.com.dyad.infrastructure.widget.field.FieldCombo;
import br.com.dyad.infrastructure.widget.field.FieldInteger;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.field.FieldString;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class GridVariaveisBaixaTitulo extends VariableGrid {

	public FieldInteger fldItem = new FieldInteger(this);
	public FieldInteger fldOperacao = new FieldInteger(this);
	public FieldString fldNumero = new FieldString(this);
	public FieldLookup fldEntidade = new FieldLookup(this);
	public FieldSimpleDate fldEmissaoInicial = new FieldSimpleDate(this){
		@Override
		public void onAfterChange() {
			super.onAfterChange();
			
			VariableGrid vGrid = (VariableGrid) this.getGrid();				
			Date emissaoInicial = (Date)this.getValue();
			
			FieldSimpleDate fldEmissaoFinal = (FieldSimpleDate)vGrid.getFieldByName("emissaoFinal");
			
			if ( emissaoInicial != null && fldEmissaoFinal.getValue() != null && emissaoInicial.after(fldEmissaoFinal.getValue())){
				SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
				throw new AppException("A data de Emissão Inicial '" + dataFormat.format(emissaoInicial) + "' deve ser menor ou igual a Data de Emissão Final'" + dataFormat.format(fldEmissaoFinal.getValue()) + "'.");
			}
			
		}	
	};
	public FieldSimpleDate fldEmissaoFinal = new FieldSimpleDate(this){
		@Override
		public void onAfterChange() {
			super.onAfterChange();
			
			VariableGrid vGrid = (VariableGrid) this.getGrid();				
			Date emissaoFinal = (Date)this.getValue();
			
			FieldSimpleDate fldEmissaoInicial = (FieldSimpleDate)vGrid.getFieldByName("emissaoInicial");
			
			if ( emissaoFinal != null && fldEmissaoInicial.getValue() != null && emissaoFinal.before(fldEmissaoInicial.getValue())){
				SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");				
				throw new AppException("A data de Emissão Final '" + dataFormat.format(emissaoFinal) + "' deve ser maior ou igual a Data de Emissão Inicial '" + dataFormat.format(fldEmissaoInicial.getValue()) + "'.");				
			}
			
		}	
	};
	
	public FieldSimpleDate fldVencimentoInicial = new FieldSimpleDate(this){
		@Override
		public void onAfterChange() {
			super.onAfterChange();
			
			VariableGrid vGrid = (VariableGrid) this.getGrid();				
			Date vencimentoInicial = (Date)this.getValue();
			
			FieldSimpleDate fldVencimentoFinal = (FieldSimpleDate)vGrid.getFieldByName("vencimentoFinal");
			
			if ( vencimentoInicial != null && fldVencimentoFinal.getValue() != null && vencimentoInicial.after(fldVencimentoFinal.getValue())){
				SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
				throw new AppException("A data de Vencimento Inicial '" + dataFormat.format(vencimentoInicial) + "' deve ser menor ou igual a Data de Vencimento Final'" + dataFormat.format(fldVencimentoFinal.getValue()) + "'.");
			}
			
		}	
	};
	public FieldSimpleDate fldVencimentoFinal = new FieldSimpleDate(this){
		@Override
		public void onAfterChange() {
			super.onAfterChange();
			
			VariableGrid vGrid = (VariableGrid) this.getGrid();				
			Date vencimentoFinal = (Date)this.getValue();
			
			FieldSimpleDate fldVencimentoInicial = (FieldSimpleDate)vGrid.getFieldByName("vencimentoInicial");
			
			if ( vencimentoFinal != null && fldVencimentoInicial.getValue() != null && vencimentoFinal.before(fldVencimentoInicial.getValue())){
				SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");				
				throw new AppException("A data de Vencimento Final '" + dataFormat.format(vencimentoFinal) + "' deve ser maior ou igual a Data de Vencimento Inicial '" + dataFormat.format(fldVencimentoInicial.getValue()) + "'.");				
			}
			
		}	
	};	
	
	public GridVariaveisBaixaTitulo(Window window) throws Exception {
		super(window, "variaveis");
	}

	@Override
	public void defineGrid() throws Exception {
		setTitle("Variáveis");

		int count = 0;

		fldOperacao.setLabel("Operação");
		fldOperacao.setName("operacao");
		fldOperacao.setWidth(150);
		fldOperacao.setOrder(count++);
		
		fldItem.setLabel("Item");
		fldItem.setName("item");
		fldItem.setWidth(150);
		fldItem.setOrder(count++);

		fldNumero.setLabel("Número");
		fldNumero.setName("numero");
		fldNumero.setOrder(count++);
		
		fldEntidade.setBeanName(Entidade.class.getName());
		fldEntidade.setName("entidade");
		fldEntidade.setOrder(count++);

		fldEmissaoInicial.setLabel("Emissão Inicial");
		fldEmissaoInicial.setName("emissaoInicial");
		fldEmissaoInicial.setWidth(150);
		fldEmissaoInicial.setOrder(count++);

		fldEmissaoFinal.setLabel("Emissão Final");
		fldEmissaoFinal.setName("emissaoFinal");
		fldEmissaoFinal.setWidth(150);
		fldEmissaoFinal.setOrder(count++);

		fldVencimentoInicial.setLabel("Vencimento Inicial");
		fldVencimentoInicial.setName("vencimentoInicial");
		fldVencimentoInicial.setWidth(150);
		fldVencimentoInicial.setOrder(count++);

		fldVencimentoFinal.setLabel("Vencimento Final");
		fldVencimentoFinal.setName("vencimentoFinal");
		fldVencimentoFinal.setWidth(150);
		fldVencimentoFinal.setOrder(count++);
	}
}
