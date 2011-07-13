package br.com.dyad.backoffice.entidade.movimentacao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.backoffice.entidade.cadastro.TipoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.InterfaceItemPedido;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.infrastructure.annotations.Calculated;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.User;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999727")
public class ItemPedido extends ItemOperacaoAbstrato implements InterfaceItemPedido {
	
	@ManyToOne
    @JoinColumn(name="operacaoId")
    private CabecalhoPedido cabecalho;

    @ManyToOne
    @JoinColumn(name="tipoPedidoId")
	private TipoPedido tipoPedido;
	
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
	 * 
	 * Campos de DETALHE
	 * 
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
	 * principal = base + ir + csll + cofins + iss + pis + inss
	 */
	@Column(precision=11,scale=2)
	private BigDecimal principal;
	
	@Column(precision=11,scale=2)
	private BigDecimal comissao;
	
	/**
	 * Coleções 
	 */
	@OneToMany(mappedBy="itemPedidoBaixado")
	private List<ItemBaixaPedido> itensBaixaPedido;
	
	/**
	 * Campos Calculados
	 */
	@Calculated
	private transient BigDecimal valorSomaFreteDescontos;
	
	@Calculated
	private transient BigDecimal valorSomaTributos;
	
	/**
	 * Campos Transientes
	 */
	private transient Long quantidadeBaixada;

	private transient BigDecimal valorBaixado;
	
	/**
	 * 	 
	 * GETTER'S e SETTER'S dos campos de CABECALHO
	 * 
	 */
	public CabecalhoPedido getCabecalho() {
		return cabecalho;
	}
	public void setCabecalho(CabecalhoPedido cabecalho) {
/*		if (cabecalho.getAprovacao() != null || cabecalho.getAprovador() != null) {
			throw new RuntimeException("Não é permitido atribuir um item a uma operação já aprovada.");
		}
*/
		this.cabecalho = cabecalho;
	}
	public TipoPedido getTipoPedido() {
		return tipoPedido;
	}
	public void setTipoPedido(TipoPedido tipoPedido) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getTipoPedido() == null && tipoPedido != null) || (this.getTipoPedido() != null && tipoPedido == null) || !(this.getCabecalho().getTipoPedido().equals(tipoPedido) )) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/		
		this.tipoPedido = tipoPedido;
	}
	public Date getEmissao() {
		return emissao;
	}
	public void setEmissao(Date emissao) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getEmissao() == null && emissao != null) || (this.getEmissao() != null && emissao == null) || (this.getCabecalho().getEmissao().compareTo(emissao) != 0)) ) {
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
			((this.getNumero() == null && numero != null) || (this.getNumero() != null && numero == null) || !(this.getCabecalho().getNumero().equals(numero))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/		
		this.numero = numero;
	}
	public Entidade getEntidade() {
		return entidade;
	}
	public void setEntidade(Entidade entidade) {
/*		if (this.getCabecalho().getAprovacao() != null &&
			((this.getEntidade() == null && entidade != null) || (this.getEntidade() != null && entidade == null) || !(this.getCabecalho().getEntidade().equals(entidade))) ) {
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
			((this.getEstabelecimento() == null && estabelecimento != null) || (this.getEstabelecimento() != null && estabelecimento == null) || !(this.getCabecalho().getEstabelecimento().equals(estabelecimento))) ) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/		
		this.estabelecimento = estabelecimento;
	}
	public Date getAprovacao() {
		return aprovacao;
	}
	public void setAprovacao(Date aprovacao) {
		this.aprovacao = aprovacao;
	}
	public User getAprovador() {
		return aprovador;
	}
	public void setAprovador(User aprovador) {
		this.aprovador = aprovador;
	}
	public Date getCancelamento() {
		return cancelamento;
	}
	public void setCancelamento(Date cancelamento) {
		this.cancelamento = cancelamento;
	}
	public User getCancelador() {
		return cancelador;
	}
	public void setCancelador(User cancelador) {
		this.cancelador = cancelador;
	}

	/**
	 * 	 
	 * GETTER'S e SETTER'S dos campos de DETALHE
	 * 
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
			((this.getUnitario() == null && unitario != null) || (this.getUnitario() != null && estabelecimento == null) || !(this.getEstabelecimento().equals(estabelecimento))) ) {
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
	
	/**
	 * 
	 * COMISSÕES
	 * 
	 */
	public List<ItemBaixaPedido> getItensBaixaPedido() {
		return itensBaixaPedido;
	}
	public void setItensBaixaPedido(List<ItemBaixaPedido> itensBaixaPedido) {
		this.itensBaixaPedido = itensBaixaPedido;
	}
	
	/**
	 * 
	 * TRANSIENTES
	 * 
	 */
	public BigDecimal getValorSomaFreteDescontos() {
		BigDecimal valorAux = null;
		BigDecimal valorSomaFreteDescontos = new BigDecimal(0);
		
		if (this.getFrete() != null) {
			valorAux = this.getFrete();
			valorSomaFreteDescontos = valorSomaFreteDescontos.add(valorAux);
		}
		
		if (this.getDesconto() != null) {
			valorAux = this.getDesconto();
			valorSomaFreteDescontos = valorSomaFreteDescontos.subtract(valorAux);
		}
		
		if (this.getDescontoItem() != null) {
			valorAux = this.getDescontoItem();
			valorSomaFreteDescontos = valorSomaFreteDescontos.subtract(valorAux);
		}
		
		return valorSomaFreteDescontos;
	}
	public void setValorSomaFreteDescontos(BigDecimal valorSomaFreteDescontos) {
		this.valorSomaFreteDescontos = valorSomaFreteDescontos;
	}
	public BigDecimal getValorSomaTributos() {
		BigDecimal valorAux = null;
		BigDecimal valorSomaTributos = new BigDecimal(0);
		
		if (this.getIr() != null) {
			valorAux =  this.getIr();
			valorSomaTributos = valorSomaTributos.add(valorAux);
		}
		
		if (this.getCsll() != null) {
			valorAux = this.getCsll();
			valorSomaTributos = valorSomaTributos.add(valorAux);
		}
		
		if (this.getCofins() != null) {
			valorAux = this.getCofins();
			valorSomaTributos = valorSomaTributos.add(valorAux);
		}
		
		if (this.getIss() != null) {
			valorAux = this.getIss();
			valorSomaTributos = valorSomaTributos.add(valorAux);
		}
		
		if (this.getPis() != null) {
			valorAux = this.getPis();
			valorSomaTributos = valorSomaTributos.add(valorAux);
		}
		
		if (this.getInss() != null) {
			valorAux = this.getInss();
			valorSomaTributos = valorSomaTributos.add(valorAux);
		}

		return valorSomaTributos;
	}
	public void setValorSomaTributos(BigDecimal valorSomaTributos) {
		this.valorSomaTributos = valorSomaTributos;
	}

	public Long getQuantidadeBaixada() {
		this.quantidadeBaixada = 0L; 
		List<ItemBaixaPedido> itensBaixaPedido = this.getItensBaixaPedido();
		
		for (ItemBaixaPedido itemBaixaPedido : itensBaixaPedido) {
			this.quantidadeBaixada += itemBaixaPedido.getQuantidade();
		}

		return this.quantidadeBaixada;
	}

	public void setQuantidadeBaixada(Long quantidadeBaixada) {
		this.quantidadeBaixada = quantidadeBaixada;
	}

	public BigDecimal getValorBaixado() {
		this.valorBaixado = new BigDecimal(0); 
		List<ItemBaixaPedido> itensBaixaPedido = this.getItensBaixaPedido();
		
		for (ItemBaixaPedido itemBaixaPedido : itensBaixaPedido) {
			this.valorBaixado = this.valorBaixado.add(itemBaixaPedido.getTotal());
		}

		return this.valorBaixado;
	}

	public void setValorBaixado(BigDecimal valorBaixado) {
		this.valorBaixado = valorBaixado;
	}

	/**
	 * Constructor
	 */
	public ItemPedido() {
		super();
	}

	/**
	 * DEFINE FIELDS
	 */
	public static void defineFields( String className){
		ItemOperacaoAbstrato.defineFields(className);

		defineField(
				className, 
				"id",
				MetaField.label, "Operação",
				MetaField.order, 100,
				MetaField.visible, false
		);

		defineField(
				className, 
				"tipoPedido",
				MetaField.label, "Tipo do Pedido",				
				MetaField.order, 200,
				MetaField.visible, false
		);

		defineField(
				className, 
				"emissao",
				MetaField.label, "Emissão",				
				MetaField.order, 300,
				MetaField.visible, false
		);

		defineField(
				className, 
				"numero",
				MetaField.label, "Número",				
				MetaField.order, 400,
				MetaField.visible, false
		);

		defineField(
				className, 
				"entidade",
				MetaField.label, "Entidade",				
				MetaField.order, 500,
				MetaField.visible, false,
				MetaField.beanName, Entidade.class.getName()
		);

		defineField(
				className, 
				"estabelecimento",
				MetaField.label, "Estabelecimento",				
				MetaField.order, 600,
				MetaField.visible, false,
				MetaField.beanName, Estabelecimento.class.getName()
		);

		defineField(
				className, 
				"nucleo",
				MetaField.label, "Núcleo",				
				MetaField.order, 700,
				MetaField.required, true,
				MetaField.visible, true,
				MetaField.beanName, Nucleo.class.getName()
		);

		defineField(
				className, 
				"recurso",
				MetaField.label, "Recurso",				
				MetaField.order, 800,
				MetaField.required, true,
				MetaField.visible, true,
				MetaField.beanName, Recurso.class.getName()
		);

		defineField(
				className, 
				"quantidade",
				MetaField.label, "Quantidade",				
				MetaField.order, 900,
				MetaField.required, true,
				MetaField.width, 150,
				MetaField.visible, true
		);

		defineField(
				className, 
				"unitario",
				MetaField.label, "Unitário",				
				MetaField.order, 1000,
				MetaField.required, true,
				MetaField.visible, true
		);
		
		defineField(
				className, 
				"total",
				MetaField.label, "Total",				
				MetaField.order, 1100,
				MetaField.readOnly, true,
				MetaField.visible, true
		);
	
		defineField(
				className, 
				"frete",
				MetaField.label, "Frete",				
				MetaField.order, 1200,
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.tableViewVisible, false 
		);
		
		defineField(
				className, 
				"desconto",
				MetaField.label, "Desconto",				
				MetaField.order, 1300,
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.tableViewVisible, false 
		);

		defineField(
				className, 
				"descontoItem",
				MetaField.label, "Desconto Item",				
				MetaField.order, 1500,
				MetaField.visible, true,
				MetaField.tableViewVisible, false 
		);

		defineField(
				className, 
				"valorSomaFreteDescontos",
				MetaField.label, "Frete/Descontos",				
				MetaField.order, 1600,
				MetaField.readOnly, true,
				MetaField.visible, true
		);
	
		defineField(
				className, 
				"base",
				MetaField.label, "Base",				
				MetaField.order, 1700,
				MetaField.visible, true,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"ir",
				MetaField.label, "IR",				
				MetaField.order, 1800,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"csll",
				MetaField.label, "CSLL",				
				MetaField.order, 1900,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"cofins",
				MetaField.label, "COFINS",				
				MetaField.order, 2000,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"iss",
				MetaField.label, "ISS",				
				MetaField.order, 2100,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"pis",
				MetaField.label, "PIS",				
				MetaField.order, 2200,
				MetaField.visible, true,
				MetaField.tableViewVisible, false,
				MetaField.readOnly, true
		);

		defineField(
				className, 
				"inss",
				MetaField.label, "INSS",				
				MetaField.order, 2300,
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.tableViewVisible, false
		);

		defineField(
				className, 
				"valorSomaTributos",
				MetaField.label, "Tributos",				
				MetaField.order, 2400,
				MetaField.readOnly, true,
				MetaField.visible, true
		);
	
		defineField(
				className, 
				"principal",
				MetaField.label, "Principal",				
				MetaField.order, 2500,
				MetaField.readOnly, true,
				MetaField.visible, true
		);
	
		defineField(
				className, 
				"comissao",
				MetaField.label, "Comissão",				
				MetaField.order, 2550,
				MetaField.readOnly, false,
				MetaField.visible, true
		);
	
		defineField(
				className, 
				"aprovador",
				MetaField.label, "Aprovador",				
				MetaField.order, 2600,
				MetaField.visible, false
		);

		defineField(
				className, 
				"aprovacao",
				MetaField.label, "Aprovação",				
				MetaField.order, 2700,
				MetaField.visible, false
		);

		defineField(
				className, 
				"cancelador",
				MetaField.label, "Cancelador",				
				MetaField.order, 2800,
				MetaField.visible, false
		);

		defineField(
				className, 
				"cancelamento",
				MetaField.label, "Cancelamento",				
				MetaField.order, 2900,
				MetaField.visible, false
		);
	}
}