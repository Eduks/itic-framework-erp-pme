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
@DiscriminatorValue(value="-89999999999307")
public class RegraLancamentoFiscalCSLL extends RegraLancamentoFiscal {
	
	@OneToMany(mappedBy="regraLancamentoFiscalCSLL")
	private List<DefinicaoLancamentoFiscalCSLL> definicoesLancamentoFiscalCSLL;
	
	public static void defineFields(String className) {
		RegraLancamentoFiscal.defineFields(className);
		
		defineField(
				className,				
				"definicoesLancamentoFiscalCSLL",
				MetaField.order, 2460,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "regraLancamentoFiscalCSLL",
				MetaField.beanName, DefinicaoLancamentoFiscalCSLL.class.getName(),
				MetaField.label, "Definições de Regras Fiscais para CSLL"
		);
	}

	public List<DefinicaoLancamentoFiscalCSLL> getDefinicoesLancamentoFiscalCSLL() {
		return definicoesLancamentoFiscalCSLL;
	}

	public void setDefinicoesLancamentoFiscalCSLL(
			List<DefinicaoLancamentoFiscalCSLL> definicoesLancamentoFiscalCSLL) {
		this.definicoesLancamentoFiscalCSLL = definicoesLancamentoFiscalCSLL;
	}

}
