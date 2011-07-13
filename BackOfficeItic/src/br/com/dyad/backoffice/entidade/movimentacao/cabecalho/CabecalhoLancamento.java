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

import br.com.dyad.backoffice.annotations.Cabecalho;
import br.com.dyad.backoffice.annotations.TipoCampoCabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.infrastructure.annotations.Calculated;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="CONTABIL")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999380")
public class CabecalhoLancamento extends CabecalhoContabilAbstrato {
	
	@ManyToOne
    @JoinColumn(name="operacaoId")
	private CabecalhoOperacaoAbstrato cabecalhoOperacao;
	
	@Column
	@Temporal(TemporalType.DATE)
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Date emissao;
	
	@OneToMany(cascade={CascadeType.ALL, CascadeType.REMOVE}, mappedBy="cabecalho")
	private List<ItemLancamento> itensLancamento;
	
	@Calculated
	private transient BigDecimal saldoParcial;
	
	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}
	
	public CabecalhoLancamento() throws Exception {
		super();
		
		itensLancamento = new ArrayList<ItemLancamento>(0);
		
		saldoParcial = new BigDecimal(0);
		
		for (ItemLancamento item : itensLancamento) {
			saldoParcial = saldoParcial.add(item.getValor());
		}
	}
	
	public static void defineFields(String className) {
		CabecalhoContabilAbstrato.defineFields(className);
		
		defineField( 
				className,
				"id",
				MetaField.label, "Id da Operação",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.width, 150,
				MetaField.order, 30
		);
		
		
		defineField( 
				className,
				"emissao",
				MetaField.order, 40,
				MetaField.width, 150,
				MetaField.label, "Data",
				MetaField.defaultValue, new Date(),
				MetaField.required, true,
				MetaField.column, 1
		);
		
		defineField( 
				className,
				"saldoParcial",
				MetaField.order, 50,
				MetaField.width, 150,				
				MetaField.label, "Saldo Atual",
				MetaField.visible, true,
				MetaField.column, 2
		);
	}

	public List<ItemLancamento> getItensLancamento() {
		return itensLancamento;
	}

	public void setItensLancamento(List<ItemLancamento> itensLancamento) {
		this.itensLancamento = itensLancamento;
	}	
	
	public void addItemLancamento(ItemLancamento item){
		itensLancamento.add(item);
	}
	
	public void removeItemLancamento(ItemLancamento item){
		saldoParcial = saldoParcial.subtract(item.getValor());
		
		itensLancamento.remove(item);
	}

	public BigDecimal getSaldoParcial() {
		return saldoParcial;
	}

	public void setSaldoParcial(BigDecimal saldoParcial) {
		this.saldoParcial = saldoParcial;
	}
	
	public void atualizaSaldoParcial(BigDecimal valorCampoAtual, BigDecimal valorCampoNovo){
		
		if (valorCampoAtual == null) {
			valorCampoAtual = new BigDecimal(0);
		}
		
		if (valorCampoNovo == null) {
			valorCampoNovo = new BigDecimal(0);
		}
		
		saldoParcial = saldoParcial.subtract(valorCampoAtual);
		saldoParcial = saldoParcial.add(valorCampoNovo);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof CabecalhoLancamento) {
			
			CabecalhoLancamento cabecalhoLancamento = (CabecalhoLancamento)obj;
			
			if (cabecalhoLancamento.getId() != null) {
				return super.equals(cabecalhoLancamento);
			} else {
				return (emissao.equals(cabecalhoLancamento.getEmissao()));
			}
		}
		
		return false;
	}

	public CabecalhoOperacaoAbstrato getCabecalhoOperacao() {
		return cabecalhoOperacao;
	}

	public void setCabecalhoOperacao(CabecalhoOperacaoAbstrato cabecalhoOperacao) {
		this.cabecalhoOperacao = cabecalhoOperacao;
	}
}