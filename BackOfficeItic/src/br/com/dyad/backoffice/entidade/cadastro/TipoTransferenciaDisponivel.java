package br.com.dyad.backoffice.entidade.cadastro;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import br.com.dyad.businessinfrastructure.entidades.tabela.Tabela;
import br.com.dyad.infrastructure.annotations.FindExpress;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999287")
@FindExpress(value=" and ( UPPER(codigo) LIKE ('%' || UPPER('@#searchToken#@') || '%') or UPPER(nome) LIKE ('%' || UPPER('@#searchToken#@') || '%'))")
public class TipoTransferenciaDisponivel extends Tabela {
	public static void defineFields(String className) {
		Tabela.defineFields(className);		
	}
}
