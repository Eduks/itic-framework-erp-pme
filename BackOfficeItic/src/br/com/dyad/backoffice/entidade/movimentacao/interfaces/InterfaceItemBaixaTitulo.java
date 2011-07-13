package br.com.dyad.backoffice.entidade.movimentacao.interfaces;

import java.math.BigDecimal;
import java.util.Date;

import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;

public interface InterfaceItemBaixaTitulo extends InterfaceItemOperacao {
    public void setEmissao(Date emissao);
	public Date getEmissao();
	public void setTituloBaixado(Titulo tituloBaixado);
	public Titulo getTituloBaixado();
	public Entidade getEntidade();
	public void setEntidade(Entidade entidade);
	public BigDecimal getTotal();
	public void setTotal(BigDecimal total);
}
