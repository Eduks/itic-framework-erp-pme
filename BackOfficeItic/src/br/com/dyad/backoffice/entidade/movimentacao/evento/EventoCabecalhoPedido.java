package br.com.dyad.backoffice.entidade.movimentacao.evento;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="EVENTO")
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999922")
public class EventoCabecalhoPedido extends EventoCabecalhoOperacao {

}