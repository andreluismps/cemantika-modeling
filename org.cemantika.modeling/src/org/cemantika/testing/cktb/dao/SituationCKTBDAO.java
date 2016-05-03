package org.cemantika.testing.cktb.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cemantika.testing.cktb.db.DataBase;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.Situation;

public class SituationCKTBDAO {
	
	private String CKTBPath;
	
	public SituationCKTBDAO(String CKTBPath){
		this.CKTBPath = CKTBPath;
	}
	
	public SituationCKTBDAO(){
		throw new IllegalArgumentException("Please use SituationCKTBDAO(String CKTBPath) constructor");
	}
	
	public Situation getByIdentity(int identity){
		Connection conn = DataBase.getConnection(CKTBPath);
		Statement stmt = null;
		
		LogicalContextCKTBDAO logicalDAO = new LogicalContextCKTBDAO(CKTBPath);

		String situationQuery = "SELECT * FROM situation WHERE id = " + identity;
		
		Situation situation = null;
		
		try {
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(situationQuery);
			
			if (resultSet.next()) {
				situation = new Situation(resultSet.getString("name"));

				situation.setIdentity(resultSet.getInt("id"));
				situation.setExpectedBehavior(resultSet.getString("expectedBehavior"));
				
				situation.setContextList(logicalDAO.getBySituation(situation));
				
				Collections.sort(situation.getContextList());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try { stmt.close(); conn.close();} catch (Exception e) {e.printStackTrace();}
		}
		
		return situation;
	}
	
	public Map<String, Situation> getAll() {
		
		Connection conn = DataBase.getConnection(CKTBPath);
		Statement stmt = null;
		
		LogicalContextCKTBDAO logicalDAO = new LogicalContextCKTBDAO(CKTBPath);

		Map<String, Situation> situationCKTB = new HashMap<String, Situation>();
		
		String situationQuery = "SELECT * FROM situation";
		
		Situation situation = null;
		
		try {
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(situationQuery);
			
			while (resultSet.next()) {
				situation = new Situation(resultSet.getString("name"));

				situation.setIdentity(resultSet.getInt("id"));
				situation.setExpectedBehavior(resultSet.getString("expectedBehavior"));
				
				situation.setContextList(logicalDAO.getBySituation(situation));
				
				Collections.sort(situation.getContextList());
				
				situationCKTB.put(situation.getName(), situation);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try { stmt.close(); conn.close();} catch (Exception e) {e.printStackTrace();}
		}
		
		return situationCKTB;
	}
	
	public void Save(Map<String, Situation> situations){
				
		Integer id = null;		
		List<String> commands = new ArrayList<String>();
		
		for (Entry<String, Situation> situationEntry : situations.entrySet()) {
			Situation situation = situationEntry.getValue();
			String name = situationEntry.getKey();
			String expectedBehavior = situation.getExpectedBehavior();
			Collections.sort(situation.getContextList());
			id = situation.getIdentity();
			
			if (id == null){
				commands.add("INSERT INTO situation (name, expectedBehavior) values ('" + name + "', '"+ expectedBehavior + "')");
				generateSituationLogicalContextInserts("(select max(id) from situation)", commands, situation);
			}
			else{
				commands.add("UPDATE situation SET name = '"  + name + "', expectedBehavior = '" + expectedBehavior + "' where id = " + id);
				commands.add("DELETE FROM situationLogicalContext WHERE idSituation = " + id);
				generateSituationLogicalContextInserts(""+id, commands, situation);
			}
		}
		
		DataBase.executeUpdate(commands, DataBase.getConnection(CKTBPath));
	}

	private void generateSituationLogicalContextInserts(String id, List<String> commands, Situation situation) {
		for (AbstractContext abstractContext : situation.getContextList()) {
			if (abstractContext instanceof LogicalContext){
				LogicalContext logicalContext = (LogicalContext) abstractContext;
				commands.add("INSERT INTO situationLogicalContext (idSituation, idLogical) values ("+ id +", " + logicalContext.getIdentity() + ")");
			}
		}
	}
}
