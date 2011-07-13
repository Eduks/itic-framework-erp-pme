package br.com.dyad.backoffice.entidade.movimentacao;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaTitulo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="OPERACAO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999165")
public class MovDispo extends ItemOperacaoAbstrato {

	@ManyToOne
    @JoinColumn(name="operacaoId")
	private CabecalhoBaixaTitulo cabecalho;

	@ManyToOne
    @JoinColumn(name="entidadeId")
	private Entidade entidade;

	@Column
	@Temporal(TemporalType.DATE)
	private Date emissao;

	@Column(precision=11,scale=2)
	private BigDecimal total;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date conciliacao;

	@Column
	private String observacao;
	
	/**
	 * Contructor
	 * @throws Exception
	 */
	public MovDispo() throws Exception {
		super();
	}
	public CabecalhoBaixaTitulo getCabecalho() {
		return cabecalho;
	}
	public void setCabecalho(CabecalhoBaixaTitulo cabecalho) {
		this.cabecalho = cabecalho;
	}
	public Entidade getEntidade() {
		return entidade;
	}
	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}
	public Date getEmissao() {
		return emissao;
	}
	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Date getConciliacao() {
		return conciliacao;
	}
	public void setConciliacao(Date conciliacao) {
		this.conciliacao = conciliacao;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public static void defineFields( String className){
		ItemOperacaoAbstrato.defineFields(className);
		
		defineField(
				className, 
				"entidade",
				MetaField.label, "Entidade",				
				MetaField.order, 30,
				MetaField.width, 300,
				MetaField.required, true,
				MetaField.visible, true,
				MetaField.beanName, Entidade.class.getName()
		);

		defineField(
				className, 
				"emissao",
				MetaField.label, "Emissão",				
				MetaField.order, 40,
				MetaField.width, 150,
				MetaField.visible, false
		);
		
		defineField(
				className, 
				"total",
				MetaField.label, "Total",				
				MetaField.order, 50,
				MetaField.width, 150,
				MetaField.required, true,
				MetaField.visible, true
		);
		
		defineField(
				className, 
				"conciliacao",
				MetaField.label, "Conciliação",				
				MetaField.order, 60,
				MetaField.width, 150,
				MetaField.visible, true
		);
		
		defineField(
				className, 
				"observacao",
				MetaField.label, "Observação",				
				MetaField.order, 70,
				MetaField.width, 150,
				MetaField.visible, true
		);
		
	}
}