package org.cemantika.testing.cktb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DataBase {
	
	private static int iTimeout = 30;
	
	public static void createDb(Connection conn){
		List<String> tablesDDL = new ArrayList<String>();
		tablesDDL.add("CREATE TABLE scenario (id INTEGER PRIMARY KEY AUTOINCREMENT, name text, CxGFullName text)");
		
		tablesDDL.add("CREATE TABLE situation (id INTEGER PRIMARY KEY AUTOINCREMENT, name text, expectedBehavior text)");
		tablesDDL.add("CREATE TABLE logicalContext (id INTEGER PRIMARY KEY AUTOINCREMENT, name text, jsonValue text)");
		//tablesDDL.add("CREATE TABLE physicalContext (id numeric, jsonValues text)");
		//tablesDDL.add("CREATE TABLE logicalPhysicalContext (idLogical numeric, idPhysical numeric)");
		//tablesDDL.add("CREATE TABLE scenarioSituationContext (idScenario numeric, idSituation numeric)");
		tablesDDL.add("CREATE TABLE situationLogicalContext (idSituation numeric, idLogical numeric)");
		tablesDDL.add("CREATE TABLE timeSlot (idScenario numeric, idSituation numeric, timeStamp numeric)");
		
		DataBase.executeUpdate(tablesDDL, conn);
		
	}	
		
	public static Connection getConnection(String file){
		String sDriverName = "org.sqlite.JDBC";
        try {
			Class.forName(sDriverName);
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}
 
        String sJdbc = "jdbc:sqlite";
        String sDbUrl = sJdbc + ":" + file;
        Connection conn = null;

        try {
			conn = DriverManager.getConnection(sDbUrl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
		
	public static void executeUpdate(List<String> commands, Connection conn){
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.setQueryTimeout(iTimeout);
			for (String command : commands) {
				stmt.executeUpdate(command);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			 try { stmt.close(); conn.close();} catch (Exception e) {e.printStackTrace();}
		}
	}


}
