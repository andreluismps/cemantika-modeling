package org.cemantika.testing.generator.heuristics;

import java.util.ArrayList;
import java.util.List;

import org.cemantika.testing.cktb.dao.LogicalContextCKTBDAO;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.ContextDefectPattern;
import org.cemantika.testing.model.ContextSourceDefectPattern;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.model.TimeSlot;

public class GranularityMismatchImprecisionHeuristic implements SensorDefectPatternHeuristic {

	LogicalContextCKTBDAO logicalContextCKTBDAO;
	
	public GranularityMismatchImprecisionHeuristic (String CKTBPath){
		this.logicalContextCKTBDAO = new LogicalContextCKTBDAO(CKTBPath);
	}
	
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
						scenarios.add(deriveGranularityMismatchImprecisionScenario(baseScenario, timeslot, situation, logicalContext, physicalContext));
				}
			}
		}
		
		return scenarios;
	}
	
	private Scenario deriveGranularityMismatchImprecisionScenario(
			Scenario baseScenario, AbstractContext timeslot,
			Situation situation, LogicalContext logicalContext,
			PhysicalContext physicalContext) {
		
		
		ContextSourceDefectPattern contextSourceDefectPattern = new ContextSourceDefectPattern(physicalContext.getName(), ContextDefectPattern.GLANULARITY_MISMATCH_IMPRECISION);
		
		Scenario derivedScenario = Scenario.newInstance(baseScenario);
		derivedScenario.setName(baseScenario.getName() + ": Defect at Time " +(((TimeSlot)timeslot).getId() +1) + " - " + contextSourceDefectPattern);//+ " on " + situation.getName());
		
		TimeSlot newTimeSlot = null;
		int index = 0;
		for (AbstractContext timeslotAbs : derivedScenario.getContextList()){
			TimeSlot ts = (TimeSlot)timeslotAbs;
			if (!ts.equals(timeslot)) {
				if (newTimeSlot != null){
					ts.setId(ts.getId() + 1);
				}else{
					index++;
				}
				continue;
			}
			
			newTimeSlot = getModifiedTimeSlot(ts, situation, logicalContext, contextSourceDefectPattern);
			index++;
		}
		
		derivedScenario.getContextList().add(index, newTimeSlot);
		
		return derivedScenario;
	}
	
	private LogicalContext findLogicalContextWithDefectInCKTB(AbstractContext originalLogicalContext, ContextSourceDefectPattern contextSourceDefectPattern){
		String name = originalLogicalContext.getName()  + " - " + contextSourceDefectPattern.getContextSourceName() + " - " + contextSourceDefectPattern.getContextDefectPattern();
		return logicalContextCKTBDAO.getByName(name);
	}
	
	private TimeSlot getModifiedTimeSlot(TimeSlot timeSlot, Situation situation, LogicalContext logicalContext, ContextSourceDefectPattern contextSourceDefectPattern){
		LogicalContext logicalContextToAdd = findLogicalContextWithDefectInCKTB(logicalContext, contextSourceDefectPattern);
		
		TimeSlot newTimeSlot = TimeSlot.newInstance(timeSlot);
		newTimeSlot.setId(newTimeSlot.getId()+1);
		
		Situation timeSlotSituation = (Situation)newTimeSlot.getContextList().get(0);
		timeSlotSituation.setName(situation.getName() + " with " + contextSourceDefectPattern);
		timeSlotSituation.getContextList().remove(logicalContext);
		timeSlotSituation.getContextList().add(logicalContextToAdd);
		
		return newTimeSlot;
	}

}
