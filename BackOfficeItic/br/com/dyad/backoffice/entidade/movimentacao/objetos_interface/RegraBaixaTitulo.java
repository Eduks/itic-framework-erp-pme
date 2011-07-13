package br.com.dyad.backoffice.entidade.movimentacao.objetos_interface;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.annotations.Cabecalho;
import br.com.dyad.backoffice.annotations.TipoCampoCabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.ItemOperacao;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaTitulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.operacao.consulta.ConsultaBaixaTitulo;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.persistence.DataListFactory;

public class RegraBaixaTitulo extends RegraAbstrata {
	
	private static HashMap<String,ArrayList<String>> cacheDeCamposPorClasse = new HashMap<String,ArrayList<String>>(); 

	private DataList dataListTitulosBaixando;
	
	private ConsultaBaixaTitulo consultaBaixaTitulo;
	
	public RegraBaixaTitulo(AppTransaction appTransaction) {
		super(appTransaction);

		this.regraId = "BAIXA_TITULO";

		this.consultaBaixaTitulo = new ConsultaBaixaTitulo(appTransaction);
		
		this.dataListTitulosBaixando = DataListFactory.newDataList(this.getAppTransaction());
	}

	public ConsultaBaixaTitulo getConsultaBaixaTitulo() {
		return this.consultaBaixaTitulo;
	}
	
	public void abre() throws Exception {
		if (!this.preparado) {
			this.preparaRegra();
		}

		//TODO transformar em um método
		ArrayList<ItemBaixaTitulo> baixasTitulo = this.consultaBaixaTitulo.pegaBaixasTituloAsList();
		
		if (baixasTitulo != null && !baixasTitulo.isEmpty()) {

			ArrayList<Long> operacoesId = new ArrayList<Long>();
			
			this.dataListItens.setLogChanges(false);
			for (ItemBaixaTitulo itemBaixaTitulo : baixasTitulo) {
				this.dataListItens.add(itemBaixaTitulo);

				if (!operacoesId.contains(itemBaixaTitulo.getOperacaoId())) {
					operacoesId.add(itemBaixaTitulo.getOperacaoId());
				}
			}
			this.dataListItens.setLogChanges(true);

			this.preparaCabecalhos();
		}
	}

	private void preparaCabecalhos() throws Exception {
		Field[] camposCabecalho = CabecalhoBaixaTitulo.class.getDeclaredFields();
		Set<String> operacoesId = this.dataListItens.getDistinctValues("operacaoId");

		for (String operacaoId : operacoesId) {
			Long operacaoIdLong = new Long(operacaoId);

			ArrayList<ItemBaixaTitulo> itensBaixaTitulo = new ArrayList<ItemBaixaTitulo>(this.getBaixasCabecalho(operacaoIdLong));
			CabecalhoBaixaTitulo cabecalhoOperacao = new CabecalhoBaixaTitulo(this.getAppTransaction().getDatabase(), operacaoIdLong);

			ArrayList<Field> camposReplica = new ArrayList<Field>();
			
			for (Field campoCabecalho : camposCabecalho) {
				Cabecalho annotationCabecalho = campoCabecalho.getAnnotation(Cabecalho.class);

				if (annotationCabecalho != null) {
					if ( annotationCabecalho.value() == TipoCampoCabecalho.REPLICA) {
						camposReplica.add(campoCabecalho);
					}
				}
			}
			
			ItemBaixaTitulo baixaTituloReplica = itensBaixaTitulo.get(0);
			for (Field campo : camposReplica ) {
				Object objTemp = ReflectUtil.getFieldValue(baixaTituloReplica, campo.getName());

				if (objTemp != null) {
					ReflectUtil.setFieldValue(cabecalhoOperacao, campo.getName(), objTemp);
				}
			}
			
			//*********************
			//Abrir Eventos
			
			
			//*********************

			this.dataListCabecalhos.add(cabecalhoOperacao);
		}
	}

	@Override
	public void fecha() throws Exception {
		this.dataListCabecalhos.setLogChanges(false);
		this.dataListItens.setLogChanges(false);
		this.dataListTitulosBaixando.setLogChanges(false);
		
		this.dataListCabecalhos.empty();
		this.dataListItens.empty();
		this.dataListTitulosBaixando.empty();

		this.dataListCabecalhos.setLogChanges(true);
		this.dataListItens.setLogChanges(true);
		this.dataListTitulosBaixando.setLogChanges(true);

		this.preparado = false;
	}

	@Override
	public void exclui(Long operacaoId) throws Exception {
		throw new Exception("Operação não permitida.");
	}

	@Override
	public void grava() throws Exception {
		this.preparaGravacao();
		
		DataList.commit(this.dataListItens);
	}

	@Override
	public void preparaGravacao() throws Exception {
		this.calculaOperacoes();
	}

	public void calculaOperacoes() throws Exception {
		List<CabecalhoBaixaTitulo> cabecalhosBaixaTitulo = this.dataListCabecalhos.getList();

		for (CabecalhoBaixaTitulo cabecalhoBaixaTitulo : cabecalhosBaixaTitulo) {
			this.calculaOperacao(cabecalhoBaixaTitulo);
		}
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public void calculaOperacao(CabecalhoBaixaTitulo cabecalhoBaixaTitulo) throws Exception {
		this.verificaIntegridadeCabecalho(cabecalhoBaixaTitulo);
		this.sincronizaItensComCabecalho(cabecalhoBaixaTitulo);

		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraTitulo.calculaOperacao()");
		
		System.out.println("FIM --- RegraTitulo.calculaOperacao() --->" + (System.currentTimeMillis() - inicio) / 1000);
	}
	
	/**
	 * Método responsável por verificar se todas as propriedades de preenchimento obrigatório
	 * dos cabecalhos das operações estlo devidamente preenchidas.
	 *  
	 * @throws Exception
	 */
	private void verificaIntegridadeCabecalhos() throws Exception {
		ArrayList<CabecalhoBaixaTitulo> listCabecalhos = (ArrayList<CabecalhoBaixaTitulo>)this.dataListCabecalhos.getList();
		
		for (CabecalhoBaixaTitulo cabecalhoOperacao : listCabecalhos) {
			this.verificaIntegridadeCabecalho(cabecalhoOperacao);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void verificaIntegridadeCabecalho(CabecalhoBaixaTitulo cabecalhoBaixaTitulo) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraBaixaTitulo.verificaIntegridadeOperacoes()");

		ArrayList<String> erros = new ArrayList(); 
		
		if (cabecalhoBaixaTitulo.getOperacaoId() == null) {
			erros.add("A propriedade operacao não está preenchida.");
		}

		if (cabecalhoBaixaTitulo.getEmissao() == null) {
			erros.add("A propriedade \"emissão\" da operação: " + cabecalhoBaixaTitulo.getOperacaoId() + " nï¿½o estï¿½ preenchida.");
		}

		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- RegraBaixaTitulo.verificaIntegridadeOperacoes()" + (System.currentTimeMillis() - inicio) / 1000);
	}

	private void sincronizaItensComCabecalho(CabecalhoBaixaTitulo cabecalhoBaixaTitulo) throws Exception{
		ArrayList<ItemBaixaTitulo> baixasTitulo = this.getBaixasTituloCabecalho( cabecalhoBaixaTitulo );

		Long inicio = System.currentTimeMillis();
		System.out.println("   INICIO --- RegraBaixaTitulo.sincronizaItensComCabecalho()");
		
		ArrayList<String> camposCabecalho = cacheDeCamposPorClasse.get( cabecalhoBaixaTitulo.getClass().getName() ); 
		if ( camposCabecalho == null ) {
			camposCabecalho = new ArrayList<String>();
			
			Field[] fields = cabecalhoBaixaTitulo.getClass().getDeclaredFields();
			for (Field field : fields) {
				Cabecalho annotationCabecalho = field.getAnnotation(Cabecalho.class);
				
				if (annotationCabecalho != null && annotationCabecalho.value() == TipoCampoCabecalho.REPLICA) {
					camposCabecalho.add(field.getName());
				}
			}
			cacheDeCamposPorClasse.put(cabecalhoBaixaTitulo.getClass().getName(), camposCabecalho);
		} 
		
		for (ItemBaixaTitulo itemBaixaTitulo : baixasTitulo) {
			for (String nomeDoCampo : camposCabecalho) {
				
				Long inicioAux1 = System.currentTimeMillis();
				System.out.println("   INICIO --- ReflectUtil.setFieldValue(...)");

				ReflectUtil.setFieldValue(itemBaixaTitulo, nomeDoCampo, ReflectUtil.getFieldValue(cabecalhoBaixaTitulo, nomeDoCampo));
				
				System.out.println("   FIM --- ReflectUtil.setFieldValue(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux1) /1000) + "s");
			}
			
			Long inicioAux2 = System.currentTimeMillis();
			System.out.println("   INICIO --- dataListItens.save(...)");
			
			this.dataListItens.save(itemBaixaTitulo);

			System.out.println("   FIM --- dataListItens.save(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux2) /1000) + "s");
		}

		System.out.println("   FIM --- RegraBaixaTitulo.sincronizaItensComCabecalho() --- Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	/**
	 * Métodos
	 */
	@Override
	public void nova() {
		if (!this.preparado) {
			this.preparaRegra();
		}
	}

	@Override
	public void preparaRegra() {
		this.preparado = true;
	}

	/**
	 * @throws Exception
	 */
	@Override
	public CabecalhoBaixaTitulo novoCabecalho() throws Exception {
		CabecalhoBaixaTitulo cabecalhoBaixaTitulo = new CabecalhoBaixaTitulo(this.getAppTransaction().getDatabase());
		cabecalhoBaixaTitulo.setClasseOperacaoId(this.getRegraId());
		
		this.dataListCabecalhos.add(cabecalhoBaixaTitulo);

		return cabecalhoBaixaTitulo;
	}

	@Override
	public ItemOperacao novoItem(CabecalhoOperacaoAbstrato cabecalhoOperacao) throws Exception {
		if ( cabecalhoOperacao == null ){
			throw new Exception("Operacao não pode ser nula!");
		}

		if ( !this.dataListCabecalhos.find("operacaoId", cabecalhoOperacao.getOperacaoId() ) ){
			throw new Exception("Esta operação não foi encontrada neste objeto!");
		}

		ItemOperacao itemOperacao = new ItemBaixaTitulo();
		itemOperacao.createId(this.getAppTransaction().getDatabase());
		itemOperacao.setOperacaoId(cabecalhoOperacao.getOperacaoId());
		itemOperacao.setClasseOperacaoId( this.regraId );

		this.dataListItens.add(itemOperacao);

		return itemOperacao;
	}

	public ArrayList<ItemBaixaTitulo> getBaixasTituloCabecalho( CabecalhoOperacaoAbstrato cabecalhoOperacao ) throws Exception{
		return this.getBaixasCabecalho(cabecalhoOperacao.getOperacaoId());
	}
	
	public ArrayList<ItemBaixaTitulo> getBaixasCabecalho( Long operacaoId ) throws Exception{
		Collection<AppEntity> baixas = this.dataListItens.getRangeAsCollection("operacaoId", operacaoId );
		
		if ( baixas != null ){
			return new ArrayList(baixas);
		} else {
			return new ArrayList<ItemBaixaTitulo>();
		}
	}

}