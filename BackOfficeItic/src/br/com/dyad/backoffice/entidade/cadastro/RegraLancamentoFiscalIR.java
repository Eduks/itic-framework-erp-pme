package br.com.dyad.backoffice.entidade.cadastro;

import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999310")
public class RegraLancamentoFiscalIR extends RegraLancamentoFiscal {
	
	@OneToMany(mappedBy="regraLancamentoFiscalIR")
	private List<DefinicaoLancamentoFiscalIR> definicoesLancamentoFiscalIR;
	
	public static void defineFields(String className) {
		RegraLancamentoFiscal.defineFields(className);
		
		defineField(
				className,				
				"definicoesLancamentoFiscalIR",
				MetaField.order, 2460,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "regraLancamentoFiscalIR",
				MetaField.beanName, DefinicaoLancamentoFiscalIR.class.getName(),
				MetaField.label, "Definições de Regras Fiscais para IR"
		);
	}

	public List<DefinicaoLancamentoFiscalIR> getDefinicoesLancamentoFiscalIR() {
		return definicoesLancamentoFiscalIR;
	}

	public void setDefinicoesLancamentoFiscalIR(
			List<DefinicaoLancamentoFiscalIR> definicoesLancamentoFiscalIR) {
		this.definicoesLancamentoFiscalIR = definicoesLancamentoFiscalIR;
	}

}
