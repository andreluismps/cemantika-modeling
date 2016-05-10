package org.cemantika.testing.generator.view;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.testing.cktb.dao.ScenarioCKTBDAO;
import org.cemantika.testing.generator.heuristics.GranularityMismatchImprecisionHeuristic;
import org.cemantika.testing.generator.heuristics.SlowSensingOutOfDatenessHeuristic;
import org.cemantika.testing.model.ContextDefectPattern;
import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.TestSuite;
import org.cemantika.testing.util.CxGUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class GenerateTestSuite extends Dialog {
	
	private PluginManager manager;
	private Map<String, Scenario> scenarios;
	
	private Scenario selectedScenario;
	private String selectedScenarioKey;	
	
	private Combo scenarioCombo;
	private Composite composite;
	private Composite scenarioTestCaseDetailComposite;
	private ScrolledComposite scrolledComposite;
	private TestSuite testSuite = new TestSuite();
	

    public GenerateTestSuite(final Shell parent, PluginManager manager, Map<String, Scenario> scenarios, IFile contextualGraph, IFile file) 
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
        
        createLabel(composite, "Base Scenario:");
        
        scenarioCombo = createScenariosCombo(composite);
        
        scenarioTestCaseDetailComposite = createScenarioDetailComposite(composite);
        
        addListenersToScenariosCompositeList(scrolledComposite, composite, scenarioCombo, scenarioTestCaseDetailComposite);

        scenarioCombo.select(0);
        
        return container;
    }

	private void addListenersToScenariosCompositeList(
			final ScrolledComposite scrolledComposite,
			final Composite composite, final Combo combo,
			final Composite scenarioDetailComposite) {
		
		combo.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				selectedScenarioKey = combo.getItem(combo.getSelectionIndex());
				
				selectedScenario = scenarios.get(selectedScenarioKey);
				
				resetScenarioDetailComposite(scenarioDetailComposite);
				
				scrolledComposite.layout(true, true);
                scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
			}
		});
	}
	
	private void resetScenarioDetailComposite(final Composite situationDetailComposite) {
		disposeChildrenControls(situationDetailComposite);
		createScenarioDetailGroup(situationDetailComposite, selectedScenario);
		createBtnSituationComposite(situationDetailComposite);
	}
	
	private Composite createScenarioDetailComposite(Composite composite) {
		Composite situationDetailComposite = new Composite(composite, SWT.NONE);
        situationDetailComposite.setLayout(new GridLayout(1, true));
        situationDetailComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
		return situationDetailComposite;
	}
		
	private Composite createBtnSituationComposite(Composite composite) {
		Composite btnSaveComposite = new Composite(composite, SWT.NONE);
		btnSaveComposite.setLayout(new GridLayout(2, true));
		btnSaveComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 2));
		return btnSaveComposite;
	}

	private void createLabel(Composite composite, String textLabel) {
		Label lblDefault = new Label(composite, SWT.NONE);
        lblDefault.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblDefault.setText(textLabel);
	}

	private Combo createScenariosCombo(Composite scenarioComposite) {
		Combo combo = new Combo(scenarioComposite, SWT.READ_ONLY );
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
		generateTestCases();
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

	private Map<String, Scenario> loadCKTB(IFile contextualGraph, IFile conceptualModel){
		return readCKTBFromFile(CxGUtils.getCxGFullName(contextualGraph));
    }
	
	public Map<String, Scenario> readCKTBFromFile(String CxGFullName) {
		return new ScenarioCKTBDAO(getCKTBPath()).getByCxG(CxGFullName);
	}
	
	private void generateTestCases(){
		if (selectedScenario == null) return;
		testSuite.getTestCases().add(selectedScenario);
		
		PhysicalContext physicalContext = null;
		ContextDefectPattern contextDefectPattern = null;
		String [] parts = null;
		for(String selectedSensorDefectPattern : selectedScenario.getSelectedSensorDefectListData()){
			parts = selectedSensorDefectPattern.split("\\|");
			physicalContext = PhysicalContext.getBySensorName(parts[0].trim());
			contextDefectPattern = ContextDefectPattern.fromString(parts[1].trim());
			switch (contextDefectPattern) {
			case GLANULARITY_MISMATCH_IMPRECISION:
				testSuite.getTestCases().addAll(new GranularityMismatchImprecisionHeuristic(getCKTBPath()).deriveTestCases(selectedScenario, physicalContext, contextDefectPattern));
				break;
			case SLOW_SENSING_OUT_OF_DATENESS:
				testSuite.getTestCases().addAll(new SlowSensingOutOfDatenessHeuristic().deriveTestCases(selectedScenario, physicalContext, contextDefectPattern));
				break;
			default:
				break;
			}
		}
	}

	public void setTestSuite(TestSuite testSuite) {
		this.testSuite = testSuite;
	}

	public TestSuite getTestSuite() {
		return testSuite;
	}
}