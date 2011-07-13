package br.com.dyad.backoffice.entidade.auxiliares;

import java.math.BigInteger;
import java.util.Date;

import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.commons.data.AppTempEntity;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;

public class PedidoAux extends BaseEntity implements AppTempEntity{
	private BigInteger operacaoId; 
	private Date emissao;
	private Entidade entidade;
	private Estabelecimento estabelecimento; 
	private Recurso recurso;
	private BigInteger quantidade;
	private BigInteger quantidadeBaixada;
	private BigInteger saldo;

	public void setId(BigInteger id) {
		this.id = id.longValue();
	}
	public BigInteger getOperacaoId() {
		return operacaoId;
	}
	public void setOperacaoId(BigInteger operacaoId) {
		this.operacaoId = operacaoId;
	}
	public Date getEmissao() {
		return emissao;
	}
	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}
	public Entidade getEntidade() {
		return entidade;
	}
	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}
	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}
	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}
	public Recurso getRecurso() {
		return recurso;
	}
	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}
	public BigInteger getQuantidade() {
		return quantidade;
	}
	public void setquantidade(BigInteger quantidade) {
		this.quantidade = quantidade;
	}
	public BigInteger getQuantidadeBaixada() {
		return quantidadeBaixada;
	}
	public void setquantidadebaixada(BigInteger quantidadeBaixada) {
		this.quantidadeBaixada = quantidadeBaixada;
	}
	public BigInteger getSaldo() {
		return saldo;
	}
	public void setsaldo(BigInteger saldo) {
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
				"emissao",
				MetaField.label, "Emissão",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 30,
				MetaField.tableViewWidth, 70
		);

		defineField(
				className,
				"entidade",
				MetaField.label, "Entidade",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 40,
				MetaField.beanName, Entidade.class.getName()
		);

		defineField(
				className,
				"estabelecimento",
				MetaField.label, "Estabelecimento",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 50,
				MetaField.beanName, Estabelecimento.class.getName()
		);

		defineField(
				className,
				"recurso",
				MetaField.label, "Recurso",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 60,
				MetaField.beanName, Recurso.class.getName()
		);

		defineField(
				className,
				"quantidade",
				MetaField.label, "Qtd.",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 70,
				MetaField.tableViewWidth, 50
		);

		defineField(
				className,
				"quantidadeBaixada",
				MetaField.label, "Qtd. Bx.",
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
