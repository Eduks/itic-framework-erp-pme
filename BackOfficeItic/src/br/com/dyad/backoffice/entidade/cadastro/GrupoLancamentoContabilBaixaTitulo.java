package br.com.dyad.backoffice.entidade.cadastro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import br.com.dyad.infrastructure.annotations.FindExpress;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999344")
@FindExpress(value=" and ( UPPER(codigo) LIKE ('%' || UPPER('@#searchToken#@') || '%') or UPPER(nome) LIKE ('%' || UPPER('@#searchToken#@') || '%'))")
public class GrupoLancamentoContabilBaixaTitulo extends GrupoLancamentoContabil {
	
	@OneToMany(mappedBy="grupoLancamentoContabilBaixaTitulo")
	@Where(clause="classId='-89999999999337'")
	private List<DefinicaoLancamentoContabilBaixaTitulo> definicoesBaixaTitulo;
	
	public static void defineFields(String className) {
		GrupoLancamentoContabil.defineFields(className);
		
		defineField(
				className,				
				"definicoesBaixaTitulo",
				MetaField.order, 2460,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "grupoLancamentoContabilBaixaTitulo",
				MetaField.beanName, DefinicaoLancamentoContabilBaixaTitulo.class.getName(),
				MetaField.label, "Definições de Grupos Contábeis de Baixa de Título"
		);
	}

	@Override
	public List<DefinicaoLancamentoContabilBaixaTitulo> getDefinicoesVigentes(Date emissao) {
		
		List<DefinicaoLancamentoContabilBaixaTitulo> vinculosVigentes = new ArrayList<DefinicaoLancamentoContabilBaixaTitulo>(0);
		
		Date inicio, fim;
		
		for (DefinicaoLancamentoContabilBaixaTitulo vinculo : definicoesBaixaTitulo) {
			
			inicio = vinculo.getInicio();
			
			fim = vinculo.getFim();
			if (fim == null) {
				fim = new Date();
			}
			
			if (inicio.compareTo(inicio) <= 0 && fim.compareTo(fim) >= 0) {
				vinculosVigentes.add(vinculo);
			}
		}
		
		return vinculosVigentes;
	}

	public List<DefinicaoLancamentoContabilBaixaTitulo> getDefinicoesBaixaTitulo() {
		return definicoesBaixaTitulo;
	}

	public void setDefinicoesBaixaTitulo(
			List<DefinicaoLancamentoContabilBaixaTitulo> definicoesBaixaTitulo) {
		this.definicoesBaixaTitulo = definicoesBaixaTitulo;
	}
	
}
