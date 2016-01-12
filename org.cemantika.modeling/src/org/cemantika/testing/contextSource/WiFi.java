/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.contextSource;

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
    private boolean value1 = false;
    private String value2 = "";
    
    //TODO Rechtsbuendig
    public WiFi(){
        super(Constants.WIFI);
    }
        
    public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {
    
		Button chkWiFiAvaliable =  createPhysicalContextDetailCheckField(group, "Wi-Fi available");
		addSelectionListener(chkWiFiAvaliable, WiFi.class.getDeclaredField("value1"), this);
		chkWiFiAvaliable.setSelection(value1);
        
        createPhysicalContextDetailLabel(group, "SSID");
        Text txtStartTime = createPhysicalContextDetailText(group);
        addFocusListener(txtStartTime, WiFi.class.getDeclaredField("value2"), this);
        txtStartTime.setText(value2);
           
	}
    
}
