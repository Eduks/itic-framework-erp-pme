package br.com.dyad.backoffice.entidade.movimentacao;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999942")
public abstract class Operacao extends BaseEntity {
	
	@Column(nullable=false)
	private Long operacaoId;

	@Column(nullable=false)
	private String classeOperacaoId;
	
	@Column
	private Long sinal;

	/*
	 * Getters e Setters
	 */
	public Long getOperacaoId() {
		return operacaoId;
	}

	public void setOperacaoId(Long operacaoId) {
		this.operacaoId = operacaoId;
	}

	public String getClasseOperacaoId() {
		return classeOperacaoId;
	}

	public void setClasseOperacaoId(String classeOperacaoId) {
		this.classeOperacaoId = classeOperacaoId;
	}

	public Long getSinal() {
		return sinal;
	}

	public void setSinal(Long sinal) {
		this.sinal = sinal;
	}

	public static void defineFields( String className){
		BaseEntity.defineFields(className);

		defineField(
				className, 
				"id",
				MetaField.order, 10,
				MetaField.visible, true,
				MetaField.readOnly, true);

		defineField(
				className, 
				"operacaoId",
				MetaField.label, "Operação",				
				MetaField.order, 20,
				MetaField.visible, false);

		defineField(
				className, 
				"classeOperacaoId",
				MetaField.label, "Classe Operação",				
				MetaField.order, 30,
				MetaField.visible, false);

		defineField(
				className, 
				"sinal",
				MetaField.label, "Sinal",				
				MetaField.order, 40,
				MetaField.visible, false);
	}
	
}