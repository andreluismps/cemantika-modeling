package org.cemantika.testing.generator.heuristics;

import java.util.List;

import org.cemantika.testing.model.ContextDefectPattern;
import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.model.Scenario;

public class ProblematicRuleLogicWrongBehaviorHeuristic implements
		SensorDefectPatternHeuristic {

	@Override
	public List<Scenario> deriveTestCases(Scenario baseScenario, PhysicalContext sensor, ContextDefectPattern contextDefectPattern) {
		return null;
	}

}
