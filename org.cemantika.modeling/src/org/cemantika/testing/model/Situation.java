/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.awt.Color;

import org.cemantika.testing.util.Constants;

/**
 *
 * @author MHL
 */
public class Situation extends AbstractContext{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6275195839246421095L;

	public Situation(String name){
      setName(name);    
      
      //leafIcon = Constants.getInstance().getImageIcon(Constants.URL_ICON_SITUATION);
    }
        
    @Override
    public String getTableRepresentation() {
      return "S: "+getName();
    }

    @Override
    public Color getBackgroundColor() {
        return Constants.COLOR_SITUATION;
    }
    
    @Override
    public void addChildContext(AbstractContext context){
        if(!(context instanceof Scenario)){
          getContextList().add(context);  
        }
    }


}
