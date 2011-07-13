package br.com.dyad.backoffice.entidade.movimentacao;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999723")
public class ItemBaixaTitulo extends ItemBaixaTituloAbstrato {
	public static void defineFields( String className){
		ItemBaixaTituloAbstrato.defineFields(className);
	}
}