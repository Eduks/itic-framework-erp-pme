package br.com.dyad.backoffice.entidade.movimentacao.interfaces;

import java.math.BigDecimal;
import java.util.Date;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;

public interface InterfaceItemBaixaPedido extends InterfaceItemOperacao {
	public Date getEmissao();
	public void setEmissao(Date emissao);
	public Entidade getEntidade();
	public void setEntidade(Entidade entidade);
	public Estabelecimento getEstabelecimento();
	public void setEstabelecimento(Estabelecimento estabelecimento);
	public ItemPedido getItemPedidoBaixado();
	public void setItemPedidoBaixado(ItemPedido itemPedidoBaixado);
	public Long getQuantidade();
	public void setQuantidade(Long quantidade);
	public BigDecimal getUnitario();
	public void setUnitario(BigDecimal unitario);
	public BigDecimal getTotal();
	public void setTotal(BigDecimal total);
	public BigDecimal getDescontoItem();
	public void setDescontoItem(BigDecimal descontoItem);
}
