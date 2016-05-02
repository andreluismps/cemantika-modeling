/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


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
    }
    
    public static Scenario newInstance(Scenario scenario){
    	
    	Scenario newInstance = new Scenario(scenario.getName());
    	newInstance.setContextList(new ArrayList<AbstractContext>());
    	for (AbstractContext context : scenario.getContextList()){
    		TimeSlot newTimeSlot = TimeSlot.newInstance((TimeSlot) context);
    		newInstance.addChildContext(newTimeSlot);
    	}
    	
    	return newInstance;
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
    
    private transient Button addSituationBtn;
    private transient Button removeTimeSlotBtn;
    private transient Button moveUpTimeSlotBtn;
    private transient Button moveDownTimeSlotBtn;
    private transient String selectedSituationKey;
    private transient Situation selectedSituation;
    private transient Map<String, Situation> eligibleSituations;
    private transient List situationsList; 
    private transient List timeSlotsList;
    private transient Composite timeSlotsComposite;
    
    public void createScenarioDetails(Group group, Map<String, Situation> eligibleSituations) throws SecurityException, NoSuchFieldException{
		
		createDetailLabel(group, "Name");
		Text txtSituationName = createSituationDetailText(group, SWT.NONE, 1);
        addFocusListener(txtSituationName, AbstractContext.class.getDeclaredField("name"), this);
        txtSituationName.setText((getName() != null) ? getName() : "");
		
        createSeparatorLine(group);
                
        Composite composite = createSplittedComposite(group);
        
        Composite situationsComposite = createCompositeToList(composite);
        
        createDetailLabel(situationsComposite, "Elegible situations from CKTB");
        
        this.eligibleSituations = eligibleSituations;
        
        situationsList = createSituationsList(situationsComposite, this.eligibleSituations);
        addListenersToSituationList(situationsList);
        
        Composite actionButtonsComposite = createButtonsComposite(composite);
        
        timeSlotsComposite = createCompositeToList(composite);
        
        fillTimeSlotsComposite();
        
        
        /*
		for (AbstractContext abstractContext : this.getContextList()) {
			if (abstractContext instanceof TimeSlot){
				TimeSlot timeSlot = (TimeSlot)abstractContext;
				for(AbstractContext abstractContext2 : timeSlot.getContextList())
					createSituationDetailLabel(group, "Time " + timeSlot.getId() + ": " + abstractContext2.getName());
			}
		}
		
		//createSituationDetailLabel(group, "");
		createSeparatorLine(group);
		createSituationDetailLabel(group, "");
		*/
		//createSituationDetailLabel(group, "Expected Behavior");
		//Text txtExpectedBehavior = createSituationDetailText(group, SWT.MULTI| SWT.WRAP | SWT.V_SCROLL, 5);
        //addFocusListener(txtExpectedBehavior, Situation.class.getDeclaredField("expectedBehavior"), this);
        //txtExpectedBehavior.setText((expectedBehavior != null) ? expectedBehavior : "");
		
	}

	private void fillTimeSlotsComposite() {
		disposeChildrenControls(timeSlotsComposite);
		
		createDetailLabel(timeSlotsComposite, "Scenario timeslots");
        
        timeSlotsList = createTimeSlotsCompositeList(timeSlotsComposite);
        addListenersToTimeSlotsList(timeSlotsList);
        
        timeSlotsComposite.layout();
	}
    
	private void addListenersToSituationList(final List situationsList){
    	situationsList.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedSituationKey = situationsList.getItem(situationsList.getSelectionIndex());
				
				selectedSituation = eligibleSituations.get(selectedSituationKey);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
    	
    	situationsList.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				addSituationBtn.setEnabled(false);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				addSituationBtn.setEnabled(true);
			}
		});
    }
	
	private void addListenersToTimeSlotsList(List timeSlotsList) {
		timeSlotsList.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
    	
		timeSlotsList.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				removeTimeSlotBtn.setEnabled(false);
				moveUpTimeSlotBtn.setEnabled(false);
				moveDownTimeSlotBtn.setEnabled(false);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				removeTimeSlotBtn.setEnabled(true);
				moveUpTimeSlotBtn.setEnabled(true);
				moveDownTimeSlotBtn.setEnabled(true);
			}
		});
	}

	private final int LIST_WIDTH = 220;
	private final int LIST_HEIGHT = 220;

	private List createTimeSlotsCompositeList(Composite composite) {
		//composite.dispose();
		List list = new List(composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData myGrid = new GridData(LIST_WIDTH, LIST_HEIGHT);
		list.setLayoutData(myGrid);
		
		java.util.List<String> timeSlots = new ArrayList<String>();
		for (AbstractContext abstractContext : this.getContextList()) {
			if (abstractContext instanceof TimeSlot){
				TimeSlot timeSlot = (TimeSlot)abstractContext;
				for(AbstractContext abstractContext2 : timeSlot.getContextList())
					timeSlots.add("Time " + String.format("%03d", timeSlot.getId()) + ": " + abstractContext2.getName());
			}
		}
			
		Collections.sort(timeSlots);
		

		for (String timeSlotName : timeSlots) {
			list.add(timeSlotName);
		}
		return list;
	
	}
	
	private List createSelectedSensorDefectCompositeList(Composite composite) {
		List list = new List(composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData myGrid = new GridData(330, 286);
		list.setLayoutData(myGrid);
		
		//TODO Add data
		
		return list;
	
	}
	
	private Button createSituationButton(Composite composite, String label) {
		Button btn = new Button(composite, SWT.PUSH);
		btn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btn.setText(label);
		return btn;
	}

	private Composite createButtonsComposite(Composite composite) {
		Composite btnComposite = new Composite(composite, SWT.NONE);
		btnComposite.setLayout(new GridLayout(1, true));
		btnComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 2));
		
	    addSituationBtn = createSituationButton(btnComposite, "Add");
	    addAddSituationListener();
	    removeTimeSlotBtn = createSituationButton(btnComposite, "Remove");
	    addRemoveTimeSlotListener();
		
		createDetailLabel(btnComposite, "");
		createDetailLabel(btnComposite, "");
		
		moveUpTimeSlotBtn = createSituationButton(btnComposite, "Move Up");
		moveDownTimeSlotBtn = createSituationButton(btnComposite, "Move Down");
		
		addSituationBtn.setEnabled(false);
		removeTimeSlotBtn.setEnabled(false);
		moveUpTimeSlotBtn.setEnabled(false);
		moveDownTimeSlotBtn.setEnabled(false);
		
		return btnComposite;
	}
	
	private Composite createDefectButtonsComposite(Composite composite) {
		Composite btnComposite = new Composite(composite, SWT.NONE);
		btnComposite.setLayout(new GridLayout(1, true));
		btnComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 2));
		
		createSituationButton(btnComposite, "Add");
		createSituationButton(btnComposite, "Remove");
		
		return btnComposite;
	}
	
	private void addAddSituationListener(){
		addSituationBtn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					
					if (situationsList.getSelectionIndex() < 0 ) return;
					
					int id = timeSlotsList.getItemCount();
					
					TimeSlot newTimeSlot = new TimeSlot(id);
					newTimeSlot.addChildContext(selectedSituation);
					
					Scenario.this.addChildContext(newTimeSlot);
					
					fillTimeSlotsComposite();
					
					break;
				}
			}
		});
	}
	
	private void addRemoveTimeSlotListener(){
		removeTimeSlotBtn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					
					int id = timeSlotsList.getSelectionIndex();
					
					if (id < 0 ) return;
					
					Scenario.this.getContextList().remove(id);
					
					for (AbstractContext abstractContext : Scenario.this.getContextList())
						if (abstractContext instanceof TimeSlot)
							//TODO realign timeslots ids after removal
					
					fillTimeSlotsComposite();
					
					break;
				}
			}
		});
	}

	private void disposeChildrenControls(final Composite composite_2) {
		for (Control control : composite_2.getChildren()) {
	        control.dispose();
	    }
	}
	
	private void createSeparatorLine(Group group) {
		Label separator = new Label(group, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
    
	protected void createDetailLabel(Composite composite, String labelText) {
		Label label = new Label(composite, SWT.NONE | SWT.WRAP);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        label.setText(labelText);
	}
	
	protected Text createSituationDetailText(Group group, int style, int lines) {
		final Text text = new Text(group, style);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = lines * text.getLineHeight();
        text.setLayoutData(gridData);
		return text;
	}
	
	private Composite createSplittedComposite(Group group) {
		Composite composite = new Composite(group, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        //group.setContent(composite);
        group.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return composite;
	}
	
	private Composite createCompositeToList(Composite composite) {
		Composite situationsComposite = new Composite(composite, SWT.NONE);
        situationsComposite.setLayout(new GridLayout(1, false));
        situationsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        return situationsComposite;
	}
	
	private List createSituationsList(Composite situationsComposite, Map<String, Situation> eligibleSituations) {
		List list = new List(situationsComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData myGrid = new GridData(LIST_WIDTH, LIST_HEIGHT);
		list.setLayoutData(myGrid);
		
		SortedSet<String> orderedSituations = new TreeSet<String>(eligibleSituations.keySet());

		for (String situationName : orderedSituations) {
			list.add(situationName);
		}
		return list;
	}
	
	private List createSensorList(Composite situationsComposite, Map<String, Situation> eligibleSituations) {
		List list = new List(situationsComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData myGrid = new GridData(330, 130);
		list.setLayoutData(myGrid);
		
		SortedSet<String> orderedSituations = new TreeSet<String>(eligibleSituations.keySet());

		for (String situationName : orderedSituations) {
			list.add(situationName);
		}
		return list;
	}
	
	private List createDefectList(Composite situationsComposite, Map<String, Situation> eligibleSituations) {
		List list = new List(situationsComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData myGrid = new GridData(330, 130);
		list.setLayoutData(myGrid);
		
		SortedSet<String> orderedSituations = new TreeSet<String>(eligibleSituations.keySet());

		for (String situationName : orderedSituations) {
			list.add(situationName);
		}
		return list;
	}

	public void createTestCaseDetails(Group group) throws SecurityException, NoSuchFieldException{
		        
        Composite composite = createSplittedComposite(group);
        
        Composite selectionComposite = createCompositeToList(composite);
        
        createDetailLabel(selectionComposite, "Identified Sensors in Scenario");
        
        List sensorsList = createSensorList(selectionComposite, new HashMap<String, Situation>());
        
        createDetailLabel(selectionComposite, "Sensor related defects"); 
        
        List defectsList = createDefectList(selectionComposite, new HashMap<String, Situation>());
        
        Composite actionButtonsComposite = createDefectButtonsComposite(composite);
        
        Composite selectedSensorDefectComposite = createCompositeToList(composite);
        
        createDetailLabel(selectedSensorDefectComposite, "Selected sensor related defects for test case generation");
        
        List timeSlotsList = createSelectedSensorDefectCompositeList(selectedSensorDefectComposite);
		
	}


}
