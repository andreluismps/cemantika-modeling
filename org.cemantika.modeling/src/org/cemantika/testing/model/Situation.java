package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;

public class Situation {
	private List<LogicalContext> logicalContexts = new ArrayList<LogicalContext>();
	
	private String name;

	public void setLogicalContexts(List<LogicalContext> logicalContexts) {
		this.logicalContexts = logicalContexts;
	}

	public List<LogicalContext> getLogicalContexts() {
		return logicalContexts;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
