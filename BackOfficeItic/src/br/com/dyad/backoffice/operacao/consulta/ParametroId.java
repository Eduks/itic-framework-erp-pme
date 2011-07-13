package br.com.dyad.backoffice.operacao.consulta;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.infrastructure.entity.AbstractEntity;

public class ParametroId {
	private String propriedade;
	
	private List<Long> valor;
	
	/**
	 * 
	 * @param propriedade
	 */
	public ParametroId(String propriedade) {
		super();
		this.propriedade = propriedade;		
	}
	
	public void setValorAsLongList(List<Long> valoresLong) {
		this.valor = valoresLong;
	}

	public void setValorAsLong(Long valorLong) {
		List<Long> valoresLong = new ArrayList<Long>();
		valoresLong.add(valorLong);
		
		this.setValorAsLongList(valoresLong);
	}

	public void setValorAsStringList(List<String> valoresString) {
		List<Long> valoresLong = new ArrayList<Long>();
		
		for (String string : valoresString) {
			valoresLong.add(new Long(string));
		}
		
		this.setValorAsLongList(valoresLong);
	}

	public void setValorAsString(String valorString) {
		Long valorLong = new Long(valorString);
		
		this.setValorAsLong(valorLong);
	}

	public void setValorAsEntityList(List<AbstractEntity> valoresEntity) {
		ArrayList<Long> valoresLong = new ArrayList<Long>();
		
		for (AbstractEntity abstractEntity : valoresEntity) {
			valoresLong.add(abstractEntity.getId());
		}
		
		this.setValorAsLongList(valoresLong);
	}

	public void setValorAsEntity(AbstractEntity valorEntity) {
		ArrayList<Long> valoresLong = new ArrayList<Long>();
		valoresLong.add(valorEntity.getId());
		
		this.setValorAsLongList(valoresLong);
	}

	public String getExpressao() {
		String where = null;
		
		if (this.valor != null) {
			if (this.valor.size() == 1) { 
				where = this.propriedade + " = " + this.valor.get(0);
			} else if (this.valor.size() > 1) {
				where = this.propriedade + " IN (" + StringUtils.join(this.valor, ",") + ")";
			}
		}
		return where;
	}
}

