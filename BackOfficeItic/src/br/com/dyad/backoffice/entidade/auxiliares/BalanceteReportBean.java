package br.com.dyad.backoffice.entidade.auxiliares;

import java.math.BigDecimal;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.infrastructure.annotations.MetaField;

public class BalanceteReportBean extends ItemLancamento{
	
	private String conta;
	private String nome;	
	private BigDecimal saldoInicial;	
	private BigDecimal debito;
	private BigDecimal credito;
	private BigDecimal saldoFinal;
	private Long ordem;
	private ContaContabil contaContabil;
	
	public ContaContabil getContaContabil() {
		return contaContabil;
	}
	
	@Override
	public void setContaContabil(ContaContabil contaContabil) {
		this.contaContabil = contaContabil;		
	}
	
	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public BigDecimal getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(BigDecimal saldoInicial) {
		this.saldoInicial = saldoInicial;
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
	
	public Long getOrdem() {
		return ordem;
	}

	public void setOrdem(Long ordem) {
		this.ordem = ordem;
	}

	public static void defineFields( String className){
		ItemLancamento.defineFields(className);
		
		int order = 0;
		
//		defineField(
//				className, 
//				"operacaoId",
//				MetaField.label, "Lançamento",				
//				MetaField.order, ++order,
//				MetaField.tableViewWidth, 75,
//				MetaField.visible, false
//		);
		
		defineField(
				className, 
				"id",
				MetaField.label, "Item do Lançamento",				
				MetaField.order, ++order,
				MetaField.tableViewWidth, 110,
				MetaField.visible, false
		);
		
		defineField(
				className, 
				"contaContabil",
				MetaField.label, "Conta Contábil",				
				MetaField.order, ++order,
				MetaField.visible, false,
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
				"emissao",
				MetaField.label, "Emissão",				
				MetaField.order, ++order,
				MetaField.tableViewWidth, 75,
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
		
//		defineField(
//				className, 
//				"classeOperacaoId",
//				MetaField.order, ++order,
//				MetaField.visible, false
//		);

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
		//-------->>>>>>>
//		defineField( 
//				className,
//				"ordem",
//				MetaField.order, ++order,
//				MetaField.tableViewWidth, 50,
//				MetaField.visible, false
//		);
		
		defineField( 
				className,
				"conta",
				MetaField.order, ++order,
				MetaField.tableViewWidth, 100,
				MetaField.visible, true
		);
		
		defineField( 
				className,
				"nome",
				MetaField.order, ++order,
				MetaField.tableViewWidth, 400,
				MetaField.visible, true
		);
		
		defineField( 
				className,
				"saldoInicial",
				MetaField.order, ++order,
				MetaField.tableViewWidth, 100,
				MetaField.negativeInRed, true,
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
				MetaField.order, ++order,
				MetaField.tableViewWidth, 100,
				MetaField.visible, true
		);
	}

}
