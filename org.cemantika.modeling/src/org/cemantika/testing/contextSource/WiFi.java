/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.contextSource;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;

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
    private String value2 = "Meeting Room";
    
    //GUI
    private JPanel panel;
    private JLabel jLabel1;
    private JCheckBox cbValue1;
    private JLabel jLabel2;
    private JTextField jTextField1;
    
    //TODO Rechtsbuendig
    public WiFi(){
        super(Constants.WIFI);
        
        //GUI
        panel = new JPanel();
        jLabel1 = new JLabel("Wi-Fi available:");
        cbValue1 = new JCheckBox();
        jLabel2 = new JLabel("SSID: ");
        jTextField1 = new JTextField(value2);
        
        panel.setLayout(new GridLayout(3, 2, 0, 15));
     
        panel.add(jLabel1);
        panel.add(cbValue1);
        panel.add(jLabel2);
        panel.add(jTextField1);
    }
    
    public void savePanel(){
        value1 = cbValue1.isSelected();
        value2 = jTextField1.getText();
    }
    
    public JPanel getPanel(){
       return panel;
    }
    
    public String getTextAreaRepresentation(){
        StringBuilder sb = new StringBuilder(jLabel1.getText()).append(" ").append(value1).append(", SSID: ").append(value2);

        return sb.toString();
    }
    
    public String getCommand(){
        return "wifi:"+value1+":"+value2;
    }
    
    
}
