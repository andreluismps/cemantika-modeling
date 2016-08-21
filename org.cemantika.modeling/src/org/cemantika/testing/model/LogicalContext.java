/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;

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
	
	public String getDefectText(){
		if (contextSourceDefectPatterns.isEmpty()) return "Defectless logical context";
		
		if (contextSourceDefectPatterns.size() == 1)
			return getSingleDefectText();
		
		return "Multiple defects " + this.getName();
	}
	
	private String getSingleDefectText(){
		
		ContextDefectPattern contextDefectPattern = getContextSourceDefectPatterns().get(0).getContextDefectPattern();
		
		String originalName = this.getName().substring(0, this.getName().indexOf(contextDefectPattern.toString()));
		originalName = originalName.substring(0, originalName.lastIndexOf('-'));
		originalName = originalName.substring(0, originalName.lastIndexOf('-'));
		originalName = originalName.substring(0, originalName.lastIndexOf(' '));
		
		String text = "'" + originalName + "'" + " with following defect: \n "
					+ " - " + contextDefectPattern + "\n\n"					
					+ "To generate defect data to this logical context, please fill these fields with ";
		switch (contextDefectPattern) {
		case GLANULARITY_MISMATCH_IMPRECISION:
			
			text += "slightly modified data. "
				+ "Even with the modification, the actual context has not changed.\n"
				+ "To ease defect discovery, use values close to the limit imposed by the rule to be evaluated.\n\n"
				+ "Example: If the logical context defines the user presence inside a room, send information indicating that it is in front of the room door, in room outside.";
			break;
		case SLOW_SENSING_OUT_OF_DATENESS:
			text += "old data.\n"
				 + "Exemple: While user is inside a company room, the supplied logical context is outdated in 2 hours and indicates that user is still at home. Or otherwise.";
			break;
		default:
			text = "";
		}
		return text;
	}

}
