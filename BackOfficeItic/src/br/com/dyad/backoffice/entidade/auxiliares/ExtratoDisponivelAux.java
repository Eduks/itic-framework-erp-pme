package br.com.dyad.backoffice.entidade.auxiliares;

import java.math.BigDecimal;
import java.util.Date;

import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.commons.data.AppTempEntity;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;

public class ExtratoDisponivelAux extends BaseEntity implements AppTempEntity{
	
	Entidade entidade;
	Date emissao;
	BigDecimal debito;
	BigDecimal credito;
	BigDecimal saldo;
	String historico;
	
	public Entidade getEntidade() {
		return entidade;
	}
	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}
	public Date getEmissao() {
		return emissao;
	}
	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}
	public BigDecimal getDebito() {
		return debito;
	}
	public void setDebito(BigDecimal debito) {
		this.debito = debito;
	}
	public BigDecimal getCredito() {
		return credito;
	}
	public void setCredito(BigDecimal credito) {
		this.credito = credito;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public String getHistorico() {
		return historico;
	}
	public void setHistorico(String historico) {
		this.historico = historico;
	}
	public static void defineFields( String className){
		BaseEntity.defineFields(className);

		defineField(
				className, 
				"id",
				MetaField.label, "Ordem",				
				MetaField.order, 10,
				MetaField.visible, true);

		defineField(
				className, 
				"entidade",
				MetaField.label, "Entidade",				
				MetaField.order, 20,
				MetaField.visible, true,
				MetaField.tableViewWidth, 350,
				MetaField.beanName, Entidade.class.getName());

		defineField(
				className, 
				"emissao",
				MetaField.label, "Emissão",				
				MetaField.order, 30,
				MetaField.visible, true);

		defineField(
				className, 
				"debito",
				MetaField.label, "Débito",				
				MetaField.order, 40,
				MetaField.visible, true);

		defineField(
				className, 
				"credito",
				MetaField.label, "Crédito",				
				MetaField.order, 50,
				MetaField.visible, true);

		defineField(
				className, 
				"saldo",
				MetaField.label, "Saldo",				
				MetaField.order, 60,
				MetaField.visible, true);

		defineField(
				className, 
				"historico",
				MetaField.label, "Histórico",				
				MetaField.order, 70,
				MetaField.visible, true);
	}
}