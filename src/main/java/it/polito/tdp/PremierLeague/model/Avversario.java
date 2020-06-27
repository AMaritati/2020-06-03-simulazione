package it.polito.tdp.PremierLeague.model;

public class Avversario implements Comparable<Avversario>  {
	private Player p;
	private Integer peso;
	public Avversario(Player p, Integer peso) {
		super();
		this.p = p;
		this.peso = peso;
	}
	public Player getP() {
		return p;
	}
	public void setP(Player p) {
		this.p = p;
	}
	public Integer getPeso() {
		return peso;
	}
	public void setPeso(Integer peso) {
		this.peso = peso;
	}
	@Override
	public int compareTo(Avversario o) {
		
		return -(this.peso.compareTo(o.getPeso()));
	}
	@Override
	public String toString() {
		return p.getPlayerID()+" "+ p.getName()+" "+peso;
	}
	
	

}
