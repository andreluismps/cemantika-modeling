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
	//PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR("Problematic Rule Logic / Wrong Behavior"),
	PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_LOW_RAM("Problematic Rule Logic / Wrong Behavior - Low RAM"),
	PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_LOW_DISK("Problematic Rule Logic / Wrong Behavior - Low DISK"),
	PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_100_PERCENT_CPU("Problematic Rule Logic / Wrong Behavior - 100% CPU"),
	PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_PLUGGED_SDCARD("Problematic Rule Logic / Wrong Behavior - Plugged SD-Card"),
	PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_UNPLUGGED_SDCARD("Problematic Rule Logic / Wrong Behavior - Unplugged SD-Card"),
	PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_PLUGGED_USB_CABLE("Problematic Rule Logic / Wrong Behavior - Plugged USB Cable"),
	PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_UNPLUGGED_USB_CABLE("Problematic Rule Logic / Wrong Behavior - Unplugged USB Cable"),
	PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_15_PERCENT_BATTERY("Problematic Rule Logic / Wrong Behavior - 15% Battery"),
	PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_5_PERCENT_BATTERY("Problematic Rule Logic / Wrong Behavior - 5% Battery"),
	
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
	
	/**
	 * True if user must fill data for these logical contexts
	 * @param contextDefectPattern
	 * @return
	 */
	
	public static boolean deriveLogicalContext(ContextDefectPattern contextDefectPattern){
		switch (contextDefectPattern) {
		case GLANULARITY_MISMATCH_IMPRECISION:
		case SENSOR_NOISE_INCORRECTNESS:
		case SENSOR_NOISE_INSTABILITY:
		case SENSOR_NOISE_UNRELIABILITY:
		case SLOW_SENSING_OUT_OF_DATENESS:
		case OVERLAPPING_SENSORS_CONCURRENT_VALUES:
			return true;

		}
		return false;
	}
	
	public static ContextDefectPattern fromString(String text) {
		if (text == null) return null; 
		
		for (ContextDefectPattern cdp : ContextDefectPattern.values()) {
			if (text.equalsIgnoreCase(cdp.text)) {
				return cdp;
			}
		}
		return null;
	}
}
