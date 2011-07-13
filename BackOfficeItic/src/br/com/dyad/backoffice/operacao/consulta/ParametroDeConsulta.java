package br.com.dyad.backoffice.operacao.consulta;

public abstract class ParametroDeConsulta {

	public abstract String pegaFiltro() throws Exception;
	private String campoParaFiltro;	
	
	public ParametroDeConsulta(String campoParaFiltro) {
		this.campoParaFiltro = campoParaFiltro;
	}

	public String getCampoParaFiltro() {
		return campoParaFiltro;
	}
}
