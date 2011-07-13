package br.com.dyad.backoffice.entidade.cadastro;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.dyad.businessinfrastructure.entidades.tabela.Tabela;

@Entity
@Table(name="TABELA")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999299")
public class TipoDocumentoBaixaPedido extends Tabela {

	public TipoDocumentoBaixaPedido() {
		super();
	}

	public static void defineFields(String className){
		Tabela.defineFields(className);
	}
}
