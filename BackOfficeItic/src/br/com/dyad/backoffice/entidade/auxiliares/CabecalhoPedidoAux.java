package br.com.dyad.backoffice.entidade.auxiliares;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;

@Entity
@Table(name="TABELA")
public class CabecalhoPedidoAux {
	@Id
	@Column
	private Long operacaoId;

	@ManyToOne
    @JoinColumn(name="entidadeId")
	private Entidade entidade;

	@ManyToOne
    @JoinColumn(name="estabelecimentoId")
	private Estabelecimento estabelecimento;

	@Column
	private Date emissao;
	
	@Column
	private Date aprovacao;

	@ManyToOne
    @JoinColumn(name="aprovadorId")
	private Entidade aprovador;

	@Column
	private Date cancelamento;

	@ManyToOne
    @JoinColumn(name="canceladorId")
	private Entidade cancelador;

	public Long getOperacaoId() {
		return operacaoId;
	}

	public void setOperacaoId(Long operacaoId) {
		this.operacaoId = operacaoId;
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

	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}

	public Date getAprovacao() {
		return aprovacao;
	}

	public void setAprovacao(Date aprovacao) {
		this.aprovacao = aprovacao;
	}

	public Entidade getAprovador() {
		return aprovador;
	}

	public void setAprovador(Entidade aprovador) {
		this.aprovador = aprovador;
	}

	public Date getCancelamento() {
		return cancelamento;
	}

	public void setCancelamento(Date cancelamento) {
		this.cancelamento = cancelamento;
	}

	public Entidade getCancelador() {
		return cancelador;
	}

	public void setCancelador(Entidade cancelador) {
		this.cancelador = cancelador;
	}

}
