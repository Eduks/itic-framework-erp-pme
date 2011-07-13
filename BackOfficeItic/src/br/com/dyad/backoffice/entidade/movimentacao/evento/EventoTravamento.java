package br.com.dyad.backoffice.entidade.movimentacao.evento;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.businessinfrastructure.entidades.evento.Evento;
import br.com.dyad.client.widget.field.FieldTypes;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="EVENTO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999185")
public class EventoTravamento extends Evento {
	
	@ManyToOne
    @JoinColumn(name="planoContaId")
	private PlanoConta planoConta;
	
	@Column
	@Lob
	private String observacao;
	
	/**
	 * Constructor
	 */
	public EventoTravamento() {
		super();
	}

	public PlanoConta getPlanoConta() {
		return planoConta;
	}

	public void setPlanoConta(PlanoConta planoConta) {
		this.planoConta = planoConta;
	}
	
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public static void defineFields(String className) {
		Evento.defineFields(className);
		
		defineField(
			className,
			"planoConta",
			MetaField.label, "Plano de Contas",
			MetaField.order, 300,
			MetaField.beanName, PlanoConta.class.getName(),
			MetaField.required, true
		);
		
		defineField(
			className,				
			"observacao",
			MetaField.label, "Observação",
			MetaField.order, 400,
			MetaField.required, true,
			MetaField.column, 0,
			MetaField.fieldType, FieldTypes.FIELD_TYPE_MEMO,
			MetaField.tableViewWidth, 350,
			MetaField.width, 400,
			MetaField.height, 4 
		);
	}
}