/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.util;

import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;

import org.cemantika.testing.model.Situation;

/**
 *
 * @author MHL
 */
public class Constants {
    
    private static Constants constants = new Constants();
    
    private Constants(){}
    
    public static Constants getInstance(){
        return constants;
    }
    
    private URL url;
    
    public ImageIcon getImageIcon(String icon_path){
       url = Situation.class.getClassLoader().getResource(icon_path);
     //  new ImageIcon(getClass().getResource("icons/x_neu.jpg"));
       return new ImageIcon(url);
       
       //return new ImageIcon("C://Users//MHL//Desktop//"+icon_path);
    }
    
    
    public static int TELNET_PORT = 5554;
    
    public final static String EMPTY_STRING = "";    
    
    public final static String URL_FILE_LOGICAL_CONTEXT = "C:/Users/MHL/Desktop/currentStatusLogical.ctx";
    
    public final static String URL_FILE_SITUATION = "C:/Users/MHL/Desktop/currentStatusSituation.ctx";
    
    public final static String URL_FILE_SCENARIO = "C:/Users/MHL/Desktop/currentStatusScenario.ctx";
    
    public final static String URL_FILE_CONTEXT_SIMULATOR = "C:/temp/contextSimulator.ctx";
    
    //*** Differntiate between Physical, Logical and Situations ***
    public final static String PHYSICAL = "Physical Context";
    
    public final static String LOGICAL = "Logical Context";
    
    public final static String SITUATION = "Situation";
    
    public final static String SCENARIO = "Scenario";
    
    public final static String ALL_SITUATIONS = "All Situations";
    
    //*** Context Source Names ***
    public final static String ACCELEROMETER = "Accelerometer";    
    
    public final static String THERMOMETER = "Thermometer";    
    
    public final static String BAROMETER = "Barometer";    
    
    public final static String LIGHTSENSOR = "Light-Sensor";  
    
    public final static String MAGNETOMETER = "Magnetometer";
    
    public final static String GYROSCOPE = "Gyroscope";
    
    public final static String TIMEDATE = "Time and Date";
    
    public final static String CALENDAR = "Appointment";
    
    public final static String GPS = "GPS";
    
    public final static String BATTERY = "Battery";
    
    public final static String RAM = "RAM";
    
    public final static String DISK = "Hard Disk";
    
    public final static String CPU = "CPU";
    
    public final static String SDCARD = "SD-Card";
    
    public final static String USBCABLE = "USB-Cable";
    
    public final static String MICROPHONE = "Microphone";
    
    public final static String CAMERA = "Camera";
    
    public final static String RFID = "RFID";
    
    public final static String QRCODE = "QR-Code";
    
    public final static String BLUETOOTH = "Bluetooth";
    
    public final static String INFRARED = "Infrared";
    
    public final static String HTTP = "HTTP-Response";
    
    public final static String WIFI = "Wi-Fi";
    
    public final static String NETWORK = "Network Connections";
    
    public final static String CELLID = "Cell ID";
    
    //*** Colour ***
    public final static Color COLOR_SITUATION = Color.WHITE;
    
    public final static Color COLOR_LOGICAL = Color.WHITE;
    
    public final static Color COLOR_PHYSICAL = Color.WHITE;
    
    //*** Physical Context Categories ***
    public final static String ALL_CATEGORIES = "All";   
    
    public final static String LOCATION = "Location";

    public final static String TIME = "Time";

    public final static String INDIVIDUAL = "Individual";

    public final static String DEVICE = "Device";

    public final static String VIRTUAL = "Virtual";

    public final static String RELATION = "Relation";








    
    
}
