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
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.model.TimeSlot;

public class ScenarioCKTBDAO {
	
	private String CKTBPath;
	
	public ScenarioCKTBDAO(String CKTBPath){
		this.CKTBPath = CKTBPath;
	}
	
	public ScenarioCKTBDAO(){
		throw new IllegalArgumentException("Please use BaseScenarioCKTBDAO(String CKTBPath) constructor");
	}
	
	public void Save(Map<String, Scenario> scenarios){
		Integer id = null;
		List<String> commands = new ArrayList<String>();
		
		for (Entry<String, Scenario> scenarioEntry : scenarios.entrySet()) {
			Scenario scenario = scenarioEntry.getValue();
			String name = scenarioEntry.getKey();
			String CxGFullName = scenario.getCxGFullName();
			id = scenario.getIdentity();
			
			if (id == null){
				commands.add("INSERT INTO scenario (name, CxGFullName) VALUES ('" + name + "', '"+ CxGFullName + "')");
				generateTimeSlotInserts("(SELECT max(id) FROM scenario)", commands, scenario);
			}
			else{
				commands.add("UPDATE scenario SET name = '"  + name + "', CxGFullName = '" + CxGFullName + "' WHERE id = " + id);
				commands.add("DELETE FROM timeSlot WHERE idScenario = " + id);
				generateTimeSlotInserts(""+id, commands, scenario);
			}
		}
		
		DataBase.executeUpdate(commands, DataBase.getConnection(CKTBPath));
	}

	private void generateTimeSlotInserts(String id, List<String> commands, Scenario scenario) {
		for (AbstractContext abstractContext : scenario.getContextList()) {
			if (abstractContext instanceof TimeSlot && abstractContext.getContextList().get(0) instanceof Situation){
				TimeSlot timeSlot = (TimeSlot) abstractContext;
				AbstractContext situation = abstractContext.getContextList().get(0);
				commands.add("INSERT INTO timeSlot (idScenario, idSituation, timeStamp) VALUES (" + id + ", " + situation.getIdentity() + ", " + timeSlot.getId() + ")");
			}
		}
	}

	public Map<String, Scenario> getByCxG(String cxGFullName) {
		
		Connection conn = DataBase.getConnection(CKTBPath);
		Statement stmt = null;
		
		TimeSlotCKTBDAO timeSlotDAO = new TimeSlotCKTBDAO(CKTBPath);

		Map<String, Scenario> scenarioCKTB = new HashMap<String, Scenario>();
		
		String scenarioQuery = "SELECT * FROM scenario WHERE CxGFullName = '" + cxGFullName + "'";

		Scenario scenario = null;
		try {
			stmt = conn.createStatement();;
			ResultSet resultSet = stmt.executeQuery(scenarioQuery);
			
			while (resultSet.next()) {
				scenario = new Scenario(resultSet.getString("name"));

				scenario.setIdentity(resultSet.getInt("id"));
				
				scenario.setCxGFullName(resultSet.getString("CxGFullName"));
				
				scenario.setContextList(timeSlotDAO.getByScenario(scenario));
				
				Collections.sort(scenario.getContextList());
				
				scenarioCKTB.put(scenario.getName(), scenario);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try { stmt.close(); conn.close();} catch (Exception e) {e.printStackTrace();}
		}
		
		return scenarioCKTB;
	}
}
