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
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999941")
public abstract class ItemPedidoAbstrato extends ItemOperacao implements InterfaceItemOperacao {
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date emissao;

	@ManyToOne
    @JoinColumn(name="entidadeId")
	private Entidade entidade;
	
	@ManyToOne
    @JoinColumn(name="estabelecimentoId")
	private Estabelecimento estabelecimento;
	
	@ManyToOne
    @JoinColumn(name="aprovadorId")
	private Pessoa aprovador;
	
	@Column
	@Temporal(TemporalType.DATE)
    private Date aprovacao;
	
	@ManyToOne
    @JoinColumn(name="canceladorId")
	private Pessoa cancelador;
	
	@Column
	@Temporal(TemporalType.DATE)
    private Date cancelamento;
	
	/**
	 * 
	 * Campos de DETALHE
	 * 
	 */	
	@ManyToOne
    @JoinColumn(name="nucleoId", nullable=false)
	private Nucleo nucleo;

	@ManyToOne
    @JoinColumn(name="recursoId", nullable=false)
	private Recurso recurso;

	@Column
	private Long quantidade;

	@Column(precision=11,scale=2)
	private BigDecimal unitario;

	/**
	 * Total = quantidade * unitario 
	 */
	@Column(precision=11,scale=2)
	private BigDecimal total;

	@Column(precision=11,scale=2)
	private BigDecimal frete;
	
	@Column(precision=11,scale=2)
	private BigDecimal desconto;
	
	@Column(precision=11,scale=2)
	private BigDecimal descontoItem;
	
	/**
	 * base = total + frete - desconto - descontoItem
	 */
	@Column(precision=11,scale=2)
	private BigDecimal base;

	/**
	 * principal = base - Soma dos Impostos Retidos
	 */
	@Column(precision=11,scale=2)
	private BigDecimal principal;

	/**
	 * 	 
	 * GETTER´S e SETTER´S dos campos de CABECALHO
	 * 
	 */
	public Date getEmissao() {
		return emissao;
	}
	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}
	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}
	public Entidade getEntidade() {
		return entidade;
	}
	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}
	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}

	/**
	 * 	 
	 * GETTER´S e SETTER´S dos campos de DETALHE
	 * 
	 */
	public Nucleo getNucleo() {
		return nucleo;
	}
	public void setNucleo(Nucleo nucleo) {
		this.nucleo = nucleo;
	}
	public Recurso getRecurso() {
		return recurso;
	}
	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}	
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	public BigDecimal getUnitario() {
		return unitario;
	}
	public void setUnitario(BigDecimal unitario) {
		this.unitario = unitario;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getFrete() {
		return frete;
	}
	public void setFrete(BigDecimal frete) {
		this.frete = frete;
	}
	public BigDecimal getDesconto() {
		return desconto;
	}
	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}
	public BigDecimal getDescontoItem() {
		return descontoItem;
	}
	public void setDescontoItem(BigDecimal descontoItem) {
		this.descontoItem = descontoItem;
	}
	public BigDecimal getBase() {
		return base;
	}
	public void setBase(BigDecimal base) {
		this.base = base;
	}
	public BigDecimal getPrincipal() {
		return principal;
	}
	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}
	public Pessoa getAprovador() {
		return aprovador;
	}
	public void setAprovador(Pessoa aprovador) {
		this.aprovador = aprovador;
	}
	public Date getAprovacao() {
		return aprovacao;
	}
	public void setAprovacao(Date aprovacao) {
		this.aprovacao = aprovacao;
	}
	public Pessoa getCancelador() {
		return cancelador;
	}
	public void setCancelador(Pessoa cancelador) {
		this.cancelador = cancelador;
	}
	public Date getCancelamento() {
		return cancelamento;
	}
	public void setCancelamento(Date cancelamento) {
		this.cancelamento = cancelamento;
	}
	
	/**
	 *
	 * DEFINE FIELDS
	 *
	 */
	public static void defineFields( String className){
		Operacao.defineFields(className);

		defineField(
				className, 
				"emissao",
				MetaField.label, "Emissão",				
				MetaField.order, 10,
				MetaField.visible, false);

		defineField(
				className, 
				"entidade",
				MetaField.label, "Entidade",				
				MetaField.order, 20,
				MetaField.visible, false,
				MetaField.beanName, Entidade.class.getName());

		defineField(
				className, 
				"estabelecimento",
				MetaField.label, "Estabelecimento",				
				MetaField.order, 30,
				MetaField.visible, false,
				MetaField.beanName, Estabelecimento.class.getName());

		defineField(
				className, 
				"nucleo",
				MetaField.label, "Núcleo",				
				MetaField.order, 40,
				MetaField.visible, true,
				MetaField.beanName, Nucleo.class.getName());

		defineField(
				className, 
				"recurso",
				MetaField.label, "Recurso",				
				MetaField.order, 50,
				MetaField.visible, true,
				MetaField.beanName, Recurso.class.getName());

		defineField(
				className, 
				"quantidade",
				MetaField.label, "Quantidade",				
				MetaField.order, 60,
				MetaField.visible, true);

		defineField(
				className, 
				"unitario",
				MetaField.label, "Unitário",				
				MetaField.order, 70,
				MetaField.visible, true);
		
		defineField(
				className, 
				"total",
				MetaField.label, "Total",				
				MetaField.order, 90,
				MetaField.visible, true);
	
		
		defineField(
				className, 
				"frete",
				MetaField.label, "Frete",				
				MetaField.order, 100,
				MetaField.visible, false);
		
		defineField(
				className, 
				"desconto",
				MetaField.label, "Desconto",				
				MetaField.order, 110,
				MetaField.visible, false);

		defineField(
				className, 
				"principal",
				MetaField.label, "Principal",				
				MetaField.order, 120,
				MetaField.visible, true);
	
		defineField(
				className, 
				"descontoItem",
				MetaField.label, "Desconto Item",				
				MetaField.order, 130,
				MetaField.visible, true);

		defineField(
				className, 
				"base",
				MetaField.label, "Base",				
				MetaField.order, 140,
				MetaField.visible, false);

		defineField(
				className, 
				"aprovador",
				MetaField.label, "Aprovador",				
				MetaField.order, 150,
				MetaField.visible, false);

		defineField(
				className, 
				"aprovacao",
				MetaField.label, "Aprovação",				
				MetaField.order, 160,
				MetaField.visible, false);

		defineField(
				className, 
				"cancelador",
				MetaField.label, "Cancelador",				
				MetaField.order, 170,
				MetaField.visible, false);

		defineField(
				className, 
				"cancelamento",
				MetaField.label, "Cancelamento",				
				MetaField.order, 180,
				MetaField.visible, false);

	}
}