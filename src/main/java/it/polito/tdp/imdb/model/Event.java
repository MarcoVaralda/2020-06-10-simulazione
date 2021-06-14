package it.polito.tdp.imdb.model;

public class Event implements Comparable<Event> {
	
	private int giorno;
	private Actor attore; // Attore scelto
	
	public Event(int giorno, Actor attore) {
		this.giorno = giorno;
		this.attore = attore;
	}

	public int getGiorno() {
		return giorno;
	}

	public void setGiorno(int giorno) {
		this.giorno = giorno;
	}

	public Actor getAttore() {
		return attore;
	}

	public void setAttore(Actor attore) {
		this.attore = attore;
	}

	@Override
	public int compareTo(Event altro) {
		return this.giorno-altro.giorno;
	}
	

}
