package br.com.dyad.backoffice.entidade.cadastro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForceDiscriminator;
import org.hibernate.annotations.Where;

import br.com.dyad.infrastructure.annotations.FindExpress;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999343")
@FindExpress(value=" and ( UPPER(codigo) LIKE ('%' || UPPER('@#searchToken#@') || '%') or UPPER(nome) LIKE ('%' || UPPER('@#searchToken#@') || '%'))")
@ForceDiscriminator
public class GrupoLancamentoContabilTransfDispo extends GrupoLancamentoContabil {
	
//	@JoinColumn(name="grupoLancamentoContabilTransfDispoId")
	@OneToMany(mappedBy="grupoLancamentoContabilTransfDispo", fetch=FetchType.LAZY)
	@Where(clause="classId='-89999999999336'")
	private List<DefinicaoLancamentoContabilTransfDispo> definicoesTransfDispo;
	
	public static void defineFields(String className) {		
		GrupoLancamentoContabil.defineFields(className);
		
		defineField(
				className,				
				"definicoesTransfDispo",
				MetaField.order, 2460,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "grupoLancamentoContabilTransfDispo",
				MetaField.beanName, DefinicaoLancamentoContabilTransfDispo.class.getName(),
				MetaField.label, "Definições de Grupos Contábeis de Transferência de Disponível"
		);
	}

	@Override
	public List<DefinicaoLancamentoContabilTransfDispo> getDefinicoesVigentes(Date emissao) {
		
		List<DefinicaoLancamentoContabilTransfDispo> definicoesVigentes = new ArrayList<DefinicaoLancamentoContabilTransfDispo>(0);
		
		Date inicio, fim;
		
		for (int i = 0; i < definicoesTransfDispo.size(); i++) {
			
			DefinicaoLancamentoContabilTransfDispo definicao = definicoesTransfDispo.get(0);
			
			inicio = definicao.getInicio();
			
			fim = definicao.getFim();
			if (fim == null) {
				fim = new Date();
			}
			
			if (inicio.compareTo(inicio) <= 0 && fim.compareTo(fim) >= 0) {
				definicoesVigentes.add(definicao);
			}
		}
		
		return definicoesVigentes;
	}

	public List<DefinicaoLancamentoContabilTransfDispo> getDefinicoesTransfDispo() {
		return definicoesTransfDispo;
	}

	public void setDefinicoesTransfDispo(
			List<DefinicaoLancamentoContabilTransfDispo> definicoesTransfDispo) {
		this.definicoesTransfDispo = definicoesTransfDispo;
	}

}
