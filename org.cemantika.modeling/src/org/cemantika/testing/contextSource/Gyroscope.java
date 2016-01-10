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

/**
 *
 * @author MHL
 */
public class Gyroscope extends PhysicalContext{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2732730733392471458L;
	//Model
    private double value1;
    private double value2;
    private double value3;
    
    //GUI
    private JPanel panel;
    private JLabel jLabel1;
    private JTextField tf1;
    private JLabel jLabel2;
    private JTextField tf2;
    private JLabel jLabel3;
    private JTextField tf3;
    
    public Gyroscope(){
        super(Constants.GYROSCOPE);
        
        //Model
        value1 = 0;
        value2 = 0;
        value3 = 0;
        
        //GUI
        panel = new JPanel();
        jLabel1 = new JLabel("X-Axis:");
        jLabel2 = new JLabel("Y-Axis:");
        jLabel3 = new JLabel("Z-Axis:");
        tf1 = new JTextField(String.valueOf(value1));
        tf2 = new JTextField(String.valueOf(value2));
        tf3 = new JTextField(String.valueOf(value3));
        
        panel.setLayout(new GridLayout(3, 2, 0, 15));

        panel.add(jLabel1);
        panel.add(tf1);

        panel.add(jLabel2);
        panel.add(tf2);
        
        panel.add(jLabel3);
        panel.add(tf3);
    }
    
    public void savePanel(){
        value1 = Double.parseDouble(tf1.getText());
        value2 = Double.parseDouble(tf2.getText());
        value3 = Double.parseDouble(tf3.getText());
    }
    
    public JPanel getPanel(){
       return panel;
    }
    
    public String getTextAreaRepresentation(){
        StringBuilder sb = new StringBuilder(getName());
        sb = sb.append(": ").append(value1).append(", ").append(value2).append(", ").append(value3);
        
        return sb.toString();
    }
    
}
