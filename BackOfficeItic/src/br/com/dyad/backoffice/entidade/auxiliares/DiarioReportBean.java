package br.com.dyad.backoffice.entidade.auxiliares;

import java.math.BigDecimal;

import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.infrastructure.annotations.MetaField;

public class DiarioReportBean extends ItemLancamento {
	
	private BigDecimal debito;
	private BigDecimal credito;
	private String conta;
	private String idCabecalho;
	private String idItem;
	private String data;
	
	public BigDecimal getDebito() {
		return debito;
	}
	public void setDebito(BigDecimal debito) {
		this.debito = debito;
	}
	public BigDecimal getCredito() {
		return credito;
	}
	public void setCredito(BigDecimal credito) {
		this.credito = credito;
	}
	
	public static void defineFields( String className){
		ItemLancamento.defineFields(className);
		
		int ordem = 0;
		
		defineField( 
				className,
				"id",
				MetaField.order, ++ordem,
				MetaField.visible, false
		);
		defineField( 
				className,
				"tipoLancamento",
				MetaField.order, ++ordem,
				MetaField.visible, false
		);
		defineField( 
				className,
				"contaContabil",
				MetaField.order, ++ordem,
				MetaField.visible, false
		);
		defineField( 
				className,
				"valor",
				MetaField.order, ++ordem,
				MetaField.visible, false
		);
		
		defineField(
				className, 
				"data",
				MetaField.order, ++ordem,
				MetaField.label, "Data",				
				MetaField.tableViewWidth, 80,
				MetaField.visible, true
		);
		
		defineField(
				className, 
				"idCabecalho",
				MetaField.order, ++ordem,
				MetaField.label, "No. Operação",
				MetaField.tableViewWidth, 100
		);
		
		defineField(
				className, 
				"idItem",
				MetaField.order, ++ordem,
				MetaField.label, "No. Item",
				MetaField.tableViewWidth, 100
		);
		
		defineField(
				className,
				"conta",
				MetaField.order, ++ordem,
				MetaField.label, "Conta",				
				MetaField.tableViewWidth, 200
		);
		
		defineField(
				className, 
				"entidade",
				MetaField.order, ++ordem,
				MetaField.label, "Entidade",				
				MetaField.tableViewWidth, 250
		);
		
		defineField(
				className, 
				"observacao",
				MetaField.order, ++ordem,
				MetaField.label, "Histórico",				
				MetaField.tableViewWidth, 250
		);
		
		defineField(
				className, 
				"debito",
				MetaField.order, ++ordem,
				MetaField.label, "Débito",				
				MetaField.tableViewWidth, 110
		);
		
		defineField(
				className, 
				"credito",
				MetaField.order, ++ordem,
				MetaField.label, "Crédito",				
				MetaField.tableViewWidth, 110
		);
	}
	
	public String getConta() {
		
		String conta = "";
		
		if (getContaContabil() != null) {
			conta += getContaContabil().getCodigo() + " - " + getContaContabil().getNome();
			
			if (getContaContabil().getEntidade() != null) {
				conta += ": " + getContaContabil().getEntidade().getNome();
			}
		} else {
			conta = this.conta;
		}
		
		return conta;
	}
	
	public void setConta(String conta) {
		this.conta = conta;
	}
	public String getIdCabecalho() {
		return idCabecalho;
	}
	public void setIdCabecalho(String idCabecalho) {
		this.idCabecalho = idCabecalho;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getIdItem() {
		return idItem;
	}
	public void setIdItem(String idItem) {
		this.idItem = idItem;
	}
	
}
