package br.com.dyad.backoffice.operacao.consulta;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;

@Entity
public class ResultadoSaldoDisponivel {
	@Id
	@Column
	private Long id;
	
	@ManyToOne
	private Entidade entidade;
	
	@Column
	private BigDecimal total;
	
	public ResultadoSaldoDisponivel(Entidade entidade, BigDecimal total) {
		this.setId(System.currentTimeMillis());
		this.setEntidade(entidade);
		this.setTotal(total);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Entidade getEntidade() {
		return entidade;
	}
	public void setEntidade(Entidade entidade) {
		this.entidade = entidade;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}