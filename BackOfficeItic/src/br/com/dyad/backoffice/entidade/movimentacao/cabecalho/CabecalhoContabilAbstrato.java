package br.com.dyad.backoffice.entidade.movimentacao.cabecalho;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.dyad.backoffice.entidade.movimentacao.Contabil;

@Entity
@Table(name="CONTABIL")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999373")
public abstract class CabecalhoContabilAbstrato extends Contabil implements InterfaceCabecalhoContabil {
	public static void defineFields(String className) {
		Contabil.defineFields(className);
	}
}

