package kOffice.br.com.dyad.backoffice.entidade.cadastro;

import br.com.dyad.backoffice.entidade.cadastro.RegraContabil;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999148")
public class RegraContabilBaixaPedido extends RegraContabil {
	public RegraContabilBaixaPedido() {
		super();
	}
}