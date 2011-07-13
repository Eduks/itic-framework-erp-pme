package br.com.dyad.backoffice.entidade.movimentacao.cabecalho;

import java.util.Date;

import br.com.dyad.backoffice.annotations.Cabecalho;
import br.com.dyad.backoffice.annotations.TipoCampoCabecalho;
import br.com.dyad.commons.data.AppTempEntity;
import br.com.dyad.infrastructure.annotations.MetaField;

public class CabecalhoTransferenciaDisponivel extends CabecalhoOperacaoAbstrato implements AppTempEntity, InterfaceCabecalhoOperacao {
	@Cabecalho(TipoCampoCabecalho.REPLICA)
	private Date emissao;

	//TODO melhorar as chamadas dos construtores, fazendo com que o mais específico chame o mais genérico
	public CabecalhoTransferenciaDisponivel(String database) throws Exception {
		this.criaOperacaoId(database);
	}

	public CabecalhoTransferenciaDisponivel(String database, Long operacaoId) throws Exception {
		this.setOperacaoId(operacaoId);
	}

	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}

	public static void defineFields(String className) {
		CabecalhoOperacaoAbstrato.defineFields(className);
		
		defineField( 
				className,
				"operacao",
				MetaField.label, "Operação",
				MetaField.visible, true,
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