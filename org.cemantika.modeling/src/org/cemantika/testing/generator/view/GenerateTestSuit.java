package org.cemantika.testing.generator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.testing.cktb.dao.ScenarioCKTBDAO;
import org.cemantika.testing.cktb.dao.SituationCKTBDAO;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.model.TimeSlot;
import org.cemantika.testing.util.CxGUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class GenerateTestSuit extends Dialog {
	
	private PluginManager manager;
	private Map<String, Scenario> scenarios;
	private Map<String, Situation> eligibleSituations;
	
	private Button apply;
	private Button revert;
	
	private Scenario selectedScenario;
	private String selectedScenarioKey;
	private String selectedScenarioName;
	
	
	private Combo scenarioCombo;
	private Composite composite;
	private Composite scenariosComposite;
	private Composite scenarioTestCaseDetailComposite;
	private ScrolledComposite scrolledComposite;
	

    public GenerateTestSuit(final Shell parent, PluginManager manager, Map<String, Scenario> scenarios, IFile contextualGraph, IFile file) 
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
        
        getShell().setText("Generate Test Cases from Base Scenario ");
        
        container.setLayout(new FillLayout());

        scrolledComposite = createMainComposite(container);
        
        composite = createSplittedComposite(scrolledComposite);
        
        createLabel(composite, "Scenario:");
        
        scenarioCombo = createScenariosCombo(composite);

        /*
        
        composite = createSplittedComposite(scrolledComposite);
        
        scenariosComposite = createScenariosCompositeList(composite);
        
        createLabel(scenariosComposite, "Scenarios:");

        list = createScenariosList(scenariosComposite);
        
        Composite buttonsComposite = createTwoColumnsComposite(scenariosComposite);
        
        createButton(buttonsComposite, "New");
        
        createButton(buttonsComposite, "Delete");
		*/
        scenarioTestCaseDetailComposite = createScenarioDetailComposite(composite);
        
        addListenersToScenariosCompositeList(scrolledComposite, composite, scenarioCombo, scenarioTestCaseDetailComposite);

        return container;
    }

	private void addListenersToScenariosCompositeList(
			final ScrolledComposite scrolledComposite,
			final Composite composite, final Combo combo,
			final Composite scenarioDetailComposite) {
		combo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {

				selectedScenarioKey = combo.getItem(combo.getSelectionIndex());
				
				selectedScenario = scenarios.get(selectedScenarioKey);

				selectedScenarioName = selectedScenario.getName();
				
				resetScenarioDetailComposite(scenarioDetailComposite);
				
				scrolledComposite.layout(true, true);
                scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
			}

			public void widgetDefaultSelected(SelectionEvent event) {}
		});
	}
	
	private void resetScenarioDetailComposite(final Composite situationDetailComposite) {
		disposeChildrenControls(situationDetailComposite);
		
		createScenarioDetailGroup(situationDetailComposite, selectedScenario);
		
		Composite btnSituationComposite = createBtnSituationComposite(situationDetailComposite);
		
	}
	
	private Button createButton(Composite composite, String label) {
		Button btn = new Button(composite, SWT.PUSH);
		btn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btn.setText(label);
		return btn;
	}
	
	private Combo createCombo(Composite composite) {
		Combo cmb = new Combo(composite, SWT.PUSH);
		cmb.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		//cmb.setText(label);
		return cmb;
	}
	
	private void addApplyListener() {
		apply.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					//Needed because last text field don not lose focus				
					scenarioTestCaseDetailComposite.setEnabled(false);
					scenarioTestCaseDetailComposite.setEnabled(true);
					
					scenarios.remove(selectedScenarioKey);
					scenarios.put(selectedScenario.getName(), selectedScenario);
					
					disposeChildrenControls(scenariosComposite);
					createLabel(scenariosComposite, "Scenarios:");
					//list = createScenariosList(scenariosComposite);
					scenariosComposite.layout();
					addListenersToScenariosCompositeList(scrolledComposite, composite, scenarioCombo, scenarioTestCaseDetailComposite);
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
					
					selectedScenario.setName(selectedScenarioName);
					//selectedScenario.setExpectedBehavior(selectedSituationExpectedBehavior);
					
					resetScenarioDetailComposite(scenarioTestCaseDetailComposite);
					scenarioTestCaseDetailComposite.layout();
					break;
				}
			}
		});
	}

	private Composite createScenarioDetailComposite(Composite composite) {
		Composite situationDetailComposite = new Composite(composite, SWT.NONE);
        situationDetailComposite.setLayout(new GridLayout(1, true));
        situationDetailComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
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

	private Combo createScenariosCombo(Composite scenarioComposite) {
		Combo combo = new Combo(scenarioComposite, SWT.READ_ONLY | SWT.SIMPLE | SWT.DROP_DOWN);
		GridData myGrid = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		combo.setLayoutData(myGrid);
		
		SortedSet<String> orderedScenarios = new TreeSet<String>(scenarios.keySet());

		for (String scenarioName : orderedScenarios) {
			combo.add(scenarioName);
		}
		return combo;
	}

	private Composite createSplittedComposite(ScrolledComposite scrolledComposite) {
		Composite composite = new Composite(scrolledComposite, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
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
		group.setLayoutData( new GridData(820, 350));
		group.setLayout( new GridLayout( 1, true ) );
		
		try {
			scenario.createTestCaseDetails(group);
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
        createButton(parent, IDialogConstants.OK_ID, "Generate", true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() 
    {
        return new Point(880, 530);
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
		//new ScenarioCKTBDAO(getCKTBPath()+".db").Save(scenarios);
	}
	
	private Map<String, Scenario> loadCKTB(IFile contextualGraph, IFile conceptualModel){
    	
		
		
		//Generate base scenario
    	Scenario baseScenario = generateBaseScenario(contextualGraph);
    	
    	Map<String, Scenario> scenariosCxG = new HashMap<String, Scenario>();
    	scenariosCxG.put(baseScenario.getName(), baseScenario);
    	
    	//read from CxG
    	//Map<String, Scenario> scenariosCxG = CxGUtils.getSituations(contextualGraph, getCKTBPath()+".db");
    	
    	
    	//Map<String, Scenario> scenariosResult = readCKTBFromFile();
    	/*
    	for (Entry<String, Situation> situationEntry : scenariosCxG.entrySet()) {
			if (!scenariosResult.containsValue(situationEntry.getValue()))
				scenariosResult.put(situationEntry.getKey(), situationEntry.getValue());
		}
		*/

    	//return scenariosResult;
    	
    	return scenariosCxG;
    }
	
	private java.util.List<Situation> getSituationsFromModel(IFile contextualGraph) {
		java.util.List<Situation> situationCxG = new ArrayList<Situation>(CxGUtils.getSituations(contextualGraph, getCKTBPath()+".db").values());
		java.util.List<Situation> situationBase = new ArrayList<Situation>(new SituationCKTBDAO(getCKTBPath()+".db").getAll().values());
		
		situationBase.retainAll(situationCxG);
		
		return situationBase;
	}

	private Scenario generateBaseScenario(IFile contextualGraph){
		//Read situations from CxG
		java.util.List<Situation> situationCxG = getSituationsFromModel(contextualGraph);
		
		Scenario baseScenario = new Scenario("CxG based Scenario");
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
	
	public Map<String, Situation> readCKTBFromFile() {
		return new SituationCKTBDAO(getCKTBPath()+".db").getAll();
	}
}