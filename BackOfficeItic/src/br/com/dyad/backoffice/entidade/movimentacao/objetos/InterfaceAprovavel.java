package br.com.dyad.backoffice.entidade.movimentacao.objetos;

public interface InterfaceAprovavel {
	public void aprova(Long operacaoId) throws Exception;
	public void desaprova(Long operacaoId) throws Exception;
}
