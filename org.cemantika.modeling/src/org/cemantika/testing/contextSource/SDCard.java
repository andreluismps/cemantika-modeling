/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.contextSource;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;

/**
 *
 * @author MHL
 */
public class SDCard extends PhysicalContext{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4250270265749791102L;

	//Model
    boolean value1 = false;
    
    //GUI
    private JPanel panel;
    private JLabel jLabel1;
    private JCheckBox cbValue1;
    
    //TODO Rechtsbuendig
    public SDCard(){
        super(Constants.SDCARD);
        
        //GUI
        panel = new JPanel();
        jLabel1 = new JLabel("SD-Card available:");
        cbValue1 = new JCheckBox();
        
        panel.setLayout(new GridLayout(3, 2, 0, 15));

        panel.add(new JLabel());
        panel.add(new JLabel());     
        panel.add(jLabel1);
        panel.add(cbValue1);
    }
    
    public void savePanel(){
        value1 = cbValue1.isSelected();
    }
    
    public JPanel getPanel(){
       return panel;
    }
    
    public String getTextAreaRepresentation(){
        StringBuilder sb = new StringBuilder(jLabel1.getText()).append(" ").append(value1);

        return sb.toString();
    }
    
    public String getCommand(){
        return "sd:"+value1;
    }
    
}
