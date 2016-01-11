/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.contextSource;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
public class GPS extends PhysicalContext{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2796409290683390549L;

	//Model
    private double latitude, longitude, altitude;
    
    //GUI
    private JPanel panel;
    private JLabel jLabel1;
    private JTextField tfLatitude;
    private JLabel jLabel2;
    private JTextField tfLongitude;
    private JLabel jLabel3;
    private JTextField tfAltitude;
    
    public GPS(){
        super(Constants.GPS);
        
        //Model
        latitude = 0;
        longitude = 0;
        altitude = 0;
        
        //GUI
        panel = new JPanel();
        jLabel1 = new JLabel("Latitude:");
        tfLatitude = new JTextField(String.valueOf(latitude));
        jLabel2 = new JLabel("Longitude:");
        tfLongitude = new JTextField(String.valueOf(longitude));
        jLabel3 = new JLabel("Altitude:");
        tfAltitude = new JTextField(String.valueOf(altitude));
        
        panel.setLayout(new GridLayout(3, 2, 0, 15));

        panel.add(jLabel1);
        panel.add(tfLatitude);

        panel.add(jLabel2);
        panel.add(tfLongitude);

        panel.add(jLabel3);
        panel.add(tfAltitude);
    }
    
    public void savePanel(){
        try{
            latitude = Double.valueOf(tfLatitude.getText());
            longitude = Double.valueOf(tfLongitude.getText());
            altitude = Double.valueOf(tfAltitude.getText());
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Please enter a correct value");
        }
    }
    
    public JPanel getPanel(){
       return panel;
    }
    
    public String getTextAreaRepresentation(){
        StringBuilder sb = new StringBuilder(getName());
        sb = sb.append(": ").append(latitude).append(", ").append(longitude).append(", ").append(altitude);
        
        return sb.toString();
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
