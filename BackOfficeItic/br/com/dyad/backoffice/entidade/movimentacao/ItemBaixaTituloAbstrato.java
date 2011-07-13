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
@DiscriminatorValue(value="-99999899999196")
public class ItemBaixaTituloAbstrato extends ItemOperacao implements InterfaceItemOperacao {

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
    @JoinColumn(name="idBaixado")
	private Titulo titulo;
	
    @ManyToOne
    @JoinColumn(name="entidadeId")
	private Entidade entidade;
	
    @Column(name="total")
	private BigDecimal total;
    
    /*
     * Getter´s e Setter´s 
     */
	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}
	public Date getEmissao() {
		return emissao;
	}
	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}
	public Titulo getTitulo() {
		return titulo;
	}
	public Entidade getEntidade() {
		return entidade;
	}
	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
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
		Operacao.defineFields(className);

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
				MetaField.label, "Titulo Baixado",				
				MetaField.order, 20,
				MetaField.beanName, Titulo.class.getName(),
				MetaField.visible, true
		);

		defineField(
				className, 
				"entidade",
				MetaField.label, "Entidade",				
				MetaField.order, 30,
				MetaField.beanName, Entidade.class.getName(),
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
