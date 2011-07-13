package br.com.dyad.backoffice.entidade.movimentacao.cabecalho;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.backoffice.entidade.cadastro.SerieDocumentoFiscal;
import br.com.dyad.backoffice.entidade.cadastro.TipoDocumentoBaixaPedido;
import br.com.dyad.backoffice.entidade.cadastro.TipoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaAutomatica;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.User;

@Entity
@Table(name="OPERACAO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999151")
public class CabecalhoBaixaAutomatica extends CabecalhoOperacaoAbstrato implements Cabecalho {

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
    @JoinColumn(name="tipoDocumentoId")
    private TipoDocumentoBaixaPedido tipoDocumento;
	
	@ManyToOne
    @JoinColumn(name="serieDocumentoFiscalId")
    private SerieDocumentoFiscal serieDocumentoFiscal;
	
	@Temporal(TemporalType.DATE)
	private Date emissao;
	
	@Column
	private String numero;

	@Column(precision=11,scale=2)
	private BigDecimal frete;
	
	@Column(precision=11,scale=2)
	private BigDecimal desconto;

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
	
	@Temporal(TemporalType.DATE)
	private Date aprovacao;
	
	@ManyToOne
    @JoinColumn(name="aprovadorId")
	private User aprovador;
	
	@Temporal(TemporalType.DATE)
	private Date cancelamento;
	
	@ManyToOne
    @JoinColumn(name="canceladorId")
	private User cancelador;
	
	@OneToMany(cascade={CascadeType.ALL,CascadeType.REMOVE},mappedBy="cabecalho")
	private List<ItemBaixaAutomatica> itensBaixaAutomatica;
	
	@OneToMany(cascade={CascadeType.ALL,CascadeType.REMOVE},mappedBy="cabecalho")
	private List<Titulo> titulos;
	
	public TipoPedido getTipoPedido() {
		return tipoPedido;
	}

	public void setTipoPedido(TipoPedido tipoPedido) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.tipoPedido = tipoPedido;
	}

	public Entidade getEntidade() {
		return entidade;
	}

	public void setEntidade(Entidade entidade) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.entidade = entidade;
	}
	
	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(Estabelecimento estabelecimento) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.estabelecimento = estabelecimento;
	}

	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.emissao = emissao;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.numero = numero;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.desconto = desconto;
	}

	public BigDecimal getFrete() {
		return frete;
	}

	public void setFrete(BigDecimal frete) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.frete = frete;
	}

	public BigDecimal getIr() {
		return ir;
	}

	public void setIr(BigDecimal ir) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.ir = ir;
	}

	public BigDecimal getCsll() {
		return csll;
	}

	public void setCsll(BigDecimal csll) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.csll = csll;
	}

	public BigDecimal getCofins() {
		return cofins;
	}

	public void setCofins(BigDecimal cofins) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.cofins = cofins;
	}

	public BigDecimal getIss() {
		return iss;
	}

	public void setIss(BigDecimal iss) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.iss = iss;
	}

	public BigDecimal getPis() {
		return pis;
	}

	public void setPis(BigDecimal pis) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.pis = pis;
	}

	public BigDecimal getInss() {
		return inss;
	}

	public void setInss(BigDecimal inss) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.inss = inss;
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
		return this.cancelamento;
	}

	public void setCancelamento(Date cancelamento) {
		this.cancelamento = cancelamento;
	}

	public User getCancelador() {
		return this.cancelador;
	}

	public void setCancelador(User cancelador) {
		this.cancelador = cancelador;
	}
	
	public List<ItemBaixaAutomatica> getItensBaixaAutomatica() {
		return itensBaixaAutomatica;
	}

	public void setItensBaixaAutomatica(List<ItemBaixaAutomatica> itensBaixaAutomatica) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.itensBaixaAutomatica = itensBaixaAutomatica;
	}

	public List<Titulo> getTitulos() {
		return titulos;
	}

	public void setTitulos(List<Titulo> titulos) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.titulos = titulos;
	}

	public void addItemBaixaAutomatica(ItemBaixaAutomatica itemBaixaAutomatica) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.itensBaixaAutomatica.add(itemBaixaAutomatica);
	}

	public void removeItemBaixaAutomatica(ItemBaixaAutomatica itemBaixaAutomatica) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.itensBaixaAutomatica.remove(itemBaixaAutomatica);
	}

	public void addTitulo(Titulo titulo) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.titulos.add(titulo);
	}

	public void removeTitulo(Titulo titulo) {
/*		if (this.getAprovacao() != null) {
			throw new RuntimeException("Não é permitido editar um pedido aprovado.");
		}
*/
		this.titulos.remove(titulo);
	}
	
	/**
	 * Constructor
	 * @throws Exception
	 */
	public CabecalhoBaixaAutomatica() throws Exception {
		super();
		
		this.itensBaixaAutomatica = new ArrayList<ItemBaixaAutomatica>();
		this.titulos = new ArrayList<Titulo>();
	}

	public static void defineFields(String className) {
		CabecalhoOperacaoAbstrato.defineFields(className);
		
		defineField( 
				className,
				"id",
				MetaField.label, "Id Operação",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 100,
				MetaField.column, 0
		);

		defineField( 
				className,
				"tipoPedido",
				MetaField.label, "Tipo Pedido",
				MetaField.required, true,
				MetaField.visible, true,
				MetaField.order, 200,
				MetaField.beanName, TipoPedido.class.getName(),
				MetaField.column, 1
		);

		defineField( 
				className,
				"emissao",
				MetaField.label, "Emissão",
				MetaField.required, true,
				MetaField.visible, true,
				MetaField.order, 300,
				MetaField.column, 2
		);
		
		defineField( 
				className,
				"tipoDocumento",
				MetaField.label, "Tipo do Documento",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.order, 340,
				MetaField.beanName, TipoDocumentoBaixaPedido.class.getName()
		);

		defineField( 
				className,
				"serieDocumentoFiscal",
				MetaField.label, "Série Documento",
				MetaField.visible, true,
				MetaField.order, 380,
				MetaField.beanName, SerieDocumentoFiscal.class.getName(),
				MetaField.column, 1
		);

		defineField( 
				className,
				"numero",
				MetaField.label, "Número",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.order, 400
		);

		defineField( 
				className,
				"entidade",
				MetaField.label, "Entidade",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.order, 500,
				MetaField.beanName, Entidade.class.getName()
		);

		defineField( 
				className,
				"estabelecimento",
				MetaField.label, "Estabelecimento",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.order, 600,
				MetaField.column, 1,
				MetaField.beanName, Estabelecimento.class.getName()
		);

		defineField( 
				className,
				"desconto",
				MetaField.label, "Desconto",
				MetaField.visible, true,
				MetaField.order, 700
		);

		defineField( 
				className,
				"frete",
				MetaField.label, "Frete",
				MetaField.visible, true,
				MetaField.order, 800,
				MetaField.column, 1
		);

		defineField( 
				className,
				"ir",
				MetaField.label, "IR",
				MetaField.visible, true,
				MetaField.order, 900,
				MetaField.column, 0
		);

		defineField( 
				className,
				"csll",
				MetaField.label, "CSLL",
				MetaField.visible, true,
				MetaField.order, 1000,
				MetaField.column, 1
		);

		defineField( 
				className,
				"cofins",
				MetaField.label, "COFINS",
				MetaField.visible, true,
				MetaField.order, 1100,
				MetaField.column, 2
		);

		defineField( 
				className,
				"iss",
				MetaField.label, "ISS",
				MetaField.visible, true,
				MetaField.order, 1200,
				MetaField.column, 0
		);

		defineField( 
				className,
				"pis",
				MetaField.label, "PIS",
				MetaField.visible, true,
				MetaField.order, 1300,
				MetaField.column, 1
		);

		defineField( 
				className,
				"inss",
				MetaField.label, "INSS",
				MetaField.visible, true,
				MetaField.order, 1400,
				MetaField.column, 2
		);

		defineField( 
				className,
				"aprovacao",
				MetaField.label, "Aprovação",
				MetaField.visible, true,
				MetaField.order, 1500,
				MetaField.readOnly, true,
				MetaField.column, 0
		);

		defineField( 
				className,
				"aprovador",
				MetaField.label, "Aprovador",
				MetaField.visible, true,
				MetaField.order, 1600,
				MetaField.beanName, Pessoa.class.getName(),
				MetaField.readOnly, true,
				MetaField.column, 1
		);

		defineField( 
				className,
				"cancelamento",
				MetaField.label, "Cancelamento",
				MetaField.visible, true,
				MetaField.order, 1700,
				MetaField.readOnly, true,
				MetaField.column, 0
		);

		defineField( 
				className,
				"cancelador",
				MetaField.label, "Cancelador",
				MetaField.visible, true,
				MetaField.order, 1800,
				MetaField.beanName, Pessoa.class.getName(),
				MetaField.readOnly, true,
				MetaField.column, 1
		);
	}

	public TipoDocumentoBaixaPedido getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumentoBaixaPedido tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public SerieDocumentoFiscal getSerieDocumentoFiscal() {
		return serieDocumentoFiscal;
	}

	public void setSerieDocumentoFiscal(SerieDocumentoFiscal serieDocumentoFiscal) {
		this.serieDocumentoFiscal = serieDocumentoFiscal;
	}

}