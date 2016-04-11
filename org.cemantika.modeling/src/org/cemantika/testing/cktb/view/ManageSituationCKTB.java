package org.cemantika.testing.cktb.view;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.testing.cktb.db.DataBase;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.util.CxGUtils;
import org.cemantika.testing.util.GsonUtils;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ManageSituationCKTB extends Dialog {
	
	private PluginManager manager;
	private Map<String, Situation> situations;
	private Group grpSensor;

    public ManageSituationCKTB(final Shell parent, PluginManager manager, Map<String, Situation> logicalContexts, IFile contextualGraph, IFile file) 
    {
    	
        super(parent);
        this.manager = manager;
        this.situations = loadCKTB(contextualGraph, file);
        //this.setShellStyle(getShellStyle() | SWT.RESIZE);
		
    }
    
    @Override
    protected Control createDialogArea(final Composite parent) 
    {
        final Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout());

        ScrolledComposite scrolledComposite = createMainComposite(container);

        Composite composite = createSplittedComposite(scrolledComposite);

        List list = createSituationsCompositeList(composite);
		
        Composite situationDetailComposite = createSituationDetailComposite(composite);
        
        addListenersToLogicalContextsCompositeList(scrolledComposite, composite, list, situationDetailComposite);

        return container;
    }

	private void addListenersToLogicalContextsCompositeList(
			final ScrolledComposite scrolledComposite,
			final Composite composite, final List list,
			final Composite situationDetailComposite) {
		list.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {

				Situation situation = situations.get(list.getItem(list.getSelectionIndex()));
				
				disposeChildrenControls(situationDetailComposite);
				
				createSituationDetailGroup(situationDetailComposite, situation);
				
				scrolledComposite.layout(true, true);
                scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
			}

			public void widgetDefaultSelected(SelectionEvent event) {}
		});
	}

	private Composite createSituationDetailComposite(Composite composite) {
		Composite situationDetailComposite = new Composite(composite, SWT.NONE);
        situationDetailComposite.setLayout(new GridLayout(1, true));
        situationDetailComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		return situationDetailComposite;
	}

	private List createSituationsCompositeList(Composite composite) {
		Composite situationsComposite = new Composite(composite, SWT.NONE);
        situationsComposite.setLayout(new GridLayout(1, false));
        situationsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Label lblDefault = new Label(situationsComposite, SWT.NONE);
        lblDefault.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblDefault.setText("Situations:");

        List list = new List(situationsComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData myGrid = new GridData(450, 380);
		list.setLayoutData(myGrid);
		
		SortedSet<String> orderedSituations = new TreeSet<String>(situations.keySet());

		for (String situationName : orderedSituations) {
			list.add(situationName);
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
    
    private void createSituationDetailGroup(final Composite composite_2, Situation situation) {
		Group group = new Group(composite_2, SWT.SHADOW_OUT);
		group.setLayoutData( new GridData(450, 380));
		group.setLayout( new GridLayout( 1, true ) );
		group.setText(situation.getName());
		try {
			situation.createLogicalContextDetails(group);
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
        return new Point(950, 520);
    }
    
	@Override
	protected void okPressed() {
		//Needed because last text field don not lose focus 
		grpSensor.setEnabled(false);
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
		
	}
	
	private Map<String, Situation> loadCKTB(IFile contextualGraph, IFile conceptualModel){
    	
    	//read from CxG
    	Map<String, Situation> situationCxG = CxGUtils.getSituations(contextualGraph, getCKTBPath()+".db");
    	/*
    	//TODO add situations with sensor defects
    	situationCxG = CxGUtils.getFaultySituations(situationCxG);
    	
    	//TODO read from db
    	Map<String, Situation> logicalContextsResult = readCKTBFromFile();
    	
    	for (Entry<String, LogicalContext> logicalContextEntry : situationCxG.entrySet()) {
			if (!logicalContextsResult.containsValue(logicalContextEntry.getValue()))
				logicalContextsResult.put(logicalContextEntry.getKey(), logicalContextEntry.getValue());
		}

    	return logicalContextsResult;
    	*/
    	
    	return situationCxG;
    }
	
	public Map<String, LogicalContext> readCKTBFromFile() {

		Map<String, LogicalContext> logicalCKTB = new HashMap<String, LogicalContext>();
		
		String logicalQuery = "SELECT * FROM logicalContext";
		
		LogicalContext logicalContext = null;
		ResultSet logicalRs = DataBase.executeSelect(logicalQuery, DataBase.getConnection(getCKTBPath()+".db"));
		Type type = new TypeToken<LogicalContext>() {}.getType();
		Gson gson = GsonUtils.getGson();
		try {
			while (logicalRs.next()) {
				logicalContext = gson.fromJson(logicalRs.getString("jsonValue"), type);
				logicalContext.setId(logicalRs.getInt("id"));
				logicalCKTB.put(logicalContext.getName(), logicalContext);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return logicalCKTB;
	}
}