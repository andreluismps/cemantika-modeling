package org.cemantika.testing.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.Scenario;

public class TestSuiteReduction {

	private static double getJaccardIndex(Scenario scenarioA, Scenario scenarioB) {

		List<LogicalContext> transitionsA = scenarioA.getTransitions();
		List<LogicalContext> transitionsB = scenarioB.getTransitions();
		
		Set<LogicalContext> union = new HashSet<LogicalContext>(transitionsA);
		Set<LogicalContext> intersection = new HashSet<LogicalContext>(transitionsA);

		union.addAll(transitionsB);
		
		intersection.retainAll(transitionsB);
		
		return intersection.size() / union.size();

	}

	private static double[][] getSimilarityMatrix(List<Scenario> scenarios){
		double[][] similarityMatrix = new double[scenarios.size()][scenarios.size()];
		
		for (int i = 0 ; i < scenarios.size() ; i++)
			for (int j = 0 ; j < scenarios.size() ; j++){
				if (i >= j) continue;
				similarityMatrix[i][j] = getJaccardIndex(scenarios.get(i), scenarios.get(j));
			}
		
		return similarityMatrix;
	}
	
	public static List<Scenario> reducedTestSuite(List<Scenario> scenarios){
		Set<Scenario> reducedTestSuite = new HashSet<Scenario>();
		double[][] similarityMatrix = getSimilarityMatrix(scenarios);
		double maxValue = 1;
		
		while(maxValue != 0){
			int[] pair = getMaxValue(similarityMatrix);
			maxValue = similarityMatrix[pair[0]] [pair[1]];
			
			if (maxValue == 0) break;
			
			similarityMatrix[pair[0]] [pair[1]] = 0;
			
			Scenario scenario1 = scenarios.get(pair[0]);
			Scenario scenario2 = scenarios.get(pair[1]);
			Scenario firstChoice = null, secondChoice = null;
			
			if(scenario1.getTransitions().size() < scenario2.getTransitions().size()){
				firstChoice = scenario2;
				secondChoice = scenario1;
			}else if (scenario1.getTransitions().size() > scenario2.getTransitions().size()){
				firstChoice = scenario1;
				secondChoice = scenario2;
			}else{
				Random random = new Random();
				int firstChoiceIndex = random.nextInt(2);
				firstChoice = (firstChoiceIndex == 0)? scenario1 : scenario2;
				secondChoice = (firstChoiceIndex == 0)? scenario2 : scenario1;
			}
			
			int removedTestCaseIndex = -1;
			if(firstChoice == scenario1)
				removedTestCaseIndex = pair[0];
			else{
				removedTestCaseIndex = pair[1];
			}
			
			for(int i = 0 ; i < scenarios.size() ; i++){
				similarityMatrix[removedTestCaseIndex][i] = 0;
				similarityMatrix[i][removedTestCaseIndex] = 0;
			}
			
			reducedTestSuite.add(secondChoice); 
		}
		
		return new ArrayList<Scenario>(reducedTestSuite);
	}

	private static int[] getMaxValue(double[][] similarityMatrix) {
		int[] position = new int[3];
		double maxValue = 0;
		for (int i = 0 ; i < similarityMatrix[0].length ; i++)
			for (int j = 0 ; j < similarityMatrix[0].length ; j++){
				if (i >= j) continue;
				if(similarityMatrix[i][j] >= maxValue){
					position[0] = i;
					position[1] = j;
					maxValue = similarityMatrix[i][j];
				}
			}
		return position;
	}

}
