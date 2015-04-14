package org.cemantika.testing.model;

import java.util.List;

public class TestCase {
	/**
	 * Name of test Scenario.
	 */
	private String name;
	
	/**
	 * List of steps in test case.
	 */
	private List<TestCaseStep> steps;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TestCaseStep> getSteps() {
		return steps;
	}

	public void setSteps(List<TestCaseStep> steps) {
		this.steps = steps;
	}
}
