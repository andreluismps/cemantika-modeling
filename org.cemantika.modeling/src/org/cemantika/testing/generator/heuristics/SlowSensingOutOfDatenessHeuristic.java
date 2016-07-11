package org.cemantika.testing.generator.heuristics;

import java.util.ArrayList;
import java.util.List;

import org.cemantika.testing.cktb.dao.LogicalContextCKTBDAO;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.ContextDefectPattern;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.model.TimeSlot;
import org.cemantika.testing.util.CxGUtils;

import com.google.common.collect.Lists;

public class SlowSensingOutOfDatenessHeuristic implements SensorDefectPatternHeuristic {

	
	LogicalContextCKTBDAO logicalContextCKTBDAO;
	
	public SlowSensingOutOfDatenessHeuristic(String CKTBPath) {
		this.logicalContextCKTBDAO = new LogicalContextCKTBDAO(CKTBPath);
	}

	@Override
	public List<Scenario> deriveTestCases(Scenario baseScenario, PhysicalContext sensor, ContextDefectPattern contextDefectPattern) {
		
		List<Scenario> scenarios = new ArrayList<Scenario>();
		
		//generate all timeslots with defect
		List<TimeSlot> timeSlotsWithOutDateness = getTimesLotsWithOutDateness(baseScenario, sensor, contextDefectPattern);
		
		//derive using all outdated timeslots until use only first timeslot with outdated data
		scenarios.addAll(deriveScenariosWithOutdatedData(baseScenario, timeSlotsWithOutDateness, sensor));
		
		return scenarios;
	}
	
	private List<Scenario> deriveScenariosWithOutdatedData(Scenario baseScenario, List<TimeSlot> timeSlotsWithOutDateness, PhysicalContext sensor) {
		List<Scenario> derivedScenarios = new ArrayList<Scenario>();
		List<AbstractContext> reverseBaseScenarioTimeSlots = Lists.reverse(baseScenario.getContextList());
		Scenario derivedScenario = null;
		
		for (AbstractContext timeSlotAbs : reverseBaseScenarioTimeSlots){
			if (!(timeSlotAbs instanceof TimeSlot)) continue;
			
			TimeSlot timeSlot = (TimeSlot)timeSlotAbs;
			TimeSlot lastSlotWithOutDateness = TimeSlot.getById(timeSlotsWithOutDateness, timeSlot.getId());
			
			if (lastSlotWithOutDateness == null) continue;
			int derivedScenarioIndex = 0;
			derivedScenario = Scenario.newInstance(baseScenario);
			derivedScenario.setName(baseScenario.getName() + " - " + sensor.getName() + " Outdated until time " + timeSlot.getId());
			
			for(AbstractContext baseTimeSlotAbs : baseScenario.getContextList()){
				
				if (!(baseTimeSlotAbs instanceof TimeSlot)) {derivedScenarioIndex++; continue;}
				
				TimeSlot baseTimeSlot = (TimeSlot)baseTimeSlotAbs;
				if (baseTimeSlot.getId() <= lastSlotWithOutDateness.getId()){
					TimeSlot timeSlotToReplace = TimeSlot.getById(timeSlotsWithOutDateness, baseTimeSlot.getId());
					if(timeSlotToReplace == null) continue;
					
					derivedScenario.getContextList().set(derivedScenarioIndex, timeSlotToReplace);
				}
				if (baseTimeSlot.getId() == lastSlotWithOutDateness.getId()){
					derivedScenarios.add(derivedScenario);
					break;
				}
				derivedScenarioIndex++;
			}
			
		}
		
		return derivedScenarios;
	}

	private List<TimeSlot> getTimesLotsWithOutDateness(Scenario baseScenario, PhysicalContext sensor, ContextDefectPattern contextDefectPattern) {
		
		List<TimeSlot> timeSlotsWithOutDateness = new ArrayList<TimeSlot>();
		for (AbstractContext timeslot : baseScenario.getContextList()){
			Situation situation = (Situation) timeslot.getContextList().get(0);
			for (AbstractContext logicalContextAbs : situation.getContextList()){
				LogicalContext logicalContext= (LogicalContext) logicalContextAbs;
				for (AbstractContext physicalContextAbs : logicalContextAbs.getContextList()){
					PhysicalContext physicalContext = (PhysicalContext) physicalContextAbs;
					if (sensor.getName().equals(physicalContext.getName()) && physicalContext.getContextDefectPatterns().contains(ContextDefectPattern.SLOW_SENSING_OUT_OF_DATENESS))
						timeSlotsWithOutDateness.add(createSlowSensingOutOfDatenessTimeSlot(baseScenario, timeslot, situation, logicalContext, physicalContext, contextDefectPattern));
				}
			}
		
		}
		return timeSlotsWithOutDateness;
	}

	private TimeSlot createSlowSensingOutOfDatenessTimeSlot(Scenario baseScenario, AbstractContext timeslot, Situation situation, LogicalContext logicalContext, PhysicalContext physicalContext, ContextDefectPattern contextDefectPattern) {
		TimeSlot outDatedTimeSlot = TimeSlot.newInstance((TimeSlot)timeslot);
		
		Situation outDatedSituation = (Situation) outDatedTimeSlot.getContextList().get(0);
		outDatedSituation.setName(outDatedSituation.getName() + " with " + physicalContext.getName() + " Outdated data");
		outDatedSituation.getContextList().remove(logicalContext);
		
		LogicalContext outDatedLogicalContext = logicalContextCKTBDAO.getByName(CxGUtils.getNameOfLogicalContextWithDefectPattern(physicalContext, contextDefectPattern, logicalContext));
		
		outDatedSituation.getContextList().add(outDatedLogicalContext);
		return outDatedTimeSlot;
	}
}
