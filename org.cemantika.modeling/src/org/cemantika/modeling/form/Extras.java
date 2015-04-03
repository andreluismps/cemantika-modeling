package org.cemantika.modeling.form;

import java.util.List;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.contextual.graph.ContextualGraph;
import org.cemantika.modeling.generator.GenerationException;
import org.cemantika.modeling.generator.ICemantikaGenerator;
import org.cemantika.modeling.generator.java.JetCemantikaGenerator;
import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.uml.model.Focus;
import org.cemantika.uml.util.UmlUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
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


public class Extras extends FormPage {
	
	private FormToolkit toolkit;
	private ScrolledForm scrolledForm;
	private FormText behaviorModel;
	private PluginManager manager;
	
	public static final String ID = Extras.class.getName();
	private static final String TITLE = "Extras";
	
	private static final String TEST_CASE_GENERATION = 
		  "The objective of this task is to generate test cases for context simulators test execution. " +
		  "The inputs to this task are: Context Conceptual Model and its generated code and a Context Behavior Model.\n" +
		  "Generate a test suit for each Context Behavior Model constructed based on identified focus below:";
	
	public Extras(FormEditor editor) {
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
        
        addTestCaseGeneration();		
	}
	
	private void addTestCaseGeneration() {
		Section testCaseGeneration = CemantikaForm.createSection(
				toolkit, scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Test Case Generation",
				TEST_CASE_GENERATION);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		testCaseGeneration.setLayoutData(td);

		Composite sectionClient = toolkit
				.createComposite(testCaseGeneration);
		sectionClient.setLayout(new TableWrapLayout());

		this.behaviorModel = toolkit.createFormText(sectionClient, true);

		td = new TableWrapData(TableWrapData.FILL_GRAB);
		behaviorModel.setLayoutData(td);
		StringBuffer html = behaviorModels();
		behaviorModel.setImage("artifact", Activator.getDefault()
				.getImageRegistry().get(Activator.CEMANTIKA_ARTIFACT));

		behaviorModel.setText(html.toString(), true, true);
		behaviorModel.addHyperlinkListener(new BehaviorModelListener());
		testCaseGeneration.setClient(sectionClient);

	}
	
	private StringBuffer behaviorModels() {
		StringBuffer html = new StringBuffer("<form>");

		String foci = listFoci(false, "<li>", "</li>");
		html.append(foci);
		html.append("</form>");
		return html;
	}
	
	@SuppressWarnings("unchecked")
	public String listFoci(boolean withForm, String startTag, String endTag) {
		if (this.manager.alreadyImportUseCase()) {
			UmlUtils uml = new UmlUtils();
			IFile useCase = Activator.getDefault().open(
					this.manager.get(PluginManager.USE_CASE_MODEL));
			List<Focus> foci = (List<Focus>) uml.showFoci(useCase)[0];
			String fociList = this.fociForm(foci, startTag, endTag);
			if (withForm) {
				fociList = "<form>" + fociList + "</form>";
			}
			return fociList;
		}
		return "<form></form>";

	}
	
	public String fociForm(List<Focus> foci, String startTag, String endTag) {
		StringBuffer html = new StringBuffer();
		int i = 0;
		for (Focus focus : foci) {
			html.append(startTag).append("<img href=\"artifact\"/><a href=\"")
					.append(i++).append("\">").append(focus.toString()).append(
							"</a>").append(endTag);
		}
		return html.toString();
	}
	
	private class BehaviorModelListener extends HyperlinkAdapter {

		private Activator activator;
		private org.eclipse.uml2.uml.Package package_;
		private IFile file;

		public BehaviorModelListener() {
			this.activator = Activator.getDefault();
			file = Activator.getDefault().open(
					manager.get(PluginManager.CONCEPTUAL_MODEL));
		}

		@Override
		public void linkActivated(HyperlinkEvent e) {
			String ref = (String) e.getHref();
			try {
				if (activator.hasErrors(this.file)) {
					activator
							.showMessage(
									"Context Conceptual Model has errors. Verify Problems View and correct them",
									SWT.ICON_ERROR | SWT.OK);
					activator.showView(IPageLayout.ID_PROBLEM_VIEW);
					return;
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
			if (ref.equals("generate")) {
				UmlUtils uml = new UmlUtils();
				this.package_ = uml.load(file);
				activator
						.showMessage(
								"Contextual Entities were generated at cemantika.model package",
								SWT.ICON_INFORMATION | SWT.OK);
			} else {
				String id = "contextual_graph_" + ref;
				IProject project = Activator.getDefault().getActiveProject();
				String contextPath = "src/"
						+ JetCemantikaGenerator.CONTEXTUAL_GRAPH_PACKAGE
								.replace('.', '/') + "/" + id + ".rf";
				IFile contextualGraph = project.getFile(contextPath);

				boolean exists = contextualGraph.exists();
				if (!exists) {
					generateContextualGraphs(id);
					activator
							.showMessage(
									"Contextual Entities were generated at cemantika.contextual.graph package",
									SWT.ICON_INFORMATION | SWT.OK);
				}

				try {
					Activator.getDefault().openEditor(contextualGraph);
				} catch (PartInitException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// TODO open
			}
		}

		private void generateContextualGraphs(String id) {
			ContextualGraph contextualGraph = new ContextualGraph();
			contextualGraph.setName(id);
			contextualGraph.setId(id);
			ICemantikaGenerator generator = new JetCemantikaGenerator(
					contextualGraph, "ContextualGraph.rfjet",
					JetCemantikaGenerator.CONTEXTUAL_GRAPH_PACKAGE,
					new NullProgressMonitor());
			// FIXME save in metamodel
			try {
				generator.generate();
			} catch (GenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		
	}

}
