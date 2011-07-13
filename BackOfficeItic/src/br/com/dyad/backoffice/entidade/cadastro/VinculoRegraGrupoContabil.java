package br.com.dyad.backoffice.entidade.cadastro;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.businessinfrastructure.entidades.vinculo.Vinculo;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="VINCULO")
@DiscriminatorValue(value="-89999999999342")
public abstract class VinculoRegraGrupoContabil extends Vinculo {
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date inicio;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date fim;
	
	public static void defineFields (String className) {
		Vinculo.defineFields(className);
		
		defineField(
				className,				
				"inicio",
				MetaField.order, 220,
				MetaField.visible, true,
				MetaField.required, true
		);

		defineField(
				className,				
				"fim",
				MetaField.order, 230,
				MetaField.visible, true,
				MetaField.required, false 
		);
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}
	
}
