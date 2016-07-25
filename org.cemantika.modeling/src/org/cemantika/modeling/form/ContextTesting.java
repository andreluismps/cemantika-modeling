package org.cemantika.modeling.form;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.generator.java.JetCemantikaGenerator;
import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.modeling.listener.overview.CreateContextKnowledgeTestBase;
import org.cemantika.modeling.listener.overview.ImportContextKnowledgeTestBase;
import org.cemantika.testing.cktb.view.ManageBaseScenarioCKTB;
import org.cemantika.testing.cktb.view.ManageLogicalContextCKTB;
import org.cemantika.testing.cktb.view.ManageSituationCKTB;
import org.cemantika.testing.generator.view.GenerateTestSuite;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.model.TestSuite;
import org.cemantika.testing.util.GsonUtils;
import org.cemantika.uml.model.Focus;
import org.cemantika.uml.util.UmlUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbench;
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

import com.google.gson.Gson;


public class ContextTesting extends FormPage {
	
	private enum Tab {
		IDENTIFY_LOGICAL_CONTEXTS_TAB, TEST_CASE_GENERATION_TAB, IDENTIFY_SITUATIONS_TAB, GENERATE_BASE_SCENARIOS_TAB;
    }
	
	private FormToolkit toolkit;
	private ScrolledForm scrolledForm;
	private FormText behaviorModel;
	private PluginManager manager;
	private FormText doneImportCKTB;
	private FormEditor editor;
	
	private ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
	
	public static final String ID = ContextTesting.class.getName();
	private static final String TITLE = "Testing";
	
	private static final String IMPORT_CKTB = 
		"Import Context Knowledge Test Base (CKTB) for context data reuse for this project.";
	
	private static final String IDENTIFY_LOGICAL_CONTEXTS = 
		  "The objective of this task is to identify logical contexts in Context Behavior model and add them to CKTB.\n" +
		  "Identify Logical Contexts for each identified focus below:";
	
	private static final String IDENTIFY_SITUATIONS = 
		  "The objective of this task is to identify situations in Context Behavior model and add them to CKTB.\n" +
		  "Identify Situations and its expected behavior for each identified focus below:";
	
	private static final String GENERATE_BASE_SCENARIOS = 
		  "The objective of this task is to generate base scenarios based in Context Behavior model and add them to CKTB.\n" +
		  "Generate base scenarios and its time sequences for each identified focus below:";
	
	private static final String TEST_CASE_GENERATION = 
		  "The objective of this task is to generate test cases for context simulators test execution.\n" +
		  "Generate a test suit for each identified focus below:";
	
	public ContextTesting(FormEditor editor) {
		super(editor, ID, TITLE);
		this.editor = editor;
		this.manager = (PluginManager) editor;
	}	
	
	protected void createFormContent(IManagedForm managedForm) {
		toolkit = managedForm.getToolkit();
		scrolledForm = managedForm.getForm();
		
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 1;

        scrolledForm.getBody().setLayout(layout);
        
        scrolledForm.setText(TITLE);
        
        addImportCKTB();
        
        addIdentifyLogicalContextsInBehaviorModel();
        
        addIdentifySituationsInBehaviorModel();
        
        addGenerateBaseScenarioInBehaviorModel();
        
        addTestCaseGeneration();
        
        registerPhysicalContexts();
	}
	
	private void addImportCKTB() {
		Section importCKTB = CemantikaForm.createSection(toolkit,
				scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Import Context Knowledge Test Base", IMPORT_CKTB);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		importCKTB.setLayoutData(td);

		Composite sectionClient = toolkit.createComposite(importCKTB);

		TableWrapLayout layout = new TableWrapLayout();
		sectionClient.setLayout(layout);
		layout.numColumns = 1;

		Activator plugin = Activator.getDefault();
		IWorkbench workbench = plugin.getWorkbench();
		Shell shell = workbench.getActiveWorkbenchWindow().getShell();

		td = new TableWrapData();
		FormText formText = toolkit.createFormText(sectionClient, true);
		formText.setLayoutData(td);
		formText.addHyperlinkListener(new ImportSectionListener(shell, manager));

		StringBuffer html = new StringBuffer();
		html
			.append("<form>")
			.append("<li><a href=\"Create Context Knowledge Test Base\">Create</a> or <a href=\"Open Context Knowledge Test Base\">Open</a> a Context Knowledge Test Base to this project.</li>")
			.append("</form>");
		formText.setText(html.toString(), true, true);
		
		createDoneImportCKTB(sectionClient);

		importCKTB.setClient(sectionClient);

	}
	
	private void addIdentifyLogicalContextsInBehaviorModel() {
		Section importCKTB = CemantikaForm.createSection(
				toolkit, scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Identify Logical Contexts in Behavior Model",
				IDENTIFY_LOGICAL_CONTEXTS);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		importCKTB.setLayoutData(td);

		Composite sectionClient = toolkit.createComposite(importCKTB);
		sectionClient.setLayout(new TableWrapLayout());
		
		this.behaviorModel = toolkit.createFormText(sectionClient, true);

		td = new TableWrapData(TableWrapData.FILL_GRAB);
		behaviorModel.setLayoutData(td);
		StringBuffer html = behaviorModels();
		behaviorModel.setImage("artifact", Activator.getDefault().getImageRegistry().get(Activator.CEMANTIKA_ARTIFACT));

		behaviorModel.setText(html.toString(), true, true);
		behaviorModel.addHyperlinkListener(new BehaviorModelListener(Tab.IDENTIFY_LOGICAL_CONTEXTS_TAB));

		importCKTB.setClient(sectionClient);

	}
	
	private void addIdentifySituationsInBehaviorModel() {
		Section importSituationsCKTB = CemantikaForm.createSection(
				toolkit, scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Identify Situations in Behavior Model",
				IDENTIFY_SITUATIONS);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		importSituationsCKTB.setLayoutData(td);

		Composite sectionClient = toolkit.createComposite(importSituationsCKTB);
		sectionClient.setLayout(new TableWrapLayout());
		
		this.behaviorModel = toolkit.createFormText(sectionClient, true);

		td = new TableWrapData(TableWrapData.FILL_GRAB);
		behaviorModel.setLayoutData(td);
		StringBuffer html = behaviorModels();
		behaviorModel.setImage("artifact", Activator.getDefault().getImageRegistry().get(Activator.CEMANTIKA_ARTIFACT));

		behaviorModel.setText(html.toString(), true, true);
		behaviorModel.addHyperlinkListener(new BehaviorModelListener(Tab.IDENTIFY_SITUATIONS_TAB));

		importSituationsCKTB.setClient(sectionClient);

	}
	
	private void addGenerateBaseScenarioInBehaviorModel(){
		Section importBaseScenariosCKTB = CemantikaForm.createSection(
				toolkit, scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Generate Base Scenarios",
				GENERATE_BASE_SCENARIOS);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		importBaseScenariosCKTB.setLayoutData(td);

		Composite sectionClient = toolkit.createComposite(importBaseScenariosCKTB);
		sectionClient.setLayout(new TableWrapLayout());
		
		this.behaviorModel = toolkit.createFormText(sectionClient, true);

		td = new TableWrapData(TableWrapData.FILL_GRAB);
		behaviorModel.setLayoutData(td);
		StringBuffer html = behaviorModels();
		behaviorModel.setImage("artifact", Activator.getDefault().getImageRegistry().get(Activator.CEMANTIKA_ARTIFACT));

		behaviorModel.setText(html.toString(), true, true);
		behaviorModel.addHyperlinkListener(new BehaviorModelListener(Tab.GENERATE_BASE_SCENARIOS_TAB));

		importBaseScenariosCKTB.setClient(sectionClient);
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
		behaviorModel.addHyperlinkListener(new BehaviorModelListener(Tab.TEST_CASE_GENERATION_TAB));
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
	
	public ClassLoader getProjectClassLoader() {
		IProject project = Activator.getDefault().getActiveProject();
		IJavaProject javaProject = JavaCore.create(project);
		String[] classPathEntries;
		List<URL> urlList = new ArrayList<URL>();
		URLClassLoader classLoader = null;
		try {
			classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
			for (int i = 0; i < classPathEntries.length; i++) {
				String entry = classPathEntries[i];
				IPath path = new Path(entry);
				URL url = path.toFile().toURI().toURL();
				urlList.add(url);
			}
			URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
			classLoader = new URLClassLoader(urls, originalClassLoader);
		} catch (CoreException e) {
			e.printStackTrace();
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return classLoader;
	}

	private class BehaviorModelListener extends HyperlinkAdapter {

		private Activator activator;
		private IFile file;
		private Tab tab;
		private Map<String, LogicalContext> logicalContexts;
		private Map<String, Situation> situations;
		private Map<String, Scenario> scenarios;

		public BehaviorModelListener(Tab tab) {
			this.tab = tab;
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
			
			IFile contextualGraph = getContextualGraph(ref);
			switch (tab) {
			case IDENTIFY_LOGICAL_CONTEXTS_TAB:
				identifyLogicalContexts(contextualGraph);
				break;
				
			case IDENTIFY_SITUATIONS_TAB:
				identifySituations(contextualGraph);
				break;
			case GENERATE_BASE_SCENARIOS_TAB:
				generateBaseScenarios(contextualGraph);
				break;
			case TEST_CASE_GENERATION_TAB:
				generateTestSuit(contextualGraph);
				break;
			default:
				break;
			}
		}		

		private IFile getContextualGraph(String ref) {
			String id = "contextual_graph_" + ref;
			IProject project = Activator.getDefault().getActiveProject();
			String contextPath = "src/"
					+ JetCemantikaGenerator.CONTEXTUAL_GRAPH_PACKAGE
							.replace('.', '/') + "/" + id + ".rf";
			IFile contextualGraph = project.getFile(contextPath);

			boolean exists = contextualGraph.exists();
			if (!exists) {
				//generateContextualGraphs(id);
				activator
						.showMessage(
								"The behavioral model does not exist for the selected focus. Please generate them at Context Specification form",
								SWT.ICON_INFORMATION | SWT.OK);
			}
			return contextualGraph;
		}
		
		
		private void identifyLogicalContexts(IFile contextualGraph) {
			Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			Dialog dialog = new ManageLogicalContextCKTB(shell, manager, logicalContexts, contextualGraph, file);
			dialog.open();
		}
		
		private void identifySituations(IFile contextualGraph) {
			Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			Dialog dialog = new ManageSituationCKTB(shell, manager, situations, contextualGraph, file);
			dialog.open();
		}
		
		private void generateBaseScenarios(IFile contextualGraph) {
			Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			Dialog dialog = new ManageBaseScenarioCKTB(shell, manager, scenarios, contextualGraph, file);
			dialog.open();
		}

		//Use compiled CxG from drools requires created java classes - Direct parse of XML is used instead.
		private void generateTestSuit(IFile contextualGraph) {
			
			Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			Dialog dialog = new GenerateTestSuite(shell, manager, scenarios, contextualGraph, file);
			dialog.open();
			
			TestSuite testSuite = ((GenerateTestSuite) dialog).getTestSuite();
			
			if (testSuite.getTestCases().isEmpty()) return;
			
			exportTestSuiteAsJSON(shell, testSuite);
	    }

		private void exportTestSuiteAsJSON(Shell shell, TestSuite testSuite) {
			FileDialog fileDialog = new FileDialog( shell, SWT.SAVE);
			fileDialog.setText("Save Test Case as");
			fileDialog.setFileName(".json");
	        String[] filterExt = { "*.json"};
	        fileDialog.setFilterExtensions(filterExt);
	        
	        //TODO refactor gson use
	        Gson gson = GsonUtils.getGson();
			String json = gson.toJson(testSuite);
			
			String jsonFile = null;
			
			try {
				
				jsonFile = fileDialog.open();
				
				FileWriter writer = new FileWriter(jsonFile);
				writer.write(json);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class GotoPage extends HyperlinkAdapter {

		@Override
		public void linkActivated(HyperlinkEvent e) {
			editor.setActivePage(ContextSpecification.ID);
		}

	}
	
	private class ImportSectionListener extends HyperlinkAdapter {

		private Shell shell;
		private PluginManager manager;

		public ImportSectionListener(Shell shell, PluginManager manager) {
			this.shell = shell;
			this.manager = manager;
		}

		@Override
		public void linkActivated(HyperlinkEvent e) {
			String label = (String) e.getHref();
			if (label.equals("Create Context Knowledge Test Base")) {
				new CreateContextKnowledgeTestBase(shell, manager, PluginManager.CONTEXT_KNOWLEDGE_TEST_BASE, "Create Context Knowledge Test Base", "cktb").handleEvent(null);
			} else if (label.equals("Open Context Knowledge Test Base")) {
				new ImportContextKnowledgeTestBase(shell, manager, PluginManager.CONTEXT_KNOWLEDGE_TEST_BASE, "Import Context Knowledge Test Base", "cktb").handleEvent(null);
			}
			
		}
	}
	
	public void updateImportSection() {
		doneImportCKTB.setVisible(manager.alreadyImportContextKnowledgeTestBase());
	}
	
	public void createDoneImportCKTB(Composite sectionClient){
		doneImportCKTB = toolkit.createFormText(sectionClient, true);
		TableWrapData td = new TableWrapData();
		doneImportCKTB.setLayoutData(td);
		doneImportCKTB.addHyperlinkListener(new GotoPage());
		StringBuffer html = new StringBuffer();
		html = new StringBuffer();
		html
			.append("<form>")
			.append("<p><span color=\"done\">Congratulations!</span> You have already connected to a Context Knoledge Test Base.</p>")
			.append("</form>");
		doneImportCKTB.setColor("done", ColorConstants.red);
		doneImportCKTB.setText(html.toString(), true, true);
		updateImportSection();
	}
	
	private static void registerPhysicalContexts() {
		
		try {
			java.lang.Class.forName("org.cemantika.testing.contextSource.CPU").newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
