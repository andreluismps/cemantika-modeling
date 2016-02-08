package org.cemantika.testing.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cemantika.testing.cxg.xsd.ActionNode;
import org.cemantika.testing.cxg.xsd.Connection;
import org.cemantika.testing.cxg.xsd.Constraint;
import org.cemantika.testing.cxg.xsd.Constraints;
import org.cemantika.testing.cxg.xsd.Process;
import org.cemantika.testing.cxg.xsd.Split;
import org.cemantika.testing.model.CxG;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.model.TimeSlot;
import org.cemantika.testing.util.CxGUtils;
import org.eclipse.core.resources.IFile;

public class TestCaseGenerator {
	
	private Map<String, LogicalContext> logicalContexts;
	
	public TestCaseGenerator(Map<String, LogicalContext> logicalContexts) {
		this.logicalContexts = logicalContexts;
	}

	public List<Scenario> testCaseGeneration(IFile conceptualModel, IFile contextualGraph){
		
		List<Scenario> testCases = new ArrayList<Scenario>();
		
		Process extractexCxG = null;
		CxG internalCxG = new CxG();
		List<ArrayList<String>> caminhos = CxGUtils.getPathsFromCxG(contextualGraph, conceptualModel, extractexCxG, internalCxG);
		
		List<Split> splits = internalCxG.getContextualNodes();
		
		Scenario baseTestCase = generateBaseTestCase(extractexCxG, internalCxG, caminhos, splits);
		
		testCases.add(baseTestCase);
		
		return testCases;
	}

	private Scenario generateBaseTestCase(Process extractexCxG,
			CxG internalCxG, List<ArrayList<String>> caminhos,
			List<Split> splits) {
		Scenario scenario = new Scenario("Base Test Case");
        
        List<Situation> situations = getSituations(splits, caminhos, extractexCxG, internalCxG);
        
        int i = 0;
        for (Situation situation : situations) {
			TimeSlot timeSlot = new TimeSlot(i++);
			timeSlot.addChildContext(situation);
			scenario.addChildContext(timeSlot);
			
		}
		return scenario;
	}
	
	private List<Situation> getSituations(List<Split> splits, List<ArrayList<String>> caminhos, Process extractexCxG, CxG internalCxG) {
		List<Situation> situations = new ArrayList<Situation>();
		int i = 0;
        for(ArrayList<String> path : caminhos){
        	Situation situation = getSituation(splits, path, extractexCxG, internalCxG);
        	situation.setName("Situation #" + ++i);
        	situations.add(situation);
	    }
		return situations;
	}
	
	private Situation getSituation(List<Split> splits, ArrayList<String> path, Process extractexCxG, CxG internalCxG) {
		int i = 0, pos = 0;
		Situation situation = new Situation("");
		for (String node : path) {
		    if (pos != 0){
		    	for (Split split : splits) {
		    		if (split.getId().equals(path.get(pos))){
						for (Connection conn : internalCxG.getConnections().getConnection()) {
							if (conn.getTo().equals(node)){
								LogicalContext logicalContext = getLogicalContext(split, conn.getTo(), internalCxG);
								situation.addChildContext(logicalContext);
							}
							
						}
					}
				}
		    	pos = 0;
		    }
		    for (Split noContextual : splits) {
				if (noContextual.getId().equals(node)){
					pos = i;
					break;
				}
			}
		    i++;
		    ActionNode actionNode = internalCxG.getActionById(node);
            if (actionNode != null)
            	situation.getExpectedActions().add(actionNode.getName());
		}
		return situation;
	}

	private LogicalContext getLogicalContext(Split split, String toNodeId, CxG internalCxG) {
		
		for (Object o : split.getMetaDataOrConstraints()) {
			if(!(o instanceof Constraints)){
				continue;
			}
			Constraints constraints = (Constraints) o;
			for (Constraint constraint : constraints.getConstraint()) {
				if(constraint.getToNodeId().equals(toNodeId)){
					return logicalContexts.get(constraint.getName());					
				}
			}
		}
		return null;
	}
}
