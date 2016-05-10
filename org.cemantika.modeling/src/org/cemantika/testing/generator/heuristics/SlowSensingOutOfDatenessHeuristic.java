package org.cemantika.testing.generator.heuristics;

import java.util.ArrayList;
import java.util.List;

import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.ContextDefectPattern;
import org.cemantika.testing.model.ContextSourceDefectPattern;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.model.TimeSlot;

public class SlowSensingOutOfDatenessHeuristic implements SensorDefectPatternHeuristic {

	@Override
	public List<Scenario> deriveTestCases(Scenario baseScenario, PhysicalContext sensor, ContextDefectPattern contextDefectPattern) {
		
		List<Scenario> scenarios = new ArrayList<Scenario>();
		
		//for each situation/sensor, generate a new scenario
		for (AbstractContext timeslot : baseScenario.getContextList()){
			Situation situation = (Situation) timeslot.getContextList().get(0);
			for (AbstractContext logicalContextAbs : situation.getContextList()){
				LogicalContext logicalContext= (LogicalContext) logicalContextAbs;
				for (AbstractContext physicalContextAbs : logicalContextAbs.getContextList()){
					PhysicalContext physicalContext = (PhysicalContext) physicalContextAbs;
					if (sensor.getName().equals(physicalContext.getName()) && physicalContext.getContextDefectPatterns().contains(ContextDefectPattern.GLANULARITY_MISMATCH_IMPRECISION))
						scenarios.add(deriveIncompleteUnavailabilityScenario(baseScenario, timeslot, situation, logicalContext, physicalContext));
				}
			}
		}
		
		return scenarios;
	}
	
	private Scenario deriveIncompleteUnavailabilityScenario(
			Scenario baseScenario, AbstractContext timeslot,
			Situation situation, LogicalContext logicalContext,
			PhysicalContext physicalContext) {
		
		ContextSourceDefectPattern contextSourceDefectPattern = new ContextSourceDefectPattern(physicalContext.getName(), ContextDefectPattern.SLOW_SENSING_OUT_OF_DATENESS);
		
		Scenario derivedScenario = Scenario.newInstance(baseScenario);
		derivedScenario.setName(baseScenario.getName() + ": Defect after Time " +(((TimeSlot)timeslot).getId() +1) + " - " + contextSourceDefectPattern);
		
		TimeSlot outDatedTimeSlot = TimeSlot.newInstance((TimeSlot)timeslot);
		outDatedTimeSlot.setId(derivedScenario.getContextList().size());
		
		Situation outDatedSituation = (Situation) outDatedTimeSlot.getContextList().get(0);
		outDatedSituation.setName("Delayed " + physicalContext.getName() + " data of " + outDatedSituation.getName());
		outDatedSituation.getContextList().clear();
		
		LogicalContext outDatedLogicalContext = LogicalContext.newInstance(logicalContext);
		outDatedLogicalContext.setName("Delayed " + physicalContext.getName() + " data of " + outDatedLogicalContext.getName());
		outDatedLogicalContext.getContextList().clear();
		outDatedLogicalContext.getContextList().add(physicalContext);
		
		outDatedSituation.getContextList().add(outDatedLogicalContext);
		
		for (AbstractContext timeSlotAbs : derivedScenario.getContextList()){

			if (((TimeSlot)timeSlotAbs).getId() <= ((TimeSlot)timeslot).getId()) continue;

			Situation situationAux = (Situation) timeSlotAbs.getContextList().get(0);
			situationAux.setName(situationAux.getName() + " - No " + physicalContext.getName() + " data" );
			for (AbstractContext logicalContextAbs : situationAux.getContextList()){
				LogicalContext logicalContextAux = (LogicalContext) logicalContextAbs;
				logicalContextAux.getContextList().remove(physicalContext);
			}
		}
		derivedScenario.getContextList().add(outDatedTimeSlot);
		
		return derivedScenario;
	}
	
}
