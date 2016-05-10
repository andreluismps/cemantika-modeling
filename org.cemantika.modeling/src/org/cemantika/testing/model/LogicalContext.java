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
public class LogicalContext extends AbstractContext{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1310999892562774018L;
	
	private List<ContextSourceDefectPattern> contextSourceDefectPatterns;
	
	public LogicalContext(){
		super();
    }
	
	public LogicalContext(String name){
		super();
        setName(name);
        contextSourceDefectPatterns = new ArrayList<ContextSourceDefectPattern>();
    }
    
    @Override
    public void addChildContext(AbstractContext context){
        if(context instanceof LogicalContext || context instanceof PhysicalContext && !getContextList().contains(context)){
          getContextList().add(context);  
        }
    }

	public void setContextSourceDefectPatterns(
			List<ContextSourceDefectPattern> contextSourceDefectPatterns) {
		this.contextSourceDefectPatterns = contextSourceDefectPatterns;
	}

	public List<ContextSourceDefectPattern> getContextSourceDefectPatterns() {
		return contextSourceDefectPatterns;
	}

	public static LogicalContext newInstance(LogicalContext context) {
		LogicalContext newInstance = new LogicalContext(context.getName());
		
		for (AbstractContext abstractContext : context.getContextList())
			newInstance.getContextList().add(abstractContext);
		newInstance.setIdentity(context.getIdentity());
		return newInstance;
	}

}
