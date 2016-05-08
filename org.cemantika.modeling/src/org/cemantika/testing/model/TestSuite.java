package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;

public class TestSuite {
	private List<AbstractContext> testCases = new ArrayList<AbstractContext>();

	public void setTestCases(List<AbstractContext> testCases) {
		this.testCases = testCases;
	}

	public List<AbstractContext> getTestCases() {
		return testCases;
	}
}
