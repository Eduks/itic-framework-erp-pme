package br.com.dyad.backoffice.operacao.consulta;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.infrastructure.entity.AbstractEntity;

public class ParametroDeConsultaDeBeans extends ParametroDeConsulta {

	public ParametroDeConsultaDeBeans(String campoFiltro) {
		super(campoFiltro);
	}
	
	private AbstractEntity objeto;
	
	private Long id;
	
	private String stringId;

	private List<AppEntity> objetos;

	private List<Long> listaDeIds;

	private String idsSeparadosPorVirgula;
	
	public void limpaValores(){
		this.objeto = null;
		this.id = null;
		this.stringId = null;
		this.objetos = null;
		this.listaDeIds = null;
		this.idsSeparadosPorVirgula = null;
	}
	
	public Boolean existeAlgumValorPreenchido(){
		return objeto != null || id != null || stringId != null || objetos != null || listaDeIds != null || idsSeparadosPorVirgula != null;
	}
	
	private void verificaSeJaExisteAlgumValorPreenchido(){
		if ( existeAlgumValorPreenchido() ){
			throw new AppException("Já existe um valor preenchido para esta classe.");
		}
	}
	
	@Override
	public String pegaFiltro() throws IllegalArgumentException, IllegalAccessException {
		if ( objeto != null ){
			return this.getCampoParaFiltro() + " = " + this.objeto.getId(); 
		}
		
		if ( id != null ){
			return this.getCampoParaFiltro() + " = " + this.id; 
		}

		if ( stringId != null ){
			return this.getCampoParaFiltro() + " = '" + this.stringId + "'"; 
		}

		if ( objetos != null ){
			ArrayList<Long> listaDeIdsDosObjetos = new ArrayList<Long>();
			for (AppEntity entidade : objetos) {
				listaDeIdsDosObjetos.add( entidade.getId() );
			}
			
			return this.getCampoParaFiltro() + " in (" + StringUtils.join(listaDeIdsDosObjetos,",")  + ")"; 
		}
		
		if ( listaDeIds != null ){
			return this.getCampoParaFiltro() + " in (" + StringUtils.join(listaDeIds,",")  + ")"; 
		}

		if ( idsSeparadosPorVirgula != null ){
			return this.getCampoParaFiltro() + " in (" + idsSeparadosPorVirgula + ")"; 
		}

		return "";
	}

	public AbstractEntity getObjeto() {
		return objeto;
	}

	public void setObjeto(AbstractEntity objeto) {
		verificaSeJaExisteAlgumValorPreenchido();
		this.objeto = objeto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		verificaSeJaExisteAlgumValorPreenchido();
		this.id = id;
	}

	public Long getStringId() {
		return id;
	}

	public void setStringId(String stringId) {
		verificaSeJaExisteAlgumValorPreenchido();
		this.stringId = stringId;
	}

	public List<AppEntity> getObjetos() {
		return objetos;
	}

	public void setObjetos(List<AppEntity> objetos) {
		verificaSeJaExisteAlgumValorPreenchido();
		this.objetos = objetos;
	}

	public List<Long> getListaDeIds() {
		return listaDeIds;
	}

	public void setListaDeIds(List<Long> listaDeIds) {
		verificaSeJaExisteAlgumValorPreenchido();
		this.listaDeIds = listaDeIds;
	}

	public String getIdsSeparadosPorVirgula() {
		return idsSeparadosPorVirgula;
	}

	public void setIdsSeparadosPorVirgula(String idsSeparadosPorVirgula) {
		verificaSeJaExisteAlgumValorPreenchido();
		this.idsSeparadosPorVirgula = idsSeparadosPorVirgula;
	}
}
