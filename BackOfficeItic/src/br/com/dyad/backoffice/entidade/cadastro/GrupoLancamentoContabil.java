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

import br.com.dyad.businessinfrastructure.entidades.tabela.Tabela;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999353")
public abstract class GrupoLancamentoContabil extends Tabela {
	
	public static void defineFields(String className) {
		Tabela.defineFields(className);
		
		defineField(
				className,
				"codigo",
				MetaField.visible, true,
				MetaField.required, true, 
				MetaField.order, 10,
				MetaField.width, 100
		);		

		defineField(
				className,
				"nome",
				MetaField.visible, true,
				MetaField.required, true, 
				MetaField.order, 20,
				MetaField.width, 200,
				MetaField.tableViewWidth, 500
		);
	}
	
	public abstract List<?> getDefinicoesVigentes(Date emissao);
}
