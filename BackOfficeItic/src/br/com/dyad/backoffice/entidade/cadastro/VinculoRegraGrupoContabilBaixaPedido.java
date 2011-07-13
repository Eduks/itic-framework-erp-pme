package br.com.dyad.backoffice.entidade.cadastro;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="VINCULO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999350")
public class VinculoRegraGrupoContabilBaixaPedido extends VinculoRegraGrupoContabil {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="regraLancamentoContabilBaixaPedidoId")
	private RegraLancamentoContabilBaixaPedido regraLancamentoContabilBaixaPedido;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="grupoLancamentoContabilBaixaPedidoId")
	private GrupoLancamentoContabilBaixaPedido grupoLancamentoContabilBaixaPedido;
	
	public static void defineFields(String className){
		VinculoRegraGrupoContabil.defineFields(className);
		
		defineField( 
				className,
				"grupoLancamentoContabilBaixaPedido",
				MetaField.order, 150,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.required, true,
				MetaField.label, "Grupo Baixa de Pedido",
				MetaField.visible, true,
				MetaField.beanName, GrupoLancamentoContabilBaixaPedido.class.getName()
		);
	}

	public RegraLancamentoContabilBaixaPedido getRegraLancamentoContabilBaixaPedido() {
		return regraLancamentoContabilBaixaPedido;
	}

	public void setRegraLancamentoContabilBaixaPedido(
			RegraLancamentoContabilBaixaPedido regraLancamentoContabilBaixaPedido) {
		this.regraLancamentoContabilBaixaPedido = regraLancamentoContabilBaixaPedido;
	}

	public GrupoLancamentoContabilBaixaPedido getGrupoLancamentoContabilBaixaPedido() {
		return grupoLancamentoContabilBaixaPedido;
	}

	public void setGrupoLancamentoContabilBaixaPedido(
			GrupoLancamentoContabilBaixaPedido grupoLancamentoContabilBaixaPedido) {
		this.grupoLancamentoContabilBaixaPedido = grupoLancamentoContabilBaixaPedido;
	}
}
