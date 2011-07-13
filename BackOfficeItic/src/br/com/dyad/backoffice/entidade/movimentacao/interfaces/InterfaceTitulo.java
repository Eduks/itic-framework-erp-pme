package br.com.dyad.backoffice.entidade.movimentacao.interfaces;

import java.math.BigDecimal;
import java.util.Date;

import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;

public interface InterfaceTitulo {
	public Entidade getEntidade();
	public void setEntidade(Entidade entidade);
	public String getNumero();
	public void setNumero(String numero);
	public BigDecimal getJuro();
	public void setJuro(BigDecimal juro);
	public BigDecimal getMulta();
	public void setMulta(BigDecimal multa);
	public BigDecimal getTarifa();
	public void setTarifa(BigDecimal tarifa);
	public BigDecimal getPrincipal();
	public void setPrincipal(BigDecimal principal);
	public BigDecimal getTotal();
	public void setTotal(BigDecimal total);
	public Date getVencimento();
	public void setVencimento(Date vencimento);
	public Date getCorrecao();
	public void setCorrecao(Date correcao);
	public Date getEmissao();
	public void setEmissao(Date emissao);
}
