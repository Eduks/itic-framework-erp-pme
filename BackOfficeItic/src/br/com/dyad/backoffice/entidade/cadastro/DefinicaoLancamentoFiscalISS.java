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
@DiscriminatorValue(value="-89999999999312")
public class DefinicaoLancamentoFiscalISS extends DefinicaoLancamentoFiscal {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="regraLancamentoFiscalISSId")
	private RegraLancamentoFiscalISS regraLancamentoFiscalISS;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorAliquota;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorAliquotaRet;
	
	@Column(length=50)
	private String codigoMuniNatOperacao;
	
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
				"valorAliquotaRet",
				MetaField.order, 50,
				MetaField.label, "Alíquota Retenção (%)"
		);
		
		defineField(
				className,				
				"codigoMuniNatOperacao",
				MetaField.order, 90,
				MetaField.required, true,
				MetaField.label, "Cód. Munic. Nat. Operação"
		);
		
	}

	public RegraLancamentoFiscalISS getRegraLancamentoFiscalISS() {
		return regraLancamentoFiscalISS;
	}

	public void setRegraLancamentoFiscalISS(
			RegraLancamentoFiscalISS regraLancamentoFiscalISS) {
		this.regraLancamentoFiscalISS = regraLancamentoFiscalISS;
	}

	public BigDecimal getValorAliquota() {
		return valorAliquota;
	}

	public void setValorAliquota(BigDecimal valorAliquota) {
		this.valorAliquota = valorAliquota;
	}

	public BigDecimal getValorAliquotaRet() {
		return valorAliquotaRet;
	}

	public void setValorAliquotaRet(BigDecimal valorAliquotaRet) {
		this.valorAliquotaRet = valorAliquotaRet;
	}

	public String getCodigoMuniNatOperacao() {
		return codigoMuniNatOperacao;
	}

	public void setCodigoMuniNatOperacao(String codigoMuniNatOperacao) {
		this.codigoMuniNatOperacao = codigoMuniNatOperacao;
	}
	
}
