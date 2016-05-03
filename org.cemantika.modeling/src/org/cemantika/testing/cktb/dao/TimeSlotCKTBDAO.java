package org.cemantika.testing.cktb.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cemantika.testing.cktb.db.DataBase;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.TimeSlot;

public class TimeSlotCKTBDAO {
	
	private String CKTBPath;
	
	public TimeSlotCKTBDAO(String CKTBPath){
		this.CKTBPath = CKTBPath;
	}
	
	public TimeSlotCKTBDAO(){
		throw new IllegalArgumentException("Please use TimeSlotCKTBDAO(String CKTBPath) constructor");
	}
	
	public List<AbstractContext> getByScenario(Scenario scenario) {
		
		Connection conn = DataBase.getConnection(CKTBPath);
		Statement stmt = null;
		
		SituationCKTBDAO situationCKTBDAO = new SituationCKTBDAO(CKTBPath);
		
		List<AbstractContext> timeSlots = new ArrayList<AbstractContext>();
		
		String scenarioQuery = "SELECT * FROM timeSlot WHERE idScenario = " + scenario.getIdentity() + " ORDER BY timeStamp";
		
		TimeSlot timeSlot = null;
		
		try {
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(scenarioQuery);
			
			while (resultSet.next()) {
				timeSlot = new TimeSlot(resultSet.getInt("timeStamp"));
				
				timeSlot.getContextList().add(situationCKTBDAO.getByIdentity(resultSet.getInt("idSituation")));
				
				Collections.sort(timeSlot.getContextList());
				
				timeSlots.add(timeSlot);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try { stmt.close(); conn.close();} catch (Exception e) {e.printStackTrace();}
		}
		
		return timeSlots;
	}
}
