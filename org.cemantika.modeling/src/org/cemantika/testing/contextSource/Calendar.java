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
public class Calendar extends PhysicalContext{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4598288158432074459L;
	//Model
    private String appointmentName;
    private String startTime;
    private String endTime;
    private String date;
    
    public Calendar(){
        super(Constants.CALENDAR);
        
        //Model
        appointmentName = "Meeting 1";
        startTime = "08:00";
        endTime = "09:00";
        date = "30.04.2014";
        
        
    }
    
    public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {
    	
    	createPhysicalContextDetailLabel(group, "Appointment Name");
        Text txtAppointmentName = createPhysicalContextDetailText(group);
        addFocusListener(txtAppointmentName, Calendar.class.getDeclaredField("appointmentName"), this);
        txtAppointmentName.setText(appointmentName);
        
        createPhysicalContextDetailLabel(group, "Start (hh:mm:ss)");
        Text txtStartTime = createPhysicalContextDetailText(group);
        addFocusListener(txtStartTime, Calendar.class.getDeclaredField("startTime"), this);
        txtStartTime.setText(startTime);
        
        createPhysicalContextDetailLabel(group, "End (hh:mm:ss)");
        Text txtEndTime = createPhysicalContextDetailText(group);
        addFocusListener(txtEndTime, Calendar.class.getDeclaredField("endTime"), this);
        txtEndTime.setText(endTime);
        
        createPhysicalContextDetailLabel(group, "Date (dd.mm.yyyy)");
        Text txtDate = createPhysicalContextDetailText(group);
        addFocusListener(txtDate, Calendar.class.getDeclaredField("date"), this);
        txtDate.setText(date);
	}
    
}
