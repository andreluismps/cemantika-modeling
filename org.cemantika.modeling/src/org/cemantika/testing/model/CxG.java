package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;

import org.cemantika.testing.cxg.xsd.Connections;
import org.cemantika.testing.cxg.xsd.End;
import org.cemantika.testing.cxg.xsd.Split;
import org.cemantika.testing.cxg.xsd.Start;
import org.cemantika.testing.cxg.xsd.Variables;

public class CxG {
	private Variables variables;
	private Start start;
	private End end;
	private List<Split> contextualNodes = new ArrayList<Split>();
	private Connections connections;

	public void setStart(Start start) {
		this.start = start;
	}

	public Start getStart() {
		return start;
	}

	public void setEnd(End end) {
		this.end = end;
	}

	public End getEnd() {
		return end;
	}

	public List<Split> getContextualNodes() {
		return contextualNodes;
	}

	public void setVariables(Variables variables) {
		this.variables = variables;
	}

	public Variables getVariables() {
		return variables;
	}

	public void setConnections(Connections connections) {
		this.connections = connections;
	}

	public Connections getConnections() {
		return connections;
	}
}
