package br.com.dyad.backoffice.entidade.movimentacao;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.backoffice.entidade.movimentacao.objetos_interface.InterfaceItemOperacao;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999725")
public abstract class TituloAbstrato extends ItemOperacao implements InterfaceItemOperacao {

	@ManyToOne
    @JoinColumn(name="entidadeId")
	private Entidade entidade;
	
	@Column
	private String numero;

	@Column(precision=11,scale=2)
	private BigDecimal juro;

	@Column(precision=11,scale=2)
	private BigDecimal multa;

	@Column(precision=11,scale=2)
	private BigDecimal tarifa;

	@Column(precision=11,scale=2)
	private BigDecimal principal;

	@Column(precision=11,scale=2)
	private BigDecimal total;

	@Column
	@Temporal(TemporalType.DATE)
	private Date vencimento;

	@Column
	@Temporal(TemporalType.DATE)
	private Date correcao;

	@Column
	@Temporal(TemporalType.DATE)
	private Date emissao;

	/*
	 * Getters e Setters 
	 */
	
	public Entidade getEntidade() {
		return entidade;
	}

	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public BigDecimal getJuro() {
		return juro;
	}

	public void setJuro(BigDecimal juro) {
		this.juro = juro;
	}

	public BigDecimal getMulta() {
		return multa;
	}

	public void setMulta(BigDecimal multa) {
		this.multa = multa;
	}

	public BigDecimal getTarifa() {
		return tarifa;
	}

	public void setTarifa(BigDecimal tarifa) {
		this.tarifa = tarifa;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Date getVencimento() {
		return vencimento;
	}

	public void setVencimento(Date vencimento) {
		this.vencimento = vencimento;
	}

	public Date getCorrecao() {
		return correcao;
	}

	public void setCorrecao(Date correcao) {
		this.correcao = correcao;
	}

	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}

	/**
	 *
	 * DEFINE FIELDS
	 *
	 */
	public static void defineFields( String className){
		Financeiro.defineFields(className);

		defineField(
				className, 
				"id",
				MetaField.label, "ID",				
				MetaField.order, 10,
				MetaField.visible, true
		);

		defineField(
				className, 
				"entidade",
				MetaField.label, "Entidade",				
				MetaField.order, 20,
				MetaField.visible, false
		);

		defineField(
				className, 
				"numero",
				MetaField.label, "Número",				
				MetaField.order, 30,
				MetaField.visible, false
		);

		defineField(
				className, 
				"emissao",
				MetaField.label, "Emissão",				
				MetaField.order, 40,
				MetaField.visible, true
		);

		defineField(
				className, 
				"vencimento",
				MetaField.label, "Vencimento",				
				MetaField.order, 50,
				MetaField.visible, true
		);

		defineField(
				className, 
				"correcao",
				MetaField.label, "Correção",				
				MetaField.order, 60,
				MetaField.visible, true
		);

		defineField(
				className, 
				"principal",
				MetaField.label, "Principal",				
				MetaField.order, 70,
				MetaField.visible, true
		);

		defineField(
				className, 
				"juro",
				MetaField.label, "Juro",				
				MetaField.order, 80,
				MetaField.visible, true
		);

		defineField(
				className, 
				"multa",
				MetaField.label, "Multa",				
				MetaField.order, 90,
				MetaField.visible, true
		);

		defineField(
				className, 
				"tarifa",
				MetaField.label, "Tarifa",				
				MetaField.order, 100,
				MetaField.visible, true
		);

		defineField(
				className, 
				"total",
				MetaField.label, "Total",				
				MetaField.order, 110,
				MetaField.visible, true
		);
	}
}