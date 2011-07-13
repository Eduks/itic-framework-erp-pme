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
@DiscriminatorValue(value="-89999999999305")
public class RegraLancamentoFiscalINSS extends RegraLancamentoFiscal {
	
	@OneToMany(mappedBy="regraLancamentoFiscalINSS")
	private List<DefinicaoLancamentoFiscalINSS> definicoesLancamentoFiscalINSS;
	
	public static void defineFields(String className) {
		RegraLancamentoFiscal.defineFields(className);
		
		defineField(
				className,				
				"definicoesLancamentoFiscalINSS",
				MetaField.order, 2460,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "regraLancamentoFiscalINSS",
				MetaField.beanName, DefinicaoLancamentoFiscalINSS.class.getName(),
				MetaField.label, "Definições de Regras Fiscais para INSS"
		);
	}

	public List<DefinicaoLancamentoFiscalINSS> getDefinicoesLancamentoFiscalINSS() {
		return definicoesLancamentoFiscalINSS;
	}

	public void setDefinicoesLancamentoFiscalINSS(
			List<DefinicaoLancamentoFiscalINSS> definicoesLancamentoFiscalINSS) {
		this.definicoesLancamentoFiscalINSS = definicoesLancamentoFiscalINSS;
	}

}
