package br.com.dyad.backoffice.entidade.cadastro;

import java.math.BigDecimal;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999313")
public class DefinicaoLancamentoFiscalIR extends DefinicaoLancamentoFiscal {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="regraLancamentoFiscalIRId")
	private RegraLancamentoFiscalIR regraLancamentoFiscalIR;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorDeducaoDependente;
	
	@Column(precision=11,scale=2)
	private BigDecimal valorMinimoRet;
	
	@Column
	private Boolean deduzINSSRetido;
	
	@Column(length=50)
	private String codigoRetDARF;
	
	@OneToMany(mappedBy="definicaoLancamentoFiscalIR")
	private List<DefinicaoLancamentoFiscalBaseAliquotaIR> definicoesLancamentoFiscalIR;
	
	public static void defineFields(String className){
		DefinicaoLancamentoFiscal.defineFields(className);
		
		defineField(
				className,				
				"valorDeducaoDependente",
				MetaField.order, 40,
				MetaField.required, true,
				MetaField.label, "Vr. Dedução p/ Dependente"
		);
		
		defineField(
				className,				
				"valorMinimoRet",
				MetaField.order, 50,
				MetaField.required, true,
				MetaField.label, "Vr. Mínimo p/ Retenção"
		);
		
		defineField(
				className,				
				"deduzINSSRetido",
				MetaField.order, 60,
				MetaField.label, "Deduz INSS Retido?"
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
				"definicoesLancamentoFiscalIR",
				MetaField.order, 2460,
				MetaField.width, 300,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "vinculoRegraDefinicaoFiscalIR",
				MetaField.beanName, DefinicaoLancamentoFiscalBaseAliquotaIR.class.getName(),
				MetaField.label, "Bases e Alíquotas de IR"
		);
		
	}

	public RegraLancamentoFiscalIR getRegraLancamentoFiscalIR() {
		return regraLancamentoFiscalIR;
	}

	public void setRegraLancamentoFiscalIR(
			RegraLancamentoFiscalIR regraLancamentoFiscalIR) {
		this.regraLancamentoFiscalIR = regraLancamentoFiscalIR;
	}

	public BigDecimal getValorDeducaoDependente() {
		return valorDeducaoDependente;
	}

	public void setValorDeducaoDependente(BigDecimal valorDeducaoDependente) {
		this.valorDeducaoDependente = valorDeducaoDependente;
	}

	public BigDecimal getValorMinimoRet() {
		return valorMinimoRet;
	}

	public void setValorMinimoRet(BigDecimal valorMinimoRet) {
		this.valorMinimoRet = valorMinimoRet;
	}

	public Boolean getDeduzINSSRetido() {
		return deduzINSSRetido;
	}

	public void setDeduzINSSRetido(Boolean deduzINSSRetido) {
		this.deduzINSSRetido = deduzINSSRetido;
	}

	public String getCodigoRetDARF() {
		return codigoRetDARF;
	}

	public void setCodigoRetDARF(String codigoRetDARF) {
		this.codigoRetDARF = codigoRetDARF;
	}

	public List<DefinicaoLancamentoFiscalBaseAliquotaIR> getDefinicoesLancamentoFiscalIR() {
		return definicoesLancamentoFiscalIR;
	}

	public void setDefinicoesLancamentoFiscalIR(
			List<DefinicaoLancamentoFiscalBaseAliquotaIR> definicoesLancamentoFiscalIR) {
		this.definicoesLancamentoFiscalIR = definicoesLancamentoFiscalIR;
	}

}
