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

/**
 *
 * @author MHL
 */
public class Barometer extends PhysicalContext{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8793645815819328918L;

	//Model
    private int value1;
    
    //GUI
    private JPanel panel;
    private JLabel jLabel1;
    private JTextField tfValue1;
    
    //TODO Rechtsbuendig
    public Barometer(){
        super(Constants.BAROMETER);
        
        //Model
        value1 = 50;
        
        //GUI
        panel = new JPanel();
        jLabel1 = new JLabel("Barometer in hPa:");
        tfValue1 = new JTextField(String.valueOf(value1));
        
        panel.setLayout(new GridLayout(3, 2, 0, 15));

        panel.add(new JLabel());
        panel.add(new JLabel());     
        panel.add(jLabel1);
        panel.add(tfValue1);
    }
    
    public void savePanel(){
        try{
            value1 = Integer.parseInt(tfValue1.getText());
            
            if(value1 > 100 || value1 < 0){
                JOptionPane.showMessageDialog(null, "Please enter a correct value");
                value1 = 100;
            }
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Please enter a correct value");
        }
    }
    
    public JPanel getPanel(){
       return panel;
    }
    
    public String getTextAreaRepresentation(){
        StringBuilder sb = new StringBuilder(jLabel1.getText()).append(" ").append(value1);

        return sb.toString();
    }
    
    public String getCommand(){
        return "barometer:"+value1;
    }
    
}
