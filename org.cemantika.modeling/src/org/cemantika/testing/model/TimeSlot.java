/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author MHL
 */
public class TimeSlot extends AbstractContext{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4296345488077489429L;

	private final int id;

    private Map<String, PhysicalContext> mapPhysicalContext;
    
    public TimeSlot(int id){
        setName("Time: "+id);
                
        this.id = id;
    }

    public List<PhysicalContext> getTransmissionInSpot(){
        mapPhysicalContext = new HashMap<String,PhysicalContext>();
        
        findAllPhysicalContextRekursiv(getContextList());
        
        List list = new ArrayList<PhysicalContext>(mapPhysicalContext.values());

        return list;
    }
    
    private void findAllPhysicalContextRekursiv(List<AbstractContext> lstContext){
        for(AbstractContext context: lstContext){
            if(context instanceof PhysicalContext){
                if(!mapPhysicalContext.containsKey(context.getName()))
                    mapPhysicalContext.put(context.getName(), (PhysicalContext) context);
            }else{
                findAllPhysicalContextRekursiv(context.getContextList());
            }
        }
    }


    @Override
    public void addChildContext(AbstractContext context) {
        if(!(context instanceof TimeSlot) && !(context instanceof Scenario)){
          getContextList().add(context);  
        }    
    }
    
}
