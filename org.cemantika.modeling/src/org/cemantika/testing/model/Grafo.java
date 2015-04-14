package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Grafo {
	private Map<String, LinkedHashSet<String>> map = new HashMap<String, LinkedHashSet<String>>();

    public void adicionaAresta(String node1, String node2) {
        LinkedHashSet<String> adjacente = map.get(node1);
        if(adjacente==null) {
            adjacente = new LinkedHashSet<String>();
            map.put(node1, adjacente);
        }
        adjacente.add(node2);
    }


    public LinkedList<String> nosAdjacentes(String last) {
        LinkedHashSet<String> adjacente = map.get(last);
        if(adjacente==null) {
            return new LinkedList<String>();
        }
        return new LinkedList<String>(adjacente);
    }
	
	public List<ArrayList<String>> listarCaminhos(Grafo grafo, String noInicial, String noFinal) {
		ArrayList<String> visitado = new ArrayList<String>();
    	visitado.add(noInicial);
    	
    	ArrayList<ArrayList<String>> caminhos = new ArrayList<ArrayList<String>>();
    	
		listarCaminhos(grafo, visitado, caminhos, noInicial, noFinal);
		return caminhos;
	}
	
	private void listarCaminhos(Grafo grafo, List<String> visitado, List<ArrayList<String>> caminhos, String noAtual, String noFinal) {        
	    
		if (noAtual.equals(noFinal)) { 
	        caminhos.add(new ArrayList(Arrays.asList(visitado.toArray())));
	        return;
	    }
	    else {
	        LinkedList<String> nodes = grafo.nosAdjacentes(noAtual);    
	        for (String node : nodes) {
	            if (visitado.contains(node)) {
	                continue;
	            } 
	            List<String> temp = new ArrayList<String>();
	            temp.addAll(visitado);
	            temp.add(node);          
	            listarCaminhos(grafo, temp, caminhos, node, noFinal);
	        }
	    }
	}
	
    
}
