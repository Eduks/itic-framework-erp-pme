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

import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999315")
public class DefinicaoLancamentoFiscalCSLL extends DefinicaoLancamentoFiscal {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="regraLancamentoFiscalCSLLId")
	private RegraLancamentoFiscalCSLL regraLancamentoFiscalCSLL;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorAliquotaRet;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorBaseMinima;
	
	@Column(length=50)
	private String codigoRetDARF;
	
	public static void defineFields(String className){
		DefinicaoLancamentoFiscal.defineFields(className);
		
		defineField(
				className,				
				"valorAliquotaRet",
				MetaField.order, 50,
				MetaField.required, true,
				MetaField.label, "Alíquota Retenção (%)"
		);
		
		defineField(
				className,				
				"valorBaseMinima",
				MetaField.order, 80,
				MetaField.required, true,
				MetaField.label, "Base Mínima"
		);
		
		defineField(
				className,				
				"codigoRetDARF",
				MetaField.order, 90,
				MetaField.required, true,
				MetaField.label, "Cód. Retenção DARF"
		);
		
	}

	public BigDecimal getValorAliquotaRet() {
		return valorAliquotaRet;
	}

	public void setValorAliquotaRet(BigDecimal valorAliquotaRet) {
		this.valorAliquotaRet = valorAliquotaRet;
	}

	public BigDecimal getValorBaseMinima() {
		return valorBaseMinima;
	}

	public void setValorBaseMinima(BigDecimal valorBaseMinima) {
		this.valorBaseMinima = valorBaseMinima;
	}

	public String getCodigoRetDARF() {
		return codigoRetDARF;
	}

	public void setCodigoRetDARF(String codigoRetDARF) {
		this.codigoRetDARF = codigoRetDARF;
	}

	public RegraLancamentoFiscalCSLL getRegraLancamentoFiscalCSLL() {
		return regraLancamentoFiscalCSLL;
	}

	public void setRegraLancamentoFiscalCSLL(
			RegraLancamentoFiscalCSLL regraLancamentoFiscalCSLL) {
		this.regraLancamentoFiscalCSLL = regraLancamentoFiscalCSLL;
	}
	
}
