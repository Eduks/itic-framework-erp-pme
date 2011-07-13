package br.com.dyad.backoffice.navigation.cadastro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import br.com.dyad.backoffice.entidade.cadastro.ContaContabil;
import br.com.dyad.backoffice.entidade.cadastro.PlanoConta;
import br.com.dyad.backoffice.entidade.movimentacao.ItemLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoLancamento;
import br.com.dyad.backoffice.entidade.movimentacao.objetos.RegraLancamento;
import br.com.dyad.client.AppException;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.infrastructure.persistence.DataListFactory;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;
import br.com.dyad.infrastructure.widget.Action;
import br.com.dyad.infrastructure.widget.Interaction;
import br.com.dyad.infrastructure.widget.Window;
import br.com.dyad.infrastructure.widget.field.FieldLookup;
import br.com.dyad.infrastructure.widget.field.FieldMemo;
import br.com.dyad.infrastructure.widget.field.FieldUpload;
import br.com.dyad.infrastructure.widget.grid.VariableGrid;

public class ImportacaoLancamentoContabilWindow extends Window {
	
	private RegraLancamento regraLancamento;
	
	private AppTransaction appTransaction;
	
	private Variaveis gridVariaveis;
	
	private ArrayList<String> lancamentosComProblemas;
	
	public ImportacaoLancamentoContabilWindow(HttpSession httpSession) throws Exception {
		super(httpSession);
	}
	
	public Action actionImportar = new Action(this, "Importar"){
		@Override
		public void onClick() throws Exception {
			confirm("confirmImportarLancamentos", "Deseja importar os Lançamentos do arquivo selecionado?");
		}
	};
	
	public void confirmImportarLancamentos(Object response) throws Exception{
		if (((String)response).equals("yes")){
			
			FieldUpload arquivo = (FieldUpload)gridVariaveis.getFieldByName("arquivo");
			
			File file = new File(arquivo.getFullPath());
			
			PlanoConta planoConta = (PlanoConta)gridVariaveis.getFieldByName("planoConta").getValue();
			
			lancamentosComProblemas = importarLancamentos(planoConta, file);
			
			if (lancamentosComProblemas.isEmpty()) {
				lancamentosComProblemas.clear();
				setNextInteraction(interactionInicio);
			} else {
				setNextInteraction(interactionLancamentosComProblema);
			}
			
		}
	}
	
	public Action actionVoltar = new Action(this, "Voltar"){
		public void onClick() throws Exception{
			confirm("confirmVoltar", "Deseja voltar para a tela inicial?");
		}
	};
	
	public void confirmVoltar(Object response) throws Exception{
		if (((String)response).equals("yes") == true) {
			regraLancamento.fecha();

			setNextInteraction(interactionInicio);
		}
	}
	
	public Action actionInformacaoImportacao = new Action(this, "Informação"){
		@Override
		public void onClick() throws Exception {
			alert("O arquivo de importação deve conter linhas (uma para cada item de lançamento), agrupadas pela " +
				"chave de criação da operação, com os seguintes campos separados por ';' (ponto e vírgula): " +
				"chave de criação ou agrupador;data de emissão;código da conta contábil;valor;observação (caso tenha)");
		}
	};
	
	public Interaction interactionInicio = new Interaction(this, "interactionInicio") {
		
		@Override
		public void defineInteraction() throws Exception {
			
			enableAndShowActions(actionImportar);
			
			actionInformacaoImportacao.setValidateLastInteraction(false);
			
			enableAndShowActions(actionInformacaoImportacao);
			
			add(gridVariaveis);
		}
	};
	
	public Interaction interactionLancamentosComProblema = new Interaction(this, "interactionLancamentosComProblema"){
		@Override
		public void defineInteraction() throws Exception {
			
			remove(getGridByName("gridLancamentosComProblema"));
			
			getWindow().alert("Alguns lançamentos não foram inseridos com sucesso! Verifique se o SALDO "+
					"de cada grupo de lançamentos é zero e se as CONTAS CONTÁBEIS estão todas cadastradas no " +
					"plano de contas selecionado.");
			
			enableAndShowActions(actionVoltar);
			
			GridLancamentosComProblema gridLancamentosComProblema = new GridLancamentosComProblema(getWindow(), "gridLancamentosComProblema");
			
			try{
				gridLancamentosComProblema.setLancamentosComProblema(lancamentosComProblemas);
				lancamentosComProblemas.clear();
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new AppException(e.getMessage());
			}
			
			add(gridLancamentosComProblema);
			
			setDefined(false);
		}
	};

	@Override
	public void defineWindow() throws Exception {
		appTransaction = DataListFactory.getNewAppSession(PersistenceUtil.getSession(getDatabase()), getDatabase()); 
		
		regraLancamento = new RegraLancamento(appTransaction);
		
		gridVariaveis = new Variaveis(this, "gridVariaveis");
	}
	
	//CONDIÇÕES:
	//1 - O arquivo deve estar na seguinte ordem: 
	//		.chave de criação
	//		.data
	//		.conta contábil
	//		.valor
	//		.observacao (não obrigatório)
	//
	//2 - Os valores devem estar separados por ";" (ponto e vírgula)
	//
	//3 - Devem estar agrupados por CHAVE DE CRIAÇÃO, ou seja, lançamentos com chaves de criação iguais
	//	  devem estar juntos.
	public ArrayList<String> importarLancamentos(PlanoConta planoConta, File file) throws Exception{
		
		ArrayList<String> lancamentosComProblemas = new ArrayList<String>(0);
		
		try {
			List<String> lancamentosAux = new ArrayList<String>(0);
			
			FileReader fileReader = new FileReader(file);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String chaveCriacao = "";
			String chaveCriacaoAux = "";
			String data = "";
			String codigoConta = "";
			String observacao = "";
			String valor = "";
			String linha = "";
			
			boolean contaContabilNaoExiste = false;
			
			CabecalhoLancamento cabecalho = null, cabecalhoAux = null;
			ItemLancamento item = null, itemAux = null;
			
			String[] campos = new String[4];
			
			HashMap<String,ContaContabil> contasArmazenadas = new HashMap<String,ContaContabil>(0);
			
			List<?> listaContaEncontrada;
			
			ContaContabil contaContabil;
			
			Long inicio = System.currentTimeMillis();
			System.out.println("********************INICIO --- importarLancamentos");
			
			
			do {
				linha = bufferedReader.readLine();
				
				if (linha != null && !linha.equals("")) {
					
					campos = linha.split(";");

					try{
						chaveCriacao = campos[0];
						data = campos[1];
						codigoConta = campos[2];
						valor = campos[3];
					} catch (ArrayIndexOutOfBoundsException e) {
						throw new ArrayIndexOutOfBoundsException("Arquivo com problema! " +
							"Verifique se os quatro campos necessários estão informados em cada linha: " +
							"chave agrupadora, data do lançamento, codigo da conta contábil e valor.");
					}

					observacao = "";

					if (campos.length == 5) {
						observacao = campos[4];
					}

					if (!chaveCriacao.equalsIgnoreCase(chaveCriacaoAux)) {

						if (cabecalhoAux != null) {
							try {
								regraLancamento.calculaOperacao(cabecalhoAux);
								
								if (contaContabilNaoExiste) {
									lancamentosComProblemas.add("A propriedade \"Conta Contábil\" está inválida.");
									lancamentosComProblemas.addAll(lancamentosAux);
									lancamentosComProblemas.add(" ");
								} else {
									
									cabecalho = (CabecalhoLancamento)regraLancamento.novoCabecalho();
									cabecalho.setEmissao (cabecalhoAux.getEmissao());
									
									for (ItemLancamento i : cabecalhoAux.getItensLancamento()) {
										
										item = (ItemLancamento)regraLancamento.novoItem(cabecalho);
										item.setValor(i.getValor());
										item.setContaContabil(i.getContaContabil());
										item.setObservacao(i.getObservacao());
										item.setEmissao(i.getEmissao());
										
									}
									
									regraLancamento.getDataListCabecalhos().save(cabecalho);
								}
								
							} catch (AppException e) {
								lancamentosComProblemas.add(e.getMessage());
								lancamentosComProblemas.addAll(lancamentosAux);
								lancamentosComProblemas.add(" ");
							}
						}
						
						cabecalhoAux = new CabecalhoLancamento();
						cabecalhoAux.setId(0L);
						cabecalhoAux.setEmissao(new Date(Date.parse(data)));
						
						chaveCriacaoAux = chaveCriacao;

						contaContabilNaoExiste = false;
						lancamentosAux.clear();
					}
					
//					contaContabil = new ContaContabil();
//					contaContabil.setPlanoConta(planoConta);
//					contaContabil.setCodigo(codigoConta);
					
					contaContabil = contasArmazenadas.get(codigoConta);
					
					if (contaContabil == null) {
						listaContaEncontrada = appTransaction.executeQuery("FROM ContaContabil WHERE codigo = '"+codigoConta+"' AND planoConta.id = "+planoConta.getId());
						
						if (listaContaEncontrada.isEmpty()) {
							contaContabil = null;
							contaContabilNaoExiste = true;
						} else {
							contaContabil = (ContaContabil)listaContaEncontrada.get(0);
							contasArmazenadas.put(codigoConta, contaContabil);
						}
					}
					
					if (contaContabil != null) {
						itemAux = new ItemLancamento();
						itemAux.setId(0L);
						itemAux.setValor(new BigDecimal(valor));
						itemAux.setContaContabil(contaContabil);
						itemAux.setObservacao(observacao);
						
						cabecalhoAux.addItemLancamento(itemAux);
					}
					
					lancamentosAux.add(linha);
					
				} else if (linha == null && cabecalhoAux != null) {
					
					// Esse else é pra capturar o último lançamento do arquivo
					
					try {
						regraLancamento.calculaOperacao(cabecalhoAux);
						
						if (contaContabilNaoExiste) {
							lancamentosComProblemas.add("A propriedade \"Conta Contábil\" está inválida.");
							lancamentosComProblemas.addAll(lancamentosAux);
							lancamentosComProblemas.add(" ");
						} else {
							
							cabecalho = (CabecalhoLancamento)regraLancamento.novoCabecalho();
							cabecalho.setEmissao (cabecalhoAux.getEmissao());
							
							for (ItemLancamento i : cabecalhoAux.getItensLancamento()) {
								
								item = (ItemLancamento)regraLancamento.novoItem(cabecalho);
								item.setValor(i.getValor());
								item.setContaContabil(i.getContaContabil());
								item.setObservacao(i.getObservacao());
								item.setEmissao(i.getEmissao());
								
							}
							
							regraLancamento.getDataListCabecalhos().save(cabecalho);
						}
						
					} catch (AppException e) {
						lancamentosComProblemas.add(e.getMessage());
						lancamentosComProblemas.addAll(lancamentosAux);
						lancamentosComProblemas.add(" ");
					}
				}
				
			} while (linha != null);
			
			System.out.println("********************FIM ANTES DO COMMIT --- RegraPedido.calculaPedidos() Tempo execução->" + ((System.currentTimeMillis() - inicio) / 1000) + "s");
			
//			grava();
			
			DataList.commit(regraLancamento.getDataListCabecalhos());
			
			System.out.println("********************FIM --- RegraPedido.calculaPedidos() Tempo execução->" + ((System.currentTimeMillis() - inicio) / 1000) + "s");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lancamentosComProblemas;
	}
	
};

class Variaveis extends VariableGrid {
	
	private FieldLookup planoConta;
	
	private FieldUpload arquivo;
	
	public Variaveis(Window window, String name) throws Exception {
		super(window, name);
		
		setTitle("Selecione o Plano de Contas e o Arquivo");
		
		planoConta = new FieldLookup(this);
		
		arquivo = new FieldUpload(this);
	}

	@Override
	public void defineGrid() throws Exception {
		
		planoConta.setName("planoConta");
		planoConta.setLabel("Plano de Contas");
		planoConta.setBeanName(PlanoConta.class.getName());
		planoConta.setWidth(250);
		planoConta.setRequired(true);
		
		arquivo.setName("arquivo");
		arquivo.setLabel("Arquivo");
		arquivo.setWidth(250);
		arquivo.setRequired(true);
		arquivo.setTempFile(true);
		
	}
	
};

class GridLancamentosComProblema extends VariableGrid {
	
	FieldMemo listaLancamentos;
	
	public GridLancamentosComProblema (Window window, String name) throws Exception {
		super(window, name);

		setTitle("Lançamentos com Problema");
		
		listaLancamentos = new FieldMemo(this);
	}

	@Override
	public void defineGrid() throws Exception {
		
		listaLancamentos.setLabel("Lançamentos");
		listaLancamentos.setName("listaLancamentos");
		listaLancamentos.setWidth(450);
		listaLancamentos.setHeight(15);
	}
	
	public void setLancamentosComProblema(List<String> lancamentos) throws Exception{
		
		StringBuilder conteudo = new StringBuilder();
		
		for (Iterator<String> iterator = lancamentos.iterator(); iterator.hasNext(); ) {
			
			String linha = (String)iterator.next();
			
			conteudo.append(linha+"\n");
		}
		
		listaLancamentos.setValue(conteudo.toString());
	}
}
