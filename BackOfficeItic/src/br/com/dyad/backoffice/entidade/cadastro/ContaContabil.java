package br.com.dyad.backoffice.entidade.cadastro;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.tabela.Tabela;
import br.com.dyad.infrastructure.annotations.FindExpress;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999193")
@FindExpress(value=" and ( UPPER(codigo) LIKE ('%' || UPPER('@#searchToken#@') || '%') or UPPER(nome) LIKE ('%' || UPPER('@#searchToken#@') || '%'))")
public class ContaContabil extends Tabela {	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="planoContaId")
	private PlanoConta planoConta;

	@Column(length=50)
	private String apelido;

	@Column
	private Boolean sintetico; //se TRUE, não pode ter lançamentos. se FALSE, pode ter lançamentos (analítico)

	@ManyToOne
	@JoinColumn(name="entidadeId", nullable=true)
	private Entidade entidade;

	/**
	 * Conctructor	
	 */
	public ContaContabil() {
		super();
	}

	public PlanoConta getPlanoConta() {
		return planoConta;
	}

	public void setPlanoConta(PlanoConta planoConta) {
		this.planoConta = planoConta;
	}

	public Boolean getSintetico() {
		return sintetico;
	}

	public void setSintetico(Boolean sintetico) {
		this.sintetico = sintetico;
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public static void defineFields(String className) {
		Tabela.defineFields(className);		

		defineField(
				className,
				"sintetico",				 
				MetaField.order, 90,
				MetaField.tableViewWidth, 100,
				MetaField.label, "Conta Sintética",
				MetaField.readOnly, true
		);

		defineField(
				className,
				"codigo",				
				MetaField.order, 200,
				MetaField.tableViewWidth, 100

		);		

		defineField(
				className,
				"apelido",				 
				MetaField.order, 250,
				MetaField.width, 100,
				MetaField.tableViewWidth, 100,
				MetaField.label, "Apelido",
				MetaField.column, 1
		);

		defineField(
				className,
				"nome",				 
				MetaField.order, 300,
				MetaField.width, 300,
				MetaField.tableViewWidth, 250
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

	}

	@Override
	public boolean equals(Object obj) {

		if (getCodigo() == null) {

			return super.equals(obj);

		} else {
			try{
				ContaContabil c = (ContaContabil)obj;

				long plano1 = planoConta.getId();
				long plano2 = c.getPlanoConta().getId();

				if (plano1 == plano2){
					return getCodigo().equalsIgnoreCase(((ContaContabil)obj).getCodigo());
				}
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}

	public Entidade getEntidade() {
		return entidade;
	}

	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}
}