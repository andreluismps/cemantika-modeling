package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
	private List<Situation> situations = new ArrayList<Situation>();
	
	private String name;

	public List<Situation> getSituations() {
		return situations;
	}

	public void setSituations(List<Situation> situations) {
		this.situations = situations;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	

}
