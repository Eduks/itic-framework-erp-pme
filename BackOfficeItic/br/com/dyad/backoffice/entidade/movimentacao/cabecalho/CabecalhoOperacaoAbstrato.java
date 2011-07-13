package br.com.dyad.backoffice.entidade.movimentacao.cabecalho;

import br.com.dyad.backoffice.annotations.Cabecalho;
import br.com.dyad.backoffice.annotations.TipoCampoCabecalho;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;
import br.com.dyad.infrastructure.entity.KeyGenerator;

public abstract class CabecalhoOperacaoAbstrato extends BaseEntity implements InterfaceCabecalhoOperacao {
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private String classeOperacaoId = null;
	
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Long operacaoId;

	public Long getOperacaoId() {
		return operacaoId;
	}

	//TODO tivemos que colocar este metodo como public por conta da grid. Arranjar forma para que o metodo nao fique como publico.	
	public void setOperacaoId(Long operacao) {
		this.operacaoId = operacao;
	}

	public String getClasseOperacaoId() {
		return classeOperacaoId;
	}

	public void setClasseOperacaoId(String classeOperacaoId) {
		this.classeOperacaoId = classeOperacaoId;
	}

	public void criaOperacaoId(String database) throws Exception{
		this.setOperacaoId( KeyGenerator.getInstance(database).generateNoLicenseKey() );		
	}

	public static void defineFields(String className) {
		BaseEntity.defineFields(className);
		
		defineField( 
				className,
				"classeOperacaoId",
				MetaField.visible, false,
				MetaField.order, 5
		);
	}
}
