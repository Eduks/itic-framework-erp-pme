package br.com.dyad.backoffice.entidade.movimentacao;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999391")
public abstract class ItemOperacaoAbstrato extends Operacao implements Item {
	public static void defineFields( String className){
		Operacao.defineFields(className);
	}
}
