package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LogicalContext {
	private List<String> sensors = new ArrayList<String>();
	
	private String name;
	
	public LogicalContext(){
		
	}

	public LogicalContext(String name, List<String> sensors){
		this.name = name;
		this.sensors = sensors;
	}
	@XmlElement(name="sensor")
	public List<String> getSensors() {
		return sensors;
	}

	public void setSensors(List<String> sensors) {
		this.sensors = sensors;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}
	
	
}
