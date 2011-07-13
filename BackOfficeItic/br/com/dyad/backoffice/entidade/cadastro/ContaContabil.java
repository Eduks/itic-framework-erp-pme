package br.com.dyad.backoffice.entidade.cadastro;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.dyad.businessinfrastructure.entidades.tabela.Tabela;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;

@SuppressWarnings("unused")
@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999193")
public class ContaContabil extends Tabela {	

	
	@ManyToOne
    @JoinColumn(name="planoContaId", nullable=false) 
	private PlanoConta planoConta;
	
	@Column(length=50)
	private String apelido;
	
	public PlanoConta getPlanoConta() {
		return planoConta;
	}

	public void setPlanoConta(PlanoConta planoConta) {
		this.planoConta = planoConta;
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public static void defineFields(String className) {
		Tabela.defineFields(className);		
		
		defineField(
				className,
				"planoConta", 
				MetaField.required, true, 
				MetaField.order, 400,
				MetaField.width, 200,
				MetaField.tableViewWidth, 300,
				MetaField.label, "Plano de Contas",
				MetaField.beanName, PlanoConta.class.getName()
		);		

		defineField(
				className,
				"apelido",				 
				MetaField.order, 500,
				MetaField.width, 100,
				MetaField.tableViewWidth, 100,
				MetaField.label, "Apelido"
		);		
		
	}

}