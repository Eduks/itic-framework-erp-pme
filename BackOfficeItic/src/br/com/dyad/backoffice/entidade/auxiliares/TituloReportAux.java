package br.com.dyad.backoffice.entidade.auxiliares;

import java.math.BigDecimal;
import java.util.Date;

import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.commons.data.AppTempEntity;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;

public class TituloReportAux extends BaseEntity implements AppTempEntity{
	
	//private Long id;
	private Long operacaoId;
	private String numero;
	private Estabelecimento estabelecimento;
	private Entidade entidade;
	private Date emissao;
	private Date vencimento;
	private Date correcao;
	private Date baixa;
	private BigDecimal principal;
	private BigDecimal total;
	
	/*
	 * Getter's e Setters 
	 */
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
	public Long getOperacaoId() {
		return operacaoId;
	}
	public void setOperacaoId(Long operacaoId) {
		this.operacaoId = operacaoId;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}
	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}
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
	public Date getBaixa() {
		return baixa;
	}
	public void setBaixa(Date baixa) {
		this.baixa = baixa;
	}
	public BigDecimal getPrincipal() {
		return principal;
	}
	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public static void defineFields( String className){
		BaseEntity.defineFields(className);

		defineField(
				className, 
				"id",
				MetaField.label, "Id",				
				MetaField.order, 10,
				MetaField.visible, true);

		defineField(
				className, 
				"operacaoId",
				MetaField.label, "Operação",				
				MetaField.order, 20,
				MetaField.visible, true);

		defineField(
				className, 
				"numero",
				MetaField.label, "Número",				
				MetaField.order, 30,
				MetaField.visible, true);

		defineField(
				className, 
				"estabelecimento",
				MetaField.label, "Estabelecimento",				
				MetaField.order, 40,
				MetaField.visible, true,
				MetaField.beanName, Estabelecimento.class.getName());

		defineField(
				className, 
				"entidade",
				MetaField.label, "Entidade",				
				MetaField.order, 50,
				MetaField.visible, true,
				MetaField.beanName, Entidade.class.getName());

		defineField(
				className, 
				"emissao",
				MetaField.label, "Emissão",				
				MetaField.order, 60,
				MetaField.visible, true);

		defineField(
				className, 
				"vencimento",
				MetaField.label, "Vencimento",				
				MetaField.order, 70,
				MetaField.visible, true);

		defineField(
				className, 
				"correcao",
				MetaField.label, "Correção",				
				MetaField.order, 80,
				MetaField.visible, true);

		defineField(
				className, 
				"baixa",
				MetaField.label, "Baixa",				
				MetaField.order, 90,
				MetaField.visible, true);

		defineField(
				className, 
				"principal",
				MetaField.label, "Principal",				
				MetaField.order, 100,
				MetaField.visible, true);
	
		defineField(
				className, 
				"total",
				MetaField.label, "Total",				
				MetaField.order, 110,
				MetaField.visible, true);
	
	}
}