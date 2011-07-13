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

import br.com.dyad.infrastructure.annotations.FindExpress;
import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-89999999999345")
@FindExpress(value=" and ( UPPER(codigo) LIKE ('%' || UPPER('@#searchToken#@') || '%') or UPPER(nome) LIKE ('%' || UPPER('@#searchToken#@') || '%'))")
public class GrupoLancamentoContabilBaixaPedido extends GrupoLancamentoContabil {
	
	@OneToMany(mappedBy="grupoLancamentoContabilBaixaPedido")
	private List<DefinicaoLancamentoContabilBaixaPedido> definicoesBaixaPedido;
	
	public static void defineFields(String className) {
		GrupoLancamentoContabil.defineFields(className);
		
		defineField(
				className,				
				"definicoesBaixaPedido",
				MetaField.order, 2460,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "grupoLancamentoContabilBaixaPedido",
				MetaField.beanName, DefinicaoLancamentoContabilBaixaPedido.class.getName(),
				MetaField.label, "Definições de Grupos Contábeis de Baixa de Pedido"
		);
	}

	@Override
	public List<?> getDefinicoesVigentes(Date emissao) {
		
		List<DefinicaoLancamentoContabilBaixaPedido> vinculosVigentes = new ArrayList<DefinicaoLancamentoContabilBaixaPedido>(0);
		
		Date inicio, fim;
		
		for (DefinicaoLancamentoContabilBaixaPedido vinculo : definicoesBaixaPedido) {
			
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

	public List<DefinicaoLancamentoContabilBaixaPedido> getDefinicoesBaixaPedido() {
		return definicoesBaixaPedido;
	}

	public void setDefinicoesBaixaPedido(
			List<DefinicaoLancamentoContabilBaixaPedido> definicoesBaixaPedido) {
		this.definicoesBaixaPedido = definicoesBaixaPedido;
	}

}
