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
	private Long operacaoId; 
	private Date emissao;
	private Entidade entidade;
	private Estabelecimento estabelecimento; 
	private Recurso recurso;
	private Long quantidade;
	private Long quantidadeBaixada;
	private Long saldo;

	public void setId(BigInteger id) {
		this.id = id.longValue();
	}
	public Long getOperacaoId() {
		return operacaoId;
	}
	public void setOperacaoId(Long operacaoId) {
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
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	public Long getQuantidadeBaixada() {
		return quantidadeBaixada;
	}
	public void setQuantidadeBaixada(Long quantidadeBaixada) {
		this.quantidadeBaixada = quantidadeBaixada;
	}
	public Long getSaldo() {
		return saldo;
	}
	public void setSaldo(Long saldo) {
		this.saldo = saldo;
	}
	
	public static void defineFields(String className) {
		BaseEntity.defineFields(className);
	
		defineField(
				className,
				"operacaoId",
				MetaField.label, "Operação",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 10,	
				MetaField.tableViewWidth, 70
				
		);
		
		defineField(
				className,
				"id", 
				MetaField.label, "Item",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 20,
				MetaField.width, 150,
				MetaField.tableViewWidth, 70
		);

		defineField(
				className,
				"emissao",
				MetaField.label, "Emissão",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 30,
				MetaField.width, 150,
				MetaField.tableViewWidth, 70
		);

		defineField(
				className,
				"entidade",
				MetaField.label, "Entidade",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 40,
				MetaField.tableViewWidth, 160,
				MetaField.beanName, Entidade.class.getName()
		);

		defineField(
				className,
				"estabelecimento",
				MetaField.label, "Estabelecimento",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 50,
				MetaField.tableViewWidth, 160,
				MetaField.beanName, Estabelecimento.class.getName()
		);

		defineField(
				className,
				"recurso",
				MetaField.label, "Recurso",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 60,
				MetaField.tableViewWidth, 160,
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
