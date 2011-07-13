package br.com.dyad.backoffice.entidade.movimentacao.objetos_interface;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.com.dyad.backoffice.annotations.Cabecalho;
import br.com.dyad.backoffice.annotations.TipoCampoCabecalho;
import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemOperacao;
import br.com.dyad.backoffice.entidade.movimentacao.ItemTransferenciaDisponivel;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoOperacaoAbstrato;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoTransferenciaDisponivel;
import br.com.dyad.backoffice.operacao.consulta.ConsultaTransferenciaDisponivel;
import br.com.dyad.commons.data.AppEntity;
import br.com.dyad.commons.data.AppTransaction;
import br.com.dyad.commons.data.DataList;
import br.com.dyad.commons.reflect.ReflectUtil;

public class RegraTransferenciaDisponivel extends RegraAbstrata {

	private static HashMap<String,ArrayList<String>> cacheDeCamposPorClasse = new HashMap<String,ArrayList<String>>(); 

	private ConsultaTransferenciaDisponivel consultaTransferenciaDisponivel;

	public RegraTransferenciaDisponivel(AppTransaction appTransaction) {
		super(appTransaction);
		//TODO Mudar o ID para uma chave
		this.regraId = "TRANSFERENCIA_DISPONIVEL";

		this.consultaTransferenciaDisponivel = new ConsultaTransferenciaDisponivel(appTransaction);

		this.preparado = false;
	}

	/**
	 * Getter´s e Setter´s
	 */
	public ConsultaTransferenciaDisponivel getConsultaTransferenciaDisponivel() {
		return consultaTransferenciaDisponivel;
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
	public CabecalhoTransferenciaDisponivel novoCabecalho() throws Exception {
		CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel = new CabecalhoTransferenciaDisponivel(this.getAppTransaction().getDatabase());
		cabecalhoTransferenciaDisponivel.setClasseOperacaoId(this.getRegraId());
		
		this.dataListCabecalhos.add(cabecalhoTransferenciaDisponivel);

		return cabecalhoTransferenciaDisponivel;
	}

	public void removeCabecalho( CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel ) throws Exception {	
		Collection<ItemTransferenciaDisponivel> itensTransferenciaDisponivel = this.getItensCabecalho(cabecalhoTransferenciaDisponivel);
		for (ItemTransferenciaDisponivel itemTransferenciaDisponivel : itensTransferenciaDisponivel ) {
			this.removeItem(itemTransferenciaDisponivel);
		}

		this.dataListCabecalhos.delete(cabecalhoTransferenciaDisponivel);
	}

	@Override
	public ItemOperacao novoItem(CabecalhoOperacaoAbstrato cabecalhoOperacao) throws Exception {
		if ( cabecalhoOperacao == null ){
			throw new Exception("Operacao não pode ser nula!");
		}

		if ( !this.dataListCabecalhos.find("operacaoId", cabecalhoOperacao.getOperacaoId() ) ){
			throw new Exception("Esta operação não foi encontrada neste objeto!");
		}

		ItemOperacao itemOperacao = new ItemTransferenciaDisponivel();
		itemOperacao.createId(this.getAppTransaction().getDatabase());
		itemOperacao.setOperacaoId(cabecalhoOperacao.getOperacaoId());
		itemOperacao.setClasseOperacaoId( this.regraId );

		this.dataListItens.add(itemOperacao);

		return itemOperacao;
	}

	public void removeItem( ItemTransferenciaDisponivel itemTransferenciaDisponivel ) throws Exception {
		this.dataListItens.delete(itemTransferenciaDisponivel);
	}

	@Override
	public void abre() throws Exception {
		if (!this.preparado) {
			this.preparaRegra();
		}

		//TODO transformar em um método
		ArrayList<ItemTransferenciaDisponivel> itensTransferenciaDisponivel = this.consultaTransferenciaDisponivel.pegaTransferenciasDisponiveisAsList();
		
		if (itensTransferenciaDisponivel != null && !itensTransferenciaDisponivel.isEmpty()) {

			this.dataListItens.setLogChanges(false);
			for (ItemTransferenciaDisponivel itemTransferenciaDisponivel : itensTransferenciaDisponivel) {
				this.dataListItens.add(itemTransferenciaDisponivel);
			}
			this.dataListItens.setLogChanges(true);

			this.preparaCabecalhos();
		}
	}

	private void preparaCabecalhos() throws Exception{
		Field[] camposCabecalho = CabecalhoTransferenciaDisponivel.class.getDeclaredFields();
		Set<String> operacoesId = this.dataListItens.getDistinctValues("operacaoId");

		for (String operacaoId : operacoesId) {
			Long operacaoIdLong = new Long(operacaoId);

			ArrayList<ItemTransferenciaDisponivel> itensTransferenciaDisponivel = new ArrayList<ItemTransferenciaDisponivel>(this.getItensCabecalho(operacaoIdLong));
			CabecalhoTransferenciaDisponivel cabecalhoOperacao = new CabecalhoTransferenciaDisponivel(this.getAppTransaction().getDatabase(), operacaoIdLong);

			ArrayList<Field> camposReplica = new ArrayList<Field>();

			for (Field campoCabecalho : camposCabecalho) {
				Cabecalho annotationCabecalho = campoCabecalho.getAnnotation(Cabecalho.class);

				if (annotationCabecalho != null) {
					if ( annotationCabecalho.value() == TipoCampoCabecalho.REPLICA) {
						camposReplica.add(campoCabecalho);
					}
				}
			}

			ItemTransferenciaDisponivel transferenciaDisponivelReplica = itensTransferenciaDisponivel.get(0);
			for (Field campo : camposReplica ) {
				Object objTemp = ReflectUtil.getFieldValue(transferenciaDisponivelReplica, campo.getName());

				if (objTemp != null) {
					ReflectUtil.setFieldValue(cabecalhoOperacao, campo.getName(), objTemp);
				}
			}

			this.dataListCabecalhos.add(cabecalhoOperacao);
		}
	}

	@Override
	public void exclui(Long operacaoId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void grava() throws Exception {
		this.preparaGravacao();

		DataList.commit(this.dataListItens);
	}
	
	@Override
	public void preparaGravacao() throws Exception {
		//this.verificaIntegridadeCabecalho();

		this.calculaOperacoes();
	
		//this.verificaIntegridadeItensOperacoes();
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public void calculaOperacoes() throws Exception {
		List<CabecalhoTransferenciaDisponivel> cabecalhosTransferenciaDisponivel = this.dataListCabecalhos.getList();

		for (CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel : cabecalhosTransferenciaDisponivel) {
			this.calculaOperacao(cabecalhoTransferenciaDisponivel);
		}
	}
		
	public void calculaOperacao(CabecalhoTransferenciaDisponivel cabecalhoTransferenciaDisponivel) throws Exception {
		this.sincronizaItensComCabecalho(cabecalhoTransferenciaDisponivel);

		this.verificaIntegridadeItensOperacao(cabecalhoTransferenciaDisponivel);
	}
	
	private void sincronizaItensComCabecalho(CabecalhoTransferenciaDisponivel cabecalhoOperacao) throws Exception{

		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- OperacaoTransferenciaDisponivel.sincronizaItensComCabecalho()");

		ArrayList<ItemTransferenciaDisponivel> itensTransferenciaDisponivel = this.getItensCabecalho( cabecalhoOperacao );
		ArrayList<String> camposCabecalho = cacheDeCamposPorClasse.get( cabecalhoOperacao.getClass().getName() ); 

		if ( camposCabecalho == null ) {
			camposCabecalho = new ArrayList<String>();

			Field[] fields = cabecalhoOperacao.getClass().getDeclaredFields();
			for (Field field : fields) {
				Cabecalho annotationCabecalho = field.getAnnotation(Cabecalho.class);

				if (annotationCabecalho != null && annotationCabecalho.value() == TipoCampoCabecalho.REPLICA) {
					camposCabecalho.add(field.getName());
				}
			}
			cacheDeCamposPorClasse.put(cabecalhoOperacao.getClass().getName(), camposCabecalho);
		} 

		for (ItemTransferenciaDisponivel itemTransferenciaDisponivel : itensTransferenciaDisponivel) {
			for (String nomeDoCampo : camposCabecalho) {

				Long inicioAux1 = System.currentTimeMillis();
				System.out.println("   INICIO --- ReflectUtil.setFieldValue(...)");

				ReflectUtil.setFieldValue(itemTransferenciaDisponivel, nomeDoCampo, ReflectUtil.getFieldValue(cabecalhoOperacao, nomeDoCampo));

				System.out.println("   FIM --- ReflectUtil.setFieldValue(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux1) /1000) + "s");
			}

			Long inicioAux2 = System.currentTimeMillis();
			System.out.println("   INICIO --- dataListItens.save(...)");
			
			this.dataListItens.save(itemTransferenciaDisponivel);
			
			System.out.println("   FIM --- dataListItens.save(...) Tempo execução->" + ((System.currentTimeMillis() - inicioAux2) /1000) + "s");
		}

		System.out.println("FIM --- OperacaoTransferenciaDisponivel.sincronizaItensComCabecalho() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}
	
	private void verificaIntegridadeItensOperacao(CabecalhoOperacaoAbstrato cabecalhoTransferenciaDisponivel) throws Exception {
		Long inicio = System.currentTimeMillis();
		System.out.println("INICIO --- RegraTransferenciaDisponivel.verificaIntegridadeItens()");

		ArrayList<ItemTransferenciaDisponivel> listTransferenciasDisponiveis = (ArrayList<ItemTransferenciaDisponivel>)this.dataListItens.getList();
		ArrayList<String> erros = new ArrayList(); 

		BigDecimal somaTotal = new BigDecimal(0);
		for (ItemTransferenciaDisponivel itemTransferenciaDisponivel : listTransferenciasDisponiveis) {
			somaTotal = somaTotal.add( itemTransferenciaDisponivel.getTotal() );
		}
		
		if (somaTotal.compareTo(new BigDecimal(0)) != 0) {
			erros.add("A soma do valor dos itens da operação, deve ser igual a 0");
		}

		if (!erros.isEmpty()) {
			throw new Exception(StringUtils.join(erros, "\r\n"));			
		}

		System.out.println("FIM --- RegraTransferenciaDisponivel.verificaIntegridadeItens() Tempo execução->" + ((System.currentTimeMillis() - inicio) /1000) + "s");
	}

	@Override
	public void fecha() throws Exception {
		this.dataListCabecalhos.setLogChanges(false);
		this.dataListItens.setLogChanges(false);
		
		this.dataListCabecalhos.empty();
		this.dataListItens.empty();

		this.dataListCabecalhos.setLogChanges(true);
		this.dataListItens.setLogChanges(true);

		this.preparado = false;
	}

	public ArrayList<ItemTransferenciaDisponivel> getItensCabecalho( CabecalhoOperacaoAbstrato cabecalhoPedido ) throws Exception{
		return getItensCabecalho( cabecalhoPedido.getOperacaoId() );
	}

	@SuppressWarnings("unchecked")
	public ArrayList<ItemTransferenciaDisponivel> getItensCabecalho( Long operacaoId ) throws Exception{
		Collection<AppEntity> itensTransferenciaDisponivel = this.dataListItens.getRangeAsCollection("operacaoId", operacaoId );
		
		if ( itensTransferenciaDisponivel != null ){
			return new ArrayList(itensTransferenciaDisponivel);
		} else {
			return new ArrayList<ItemTransferenciaDisponivel>();
		}
	}
}
