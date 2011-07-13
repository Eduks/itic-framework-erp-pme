package br.com.dyad.backoffice.entidade.auxiliares;

import br.com.dyad.commons.data.AppTempEntity;
import br.com.dyad.infrastructure.annotations.MetaField;
import br.com.dyad.infrastructure.entity.BaseEntity;


public class InventarioFisicoEstoqueReportBean extends BaseEntity implements AppTempEntity {
	
	private String recurso;
	private Long qtdeEntrada;
	private Long qtdeConsumo;
	private Long saldo;

	public InventarioFisicoEstoqueReportBean() {
		super();
		
		saldo = 0L;
	}
	
	public static void defineFields( String className){
		BaseEntity.defineFields(className);
		
		int ordem = 0;
		
		defineField( 
				className,
				"recurso",
				MetaField.order, ++ordem,
				MetaField.tableViewWidth, 600,
				MetaField.label, "Recursos"
		);
		defineField( 
				className,
				"qtdeEntrada",
				MetaField.order, ++ordem,
				MetaField.tableViewWidth, 35,
				MetaField.label, "Entrada"
		);
		defineField( 
				className,
				"qtdeConsumo",
				MetaField.order, ++ordem,
				MetaField.tableViewWidth, 35,
				MetaField.label, "Consumo"
		);
		defineField( 
				className,
				"saldo",
				MetaField.order, ++ordem,
				MetaField.tableViewWidth, 35,
				MetaField.label, "Saldo"
		);
		
	}
	
	public String getRecurso() {
		return recurso;
	}

	public void setRecurso(String recurso) {
		this.recurso = recurso;
	}

	public Long getQtdeEntrada() {
		return qtdeEntrada;
	}

	public void setQtdeEntrada(Long qtdeEntrada) {
		this.qtdeEntrada = qtdeEntrada;
		
		saldo += qtdeEntrada;
	}

	public Long getQtdeConsumo() {
		return qtdeConsumo;
	}

	public void setQtdeConsumo(Long qtdeConsumo) {
		this.qtdeConsumo = qtdeConsumo;
		
		saldo += qtdeConsumo*(-1);
	}

	public Long getSaldo() {
		return saldo;
	}

	public void setSaldo(Long saldo) {
		this.saldo = saldo;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof InventarioFisicoEstoqueReportBean) {
			InventarioFisicoEstoqueReportBean bean = (InventarioFisicoEstoqueReportBean)obj;
			
			if (bean.getRecurso() != null && recurso != null) {
				return bean.getRecurso().equals(recurso);
			} else {
				return false;
			}
		}
		
		
		return false;
	}
	
	public void addQtdeEntrada(Long qtdeEntrada) {
		this.qtdeEntrada += qtdeEntrada;
	}
	
	public void addQtdeConsumo(Long qtdeConsumo) {
		this.qtdeConsumo += qtdeConsumo;
	}
	
	public void calculaSaldo(){
		saldo = qtdeEntrada - qtdeConsumo;
	}

}
