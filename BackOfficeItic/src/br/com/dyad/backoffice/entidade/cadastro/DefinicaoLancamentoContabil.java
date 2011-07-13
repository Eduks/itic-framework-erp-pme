package br.com.dyad.backoffice.entidade.cadastro;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.businessinfrastructure.entidades.vinculo.Vinculo;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="VINCULO")
@DiscriminatorValue(value="-89999999999338")
public abstract class DefinicaoLancamentoContabil extends Vinculo {
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date inicio;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date fim;
	
	@Column(length=25)
	private String sinal;
	
	@ManyToOne
	@JoinColumn(name="tipoLancamentoId", nullable=true)
	private TipoLancamento tipoLancamento;
	
	@Column
	private String codigoFormulaData;
	
	@ManyToOne
	@JoinColumn(name="planoContaId", nullable=true)
	private PlanoConta planoConta;
	
	@Column
	private String codigoFormulaConta;
	
	@Column(length=25)
	private String tamSufixoConta;
	
	@Column(length=25)
	private String prefixoConta;
	
	@Column(length=25)
	private String sufixoConta;
	
	@Column
	private String codigoFormulaEntidade;
	
	@Column
	private String codigoFormulaHistorico;
	
	@Column(length=25)
	private String prefixoHistorico;
	
	@Column(length=25)
	private String sufixoHistorico;
	
	@Column
	private String codigoFormulaValor;
	
	@Column(precision=3,scale=2)
	private BigDecimal fatorMultiplicacaoValor;
	
	public static void defineFields(String className) {
		Vinculo.defineFields(className);
		
		defineField( 
				className,
				"inicio",
				MetaField.order, 50,
				MetaField.width, 150,
				MetaField.required, true,
				MetaField.label, "Início"
		);
		
		defineField( 
				className,
				"fim",
				MetaField.order, 100,
				MetaField.width, 150,
				MetaField.label, "Fim"
		);
		
		defineField( 
				className,
				"sinal",
				MetaField.order, 150,
				MetaField.width, 150,
				MetaField.required, true,
				MetaField.label, "Sinal"
		);
		
		defineField( 
				className,
				"tipoLancamento",
				MetaField.order, 200,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.required, true,
				MetaField.label, "Tipo Lançamento",
				MetaField.beanName, TipoLancamento.class.getName()
		);
		
		defineField( 
				className,
				"codigoFormulaData",
				MetaField.order, 250,
				MetaField.width, 150,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Data"
		);
		
		defineField( 
				className,
				"planoConta",
				MetaField.order, 300,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.required, true,
				MetaField.label, "Plano de Contas",
				MetaField.beanName, PlanoConta.class.getName()
		);
		
		defineField( 
				className,
				"codigoFormulaConta",
				MetaField.order, 350,
				MetaField.width, 150,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Conta"
		);
		
		defineField( 
				className,
				"tamSufixoConta",
				MetaField.order, 400,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.label, "Tam. Sufixo Conta"
		);
		
		defineField( 
				className,
				"prefixoConta",
				MetaField.order, 450,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.label, "Prefixo da Conta"
		);
		
		defineField( 
				className,
				"sufixoConta",
				MetaField.order, 500,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.label, "Sufixo da Conta"
		);
		
		defineField( 
				className,
				"codigoFormulaEntidade",
				MetaField.order, 550,
				MetaField.width, 150,
				MetaField.label, "Fórmula Para Entidade"
		);
		
		defineField( 
				className,
				"codigoFormulaHistorico",
				MetaField.order, 600,
				MetaField.width, 150,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Histórico"
		);
		
		defineField( 
				className,
				"prefixoHistorico",
				MetaField.order, 650,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.label, "Prefixo do Histórico"
		);
		
		defineField( 
				className,
				"sufixoHistorico",
				MetaField.order, 700,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.label, "Sufixo do Histórico"
		);
		
		defineField( 
				className,
				"codigoFormulaValor",
				MetaField.order, 750,
				MetaField.width, 150,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Valor"
		);
		
		defineField( 
				className,
				"fatorMultiplicacaoValor",
				MetaField.order, 1000,
				MetaField.width, 150,
				MetaField.label, "Fator de Multiplicação do Valor"
		);
		
	}
	
	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public TipoLancamento getTipoLancamento() {
		return tipoLancamento;
	}

	public void setTipoLancamento(TipoLancamento tipoLancamento) {
		this.tipoLancamento = tipoLancamento;
	}

	public PlanoConta getPlanoConta() {
		return planoConta;
	}

	public void setPlanoConta(PlanoConta planoConta) {
		this.planoConta = planoConta;
	}

	public String getTamSufixoConta() {
		return tamSufixoConta;
	}

	public void setTamSufixoConta(String tamSufixoConta) {
		this.tamSufixoConta = tamSufixoConta;
	}

	public String getPrefixoConta() {
		return prefixoConta;
	}

	public void setPrefixoConta(String prefixoConta) {
		this.prefixoConta = prefixoConta;
	}

	public String getSufixoConta() {
		return sufixoConta;
	}

	public void setSufixoConta(String sufixoConta) {
		this.sufixoConta = sufixoConta;
	}

	public String getPrefixoHistorico() {
		return prefixoHistorico;
	}

	public void setPrefixoHistorico(String prefixoHistorico) {
		this.prefixoHistorico = prefixoHistorico;
	}

	public String getSufixoHistorico() {
		return sufixoHistorico;
	}

	public void setSufixoHistorico(String sufixoHistorico) {
		this.sufixoHistorico = sufixoHistorico;
	}

	public BigDecimal getFatorMultiplicacaoValor() {
		return fatorMultiplicacaoValor;
	}

	public void setFatorMultiplicacaoValor(BigDecimal fatorMultiplicacaoValor) {
		this.fatorMultiplicacaoValor = fatorMultiplicacaoValor;
	}

	public String getSinal() {
		return sinal;
	}

	public void setSinal(String sinal) {
		this.sinal = sinal;
	}

	public String getCodigoFormulaData() {
		return codigoFormulaData;
	}

	public void setCodigoFormulaData(String codigoFormulaData) {
		this.codigoFormulaData = codigoFormulaData;
	}

	public String getCodigoFormulaConta() {
		return codigoFormulaConta;
	}

	public void setCodigoFormulaConta(String codigoFormulaConta) {
		this.codigoFormulaConta = codigoFormulaConta;
	}

	public String getCodigoFormulaEntidade() {
		return codigoFormulaEntidade;
	}

	public void setCodigoFormulaEntidade(String codigoFormulaEntidade) {
		this.codigoFormulaEntidade = codigoFormulaEntidade;
	}

	public String getCodigoFormulaHistorico() {
		return codigoFormulaHistorico;
	}

	public void setCodigoFormulaHistorico(String codigoFormulaHistorico) {
		this.codigoFormulaHistorico = codigoFormulaHistorico;
	}

	public String getCodigoFormulaValor() {
		return codigoFormulaValor;
	}

	public void setCodigoFormulaValor(String codigoFormulaValor) {
		this.codigoFormulaValor = codigoFormulaValor;
	}

}
