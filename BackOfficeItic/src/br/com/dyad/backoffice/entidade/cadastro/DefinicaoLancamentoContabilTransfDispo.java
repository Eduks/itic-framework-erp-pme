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

import org.hibernate.annotations.ForceDiscriminator;

import br.com.dyad.backoffice.formula.FormulaContaTransfDispo;
import br.com.dyad.backoffice.formula.FormulaDataTransfDispo;
import br.com.dyad.backoffice.formula.FormulaEntidadeTransfDispo;
import br.com.dyad.backoffice.formula.FormulaHistoricoTransfDispo;
import br.com.dyad.backoffice.formula.FormulaUtil;
import br.com.dyad.backoffice.formula.FormulaValorTransfDispo;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="VINCULO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999336")
@ForceDiscriminator
public class DefinicaoLancamentoContabilTransfDispo extends DefinicaoLancamentoContabil {
	
	@ManyToOne
	@JoinColumn(name="grupoLancamentoContabilTransfDispoId")
	private GrupoLancamentoContabilTransfDispo grupoLancamentoContabilTransfDispo;
	
	public static void defineFields(String className) {
		DefinicaoLancamentoContabil.defineFields(className);
		
		defineField( 
				className,
				"codigoFormulaData",
				MetaField.order, 250,
				MetaField.width, 250,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Data",
				MetaField.options, FormulaUtil.getClasses(FormulaDataTransfDispo.class)
		);
		
		defineField( 
				className,
				"codigoFormulaConta",
				MetaField.order, 350,
				MetaField.width, 250,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Conta",
				MetaField.options, FormulaUtil.getClasses(FormulaContaTransfDispo.class)
		);
		
		defineField( 
				className,
				"codigoFormulaEntidade",
				MetaField.order, 550,
				MetaField.width, 250,
				MetaField.label, "Fórmula Para Entidade",
				MetaField.options, FormulaUtil.getClasses(FormulaEntidadeTransfDispo.class)
		);
		
		defineField( 
				className,
				"codigoFormulaHistorico",
				MetaField.order, 600,
				MetaField.width, 250,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Histórico",
				MetaField.options, FormulaUtil.getClasses(FormulaHistoricoTransfDispo.class)
		);
		
		defineField( 
				className,
				"codigoFormulaValor",
				MetaField.order, 750,
				MetaField.width, 250,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Valor",
				MetaField.options, FormulaUtil.getClasses(FormulaValorTransfDispo.class)
		);
	}

	public GrupoLancamentoContabilTransfDispo getGrupoLancamentoContabilTransfDispo() {
		return grupoLancamentoContabilTransfDispo;
	}

	public void setGrupoLancamentoContabilTransfDispo(
			GrupoLancamentoContabilTransfDispo grupoLancamentoContabilTransfDispo) {
		this.grupoLancamentoContabilTransfDispo = grupoLancamentoContabilTransfDispo;
	}
	
}
