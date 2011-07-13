package br.com.dyad.backoffice.entidade.movimentacao.cabecalho;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.dyad.backoffice.annotations.Cabecalho;
import br.com.dyad.backoffice.annotations.TipoCampoCabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="OPERACAO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999385")
public class CabecalhoTransferenciaDisponivel extends CabecalhoOperacaoAbstrato{
	
	@Column
	@Temporal(TemporalType.DATE)
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Date emissao;
	
	@OneToMany(cascade={CascadeType.ALL, CascadeType.REMOVE}, mappedBy="cabecalho")
	private List<ItemTransferenciaDisponivel> itensTransferenciaDisponivel;
	
	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}
	
	public List<ItemTransferenciaDisponivel> getItensTransferenciaDisponivel() {
		return itensTransferenciaDisponivel;
	}
	
	public void setItensTransferenciaDisponivel(List<ItemTransferenciaDisponivel> itensTransferenciaDisponivel) {
		this.itensTransferenciaDisponivel = itensTransferenciaDisponivel;
	}
	
	public void addItemTransferenciaDisponivel(ItemTransferenciaDisponivel item){
		itensTransferenciaDisponivel.add(item);
	}
	
	public void removeItemTransferenciaDisponivel(ItemTransferenciaDisponivel item){
		itensTransferenciaDisponivel.remove(item);
	}

	/**
	 * Constructor
	 * @throws Exception
	 */
	public CabecalhoTransferenciaDisponivel() throws Exception {
		super();
		
		this.itensTransferenciaDisponivel = new ArrayList<ItemTransferenciaDisponivel>(0);
	}

	public static void defineFields(String className) {
		CabecalhoOperacaoAbstrato.defineFields(className);
		
		defineField( 
				className,
				"id",
				MetaField.label, "Id Operação",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.width, 150,
				MetaField.order, 20
		);
		
		defineField( 
				className,
				"emissao",
				MetaField.label, "Emissão",
				MetaField.visible, true,
				MetaField.required, true,
				MetaField.defaultValue, new Date(),
				MetaField.order, 30
		);
	}
}