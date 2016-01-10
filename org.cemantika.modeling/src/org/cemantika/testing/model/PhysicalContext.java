/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cemantika.testing.util.Constants;

/**
 *
 * @author MHL
 */
public class PhysicalContext extends AbstractContext{
 //   ContextSource
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -437658113632518586L;

	public PhysicalContext(String name){
      setName(name);    
      
      //leafIcon = Constants.getInstance().getImageIcon(Constants.URL_ICON_PHYSICAL);
    }
    
    @Override
    public void addChildContext(AbstractContext context){
        //Physical Context can`t have children
    }

    @Override
    public String getTableRepresentation() {
        return "P: "+getName();
    }

    @Override
    public Color getBackgroundColor() {
        return Constants.COLOR_PHYSICAL;
    }
    
    //TODO: Abstract machen
    public void savePanel(){
        
    }
    
    //TODO: Abstract machen
    public String getTextAreaRepresentation() {
        return getName()+":";
    }
    
    //TODO: Abstract machen
    public JPanel getPanel(){
       JPanel panel = new JPanel();

        JLabel jLabel1 = new JLabel();

        panel.setLayout(new GridLayout(3, 2));

        jLabel1.setText("Not implemented yet");
        panel.add(jLabel1);

       return panel;
    }
    
    //TODO: Abstract machen
    public String getCommand(){
        return "";
    }
}
