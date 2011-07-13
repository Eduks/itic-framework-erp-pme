package br.com.dyad.backoffice.entidade.movimentacao.objetos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class RegraPlanoConta {
	
	private AppTransaction appTransaction;
	
	private ContaContabil contaAux;
	
	private HashMap<ContaContabil,List<ItemLancamento>> contasContabeisVerificadas;
	
	public RegraPlanoConta (AppTransaction appTransaction) {
		this.appTransaction = appTransaction;
		
		contasContabeisVerificadas = new HashMap<ContaContabil,List<ItemLancamento>>(0);
	}
	
	public AppTransaction getAppTransaction() {
		return appTransaction;
	}

	public void setAppTransaction(AppTransaction appTransaction) {
		this.appTransaction = appTransaction;
	}

	public boolean validaAnalitico(ContaContabil contaContabil){
		
		List<Object> params = new ArrayList<Object>(0);
		
		contaContabil.setSintetico(false);
		
		ArrayList<ContaContabil> contasContabeis = new ArrayList<ContaContabil>(0);
		contasContabeis.addAll(contaContabil.getPlanoConta().getContasContabeis());
		
		if (!contasContabeis.contains(contaContabil)) {
			contasContabeis.add(contaContabil);
		}
		
		DataList.sort(contasContabeis, "codigo");
		
		int index = contasContabeis.indexOf(contaContabil);
		
		if (index - 1 >= 0) {
			
			if (contaContabil.getCodigo().indexOf(contasContabeis.get(index - 1).getCodigo()) == 0) {
				
				contaAux = contasContabeis.get(index - 1);
				
				List lista = contasContabeisVerificadas.get(contaAux);
				
				if (lista == null) {
					params.add(contaAux.getId());
					
					lista = PersistenceUtil.executeHql((Session)appTransaction.getSession(), "FROM ItemLancamento WHERE contaContabil.id = ?", params);
					
					contasContabeisVerificadas.put(contaAux, lista);
				}

				if (lista.size() == 0) {
					contasContabeis.get(index - 1).setSintetico(true);
					return true;
				} else {
					throw new AppException("Esta conta não pode ser criada como filha da conta " +
						""+contaAux.getCodigo()+" - "+contaAux.getNome()+". Esta conta já possui lançamentos cadastrados e " +
						"por isso não pode ser uma conta sintética, ou seja, não pode possuir filho(s).");
				}
			}
		}
		
		if (index + 1 < contasContabeis.size()) {
			if (contasContabeis.get(index + 1).getCodigo().indexOf(contaContabil.getCodigo()) == 0) {
				contaContabil.setSintetico(true);
				return true;
			}
		}
		
		return true;
	}

	public boolean validaExclusao(ContaContabil contaContabil){
		
		List<Object> params = new ArrayList<Object>(0);
		
		ArrayList<ContaContabil> contasContabeis = new ArrayList<ContaContabil>(0);
		contasContabeis.addAll(contaContabil.getPlanoConta().getContasContabeis());

		String codigo = contaContabil.getCodigo();
		
		contaAux = null;
		
		List lista = contasContabeisVerificadas.get(contaContabil);
		
		if (lista == null) {
			params.add(contaContabil.getId());
			
			lista = PersistenceUtil.executeHql((Session)appTransaction.getSession(), "FROM ItemLancamento WHERE contaContabil.id = ?", params);
			
			contasContabeisVerificadas.put(contaContabil, lista);
		}
		
		if (lista.size() > 0) {
			throw new AppException("Esta conta não pode ser excluída. Motivo: já foram realizados lançamentos " +
			"com esta conta.");
		} else {

			DataList.sort(contasContabeis, "codigo");

			int index = contasContabeis.indexOf(contaContabil);
			
			if (contasContabeis.get(index + 1).getCodigo().indexOf(contaContabil.getCodigo()) == 0) {
				throw new AppException("Esta conta não pode ser excluída. Motivo: esta exclusão deixará contas " +	
					"orfãs, ou seja, sem um antecessor imediato");
			}
			
			for (int i = index - 1; i >= 0; i--) {
				
				if (contaContabil.getCodigo().indexOf(contasContabeis.get(i).getCodigo()) == 0) {
					contaAux = contasContabeis.get(i);
					
					for (int j = i+1; j < contasContabeis.size(); j++) {
						
						if (!contasContabeis.get(j).getCodigo().equalsIgnoreCase(codigo) 
								&& contasContabeis.get(j).getCodigo().indexOf(contaAux.getCodigo()) == 0) {
							return true;
						} else if (contasContabeis.get(j).getCodigo().indexOf(contaAux.getCodigo()) == -1) {
							contaAux.setSintetico(false);
							
							return true;
						}
						
					}
				}
			}
		}
		
		return true;
	}

	public boolean validaCodigoUnico(ContaContabil contaContabil){

		ArrayList<ContaContabil> contasContabeis = new ArrayList<ContaContabil>(0);
		contasContabeis.addAll(contaContabil.getPlanoConta().getContasContabeis());
		
		contasContabeis.add(contaContabil);
		
		DataList.sort(contasContabeis, "codigo", "id");
		
		if (contasContabeis.indexOf(contaContabil) != contasContabeis.lastIndexOf(contaContabil)) {
			int index = contasContabeis.indexOf(contaContabil);
			int lastIndex = contasContabeis.lastIndexOf(contaContabil);
			
			if (contasContabeis.get(index).getId().intValue() != contasContabeis.get(lastIndex).getId().intValue()) {
				throw new AppException("Não foi possível gravar a conta! " +
					"Motivo: conta com mesmo código já cadastrada.");
			}
		}
		
		return true;
	}
	
	public void importarPlanoContas(File file) {
		
		List<Object> params = new ArrayList<Object>(0);
		
		try {
			FileReader fileReader = new FileReader(file);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String codigo;
			String nome;
			String linha = "";

			PlanoConta planoConta = new PlanoConta();
			planoConta.createId(appTransaction.getDatabase());
			
			ContaContabil c = null;
			
			int mes = Calendar.getInstance().get(Calendar.MONTH)+1;
			
			int ano = Calendar.getInstance().get(Calendar.YEAR);
			
			params.add(mes);
			params.add(ano);
			
			List planosContas = PersistenceUtil.executeHql((Session)appTransaction.getSession(), "FROM PlanoConta WHERE codigo LIKE 'PC - ?/?%'", params);
			
			if (planosContas.isEmpty()) {
				planoConta.setCodigo("PC - "+mes+"/"+ano);
				planoConta.setNome("PLANO IMPORTADO EM "+mes+"/"+ano);
			} else {
				planoConta.setCodigo("PC - "+mes+"/"+ano+" ("+(planosContas.size()+1)+")");
				planoConta.setNome("PLANO IMPORTADO EM "+mes+"/"+ano+" ("+(planosContas.size()+1)+")");
			}
			
			appTransaction.beginTransaction();
			
			appTransaction.save(planoConta);
			
			linha = bufferedReader.readLine();
			
			while (linha != null) {
				
				if (!linha.equals("")) {

					codigo = linha.split(";")[0].replaceAll("\"", "").trim();
					nome = linha.split(";")[1].replaceAll("\"", "").trim();

					c = new ContaContabil();
					c.createId(appTransaction.getDatabase());
					c.setPlanoConta(planoConta);
					c.setCodigo(codigo);
					c.setNome(nome);
					c.setSintetico(false);

					if (!planoConta.getContasContabeis().contains(c)) {
						validaAnalitico(c);
						planoConta.getContasContabeis().add(c);
					}
				
				}
				
				linha = bufferedReader.readLine();
			}

			appTransaction.commit();
			
//			for (int i = 0; i < planoConta.getContasContabeis().size(); i++) {
//				System.out.println(planoConta.getContasContabeis().get(i).getCodigo() + " - " + planoConta.getContasContabeis().get(i).getNome() + " - Sintético: " + planoConta.getContasContabeis().get(i).getSintetico());
//			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}