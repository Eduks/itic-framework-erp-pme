package br.com.dyad.backoffice.testes;

public @interface PlanoContaValidator {
	
	String message() default "{br.com.dyad.testes.PlanoContaValidator}";
	
	Class<?>[] groups()  default { };
	

}
