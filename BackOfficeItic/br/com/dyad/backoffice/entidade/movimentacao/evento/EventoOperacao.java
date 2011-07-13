package br.com.dyad.backoffice.entidade.movimentacao.evento;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.dyad.businessinfrastructure.entidades.evento.Evento;
@Entity
@DiscriminatorValue(value="-99999899999924")
@Table(name="EVENTO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
public class EventoOperacao extends Evento {
	
	@Column
	Long operacaoId;

	public Long getOperacaoId() {
		return operacaoId;
	}

	public void setOperacaoId(Long operacaoId) {
		this.operacaoId = operacaoId;
	}

}