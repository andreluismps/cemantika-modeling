package org.cemantika.modeling.form;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.uml.util.UmlUtils;
import org.cemantika.uml.util.UmlUtils.ProfileType;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


public class ContextManagement extends FormPage {
	
	private FormToolkit toolkit;
	private ScrolledForm scrolledForm;
	private FormText contextSources;
	private PluginManager manager;
	
	public static final String ID = ContextManagement.class.getName();
	private static final String TITLE = "Context Management";
	
	private static final String SPECIFY_CONTEXT_ACQUISITION = 
		  "The objective of this task is to specify, given the contextual elements, their context sources and the associations between them."
		+ "The outcome is an updated version of the Context Conceptual Model.";
	
	public ContextManagement(FormEditor editor) {
		super(editor, ID, TITLE);
		this.manager = (PluginManager) editor;
	}	
	
	protected void createFormContent(IManagedForm managedForm) {
		toolkit = managedForm.getToolkit();
		scrolledForm = managedForm.getForm();
		
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 2;

        scrolledForm.getBody().setLayout(layout);
        
        scrolledForm.setText(TITLE);
        
        addSpecifyContextAcquisition();		
	}
	
	private void addSpecifyContextAcquisition() {
		Section specifyContextAcquisition = CemantikaForm.createSection(
				toolkit, scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Specify Context Acquisition",
				SPECIFY_CONTEXT_ACQUISITION);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		specifyContextAcquisition.setLayoutData(td);

		Composite sectionClient = toolkit
				.createComposite(specifyContextAcquisition);
		sectionClient.setLayout(new TableWrapLayout());

		this.contextSources = toolkit.createFormText(sectionClient, true);

		td = new TableWrapData(TableWrapData.FILL_GRAB);
		contextSources.setLayoutData(td);
		String html = "<form><p><img href=\"artifact\"/><a href=\"CE\">Identify Context Sources and their Associations with Contextual Elements</a>.</p></form>";

		contextSources.setImage("artifact", Activator.getDefault()
				.getImageRegistry().get(Activator.CEMANTIKA_ARTIFACT));

		contextSources.setText(html, true, true);
		contextSources.addHyperlinkListener(new ContextualEntityListener());
		specifyContextAcquisition.setClient(sectionClient);

	}

	private class ContextualEntityListener extends HyperlinkAdapter {

		public void linkActivated(HyperlinkEvent event) {
			Shell shell = Activator.getDefault().getShell();
			try {
				IFile conceptualDiagram = Activator.getDefault().open(
						manager.get(PluginManager.CONCEPTUAL_DIAGRAM));
				IFile conceptualModel = Activator.getDefault().open(
						manager.get(PluginManager.CONCEPTUAL_MODEL));
				UmlUtils uml = new UmlUtils();
				uml.applyCemantikaProfile(conceptualModel,
						ProfileType.CLASS_DIAGRAM);
				Activator.getDefault().openEditor(conceptualDiagram);
			} catch (Exception e) {
				MessageDialog.openError(shell, "Open Conceptual Model Error",
						"Exception while opening conceptual model editor: "
								+ e.getMessage());
			}
		}
	}

}
