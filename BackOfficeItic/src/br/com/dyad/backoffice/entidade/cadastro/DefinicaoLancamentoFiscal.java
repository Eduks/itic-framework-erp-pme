package br.com.dyad.backoffice.entidade.cadastro;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.businessinfrastructure.entidades.vinculo.Vinculo;
import br.com.dyad.client.widget.field.FieldTypes;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999317")
public class DefinicaoLancamentoFiscal extends Vinculo {
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date inicio;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date fim;
	
	@Column
	@Lob
	private String observacao;
	
	public static void defineFields (String className) {
		Vinculo.defineFields(className);
		
		defineField(
				className,				
				"inicio",
				MetaField.order, 10,
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.label, "Início"
		);

		defineField(
				className,				
				"fim",
				MetaField.order, 20,
				MetaField.visible, true,
				MetaField.required, false,
				MetaField.label, "Fim"
		);
		
		defineField( 
				className,
				"observacao",
				MetaField.order, 500,
				MetaField.width, 300,
				MetaField.tableViewWidth, 200,
				MetaField.height, 3,
				MetaField.fieldType, FieldTypes.FIELD_TYPE_MEMO,
				MetaField.label, "Observações"
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

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
}
