package br.com.dyad.backoffice.entidade.movimentacao;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Item;

@Entity
@Table(name="CONTABIL")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999372")
public abstract class ItemContabilAbstrato extends Contabil implements Item {
	public static void defineFields( String className){
		Contabil.defineFields(className);
	}
}
