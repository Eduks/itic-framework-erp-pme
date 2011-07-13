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
@DiscriminatorValue(value="-89999999999314")
public class DefinicaoLancamentoFiscalINSS extends DefinicaoLancamentoFiscal {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="regraLancamentoFiscalINSSId")
	private RegraLancamentoFiscalINSS regraLancamentoFiscalINSS;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorAliquotaRet;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorRedBaseCalculo;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorAliquotaINSSEmpresa;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorMinimoRet;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorMaximoRet;
	
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
				"valorRedBaseCalculo",
				MetaField.order, 60,
				MetaField.label, "Red. Base Cálculo (%)"
		);
		
		defineField(
				className,				
				"valorAliquotaINSSEmpresa",
				MetaField.order, 70,
				MetaField.label, "Alíquota INSS Empresa"
		);
		
		defineField(
				className,				
				"valorMinimoRet",
				MetaField.order, 80,
				MetaField.label, "Vr. Mínimo p/ Retenção"
		);
		
		defineField(
				className,				
				"valorMaximoRet",
				MetaField.order, 90,
				MetaField.label, "Vr. Teto p/ Retenção"
		);
		
	}

	public RegraLancamentoFiscalINSS getRegraLancamentoFiscalINSS() {
		return regraLancamentoFiscalINSS;
	}

	public void setRegraLancamentoFiscalINSS(
			RegraLancamentoFiscalINSS regraLancamentoFiscalINSS) {
		this.regraLancamentoFiscalINSS = regraLancamentoFiscalINSS;
	}

	public BigDecimal getValorAliquotaRet() {
		return valorAliquotaRet;
	}

	public void setValorAliquotaRet(BigDecimal valorAliquotaRet) {
		this.valorAliquotaRet = valorAliquotaRet;
	}

	public BigDecimal getValorRedBaseCalculo() {
		return valorRedBaseCalculo;
	}

	public void setValorRedBaseCalculo(BigDecimal valorRedBaseCalculo) {
		this.valorRedBaseCalculo = valorRedBaseCalculo;
	}

	public BigDecimal getValorAliquotaINSSEmpresa() {
		return valorAliquotaINSSEmpresa;
	}

	public void setValorAliquotaINSSEmpresa(BigDecimal valorAliquotaINSSEmpresa) {
		this.valorAliquotaINSSEmpresa = valorAliquotaINSSEmpresa;
	}

	public BigDecimal getValorMinimoRet() {
		return valorMinimoRet;
	}

	public void setValorMinimoRet(BigDecimal valorMinimoRet) {
		this.valorMinimoRet = valorMinimoRet;
	}

	public BigDecimal getValorMaximoRet() {
		return valorMaximoRet;
	}

	public void setValorMaximoRet(BigDecimal valorMaximoRet) {
		this.valorMaximoRet = valorMaximoRet;
	}
	
}
