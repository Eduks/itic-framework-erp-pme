package br.com.dyad.backoffice.entidade.cadastro;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.dyad.businessinfrastructure.entidades.vinculo.Vinculo;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="VINCULO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999303")
public class DefinicaoLancamentoFiscalBaseAliquotaIR extends Vinculo {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="definicaoLancamentoFiscalIRId")
	private DefinicaoLancamentoFiscalIR definicaoLancamentoFiscalIR;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorBaseMinima;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorBaseMaxima;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorAliquotaRet;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorDeducaoFaixa;
	
	public static void defineFields (String className) {
		Vinculo.defineFields(className);
		
		defineField(
				className,				
				"valorBaseMinima",
				MetaField.order, 10,
				MetaField.required, true,
				MetaField.label, "Base Mínima"
		);
		
		defineField(
				className,				
				"valorBaseMaxima",
				MetaField.order, 20,
				MetaField.required, true,
				MetaField.label, "Base Máxima"
		);
		
		defineField(
				className,				
				"valorAliquotaRet",
				MetaField.order, 30,
				MetaField.required, true,
				MetaField.label, "Alíquota Retenção (%)"
		);
		
		defineField(
				className,				
				"valorDeducaoFaixa",
				MetaField.order, 40,
				MetaField.required, true,
				MetaField.label, "Vr. Dedução p/ Faixa"
		);
	}

	public BigDecimal getValorBaseMinima() {
		return valorBaseMinima;
	}

	public void setValorBaseMinima(BigDecimal valorBaseMinima) {
		this.valorBaseMinima = valorBaseMinima;
	}

	public BigDecimal getValorBaseMaxima() {
		return valorBaseMaxima;
	}

	public void setValorBaseMaxima(BigDecimal valorBaseMaxima) {
		this.valorBaseMaxima = valorBaseMaxima;
	}

	public BigDecimal getValorAliquotaRet() {
		return valorAliquotaRet;
	}

	public void setValorAliquotaRet(BigDecimal valorAliquotaRet) {
		this.valorAliquotaRet = valorAliquotaRet;
	}

	public BigDecimal getValorDeducaoFaixa() {
		return valorDeducaoFaixa;
	}

	public void setValorDeducaoFaixa(BigDecimal valorDeducaoFaixa) {
		this.valorDeducaoFaixa = valorDeducaoFaixa;
	}

	public DefinicaoLancamentoFiscalIR getVinculoRegraDefinicaoFiscalIR() {
		return definicaoLancamentoFiscalIR;
	}

	public void setVinculoRegraDefinicaoFiscalIR(
			DefinicaoLancamentoFiscalIR definicaoLancamentoFiscalIR) {
		this.definicaoLancamentoFiscalIR = definicaoLancamentoFiscalIR;
	}
	
}
