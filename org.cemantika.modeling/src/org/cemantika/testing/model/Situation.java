/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author MHL
 */
public class Situation extends AbstractContext{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6275195839246421095L;
	
	private List<String> expectedActions = new ArrayList<String>(); 

	public Situation(String name){
      setName(name);    
    }
        
    @Override
    public void addChildContext(AbstractContext context){
        if(!(context instanceof Scenario)){
          getContextList().add(context);  
        }
    }

	public void setExpectedActions(List<String> expectedActions) {
		this.expectedActions = expectedActions;
	}

	public List<String> getExpectedActions() {
		return expectedActions;
	}

	public static Situation newInstance(Situation context) {
		Situation newInstance = new Situation(context.getName());
		newInstance.setContextList(new ArrayList<AbstractContext>(context.getContextList()));
		return newInstance;
	}


}
