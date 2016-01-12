/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;


/**
 *
 * @author MHL
 */
public class Scenario extends AbstractContext{

    /**
	 * 
	 */
	private static final long serialVersionUID = -6159223338162142559L;

	private int index = 0;
    
    private int currentTransmissionIndex = 0;
    
    public Scenario(String name){
      setName(name);    
      
      //leafIcon = Constants.getInstance().getImageIcon(Constants.URL_ICON_SCENARIO);
      
      addChildContext(new TimeSlot(0));
    }
    
    @Override
    public void addChildContext(AbstractContext context){
        if(context instanceof TimeSlot){
          getContextList().add(context);  
        }
    }
    
    @Override
    public void addChildContext(AbstractContext context, int timeSlot){
        if(!(context instanceof TimeSlot) &&
           !(context instanceof Scenario)){
            
            while(getContextList().size() <= timeSlot){
               addChildContext(new TimeSlot(++index)); 
            }
            getContextList().get(timeSlot).addChildContext(context);
        }else if(context instanceof Scenario){
            //Just for the case: TIMELINE
            while((getContextList().size()) <= (timeSlot+context.getContextList().size()-1)){
               addChildContext(new TimeSlot(++index)); 
            }
            
            for(int i=timeSlot;i<context.getContextList().size()+timeSlot;i++){
                for(AbstractContext currentContext: context.getContextList().get(i-timeSlot).getContextList()){
                    //getContextList().get(i).addChildContext(currentContext); 
                    TimeSlot t = (TimeSlot) getContextList().get(i);
                    t.addChildContext(currentContext);
                }
            }
        }
    }
    
    public int getMaxDepth(){
        int depth = 0;
        
        for(AbstractContext slot: getContextList()){
            if(slot.getContextList().size()>depth){
                depth = slot.getContextList().size();
            }
        }

        return depth;
    }

    public int getCurrentTransmissionIndex() {
        return currentTransmissionIndex;
    }

    public void setCurrentTransmissionIndex(int currentTransmissionIndex) {
        this.currentTransmissionIndex = currentTransmissionIndex;
    }
    
    


}
