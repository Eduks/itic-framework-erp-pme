package br.com.dyad.backoffice.entidade.movimentacao;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.dyad.infrastructure.entity.BaseEntity;

@Entity
@Table(name="CONTABIL")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999178")
public abstract class Contabil extends BaseEntity{
	public static void defineFields( String className){
		BaseEntity.defineFields(className);
	}	
}