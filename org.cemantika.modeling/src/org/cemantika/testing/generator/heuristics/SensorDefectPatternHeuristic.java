package org.cemantika.testing.generator.heuristics;

import java.util.List;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.model.Scenario;

public interface SensorDefectPatternHeuristic {
	List<Scenario> deriveTestCases(Scenario baseScenario, PhysicalContext sensor);
}
