/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.contextSource;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
    
    //GUI
    private JPanel panel;
    private JLabel jLabel1;
    private JTextField tfAppointmentName;
    private JLabel jLabel2;
    private JTextField tfStartTime;
    private JLabel jLabel3;
    private JTextField tfEndTime;
    private JLabel jLabel4;
    private JTextField tfDate;
    
    public Calendar(){
        super(Constants.CALENDAR);
        
        //Model
        appointmentName = "Meeting 1";
        startTime = "08:00";
        endTime = "09:00";
        date = "30.04.2014";
        
        //GUI
        panel = new JPanel();
        jLabel1 = new JLabel("Appointment Name:");
        jLabel2 = new JLabel("Start (hh:mm:ss):");
        jLabel3 = new JLabel("End (hh:mm:ss):");
        jLabel4 = new JLabel("Date (dd.mm.yyyy):");
        tfAppointmentName = new JTextField(appointmentName);
        tfStartTime = new JTextField(startTime);
        tfEndTime = new JTextField(endTime);
        tfDate = new JTextField(date);
        
        panel.setLayout(new GridLayout(4, 2, 0, 8));

        panel.add(jLabel1);
        panel.add(tfAppointmentName);

        panel.add(jLabel4);
        panel.add(tfDate);

        panel.add(jLabel2);
        panel.add(tfStartTime);
        
        panel.add(jLabel3);
        panel.add(tfEndTime);
    }
    
    public void savePanel(){
        appointmentName = tfAppointmentName.getText();
        startTime = tfStartTime.getText();
        endTime = tfEndTime.getText();    
        date = tfDate.getText();
    }
    
    public JPanel getPanel(){
       return panel;
    }
    
    public String getTextAreaRepresentation(){
        StringBuilder sb = new StringBuilder(getName());
        sb = sb.append(": ").append(appointmentName).append(", Date: ").append(date).append(", Start: ").append(startTime).append(", End: ").append(endTime);
        
        return sb.toString();
    }
    
    @Override
    public String getCommand(){
        return "app-"+date+"-"+startTime+"-"+endTime;
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
