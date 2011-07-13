package br.com.dyad.backoffice.entidade.movimentacao;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999940")
public abstract class Financeiro extends Operacao {
	public static void defineFields( String className){
		Operacao.defineFields(className);
	}
}