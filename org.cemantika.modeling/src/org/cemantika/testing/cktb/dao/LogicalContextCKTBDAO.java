package org.cemantika.testing.cktb.dao;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cemantika.testing.cktb.db.DataBase;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.util.GsonUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LogicalContextCKTBDAO {
	
	private String CKTBPath;
	
	public LogicalContextCKTBDAO(String CKTBPath){
		this.CKTBPath = CKTBPath;
	}
	
	public LogicalContextCKTBDAO(){
		throw new IllegalArgumentException("Please use LogicalContextCKTBDAO(String CKTBPath) constructor");
	}
	
	public Map<String, LogicalContext> getAll() {
		Connection conn = DataBase.getConnection(CKTBPath);
		Statement stmt = null;
		
		Map<String, LogicalContext> logicalCKTB = new HashMap<String, LogicalContext>();
		
		String logicalQuery = "SELECT * FROM logicalContext";
		
		LogicalContext logicalContext = null;
		
		Type type = new TypeToken<LogicalContext>() {}.getType();
		Gson gson = GsonUtils.getGson();
		try {
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(logicalQuery);
			
			while (resultSet.next()) {
				logicalContext = gson.fromJson(resultSet.getString("jsonValue"), type);
				logicalContext.setIdentity(resultSet.getInt("id"));
				logicalCKTB.put(logicalContext.getName(), logicalContext);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try { stmt.close(); conn.close();} catch (Exception e) {e.printStackTrace();}
		}
		
		return logicalCKTB;
	}
	
	public List<AbstractContext> getBySituation(AbstractContext situation){
		
		String logicalQuery = "SELECT l.* "
							+ "  FROM logicalContext l " 
							+ " INNER JOIN situationLogicalContext sl ON l.id = sl.idLogical "
							+ " WHERE sl.idSituation = " + situation.getIdentity();
		
		return getByLogicalQuery(logicalQuery);
	}
	
	public List<AbstractContext> getByScenario(AbstractContext scenario){
		
		String logicalQuery = "SELECT distinct l.*" 
							+ "  FROM logicalContext l"
							+ " INNER JOIN situationLogicalContext sl ON l.id = sl.idLogical" 
							+ " INNER JOIN timeSlot ts ON sl.idSituation = ts.idSituation"
							+ " WHERE ts.idScenario = " + scenario.getIdentity();
		
		return getByLogicalQuery(logicalQuery);
	}

	private List<AbstractContext> getByLogicalQuery(String logicalQuery) {
		List<AbstractContext> logicalContexts = new ArrayList<AbstractContext>();
		Connection conn = DataBase.getConnection(CKTBPath);
		Statement stmt = null;
		LogicalContext logicalContext = null;
		
		Type type = new TypeToken<LogicalContext>() {}.getType();
		Gson gson = GsonUtils.getGson();
		try {
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(logicalQuery);
			
			while (resultSet.next()) {
				logicalContext = gson.fromJson(resultSet.getString("jsonValue"), type);
				logicalContext.setIdentity(resultSet.getInt("id"));
				logicalContexts.add(logicalContext);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try { stmt.close(); conn.close();} catch (Exception e) {e.printStackTrace();}
		}
		return logicalContexts;
	}
	
	public void Save(Map<String, LogicalContext> logicalContexts){
		Gson gson = GsonUtils.getGson();
				
		Integer id = null;		
		java.util.List<String> commands = new ArrayList<String>();
		
		for (Entry<String, LogicalContext> logicalContextEntry : logicalContexts.entrySet()) {
			String name = logicalContextEntry.getKey();
			String jsonValue = gson.toJson(logicalContextEntry.getValue());
			id = logicalContextEntry.getValue().getIdentity();
			if (id == null)
				commands.add("INSERT INTO logicalContext (name, jsonValue) values ('" + name + "', '"+ jsonValue + "')");
			else
				commands.add("UPDATE logicalContext SET name = '"  + name + "', jsonValue = '" + jsonValue + "' where id = " + id);
		}
		
		DataBase.executeUpdate(commands, DataBase.getConnection(CKTBPath));
	}
}
