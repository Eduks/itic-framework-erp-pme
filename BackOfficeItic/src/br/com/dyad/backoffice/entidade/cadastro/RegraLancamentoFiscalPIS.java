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
@DiscriminatorValue(value="-89999999999306")
public class RegraLancamentoFiscalPIS extends RegraLancamentoFiscal {
	
	@OneToMany(mappedBy="regraLancamentoFiscalPIS")
	private List<DefinicaoLancamentoFiscalPIS> definicoesLancamentoFiscalPIS;
	
	public static void defineFields(String className) {
		RegraLancamentoFiscal.defineFields(className);
		
		defineField(
				className,				
				"definicoesLancamentoFiscalPIS",
				MetaField.order, 2460,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "regraLancamentoFiscalPIS",
				MetaField.beanName, DefinicaoLancamentoFiscalPIS.class.getName(),
				MetaField.label, "Definições de Regras Fiscais para PIS"
		);
	}

	public List<DefinicaoLancamentoFiscalPIS> getDefinicoesLancamentoFiscalPIS() {
		return definicoesLancamentoFiscalPIS;
	}

	public void setDefinicoesLancamentoFiscalPIS(
			List<DefinicaoLancamentoFiscalPIS> definicoesLancamentoFiscalPIS) {
		this.definicoesLancamentoFiscalPIS = definicoesLancamentoFiscalPIS;
	}
}
