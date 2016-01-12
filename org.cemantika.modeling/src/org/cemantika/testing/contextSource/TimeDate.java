/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;

/**
 *
 * @author MHL
 */
public class TimeDate extends PhysicalContext{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -3629033123060687135L;

	//Model
    private String time, date;
    
    
    
    public TimeDate(){
        super(Constants.TIMEDATE);
        
        //Model
        time = "00:00:00";
        date = "01.01.2014";
    }
}
