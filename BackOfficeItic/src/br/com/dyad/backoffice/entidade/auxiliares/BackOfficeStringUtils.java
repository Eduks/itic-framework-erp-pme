package br.com.dyad.backoffice.entidade.auxiliares;

import org.apache.commons.lang.StringUtils;

/**
 * @author Franklin Kaswiner
 *
 */
public class BackOfficeStringUtils {
	public boolean cpfValido(String cpf) {
		String cpfNumerico = somenteNumericos(cpf);
		
		return calculaDVCpf(cpfNumerico).compareTo(cpfNumerico.substring(9)) == 0; 		
	}
	
	public String calculaDVCpf(String cpf) {
		String cpfNumerico = somenteNumericos(cpf);
		String cpfValidar = cpfNumerico.substring(0, 9);
		
		if (cpfNumerico.length() != 11) {
			throw new Error("Cpf deve conter 11 dígitos.");
		}
		
		//Primeiro Digito
		int soma = 0;
		for (int i = 0; i < 9; i++) {
			soma += (10 - i) * cpfValidar.charAt(i);
		}
		int resto = soma % 11;
		int digito1 = (resto < 1 ? '0' : (11 - resto));
		
		//Segundo Digito
		cpfValidar += digito1;
		soma = 0;
		for (int i = 0; i < 10; i++) {
			soma += (11 - i) * cpfValidar.charAt(i);
		}
		resto = soma % 11;
		int digito2 = (resto < 2 ? '0' : (11 - resto));
		
		char digitos[] = {(char)digito1, (char)digito2};
		return new String( digitos );
	}
	
	public String calculaDVCnpj(String cnpj) {
		return new String();		
	}
	
	public String formataComoCpf(String expression) {
		String formataComoCpf = new String(); 
		
		return formataComoCpf;
	}

	public String formataComoCnpj(String expression) {
		String cnpjFormatado = null; 
		
		return cnpjFormatado;
	}

	public static String somenteAlfaNumericos(String expressao) {
		String caracteresPermitidos = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
		return somentePermitidos(expressao, caracteresPermitidos);
	}

	public static String somenteAlfa(String expressao) {
		String caracteresPermitidos = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
		return somentePermitidos(expressao, caracteresPermitidos);
	}

	public static String somenteNumericos(String expressao) {
		String caracteresPermitidos = "0123456789";
		
		return somentePermitidos(expressao, caracteresPermitidos);
	}

	private static String somentePermitidos(String expressao, String permitidos) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < expressao.length(); i++) {
			char caractere = expressao.charAt(i);
			
			if (StringUtils.contains(permitidos, caractere)) {
				stringBuilder.append(caractere);
			}
		}
		
		return stringBuilder.toString(); 
	}

}
