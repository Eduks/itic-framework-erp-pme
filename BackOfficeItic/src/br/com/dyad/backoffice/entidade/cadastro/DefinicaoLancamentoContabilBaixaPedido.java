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

import br.com.dyad.backoffice.formula.FormulaContaBaixaPedido;
import br.com.dyad.backoffice.formula.FormulaDataBaixaPedido;
import br.com.dyad.backoffice.formula.FormulaEntidadeBaixaPedido;
import br.com.dyad.backoffice.formula.FormulaHistoricoBaixaPedido;
import br.com.dyad.backoffice.formula.FormulaUtil;
import br.com.dyad.backoffice.formula.FormulaValorBaixaPedido;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="VINCULO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999339")
public class DefinicaoLancamentoContabilBaixaPedido extends DefinicaoLancamentoContabil {
	
	@ManyToOne
	@JoinColumn(name="grupoLancamentoContabilBaixaPedidoId")
	private GrupoLancamentoContabilBaixaPedido grupoLancamentoContabilBaixaPedido;
	
	public static void defineFields(String className) {
		DefinicaoLancamentoContabil.defineFields(className);
		
		defineField( 
				className,
				"codigoFormulaData",
				MetaField.order, 250,
				MetaField.width, 250,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Data",
				MetaField.options, FormulaUtil.getClasses(FormulaDataBaixaPedido.class)
		);
		
		defineField( 
				className,
				"codigoFormulaConta",
				MetaField.order, 350,
				MetaField.width, 250,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Conta",
				MetaField.options, FormulaUtil.getClasses(FormulaContaBaixaPedido.class)
		);
		
		defineField( 
				className,
				"codigoFormulaEntidade",
				MetaField.order, 550,
				MetaField.width, 250,
				MetaField.label, "Fórmula Para Entidade",
				MetaField.options, FormulaUtil.getClasses(FormulaEntidadeBaixaPedido.class)
		);
		
		defineField( 
				className,
				"codigoFormulaHistorico",
				MetaField.order, 600,
				MetaField.width, 250,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Histórico",
				MetaField.options, FormulaUtil.getClasses(FormulaHistoricoBaixaPedido.class)
		);
		
		defineField( 
				className,
				"codigoFormulaValor",
				MetaField.order, 750,
				MetaField.width, 250,
				MetaField.required, true,
				MetaField.label, "Fórmula Para Valor",
				MetaField.options, FormulaUtil.getClasses(FormulaValorBaixaPedido.class)
		);
	}

	public GrupoLancamentoContabilBaixaPedido getGrupoLancamentoContabilBaixaPedido() {
		return grupoLancamentoContabilBaixaPedido;
	}

	public void setGrupoLancamentoContabilBaixaPedido(
			GrupoLancamentoContabilBaixaPedido grupoLancamentoContabilBaixaPedido) {
		this.grupoLancamentoContabilBaixaPedido = grupoLancamentoContabilBaixaPedido;
	}

}
