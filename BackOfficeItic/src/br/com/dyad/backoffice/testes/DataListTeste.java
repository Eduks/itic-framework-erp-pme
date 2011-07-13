package br.com.dyad.backoffice.testes;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Observer;

import br.com.dyad.commons.data.DataList;

public class DataListTeste extends DataList {
	
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	ArrayList observers = new ArrayList();
		
	public DataListTeste() {
		//this.pcs.addPropertyChangeListener(listener);
	}
	
	public void addObserver(Observer observer) {
		this.observers.add(observer);
	}
}
