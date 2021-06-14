package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> listAllGenres(){
		String sql = "SELECT DISTINCT genre "
				+ "FROM movies_genres ";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(res.getString("genre"));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	public List<Actor> getVertices(Map<Integer,Actor> idMap, String genere) {
		String sql = "SELECT a.id AS id, a.first_name AS nome, a.last_name AS cognome, a.gender AS genere "
				+ "FROM actors a, roles r, movies_genres mg "
				+ "WHERE a.id=r.actor_id AND r.movie_id=mg.movie_id AND mg.genre=?";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1,genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getInt("id"))) {
					Actor actor = new Actor(res.getInt("id"), res.getString("nome"), res.getString("cognome"),
							res.getString("genere"));
					idMap.put(res.getInt("id"), actor);
					result.add(actor);
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getArchi(Map<Integer,Actor> idMap, String genere) {
		String sql = "SELECT r1.actor_id AS id1, r2.actor_id AS id2, COUNT(*) AS peso "
				+ "FROM roles r1, roles r2, movies_genres mg "
				+ "WHERE r1.actor_id>r2.actor_id AND mg.genre=? "
				+ "      AND r1.movie_id=mg.movie_id AND r1.movie_id=r2.movie_id "
				+ "GROUP BY id1,id2";
		
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1,genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Actor a1 = idMap.get(res.getInt("id1"));
				Actor a2 = idMap.get(res.getInt("id2"));
				
				if(a1!=null && a2!=null) {
					Adiacenza a = new Adiacenza(a1,a2,res.getInt("peso"));
					result.add(a);
				}

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
