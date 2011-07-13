package br.com.dyad.backoffice.entidade.cadastro;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.businessinfrastructure.entidades.tabela.ClasseContabilFiscalEntidade;
import br.com.dyad.businessinfrastructure.entidades.tabela.ClasseContabilFiscalRecurso;
import br.com.dyad.businessinfrastructure.entidades.tabela.Tabela;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@DiscriminatorValue(value="-99999899999149")
public abstract class RegraLancamentoContabilFiscal extends Tabela {
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date inicio;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date fim;
	
	@ManyToOne
	@JoinColumn(name="classeContabilFiscalEntidadeId")
	private ClasseContabilFiscalEntidade classeContabilFiscalEntidade;
	
	@ManyToOne
	@JoinColumn(name="classeContabilFiscalRecursoId")
	private ClasseContabilFiscalRecurso classeContabilFiscalRecurso;
	
	public RegraLancamentoContabilFiscal() {
		super();
	}

	public static void defineFields(String className) {
		Tabela.defineFields(className);
		
		defineField(
				className,				
				"inicio",
				MetaField.label, "Início",
				MetaField.order, 10,
				MetaField.required, true, 
				MetaField.beanName, ClasseContabilFiscalEntidade.class.getName()
		);
		
		defineField(
				className,				
				"fim",
				MetaField.label, "Fim",
				MetaField.order, 20,
				MetaField.beanName, ClasseContabilFiscalEntidade.class.getName()
		);
		
		defineField(
				className,
				"codigo", 
				MetaField.required, false, 
				MetaField.order, 100,
				MetaField.width, 100
		);	
		
		defineField(
				className,				
				"classeContabilFiscalEntidade",
				MetaField.label, "Classe Contábil/Fiscal para Pessoa",
				MetaField.order, 210,
				MetaField.beanName, ClasseContabilFiscalEntidade.class.getName()
		);
		
		defineField(
				className,				
				"classeContabilFiscalRecurso",
				MetaField.label, "Classe Contábil/Fiscal para Recurso",
				MetaField.order, 220,
				MetaField.beanName, ClasseContabilFiscalRecurso.class.getName()
		);
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public ClasseContabilFiscalEntidade getClasseContabilFiscalEntidade() {
		return classeContabilFiscalEntidade;
	}

	public void setClasseContabilFiscalEntidade(
			ClasseContabilFiscalEntidade classeContabilFiscalEntidade) {
		this.classeContabilFiscalEntidade = classeContabilFiscalEntidade;
	}

	public ClasseContabilFiscalRecurso getClasseContabilFiscalRecurso() {
		return classeContabilFiscalRecurso;
	}

	public void setClasseContabilFiscalRecurso(
			ClasseContabilFiscalRecurso classeContabilFiscalRecurso) {
		this.classeContabilFiscalRecurso = classeContabilFiscalRecurso;
	}
}