package br.com.dyad.backoffice.entidade.movimentacao.cabecalho;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.MovDispo;
import br.com.dyad.backoffice.entidade.movimentacao.interfaces.Cabecalho;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="OPERACAO")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999386")
public class CabecalhoBaixaTitulo extends CabecalhoOperacaoAbstrato implements Cabecalho {
	@Temporal(TemporalType.DATE)
	private Date emissao;
	
	@OneToMany(cascade={CascadeType.ALL,CascadeType.REMOVE},mappedBy="cabecalho")
	private List<ItemBaixaTitulo> itensBaixaTitulo;
	
	@OneToMany(cascade={CascadeType.ALL,CascadeType.REMOVE},mappedBy="cabecalho")
	private List<MovDispo> movDispos;
	
	/**
	 * Contructor
	 * @throws Exception
	 */
	public CabecalhoBaixaTitulo() throws Exception {
		super();
		
		this.itensBaixaTitulo = new ArrayList<ItemBaixaTitulo>();
		this.movDispos = new ArrayList<MovDispo>();
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

	public void addItemBaixaTitulo(ItemBaixaTitulo itemBaixaTitulo) {
		this.itensBaixaTitulo.add(itemBaixaTitulo);
	}

	public void removeItemBaixaTitulo(ItemBaixaTitulo itemBaixaTitulo) {
		this.itensBaixaTitulo.remove(itemBaixaTitulo);
}

	public List<MovDispo> getMovDispos() {
		return movDispos;
	}

	public void setMovDispos(List<MovDispo> movDispos) {
		this.movDispos = movDispos;
	}

	public void addMovDispo(MovDispo movDispo) {
		this.movDispos.add(movDispo);
	}

	public void removeMovDispo(MovDispo movDispo) {
		this.movDispos.remove(movDispo);
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
				MetaField.width, 150,
				MetaField.order, 30
		);
	}
}