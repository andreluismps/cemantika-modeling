package org.cemantika.testing.cktb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
		throw new IllegalArgumentException("Please use LogicalContextCKTBDAO(String CKTBPath) constructor");
	}
	
	public Map<String, Situation> getAll() {
		
		LogicalContextCKTBDAO logicalDAO = new LogicalContextCKTBDAO(CKTBPath);

		Map<String, Situation> situationCKTB = new HashMap<String, Situation>();
		
		String logicalQuery = "SELECT * FROM situation";
		
		Situation situation = null;
		ResultSet situationRs = DataBase.executeSelect(logicalQuery, DataBase.getConnection(CKTBPath));
		try {
			while (situationRs.next()) {
				situation = new Situation(situationRs.getString("name"));

				situation.setId(situationRs.getInt("id"));
				situation.setExpectedBehavior(situationRs.getString("expectedBehavior"));
				
				situation.setContextList(logicalDAO.getBySituation(situation));
				
				situationCKTB.put(situation.getName(), situation);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
			int i = 1;
			id = situation.getId();
			
			if (id == null){
				commands.add("INSERT INTO situation (name, expectedBehavior) values ('" + name + "', '"+ expectedBehavior + "')");
				generateSituationLogicalContextInserts("last_insert_rowid()", commands, situation);
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
				commands.add("INSERT INTO situationLogicalContext (idSituation, idLogical) values ("+ id +", " + logicalContext.getId() + ")");
			}
		}
	}
}
