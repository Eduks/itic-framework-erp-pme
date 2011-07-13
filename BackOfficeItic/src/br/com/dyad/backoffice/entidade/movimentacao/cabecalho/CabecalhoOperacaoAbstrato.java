package br.com.dyad.backoffice.entidade.movimentacao.cabecalho;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.dyad.backoffice.entidade.movimentacao.Operacao;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999381")
public abstract class CabecalhoOperacaoAbstrato extends Operacao implements InterfaceCabecalhoOperacao {
	public static void defineFields(String className) {
		Operacao.defineFields(className);
	}
}
