package br.com.dyad.backoffice.entidade.movimentacao;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ForceDiscriminator;

import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999942")
@ForceDiscriminator
public abstract class Operacao extends BaseEntity {
	
	@Column(nullable=false)
	private String classeOperacaoId;
	
	/*
	 * Getters e Setters
	 */
	public String getClasseOperacaoId() {
		return classeOperacaoId;
	}

	public void setClasseOperacaoId(String classeOperacaoId) {
		this.classeOperacaoId = classeOperacaoId;
	}

	public static void defineFields( String className){
		BaseEntity.defineFields(className);

		defineField(
				className, 
				"classeOperacaoId",
				MetaField.label, "Classe Operação",				
				MetaField.visible, false
		);
	}
}