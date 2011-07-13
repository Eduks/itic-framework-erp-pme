package br.com.dyad.backoffice.entidade.movimentacao.evento;

import java.math.BigDecimal;

import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoCabecalhoPedido;

import javax.persistence.Column;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;

@Entity
@Table(name="EVENTO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999163")
public class EventoCabecalhoPedidoCSLL extends EventoCabecalhoPedido {

	public EventoCabecalhoPedidoCSLL() {
		this.setClassId(this.getClass().getAnnotation(DiscriminatorValue.class).value());
	}
	
	@Column(precision=11,scale=2)
	BigDecimal valor;

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
}