/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.ContextDefectPattern;
import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author MHL
 */
public class WiFi extends PhysicalContext{
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -894979986671295654L;
	//Model
    private boolean value1;
    private String value2;
    
    //TODO Rechtsbuendig
    public WiFi(){
        super(Constants.WIFI);
        
        getContextDefectPatterns().add(ContextDefectPattern.SENSOR_NOISE_UNRELIABILITY);
        getContextDefectPatterns().add(ContextDefectPattern.SLOW_SENSING_OUT_OF_DATENESS);
        getContextDefectPatterns().add(ContextDefectPattern.SLOW_SENSING_WRONG_INTERPRETATION);
        getContextDefectPatterns().add(ContextDefectPattern.GLANULARITY_MISMATCH_IMPRECISION);
        getContextDefectPatterns().add(ContextDefectPattern.OVERLAPPING_SENSORS_UNPREDICTABLE);
    }
        
    public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {
    
		Button chkWiFiAvaliable =  createPhysicalContextDetailCheckField(group, "Wi-Fi available");
		addSelectionListener(chkWiFiAvaliable, WiFi.class.getDeclaredField("value1"), this);
		chkWiFiAvaliable.setSelection(value1);
        
        createPhysicalContextDetailLabel(group, "SSID");
        Text txtStartTime = createPhysicalContextDetailText(group);
        addFocusListener(txtStartTime, WiFi.class.getDeclaredField("value2"), this);
        txtStartTime.setText((value2 !=  null) ? value2 : "");
           
	}
    
}
