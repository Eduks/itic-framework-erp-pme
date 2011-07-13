package br.com.dyad.backoffice.formula;

import java.util.ArrayList;
import java.util.Iterator;

import br.com.dyad.commons.reflect.ReflectUtil;
import br.com.dyad.infrastructure.customization.ClasspathIterator;
import br.com.dyad.infrastructure.widget.WidgetListener;

public class FormulaUtil {

	public static ArrayList<ArrayList<String>> getClasses(final Class parentClass){

		final ArrayList<ArrayList<String>> lista = new ArrayList<ArrayList<String>>(0); 

		ClasspathIterator classpath = new ClasspathIterator();		
		classpath.addListener(new WidgetListener(){

			@Override
			public void handleEvent(Object sender) throws Exception {

				String senderString = (String)sender;

				if (!senderString.startsWith("br.com.dyad.client")) {

					Class classe = Class.forName(senderString);
					boolean pertence = false;

					if (parentClass == null) {
						pertence = ReflectUtil.inheritsFrom(classe, Formula.class);
					} else {
						pertence = ReflectUtil.inheritsFrom(classe, parentClass);
					}

					if (pertence) {

						FormulaMetadata formula = (FormulaMetadata)classe.getAnnotation(FormulaMetadata.class);

						if (formula != null) {
							ArrayList<String> listaAux = new ArrayList<String>(0);

							listaAux.add(formula.codigo()+"");
							listaAux.add(separaNomeClasse(formula.nome()));

							lista.add(listaAux);
						}

					}

				}

			}

		});

		classpath.listClasses();

		return lista;
	}

	public static Formula getFormula(final String codigo, final Class parentClass){

		if (codigo == null) {
			return null;
		} else {

			final ArrayList<Formula> lista = new ArrayList<Formula>(0); 

			ClasspathIterator classpath = new ClasspathIterator();		
			classpath.addListener(new WidgetListener(){

				@Override
				public void handleEvent(Object sender) throws Exception {

					String senderString = (String)sender;

					if (!senderString.startsWith("br.com.dyad.client")) {

						Class classe = Class.forName(senderString);
						boolean pertence = false;

						if (parentClass == null) {
							pertence = ReflectUtil.inheritsFrom(classe, Formula.class);
						} else {
							pertence = ReflectUtil.inheritsFrom(classe, parentClass);
						}

						if (pertence) {

							if (classe.isAnnotationPresent(FormulaMetadata.class)) {
								FormulaMetadata formula = (FormulaMetadata)classe.getAnnotation(FormulaMetadata.class);

								if (formula.codigo() == Long.parseLong(codigo)) {
									lista.add((Formula)ReflectUtil.getClassInstance(classe, null));
								}
							}
	//
	//						if (formula != null) {
	//							ArrayList<String> listaAux = new ArrayList<String>(0);
	//
	//							listaAux.add(formula.codigo()+"");
	//							listaAux.add(separaNomeClasse(formula.nome()));
	//
	//							lista.add(listaAux);
	//						}

						}

					}

				}

			});

			classpath.listClasses();

			return lista.isEmpty() ? null : lista.get(0);
		}
	}

	public static String separaNomeClasse(String classe){

		if (classe.equals(classe.toUpperCase())) {
			return classe;
		} else {

			StringBuilder nomeSeparado = new StringBuilder();
			nomeSeparado.append(classe.charAt(0));

			for (int i = 1; i < classe.length(); i++) {

				if (classe.substring(i, i+1).equals(classe.substring(i, i+1).toUpperCase())) {
					if (classe.charAt(i-1) != ' ') {
						nomeSeparado.append(" ");
					}
				}

				nomeSeparado.append(classe.charAt(i));
			}

			return nomeSeparado.toString();
		}
	}

}
