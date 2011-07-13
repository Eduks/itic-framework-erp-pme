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

import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.InterfaceItemBaixaTitulo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="OPERACAO")
@DiscriminatorColumn(name = "classId", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999723")
public class ItemBaixaTitulo extends ItemOperacaoAbstrato implements InterfaceItemBaixaTitulo {

	@ManyToOne
    @JoinColumn(name="operacaoId")
    private CabecalhoBaixaTitulo cabecalho;

	/*
     * Campos de Cabecalho
     */
    @Column(name="emissao")
    @Temporal(TemporalType.DATE)
	private Date emissao;
	
    /*
     * Campos de Itens 
     */
	@ManyToOne
    @JoinColumn(name="baixadoId")
	private Titulo tituloBaixado;
	
    @ManyToOne
    @JoinColumn(name="entidadeId")
	private Entidade entidade;
	
    @ManyToOne
    @JoinColumn(name="estabelecimentoId")
	private Entidade estabelecimento;
	
    @Column(name="total")
	private BigDecimal total;
    
    /**
     * Constructor
     * @throws Exception
     */
    public ItemBaixaTitulo() throws Exception {
    	super();
    }
    
    /*
     * Getter's e Setter's 
     */
    public CabecalhoBaixaTitulo getCabecalho() {
		return cabecalho;
	}
	
    public void setCabecalho(CabecalhoBaixaTitulo cabecalhoBaixaTitulo) {
		this.cabecalho = cabecalhoBaixaTitulo;
	}
	
	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}
	
	public Date getEmissao() {
		return emissao;
	}
	
	public void setTituloBaixado(Titulo tituloBaixado) {
		this.tituloBaixado = tituloBaixado;
	}
	
	public Titulo getTituloBaixado() {
		return tituloBaixado;
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

	public BigDecimal getTotal() {
		return total;
	}
	
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	/**
	 *
	 * DEFINE FIELDS
	 *
	 */
	public static void defineFields( String className){
		ItemOperacaoAbstrato.defineFields(className);

		defineField(
				className, 
				"emissao",
				MetaField.label, "Emissão",				
				MetaField.order, 10,
				MetaField.visible, false
		);

		defineField(
				className, 
				"titulo",
				MetaField.label, "Título Baixado",				
				MetaField.order, 20,
				MetaField.beanName, Titulo.class.getName(),
				MetaField.visible, true
		);

		defineField(
				className, 
				"entidade",
				MetaField.label, "Entidade",				
				MetaField.order, 30,
				MetaField.readOnly, true,
				MetaField.beanName, Entidade.class.getName(),
				MetaField.visible, true
		);

		defineField(
				className, 
				"estabelecimento",
				MetaField.label, "Estabelecimento",				
				MetaField.order, 40,
				MetaField.readOnly, true,
				MetaField.beanName, Estabelecimento.class.getName(),
				MetaField.visible, true
		);

		defineField(
				className, 
				"total",
				MetaField.label, "Total",				
				MetaField.order, 40,
				MetaField.visible, true
		);
	}
}