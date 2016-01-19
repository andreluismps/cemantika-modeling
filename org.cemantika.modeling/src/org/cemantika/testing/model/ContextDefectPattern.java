package org.cemantika.testing.model;

public enum ContextDefectPattern {
	INCOMPLETE_UNAIVALABALITY("Incomplete / Unaivalability"),
	INCOMPLETE_NOT_INTERPRETABLE("Incomplete / Not Interpretable"), 
	INCOMPLETE_AMBIGUITY("Incomplete / Ambiguity"), 
	
	INCONSISTENCY_NOT_INTERPRETABLE("Inconsistency / Not Interpretable"),
	
	SENSOR_NOISE_INCORRECTNESS("Sensor Noise / Incorrectness"),
	SENSOR_NOISE_FALSE_READING("Sensor Noise / False Reading"),
	SENSOR_NOISE_INSTABILITY("Sensor Noise / Instability"),
	SENSOR_NOISE_UNRELIABILITY("Sensor Noise / Unreability"),
	
	SLOW_SENSING_OUT_OF_DATENESS("Slow Sensing / Out-of-Dateness"),
	SLOW_SENSING_WRONG_INTERPRETATION("Slow Sensing / Wrong Interpretation"),
	
	GLANULARITY_MISMATCH_IMPRECISION("Granularity Mismatch / Imprecision"),
	
	PROBLEMATIC_RULE_LOGIC_NOT_INTERPRETABLE("Problematic Rule Logic / Not Interpretable"),
	PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR(""),
	
	OVERLAPPING_SENSORS_CONCURRENT_VALUES("Overlapping Sensors / Concurrent Values"),
	OVERLAPPING_SENSORS_UNPREDICTABLE("Overlapping Sensors / Unpredictable");
	
	private final String text;

	private ContextDefectPattern(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
	
}
