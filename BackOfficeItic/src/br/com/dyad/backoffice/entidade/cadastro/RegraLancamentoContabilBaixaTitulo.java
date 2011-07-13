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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.dyad.infrastructure.annotations.MetaField;

@Entity
@Table(name="TABELA")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="classId", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="-99999899999147")
public class RegraLancamentoContabilBaixaTitulo extends RegraLancamentoContabil {
	
	@ManyToOne
    @JoinColumn(name="tipoPedidoId")
	private TipoPedido tipoPedido;
	
	@OneToMany(mappedBy="regraLancamentoContabilBaixaTitulo")
	private List<VinculoRegraGrupoContabilBaixaTitulo> vinculosRegraGrupoContabilBaixaTitulo;
	
	public TipoPedido getTipoPedido() {
		return tipoPedido;
	}

	public void setTipoPedido(TipoPedido tipoPedido) {
		this.tipoPedido = tipoPedido;
	}

	/**
	 * Constructor
	 */
	public RegraLancamentoContabilBaixaTitulo() {
		super();
	}

	public static void defineFields(String className) {
		RegraLancamentoContabilFiscal.defineFields(className);
		
		defineField(
				className,				
				"codigo",
				MetaField.visible, false
		);
		
		defineField(
				className,				
				"tipoPedido",
				MetaField.label, "Tipo do Pedido",
				MetaField.order, 200,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.beanName, TipoPedido.class.getName()
		);
		
		defineField(
				className,				
				"vinculosRegraGrupoContabilBaixaTitulo",
				MetaField.order, 2460,
				MetaField.visible, true,
				MetaField.required, false, 
				MetaField.masterFieldNames, "id",
				MetaField.detailFieldNames, "regraLancamentoContabilBaixaTitulo",
				MetaField.beanName, VinculoRegraGrupoContabilBaixaTitulo.class.getName(),
				MetaField.label, "Grupos de Lançamento Contábil para Baixa de Título"
		);
		
	}

	@Override
	public List<VinculoRegraGrupoContabilBaixaTitulo> getVinculosRegraGrupoContabilVigentes(Date emissao) {
		
		List<VinculoRegraGrupoContabilBaixaTitulo> vinculosVigentes = new ArrayList<VinculoRegraGrupoContabilBaixaTitulo>(0);
		
		Date inicio, fim;
		
		for (VinculoRegraGrupoContabilBaixaTitulo vinculo : vinculosRegraGrupoContabilBaixaTitulo) {
			
			inicio = vinculo.getInicio();
			
			fim = vinculo.getFim();
			if (fim == null) {
				fim = new Date();
			}
			
			if (inicio.compareTo(emissao) <= 0 && fim.compareTo(emissao) >= 0) {
				vinculosVigentes.add(vinculo);
			}
		}
		
		return vinculosVigentes;
	}

	public List<VinculoRegraGrupoContabilBaixaTitulo> getVinculosRegraGrupoContabilBaixaTitulo() {
		return vinculosRegraGrupoContabilBaixaTitulo;
	}

	public void setVinculosRegraGrupoContabilBaixaTitulo(
			List<VinculoRegraGrupoContabilBaixaTitulo> vinculosRegraGrupoContabilBaixaTitulo) {
		this.vinculosRegraGrupoContabilBaixaTitulo = vinculosRegraGrupoContabilBaixaTitulo;
	}
}