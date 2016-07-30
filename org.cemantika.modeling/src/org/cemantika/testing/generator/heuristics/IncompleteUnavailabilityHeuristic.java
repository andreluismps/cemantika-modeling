package org.cemantika.testing.generator.heuristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cemantika.testing.generator.TestSuiteReduction;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.ContextDefectPattern;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.model.TimeSlot;

public class IncompleteUnavailabilityHeuristic implements SensorDefectPatternHeuristic {

	@Override
	public List<Scenario> deriveTestCases(Scenario baseScenario, PhysicalContext sensor, ContextDefectPattern contextDefectPattern) {
		
		List<Scenario> scenarios = new ArrayList<Scenario>();
		
		//generate all timeslots with defect
		List<TimeSlot> timeSlotsWithIncompleteUnavailable = getTimesLotsWithIncompleteUnavailable(baseScenario, sensor, contextDefectPattern);
		
		
		//derive using always first available and others disabled for sensor
		if (timeSlotsWithIncompleteUnavailable.size() > 0)
			scenarios.addAll(deriveScenariosWithtimeSlotsWithIncompleteUnavailable(baseScenario, timeSlotsWithIncompleteUnavailable, sensor));
		
		return TestSuiteReduction.reducedTestSuite(scenarios);
	}

	private Collection<? extends Scenario> deriveScenariosWithtimeSlotsWithIncompleteUnavailable(Scenario baseScenario, List<TimeSlot> timeSlotsWithIncompleteUnavailable, PhysicalContext sensor) {
		List<Scenario> derivedScenarios = new ArrayList<Scenario>();
		Scenario derivedScenario = null;

		for(AbstractContext timeSlotAbs : baseScenario.getContextList()){
			if (!(timeSlotAbs instanceof TimeSlot)) continue;
			
			TimeSlot timeSlot = (TimeSlot)timeSlotAbs;
			
			if(timeSlot.getId() == baseScenario.getContextList().size() - 1) break;
			
			TimeSlot firstSlotWithIncompleteUnavailable = TimeSlot.getById(timeSlotsWithIncompleteUnavailable, timeSlot.getId());
			if (firstSlotWithIncompleteUnavailable == null) continue;
			
			derivedScenario = Scenario.newInstance(baseScenario);
			derivedScenario.setName(baseScenario.getName() + " - " + sensor.getName() + " available until time " + timeSlot.getId());
			
			for(AbstractContext baseTimeSlotAbs : baseScenario.getContextList()){
				if (!(baseTimeSlotAbs instanceof TimeSlot)) continue;
				
				TimeSlot baseTimeSlot = (TimeSlot)baseTimeSlotAbs;
				
				if(baseTimeSlot.getId() <= timeSlot.getId()) continue;
				
				TimeSlot timeSlotToReplace = TimeSlot.getById(timeSlotsWithIncompleteUnavailable, baseTimeSlot.getId());
				
				if(timeSlotToReplace == null) continue;
				
				derivedScenario.getContextList().set(baseTimeSlot.getId(), timeSlotToReplace);
				
			}
			derivedScenarios.add(derivedScenario);
			
		}
			
		return derivedScenarios;
	}

	private List<TimeSlot> getTimesLotsWithIncompleteUnavailable(Scenario baseScenario, PhysicalContext sensor, ContextDefectPattern contextDefectPattern) {
		List<TimeSlot> timeSlotsWithIncompleteUnavailable = new ArrayList<TimeSlot>();
		for (AbstractContext timeslot : baseScenario.getContextList()){
			Situation situation = (Situation) timeslot.getContextList().get(0);
			for (AbstractContext logicalContextAbs : situation.getContextList()){
				LogicalContext logicalContext= (LogicalContext) logicalContextAbs;
				for (AbstractContext physicalContextAbs : logicalContextAbs.getContextList()){
					PhysicalContext physicalContext = (PhysicalContext) physicalContextAbs;
					if (sensor.getName().equals(physicalContext.getName()) && physicalContext.getContextDefectPatterns().contains(ContextDefectPattern.INCOMPLETE_UNAIVALABALITY))
						timeSlotsWithIncompleteUnavailable.add(createSlowSensingIncompleteUnavailableTimeSlot(baseScenario, timeslot, situation, logicalContext, physicalContext, contextDefectPattern));
				}
			}
		
		}
		
		timeSlotsWithIncompleteUnavailable.remove(0);
		
		return timeSlotsWithIncompleteUnavailable;
	}

	private TimeSlot createSlowSensingIncompleteUnavailableTimeSlot(Scenario baseScenario, AbstractContext timeslot, Situation situation, LogicalContext logicalContext, PhysicalContext physicalContext, ContextDefectPattern contextDefectPattern) {
		TimeSlot incompleteUnavailableTimeSlot = TimeSlot.newInstance((TimeSlot)timeslot);
		
		Situation incompleteUnavailableSituation = (Situation) incompleteUnavailableTimeSlot.getContextList().get(0);
		incompleteUnavailableSituation.setName(incompleteUnavailableSituation.getName() + " without " + physicalContext.getName() + " data");
		incompleteUnavailableSituation.getContextList().remove(logicalContext);
		
		
		LogicalContext incompleteUnavailableLogicalContext = LogicalContext.newInstance(logicalContext);
		incompleteUnavailableLogicalContext.setName(logicalContext.getName() + " without " + physicalContext.getName() + " data");
		incompleteUnavailableLogicalContext.getContextList().remove(physicalContext);
		
		if(incompleteUnavailableLogicalContext.getContextList().size() > 0)
			incompleteUnavailableSituation.getContextList().add(incompleteUnavailableLogicalContext);
		return incompleteUnavailableTimeSlot;
		
	}

	

}
