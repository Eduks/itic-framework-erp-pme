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

import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.InterfaceTitulo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999724")
public class Titulo extends ItemOperacaoAbstrato implements InterfaceTitulo {

	@ManyToOne
    @JoinColumn(name="operacaoId")
	private CabecalhoOperacaoAbstrato cabecalho;
	
	@ManyToOne
    @JoinColumn(name="entidadeId")
	private Entidade entidade;
	
	@ManyToOne
    @JoinColumn(name="estabelecimentoId")
	private Entidade estabelecimento;
	
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
	private BigDecimal desconto;

	@Column(precision=11,scale=2)
	private BigDecimal acrescimo;

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
	
	@OneToMany(mappedBy="tituloBaixado")
	private List<ItemBaixaTitulo> itensBaixaTitulo;
	
	/**
	 * Contructor
	 * @throws Exception
	 */	
	public Titulo() throws Exception {
		super();
	}
	
	/*
	 * Getters e Setters 
	 */
	public CabecalhoOperacaoAbstrato getCabecalho() {
		return cabecalho;
	}

	public void setCabecalho(CabecalhoOperacaoAbstrato cabecalho) {
		this.cabecalho = cabecalho;
	}
	
	public Entidade getEntidade() {
		return entidade;
	}

	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}

	public Entidade getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(Entidade estabelecimento) {
		this.estabelecimento = estabelecimento;
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
		this.calculaTotal();
	}

	public BigDecimal getMulta() {
		return multa;
	}

	public void setMulta(BigDecimal multa) {
		this.multa = multa;
		this.calculaTotal();
	}

	public BigDecimal getTarifa() {
		return tarifa;
	}

	public void setTarifa(BigDecimal tarifa) {
		this.tarifa = tarifa;
		this.calculaTotal();
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
		this.calculaTotal();
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
		this.calculaTotal();
	}

	public BigDecimal getAcrescimo() {
		return acrescimo;
	}

	public void setAcrescimo(BigDecimal acrescimo) {
		this.acrescimo = acrescimo;
		this.calculaTotal();
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
	
	public List<ItemBaixaTitulo> getItensBaixaTitulo() {
		return itensBaixaTitulo;
	}

	public void setItensBaixaTitulo(List<ItemBaixaTitulo> itensBaixaTitulo) {
		this.itensBaixaTitulo = itensBaixaTitulo;
	}

	public static void defineFields( String className){
		ItemOperacaoAbstrato.defineFields(className);

		defineField(
				className, 
				"id",
				MetaField.label, "Título",				
				MetaField.order, 10,
				MetaField.width, 150,
				MetaField.visible, true
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
				MetaField.visible, false
		);

		defineField(
				className, 
				"vencimento",
				MetaField.label, "Vencimento",				
				MetaField.order, 50,
				MetaField.width, 150,
				MetaField.required, true,
				MetaField.visible, true
		);

		defineField(
				className, 
				"correcao",
				MetaField.label, "Correção",				
				MetaField.order, 60,
				MetaField.width, 150,
				MetaField.required, true,
				MetaField.visible, true
		);

		defineField(
				className, 
				"principal",
				MetaField.label, "Principal",				
				MetaField.order, 70,
				MetaField.required, true,
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
				"acrescimo",
				MetaField.label, "Acréscimo",				
				MetaField.order, 110,
				MetaField.visible, true
		);

		defineField(
				className, 
				"desconto",
				MetaField.label, "Desconto",				
				MetaField.order, 120,
				MetaField.visible, true
		);

		defineField(
				className, 
				"total",
				MetaField.label, "Total",				
				MetaField.order, 130,
				MetaField.readOnly, true,
				MetaField.visible, true
		);
	}

	public void calculaTotal() {
		BigDecimal principal = this.getPrincipal();
		BigDecimal juro = this.getJuro();
		BigDecimal multa = this.getMulta();
		BigDecimal acrescimo = this.getAcrescimo();
		BigDecimal tarifa = this.getTarifa();
		BigDecimal desconto = this.getDesconto();
		BigDecimal total = new BigDecimal(0);
		
		if (principal != null) {
			total = total.add(principal);
		}
		if (juro != null) {
			total = total.add(juro);
		}
		if (multa != null) {
			total = total.add(multa);
		}
		if (acrescimo != null) {
			total = total.add(acrescimo);
		}
		if (tarifa != null) {
			total = total.add(tarifa);
		}
		if (desconto != null) {
			total = total.subtract(desconto);
		}
		
		this.setTotal(total);
	}
	
	public BigDecimal getValorBaixado() {
		BigDecimal valorBaixado = new BigDecimal(0);
		
		List<ItemBaixaTitulo> baixasTitulo= this.getItensBaixaTitulo();
		
		for (ItemBaixaTitulo itemBaixaTitulo : baixasTitulo) {
			valorBaixado = valorBaixado.add(itemBaixaTitulo.getTotal());
		}
		
		return valorBaixado;
	}
}