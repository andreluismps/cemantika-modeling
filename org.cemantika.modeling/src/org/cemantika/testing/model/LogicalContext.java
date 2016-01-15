/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;


/**
 *
 * @author MHL
 */
public class LogicalContext extends AbstractContext{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1310999892562774018L;

	public LogicalContext(){
		super();
    }
	
	public LogicalContext(String name){
		super();
        setName(name);
    }
    
    @Override
    public void addChildContext(AbstractContext context){
        if(context instanceof LogicalContext || context instanceof PhysicalContext && !getContextList().contains(context)){
          getContextList().add(context);  
        }
    }

}
