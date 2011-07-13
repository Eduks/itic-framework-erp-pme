package br.com.dyad.backoffice.entidade.cadastro;

import java.util.ArrayList;
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

import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoHistorico;
import br.com.dyad.backoffice.entidade.movimentacao.evento.EventoTravamento;
import br.com.dyad.businessinfrastructure.entidades.tabela.Tabela;
import br.com.dyad.infrastructure.annotations.FindExpress;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999195")
@FindExpress(value=" and ( UPPER(codigo) LIKE ('%' || UPPER('@#searchToken#@') || '%') or UPPER(nome) LIKE ('%' || UPPER('@#searchToken#@') || '%'))")
public class PlanoConta extends Tabela {

	@OneToMany(mappedBy="planoConta", cascade=CascadeType.ALL)
	private List<ContaContabil> contasContabeis;

	@OneToMany(mappedBy="planoConta")
	private List<EventoTravamento> eventosTravamento;

	@OneToMany(mappedBy="planoConta")
	private List<EventoHistorico> eventosHistorico;
	
	/**
	 * Constructor
	 */
	public PlanoConta() {
		super();
		
		contasContabeis = new ArrayList<ContaContabil>(0);
		eventosTravamento = new ArrayList<EventoTravamento>(0);
		eventosHistorico = new ArrayList<EventoHistorico>(0);
	}

	public List<ContaContabil> getContasContabeis() {
		return contasContabeis;
	}
	public void setContasContabeis(List<ContaContabil> contasContabeis) {
		this.contasContabeis = contasContabeis;
	}

	public void addContaContabil(ContaContabil contaContabil){
		contasContabeis.add(contaContabil);
	}

	public List<EventoTravamento> getEventosTravamento() {
		return eventosTravamento;
	}
	public void setEventosTravamento(List<EventoTravamento> eventosTravamento) {
		this.eventosTravamento = eventosTravamento;
	}
	public List<EventoHistorico> getEventosHistorico() {
		return eventosHistorico;
	}
	public void setEventosHistorico(List<EventoHistorico> eventosHistorico) {
		this.eventosHistorico = eventosHistorico;
	}

	public static void defineFields(String className) {
		Tabela.defineFields(className);
		
		defineField(
				className,
				"codigo", 
				MetaField.required, true, 
				MetaField.order, 100,
				MetaField.width, 100
		);		

		defineField(
				className,
				"nome", 
				MetaField.required, true, 
				MetaField.order, 200,
				MetaField.width, 200,
				MetaField.tableViewWidth, 500,
				MetaField.column, 1
		);
		
		defineField(
				className,				
				"contasContabeis",
				MetaField.order, 300,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.beanName, ContaContabil.class.getName(),
				MetaField.masterFieldNames, "id",                
				MetaField.detailFieldNames, "planoConta",
				MetaField.label, "Contas Contábeis"
		);
	}
}