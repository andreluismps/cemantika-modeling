package org.cemantika.testing.cktb.view;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.LogicalContext;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ManageContextKnowledgeTestBase extends Dialog {
	
	private Map<String, LogicalContext> logicalContexts;

    public ManageContextKnowledgeTestBase(final Shell parent, Map<String, LogicalContext> logicalContexts) 
    {
        super(parent);
        this.logicalContexts = logicalContexts;
    }

    @Override
    protected Control createDialogArea(final Composite parent) 
    {
        final Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout());

        final ScrolledComposite scrolledComposite = createMainComposite(container);

        final Composite composite = createSplittedComposite(scrolledComposite);

        final List list = createLogicalContextsCompositeList(composite);
		
        final Composite physicalContextsComposite = createPhysicalContextsComposite(composite);
        
        addListenersToLogicalContextsCompositeList(scrolledComposite, composite, list, physicalContextsComposite);

        return container;
    }

	private void addListenersToLogicalContextsCompositeList(
			final ScrolledComposite scrolledComposite,
			final Composite composite, final List list,
			final Composite physicalContextsComposite) {
		list.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				int index = list.getSelectionIndex();
				
				LogicalContext logicalContext = logicalContexts.get(list.getItem(index));
				
				System.out.println(list.getItem(index));
				
				disposeChildrenControls(physicalContextsComposite);
				
				for (AbstractContext physicalContext : logicalContext.getContextList()) {
					createPhysicalContextGroup(physicalContextsComposite, physicalContext);
				}
				
                scrolledComposite.layout(true, true);
                scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				int[] selections = list.getSelectionIndices();
				String outText = "";
				for (int loopIndex = 0; loopIndex < selections.length; loopIndex++)
					outText += selections[loopIndex] + " ";
				System.out.println("You selected: " + outText);
			}
		});
	}

	private Composite createPhysicalContextsComposite(final Composite composite) {
		final Composite physicalContextsComposite = new Composite(composite, SWT.NONE);
        physicalContextsComposite.setLayout(new GridLayout(1, true));
        physicalContextsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		return physicalContextsComposite;
	}

	private List createLogicalContextsCompositeList(final Composite composite) {
		final Composite logicalContextsComposite = new Composite(composite, SWT.NONE);
        logicalContextsComposite.setLayout(new GridLayout(1, false));
        logicalContextsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        final Label lblDefault = new Label(logicalContextsComposite, SWT.NONE);
        lblDefault.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblDefault.setText("Logical Contexts:");

        final List list = new List(logicalContextsComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData myGrid = new GridData(250, 380);
		list.setLayoutData(myGrid);
		
		SortedSet<String> orderedLogicalContexts = new TreeSet<String>(logicalContexts.keySet());

		for (String logicalContextName : orderedLogicalContexts) {
			list.add(logicalContextName);
		}
		return list;
	}

	private Composite createSplittedComposite(
			final ScrolledComposite scrolledComposite) {
		final Composite composite = new Composite(scrolledComposite, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        scrolledComposite.setContent(composite);
        scrolledComposite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return composite;
	}

	private ScrolledComposite createMainComposite(final Composite container) {
		final ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
		return scrolledComposite;
	}
    
    private void createPhysicalContextGroup(final Composite composite_2, AbstractContext physicalContext) {
		Group group = createSensorGroup(composite_2, physicalContext.getName());
		
		// for  Physical context group, create elements inside group
		Object physicalElement = null;
//		switch (physicalContext.getType()) {
//		case BOOLEAN:
			createSensorDataCheckField(group, physicalElement);
//			break;

//		default:
			createSensorDataTextField(group, physicalElement);
			createSensorDataTextField(group, physicalElement);
//			break;
//		}
		
	}
    
    private void createSensorDataCheckField(Group group, Object physicalElement) {
		Button check = new Button(group, SWT.CHECK);
        check.setText("TODO - physicalElement ckeck");
		check.setSelection(true);
        check.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
	}

	private void createSensorDataTextField(Group group, Object physicalElement) {
		Label label = new Label(group, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label.setText("TODO - physicalElement Field");

        Text text = new Text(group, SWT.NONE);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1 ));
        text.setText("TODO - physicalElement Field Value");
	}
	
	private void disposeChildrenControls(final Composite composite_2) {
		for (Control control : composite_2.getChildren()) {
	        control.dispose();
	    }
	}
    
    public Group createSensorGroup(final Composite composite_2, String name) {
		Group group = new Group(composite_2, SWT.SHADOW_OUT);
		group.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );
		group.setLayout( new GridLayout( 2, true ) );
		group.setText(name);
		return group;
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
        return new Point(700, 520);
    }

	
	/*
	int APPLY_ID = 2048;
	private Text txtFirstName;
	  private Text lastNameText;



	public ManageContextKnowledgeTestBase(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
		final Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    GridLayout layout = new GridLayout(2, false);
	    container.setLayout(layout);
		
		final List list = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		GridData myGrid = new GridData(GridData.FILL_BOTH);
		list.setLayoutData(myGrid);
		list.setBounds(0, 0, 200, 200);
		for (int loopIndex = 0; loopIndex < 50; loopIndex++) {
			list.add("Battery Low: " + loopIndex);
		}

		list.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				int[] selections = list.getSelectionIndices();
				String outText = "";
				for (int loopIndex = 0; loopIndex < selections.length; loopIndex++)
					outText += selections[loopIndex] + " ";
				System.out.println("You selected: " + outText);
				//Composite container = new Composite(area, SWT.NONE);
				createFirstName(container);
			    createLastName(container);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				int[] selections = list.getSelectionIndices();
				String outText = "";
				for (int loopIndex = 0; loopIndex < selections.length; loopIndex++)
					outText += selections[loopIndex] + " ";
				System.out.println("You selected: " + outText);
			}
		});
				  

		createButton(container, APPLY_ID, "Apply", true);
		Button button = new Button(container, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		button.setText("Press me");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Pressed");
			}
		});

		return container;
	}

	@Override
	protected void okPressed() {
		System.out.println("OK");
		super.okPressed();
	}

	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Manage Context Knowledge Test Base");
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		if (buttonId == APPLY_ID) {
			System.out.println("Aplly");
		}

	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
	
	private void createFirstName(Composite container) {
	    Label lbtFirstName = new Label(container, SWT.NONE);
	    lbtFirstName.setText("First Name");

	    GridData dataFirstName = new GridData();
	    dataFirstName.grabExcessHorizontalSpace = true;
	    dataFirstName.horizontalAlignment = GridData.FILL;

	    txtFirstName = new Text(container, SWT.BORDER);
	    txtFirstName.setLayoutData(dataFirstName);
	  }
	  
	  private void createLastName(Composite container) {
	    Label lbtLastName = new Label(container, SWT.NONE);
	    lbtLastName.setText("Last Name");
	    
	    GridData dataLastName = new GridData();
	    dataLastName.grabExcessHorizontalSpace = true;
	    dataLastName.horizontalAlignment = GridData.FILL;
	    lastNameText = new Text(container, SWT.BORDER);
	    lastNameText.setLayoutData(dataLastName);
	  }
*/
}