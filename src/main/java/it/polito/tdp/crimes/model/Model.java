package it.polito.tdp.crimes.model;

import java.util.*;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private EventsDao dao;
	private SimpleWeightedGraph<String, DefaultWeightedEdge> grafo;
	private Map<Integer, String> idMap;
	private int bestPeso;
	private List<String> bestPercorso;

	
		
	public Model(){
		dao=new EventsDao();
		idMap=new HashMap<Integer,String>();
		//dao.listAllMatches(idMap);
		
	}
	
	
	public void CreaGrafo(int mese, String categoria) {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//aggiungo vertici
		Graphs.addAllVertices(grafo,dao.getVertici(mese,categoria));
		//aggiungo archi
		for(Adiacenza a:dao.getArchi(mese, categoria)) { 
				Graphs.addEdge(grafo, a.getUno(), a.getDue(), a.getPeso());
			}
					
	}
	
	public Set<String> getVertici() {
		return this.grafo.vertexSet();
	}
	
	public String grafoRecap() {
		return "GRAFO CREATO" +"\n"+" N VERTICI: "+this.grafo.vertexSet().size()+"\n"+"N ARCHI: "+this.grafo.edgeSet().size()+"\n";
	}
	public List<String> getReati(){
		return dao.listaReati();
	}
	public List<Integer> getGG(){
		return dao.listaGiorni();
	}
	public List<Adiacenza> minori(int giorno, String categoria){
		List<Adiacenza> lista=new LinkedList<Adiacenza>(dao.getArchi(giorno, categoria));
		Collections.sort(lista);
		double d=(lista.get(0).getPeso() +lista.get(lista.size()-1).getPeso() )/2;
		List<Adiacenza> result=new LinkedList<Adiacenza>();
		for(Adiacenza a:lista) {
			if(a.getPeso()<d)
				result.add(a);
		}
		return result;
	}


	public List<String> percorso(String partenza, String destinazione) {
		this.bestPeso=0;
		this.bestPercorso=null;
		List<String> parziale=new ArrayList<>();
		parziale.add(partenza);
		cerca(parziale,0, destinazione);	
		return this.bestPercorso;
	}


	private void cerca(List<String> parziale, int peso, String destinazione) {
		//caso terminale
		if((peso>this.bestPeso || peso==0) && parziale.get(parziale.size()-1).equals(destinazione)) {
			this.bestPeso=peso;
			this.bestPercorso=new LinkedList<String>(parziale);
		}
		//genero percorso
		for(String s:Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			int pesoDaAggiungere=(int) this.grafo.getEdgeWeight(grafo.getEdge(parziale.get(parziale.size()-1),s));
			
			if(!parziale.contains(s)) {
				parziale.add(s);
				peso+=pesoDaAggiungere;
				cerca(parziale,peso,destinazione);
				peso-=pesoDaAggiungere;
				parziale.remove(s);
			}
		}
		
	}
	
	
	public int getBestPeso() {
		return bestPeso;
	}


	public void setBestPeso(int bestPeso) {
		this.bestPeso = bestPeso;
	}


	public List<String> getBestPercorso() {
		return bestPercorso;
	}


	public void setBestPercorso(List<String> bestPercorso) {
		this.bestPercorso = bestPercorso;
	}
	
	
}
