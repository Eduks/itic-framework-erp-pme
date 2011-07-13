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
@DiscriminatorValue(value="-89999999999349")
public class VinculoRegraGrupoContabilBaixaTitulo extends VinculoRegraGrupoContabil {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="regraLancamentoContabilBaixaTituloId")
	private RegraLancamentoContabilBaixaTitulo regraLancamentoContabilBaixaTitulo;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="grupoLancamentoContabilBaixaTituloId")
	private GrupoLancamentoContabilBaixaTitulo grupoLancamentoContabilBaixaTitulo;
	
	public static void defineFields(String className){
		VinculoRegraGrupoContabil.defineFields(className);
		
		defineField( 
				className,
				"grupoLancamentoContabilBaixaTitulo",
				MetaField.order, 150,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.required, true,
				MetaField.label, "Grupo Baixa de Título",
				MetaField.visible, true,
				MetaField.beanName, GrupoLancamentoContabilBaixaTitulo.class.getName()
		);
	}

	public RegraLancamentoContabilBaixaTitulo getRegraLancamentoContabilBaixaTitulo() {
		return regraLancamentoContabilBaixaTitulo;
	}

	public void setRegraLancamentoContabilBaixaTitulo(
			RegraLancamentoContabilBaixaTitulo regraLancamentoContabilBaixaTitulo) {
		this.regraLancamentoContabilBaixaTitulo = regraLancamentoContabilBaixaTitulo;
	}

	public GrupoLancamentoContabilBaixaTitulo getGrupoLancamentoContabilBaixaTitulo() {
		return grupoLancamentoContabilBaixaTitulo;
	}

	public void setGrupoLancamentoContabilBaixaTitulo(
			GrupoLancamentoContabilBaixaTitulo grupoLancamentoContabilBaixaTitulo) {
		this.grupoLancamentoContabilBaixaTitulo = grupoLancamentoContabilBaixaTitulo;
	}
}
