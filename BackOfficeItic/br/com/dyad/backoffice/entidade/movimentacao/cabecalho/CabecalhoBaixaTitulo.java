package br.com.dyad.backoffice.entidade.movimentacao.cabecalho;

import java.util.Date;

import br.com.dyad.backoffice.annotations.Cabecalho;
import br.com.dyad.backoffice.annotations.TipoCampoCabecalho;
import br.com.dyad.commons.data.AppTempEntity;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;

public class CabecalhoBaixaTitulo extends CabecalhoOperacaoAbstrato implements AppTempEntity {
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Date emissao;
	
	//TODO melhorar as chamadas dos construtores, fazendo com que o mais específico chame o mais genérico
	public CabecalhoBaixaTitulo(String database) throws Exception {
		this.criaOperacaoId(database);
	}

	public CabecalhoBaixaTitulo(String database, Long operacaoId) throws Exception {
		this.setOperacaoId(operacaoId);
	}

	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
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
				"emissao",
				MetaField.label, "Emissão",
				MetaField.visible, true,
				MetaField.order, 20
		);
	}
}