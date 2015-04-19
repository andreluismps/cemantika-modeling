package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;

public class LogicalContext {
	private List<String> sensors = new ArrayList<String>();
	
	private String name;

	public LogicalContext(String name, List<String> sensors){
		this.name = name;
		this.sensors = sensors;
	}
	
	public List<String> getSensors() {
		return sensors;
	}

	public void setSensors(List<String> sensors) {
		this.sensors = sensors;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
