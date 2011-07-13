package br.com.dyad.backoffice.entidade.movimentacao.evento;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="EVENTO")
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999919")
public class EventoCabecalhoPedidoDesconto extends EventoCabecalhoPedido {

	public EventoCabecalhoPedidoDesconto() {
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