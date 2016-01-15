/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author MHL
 */
public class GPS extends PhysicalContext{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2796409290683390549L;

	//Model
    private double latitude, longitude, altitude;
        
    public GPS(){
        super(Constants.GPS);
        
        //Model
        //latitude = 0;
        //longitude = 0;
        //altitude = 0;
        
        
    }
    
    public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {
    	
    	createPhysicalContextDetailLabel(group, "Latitude");
        Text txtLatitude = createPhysicalContextDetailText(group);
        addFocusListener(txtLatitude, GPS.class.getDeclaredField("latitude"), this);
        txtLatitude.setText(String.valueOf(latitude));
        
        createPhysicalContextDetailLabel(group, "Longitude");
        Text txtLongitude = createPhysicalContextDetailText(group);
        addFocusListener(txtLongitude, GPS.class.getDeclaredField("longitude"), this);
        txtLongitude.setText(String.valueOf(longitude));
        
        createPhysicalContextDetailLabel(group, "Altitude");
        Text txtAltitude = createPhysicalContextDetailText(group);
        addFocusListener(txtAltitude, GPS.class.getDeclaredField("altitude"), this);
        txtAltitude.setText(String.valueOf(altitude));
	}	
        
}
