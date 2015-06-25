package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Situation {
	private List<LogicalContext> logicalContexts = new ArrayList<LogicalContext>();
	
	private String name;
	
	public Situation(){
		
	}

	public void setLogicalContexts(List<LogicalContext> logicalContexts) {
		this.logicalContexts = logicalContexts;
	}

	@XmlElement(name="logicalContext")
	public List<LogicalContext> getLogicalContexts() {
		return logicalContexts;
	}

	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute
	public String getName() {
		return name;
	}
}
