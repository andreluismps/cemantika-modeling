package org.cemantika.modeling.form;

import java.util.ArrayList;
import java.util.List;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.contextual.graph.ContextualGraph;
import org.cemantika.modeling.generator.GenerationException;
import org.cemantika.modeling.generator.ICemantikaGenerator;
import org.cemantika.modeling.generator.java.JetCemantikaGenerator;
import org.cemantika.modeling.generator.model.Attribute;
import org.cemantika.modeling.generator.model.CemantikaClass;
import org.cemantika.modeling.generator.model.ContextualElementAttribute;
import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.modeling.view.dialog.ContextBehaviorDialog;
import org.cemantika.uml.model.Focus;
import org.cemantika.uml.util.UmlUtils;
import org.cemantika.uml.util.UmlUtils.ProfileType;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbench;
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
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;

public class ContextSpecification extends FormPage {

	public static final String ID = ContextSpecification.class.getName();
	private static final String IDENTIFY_FOCUS = "Identify Focus is the first task. "
			+ "It's purpose is to identify CSS foci and generate a Enriched Use Case Model. Follow the steps below to accomplish it:";
	private static final String IDENTIFY_BEHAVIOR_VARIATIONS = "The objective of this task is to identify, given a focus, which variations are"
			+ " expected in the CSS Behavior, and which factors affect these variations. Define CSS variations for each Focus below:";
	private FormToolkit toolkit;
	private ScrolledForm scrolledForm;
	private static final String TITLE = "Context Specification";
	private static final String IDENTIFY_CONTEXTUAL_ENTITIES = "The objective of this task is to identify, given a focus, contextual entities and contextual elements. The outcome is the Context Conceptual Model.";
	private static final String DESIGN_CONTEXT_BEHAVIOR_MODEL = "This activity has the objective to produce the Context Behavior Model"
			+ " corresponding to the identified focus, as well as to design the associations"
			+ " between the CEs and the behavior variations.";
	private FormText doneText;
	private PluginManager manager;
	private FormText behaviorVariations;
	private FormText contextualEntities;
	private FormText behaviorModel;

	public ContextSpecification(FormEditor editor) {
		super(editor, ID, TITLE);
		this.manager = (PluginManager) editor;
	}

	protected void createFormContent(IManagedForm managedForm) {
		toolkit = managedForm.getToolkit();
		scrolledForm = managedForm.getForm();

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;

		scrolledForm.getBody().setLayout(layout);

		scrolledForm.setText(TITLE);

		addIdentifyFocus();
		addIdentifyBehaviorVariations();
		addIdentifyContextualEntities();
		addDesignContextBehaviorModel();
	}

	private void addDesignContextBehaviorModel() {
		Section designContextBehaviorModel = CemantikaForm.createSection(
				toolkit, scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Design Context Behavior Model", DESIGN_CONTEXT_BEHAVIOR_MODEL);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		designContextBehaviorModel.setLayoutData(td);

		Composite sectionClient = toolkit
				.createComposite(designContextBehaviorModel);
		sectionClient.setLayout(new TableWrapLayout());

		this.behaviorModel = toolkit.createFormText(sectionClient, true);

		td = new TableWrapData(TableWrapData.FILL_GRAB);
		behaviorModel.setLayoutData(td);
		StringBuffer html = behaviorModels();
		behaviorModel.setImage("artifact", Activator.getDefault()
				.getImageRegistry().get(Activator.CEMANTIKA_ARTIFACT));

		behaviorModel.setText(html.toString(), true, true);
		behaviorModel.addHyperlinkListener(new BehaviorModelListener());
		designContextBehaviorModel.setClient(sectionClient);
	}

	private StringBuffer behaviorModels() {
		StringBuffer html = new StringBuffer(
				"<form><li>The first step of this task is to <img href=\"artifact\"/><a href=\"generate\">Generate Contextual Entities and Contextual Elements from Conceptual Model</a>.</li>"
						+ "<p>After that, construct the behavior model for each identified focus below:</p>");

		String foci = listFoci(false, "<li>", "</li>");
		html.append(foci);
		html.append("</form>");
		return html;
	}

	private void addIdentifyContextualEntities() {
		Section identifyContextualEntities = CemantikaForm.createSection(
				toolkit, scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Identify Contextual Entities and Contextual Elements",
				IDENTIFY_CONTEXTUAL_ENTITIES);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		identifyContextualEntities.setLayoutData(td);

		Composite sectionClient = toolkit
				.createComposite(identifyContextualEntities);
		sectionClient.setLayout(new TableWrapLayout());

		this.contextualEntities = toolkit.createFormText(sectionClient, true);

		td = new TableWrapData(TableWrapData.FILL_GRAB);
		contextualEntities.setLayoutData(td);
		String html = "<form><p><img href=\"artifact\"/><a href=\"CE\">Identify Contextual Entities and Contextual Elements</a>.</p></form>";

		contextualEntities.setImage("artifact", Activator.getDefault()
				.getImageRegistry().get(Activator.CEMANTIKA_ARTIFACT));

		contextualEntities.setText(html, true, true);
		contextualEntities.addHyperlinkListener(new ContextualEntityListener());
		identifyContextualEntities.setClient(sectionClient);
	}

	private void addIdentifyBehaviorVariations() {
		Section identifyBehaviorVariations = CemantikaForm.createSection(
				toolkit, scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Identify Behavior Variations", IDENTIFY_BEHAVIOR_VARIATIONS);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		identifyBehaviorVariations.setLayoutData(td);

		Composite sectionClient = toolkit
				.createComposite(identifyBehaviorVariations);
		sectionClient.setLayout(new TableWrapLayout());

		this.behaviorVariations = toolkit.createFormText(sectionClient, true);

		td = new TableWrapData(TableWrapData.FILL_GRAB);
		behaviorVariations.setLayoutData(td);
		String html = listFoci(true, "<p>", "</p>");

		behaviorVariations.setImage("artifact", Activator.getDefault()
				.getImageRegistry().get(Activator.CEMANTIKA_ARTIFACT));

		behaviorVariations.setText(html, true, true);
		behaviorVariations
				.addHyperlinkListener(new BehaviorVariationListener());
		identifyBehaviorVariations.setClient(sectionClient);
	}

	private void addIdentifyFocus() {
		Section identifyFocus = CemantikaForm.createSection(toolkit,
				scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED, "Identify Focus",
				IDENTIFY_FOCUS);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		identifyFocus.setLayoutData(td);

		Composite sectionClient = toolkit.createComposite(identifyFocus);
		sectionClient.setLayout(new TableWrapLayout());

		this.doneText = toolkit.createFormText(sectionClient, true);

		td = new TableWrapData();
		doneText.setLayoutData(td);
		StringBuffer html = new StringBuffer();
		html = new StringBuffer();
		html
				.append("<form>")
				.append(
						"<p><img href=\"artifact\"/><a href=\"Foci\">Define CSS Foci</a>.</p>")
				.append("</form>");
		doneText.setImage("artifact", Activator.getDefault().getImageRegistry()
				.get(Activator.CEMANTIKA_ARTIFACT));
		doneText.addHyperlinkListener(new OpenUseCase());

		doneText.setText(html.toString(), true, true);

		identifyFocus.setClient(sectionClient);
		updateFocusSection();

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

	public void updateIdentifyBehaviorVariations() {
		if (this.manager.alreadyImportUseCase()) {
			this
					.updateIdentifyBehaviorVariations(listFoci(true, "<p>",
							"</p>"));
			this.updateBehaviorModels();
		}
	}

	private void updateBehaviorModels() {
		behaviorModel.setText(behaviorModels().toString(), true, true);
	}

	public void updateIdentifyBehaviorVariations(String html) {
		if (behaviorVariations != null && !behaviorVariations.isDisposed())
			behaviorVariations.setText(html, true, true);
		if (behaviorModel != null && !behaviorModel.isDisposed())
			this.updateBehaviorModels();
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

	public void updateFocusSection() {
		if (doneText != null) {
			doneText.setVisible(manager.alreadyImportUseCase());
		}
	}

	private class OpenUseCase extends HyperlinkAdapter {

		@Override
		public void linkActivated(HyperlinkEvent event) {
			Shell shell = Activator.getDefault().getShell();
			try {
				IFile useCase = Activator.getDefault().open(
						manager.get(PluginManager.USE_CASE_DIAGRAM));
				IFile useCaseModel = Activator.getDefault().open(
						manager.get(PluginManager.USE_CASE_MODEL));
				UmlUtils uml = new UmlUtils();
				uml.applyCemantikaProfile(useCaseModel, ProfileType.USE_CASE);
				Activator.getDefault().openEditor(useCase);
			} catch (Exception e) {
				MessageDialog.openError(shell, "Open Use Case Error",
						"Exception while opening use case editor: "
								+ e.getMessage());
			}
		}
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

	private class BehaviorVariationListener extends HyperlinkAdapter {

		@SuppressWarnings("unchecked")
		@Override
		public void linkActivated(HyperlinkEvent e) {
			Activator activator = Activator.getDefault();
			IFile file = activator.open(
					manager.get(PluginManager.USE_CASE_MODEL));
			try {
				if (activator.hasErrors(file)) {
					activator
							.showMessage(
									"Enriched Use Case Model has errors. Verify Problems View and correct them",
									SWT.ICON_ERROR | SWT.OK);
					activator.showView(IPageLayout.ID_PROBLEM_VIEW);
					return;
				}
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			
			UmlUtils uml = new UmlUtils();
			Object[] values = uml.showFoci(file);
			List<Focus> foci = (List<Focus>) values[0];
			org.eclipse.uml2.uml.Package useCase = (Package) values[1];
			Activator plugin = Activator.getDefault();
			IWorkbench workbench = plugin.getWorkbench();
			Shell shell = workbench.getActiveWorkbenchWindow().getShell();
			String indexStr = (String) e.getHref();
			int index = Integer.parseInt(indexStr);
			Focus foco = foci.get(index);

			ContextBehaviorDialog dialog = new ContextBehaviorDialog(shell,
					foco.getBehaviorVariation());
			if (dialog.open() != InputDialog.OK) {
				return;
			} else {
				foco.setBehavior(dialog.getBehavior());
				uml.save(useCase, file);
			}
		}

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
				generateContextualEntities();
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

		private void generateContextualEntities() {
			List<CemantikaClass> clazzes = createClassesFromUml();
			try {
				for (CemantikaClass clazz : clazzes) {
					ICemantikaGenerator generator = new JetCemantikaGenerator(
							clazz, "ContextualEntity.javajet",
							JetCemantikaGenerator.CEMANTIKA_MODEL_PACKAGE,
							new NullProgressMonitor());
					generator.generate();
				}
			} catch (GenerationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		private List<CemantikaClass> createClassesFromUml() {
			List<org.eclipse.uml2.uml.Class> classes = UmlUtils
					.getClasses(package_);
			List<CemantikaClass> clazzes = new ArrayList<CemantikaClass>();
			for (org.eclipse.uml2.uml.Class clazz : classes) {
				CemantikaClass cemantikaClass = new CemantikaClass();
				cemantikaClass.setClassName(clazz.getName());
				if (clazz.getGeneralizations().size() > 0) {
					org.eclipse.uml2.uml.Class superClass = (Class) clazz
							.getGeneralizations().get(0).getGeneral();
					cemantikaClass.setSuperClass(superClass.getName());
				}
				cemantikaClass.setPackageName("cemantika.model");
				cemantikaClass
						.addImport(
								"import org.cemantika.metamodel.structure.ContextType")
						.addImport(
								"import org.cemantika.metamodel.structure.ContextualElement")
						.addImport(
								"import org.cemantika.metamodel.structure.ContextualEntity");

				if (UmlUtils.hasStereotype(clazz,
						UmlUtils.CONTEXT_ENTITY_STEREOTYPE)) {
					cemantikaClass.setAnnotation("@ContextualEntity");
				}

				Attribute attribute = null;
				for (Property property : UmlUtils.getProperties(clazz)) {
					if (UmlUtils.hasStereotype(property,
							UmlUtils.CONTEXT_ELEMENT_STEREOTYPE)) {
						String contextElementType = UmlUtils.getTaggedValue(
								property, UmlUtils.CONTEXT_ELEMENT_STEREOTYPE,
								"type");
						attribute = new ContextualElementAttribute(UmlUtils
								.getPropertyType(property), property.getName(),
								"ContextType." + contextElementType);
					} else {
						attribute = new Attribute(UmlUtils
								.getPropertyType(property), property.getName());

					}
					cemantikaClass.addAttribute(attribute);
				}

				for (Association association : UmlUtils.getAssociations(clazz)) {
					List<Property> properties = association.getMemberEnds();
					for (Property property : properties) {
						if (UmlUtils.isAutoRelationShip(association)) {
							addProperty(association, property, cemantikaClass);
						} else {
							if (!property.getType().equals(clazz))
								addProperty(association, property,
										cemantikaClass);
						}
					}
				}

				clazzes.add(cemantikaClass);
			}

			return clazzes;
		}
	}

	private void addProperty(Association association, Property property,
			CemantikaClass cemantikaClass) {
		boolean multiple = false;
		if (property.getUpper() == -1) {
			multiple = true;
		}
		String name = property.getName();
		Attribute attribute = null;
		if (name != null && !name.equals("")) {
			if (UmlUtils.hasStereotype(association,
					UmlUtils.CONTEXT_ELEMENT_STEREOTYPE)) {
				String contextElementType = UmlUtils.getTaggedValue(
						association, UmlUtils.CONTEXT_ELEMENT_STEREOTYPE,
						"type");
				attribute = new ContextualElementAttribute(UmlUtils
						.getPropertyType(property), property.getName(),
						"ContextType." + contextElementType);
			} else {
				attribute = new Attribute(UmlUtils.getPropertyType(property),
						property.getName(), multiple);

			}
			cemantikaClass.addAttribute(attribute);
		}
	}

}
