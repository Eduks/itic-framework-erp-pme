package br.com.dyad.backoffice.entidade.cadastro;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999318")
public class RegraLancamentoFiscal extends RegraLancamentoContabilFiscal {
	
	@ManyToOne
    @JoinColumn(name="tipoPedidoId")
	private TipoPedido tipoPedido;
	
	public static void defineFields (String className) {
		RegraLancamentoContabilFiscal.defineFields(className);
		
		defineField( 
				className,
				"tipoPedido",
				MetaField.label, "Tipo Pedido",
				MetaField.required, true,
				MetaField.order, 200,
				MetaField.beanName, TipoPedido.class.getName()
		);
	}

	public TipoPedido getTipoPedido() {
		return tipoPedido;
	}

	public void setTipoPedido(TipoPedido tipoPedido) {
		this.tipoPedido = tipoPedido;
	}
	
}
