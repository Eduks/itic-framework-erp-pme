package br.com.dyad.backoffice.entidade.cadastro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForceDiscriminator;

import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999347")
@ForceDiscriminator
public class RegraLancamentoContabilTransfDispo extends RegraLancamentoContabil {
	
//	@JoinColumn(name="regraLancamentoContabilTransfDispoId")
	@OneToMany(mappedBy="regraLancamentoContabilTransfDispo", fetch=FetchType.LAZY)
	private List<VinculoRegraGrupoContabilTransfDispo> vinculosRegraGrupoContabilTransfDispo;
	
	/**
	 * Constructor
	 */
	public RegraLancamentoContabilTransfDispo() {
		super();
	}

	public static void defineFields(String className) {
		RegraLancamentoContabilFiscal.defineFields(className);
		
		defineField(
				className,				
				"codigo",
				MetaField.visible, false
		);
		
		defineField(
				className,				
				"vinculosRegraGrupoContabilTransfDispo",
				MetaField.order, 2460,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "regraLancamentoContabilTransfDispo",
				MetaField.beanName, VinculoRegraGrupoContabilTransfDispo.class.getName(),
				MetaField.label, "Grupos de Lançamento Contábil para Transf. de Disponível"
		);
		
	}

	@Override
	public List<VinculoRegraGrupoContabilTransfDispo> getVinculosRegraGrupoContabilVigentes(Date emissao) {
		
		List<VinculoRegraGrupoContabilTransfDispo> vinculosVigentes = new ArrayList<VinculoRegraGrupoContabilTransfDispo>(0);
		
		Date inicio, fim;
		
		for (VinculoRegraGrupoContabilTransfDispo vinculo : vinculosRegraGrupoContabilTransfDispo) {
			
			inicio = vinculo.getInicio();
			
			fim = vinculo.getFim();
			if (fim == null) {
				fim = new Date();
			}
			
			if (inicio.compareTo(emissao) <= 0 && fim.compareTo(emissao) >= 0) {
				vinculosVigentes.add(vinculo);
			}
		}
		
		return vinculosVigentes;
	}

	public List<VinculoRegraGrupoContabilTransfDispo> getVinculosRegraGrupoContabilTransfDispo() {
		return vinculosRegraGrupoContabilTransfDispo;
	}

	public void setVinculosRegraGrupoContabilTransfDispo(
			List<VinculoRegraGrupoContabilTransfDispo> vinculosRegraGrupoContabilTransfDispo) {
		this.vinculosRegraGrupoContabilTransfDispo = vinculosRegraGrupoContabilTransfDispo;
	}
}