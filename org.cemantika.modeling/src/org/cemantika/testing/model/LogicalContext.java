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
public class LogicalContext extends AbstractContext{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1310999892562774018L;

	public LogicalContext(String name){
        setName(name);
    }
    
    @Override
    public String getTableRepresentation() {
        return "L: "+getName();
    }

    @Override
    public Color getBackgroundColor() {
        return Constants.COLOR_LOGICAL;
    }
    
    @Override
    public void addChildContext(AbstractContext context){
        if(context instanceof LogicalContext || context instanceof PhysicalContext && !getContextList().contains(context)){
          getContextList().add(context);  
        }
    }

}
