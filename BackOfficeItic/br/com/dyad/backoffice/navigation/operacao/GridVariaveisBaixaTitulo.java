package br.com.dyad.backoffice.navigation.operacao;

import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldClassLookup;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldSimpleDate;
import br.com.dyad.infrastructure.widget.field.FieldString;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class GridVariaveisBaixaTitulo extends VariableGrid {

	public FieldString fldItem = new FieldString(this);
	public FieldString fldOperacao = new FieldString(this);
	public FieldString fldNumero = new FieldString(this);
	public FieldLookup fldEntidade = new FieldLookup(this);
	public FieldClassLookup fldClasseDeEntidade = new FieldClassLookup(this);
	public FieldSimpleDate fldEmissaoInicial = new FieldSimpleDate(this);
	public FieldSimpleDate fldEmissaoFinal = new FieldSimpleDate(this);
	public FieldSimpleDate fldVencimentoInicial = new FieldSimpleDate(this);
	public FieldSimpleDate fldVencimentoFinal = new FieldSimpleDate(this);

	public GridVariaveisBaixaTitulo(Window window) throws Exception {
		super(window, "variaveis");
	}

	@Override
	public void defineGrid() throws Exception {
		setTitle("Variáveis");

		int count = 0;

		fldItem.setLabel("Item");
		fldItem.setName("item");
		fldItem.setOrder(count++);

		fldOperacao.setLabel("Operação");
		fldOperacao.setName("operacao");
		fldOperacao.setOrder(count++);

		fldNumero.setLabel("Número");
		fldNumero.setName("numero");
		fldNumero.setOrder(count++);

		fldEntidade.setBeanName(Pessoa.class.getName());
		fldEntidade.setName("entidade");
		fldEntidade.setOrder(count++);

		fldClasseDeEntidade.setBeanName(Pessoa.class.getName());
		fldClasseDeEntidade.setName("classeDaEntidade");
		fldClasseDeEntidade.setOrder(count++);				

		fldEmissaoInicial.setLabel("Emissão Inicial");
		fldEmissaoInicial.setName("emissaoInicial");
		fldEmissaoInicial.setOrder(count++);

		fldEmissaoFinal.setLabel("Emissão Final");
		fldEmissaoFinal.setName("emissaoFinal");
		fldEmissaoFinal.setOrder(count++);

		fldVencimentoInicial.setLabel("Vencimento Inicial");
		fldVencimentoInicial.setName("vencimentoInicial");
		fldVencimentoInicial.setOrder(count++);

		fldVencimentoFinal.setLabel("Vencimento Final");
		fldVencimentoFinal.setName("vencimentoFinal");
		fldVencimentoFinal.setOrder(count++);
	}
}
