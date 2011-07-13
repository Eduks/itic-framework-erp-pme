package br.com.dyad.backoffice.entidade.auxiliares;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.commons.data.AppTempEntity;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;

public class TituloAux extends BaseEntity implements AppTempEntity{
	private BigInteger operacaoId; 
	private Entidade entidade;
	private Date emissao;
	private Date vencimento;
	private Date correcao;
	private BigDecimal valor;
	private BigDecimal valorBaixado;
	private BigDecimal saldo;

	public void setId(BigInteger id) {
		this.id = id.longValue();
	}
	public BigInteger getOperacaoId() {
		return operacaoId;
	}
	public void setOperacaoId(BigInteger operacaoId) {
		this.operacaoId = operacaoId;
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
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public BigDecimal getValorBaixado() {
		return valorBaixado;
	}
	public void setValorBaixado(BigDecimal valorBaixado) {
		this.valorBaixado = valorBaixado;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	
	public static void defineFields(String className) {
		BaseEntity.defineFields(className);
		
		defineField(
				className,
				"id", 
				MetaField.label, "Id",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 10,
				MetaField.tableViewWidth, 70
		);

		defineField(
				className,
				"operacaoId",
				MetaField.label, "Operação",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 20,
				MetaField.tableViewWidth, 70
				
		);

		defineField(
				className,
				"entidade", 
				MetaField.label, "Entidade",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 30,
				MetaField.beanName, Entidade.class.getName(),
				MetaField.tableViewWidth, 70
		);

		defineField(
				className,
				"emissao",
				MetaField.label, "Emissão",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 40,
				MetaField.tableViewWidth, 70
		);

		defineField(
				className,
				"vencimento",
				MetaField.label, "Vencimento",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 50,
				MetaField.tableViewWidth, 70
		);

		defineField(
				className,
				"correcao",
				MetaField.label, "Correção",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 60,
				MetaField.tableViewWidth, 70
		);

		defineField(
				className,
				"valor",
				MetaField.label, "Valor",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 70,
				MetaField.tableViewWidth, 50
		);

		defineField(
				className,
				"valorBaixado",
				MetaField.label, "Valor. Bx.",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 80,
				MetaField.tableViewWidth, 50
		);

		defineField(
				className,
				"saldo",
				MetaField.label, "Saldo",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 90,
				MetaField.tableViewWidth, 50
		);
	}	
}
