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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.TipoLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoLancamento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.client.AppException;
import br.com.dyad.client.widget.field.FieldTypes;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="CONTABIL")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999177")
public class ItemLancamento extends ItemContabilAbstrato {
	
	@ManyToOne
    @JoinColumn(name="contabilId")
	private CabecalhoLancamento cabecalho;
	
	@Column(precision=11,scale=2)
	private BigDecimal valor;
	
	@ManyToOne
    @JoinColumn(name="entidadeId", nullable=true)
	private Entidade entidade;
	
	@ManyToOne
    @JoinColumn(name="tipoLancamentoId")
	private TipoLancamento tipoLancamento;
	
	@ManyToOne
    @JoinColumn(name="contaId")
	private ContaContabil contaContabil;

	@Column
	@Lob
	private String observacao;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date emissao;
	
	public CabecalhoLancamento getCabecalho() {
		return cabecalho;
	}

	public void setCabecalho(CabecalhoLancamento cabecalho) {
		this.cabecalho = cabecalho;
	}

	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}
	
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		if (cabecalho != null) {
			cabecalho.atualizaSaldoParcial(this.valor, valor);
		}
		
		this.valor = valor;
	}

	public Entidade getEntidade() {
		return entidade;
	}

	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}

	public ContaContabil getContaContabil() {
		return contaContabil;
	}

	public void setContaContabil(ContaContabil contaContabil) {
		if ( contaContabil != null && contaContabil.getSintetico() ){
			throw new AppException("Não é possível criar um Lançamento em uma Conta Sintética. \nSelecione uma conta Analítica para este Lançamento.");
		} else {
			this.contaContabil = contaContabil;
		}
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public static void defineFields(String className) {
		ItemContabilAbstrato.defineFields(className);
		
		defineField(
				className, 
				"sinal",				
				MetaField.visible, false
		);
		
		defineField( 
				className,
				"emissao",
				MetaField.visible, false				
		);
		defineField( 
				className,
				"id",
				MetaField.order, 100,
				MetaField.width, 150,
				MetaField.readOnly, true,
				MetaField.visible, true,
				MetaField.label, "Item"
		);
		defineField( 
				className,
				"tipoLancamento",
				MetaField.order, 150,
				MetaField.width, 200,
				MetaField.tableViewWidth, 160,
				MetaField.required, true,
				MetaField.label, "Tipo Lançamento",
				MetaField.beanName, TipoLancamento.class.getName()
		);
		defineField( 
				className,
				"contaContabil",
				MetaField.order, 200,
				MetaField.width, 300,
				MetaField.tableViewWidth, 160,
				MetaField.required, true,
				MetaField.label, "Conta Contábil",
				MetaField.beanName, ContaContabil.class.getName()
		);
		defineField( 
				className,
				"valor",
				MetaField.order, 300,
				MetaField.width, 200,
				MetaField.tableViewWidth, 70,
				MetaField.required, true,
				MetaField.label, "Valor"
		);
		defineField( 
				className,
				"entidade",
				MetaField.order, 400,
				MetaField.width, 300,
				MetaField.tableViewWidth, 150,
				MetaField.label, "Entidade",
				MetaField.beanName, Entidade.class.getName()
		);
		defineField( 
				className,
				"observacao",
				MetaField.order, 500,
				MetaField.width, 300,
				MetaField.tableViewWidth, 200,
				MetaField.height, 3,
				MetaField.defaultValue, "Histórico do Lançamento.",
				MetaField.fieldType, FieldTypes.FIELD_TYPE_MEMO,
				MetaField.label, "Histórico"
		);
	}

	public TipoLancamento getTipoLancamento() {
		return tipoLancamento;
	}

	public void setTipoLancamento(TipoLancamento tipoLancamento) {
		this.tipoLancamento = tipoLancamento;
	}
}