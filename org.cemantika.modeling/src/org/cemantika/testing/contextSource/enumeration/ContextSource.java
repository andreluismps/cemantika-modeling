package org.cemantika.testing.contextSource.enumeration;

import org.cemantika.testing.util.Constants;

public enum ContextSource {
	ACCELEROMETER(Constants.ACCELEROMETER),   
    
    THERMOMETER(Constants.THERMOMETER), 
    
    BAROMETER(Constants.BAROMETER),    
    
    LIGHTSENSOR(Constants.LIGHTSENSOR),  
    
    MAGNETOMETER(Constants.MAGNETOMETER),
    
    GYROSCOPE(Constants.GYROSCOPE),
    
    TIMEDATE(Constants.TIMEDATE),
    
    CALENDAR(Constants.CALENDAR),
    
    GPS(Constants.GPS),
    
    BATTERY(Constants.BATTERY),
    
    RAM(Constants.RAM),
    
    DISK(Constants.DISK),
    
    CPU(Constants.CPU),
    
    SDCARD(Constants.SDCARD),
    
    USBCABLE(Constants.SDCARD),
    
    MICROPHONE(Constants.MICROPHONE),
    
    CAMERA(Constants.CAMERA),
    
    RFID(Constants.RFID),
    
    QRCODE(Constants.QRCODE),
    
    BLUETOOTH(Constants.BLUETOOTH),
    
    INFRARED(Constants.INFRARED),
    
    HTTP(Constants.HTTP),
    
    WIFI(Constants.WIFI),
    
    NETWORK(Constants.NETWORK),
    
    CELLID(Constants.CELLID);
    
    private final String text;

	private ContextSource(final String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	public static ContextSource fromString(String text) {
		if (text == null) return null; 
		
		for (ContextSource cs : ContextSource.values()) {
			if (text.equalsIgnoreCase(cs.text)) {
				return cs;
			}
		}
		return null;
	}
}
