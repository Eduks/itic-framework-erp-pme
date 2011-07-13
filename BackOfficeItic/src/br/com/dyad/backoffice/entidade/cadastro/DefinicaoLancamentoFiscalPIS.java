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
@DiscriminatorValue(value="-89999999999311")
public class DefinicaoLancamentoFiscalPIS extends DefinicaoLancamentoFiscal {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="regraLancamentoFiscalPISId")
	private RegraLancamentoFiscalPIS regraLancamentoFiscalPIS;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorAliquota;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorAliquotaST;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorAliquotaRet;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorPauta;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorPautaST;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorBaseMinima;
	
	@Column(length=50)
	private String codigoRetDARF;
	
	@Column(length=50)
	private String situacaoTributaria;
	
	public static void defineFields(String className){
		DefinicaoLancamentoFiscal.defineFields(className);
		
		defineField(
				className,				
				"valorAliquota",
				MetaField.order, 30,
				MetaField.label, "Alíquota (%)"
		);

		defineField(
				className,				
				"valorAliquotaST",
				MetaField.order, 40,
				MetaField.required, true,
				MetaField.label, "Alíquota ST (%)"
		);
		
		defineField(
				className,				
				"valorAliquotaRet",
				MetaField.order, 50,
				MetaField.required, true,
				MetaField.label, "Alíquota Retenção (%)"
		);
		
		defineField(
				className,				
				"valorPauta",
				MetaField.order, 60,
				MetaField.label, "Pauta"
		);
		
		defineField(
				className,				
				"valorPautaST",
				MetaField.order, 70,
				MetaField.label, "Pauta ST"
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
		
		defineField(
				className,				
				"situacaoTributaria",
				MetaField.order, 100,
				MetaField.label, "Situação Tributária"
		);
		
	}

	public RegraLancamentoFiscalPIS getRegraLancamentoFiscalPIS() {
		return regraLancamentoFiscalPIS;
	}

	public void setRegraLancamentoFiscalPIS(
			RegraLancamentoFiscalPIS regraLancamentoFiscalPIS) {
		this.regraLancamentoFiscalPIS = regraLancamentoFiscalPIS;
	}

	public BigDecimal getValorAliquota() {
		return valorAliquota;
	}

	public void setValorAliquota(BigDecimal valorAliquota) {
		this.valorAliquota = valorAliquota;
	}

	public BigDecimal getValorAliquotaST() {
		return valorAliquotaST;
	}

	public void setValorAliquotaST(BigDecimal valorAliquotaST) {
		this.valorAliquotaST = valorAliquotaST;
	}

	public BigDecimal getValorAliquotaRet() {
		return valorAliquotaRet;
	}

	public void setValorAliquotaRet(BigDecimal valorAliquotaRet) {
		this.valorAliquotaRet = valorAliquotaRet;
	}

	public BigDecimal getValorPauta() {
		return valorPauta;
	}

	public void setValorPauta(BigDecimal valorPauta) {
		this.valorPauta = valorPauta;
	}

	public BigDecimal getValorPautaST() {
		return valorPautaST;
	}

	public void setValorPautaST(BigDecimal valorPautaST) {
		this.valorPautaST = valorPautaST;
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

	public String getSituacaoTributaria() {
		return situacaoTributaria;
	}

	public void setSituacaoTributaria(String situacaoTributaria) {
		this.situacaoTributaria = situacaoTributaria;
	}
	
}
