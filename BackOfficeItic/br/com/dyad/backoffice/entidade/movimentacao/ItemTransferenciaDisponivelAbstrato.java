package br.com.dyad.backoffice.entidade.movimentacao;

import java.math.BigDecimal;
import java.util.Date;

import br.com.dyad.backoffice.entidade.movimentacao.Financeiro;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.infrastructure.annotations.MetaField;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999392")
public abstract class ItemTransferenciaDisponivelAbstrato extends ItemOperacao {
	@ManyToOne
	@JoinColumn(name="entidadeId")	
	private Entidade entidade;
	
	@Column
	private Date emissao;

	@Column
	private BigDecimal total;

	/*
	 * Getters e Setters
	 */
	public Entidade getEntidade() {
		return entidade;
	}
	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}
	public Date getEmissao() {
		return emissao;
	}
	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getTotal() {
		return total;
	}

	public static void defineFields(String className) {
		Financeiro.defineFields(className);
		
		defineField( 
				className,
				"entidade",
				MetaField.label, "Entidade",
				MetaField.visible, true,
				MetaField.order, 100,
				MetaField.beanName, Entidade.class.getName()
		);
 		
		defineField( 
				className,
				"emissao",
				MetaField.label, "Emissão",
				MetaField.visible, false,
				MetaField.order, 200
		);
 		
		defineField( 
				className,
				"total",
				MetaField.label, "Total",
				MetaField.visible, true,
				MetaField.order, 300
		);
	}
}