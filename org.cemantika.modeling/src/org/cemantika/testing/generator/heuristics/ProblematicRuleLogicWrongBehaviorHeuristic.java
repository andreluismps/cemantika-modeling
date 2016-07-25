package org.cemantika.testing.generator.heuristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cemantika.testing.contextSource.Battery;
import org.cemantika.testing.contextSource.CPU;
import org.cemantika.testing.contextSource.HardDisk;
import org.cemantika.testing.contextSource.RAM;
import org.cemantika.testing.contextSource.SDCard;
import org.cemantika.testing.contextSource.USBCable;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.ContextDefectPattern;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.model.TimeSlot;

public class ProblematicRuleLogicWrongBehaviorHeuristic implements
		SensorDefectPatternHeuristic {

	@Override
	public List<Scenario> deriveTestCases(Scenario baseScenario, PhysicalContext sensor, ContextDefectPattern contextDefectPattern) {
		return deriveTestCases(baseScenario, contextDefectPattern);
	}
	
	public List<Scenario> deriveTestCases(Scenario baseScenario, ContextDefectPattern contextDefectPattern) {
		
		List<Scenario> scenarios = new ArrayList<Scenario>();
		
		//generate all timeslots with defect
		List<TimeSlot> timeSlotsWithProblematicRuleLogicWrongBehavior = getTimesLotsWithProblematicRuleLogicWrongBehavior(baseScenario, contextDefectPattern);
		
		
		if (timeSlotsWithProblematicRuleLogicWrongBehavior.size() > 0)
			scenarios.addAll(deriveScenariosWithWrongBehavior(baseScenario, timeSlotsWithProblematicRuleLogicWrongBehavior, contextDefectPattern));
		
		return scenarios;
	}
	
	private List<TimeSlot> getTimesLotsWithProblematicRuleLogicWrongBehavior(Scenario baseScenario, ContextDefectPattern contextDefectPattern) {
		List<TimeSlot> timeSlotsWithProblematicRuleLogicWrongBehavior = new ArrayList<TimeSlot>();
		
		LogicalContext logicalContextWithDefect = getLogicalContext(contextDefectPattern);
		
		if (logicalContextWithDefect.getContextList().size() == 0) return timeSlotsWithProblematicRuleLogicWrongBehavior;
		
		for (AbstractContext timeslot : baseScenario.getContextList()){
			timeSlotsWithProblematicRuleLogicWrongBehavior.add(createProblematicRuleLogicWrongBehaviorTimeSlot(timeslot, logicalContextWithDefect));
		}
		
		return timeSlotsWithProblematicRuleLogicWrongBehavior;
	}

	private TimeSlot createProblematicRuleLogicWrongBehaviorTimeSlot(AbstractContext timeslot, LogicalContext logicalContextWithDefect) {
		TimeSlot problematicRuleLogicWrongBehaviorTimeSlot = TimeSlot.newInstance((TimeSlot)timeslot);
		
		Situation problematicRuleLogicWrongBehaviorSituation = (Situation) problematicRuleLogicWrongBehaviorTimeSlot.getContextList().get(0);
		problematicRuleLogicWrongBehaviorSituation.setName(problematicRuleLogicWrongBehaviorSituation.getName() + " with " + logicalContextWithDefect.getName());
		problematicRuleLogicWrongBehaviorSituation.addChildContext(logicalContextWithDefect);
		
		return problematicRuleLogicWrongBehaviorTimeSlot;
		
	}

	private Collection<Scenario> deriveScenariosWithWrongBehavior(Scenario baseScenario, List<TimeSlot> timeSlotsWithProblematicRuleLogicWrongBehavior, ContextDefectPattern contextDefectPattern) {
		List<Scenario> derivedScenarios = new ArrayList<Scenario>();
		Scenario derivedScenario = null;
		
		for(AbstractContext timeSlotAbs : baseScenario.getContextList()){
			if (!(timeSlotAbs instanceof TimeSlot)) continue;
			
			TimeSlot timeSlot = (TimeSlot)timeSlotAbs;
			
			derivedScenario = Scenario.newInstance(baseScenario);
			derivedScenario.setName(baseScenario.getName() + " - " + contextDefectPattern.toString() + " after time " + timeSlot.getId());
			
			for(AbstractContext baseTimeSlotAbs : baseScenario.getContextList()){
				if (!(baseTimeSlotAbs instanceof TimeSlot)) continue;
				
				TimeSlot baseTimeSlot = (TimeSlot)baseTimeSlotAbs;
				
				if(baseTimeSlot.getId() != timeSlot.getId()) continue;
				
				TimeSlot timeSlotToReplace = TimeSlot.getById(timeSlotsWithProblematicRuleLogicWrongBehavior, baseTimeSlot.getId());
				
				if(timeSlotToReplace == null) continue;
				
				derivedScenario.getContextList().set(baseTimeSlot.getId(), timeSlotToReplace);
				
				break;
				
			}
			derivedScenarios.add(derivedScenario);
			
		}
		return derivedScenarios;
	}

	private LogicalContext getLogicalContext(ContextDefectPattern contextDefectPattern){
		LogicalContext logicalContext = new LogicalContext(contextDefectPattern.toString());
		AbstractContext physicalContext = null;
		switch (contextDefectPattern) {
		case PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_LOW_RAM:
			physicalContext = new RAM();
			((RAM)physicalContext).setValue1(0);			
			break;
		case PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_LOW_DISK:
			physicalContext = new HardDisk();
			((HardDisk)physicalContext).setValue1(0);
			break;
		case PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_100_PERCENT_CPU:
			physicalContext = new CPU();
			((CPU)physicalContext).setValue1(100);
			break;
		case PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_PLUGGED_SDCARD:
			physicalContext = new SDCard();
			((SDCard)physicalContext).setValue1(true);
			break;
		case PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_UNPLUGGED_SDCARD:
			physicalContext = new SDCard();
			((SDCard)physicalContext).setValue1(false);
			break;
		case PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_PLUGGED_USB_CABLE:
			physicalContext = new USBCable();
			((USBCable)physicalContext).setValue1(true);
			break;
		case PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_UNPLUGGED_USB_CABLE:
			physicalContext = new USBCable();
			((USBCable)physicalContext).setValue1(false);
			break;
		case PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_15_PERCENT_BATTERY:
			physicalContext = new Battery();
			((Battery)physicalContext).setValue1(15);
			break;
		case PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_5_PERCENT_BATTERY:
			physicalContext = new Battery();
			((Battery)physicalContext).setValue1(5);
			break;
		case PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_1_PERCENT_BATTERY:
			physicalContext = new Battery();
			((Battery)physicalContext).setValue1(1);
			break;
		}
		
		logicalContext.addChildContext(physicalContext);
		
		return logicalContext;
	}

}
