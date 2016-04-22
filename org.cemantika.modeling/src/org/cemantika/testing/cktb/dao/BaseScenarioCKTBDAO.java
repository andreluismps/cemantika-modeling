package org.cemantika.testing.cktb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cemantika.testing.cktb.db.DataBase;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;

public class BaseScenarioCKTBDAO {
	
	private String CKTBPath;
	
	public BaseScenarioCKTBDAO(String CKTBPath){
		this.CKTBPath = CKTBPath;
	}
	
	public BaseScenarioCKTBDAO(){
		throw new IllegalArgumentException("Please use BaseScenarioCKTBDAO(String CKTBPath) constructor");
	}
	
	public Map<String, Scenario> getAll() {
		
		TimeSlotCKTBDAO timeSlotDAO = new TimeSlotCKTBDAO(CKTBPath);

		Map<String, Scenario> situationCKTB = new HashMap<String, Scenario>();
		
		String logicalQuery = "SELECT * FROM scenario";
		
		Scenario scenario = null;
		ResultSet situationRs = DataBase.executeSelect(logicalQuery, DataBase.getConnection(CKTBPath));
		try {
			while (situationRs.next()) {
				scenario = new Scenario(situationRs.getString("name"));

				scenario.setIdentity(situationRs.getInt("id"));
				//scenario.setExpectedBehavior(situationRs.getString("expectedBehavior"));
				
				scenario.setContextList(timeSlotDAO.getByScenario(scenario));
				
				Collections.sort(scenario.getContextList());
				
				situationCKTB.put(scenario.getName(), scenario);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return situationCKTB;
	}
	
	public void Save(Map<String, Scenario> scenarios){
				
		Integer id = null;		
		List<String> commands = new ArrayList<String>();
		
		for (Entry<String, Scenario> scenarioEntry : scenarios.entrySet()) {
			Scenario scenario = scenarioEntry.getValue();
			String name = scenarioEntry.getKey();
			//String expectedBehavior = scenario.getExpectedBehavior();
			Collections.sort(scenario.getContextList());
			id = scenario.getIdentity();
			
			if (id == null){
			//	commands.add("INSERT INTO situation (name, expectedBehavior) values ('" + name + "', '"+ expectedBehavior + "')");
			//	generateSituationLogicalContextInserts("(select max(id) from situation)", commands, scenario);
			}
			else{
			//	commands.add("UPDATE situation SET name = '"  + name + "', expectedBehavior = '" + expectedBehavior + "' where id = " + id);
			//	commands.add("DELETE FROM situationLogicalContext WHERE idSituation = " + id);
			//	generateSituationLogicalContextInserts(""+id, commands, scenario);
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
