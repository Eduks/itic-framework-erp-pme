package br.com.dyad.backoffice.entidade.movimentacao.interfaces;

import java.math.BigDecimal;
import java.util.Date;

import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;

public interface InterfaceItemTransferenciaDisponivel {
	public Entidade getEntidade();
	public void setEntidade(Entidade entidade);
	public Date getEmissao();
	public void setEmissao(Date emissao);
	public void setTotal(BigDecimal total);
	public BigDecimal getTotal();
}
