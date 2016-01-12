package org.cemantika.testing.cktb.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.PhysicalContext;
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

public class ManageContextKnowledgeTestBase extends Dialog {
	
	private PluginManager manager;
	private Map<String, LogicalContext> logicalContexts;

    public ManageContextKnowledgeTestBase(final Shell parent, PluginManager manager, Map<String, LogicalContext> logicalContexts) 
    {
        super(parent);
        this.logicalContexts = logicalContexts;
        this.manager = manager;
        
		copyFile(new File(manager.get(PluginManager.CONTEXT_KNOWLEDGE_TEST_BASE)), new File(manager.get(PluginManager.CONTEXT_KNOWLEDGE_TEST_BASE) + ".bkp"));
		
    }

    @Override
    protected Control createDialogArea(final Composite parent) 
    {
        final Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout());

        ScrolledComposite scrolledComposite = createMainComposite(container);

        Composite composite = createSplittedComposite(scrolledComposite);

        List list = createLogicalContextsCompositeList(composite);
		
        Composite physicalContextsComposite = createPhysicalContextsComposite(composite);
        
        addListenersToLogicalContextsCompositeList(scrolledComposite, composite, list, physicalContextsComposite);

        return container;
    }

	private void addListenersToLogicalContextsCompositeList(
			final ScrolledComposite scrolledComposite,
			final Composite composite, final List list,
			final Composite physicalContextsComposite) {
		list.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {

				LogicalContext logicalContext = logicalContexts.get(list.getItem(list.getSelectionIndex()));
				
				disposeChildrenControls(physicalContextsComposite);
				
				for (AbstractContext physicalContext : logicalContext.getContextList()) {
					createPhysicalContextGroup(physicalContextsComposite, physicalContext);
				}
				
                scrolledComposite.layout(true, true);
                scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
			}

			public void widgetDefaultSelected(SelectionEvent event) {}
		});
	}

	private Composite createPhysicalContextsComposite(Composite composite) {
		Composite physicalContextsComposite = new Composite(composite, SWT.NONE);
        physicalContextsComposite.setLayout(new GridLayout(1, true));
        physicalContextsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		return physicalContextsComposite;
	}

	private List createLogicalContextsCompositeList(Composite composite) {
		Composite logicalContextsComposite = new Composite(composite, SWT.NONE);
        logicalContextsComposite.setLayout(new GridLayout(1, false));
        logicalContextsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Label lblDefault = new Label(logicalContextsComposite, SWT.NONE);
        lblDefault.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblDefault.setText("Logical Contexts:");

        List list = new List(logicalContextsComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData myGrid = new GridData(250, 380);
		list.setLayoutData(myGrid);
		
		SortedSet<String> orderedLogicalContexts = new TreeSet<String>(logicalContexts.keySet());

		for (String logicalContextName : orderedLogicalContexts) {
			list.add(logicalContextName);
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
    
    private void createPhysicalContextGroup(final Composite composite_2, AbstractContext physicalContext) {
		Group group = createSensorGroup(composite_2, physicalContext.getName());
		try {
			((PhysicalContext)physicalContext).createPhysicalContextDetails(group);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		copyFile(new File(manager.get(PluginManager.CONTEXT_KNOWLEDGE_TEST_BASE) + ".bkp"), new File(manager.get(PluginManager.CONTEXT_KNOWLEDGE_TEST_BASE)));
		
	}

	private void persistCKTB() {

		Gson gson = new Gson();
		String json = gson.toJson(logicalContexts);
		
		try {
			FileWriter writer = new FileWriter(manager.get(PluginManager.CONTEXT_KNOWLEDGE_TEST_BASE));
			writer.write(json);
			writer.close();
			File backup = new File(manager.get(PluginManager.CONTEXT_KNOWLEDGE_TEST_BASE)+".bkp");
			backup.delete();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void copyFile(File source, File dest) {
		InputStream is = null;
		OutputStream os = null;
		try {

			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;

			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			is.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}