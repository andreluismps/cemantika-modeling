package org.cemantika.testing.cktb.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.testing.cktb.dao.ScenarioCKTBDAO;
import org.cemantika.testing.cktb.dao.SituationCKTBDAO;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.model.TimeSlot;
import org.cemantika.testing.util.CxGUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.swt.widgets.Shell;

public class ManageBaseScenarioCKTB extends Dialog {
	
	private PluginManager manager;
	private Map<String, Scenario> scenarios;
	private Map<String, Situation> eligibleSituations;
	
	private Button apply;
	private Button revert;
	
	private Button newScenario;
	private Button deleteScenario;
	
	private Scenario selectedScenario;
	
	private String selectedScenarioKey;
	private String selectedScenarioName;
	private java.util.List<AbstractContext> selectedScenarioTimeSlots;
	private String CxGFullName;
	
	private List list;
	private Composite composite;
	private Composite scenariosComposite;
	private Composite scenarioDetailComposite;
	private Composite buttonsComposite;
	private ScrolledComposite scrolledComposite;
	private final String NEW_SCENARIO_NAME = "New Scenario #";
	private int newScenarioCount = 1;
	private final String MODEL_EXTRACTED_SCENARIO = "**** CxG generated Scenario - Rename to save me ****";
	private java.util.List<AbstractContext> deletedScenarios = new ArrayList<AbstractContext>();
	
	

    public ManageBaseScenarioCKTB(final Shell parent, PluginManager manager, Map<String, Scenario> scenarios, IFile contextualGraph, IFile file) 
    {
    	
        super(parent);
        this.manager = manager;
        this.scenarios = loadCKTB(contextualGraph, file);
        this.setShellStyle(getShellStyle() | SWT.RESIZE);
		
    }
    
    @Override
    protected Control createDialogArea(final Composite parent) 
    {
        final Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout());
        
        getShell().setText("Manage Base Scenarios");

        scrolledComposite = createMainComposite(container);

        composite = createSplittedComposite(scrolledComposite);
        
        scenariosComposite = createScenariosCompositeList(composite);
        
        list = createScenariosList(scenariosComposite);
        
        createScenariosListButtonsComposite();
		
        scenarioDetailComposite = createScenarioDetailComposite(composite);
        
        addListenersToScenariosCompositeList(scrolledComposite, composite, list, scenarioDetailComposite);

        return container;
    }

	private void addListenersToScenariosCompositeList(
			final ScrolledComposite scrolledComposite,
			final Composite composite, final List list,
			final Composite scenarioDetailComposite) {
		list.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {

				selectedScenarioKey = list.getItem(list.getSelectionIndex());
				
				selectedScenario = Scenario.newInstance(scenarios.get(selectedScenarioKey));

				selectedScenarioName = selectedScenario.getName();
				
				selectedScenarioTimeSlots = new ArrayList<AbstractContext>(selectedScenario.getContextList());
				
				resetScenarioDetailComposite();
				
				scrolledComposite.layout(true, true);
                scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
			}

			public void widgetDefaultSelected(SelectionEvent event) {}
		});
		
		list.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				deleteScenario.setEnabled(false);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				deleteScenario.setEnabled(true);
			}
		});
	}
	
	private void resetScenarioDetailComposite() {
		disposeChildrenControls(scenarioDetailComposite);
		
		createScenarioDetailGroup(scenarioDetailComposite, selectedScenario);
		
		Composite btnSituationComposite = createBtnSituationComposite(scenarioDetailComposite);
		
		revert = createButton(btnSituationComposite, "Revert");
		addRevertListener();
		
		apply = createButton(btnSituationComposite, "Apply");
		addApplyListener();
	}
	
	private Button createButton(Composite composite, String label) {
		Button btn = new Button(composite, SWT.PUSH);
		btn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btn.setText(label);
		return btn;
	}
	
	private void addNewListener() {
		newScenario.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					//Needed because last text field don not lose focus				
					scenarioDetailComposite.setEnabled(false);
					scenarioDetailComposite.setEnabled(true);
					
					Scenario newScenarioInstance = new Scenario(NEW_SCENARIO_NAME + newScenarioCount++);
					newScenarioInstance.setContextList(new ArrayList<AbstractContext>());
					newScenarioInstance.setCxGFullName(CxGFullName);
					
					scenarios.put(newScenarioInstance.getName(), newScenarioInstance);
					
					disposeChildrenControls(scenariosComposite);

					list = createScenariosList(scenariosComposite);
					
					createScenariosListButtonsComposite();

					scenariosComposite.layout();
					addListenersToScenariosCompositeList(scrolledComposite, composite, list, scenarioDetailComposite);
					break;
				}
			}
		});
	}
	
	private void addDeleteListener() {
		deleteScenario.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					//Needed because last text field don not lose focus				
					scenarioDetailComposite.setEnabled(false);
					scenarioDetailComposite.setEnabled(true);
					
					scenarios.remove(selectedScenarioKey);
					
					deletedScenarios.add(selectedScenario);
					
					disposeChildrenControls(scenariosComposite);

					list = createScenariosList(scenariosComposite);
					
					createScenariosListButtonsComposite();
			        
			        resetScenarioDetailComposite();
					
					scenariosComposite.layout();
					addListenersToScenariosCompositeList(scrolledComposite, composite, list, scenarioDetailComposite);
					break;
				}
			}
			
		});
	}
	
	private void addApplyListener() {
		apply.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					//Needed because last text field don not lose focus				
					scenarioDetailComposite.setEnabled(false);
					scenarioDetailComposite.setEnabled(true);
					
					for(int i =0; i<= newScenarioCount; i++)
						if (selectedScenario.getName().equals(NEW_SCENARIO_NAME + i))
							return;
					
					scenarios.remove(selectedScenarioKey);
					scenarios.put(selectedScenario.getName(), selectedScenario);
					
					disposeChildrenControls(scenariosComposite);

					list = createScenariosList(scenariosComposite);
					
					createScenariosListButtonsComposite();
					
					scenariosComposite.layout();
					addListenersToScenariosCompositeList(scrolledComposite, composite, list, scenarioDetailComposite);
					break;
				}
			}
		});
	}
	
	private void addRevertListener() {
		revert.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					
					scenarioDetailComposite.setEnabled(false);
					scenarioDetailComposite.setEnabled(true);
					
					selectedScenario.setName(selectedScenarioName);
					selectedScenario.setContextList(selectedScenarioTimeSlots);
					
					resetScenarioDetailComposite();
					scenarioDetailComposite.layout();

					addListenersToScenariosCompositeList(scrolledComposite, composite, list, scenarioDetailComposite);
					break;
				}
			}
		});
	}
	
	private void createScenariosListButtonsComposite() {
		buttonsComposite = createTwoColumnsComposite(scenariosComposite);
		
		newScenario = createButton(buttonsComposite, "New");
		addNewListener();
		
		deleteScenario = createButton(buttonsComposite, "Delete");
		addDeleteListener();
		deleteScenario.setEnabled(false);
	}

	private Composite createScenarioDetailComposite(Composite composite) {
		Composite situationDetailComposite = new Composite(composite, SWT.NONE);
        situationDetailComposite.setLayout(new GridLayout(1, true));
        situationDetailComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		return situationDetailComposite;
	}
	
	private Composite createTwoColumnsComposite(Composite composite) {
		Composite twoColumnsComposite = new Composite(composite, SWT.NONE);
        twoColumnsComposite.setLayout(new GridLayout(2, true));
        twoColumnsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		return twoColumnsComposite;
	}
	
	private Composite createBtnSituationComposite(Composite composite) {
		Composite btnSaveComposite = new Composite(composite, SWT.NONE);
		btnSaveComposite.setLayout(new GridLayout(2, true));
		btnSaveComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 2));
		return btnSaveComposite;
	}

	private Composite createScenariosCompositeList(Composite composite) {
		Composite scenariossComposite = new Composite(composite, SWT.NONE);
        scenariossComposite.setLayout(new GridLayout(1, false));
        scenariossComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        

        return scenariossComposite;
	}

	private void createLabel(Composite composite, String textLabel) {
		Label lblDefault = new Label(composite, SWT.NONE);
        lblDefault.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblDefault.setText(textLabel);
	}

	private List createScenariosList(Composite scenarioComposite) {
		
		createLabel(scenariosComposite, "Scenarios:");
		List list = new List(scenarioComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData myGrid = new GridData(220, 380);
		list.setLayoutData(myGrid);
		
		SortedSet<String> orderedScenarios = new TreeSet<String>(scenarios.keySet());

		for (String scenarioName : orderedScenarios) {
			list.add(scenarioName);
		}
		return list;
	}

	private Composite createSplittedComposite(ScrolledComposite scrolledComposite) {
		Composite composite = new Composite(scrolledComposite, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        scrolledComposite.setContent(composite);
        scrolledComposite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return composite;
	}

	private ScrolledComposite createMainComposite(Composite container) {
		ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
		return scrolledComposite;
	}
    
    private void createScenarioDetailGroup(final Composite composite_2, Scenario scenario) {
		Group group = new Group(composite_2, SWT.SHADOW_OUT);
		group.setLayoutData( new GridData(620, 350));
		group.setLayout( new GridLayout( 1, true ) );
		group.setText("Selected scenario");
		try {
			scenario.createScenarioDetails(group, eligibleSituations);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void disposeChildrenControls(final Composite composite_2) {
		for (Control control : composite_2.getChildren()) {
	        control.dispose();
	    }
	}
    
    @Override
    protected void createButtonsForButtonBar(final Composite parent) 
    {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() 
    {
        return new Point(950, 540);
    }
    
	@Override
	protected void okPressed() {
		persistCKTB();
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		cancelModificationsOnCKTB();
		super.cancelPressed();
	}

	private void cancelModificationsOnCKTB() {
		
	}

	private String getCKTBPath() {
		return manager.get(PluginManager.CONTEXT_KNOWLEDGE_TEST_BASE);
	}

	private void persistCKTB() {
		scenarios.remove(MODEL_EXTRACTED_SCENARIO);
		new ScenarioCKTBDAO(getCKTBPath()).Save(scenarios, deletedScenarios);
	}
	
	private Map<String, Scenario> loadCKTB(IFile contextualGraph, IFile conceptualModel){
    	
		//Generate base scenario
    	Scenario baseScenario = generateBaseScenario(contextualGraph);
    	
    	Map<String, Scenario> scenariosCxG = new HashMap<String, Scenario>();
    	scenariosCxG.put(baseScenario.getName(), baseScenario);
    	
    	Map<String, Scenario> scenariosResult = readCKTBFromFile(baseScenario.getCxGFullName());
    	
    	if (!scenariosResult.containsKey(baseScenario.getName()))
    		scenariosResult.put(baseScenario.getName(), baseScenario);

    	return scenariosResult;
    }
	
	private java.util.List<Situation> getSituationsFromModel(IFile contextualGraph) {
		java.util.List<Situation> situationCxG = new ArrayList<Situation>(CxGUtils.getSituations(contextualGraph, getCKTBPath()).values());
		java.util.List<Situation> situationBase = new ArrayList<Situation>(new SituationCKTBDAO(getCKTBPath()).getAll().values());
		
		situationBase.retainAll(situationCxG);
		
		return situationBase;
	}

	private Scenario generateBaseScenario(IFile contextualGraph){
		//Read situations from CxG
		java.util.List<Situation> situationCxG = getSituationsFromModel(contextualGraph);
		
		Scenario baseScenario = new Scenario(MODEL_EXTRACTED_SCENARIO);
		
		CxGFullName = CxGUtils.getCxGFullName(contextualGraph);
		
		baseScenario.setCxGFullName(CxGFullName);
		
		this.eligibleSituations = new HashMap<String, Situation>();
		int i = 0;
		for (Situation situation : situationCxG) {
			TimeSlot timeSlot = new TimeSlot(i++);
			timeSlot.addChildContext(situation);
			baseScenario.addChildContext(timeSlot);
			//Fill eligible situations map
			eligibleSituations.put(situation.getName(), situation);
		}
		return baseScenario;
	}
	
	public Map<String, Scenario> readCKTBFromFile(String CxGFullName) {
		return new ScenarioCKTBDAO(getCKTBPath()).getByCxG(CxGFullName);
	}
}