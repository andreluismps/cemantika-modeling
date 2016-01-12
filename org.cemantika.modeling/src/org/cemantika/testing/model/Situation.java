/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;


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
    }
        
    @Override
    public void addChildContext(AbstractContext context){
        if(!(context instanceof Scenario)){
          getContextList().add(context);  
        }
    }


}
