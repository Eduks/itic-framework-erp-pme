package br.com.dyad.backoffice.entidade.movimentacao.interfaces;

import java.math.BigDecimal;
import java.util.Date;

import br.com.dyad.backoffice.entidade.cadastro.TipoPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaAutomatica;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Nucleo;
import br.com.dyad.businessinfrastructure.entidades.tabela.Recurso;
import br.com.dyad.infrastructure.entity.User;

public interface InterfaceItemBaixaAutomatica extends InterfaceItemOperacao {
	public Date getEmissao();
	public void setEmissao(Date emissao);
	public TipoPedido getTipoPedido();
	public void setTipoPedido(TipoPedido tipoPedido);
	public void setEntidade(Entidade entidade);
	public Entidade getEntidade();
	public Estabelecimento getEstabelecimento();
	public void setEstabelecimento(Estabelecimento estabelecimento);
	public Nucleo getNucleo();
	public void setNucleo(Nucleo nucleo);
	public Recurso getRecurso();
	public void setRecurso(Recurso recurso);
	public Long getQuantidade();
	public void setQuantidade(Long quantidade);
	public BigDecimal getUnitario();
	public void setUnitario(BigDecimal unitario);
	public BigDecimal getTotal();
	public void setTotal(BigDecimal total);
	public BigDecimal getFrete();
	public void setFrete(BigDecimal frete);
	public BigDecimal getDesconto();
	public void setDesconto(BigDecimal desconto);
	public BigDecimal getDescontoItem();
	public void setDescontoItem(BigDecimal descontoItem);
	public BigDecimal getBase();
	public void setBase(BigDecimal base);
	public BigDecimal getPrincipal();
	public void setPrincipal(BigDecimal principal);
	public User getAprovador();
	public void setAprovador(User aprovador);
	public Date getAprovacao();
	public void setAprovacao(Date aprovacao);
	public User getCancelador();
	public void setCancelador(User cancelador);
	public ItemBaixaAutomatica getItemBaixaAutomaticaBaixado();
}
