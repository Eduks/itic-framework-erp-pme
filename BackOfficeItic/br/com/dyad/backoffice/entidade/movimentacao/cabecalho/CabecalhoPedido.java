package br.com.dyad.backoffice.entidade.movimentacao.cabecalho;

import java.math.BigDecimal;
import java.util.Date;

import br.com.dyad.backoffice.annotations.Cabecalho;
import br.com.dyad.backoffice.annotations.TipoCampoCabecalho;
import br.com.dyad.businessinfrastructure.entidades.entidade.Entidade;
import br.com.dyad.businessinfrastructure.entidades.entidade.Estabelecimento;
import br.com.dyad.businessinfrastructure.entidades.entidade.Pessoa;
import br.com.dyad.commons.data.AppTempEntity;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;

public class CabecalhoPedido extends CabecalhoOperacaoAbstrato implements AppTempEntity, InterfaceCabecalhoOperacao {
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Entidade entidade;
	
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Estabelecimento estabelecimento;

	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Date emissao;
	
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Date aprovacao;
	
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Pessoa aprovador;
	
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Date cancelamento;
	
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Pessoa cancelador;
	
	//Estas duas propriedades devem ser gravados nos eventos, por isso são transientes.
	@Cabecalho(TipoCampoCabecalho.TOTALIZA)
	private BigDecimal frete;
	
	@Cabecalho(TipoCampoCabecalho.TOTALIZA)
	private BigDecimal desconto;

	//TODO melhorar as chamadas dos construtores, fazendo com que o mais específico chame o mais genérico
	public CabecalhoPedido(String database) throws Exception {
		this.criaOperacaoId(database);
	}

	public CabecalhoPedido(String database, Long operacaoId) throws Exception {
		this.setOperacaoId(operacaoId);
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

	public Pessoa getAprovador() {
		return aprovador;
	}

	public void setAprovador(Pessoa aprovador) {
		this.aprovador = aprovador;
	}

	public Date getCancelamento() {
		return this.cancelamento;
	}

	public void setCancelamento(Date cancelamento) {
		this.cancelamento = cancelamento;
	}

	public Pessoa getCancelador() {
		return this.cancelador;
	}

	public void setCancelador(Pessoa cancelador) {
		this.cancelador = cancelador;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public BigDecimal getFrete() {
		return frete;
	}

	public void setFrete(BigDecimal frete) {
		this.frete = frete;
	}

	public static void defineFields(String className) {
		BaseEntity.defineFields(className);
		
		defineField( 
				className,
				"classeOperacaoId",
				MetaField.visible, false
		);

		defineField( 
				className,
				"operacaoId",
				MetaField.label, "Operação",
				MetaField.visible, true,
				MetaField.readOnly, true,
				MetaField.order, 10
		);

		defineField( 
				className,
				"entidade",
				MetaField.label, "Entidade",
				MetaField.visible, true,
				MetaField.order, 20,
				MetaField.beanName, Entidade.class.getName()
		);

		defineField( 
				className,
				"estabelecimento",
				MetaField.label, "Estabelecimento",
				MetaField.visible, true,
				MetaField.order, 30,
				MetaField.beanName, Estabelecimento.class.getName()
		);

		defineField( 
				className,
				"emissao",
				MetaField.label, "Emissão",
				MetaField.visible, true,
				MetaField.order, 40
		);

		defineField( 
				className,
				"desconto",
				MetaField.label, "Desconto",
				MetaField.visible, true,
				MetaField.order, 50
		);

		defineField( 
				className,
				"frete",
				MetaField.label, "Frete",
				MetaField.visible, true,
				MetaField.order, 60
		);

		defineField( 
				className,
				"aprovacao",
				MetaField.label, "Aprovação",
				MetaField.visible, true,
				MetaField.order, 70,
				MetaField.readOnly, true
		);

		defineField( 
				className,
				"aprovador",
				MetaField.label, "Aprovador",
				MetaField.visible, true,
				MetaField.order, 80,
				MetaField.beanName, Pessoa.class.getName(),
				MetaField.readOnly, true
		);

		defineField( 
				className,
				"cancelamento",
				MetaField.label, "Cancelamento",
				MetaField.visible, true,
				MetaField.order, 90,
				MetaField.readOnly, true
		);

		defineField( 
				className,
				"cancelador",
				MetaField.label, "Cancelador",
				MetaField.visible, true,
				MetaField.order, 100,
				MetaField.beanName, Pessoa.class.getName(),
				MetaField.readOnly, true
		);
	}
}