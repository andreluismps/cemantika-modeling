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
import org.cemantika.testing.model.TimeSlot;

public class TimeSlotCKTBDAO {
	
	private String CKTBPath;
	
	public TimeSlotCKTBDAO(String CKTBPath){
		this.CKTBPath = CKTBPath;
	}
	
	public TimeSlotCKTBDAO(){
		throw new IllegalArgumentException("Please use TimeSlotCKTBDAO(String CKTBPath) constructor");
	}
	
	public void Save(List<TimeSlot> timeSlots){
/*				
		Integer id = null;		
		List<String> commands = new ArrayList<String>();
		
		for (TimeSlot timeSlot : timeSlots) {
			//Situation situation = situationEntry.getValue();
			//String name = situationEntry.getKey();
			//String expectedBehavior = situation.getExpectedBehavior();
			//Collections.sort(situation.getContextList());
			//id = situation.getId();
			
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
				commands.add("INSERT INTO situationLogicalContext (idSituation, idLogical) values ("+ id +", " + logicalContext.getId() + ")");
			}
		}
		*/
	}
	public List<AbstractContext> getByScenario(Scenario scenario) {
		
		return null;
	}
}
