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
public class TimeDate extends PhysicalContext{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -3629033123060687135L;

	//Model
    private String time, date;
    
    //GUI
    private JPanel panel;
    private JLabel jLabel1;
    private JTextField tfTime;
    private JLabel jLabel2;
    private JTextField tfDate;
    
    public TimeDate(){
        super(Constants.TIMEDATE);
        
        //Model
        time = "00:00:00";
        date = "01.01.2014";
        
        //GUI
        panel = new JPanel();
        jLabel1 = new JLabel("Time (hh:mm:ss):");
        tfTime = new JTextField(time);
        jLabel2 = new JLabel("Date (dd.mm.yyyy):");
        tfDate = new JTextField(date);
        
        panel.setLayout(new GridLayout(3, 2, 0, 15));

        panel.add(jLabel1);
        panel.add(tfTime);

        panel.add(jLabel2);
        panel.add(tfDate);
    }
    
    public void savePanel(){
        time = tfTime.getText();
        date = tfDate.getText();
    }
    
    public JPanel getPanel(){
       return panel;
    }
    
    public String getTextAreaRepresentation(){
        StringBuilder sb = new StringBuilder(getName());
        sb = sb.append(": ").append(time).append(", ").append(date);
        
        return sb.toString();
    }
    
        @Override
    public String getCommand(){
        return "date-"+date+"-"+time;
    }
    
}
