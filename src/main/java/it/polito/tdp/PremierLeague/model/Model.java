package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	private Graph<Player, DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;
	
	public Model() {
		idMap = new HashMap<Integer,Player>();
	}
	
	public void creaGrafo(double goalFatti) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		PremierLeagueDAO dao = new PremierLeagueDAO();
		dao.loadPlayers(idMap,goalFatti);
		
		//Aggiungere i vertici
				Graphs.addAllVertices(this.grafo, idMap.values());
				
		// Aggiungere gli archi
				for (Adiacenza a : dao.getAd(idMap)) {
					if(a.getT1()-a.getT2()>0) {
						Graphs.addEdge(this.grafo, a.getP1(), a.getP2(), a.getT1()-a.getT2());
					}
					else {
						Graphs.addEdge(this.grafo, a.getP2(), a.getP1(), a.getT2()-a.getT1());
					}
				}
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public Player topPlayer() {
		Integer x = Integer.MIN_VALUE;
		Player topPlayer = null;
		for (Player p : this.grafo.vertexSet()) {
			if (this.grafo.outDegreeOf(p)>x) {
				x = this.grafo.outDegreeOf(p);
				topPlayer = p;
			}
		}		
		return topPlayer;
		
	}
	
	public List<Avversario> avversari(){
		List<Avversario> lista = new ArrayList<>();
		
		for (DefaultWeightedEdge e : this.grafo.edgesOf(this.topPlayer())) {
			if(!this.grafo.getEdgeTarget(e).equals(this.topPlayer()))
		lista.add(new Avversario(this.grafo.getEdgeTarget(e),(int)this.grafo.getEdgeWeight(e)));
		}
		Collections.sort(lista);
		return lista;
	}
	public static void main(String args[]) {
		Model m = new Model();
		m.creaGrafo(0.5);
		System.out.println(m.topPlayer());
		
		System.out.println(m.avversari());
		
	}

}


