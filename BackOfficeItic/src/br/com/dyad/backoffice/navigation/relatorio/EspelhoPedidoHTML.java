package br.com.dyad.backoffice.navigation.relatorio;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.dyad.backoffice.entidade.movimentacao.ItemPedido;
import br.com.dyad.backoffice.entidade.movimentacao.cabecalho.CabecalhoPedido;
import br.com.dyad.businessinfrastructure.utils.BusinessUtils;
import br.com.dyad.infrastructure.aspect.UserInfoAspect;

public class EspelhoPedidoHTML {
	
	private CabecalhoPedido cabecalhoPedido;
	private List<ItemPedido> itensPedido;
	
	private SimpleDateFormat formatData;

	private String statusPedido;
	private String emissaoPedidoFormatada;
	private String observacaoCabecalho;
	
	public EspelhoPedidoHTML() {
		super();
		
		formatData =  new SimpleDateFormat();
	}
	
	public CabecalhoPedido getCabecalhoPedido() {
		return cabecalhoPedido;
	}

	public void setCabecalhoPedido(CabecalhoPedido cabecalhoPedido) {
		this.cabecalhoPedido = cabecalhoPedido;
	}

	public List<ItemPedido> getItensPedido() {
		return itensPedido;
	}

	public void setItensPedido(List<ItemPedido> itensPedido) {
		this.itensPedido = itensPedido;
	}



	public String preparaHtml() {
		StringBuffer html = new StringBuffer();
		
		html.append(this.preparaSessao0());
		html.append(this.preparaSessao1());
		html.append(this.preparaSessao2());
		html.append(this.preparaSessao3());
		html.append(this.preparaSessao4());
		//html.append(this.preparaSessao5());
		html.append(this.preparaSessao6());
		html.append(this.preparaSessao7());
		
		return html.toString();
	}

	public String preparaHTMLItensPedido() {
		StringBuffer htmlItensPedido = new StringBuffer();
		
		if( itensPedido == null ){
			return "";
		}
		
		for (ItemPedido itemPedido : itensPedido) {
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
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + (itemPedido.getBase() != null ? itemPedido.getBase() : "0,00") + "</td>" + //Base
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
	
	/*public String preparaHTMLTitulosPedido() {
		StringBuffer htmlTitulosPedido = new StringBuffer();
		
		if( titulosPedido == null ){
			return "";
		}
		
		for (Titulo tituloPedido : titulosPedido) {
			htmlTitulosPedido.append("" +
				"                                    <td>" + tituloPedido.getId() + "</td>" + //Chave
				"                                    <td>" + tituloPedido.getNumero() + "</td>" + //Numero
				"                                    <td>" + "&nbsp;" + "</td>" + //Prazo
				"                                    <td>" + tituloPedido.getVencimento() + "</td>" + //Vencimento
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + tituloPedido.getCorrecao() + "</td>" + //Correcao
				"                                    <td>" + "&nbsp" + "</td>" + //%
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + tituloPedido.getPrincipal() + "</td>" + //Principal
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + "0,00" + "</td>" + //Acrescimos
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + "0,00" + "</td>" + //Descontos
				"                                    <td style=\"padding-right: 2px; text-align: right;\">" + tituloPedido.getTotal() + "</td>" //Valor
				);
		}
		
		return htmlTitulosPedido.toString();
	}*/
	
	public String preparaHTMLPrincipal() {
		BigDecimal valorPrincipalPedido = new BigDecimal(0);
		DecimalFormat decimalFormat = new DecimalFormat();
		
		if( itensPedido == null ){
			return "";
		}
		
		for (ItemPedido itemPedido : itensPedido) {
			valorPrincipalPedido = valorPrincipalPedido.add(itemPedido.getPrincipal() != null ? itemPedido.getPrincipal() : new BigDecimal(0));
		}
		
		return decimalFormat.format(valorPrincipalPedido);
	}

	public String preparaHTMLTotal() {
		BigDecimal valorTotalPedido = new BigDecimal(0);
		DecimalFormat decimalFormat = new DecimalFormat();
		
		if( itensPedido == null ){
			return "";
		}
		
		for (ItemPedido itemPedido : itensPedido) {
			valorTotalPedido = valorTotalPedido.add(itemPedido.getPrincipal() != null ? itemPedido.getPrincipal() : new BigDecimal(0));
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
		"  					                         " + (cabecalhoPedido.getEstabelecimento().getNome() == null ? "" : cabecalhoPedido.getEstabelecimento().getNome()) +
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
		"                                                        " + (cabecalhoPedido.getId() == null ? "" : cabecalhoPedido.getId()) +
		"                                                     </span>" +
		"                                                  </span>" +
		"                                               </td>" +
		"                                            </tr>" +
		"                                            <tr>" +
		"                                               <td class=\"tpn\">" + (cabecalhoPedido.getEstabelecimento().getLogradouro() == null ? "" : cabecalhoPedido.getEstabelecimento().getLogradouro()) + " - " + (cabecalhoPedido.getEstabelecimento().getBairro() == null ? "" : cabecalhoPedido.getEstabelecimento().getBairro()) + "</td>" +
		"                                               <td class=\"tpn\" style=\"text-align: right;\">&nbsp;</td>" +
		"                                            </tr>" +
		"                                            <tr>" +
		"                                               <td class=\"tpn\">" + (cabecalhoPedido.getEstabelecimento().getBairro() == null ? "" : cabecalhoPedido.getEstabelecimento().getBairro()) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
		"                                                  " + (cabecalhoPedido.getEstabelecimento().getLocalidade() == null ? "" : cabecalhoPedido.getEstabelecimento().getLocalidade().getNome()) + " - " + (cabecalhoPedido.getEstabelecimento().getUf() == null ? "" : cabecalhoPedido.getEstabelecimento().getUf()) + "&nbsp;" +
		"                                               </td>" +
		"                                               <td class=\"tpn\" style=\"text-align: right;\">" +
		"                                                  " + formatData.format(new Date()) +
		"                                               </td>" +
		"                                            </tr>" +
		"                                            <tr>" +
		"                                               <td class=\"tpn\">" +
		"                                                  CEP: " + (cabecalhoPedido.getEstabelecimento().getCep() == null ? "" : cabecalhoPedido.getEstabelecimento().getCep()) + "&nbsp;" +
		"                                               </td>" +
		"                                               <td class=\"tpn\" style=\"text-align: right;\">" +
		"                                                  " + UserInfoAspect.getUser() +
		"                                               </td>" +
		"                                            </tr>" +
		"                                            <tr>" +
		"                                               <td class=\"tpn\">" +
		"                                                  Fone: " + (cabecalhoPedido.getEstabelecimento().getFone() == null ? "" : cabecalhoPedido.getEstabelecimento().getFone()) + 
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
		"                                             " + (cabecalhoPedido.getEntidade().getCodigo() == null ? "" : cabecalhoPedido.getEntidade().getCodigo()) +
		"                                          </b>" +
		"    							           <br>" +
		"									       " + (cabecalhoPedido.getEntidade().getNome() == null ? "" : cabecalhoPedido.getEntidade().getNome()) + 
		"                                       </span>" + 
		"    								    <br>" +
		"                                       <span class=\"tp\">" + 
		"    								       " + (cabecalhoPedido.getEntidade().getLogradouro() == null ? "" : cabecalhoPedido.getEntidade().getLogradouro()) + 
		"                               	       <br>" +
		"                                          " + (cabecalhoPedido.getEntidade().getBairro() == null ? "" : cabecalhoPedido.getEntidade().getBairro()) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + (cabecalhoPedido.getEntidade().getLocalidade() == null ? "" : cabecalhoPedido.getEntidade().getLocalidade()) + " - " + (cabecalhoPedido.getEntidade().getUf() == null ? "" : cabecalhoPedido.getEntidade().getUf()) + 
		"                                          <br>" +
		"                                          CEP: " + (cabecalhoPedido.getEntidade().getCep() == null ? "" : cabecalhoPedido.getEntidade().getCep()) + 
		"                                          <br>" +
		"           						       Fone: " + (cabecalhoPedido.getEntidade().getFone() == null ? "" : cabecalhoPedido.getEntidade().getFone()) + 
		"    								       <br>" +
		"    								       CNPJ: " + (cabecalhoPedido.getEntidade().getCpfcnpj() == null ? "" : BusinessUtils.formataCpfCnpj(cabecalhoPedido.getEntidade().getCpfcnpj())) + 
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
		"                                          " + (statusPedido == null ? "" : statusPedido) +
		"                                          </b>" +
		"                                          ("+(cabecalhoPedido.getAprovador() == null ? "" : cabecalhoPedido.getAprovador())+" - "+(cabecalhoPedido.getAprovacao() == null ? "" : cabecalhoPedido.getAprovacao() )+")" +
		"                                       </span>" +
		"                                       <br>" +
		"                                       <span class=\"tpn\">" +
		"                                          Estabelecimento:" + 
		"                                       </span>" +
		"                                       " + (cabecalhoPedido.getEstabelecimento().getCodigo() == null ? "" : cabecalhoPedido.getEstabelecimento().getCodigo()) +
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
		"                                             " + (cabecalhoPedido.getEstabelecimento().getCodigo() == null ? "" : cabecalhoPedido.getEstabelecimento().getCodigo()) +
		"                                          </b>" +
		"                                          <br>" +
		"                                          " + (cabecalhoPedido.getEstabelecimento().getNome() == null ? "" : cabecalhoPedido.getEstabelecimento().getNome()) +
		"                                       </span>" +
		"                                       <br>" +
		"                                       <span class=\"tp\">" +
		"                                          " + (cabecalhoPedido.getEstabelecimento().getLogradouro() == null ? "" : cabecalhoPedido.getEstabelecimento().getLogradouro()) + " - " + (cabecalhoPedido.getEstabelecimento().getComplemento() == null ? "" : cabecalhoPedido.getEstabelecimento().getComplemento()) +
		"                                          <br>" +
		"                                          " + (cabecalhoPedido.getEstabelecimento().getBairro() == null ? "" : cabecalhoPedido.getEstabelecimento().getBairro()) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + (cabecalhoPedido.getEstabelecimento().getLocalidade() == null ? "" : cabecalhoPedido.getEstabelecimento().getLocalidade()) + " - " + (cabecalhoPedido.getEstabelecimento().getUf() == null ? "" : cabecalhoPedido.getEstabelecimento().getUf()) +
		"                                          <br>" +
		"                                          CEP: " + (cabecalhoPedido.getEstabelecimento().getCep() == null ? "" : cabecalhoPedido.getEstabelecimento().getCep()) +
		"                                          <br>" +
		"                                          Fone: " + (cabecalhoPedido.getEstabelecimento().getFone() == null ? "" : cabecalhoPedido.getEstabelecimento().getFone()) +
		"                                          <br>" + 
		"                                          E-Mail: " + (cabecalhoPedido.getEstabelecimento().getEmail() == null ? "" : cabecalhoPedido.getEstabelecimento().getEmail()) +
		"                                          <br>" +
		"                                          CNPJ: " + (cabecalhoPedido.getEstabelecimento().getCpfcnpj() == null ? "" : BusinessUtils.formataCpfCnpj(cabecalhoPedido.getEstabelecimento().getCpfcnpj())) +
		"                                          <br>" +
		"                                          Insc Municipal: " + (cabecalhoPedido.getEstabelecimento().getInscricaoEstadual() == null ? "" : cabecalhoPedido.getEstabelecimento().getInscricaoEstadual()) +
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

	/*private String preparaSessao5() { 
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

		//+this.preparaHTMLTitulosPedido() +

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
	}*/

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
		"                                       " + (cabecalhoPedido.getCofins() == null ? "0,00" : cabecalhoPedido.getCofins()) + 
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaTrue\">" +
		"                                    <td>" +
		"                                       CSLL Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoPedido.getCsll() == null ? "0,00" : cabecalhoPedido.getCsll()) +
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaFalse\">" +
		"                                    <td>" +
		"                                       INSS Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoPedido.getInss() == null ? "0,00" : cabecalhoPedido.getInss()) +
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaTrue\">" +
		"                                    <td>" +
		"                                       IR Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoPedido.getIr() == null ? "0,00" : cabecalhoPedido.getIr()) +
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaFalse\">" +
		"                                    <td>" +
		"                                       ISS Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoPedido.getIss() == null ? "0,00" : cabecalhoPedido.getIss()) +
		"                                    </td>" +
		"                                 </tr>" +
		"                                 <tr class=\"conteudoTabelaTrue\">" +
		"                                    <td>" +
		"                                       PIS Deb/Cred: " + 
		"                                    </td>" +
		"                                    <td style=\"padding-right: 2px; text-align: right;\">" +
		"                                       " + (cabecalhoPedido.getPis() == null ? "0,00" : cabecalhoPedido.getPis()) +
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
