package it.polito.tdp.imdb.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	ImdbDAO dao ;
	Map<Integer,Actor> idMap;
	List<Actor> vertici;
	Graph<Actor,DefaultWeightedEdge> grafo;
	Map<Actor,Actor> predecessore;
	
	Simulator s ;
	
	public Model() {
		this.dao = new ImdbDAO();
		this.idMap = new HashMap<>();
	}
	
	public List<String> listAllGenres() {
		return this.dao.listAllGenres();
	}
	
	public void creGrafo(String genere) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.vertici = this.dao.getVertices(idMap,genere);
		Collections.sort(vertici);
		
		// Aggiungo i vertici
		Graphs.addAllVertices(this.grafo,idMap.values());
		
		// Aggiungo gli archi
		for(Adiacenza a : this.dao.getArchi(idMap, genere))
			if(!this.grafo.containsEdge(a.getA1(),a.getA2()))
				Graphs.addEdge(this.grafo,a.getA1(),a.getA2(),a.getPeso());
		
	}
	
	public String getNumeroVertici() {
		return "Numero vertici: "+this.grafo.vertexSet().size()+"\n";
	}
	
	public String getNumeroArchi() {
		return "Numero archi: "+this.grafo.edgeSet().size()+"\n";
	}
	
	public List<Actor> getVertici() {
		return this.vertici;
	}
	
	public List<Actor> attoriRaggiungibli(Actor partenza){
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<Actor, DefaultWeightedEdge>(this.grafo);
		List<Actor> actors = new LinkedList<>(ci.connectedSetOf(partenza));
		actors.remove(partenza);
		Collections.sort(actors);
		return actors;
	}
	
	
	// Simulazione
	public String simula(int n, String genere) {
		this.s = new Simulator();
		
		this.s.init(n, genere, grafo);
		this.s.run();
		
		List<Actor> intervistati = this.s.getAttoriIntervistati();
		int giorniDiPausa = this.s.getGiorniDiPausa();
		
		String result = "";
		for(Actor a : intervistati) 
			result += a +"\n";
		result += "Con "+giorniDiPausa +" giorni di pausa";
		
		return result;
	}

}
