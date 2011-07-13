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

import br.com.dyad.backoffice.entidade.cadastro.TipoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.InterfaceItemBaixaAutomatica;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.infrastructure.annotations.Calculated;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.User;

@Entity
@Table(name="OPERACAO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999183")
public class ItemBaixaAutomatica extends ItemOperacaoAbstrato implements InterfaceItemBaixaAutomatica {
	
	@ManyToOne
    @JoinColumn(name="operacaoId")
	private CabecalhoBaixaAutomatica cabecalho;

	@Column
	@Temporal(TemporalType.DATE)
	private Date emissao;

	@Column
	private String numero;

	@ManyToOne
    @JoinColumn(name="tipoPedidoId")
	private TipoPedido tipoPedido;
	
	@ManyToOne
    @JoinColumn(name="entidadeId")
	private Entidade entidade;
	
	@ManyToOne
    @JoinColumn(name="estabelecimentoId")
	private Estabelecimento estabelecimento;
	
	@ManyToOne
    @JoinColumn(name="aprovadorId")
	private User aprovador;
	
	@Column
	@Temporal(TemporalType.DATE)
    private Date aprovacao;
	
	@ManyToOne
    @JoinColumn(name="canceladorId")
	private User cancelador;
	
	@Column
	@Temporal(TemporalType.DATE)
    private Date cancelamento;
	
	/**
	 * Campos de DETALHE
	 */	
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
	 * principal = base - Soma dos Impostos Retidos
	 */
	@Column(precision=11,scale=2)
	private BigDecimal principal;

	@Column(precision=11,scale=2)
	private BigDecimal comissao;
	
	/**
	 * TRANSIENTES
	 */
	@Calculated
	private transient BigDecimal valorSomaFreteDescontos;
	
	@Calculated
	private transient BigDecimal valorSomaTributos;
	
	/**
	 * Item de pedido que está sendo baixado
	 */	
	@ManyToOne
    @JoinColumn(name="baixadoId")
    private ItemBaixaAutomatica itemBaixaAutomaticaBaixado;

	/**
	 * GETTER'S e SETTER'S dos campos de CABECALHO
	 */
	public CabecalhoBaixaAutomatica getCabecalho() {
		return cabecalho;
	}
	public void setCabecalho(CabecalhoBaixaAutomatica cabecalho) {
/*		if (cabecalho.getAprovacao() != null || cabecalho.getAprovador() != null) {
			throw new RuntimeException("Não é permitido atribuir um item a uma operação já aprovada.");
		}
*/
		this.cabecalho = cabecalho;
	}
	public Date getEmissao() {
		return emissao;
	}
	public void setEmissao(Date emissao) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getEmissao() == null && emissao != null) || (this.getEmissao() != null && emissao == null) || (this.getEmissao().compareTo(emissao) != 0)) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/			
		this.emissao = emissao;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getNumero() == null && numero != null) || (this.getNumero() != null && numero == null) || !(this.getNumero().equals(numero))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/			
		this.numero = numero;
	}
	public TipoPedido getTipoPedido() {
		return tipoPedido;
	}
	public void setTipoPedido(TipoPedido tipoPedido) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getTipoPedido() == null && tipoPedido != null) || (this.getTipoPedido() != null && tipoPedido == null) || !(this.getTipoPedido().equals(tipoPedido))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.tipoPedido = tipoPedido;
	}
	public Entidade getEntidade() {
		return entidade;
	}
	public void setEntidade(Entidade entidade) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getEntidade() == null && entidade != null) || (this.getEntidade() != null && entidade == null) || !(this.getEntidade().equals(entidade))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.entidade = entidade;
	}
	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}
	public void setEstabelecimento(Estabelecimento estabelecimento) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getEstabelecimento() == null && estabelecimento != null) || (this.getEstabelecimento() != null && estabelecimento == null) || !(this.getEstabelecimento().equals(estabelecimento))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.estabelecimento = estabelecimento;
	}
	public User getAprovador() {
		return aprovador;
	}
	public void setAprovador(User aprovador) {
		this.aprovador = aprovador;
	}
	public Date getAprovacao() {
		return aprovacao;
	}
	public void setAprovacao(Date aprovacao) {
		this.aprovacao = aprovacao;
	}
	public User getCancelador() {
		return cancelador;
	}
	public void setCancelador(User cancelador) {
		this.cancelador = cancelador;
	}
	public Date getCancelamento() {
		return cancelamento;
	}
	public void setCancelamento(Date cancelamento) {
		this.cancelamento = cancelamento;
	}

	/**
	 * GETTER'S e SETTER'S dos campos de DETALHE
	 */
	public Nucleo getNucleo() {
		return nucleo;
	}
	public void setNucleo(Nucleo nucleo) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getNucleo() == null && nucleo != null) || (this.getNucleo() != null && nucleo == null) || !(this.getNucleo().equals(nucleo))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.nucleo = nucleo;
	}
	public Recurso getRecurso() {
		return recurso;
	}
	public void setRecurso(Recurso recurso) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getRecurso() == null && recurso != null) || (this.getRecurso() != null && recurso == null) || !(this.getRecurso().equals(recurso))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.recurso = recurso;
	}	
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getQuantidade() == null && quantidade != null) || (this.getQuantidade() != null && quantidade == null) || !(this.getQuantidade().equals(quantidade))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.quantidade = quantidade;
	}
	public BigDecimal getUnitario() {
		return unitario;
	}
	public void setUnitario(BigDecimal unitario) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getUnitario() == null && unitario != null) || (this.getUnitario() != null && unitario == null) || !(this.getUnitario().equals(unitario))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.unitario = unitario;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getTotal() == null && total != null) || (this.getTotal() != null && total == null) || !(this.getTotal().equals(total))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.total = total;
	}
	
	public BigDecimal getFrete() {
		return frete;
	}
	public void setFrete(BigDecimal frete) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getFrete() == null && frete != null) || (this.getFrete() != null && frete == null) || !(this.getFrete().equals(frete))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.frete = frete;
	}
	public BigDecimal getDesconto() {
		return desconto;
	}
	public void setDesconto(BigDecimal desconto) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getDesconto() == null && desconto != null) || (this.getDesconto() != null && desconto == null) || !(this.getDesconto().equals(desconto))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.desconto = desconto;
	}
	public BigDecimal getDescontoItem() {
		return descontoItem;
	}
	public void setDescontoItem(BigDecimal descontoItem) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getDescontoItem() == null && descontoItem != null) || (this.getDescontoItem() != null && descontoItem == null) || !(this.getDescontoItem().equals(descontoItem))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.descontoItem = descontoItem;
	}
	public BigDecimal getBase() {
		return base;
	}
	public void setBase(BigDecimal base) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getBase() == null && base != null) || (this.getBase() != null && base == null) || !(this.getBase().equals(base))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.base = base;
	}
	
	public BigDecimal getIr() {
		return ir;
	}
	public void setIr(BigDecimal ir) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getIr() == null && ir != null) || (this.getIr() != null && ir == null) || !(this.getIr().equals(ir))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.ir = ir;
	}
	public BigDecimal getCsll() {
		return csll;
	}
	public void setCsll(BigDecimal csll) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getCsll() == null && csll != null) || (this.getCsll() != null && csll == null) || !(this.getCsll().equals(csll))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.csll = csll;
	}
	public BigDecimal getCofins() {
		return cofins;
	}
	public void setCofins(BigDecimal cofins) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getCofins() == null && cofins != null) || (this.getCofins() != null && cofins == null) || !(this.getCofins().equals(cofins))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.cofins = cofins;
	}
	public BigDecimal getIss() {
		return iss;
	}
	public void setIss(BigDecimal iss) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getIss() == null && iss != null) || (this.getIss() != null && iss == null) || !(this.getIss().equals(iss))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.iss = iss;
	}
	public BigDecimal getPis() {
		return pis;
	}
	public void setPis(BigDecimal pis) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getPis() == null && pis != null) || (this.getPis() != null && pis == null) || !(this.getPis().equals(pis))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.pis = pis;
	}
	public BigDecimal getInss() {
		return inss;
	}
	public void setInss(BigDecimal inss) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getInss() == null && inss != null) || (this.getInss() != null && inss == null) || !(this.getInss().equals(inss))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.inss = inss;
	}
	public BigDecimal getPrincipal() {
		return principal;
	}
	public void setPrincipal(BigDecimal principal) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getPrincipal() == null && principal != null) || (this.getPrincipal() != null && principal == null) || !(this.getPrincipal().equals(principal))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.principal = principal;
	}
	public BigDecimal getComissao() {
		return comissao;
	}
	public void setComissao(BigDecimal comissao) {
/*		if (this.getCabecalho().getAprovacao() != null &&
				((this.getComissao() == null && comissao != null) || (this.getComissao() != null && comissao == null) || !(this.getComissao().equals(comissao))) ) {
				throw new RuntimeException("Não é permitido editar um pedido aprovado.");
			}
*/
		this.comissao = comissao;
	}
	
	@Override
	public ItemBaixaAutomatica getItemBaixaAutomaticaBaixado() {
		return itemBaixaAutomaticaBaixado;
	}
	public void setItemBaixaAutomaticaBaixado(ItemBaixaAutomatica itemBaixaAutomaticaBaixado) {
		this.itemBaixaAutomaticaBaixado = itemBaixaAutomaticaBaixado;
	}
	
	public BigDecimal getValorSomaFreteDescontos() {
		BigDecimal valorSomaFreteDescontos = new BigDecimal(0);
		
		if (this.getFrete() != null) {
			valorSomaFreteDescontos = valorSomaFreteDescontos.add(this.getFrete());
		}
		
		if (this.getDesconto() != null) {
			valorSomaFreteDescontos = valorSomaFreteDescontos.subtract(this.getDesconto());
		}
		
		if (this.getDescontoItem() != null) {
			valorSomaFreteDescontos = valorSomaFreteDescontos.subtract(this.getDescontoItem());
		}

		return valorSomaFreteDescontos;
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
	public ItemBaixaAutomatica() {
		super();
		this.itemBaixaAutomaticaBaixado = this;
	}

	public static void defineFields( String className){
		ItemOperacaoAbstrato.defineFields(className);

		defineField(
				className, 
				"cabecalho",
				MetaField.visible, false
		);

		defineField(
				className, 
				"emissao",
				MetaField.label, "Emissão",				
				MetaField.order, 10,
				MetaField.visible, false);

		defineField(
				className, 
				"numero",
				MetaField.label, "Número",				
				MetaField.order, 11,
				MetaField.visible, false);

		defineField(
				className, 
				"tipoPedido",
				MetaField.label, "Tipo do Pedido",				
				MetaField.order, 12,
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
				MetaField.required, true,
				MetaField.visible, true,
				MetaField.beanName, Nucleo.class.getName());

		defineField(
				className, 
				"recurso",
				MetaField.label, "Recurso",				
				MetaField.order, 50,
				MetaField.required, true,
				MetaField.visible, true,
				MetaField.beanName, Recurso.class.getName());

		defineField(
				className, 
				"quantidade",
				MetaField.label, "Quantidade",				
				MetaField.order, 60,
				MetaField.required, true,
				MetaField.width, 150,
				MetaField.visible, true);

		defineField(
				className, 
				"unitario",
				MetaField.label, "Unitário",				
				MetaField.order, 70,
				MetaField.required, true,
				MetaField.visible, true);
		
		defineField(
				className, 
				"total",
				MetaField.label, "Total",				
				MetaField.order, 90,
				MetaField.readOnly, true,
				MetaField.visible, true
		);
			
		defineField(
				className, 
				"frete",
				MetaField.label, "Frete",				
				MetaField.order, 100,
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.tableViewVisible, false 
		);
		
		defineField(
				className, 
				"desconto",
				MetaField.label, "Desconto",				
				MetaField.order, 110,
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.tableViewVisible, false 
		);

		defineField(
				className, 
				"descontoItem",
				MetaField.label, "Desconto Item",				
				MetaField.order, 120,
				MetaField.visible, true,
				MetaField.tableViewVisible, false 
		);

		defineField(
				className, 
				"valorSomaFreteDescontos",
				MetaField.label, "Frete/Descontos",				
				MetaField.order, 130,
				MetaField.readOnly, true,
				MetaField.visible, true
		);
	
		defineField(
				className, 
				"base",
				MetaField.label, "Base",				
				MetaField.order, 140,
				MetaField.visible, true,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"ir",
				MetaField.label, "IR",				
				MetaField.order, 150,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"csll",
				MetaField.label, "CSLL",				
				MetaField.order, 160,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"cofins",
				MetaField.label, "COFINS",				
				MetaField.order, 170,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"iss",
				MetaField.label, "ISS",				
				MetaField.order, 180,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"pis",
				MetaField.label, "PIS",				
				MetaField.order, 190,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"inss",
				MetaField.label, "INSS",				
				MetaField.order, 200,
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.tableViewVisible, false
		);

		defineField(
				className, 
				"valorSomaTributos",
				MetaField.label, "Tributos",				
				MetaField.order, 210,
				MetaField.readOnly, true,
				MetaField.visible, true
		);
	
		defineField(
				className, 
				"principal",
				MetaField.label, "Principal",				
				MetaField.order, 220,
				MetaField.readOnly, true,
				MetaField.visible, true
		);
	
		defineField(
				className, 
				"comissao",
				MetaField.label, "Comissão",				
				MetaField.order, 221,
				MetaField.readOnly, false,
				MetaField.visible, true
		);
	
		defineField(
				className, 
				"aprovador",
				MetaField.label, "Aprovador",				
				MetaField.order, 230,
				MetaField.visible, false
		);

		defineField(
				className, 
				"aprovacao",
				MetaField.label, "Aprovação",				
				MetaField.order, 240,
				MetaField.visible, false
		);

		defineField(
				className, 
				"cancelador",
				MetaField.label, "Cancelador",				
				MetaField.order, 250,
				MetaField.visible, false
		);

		defineField(
				className, 
				"cancelamento",
				MetaField.label, "Cancelamento",				
				MetaField.order, 260,
				MetaField.visible, false
		);
	}
}