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

import br.com.dyad.backoffice.entidade.cadastro.TipoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.InterfaceItemBaixaPedido;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.infrastructure.annotations.Calculated;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999218")
public class ItemBaixaPedido extends ItemOperacaoAbstrato implements InterfaceItemBaixaPedido {
   	
	@ManyToOne
    @JoinColumn(name="operacaoId")
    private CabecalhoBaixaPedido cabecalho;

	@Column
	@Temporal(TemporalType.DATE)
	private Date emissao;

	@Column
	private String numero;

	@ManyToOne
    @JoinColumn(name="entidadeId")
	private Entidade entidade;

	@ManyToOne
    @JoinColumn(name="estabelecimentoId")
	private Estabelecimento estabelecimento;

	@Column
	private String observacao;

	/**
	 * 
	 * Campos de DETALHE
	 * 
	 */	
	@ManyToOne
    @JoinColumn(name="baixadoId")
	private ItemPedido itemPedidoBaixado;
	
	@ManyToOne
    @JoinColumn(name="tipoPedidoId")
	private TipoPedido tipoPedido;

	@ManyToOne
    @JoinColumn(name="nucleoId")
	private Nucleo nucleo;
	
	@ManyToOne
    @JoinColumn(name="recursoId")
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
	
	@Column(precision=11,scale=2)
	private BigDecimal ir;
	
	@Column(precision=11,scale=2)
	private BigDecimal csll;
	
	@Column(precision=11,scale=2)
	private BigDecimal cofins;
	
	@Column(precision=11,scale=2)
	private BigDecimal iss;
	
	@Column(precision=11,scale=2)
	private BigDecimal pis;
	
	@Column(precision=11,scale=2)
	private BigDecimal inss;
	
	/**
	 * principal = base + ir + csll + cofins + iss + pis + inss
	 */
	@Column(precision=11,scale=2)
	private BigDecimal principal;
	
	/**
	 * Campos Calculados
	 */
	@Calculated
	private transient BigDecimal valorSomaFreteDescontos;
	
	@Calculated
	private transient BigDecimal valorSomaTributos;
	
	/**
	 * GETTER'S e SETTER'S dos campos de DETALHE
	 */
	public CabecalhoBaixaPedido getCabecalho() {
		return cabecalho;
	}

	public void setCabecalho(CabecalhoBaixaPedido cabecalho) {
		this.cabecalho = cabecalho;
	}

	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}

	public Entidade getEntidade() {
		return entidade;
	}

	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}

	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	/**
	 * Propriedades do ITEM
	 */
	public ItemPedido getItemPedidoBaixado() {
		return itemPedidoBaixado;
	}

	public void setItemPedidoBaixado(ItemPedido itemPedidoBaixado) {
		this.itemPedidoBaixado = itemPedidoBaixado;
	}
	
	public TipoPedido getTipoPedido() {
		return tipoPedido;
	}

	public void setTipoPedido(TipoPedido tipoPedido) {
		this.tipoPedido = tipoPedido;
	}

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

	public BigDecimal getIr() {
		return ir;
	}

	public void setIr(BigDecimal ir) {
		this.ir = ir;
	}

	public BigDecimal getCsll() {
		return csll;
	}

	public void setCsll(BigDecimal csll) {
		this.csll = csll;
	}

	public BigDecimal getCofins() {
		return cofins;
	}

	public void setCofins(BigDecimal cofins) {
		this.cofins = cofins;
	}

	public BigDecimal getIss() {
		return iss;
	}

	public void setIss(BigDecimal iss) {
		this.iss = iss;
	}

	public BigDecimal getPis() {
		return pis;
	}

	public void setPis(BigDecimal pis) {
		this.pis = pis;
	}

	public BigDecimal getInss() {
		return inss;
	}

	public void setInss(BigDecimal inss) {
		this.inss = inss;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getValorSomaFreteDescontos() {
		this.valorSomaFreteDescontos = new BigDecimal(0);
		
		if (this.getFrete() != null) {
			this.valorSomaFreteDescontos = this.valorSomaFreteDescontos.add(this.getFrete());
		}
		
		if (this.getDesconto() != null) {
			this.valorSomaFreteDescontos = this.valorSomaFreteDescontos.subtract(this.getDesconto());
		}
		
		if (this.getDescontoItem() != null) {
			this.valorSomaFreteDescontos = this.valorSomaFreteDescontos.subtract(this.getDescontoItem());
		}

		return this.valorSomaFreteDescontos;
	}

	public void setValorSomaFreteDescontos(BigDecimal valorSomaFreteDescontos) {
		this.valorSomaFreteDescontos = valorSomaFreteDescontos;
	}

	public BigDecimal getValorSomaTributos() {
		this.valorSomaTributos = new BigDecimal(0);
		
		if (this.getIr() != null) {
			this.valorSomaTributos = this.valorSomaTributos.add(this.getIr());
		}
		
		if (this.getCsll() != null) {
			this.valorSomaTributos = this.valorSomaTributos.add(this.getCsll());
		}
		
		if (this.getCofins() != null) {
			this.valorSomaTributos = this.valorSomaTributos.add(this.getCofins());
		}
		
		if (this.getIss() != null) {
			this.valorSomaTributos = this.valorSomaTributos.add(this.getIss());
		}
		
		if (this.getPis() != null) {
			this.valorSomaTributos = this.valorSomaTributos.add(this.getPis());
		}
		
		if (this.getInss() != null) {
			this.valorSomaTributos = this.valorSomaTributos.add(this.getInss());
		}

		return this.valorSomaTributos;
	}

	public void setValorSomaTributos(BigDecimal valorSomaTributos) {
		this.valorSomaTributos = valorSomaTributos;
	}

	/**
	 * Constructor
	 */
	public ItemBaixaPedido() {
		super();
	}
	
	public static void defineFields( String className){
		ItemOperacaoAbstrato.defineFields(className);

		defineField(
				className, 
				"emissao",
				MetaField.label, "Emissão",				
				MetaField.order, 100,
				MetaField.visible, false
		);

		defineField(
				className, 
				"entidade",
				MetaField.label, "Entidade",				
				MetaField.order, 300,
				MetaField.visible, false,
				MetaField.beanName, Entidade.class.getName()
		);

		defineField(
				className, 
				"estabelecimento",
				MetaField.label, "Estabelecimento",				
				MetaField.order, 400,
				MetaField.visible, false,
				MetaField.beanName, Estabelecimento.class.getName()
		);

		defineField(
				className, 
				"observacao",
				MetaField.label, "Observação",				
				MetaField.order, 500,
				MetaField.visible, false,
				MetaField.beanName, Estabelecimento.class.getName()
		);

		defineField(
				className, 
				"numero",
				MetaField.label, "Número",				
				MetaField.order, 600,
				MetaField.visible, false,
				MetaField.beanName, Estabelecimento.class.getName()
		);

		/**
		 * Propriedades do ITEM
		 */
		
		defineField(
				className, 
				"baixadoId",
				MetaField.label, "Baixado",				
				MetaField.order, 70,
				MetaField.required, true,
				MetaField.visible, true
		);

		defineField(
				className, 
				"tipoPedido",
				MetaField.label, "Tipo Pedido",				
				MetaField.order, 71,
				MetaField.required, true,
				MetaField.visible, true,
				MetaField.beanName, TipoPedido.class.getName()
		);

		defineField(
				className, 
				"nucleo",
				MetaField.label, "Núcleo",				
				MetaField.order, 80,
				MetaField.required, true,
				MetaField.visible, true,
				MetaField.beanName, Nucleo.class.getName()
		);

		defineField(
				className, 
				"recurso",
				MetaField.label, "Recurso",				
				MetaField.order, 90,
				MetaField.required, true,
				MetaField.visible, true,
				MetaField.beanName, Recurso.class.getName()
		);

		defineField(
				className, 
				"quantidade",
				MetaField.label, "Quantidade",				
				MetaField.order, 100,
				MetaField.required, true,
				MetaField.visible, true
		);

		defineField(
				className, 
				"unitario",
				MetaField.label, "Unitário",				
				MetaField.order, 110,
				MetaField.required, true,
				MetaField.visible, true
		);
		
		defineField(
				className, 
				"total",
				MetaField.label, "Total",				
				MetaField.order, 120,
				MetaField.readOnly, true,
				MetaField.visible, true,
				MetaField.tableViewVisible, true
		);
		
		defineField(
				className, 
				"frete",
				MetaField.label, "Frete",				
				MetaField.order, 130,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);
		
		defineField(
				className, 
				"desconto",
				MetaField.label, "Desconto",				
				MetaField.order, 140,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);
		
		defineField(
				className, 
				"descontoItem",
				MetaField.label, "Desconto Item",				
				MetaField.order, 150,
				MetaField.visible, true,
				MetaField.tableViewVisible, false
		);
		
		defineField(
				className, 
				"valorSomaFreteDescontos",
				MetaField.label, "Frete/Descontos",				
				MetaField.order, 160,
				MetaField.readOnly, true,
				MetaField.visible, true
		);

		defineField(
				className, 
				"base",
				MetaField.label, "Base",				
				MetaField.order, 170,
				MetaField.readOnly, true,
				MetaField.visible, true
		);
		
		defineField(
				className, 
				"ir",
				MetaField.label, "IR",				
				MetaField.order, 180,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"csll",
				MetaField.label, "CSLL",				
				MetaField.order, 190,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"cofins",
				MetaField.label, "COFINS",				
				MetaField.order, 200,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"iss",
				MetaField.label, "ISS",				
				MetaField.order, 210,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"pis",
				MetaField.label, "PIS",				
				MetaField.order, 220,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"inss",
				MetaField.label, "INSS",				
				MetaField.order, 230,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"valorSomaTributos",
				MetaField.label, "Tributos",				
				MetaField.order, 240,
				MetaField.visible, true,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"principal",
				MetaField.label, "Principal",				
				MetaField.order, 250,
				MetaField.visible, true,
				MetaField.readOnly, true
		);
	}
}