package br.com.dyad.backoffice.entidade.cadastro;

import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999302")
public abstract class RegraLancamentoContabil extends RegraLancamentoContabilFiscal {
	
	public static void defineFields (String className) {
		RegraLancamentoContabilFiscal.defineFields(className);
	}
	
	public abstract List<?> getVinculosRegraGrupoContabilVigentes(Date emissao);
	
}
