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
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="OPERACAO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999387")
public class CabecalhoBaixaPedido extends CabecalhoOperacaoAbstrato implements Cabecalho {

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
	private BigDecimal desconto;

	@Column(precision=11,scale=2)
	private BigDecimal frete;
	
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
	
	@OneToMany(cascade={CascadeType.ALL, CascadeType.REMOVE},mappedBy="cabecalho")
	private List<ItemBaixaPedido> itensBaixaPedido;
	
	@OneToMany(cascade={CascadeType.ALL, CascadeType.REMOVE},mappedBy="cabecalho")
	private List<Titulo> titulos;
	
	/**
	 * Constructor
	 * @throws Exception
	 */
	public CabecalhoBaixaPedido() throws Exception {
		super();
		
		itensBaixaPedido = new ArrayList<ItemBaixaPedido>(0);
		titulos = new ArrayList<Titulo>(0);
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

	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public BigDecimal getFrete() {
		return frete;
	}

	public void setFrete(BigDecimal frete) {
		this.frete = frete;
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

	public List<ItemBaixaPedido> getItensBaixaPedido() {
		return itensBaixaPedido;
	}

	public void setItensBaixaPedido(List<ItemBaixaPedido> itensBaixaPedido) {
		this.itensBaixaPedido = itensBaixaPedido;
	}
	
	public void addItemBaixaPedido(ItemBaixaPedido itemBaixaPedido) {
		this.itensBaixaPedido.add(itemBaixaPedido);
	}

	public void removeItemBaixaPedido(ItemBaixaPedido itemBaixaPedido) {
		this.itensBaixaPedido.remove(itemBaixaPedido);
	}

	public List<Titulo> getTitulos() {
		return titulos;
	}

	public void setTitulos(List<Titulo> titulos) {
		this.titulos = titulos;
	}

	public void addTitulo(Titulo titulo) {
		this.titulos.add(titulo);
	}

	public void removeTitulo(Titulo titulo) {
		this.titulos.remove(titulo);
	}

	public static void defineFields(String className) {
		CabecalhoOperacaoAbstrato.defineFields(className);
		
		defineField( 
				className,
				"id",
				MetaField.label, "Id Operação",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 100
		);

		defineField( 
				className,
				"emissao",
				MetaField.label, "Emissão",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.order, 200,
				MetaField.column, 1
		);

		defineField( 
				className,
				"tipoDocumento",
				MetaField.label, "Tipo do Documento",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.order, 300,
				MetaField.beanName, TipoDocumentoBaixaPedido.class.getName()
		);

		defineField( 
				className,
				"serieDocumentoFiscal",
				MetaField.label, "Série Documento",
				MetaField.visible, true,
				MetaField.order, 400,
				MetaField.beanName, SerieDocumentoFiscal.class.getName(),
				MetaField.column, 1
		);

		defineField( 
				className,
				"numero",
				MetaField.label, "Número",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.order, 500,
				MetaField.column, 2
		);

		defineField( 
				className,
				"entidade",
				MetaField.label, "Entidade",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.order, 600,
				MetaField.beanName, Entidade.class.getName()
		);

		defineField( 
				className,
				"estabelecimento",
				MetaField.label, "Estabelecimento",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.order, 700,
				MetaField.beanName, Estabelecimento.class.getName(),
				MetaField.column, 1
		);

		defineField( 
				className,
				"desconto",
				MetaField.label, "Desconto",
				MetaField.visible, true,
				MetaField.order, 800,
				MetaField.column, 0
		);

		defineField( 
				className,
				"frete",
				MetaField.label, "Frete",
				MetaField.visible, true,
				MetaField.order, 900,
				MetaField.column, 1
		);
		
		defineField( 
				className,
				"ir",
				MetaField.label, "IR",
				MetaField.visible, true,
				MetaField.order, 1000,
				MetaField.column, 0
		);

		defineField( 
				className,
				"csll",
				MetaField.label, "CSLL",
				MetaField.visible, true,
				MetaField.order, 1100,
				MetaField.column, 1
		);

		defineField( 
				className,
				"cofins",
				MetaField.label, "COFINS",
				MetaField.visible, true,
				MetaField.order, 1200,
				MetaField.column, 2
		);

		defineField( 
				className,
				"iss",
				MetaField.label, "ISS",
				MetaField.visible, true,
				MetaField.order, 1300,
				MetaField.column, 0
		);

		defineField( 
				className,
				"pis",
				MetaField.label, "PIS",
				MetaField.visible, true,
				MetaField.order, 1400,
				MetaField.column, 1
		);

		defineField( 
				className,
				"inss",
				MetaField.label, "INSS",
				MetaField.visible, true,
				MetaField.order, 1500,
				MetaField.column, 2
		);
	}
}
