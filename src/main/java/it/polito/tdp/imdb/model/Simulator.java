package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulator {
	
	Graph<Actor,DefaultWeightedEdge> grafo;
	List<Actor> attoriRimanenti = new ArrayList<>();
	
	// Eventi
	PriorityQueue<Event> queue = new PriorityQueue<>();
	
	// Parametri di simulazione
	int n;
	String genere;
	String genereAttorePrecedente;
	
	// Stato del sistema
	List<Actor> attoriIntervistati;
	
	// Misure in uscita
	int giorniDiPausa;
	
	public void init(int n, String genere, Graph<Actor,DefaultWeightedEdge> grafo) {
		this.n=n;
		this.genere=genere;
		this.grafo=grafo;
		this.genereAttorePrecedente="";
		
		for(Actor a : this.grafo.vertexSet())
			this.attoriRimanenti.add(a);
	}
	
	public void run() {
		this.giorniDiPausa=0;
		this.attoriIntervistati = new ArrayList<>();
		
		// Eventi iniziali --> Scelgo un attore casualemente
		int random = (int)(Math.random()*this.grafo.vertexSet().size());
		Actor partenza = this.attoriRimanenti.get(random);
		this.queue.add(new Event(1,partenza));
		this.attoriRimanenti.remove(partenza);
		this.attoriIntervistati.add(partenza);
		
		// Ciclo degli eventi
		while(true) {
			Event e = this.queue.poll();
			if(e.getGiorno()<=this.n)
				processEvent(e);
			else
				break;
		}
		
	}

	private void processEvent(Event e) {
		if(e.getAttore()!=null && this.genereAttorePrecedente.equals(e.getAttore().gender)) {			
			// 90% di probabilità che si prenda una pausa il giorno dopo
			int prob = (int)(Math.random()*100);
			if(prob<=90) {
				this.queue.add(new Event(e.getGiorno()+1,null)); // Evento "Giorno di pausa"
				this.giorniDiPausa++;
				return;
			}
			// altrimenti procedi normalmente
		}
		if(e.getAttore()==null)
			this.genereAttorePrecedente="";
		else
			this.genereAttorePrecedente=e.getAttore().gender;

		int prob1 = (int)(Math.random()*100);
		
		if(prob1<=60 || e.getAttore()==null) {
			// Scelgo l'attore in modo casuale
			int random = (int)(Math.random()*this.attoriRimanenti.size());
			Actor prossimo = this.attoriRimanenti.get(random);
			this.queue.add(new Event(e.getGiorno()+1,prossimo));
			this.attoriRimanenti.remove(prossimo);
			this.attoriIntervistati.add(prossimo);
		}
		else {
			// Chiedo consiglio all'attore intervistato il giorno prima
			List<Actor> listaVicini = getProssimo(e.getAttore(),this.grafo);
			Actor prossimo = null;
			if(listaVicini.size()==1) {
				prossimo = listaVicini.get(0);
				this.queue.add(new Event(e.getGiorno()+1,prossimo));
				this.attoriRimanenti.remove(prossimo);
				this.attoriIntervistati.add(prossimo);
			}
			else if(listaVicini.size()>1) {
				// Estraggo casualmente il prossimo
				int random = (int)(Math.random()*listaVicini.size());
				prossimo = listaVicini.get(random);
				this.queue.add(new Event(e.getGiorno()+1,prossimo));
				this.attoriRimanenti.remove(prossimo);
				this.attoriIntervistati.add(prossimo);
			}
			else {
				// Scelgo in modo casuale
				int random = (int)(Math.random()*this.attoriRimanenti.size());
				prossimo = this.attoriRimanenti.get(random);
				this.queue.add(new Event(e.getGiorno()+1,prossimo));
				this.attoriRimanenti.remove(prossimo);
				this.attoriIntervistati.add(prossimo);
			}
		}
	}

	private List<Actor> getProssimo(Actor attore, Graph<Actor, DefaultWeightedEdge> grafo) {
		int max=0;
		List<Actor> result = new ArrayList<>();
		
		for(DefaultWeightedEdge arco : this.grafo.edgesOf(attore)) {
			int peso = (int)this.grafo.getEdgeWeight(arco);
			Actor a = Graphs.getOppositeVertex(this.grafo,arco,attore);
			if(this.attoriRimanenti.contains(a)) {
				if(peso>max) {
					max = peso;
					result = new ArrayList<>();
					result.add(a);
				}
				else if(peso==max) {
					result.add(a);
				}
			}
		}
		
		return result;
	}
	
	public int getGiorniDiPausa() {
		return this.giorniDiPausa;
	}
	
	public List<Actor> getAttoriIntervistati() {
		return this.attoriIntervistati;
	}

}
