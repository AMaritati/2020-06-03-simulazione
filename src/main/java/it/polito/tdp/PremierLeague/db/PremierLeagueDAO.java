package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void loadPlayers(Map<Integer, Player> idMap,double goal) {
		String sql = "SELECT a.PlayerID AS ID,p.Name AS N,AVG(Goals) " + 
				"FROM actions a,players p " + 
				"WHERE a.PlayerID=p.PlayerID " + 
				"GROUP BY a.playerID " + 
				"HAVING AVG(Goals)>?";
		
		Connection conn = DBConnect.getConnection();

		try {
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, goal);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				if(!idMap.containsKey(rs.getInt("ID"))) {
					Player p = new Player(rs.getInt("ID"), rs.getString("N"));
					idMap.put(p.getPlayerID(), p);
				}
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Adiacenza> getAd(Map<Integer, Player> idMap) {
		String sql = "SELECT a1.PlayerID AS p1,a2.PlayerID AS p2,SUM(a1.TimePlayed) AS T1," + 
				"SUM(a2.TimePlayed) AS T2 " + 
				"FROM actions a1,actions a2 " + 
				"WHERE a1.`Starts`=1 AND a2.`Starts`= 1 AND a1.TeamID!=a2.TeamID AND " + 
				"a1.PlayerID > a2.PlayerID AND a1.MatchID = a2.MatchID " + 
				"GROUP BY a1.PlayerID,a2.PlayerID " + 
				"HAVING (T1-T2) <> 0";
		
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();
		try {
			
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				Player sorgente = idMap.get(rs.getInt("p1"));
				Player destinazione = idMap.get(rs.getInt("p2"));
				
				if(sorgente != null && destinazione != null) {
					result.add(new Adiacenza(sorgente, destinazione, rs.getInt("T1"), rs.getInt("T2")));
				}

			}
			conn.close();
		}catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
		return result;
	}
}
