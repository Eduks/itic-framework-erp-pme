package br.com.dyad.backoffice.entidade.movimentacao.interfaces;

public interface InterfaceBaseEntity {
	public Long getId();
	public void setId(Long id);
	public String getClassId();
	public void setClassId(String classId);
	public Long getLastTransaction();
	public void setLastTransaction(Long lastTransaction);
	public Long getLicenseId();
	public void setLicenseId(Long licenseId);
}
