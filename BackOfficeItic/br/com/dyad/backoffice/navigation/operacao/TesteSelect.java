package br.com.dyad.backoffice.navigation.operacao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.infrastructure.persistence.PersistenceUtil;

public class TesteSelect {
	
	public static void main(String[] args) {
		Session session = PersistenceUtil.getSession("INFRA");
		/*
		Query query = session.createQuery(" from br.com.dyad.backoffice.entidade.movimentacoes.ItemPedido Pedido " + 
                            " where " +  
                            " emissao >= '01/27/2010' and  emissao <= '01/27/2010' " + 
                            " and (Not Exists ( " +      
                            " From br.com.dyad.backoffice.entidade.movimentacoes.ItemBaixaPedido Baixa " +       
                            " Where Baixa.pedidoBaixado = Pedido) " +     
                            " or ( " +       
                            "   Select " +          
                            "     sum(Baixa2.quantidade) " +      
                            "   From br.com.dyad.backoffice.entidade.movimentacoes.ItemBaixaPedido Baixa2 " +       
                            "   Where Baixa2.pedidoBaixado = Pedido ) < Pedido.quantidade) )"
                        );
		List list = query.list();
		System.out.println("query.list()=" + query.list());

                        */
                        
		String sql1 = "SELECT {pedido.*}, {baixaPedido.*}  FROM OPERACAO pedido, OPERACAO baixaPedido WHERE baixaPedido.PEDIDOBAIXADOID = pedido.ID";
		
		String sql = "" +
			"SELECT " +
			"   pedidoPendente.ID, pedidoPendente.quantidadeBaixada, pedido.quantidade - baixaPedido.quantidadeBaixada as saldo " +
			"FROM " +
			"   operacao pedido " +
			"INNER JOIN ( " +
			"   SELECT" + 
	        "      baixa.idBaixado, SUM(baixa.quantidade) as quantidadeBaixada" +
			"   FROM " +
	        "      Operacao baixa" + 
			"   GROUP BY " +
	        "      baixa.idBaixado " +
			") baixaPedido on (pedido.ID = baixaPedido.idBaixado)";
		
		
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.addEntity("pedido", ItemPedido.class);
		//sqlQuery.addJoin(arg0, arg1)
		
		//sqlQuery.addEntity("baixaPedido", ItemBaixaPedido.class);
		
		
		List lista = sqlQuery.list();
		System.out.println(lista);
	}
}
