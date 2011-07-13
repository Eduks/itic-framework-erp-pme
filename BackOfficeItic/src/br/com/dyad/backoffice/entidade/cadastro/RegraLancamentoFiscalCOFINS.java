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

import br.com.dyad.infrastructure.annotations.FindExpress;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999308")
@FindExpress(value=" and ( UPPER(codigo) LIKE ('%' || UPPER('@#searchToken#@') || '%') or UPPER(nome) LIKE ('%' || UPPER('@#searchToken#@') || '%'))")
public class RegraLancamentoFiscalCOFINS extends RegraLancamentoFiscal {
	
	@OneToMany(mappedBy="regraLancamentoFiscalCOFINS")
	private List<DefinicaoLancamentoFiscalCOFINS> definicoesLancamentoFiscalCOFINS;
	
	public static void defineFields(String className) {
		RegraLancamentoFiscal.defineFields(className);
		
		defineField(
				className,				
				"definicoesLancamentoFiscalCOFINS",
				MetaField.order, 2460,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "regraLancamentoFiscalCOFINS",
				MetaField.beanName, DefinicaoLancamentoFiscalCOFINS.class.getName(),
				MetaField.label, "Definições de Regras Fiscais para COFINS"
		);
	}

	public List<DefinicaoLancamentoFiscalCOFINS> getDefinicoesLancamentoFiscalCOFINS() {
		return definicoesLancamentoFiscalCOFINS;
	}

	public void setDefinicoesLancamentoFiscalCOFINS(
			List<DefinicaoLancamentoFiscalCOFINS> definicoesLancamentoFiscalCOFINS) {
		this.definicoesLancamentoFiscalCOFINS = definicoesLancamentoFiscalCOFINS;
	}

}
