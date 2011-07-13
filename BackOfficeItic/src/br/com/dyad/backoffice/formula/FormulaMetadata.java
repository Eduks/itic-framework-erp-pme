package br.com.dyad.backoffice.formula;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface FormulaMetadata {
	
	public long codigo();
	
	public String nome();
	
}
