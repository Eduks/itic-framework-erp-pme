package br.com.dyad.backoffice.entidade.movimentacao.objetos_interface;

public interface InterfaceCancelavel {
	public void cancela(Long operacaoId) throws Exception;
	public void descancela(Long operacaoId) throws Exception;
}
