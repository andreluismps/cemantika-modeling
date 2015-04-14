package org.cemantika.testing.model;

import java.util.List;

public class TestCaseStep {
	/**
	 * Name of step
	 */
	private String name;
	/**
	 * Time of step. Given in seconds 
	 */
	private Integer time;
	
	/**
	 * List of sensors used in test case step
	 */
	private List<String> sensors;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public List<String> getSensors() {
		return sensors;
	}

	public void setSensors(List<String> sensors) {
		this.sensors = sensors;
	}
	
}
