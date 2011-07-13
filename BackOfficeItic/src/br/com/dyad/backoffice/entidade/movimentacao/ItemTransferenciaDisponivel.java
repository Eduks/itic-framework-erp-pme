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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.backoffice.entidade.cadastro.TipoTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoTransferenciaDisponivel;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.client.widget.field.FieldTypes;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="OPERACAO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999191")
public class ItemTransferenciaDisponivel extends ItemOperacaoAbstrato {

	@ManyToOne
    @JoinColumn(name="operacaoId", nullable=true)
	private CabecalhoTransferenciaDisponivel cabecalho;
	
	@ManyToOne
	@JoinColumn(name="entidadeId")	
	private Entidade entidade;
	
	@ManyToOne
	@JoinColumn(name="tipoTransferenciaDisponivelId")	
	private TipoTransferenciaDisponivel tipoTransferenciaDisponivel;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date emissao;

	@Column
	private BigDecimal total;

	@Column
	@Temporal(TemporalType.DATE)
	private Date conciliacao;

	@Column
	@Lob
	private String observacao;

	/**
	 * Constructor
	 * @throws Exception
	 */
	public ItemTransferenciaDisponivel() throws Exception {
		super();
	}
	
	/*
	 * Getters e Setters
	 */
	public CabecalhoTransferenciaDisponivel getCabecalho() {
		return cabecalho;
	}
	public void setCabecalho(CabecalhoTransferenciaDisponivel cabecalho) {
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
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getTotal() {
		return total;
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

	public static void defineFields(String className) {
		ItemOperacaoAbstrato.defineFields(className);
		
		defineField(
				className, 
				"id",
				MetaField.order, 10,
				MetaField.label, "Item",
				MetaField.visible, true,
				MetaField.readOnly, true
		);
		
		defineField( 
				className,
				"entidade",
				MetaField.label, "Entidade",
				MetaField.visible, true,
				MetaField.order, 100,
				MetaField.width, 300,
				MetaField.required, true,
				MetaField.beanName, Entidade.class.getName()
		);
		
		defineField( 
				className,
				"tipoTransferenciaDisponivel",
				MetaField.label, "Tipo Transferência",
				MetaField.visible, true,
				MetaField.order, 150,
				MetaField.width, 300,
				MetaField.required, true,
				MetaField.beanName, TipoTransferenciaDisponivel.class.getName()
		);
 		
		defineField( 
				className,
				"emissao",
				MetaField.label, "Emissão",
				MetaField.visible, false,
				MetaField.order, 200
		);
 		
		defineField( 
				className,
				"total",
				MetaField.label, "Total",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.order, 300
		);

		defineField( 
				className,
				"conciliacao",
				MetaField.label, "Conciliação",
				MetaField.visible, true,
				MetaField.order, 400
		);
 		
		defineField( 
				className,
				"observacao",
				MetaField.label, "Observação",
				MetaField.visible, true,
				MetaField.fieldType, FieldTypes.FIELD_TYPE_MEMO,
				MetaField.order, 500
		);
 		
	}

	public TipoTransferenciaDisponivel getTipoTransferenciaDisponivel() {
		return tipoTransferenciaDisponivel;
	}

	public void setTipoTransferenciaDisponivel(
			TipoTransferenciaDisponivel tipoTransferenciaDisponivel) {
		this.tipoTransferenciaDisponivel = tipoTransferenciaDisponivel;
	}
}