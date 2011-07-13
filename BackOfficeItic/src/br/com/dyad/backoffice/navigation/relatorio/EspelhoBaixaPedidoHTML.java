package br.com.dyad.backoffice.navigation.relatorio;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.dyad.backoffice.entidade.movimentacao.ItemBaixaPedido;
import br.com.dyad.backoffice.entidade.movimentacao.Titulo;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoBaixaPedido;
import br.com.dyad.businessinfrastructure.utils.BusinessUtils;
import br.com.dyad.infrastructure.aspect.UserInfoAspect;

public class EspelhoBaixaPedidoHTML {
	
	private CabecalhoBaixaPedido cabecalhoBaixaPedido;
	private List<ItemBaixaPedido> itensBaixaPedido;
	private List<Titulo> titulos;
	private SimpleDateFormat formatData;

	private String emissaoPedidoFormatada;
	private String observacaoCabecalho;
	
	public EspelhoBaixaPedidoHTML() {
		super();
		
		formatData =  new SimpleDateFormat();
	}
	
	public CabecalhoBaixaPedido getCabecalhoPedido() {
		return cabecalhoBaixaPedido;
	}

	public void setCabecalhoBaixaPedido(CabecalhoBaixaPedido cabecalhoBaixaPedido) {
		this.cabecalhoBaixaPedido = cabecalhoBaixaPedido;
	}

	public List<ItemBaixaPedido> getItensBaixaPedido() {
		return itensBaixaPedido;
	}

	public void setItensBaixaPedido(List<ItemBaixaPedido> itensBaixaPedido) {
		this.itensBaixaPedido = itensBaixaPedido;
	}

	public List<Titulo> getTitulos() {
		return titulos;
	}

	public void setTitulos(List<Titulo> titulos) {
		this.titulos = titulos;
	}

	public String preparaHtml() {
		StringBuffer html = new StringBuffer();
		
		html.append(this.preparaSessao0());
		html.append(this.preparaSessao1());
		html.append(this.preparaSessao2());
		html.append(this.preparaSessao3());
		html.append(this.preparaSessao4());
		html.append(this.preparaSessao5());
		html.append(this.preparaSessao6());
		html.append(this.preparaSessao7());
		
		return html.toString();
	}

	public String preparaHTMLItensPedido() {
		StringBuffer htmlItensPedido = new StringBuffer();
		
		if( itensBaixaPedido == null ){
			return "";
		}
		
		for (ItemBaixaPedido itemPedido : itensBaixaPedido) {
			htmlItensPedido.append("" +
				"                                 <tr class=\"conteudoTabelaFalse\" valign=\"middle\">" +
				"                                    <td>" + (itemPedido.getId() == null ? "" : itemPedido.getId() ) + "</td>" + //Chave
//				"                                    <td>" + "&nbsp;" + "</td>" + //Classe
				"                                    <td>" + (itemPedido.getRecurso().getCodigo() == null ? "" : itemPedido.getRecurso().getCodigo()) + "</td>" + //Recurso
//				"                                    <td>" + "&nbsp;" + "</td>" + //Entrega
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + (itemPedido.getQuantidade() == null ? "" : itemPedido.getQuantidade() ) + "</td>" + //Quantidade
//				"                                    <td>" + "" + "</td>" + //Un
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + (itemPedido.getUnitario() == null ? "" : itemPedido.getUnitario()) + "</td>" + //Unitario
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + (itemPedido.getDesconto() != null ? itemPedido.getDesconto() : "0,00") + "</td>" + //Desconto
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + (itemPedido.getDescontoItem() != null ? itemPedido.getDescontoItem() : "0,00") + "</td>" + //Desconto Item
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + (itemPedido.getFrete() != null ? itemPedido.getFrete() : "0,00") + "</td>" + //Frete
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + (itemPedido.getTotal() != null ? itemPedido.getTotal() : "0,00") + "</td>" + //Total
				"                                 </tr>"
				);
		}
		
		return htmlItensPedido.toString();
	}
	/**
	 * De acordo com o Franklin o pedido não deve haver títulos. Então não será mostrado no espelhamento
	 * do pedido.
	 * Descrição do Método
	 * 
	 * @param parametro1  Descrição do parâmetro 1
	 * @param parametro2  Descrição do parâmetro 2	  
	 * @return            Descrição sobre o retorno do método
	 * @author            Haron
	 */
	
	public String preparaHTMLTitulos() {
		StringBuffer htmlTitulosPedido = new StringBuffer();
		
		for (Titulo titulo : titulos) {
			htmlTitulosPedido.append("" +
				"                                    <td>" + titulo.getId() + "</td>" + //Chave
				"                                    <td>" + titulo.getNumero() + "</td>" + //Numero
				"                                    <td>" + "&nbsp;" + "</td>" + //Prazo
				"                                    <td>" + titulo.getVencimento() + "</td>" + //Vencimento
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + titulo.getCorrecao() + "</td>" + //Correcao
				"                                    <td>" + "&nbsp" + "</td>" + //%
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + titulo.getPrincipal() + "</td>" + //Principal
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + (titulo.getAcrescimo() != null ? titulo.getAcrescimo() : "0,00") + "</td>" + //Acrescimos
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + (titulo.getDesconto() != null ? titulo.getDesconto() : "0,00") + "</td>" + //Descontos
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + titulo.getTotal() + "</td>" //Valor
				);
		}
		
		return htmlTitulosPedido.toString();
	}
	
	public String preparaHTMLPrincipal() {
		BigDecimal valorPrincipalPedido = new BigDecimal(0);
		DecimalFormat decimalFormat = new DecimalFormat();
		
		if( itensBaixaPedido == null ){
			return "";
		}
		
		for (ItemBaixaPedido itemBaixaPedido : itensBaixaPedido) {
			valorPrincipalPedido = valorPrincipalPedido.add(itemBaixaPedido.getPrincipal() != null ? itemBaixaPedido.getPrincipal() : new BigDecimal(0));
		}
		
		return decimalFormat.format(valorPrincipalPedido);
	}

	public String preparaHTMLTotal() {
		BigDecimal valorTotalPedido = new BigDecimal(0);
		DecimalFormat decimalFormat = new DecimalFormat();
		
		if( itensBaixaPedido == null ){
			return "";
		}
		
		for (ItemBaixaPedido itemBaixaPedido : itensBaixaPedido) {
			valorTotalPedido = valorTotalPedido.add(itemBaixaPedido.getPrincipal() != null ? itemBaixaPedido.getPrincipal() : new BigDecimal(0));
		}
		
		return decimalFormat.format(valorTotalPedido);
	}
	

	private String preparaSessao0() { 
		String sessao0 = "" +
	    "<style type=\"text/css\">" +
	    "  .header              { border-bottom: 1px solid #000000; border-top: 1px solid #000000; color: #000000; background-color: #FFFFFF;}" +
	    "  .date                { text-align: center;  vertical-align: top; padding-left : 4px; padding-right : 4px; }" +
	    "  .number              { text-align: right;   vertical-align: top; padding-left : 4px; padding-right : 4px; }" +
	    "  .negativeNumber      { text-align: right;   vertical-align: top; padding-left : 4px; padding-right : 4px; color: #ee0000; }" +
	    "  .textAlignLeft       { text-align: left ;   vertical-align: top; padding-left : 4px; padding-right : 4px; }" +
	    "  .textAlignRight      { text-align: right;   vertical-align: top; padding-left : 4px; padding-right : 4px; }" +
	    "  .textAlignJustify    { text-align: justify; vertical-align: top; padding-left : 4px; padding-right : 4px; }" +
	    "  .textAlignCenter     { text-align: center;  vertical-align: top; padding-left : 4px; padding-right : 4px; }" +
	    "  .noresult            { font-family: Verdana; font-size: 10pt; font-style : normal; font-weight : bold; color: red }" +
	    "  .breakPage           { page-break-after: always }" +
	    "  .bodyProcess         { background-color: #FFFFFF; background-image: none; margin-left: 5; margin-right: 0; margin-top: 5; margin-bottom: 0 }" +
	    "  .topLine             { position: fixed; top: 0px; height:10px; width: 100%; background-color: #FFFFFF; }" +
	    "" +        
	    "  @media screen {" +
	    "     .root_table          { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 8pt; font-weight : normal; text-align : left;    vertical-align: top; }" +
	    "     .fixed_header        { position:fixed; display: block; top: 10px; left: 10px;}" +
	    "     .layout_header       { display: none;  }" +
	    "     .base                { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 8pt; font-weight : normal; text-align : left;    vertical-align: top; }" +
	    "     .layoutPath          { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 7pt; font-weight : normal; text-align : left;    vertical-align: top; }" +
	    "     .enterprise          { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 13pt; font-weight : bold;   text-align : left;    vertical-align: middle; font-style : normal; color: #000000;}" +
	    "     .title               { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 12pt; font-weight : bold;   text-align : left;    vertical-align: top;    font-style : normal; color: #000000; }" +
	    "     .dateAndUser         { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 8pt; font-weight : normal; text-align : right;   vertical-align: top;    color: #000000; }" +
	    "     .variables           { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 8pt; font-weight : normal; text-align : justify; vertical-align: top;    color: #000000; }" +
	    "     .columnGroupHeader   { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 8pt; font-weight : bold;   text-align : center;  vertical-align: bottom; padding-left: 2px; padding-right: 2px; background-color: #FFFFFF;}" +
	    "     .columnHeader        { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 8pt; font-weight : bold;   vertical-align: bottom; padding-left: 4px; padding-right : 4px; background-color: #FFFFFF;}" +
	    "     .group               { font-family: Verdana; font-size : 10pt; font-style : normal; font-variant : normal; font-weight : bold; color: #000000; vertical-align: top; }" +
	    "     .total_group         { font-family: Verdana; font-size : 8pt; font-style : normal; font-variant : normal; font-weight : bold; color: #000000; vertical-align: top; text-align : right;}" +
	    "     .link                { text-align: left;   vertical-align: top; padding-left : 4px; padding-right : 4px; font-size: 8pt; font-weight: bold; color: #075076; cursor: pointer;}" +
	    "     .linkDate            { text-align: center; vertical-align: top; padding-left : 4px; padding-right : 4px; font-size: 8pt; font-weight: bold; color: #075076; cursor: pointer;}" +
	    "     .linkNumber          { text-align: right;  vertical-align: top; padding-left : 4px; padding-right : 4px; font-size: 8pt; font-weight: bold; color: #075076; cursor: pointer;}" +
	    "  }" +
        "" +
	    "  @media print {" +
	    "     .root_table          { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 6pt; font-weight : normal; text-align : left; vertical-align: top; }" +
	    "     .fixed_header        { display: none; }" +
	    "     .layout_header       { display: table-header-group; }" +
	    "     .base                { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 6pt; font-weight : normal; text-align : left;    vertical-align: top; }" +
	    "     .layoutPath          { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 5pt; font-weight : normal; text-align : left;    vertical-align: top; }" +
	    "     .enterprise          { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 11pt; font-weight : bold;   text-align : left;    vertical-align: middle; font-style : normal; color: #000000;}" +
	    "     .title               { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 10pt; font-weight : bold;   text-align : left;    vertical-align: top;    font-style : normal; color: #000000; }" +
	    "     .dateAndUser         { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 6pt; font-weight : normal; text-align : right;   vertical-align: top;    color: #000000; }" +
	    "     .variables           { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 6pt; font-weight : normal; text-align : justify; vertical-align: top;    color: #000000; }" +
	    "     .columnGroupHeader   { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 6pt; font-weight : bold;   text-align : center;  vertical-align: bottom; padding-left: 2px; padding-right: 2px; background-color: #FFFFFF;}" +
	    "     .columnHeader        { font-style: normal; font-family: verdana, arial, helvetica, sans-serif; font-size: 6pt; font-weight : bold;   vertical-align: bottom; padding-left: 4px; padding-right : 4px; background-color: #FFFFFF;}" +
	    "     .group               { font-family: Verdana; font-size : 8pt; font-style : normal; font-variant : normal; font-weight : bold; color: #000000; vertical-align: top; }" +
	    "     .total_group         { font-family: Verdana; font-size : 6pt; font-style : normal; font-variant : normal; font-weight : bold; color: #000000; vertical-align: top; text-align : right;}" +
	    "     .link                { text-align: left;   vertical-align: top; padding-left : 4px; padding-right : 4px; font-size: 6pt; font-weight: bold; color: #075076; cursor: pointer;}" +
	    "     .linkDate            { text-align: center; vertical-align: top; padding-left : 4px; padding-right : 4px; font-size: 6pt; font-weight: bold; color: #075076; cursor: pointer;}" +
	    "     .linkNumber          { text-align: right;  vertical-align: top; padding-left : 4px; padding-right : 4px; font-size: 6pt; font-weight: bold; color: #075076; cursor: pointer;}" +
	    "  }" +
	    "</style>" +
	    "<script>" +
	    "  document.body.style.backgroundImage = \"\"" +
	    "</script>" +
        "" +
	    "<table id=\"lid-0\" treeexpansionlevel=\"0\" class=\"root_table\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tbody>" +
	    "   </tbody>" +
	    "</table>" +
        "" +
	    "<style type=\"text/css\">" +
	    "<!--" +
	    "  @media screen {" +
	    "     body                 {  margin-left: 0px; margin-top: 0px;  margin-right: 0px;   margin-bottom: 0px;                                                                          }" +
	    "     .tit                 {  font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";  font-size: 10px;  font-weight: bold;   color: #666666;                                 }" +
	    "     .tituloTabela        {  font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";  font-size: 10px;  font-weight: bold;   color: #FFFFFF;   background-color: #999999;    }" +
	    "     .conteudoTabelaTrue  {  font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";  font-size: 10px;  color: #666666;   	background-color: #F1F1F1;                      }" +
	    "     .conteudoTabelaFalse {	 font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";	font-size: 10px;	color: #666666;                                                 }" +
	    "     .tp                  {	 font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";  font-size: 10px;  color: #666666;                                                   }" +
	    "     .tpn                 {	 font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";  font-size: 10px;  color: #666666;      /* font-weight: bold; */                     }" +
	    "     .titulo              {  font-size:   12px                                                                                                                                     }" +
	    "  }" +
	    "  @media print {" +
	    "     body                 {  margin-left: 0px; margin-top: 0px;  margin-right: 0px;   margin-bottom: 0px;                                                                          }" +
	    "     .tit                 {  font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";  font-size: 8px;  font-weight: bold;   color: #666666;                                  }" +
	    "     .tituloTabela        {  font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";  font-size: 8px;  font-weight: bold;   color: #FFFFFF;   background-color: #999999;     }" +
	    "     .conteudoTabelaTrue  {  font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";  font-size: 8px;  color: #666666;   	  background-color: #F1F1F1;                    }" +
	    "     .conteudoTabelaFalse {  font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";	font-size: 8px;	color: #666666;                                                         }" +
	    "     .tp                  {  font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";  font-size: 8px;  color: #666666;                                                       }" +
	    "     .tpn                 {  font-family: Verdana, \"Trebuchet MS\", Arial, \"MS Sans Serif\";  font-size: 8px;  color: #666666;    /* font-weight: bold; */                           }" +
	    "     .titulo              {  font-size:   10px                                                                                                                                     }" +
	    "}" +
	    "-->" +
	    "</style>" +
        "";
	   return sessao0;
	}
	
	private String preparaSessao1() { 
		formatData.applyPattern("dd/MM/yyyy hh:mm:ss");
		String sessao1 = "" +
		"   <!------------------------------------------->" + 
		"   <!------- LOGOTIPO / DADOS DA EMPRESA ------->" +
		"   <!------------------------------------------->" +
		"   <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"      <tbody>" +
		"         <tr valign=\"top\">" +
//		"" +
//		"            <!------------------------------------------->" +
//		"            <!----------------- LOGOTIPO ---------------->" +
//		"            <!------------------------------------------->" +
//		"            <td width=\"181px\">" +
//		"               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
//		"                  <tbody>" +
//		"                     <tr valign=\"top\">" +
//		"                        <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
//		"  	                        <table cellpadding=\"5\" width=\"100%\">" +
//		"                              <tbody>" +
//		"                                 <tr>" +
//		"                                    <td>" +
//		"                                    	&nbsp;" +
//		"                                    </td>" +
//		"                                 </tr>" +
//		"                              </tbody>" +
//		"                           </table>" +
//		"  		                </td>" +
//		"                    </tr>" +	
//		"                 </tbody>" +
//		"              </table>" +
//		"           </td>" +
//		"           <td width=\"20\">" +
//		"              &nbsp;" +
//		"           </td>" +
		"" +
		"           <!------------------------------------------->" +
		"           <!------------- DADOS DA EMPRESA ------------>" +
		"           <!------------------------------------------->" +
		"  	        <td>" +
		"              <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"                 <tbody>" +
		"  	                 <tr valign=\"top\">" +
		"                       <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
		"                          <table width=\"220\">" +
		"  	                          <tbody>" +
		"  				                 <tr>" +
		"  					                <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\" width=\"220\">" +
		"                                      <span class=\"tit titulo\">" +
		"  					                      <span style=\"font-size: 14px;\">" +
		"  					                         " + (cabecalhoBaixaPedido.getEstabelecimento().getNome() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getNome()) +
		"  					                      </span>" +
		"  					                   </span>" +
		"  					                </td>" +
		"  					             </tr>" +
		"  			                  </tbody>" +
		"  			               </table>" +
		"  			               <table cellpadding=\"5\" width=\"100%\">" +
		"                             <tbody>" +
		"                                <tr>" +
		"                                   <td>" +
		"                                      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"                                         <tbody>" +
		"                                            <tr>" +
		"                                               <td class=\"tit\">" +
		"                                                  &nbsp;" +
		"                                               </td>" +
		"                                               <td class=\"tit\" style=\"text-align: right;\">" +
		"                                                  <span>Ch Pedido:" + 
		"                                                     <span style=\"text-align: right; font-size: 16px;\" trebuchet=\"\" ,=\"\" arial,=\"\" ms=\"\" sans=\"\" serif=\"\" ;=\"\" font-weight:=\"\" bold;=\"\" color:=\"\" #666666;=\"\">" +
		"                                                        " + (cabecalhoBaixaPedido.getId() == null ? "" : cabecalhoBaixaPedido.getId()) +
		"                                                     </span>" +
		"                                                  </span>" +
		"                                               </td>" +
		"                                            </tr>" +
		"                                            <tr>" +
		"                                               <td class=\"tpn\">" + (cabecalhoBaixaPedido.getEstabelecimento().getLogradouro() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getLogradouro()) + "-" + (cabecalhoBaixaPedido.getEstabelecimento().getBairro() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getBairro()) + "</td>" +
		"                                               <td class=\"tpn\" style=\"text-align: right;\">&nbsp;</td>" +
		"                                            </tr>" +
		"                                            <tr>" +
		"                                               <td class=\"tpn\">" + (cabecalhoBaixaPedido.getEstabelecimento().getBairro() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getBairro()) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
		"                                                  " + (cabecalhoBaixaPedido.getEstabelecimento().getLocalidade() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getLocalidade()) + "-" + (cabecalhoBaixaPedido.getEstabelecimento().getUf() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getUf()) + "&nbsp;" +
		"                                               </td>" +
		"                                               <td class=\"tpn\" style=\"text-align: right;\">" +
		"                                                  " + formatData.format(new Date()) +
		"                                               </td>" +
		"                                            </tr>" +
		"                                            <tr>" +
		"                                               <td class=\"tpn\">" +
		"                                                  CEP: " + (cabecalhoBaixaPedido.getEstabelecimento().getCep() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getCep()) + "&nbsp;" +
		"                                               </td>" +
		"                                               <td class=\"tpn\" style=\"text-align: right;\">" +
		"                                                  " + UserInfoAspect.getUser() +
		"                                               </td>" +
		"                                            </tr>" +
		"                                            <tr>" +
		"                                               <td class=\"tpn\">" +
		"                                                  Fone: " + (cabecalhoBaixaPedido.getEstabelecimento().getFone() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getFone()) + 
		"                                               </td>" +
		"                                               <td class=\"tpn\" style=\"text-align: right;\">" +
		"                                                  " + UserInfoAspect.getDatabase() +
		"                                               </td>" +
		"                                            </tr>" +
		"                                         </tbody>" +
		"                                      </table>" +
		"                                   </td>" +
		"                                </tr>" +
		"                             </tbody>" +
		"                          </table>" +
		"  		                </td>" +
		"  	                 </tr>" +	
		"                 </tbody>" +
		"              </table>" +
		"           </td>" +
		"        </tr>" +	
		"     </tbody>" +
		"  </table>";
		
		return sessao1;
	}

	private String preparaSessao2() { 
		String sessao2 = "" +
		"   <!------------------------------------------->" +
		"   <!----- PESSOA / INFORMACOES DO PEDIDO ------>" +
		"   <!------------------------------------------->" +
		"   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"      <tbody>" +
		"         <tr valign=\"top\">" +
		"" +	         
		"            <!------------------------------------------->" +
		"            <!------------------ PESSOA ----------------->" +
		"            <!------------------------------------------->" +
		"            <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\" width=\"49%\">" +
		"               <table width=\"220px;\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
		"                           <span class=\"tit titulo\">" +
		"                              Pessoa" + 
		"                           </span>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"               <table cellpadding=\"5\" width=\"100%\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td>" +
		"                           <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"                              <tbody>" +
		"                                 <tr>" +
		"                                    <td>" +
		"  								        <span class=\"tpn\">" + 
		"                                          <b>" +
		"                                             " + (cabecalhoBaixaPedido.getEntidade().getCodigo() == null ? "" : cabecalhoBaixaPedido.getEntidade().getCodigo()) +
		"                                          </b>" +
		"    							           <br>" +
		"									       " + (cabecalhoBaixaPedido.getEntidade().getNome() == null ? "" : cabecalhoBaixaPedido.getEntidade().getNome()) + 
		"                                       </span>" + 
		"    								    <br>" +
		"                                       <span class=\"tp\">" + 
		"    								       " + (cabecalhoBaixaPedido.getEntidade().getLogradouro() == null ? "" : cabecalhoBaixaPedido.getEntidade().getLogradouro()) + 
		"                               	       <br>" +
		"                                          " + (cabecalhoBaixaPedido.getEntidade().getBairro() == null ? "" : cabecalhoBaixaPedido.getEntidade().getBairro()) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + (cabecalhoBaixaPedido.getEntidade().getLocalidade() == null ? "" : cabecalhoBaixaPedido.getEntidade().getLocalidade()) + "-" + (cabecalhoBaixaPedido.getEntidade().getUf() == null ? "" : cabecalhoBaixaPedido.getEntidade().getUf()) + 
		"                                          <br>" +
		"                                          CEP: " + (cabecalhoBaixaPedido.getEntidade().getCep() == null ? "" : cabecalhoBaixaPedido.getEntidade().getCep()) + 
		"                                          <br>" +
		"           						       Fone: " + (cabecalhoBaixaPedido.getEntidade().getFone() == null ? "" : cabecalhoBaixaPedido.getEntidade().getFone()) + 
		"    								       <br>" +
		"    								       CNPJ: " + (cabecalhoBaixaPedido.getEntidade().getCpfcnpj() == null ? "" : BusinessUtils.formataCpfCnpj(cabecalhoBaixaPedido.getEntidade().getCpfcnpj())) + 
		"      							        </span>" +
		"	                                 </td>" +
		"                                 </tr>" +
		"                              </tbody>" +
		"                           </table>" +
		"				         </td>" +
		"	                  </tr>" +
		"			       </tbody>" +
		"               </table>" +
		"            </td>" +
		"            <td width=\"2%\">" +
		"            </td>" +
		"" + 
		"            <!------------------------------------------->" +
		"            <!---------- INFORMAÇOES DO PEDIDO ---------->" +
		"            <!------------------------------------------->" +
		"            <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\" width=\"49%\">" +
		"               <table width=\"220px\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"			             <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
		"                           <span class=\"tit titulo\">" +
		"                              Informações de Cabeçalho" +
		"                           </span>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"               <table cellpadding=\"5\" width=\"100%\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td>" +
		"                           <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"                              <tbody>" +
		"                                 <tr>" +
		"                                    <td class=\"tp\">" +
		"                                       <span class=\"tpn\">" +
		"                                          Emissão: " +
		"                                       </span>" +
		"                                       " + (emissaoPedidoFormatada == null ? "" : emissaoPedidoFormatada) + "&nbsp;&nbsp;" + 
		"                                       <span class=\"tpn\">" +
		"                                          <b>" +
		"                                          " + ("") +
		"                                          </b>" +
		"                                          ("+""+")" +
		"                                       </span>" +
		"                                       <br>" +
		"                                       <span class=\"tpn\">" +
		"                                          Estabelecimento:" + 
		"                                       </span>" +
		"                                       " + (cabecalhoBaixaPedido.getEstabelecimento().getCodigo() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getCodigo()) +
		"                                       <span class=\"tpn\">" +
		"                                          <br>" +
		"                                          Observação:" + 
		"                                       </span>" +
		"                                       " + (observacaoCabecalho == null ? "" : observacaoCabecalho) +
		"                                       <br>" +
		"                                       <span class=\"tpn\">" +
		"                                          " + "&nbsp;" + 
		"                                       </span>" +
		"                                       <span class=\"tp\">" +
		"                                       </span>" +
		"                                       &nbsp;&nbsp;&nbsp;" +
		"                                       <span class=\"tpn\">" +
		"                                          <br>" +
		"                                          &nbsp;" + 
		"                                       </span>" +
		"                                       <br>" +
		"                                    </td>" +
		"                                 </tr>" +
		"                              </tbody>" +
		"                           </table>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"            </td>" +
		"         </tr>" +
		"      </tbody>" +
		"   </table>" +
		"   <br>";
		
		return sessao2;
	}
	
	private String preparaSessao3() { 

		String sessao3 = "" +
		"   <!------------------------------------------->" +
		"   <!--------- FATURAR DE / COBRANCA ----------->" +
		"   <!------------------------------------------->" +
		"   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"      <tbody>" +
		"         <tr valign=\"top\">" +
		"" +            
		"            <!------------------------------------------->" +
		"            <!--------------- FATURAR DE ---------------->" +
		"            <!------------------------------------------->" +
		"            <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\" width=\"49%\">" +
		"               <table width=\"220px;\">" +
		"                  <tbody>" +
		"                     <tr>" +   				
		"                        <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
		"  				            <span class=\"tit titulo\">" +
		"  				               Faturar  de" + 
		"  				            </span>" +
		"  				         </td>" +
		"  			          </tr>" +
		"  			       </tbody>" +
		"  			    </table>" +
		"               <table cellpadding=\"5\" width=\"100%\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td>" +
		"                           <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"                              <tbody>" +
		"                                 <tr>" +
		"                                    <td class=\"tp\">" +
		"                                       <span class=\"tpn\">" +
		"                                          <b>" +
		"                                             " + (cabecalhoBaixaPedido.getEstabelecimento().getCodigo() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getCodigo()) +
		"                                          </b>" +
		"                                          <br>" +
		"                                          " + (cabecalhoBaixaPedido.getEstabelecimento().getNome() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getNome()) +
		"                                       </span>" +
		"                                       <br>" +
		"                                       <span class=\"tp\">" +
		"                                          " + (cabecalhoBaixaPedido.getEstabelecimento().getLogradouro() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getLogradouro()) + "-" + (cabecalhoBaixaPedido.getEstabelecimento().getComplemento() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getComplemento()) +
		"                                          <br>" +
		"                                          " + (cabecalhoBaixaPedido.getEstabelecimento().getBairro() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getBairro()) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + (cabecalhoBaixaPedido.getEstabelecimento().getLocalidade() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getLocalidade()) + "-" + (cabecalhoBaixaPedido.getEstabelecimento().getUf() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getUf()) +
		"                                          <br>" +
		"                                          CEP: " + (cabecalhoBaixaPedido.getEstabelecimento().getCep() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getCep()) +
		"                                          <br>" +
		"                                          Fone: " + (cabecalhoBaixaPedido.getEstabelecimento().getFone() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getFone()) +
		"                                          <br>" + 
		"                                          E-Mail: " + (cabecalhoBaixaPedido.getEstabelecimento().getEmail() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getEmail()) +
		"                                          <br>" +
		"                                          CNPJ: " + (cabecalhoBaixaPedido.getEstabelecimento().getCpfcnpj() == null ? "" : BusinessUtils.formataCpfCnpj(cabecalhoBaixaPedido.getEstabelecimento().getCpfcnpj())) +
		"                                          <br>" +
		"                                          Insc Municipal: " + (cabecalhoBaixaPedido.getEstabelecimento().getInscricaoEstadual() == null ? "" : cabecalhoBaixaPedido.getEstabelecimento().getInscricaoEstadual()) +
		"                                       </span>" +
		"                                    </td>" +
		"                                 </tr>" +
		"                              </tbody>" +
		"                           </table>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"  		     </td>" +
		"  		     <td width=\"2%\">" +
		"" +  		    
		"            <!------------------------------------------->" +
		"            <!---------------- COBRANCA ----------------->" +
		"            <!------------------------------------------->" +
		"  	         </td>" +
		"            <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\" width=\"49%\">" +
		"  		        <table width=\"220px\">" +
		"  			       <tbody>" +
		"  			          <tr>" +
		"  			             <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
		"  			                <span class=\"tit titulo\">" +
		"  			                   Cobrança" +
		"  			                </span>" +
		"  			             </td>" +
		"  			          </tr>" +
		"  			       </tbody>" +
		"               </table>" +
		"               <table cellpadding=\"5\" width=\"100%\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td>" +
		"                           <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"                              <tbody>" +
		"                                 <tr>" +
		"                                    <td class=\"tp\">" +
		"                                    </td>" +
		"                                 </tr>" +
		"                              </tbody>" +
		"                           </table>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"            </td>" +
		"         </tr>" +
		"      </tbody>" +
		"    </table>" +
		"    <br>";
		
		return sessao3;
	}

	private String preparaSessao4() { 

		String sessao4 = "" +
		"   <!------------------------------------------->" +
		"   <!------------- ITENS DO PEDIDO ------------->" +
		"   <!------------------------------------------->" +
		"   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"      <tbody>" +
		"         <tr valign=\"top\">" +
		"            <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
		"               <table width=\"220\">" +
		"                  <tbody>" +
		"  		              <tr>" +
		"                        <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\" width=\"220\">" +
		"                           <span class=\"tit titulo\">" +
		"                              Itens" +
		"                           </span>" +
		"  			             </td>" +
		"  			          </tr>" +
		"  			       </tbody>" +
		"  			    </table>" +
		"               <table cellpadding=\"5\" width=\"100%\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td>" +
		"                           <table border=\"0\" cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">" +
		"                              <tbody>" +
		"                                 <!--------------->" +
		"                                 <!-- Cabeçalho -->" +
		"                                 <!--------------->" +
		"                                 <tr style=\"background: none repeat scroll 0% 0% rgb(153, 153, 153); color: rgb(255, 255, 255); font-family: Verdana; font-size: 10px; font-weight: bold;\" valign=\"middle\">" +
		"                                    <td>Chave</td>" +
//		"                                    <td>Classe</td>" +
		"                                    <td>Recurso</td>" +
//		"                                    <td>Entrega</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">Qde</td>" +
//		"                                    <td>Un</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">Unitário</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">Desconto</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">Desconto Item</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">Frete</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">Total</td>" +
		"                                 </tr>" +
		"" +                              
		"                                 <!-------------------->" +
		"                                 <!--- Itens Pedido --->" +
		"                                 <!-------------------->" +
		//"                                 <tr class=\"conteudoTabelaFalse\" valign=\"middle\">" +

		this.preparaHTMLItensPedido() +

		//"                                 </tr>" +
		"                              </tbody>" +
		"                           </table>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"            </td>" +
		"         </tr>	" +
		"      </tbody>" +
		"   </table>" +
		"   <br>";
		
		return sessao4;
	}

	private String preparaSessao5() { 
		String sessao5 = "" +
		"   <!------------------------------------------->" +
		"   <!---------------- TÍTULOS ------------------>" +
		"   <!------------------------------------------->" +
		"   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"      <tbody>" +
		"         <tr valign=\"top\">" +
		"  		     <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
		"  		        <table width=\"220\">" +
		"  			       <tbody>" +
		"  	                  <tr>" +
		"  			             <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\" width=\"220\">" +
		"  			                <span class=\"tit titulo\">" +
		"  			                   Títulos" +
		"  			                </span>" +
		"  			             </td>" +
		"  			          </tr>" +
		"  			       </tbody>" +
		"  			    </table>" +
		"  		        <table cellpadding=\"5\" width=\"100%\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td>" +
		"                           <table border=\"0\" cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">" +
		"                              <tbody>" +
		"" +
		"                                 <!--------------->" +
		"                                 <!-- Cabeçalho -->" +
		"                                 <!--------------->" +
		"                                 <tr style=\"background: none repeat scroll 0% 0% rgb(153, 153, 153); color: rgb(255, 255, 255); font-family: Verdana; font-size: 10px; font-weight: bold;\" valign=\"middle\">" +
		"                                    <td>Chave</td>" +
		"                                    <td>Número</td>" +
		"                                    <td>Prazo</td>" +
		"                                    <td>Vencimento</td>" +
		"                                    <td>Correção</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">%</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">Principal</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">Acréscimos*</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">Descontos*</td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">Valor*</td>" +
		"                                 </tr>" +
		"" +
		"                                 <!--------------->" +
		"                                 <!--- Titulos --->" +
		"                                 <!--------------->" +
		"                                 <tr class=\"conteudoTabelaTrue\" valign=\"middle\">" +

		this.preparaHTMLTitulos() +

		"                                 </tr>" +
		"                              </tbody>" +
		"                           </table>" +
		"                           <span class=\"tp\">" +
		"                              * Valores sujeitos a alteração até a baixa do título" +
		"                           </span>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"  		     </td>" +
		"  	      </tr>" +
		"      </tbody>" +
		"   </table>" +
		"   <br>";
		
		return sessao5;
	}

	private String preparaSessao6() { 
		String sessao6 = "" +
		"   <!------------------------------------------->" +
		"   <!-- TOTAIS/CREDITOS E DEBITOS DE IMPOSTOS -->" +
		"   <!------------------------------------------->" +
		"   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"      <tbody>" +
		"  	      <tr valign=\"top\">" +
		"            <!------------------------------------>" +
		"            <!-------------- TOTAIS -------------->" +
		"            <!------------------------------------>" +
		"            <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\" width=\"49%\">" +
		"               <table width=\"220px;\">" +
		"  		           <tbody>" +
		"  			          <tr>" +
		"  				         <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
		"                           <span class=\"tit titulo\">" +
		"                              Totais" +
		"                           </span>" +
		"                        </td>" +
		"                     </tr>" +
		"  			       </tbody>" +
		"  			    </table>" +
		"               <table cellpadding=\"5\" width=\"100%\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td>" +
		"                           <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"                              <tbody>" +
		"                                 <tr class=\"conteudoTabelaTrue\">" +
		"                                     <td>" +
		"                                       Total de Mercadorias:" +
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + this.preparaHTMLPrincipal() +
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaFalse\">" +
		"                                    <td>" +
		"                                       <b>" +
		"                                          Total NF:" +
		"                                       </b>" +
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right; font-weight: bold;\">" +
		"                                       " + this.preparaHTMLTotal() +
		"                                    </td>" +
		"                                 </tr>" +
		"                              </tbody>" +
		"                           </table>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"            </td>" +
		"" +
		"            <td width=\"2%\">" +
		"  		     </td>" +
		"" +
		"            <!------------------------------------>" +
		"            <!-- CRÉDITOS E DÉBITOS DE IMPOSTOS -->" +
		"            <!------------------------------------>" +
		"            <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\" width=\"49%\">" +
		"  		        <table width=\"270px\">" +
		"  		           <tbody>" +
		"  		 	          <tr>" +
		"  			             <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
		"  			                <span class=\"tit titulo\">" +
		"  			                   Créditos / Débitos de Impostos" +
		"     			             </span>" +
		"     			          </td>" +
		"  		   		      </tr>" +
		"  	               </tbody>" +
		"  	            </table>" +
		"               <table cellpadding=\"5\" width=\"100%\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td>" +
		"                           <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"                              <tbody>" +
		"                                 <tr class=\"conteudoTabelaFalse\">" +
		"                                    <td>" +
		"                                       COFINS Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoBaixaPedido.getCofins() == null ? "0,00" : cabecalhoBaixaPedido.getCofins()) + 
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaTrue\">" +
		"                                    <td>" +
		"                                       CSLL Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoBaixaPedido.getCsll() == null ? "0,00" : cabecalhoBaixaPedido.getCsll()) +
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaFalse\">" +
		"                                    <td>" +
		"                                       INSS Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoBaixaPedido.getInss() == null ? "0,00" : cabecalhoBaixaPedido.getInss()) +
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaTrue\">" +
		"                                    <td>" +
		"                                       IR Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoBaixaPedido.getIr() == null ? "0,00" : cabecalhoBaixaPedido.getIr()) +
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaFalse\">" +
		"                                    <td>" +
		"                                       ISS Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoBaixaPedido.getIss() == null ? "0,00" : cabecalhoBaixaPedido.getIss()) +
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaTrue\">" +
		"                                    <td>" +
		"                                       PIS Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoBaixaPedido.getPis() == null ? "0,00" : cabecalhoBaixaPedido.getPis()) +
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaFalse\">" +
		"                                    <td colspan=\"2\">" +
		"                                       Observação Fiscal: Prestação de Serviço" +
		"                                    </td>" +
		"                                 </tr>" +
		"                              </tbody>" +
		"                           </table>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"  	         </td>" +
		"         </tr>" +
		"      </tbody>" +
		"   </table>" +
		"   <br>";
	
		return sessao6;
	} 

	private String preparaSessao7() { 
		String sessao7 = "" +
		"   <!------------------------------------------->" +
		"   <!------------------ VISTO ------------------>" +
		"   <!------------------------------------------->" +
		"   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
		"      <tbody>" +
		"         <tr valign=\"top\">" +
		"  	         <td style=\"\" width=\"49%\">" +
		"  		        <table width=\"220px;\">" +
		"                  <tbody>" +
		"                     <tr>" +				
		"  				         <td style=\"\">" +
		"  				            <span class=\"tit titulo\">" +
		"  		                    </span>" +
		"  			             </td>" +
		"  			          </tr>" +
		"  			       </tbody>" +
		"  			    </table>" +
		"               <table cellpadding=\"5\" width=\"100%\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"  		     </td>" +
		"  		     <td width=\"2%\">" +
		"  		     </td>" +
		"" +
		"            <!------------------------------------------->" +
		"            <!------------------ VISTO ------------------>" +
		"            <!------------------------------------------->" +
		"  	         <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\" width=\"49%\">" +
		"     	        <table width=\"220px\">" +
		"     		       <tbody>" +
		"  			          <tr>" +
		"  			             <td style=\"border-bottom: 2px solid rgb(181, 181, 181); border-right: 2px solid rgb(181, 181, 181);\">" +
		"  				            <span class=\"tit titulo\">" +
		"  				               Visto" +
		"  				            </span>" +
		"  				         </td>" +
		"  		              </tr>" +
		"     	           </tbody>" +
		"  			    </table>" +
		"               <table cellpadding=\"5\" width=\"100%\">" +
		"                  <tbody>" +
		"                     <tr>" +
		"                        <td>" +
		"                           <br>" +
		"                           <br>" +
		"                        </td>" +
		"                     </tr>" +
		"                  </tbody>" +
		"               </table>" +
		"  		     </td>" +
		"  	      </tr>" +
		"      </tbody>" +
		"   </table>" +
		"   <br>" +
		"   <div style=\"left: 0px; top: 0px; width: 0px; height: 0px;\" class=\"fbProxyElement\"></div>" +
		"   <div style=\"left: 0px; top: 0px; width: 0px; height: 0px;\" class=\"fbProxyElement\"></div>" +
		"   <div style=\"left: 0px; top: 0px; width: 0px; height: 0px;\" class=\"fbProxyElement\"></div>" +
		"   <div style=\"left: 0px; top: 0px; width: 0px; height: 0px;\" class=\"fbProxyElement\"></div>" +
		"   <div style=\"left: 0px; top: 0px; width: 0px; height: 0px;\" class=\"fbProxyElement\"></div>" +
		"   <div style=\"left: 0px; top: 0px; width: 0px; height: 0px;\" class=\"fbProxyElement\"></div>" +
		"   <div style=\"left: 0px; top: 0px; width: 0px; height: 0px;\" class=\"fbProxyElement\"></div>" +
		"   <div style=\"left: 0px; top: 0px; width: 0px; height: 0px;\" class=\"fbProxyElement\"></div>" +
		"   <div style=\"left: 0px; top: 0px; width: 0px; height: 0px;\" class=\"fbProxyElement\"></div>" +
		"   <div style=\"left: 0px; top: 0px; width: 0px; height: 0px;\" class=\"fbProxyElement\"></div>";
		
		return sessao7;
	}
}
