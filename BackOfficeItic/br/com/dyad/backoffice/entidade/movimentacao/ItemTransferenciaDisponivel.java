package br.com.dyad.backoffice.entidade.movimentacao;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.InterfaceItemOperacao;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999191")
public class ItemTransferenciaDisponivel extends ItemTransferenciaDisponivelAbstrato implements InterfaceItemOperacao {

	public static void defineFields(String className) {
		ItemTransferenciaDisponivelAbstrato.defineFields(className);
	}
}