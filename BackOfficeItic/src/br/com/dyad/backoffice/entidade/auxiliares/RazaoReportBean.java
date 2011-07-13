package br.com.dyad.backoffice.entidade.auxiliares;

import java.math.BigDecimal;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;

public class RazaoReportBean extends ItemLancamento {
	
	private Long ordem;
	private BigDecimal debito;
	private BigDecimal credito;
	private BigDecimal saldoFinal;

	public Long getOrdem() {
		return ordem;
	}

	public void setOrdem(Long ordem) {
		this.ordem = ordem;
	}
	
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

	public BigDecimal getSaldoFinal() {
		return saldoFinal;
	}

	public void setSaldoFinal(BigDecimal saldoFinal) {
		this.saldoFinal = saldoFinal;
	}

	public static void defineFields( String className){
		BaseEntity.defineFields(className);
		
		int order = 0;
		
		
		
		defineField(
				className, 
				"contaContabil",
				MetaField.label, "Conta Contábil",				
				MetaField.order, ++order,
				MetaField.visible, true,
				MetaField.tableViewWidth, 250,
				MetaField.beanName, ContaContabil.class.getName()
		);
		
		defineField(
				className, 
				"entidade",
				MetaField.label, "Entidade",				
				MetaField.order, ++order,
				MetaField.visible, false,
				MetaField.tableViewWidth, 250,
				MetaField.beanName, Entidade.class.getName()
		);		
		
		defineField(
				className, 
				"classeOperacaoId",
				MetaField.order, ++order,
				MetaField.visible, false
		);

		defineField(
				className, 
				"sinal",
				MetaField.order, ++order,
				MetaField.visible, false
		);
		
		defineField( 
				className,
				"observacao",
				MetaField.order, ++order,
				MetaField.visible, false
		);
		
		defineField(
				className, 
				"principal",
				MetaField.label, "Valor",				
				MetaField.order, ++order,
				MetaField.tableViewWidth, 100,
				MetaField.visible, false
		);
		
		//-------->>>>>>>
		defineField( 
				className,
				"ordem",
				MetaField.order, ++order,
				MetaField.tableViewWidth, 50,
				MetaField.visible, false
		);
		
		defineField(
				className, 
				"emissao",
				MetaField.label, "Data",				
				MetaField.order, ++order,
				MetaField.tableViewWidth, 75,
				MetaField.visible, true
		);
		
		defineField(
				className, 
				"operacaoId",
				MetaField.label, "Lançamento",				
				MetaField.order, ++order,
				MetaField.tableViewWidth, 100,
				MetaField.visible, true
		);
		
		defineField(
				className, 
				"id",
				MetaField.label, "Item do Lançamento",				
				MetaField.order, ++order,
				MetaField.tableViewWidth, 100,
				MetaField.visible, true
		);				
		
		defineField( 
				className,
				"debito",
				MetaField.label, "Débito",
				MetaField.order, ++order,
				MetaField.tableViewWidth, 100,				
				MetaField.visible, true
		);
		
		defineField( 
				className,
				"credito",
				MetaField.label, "Crédito",
				MetaField.order, ++order,
				MetaField.tableViewWidth, 100,
				MetaField.visible, true
		);
		
		defineField( 
				className,
				"saldoFinal",
				MetaField.label, "Saldo",
				MetaField.order, ++order,
				MetaField.tableViewWidth, 100,
				MetaField.visible, true
		);
	}

}
