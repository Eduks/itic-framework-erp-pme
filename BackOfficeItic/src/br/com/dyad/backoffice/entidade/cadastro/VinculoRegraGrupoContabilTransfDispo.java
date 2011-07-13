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

import org.hibernate.annotations.ForceDiscriminator;

import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="VINCULO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999348")
@ForceDiscriminator
public class VinculoRegraGrupoContabilTransfDispo extends VinculoRegraGrupoContabil {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="regraLancamentoContabilTransfDispoId")
	private RegraLancamentoContabilTransfDispo regraLancamentoContabilTransfDispo;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="grupoLancamentoContabilTransfDispoId")
	private GrupoLancamentoContabilTransfDispo grupoLancamentoContabilTransfDispo;
	
	public static void defineFields(String className){
		VinculoRegraGrupoContabil.defineFields(className);
		
		defineField( 
				className,
				"grupoLancamentoContabilTransfDispo",
				MetaField.order, 150,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.required, true,
				MetaField.label, "Grupo Transferência de Disponível",
				MetaField.visible, true,
				MetaField.beanName, GrupoLancamentoContabilTransfDispo.class.getName()
		);
	}

	public RegraLancamentoContabilTransfDispo getRegraLancamentoContabilTransfDispo() {
		return regraLancamentoContabilTransfDispo;
	}

	public void setRegraLancamentoContabilTransfDispo(
			RegraLancamentoContabilTransfDispo regraLancamentoContabilTransfDispo) {
		this.regraLancamentoContabilTransfDispo = regraLancamentoContabilTransfDispo;
	}

	public GrupoLancamentoContabilTransfDispo getGrupoLancamentoContabilTransfDispo() {
		return grupoLancamentoContabilTransfDispo;
	}

	public void setGrupoLancamentoContabilTransfDispo(
			GrupoLancamentoContabilTransfDispo grupoLancamentoContabilTransfDispo) {
		this.grupoLancamentoContabilTransfDispo = grupoLancamentoContabilTransfDispo;
	}
}
