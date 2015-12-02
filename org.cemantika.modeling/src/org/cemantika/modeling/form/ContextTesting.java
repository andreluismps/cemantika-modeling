package org.cemantika.modeling.form;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.generator.java.JetCemantikaGenerator;
import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.modeling.listener.overview.CreateContextKnowledgeTestBase;
import org.cemantika.modeling.listener.overview.ImportContextKnowledgeTestBase;
import org.cemantika.testing.generator.TestCaseGenerator;
import org.cemantika.testing.model.Grafo;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
import org.cemantika.testing.util.CxGUtils;
import org.cemantika.testing.util.XMLOperator;
import org.cemantika.uml.model.Focus;
import org.cemantika.uml.util.UmlUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.io.ResourceFactory;
import org.drools.process.core.Context;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.Split;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
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
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;


public class ContextTesting extends FormPage {
	
	private enum Tab {
		IDENTIFY_LOGICAL_CONTEXTS_TAB, TEST_CASE_GENERATION_TAB;
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
		  "The inputs to this task are: Context Conceptual Model and its generated code and a Context Behavior Model.\n" +
		  "Identify Logical Contexts in Behavior Model constructed based on identified focus below:";
	
	private static final String TEST_CASE_GENERATION = 
		  "The objective of this task is to generate test cases for context simulators test execution.\n" +
		  "The inputs to this task are: Context Knowledge Test Base, Context Conceptual Model, and a Context Behavior Model.\n" +
		  "Generate a test suit for each Context Behavior Model constructed based on identified focus below:";
	
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
        
        addTestCaseGeneration();		
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

		Composite sectionClient = toolkit
				.createComposite(importCKTB);
		sectionClient.setLayout(new TableWrapLayout());
		
		this.behaviorModel = toolkit.createFormText(sectionClient, true);

		td = new TableWrapData(TableWrapData.FILL_GRAB);
		behaviorModel.setLayoutData(td);
		StringBuffer html = behaviorModels();
		behaviorModel.setImage("artifact", Activator.getDefault()
				.getImageRegistry().get(Activator.CEMANTIKA_ARTIFACT));

		behaviorModel.setText(html.toString(), true, true);
		behaviorModel.addHyperlinkListener(new BehaviorModelListener(Tab.IDENTIFY_LOGICAL_CONTEXTS_TAB));

		importCKTB.setClient(sectionClient);

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
		private org.eclipse.uml2.uml.Package package_;
		private IFile file;
		private Tab tab;

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
			
			Set<LogicalContext> logicalContexts = CxGUtils.getLogicalContexts(contextualGraph, file);
			
			Set<LogicalContext> cktbData = loadCKTB();
			
			cktbData.addAll(logicalContexts);
			
			showCKTBEditor();
			
		}
		
		
		private Set<LogicalContext> loadCKTB() {
			// TODO load CKTB file data
			return null;
		}

		private void showCKTBEditor() {
			// TODO Show CKTB Editor to fill new values
			
		}

		//TODO refazer scenarios e hierarquia.
		//Compilar o grafo contextual requer classes criadas na aplicacoo modelada
		private void generateTestSuit(IFile contextualGraph) {
			
			//Show predef options (can be CKTB items in absence within CxG)
			
			//Create base scenario
			
			//Derive cases
			
			IFile testCaseInput = new CxGUtils().readCxG(contextualGraph, file);			
			
			Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			
			Scenario scenario = new TestCaseGenerator().testCaseGeneration(new File(testCaseInput.getLocationURI()));
			
			FileDialog dialog = new FileDialog( shell, SWT.SAVE);
	        dialog.setText("Save Test Case as");
	        dialog.setFileName(".xml");
	        String[] filterExt = { "*.xml"};
	        dialog.setFilterExtensions(filterExt);
	        XMLOperator.generateXMLFile(scenario, new File(dialog.open()));
			
	    }
		
		//Scenario = all graph's paths (scenarios list).
	    //Situation = all logical contexts in a path (name: )
	    //Logical context = set of sensors in a contextual node (name: tem perigo)
		//Incluir deffect patterns para context sources
		private void parseProcess(RuleFlowProcess ruleFlowProcess) throws ClassNotFoundException{
	    	
			List<ArrayList<String>> caminhos = listPaths(ruleFlowProcess);
			List<Split> splits = getSplitNodes(ruleFlowProcess);
	        //encontra caminhos
	        
	        
	        //Get a Scenario
			
			Activator plugin = Activator.getDefault();
			IWorkbench workbench = plugin.getWorkbench();
			Shell shell = workbench.getActiveWorkbenchWindow().getShell();
			
	        Scenario scenario = new Scenario();
	        scenario.setName("Scenario #1");
	        scenario.setSituations(getSituations(splits, caminhos, ruleFlowProcess));
	        scenario.getSituations();
	        FileDialog dialog = new FileDialog( shell, SWT.SAVE);
	        dialog.setText("Save Test Case as");
	        dialog.setFileName(".xml");
	        String[] filterExt = { "*.xml"};
	        dialog.setFilterExtensions(filterExt);
	        XMLOperator.generateXMLFile(scenario, new File(dialog.open()));
	    	
	    }		

	    private KnowledgeBase readRule(IFile contextualGraph) throws Exception {
	   	    
  	    	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
  	        
  	        kbuilder.add( ResourceFactory.newFileResource(contextualGraph.getRawLocation().makeAbsolute().toFile()), ResourceType.DRF );

  	        if (kbuilder.hasErrors()){
  	        	throw new RuntimeException(kbuilder.getErrors().toString());
  	        }
  	        
  	        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
  	        
  	        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

  	        return kbase;
	    	
	    }
	    
	    private Grafo fillGrafo(RuleFlowProcess ruleFlowProcess){
	    	Grafo grafo = new Grafo();
	    	
	    	for (Node node : ruleFlowProcess.getNodes()) {	    		
	    		List<org.drools.definition.process.Connection> conns = node.getOutgoingConnections("DROOLS_DEFAULT");
	    		for (Connection connection : conns) {
	    			grafo.adicionaAresta(""+connection.getFrom().getId(), ""+ connection.getTo().getId());
				}
			}
	    	return grafo;
	    }
	    
	    private List<ArrayList<String>> listPaths(RuleFlowProcess ruleFlowProcess){
	    	Grafo grafo = fillGrafo(ruleFlowProcess);
	    	Node start = ruleFlowProcess.getStart();
	    	Node end = getEndNode(ruleFlowProcess);	
	    	
	    	return grafo.listarCaminhos(grafo, ""+start.getId(), ""+end.getId());
	    }
	    
	    private Node getEndNode(RuleFlowProcess ruleFlowProcess) {
	    	for (Node node : ruleFlowProcess.getNodes()) {
	    		if (node instanceof EndNode) {
	    			return node;
	    		}
	    	}
			return null;
		}
	    
	    private List<Split> getSplitNodes(RuleFlowProcess ruleFlowProcess) {
	    	List<Split> splits = new ArrayList<Split>();
	    	for (Node node : ruleFlowProcess.getNodes()) {
	    		if (node instanceof Split) {
					splits.add((Split) node);
				}
	    	}
	    	return splits;
		}

		private List<Situation> getSituations(List<Split> splits, List<ArrayList<String>> caminhos, RuleFlowProcess ruleFlowProcess) {
			List<Situation> situations = new ArrayList<Situation>();
			int i = 0;
	        for(ArrayList<String> path : caminhos){
	        	Situation situation = getSituation(splits, path, ruleFlowProcess);
	        	situation.setName("Situation #" + ++i);
	        	situations.add(situation);
		    }
			return situations;
		}

		private Situation getSituation(List<Split> splits, ArrayList<String> path, RuleFlowProcess ruleFlowProcess) {
			int i = 0, pos = 0;
			Situation situation = new Situation();
			for (String node : path) {
			    if (pos != 0){
			    	for (Split split : splits) {
			    		if (split.getId() == Integer.parseInt(path.get(pos))){
			    			List<org.drools.definition.process.Connection> conns = split.getOutgoingConnections("DROOLS_DEFAULT");
							for (org.drools.definition.process.Connection conn : conns) {
								if (conn.getTo().getId() == Integer.parseInt(node)){
									situation.getLogicalContexts().add(getLogicalContext(split.getConstraint(conn), ruleFlowProcess));
								}
								
							}
						}
					}
			    	pos = 0;
			    }
			    for (Split noContextual : splits) {
					if (noContextual.getId() == Integer.parseInt(node)){
						pos = i;
						break;
					}
				}
			    i++;
			}
			return situation;
		}

		private LogicalContext getLogicalContext(Constraint constraint, RuleFlowProcess ruleFlowProcess) {
			return new LogicalContext(constraint.getName(), getSensors(constraint, ruleFlowProcess));
		}

		private List<String> getSensors(Constraint constraint, RuleFlowProcess ruleFlowProcess) {
            
			List<String> sensors = new ArrayList<String>();
			
            List<Context> contexts = ruleFlowProcess.getContexts("VariableScope");
            VariableScope varscope = (VariableScope) contexts.get(0);
            List<Variable> vars = varscope.getVariables();
            
            String condition = parseConstraint(constraint);
            String [] statements = condition.split(" ");
            for (String statement : statements) {
            	statement = statement.trim();
            	String [] statmentElements = statement.split("\\.");
            	Class<?> clazz = getClazz(vars, statmentElements);
            	if (clazz != null)
            		sensors.addAll(getSensorsFromContextualElement(clazz.getSimpleName(), getContextualElement(statmentElements, clazz).getName()));
			}
			return sensors;
		}

		private Field getContextualElement(String[] statmentElements, Class<?> clazz) {
			int i = 0;
			Field contextualElement = null;
			for (String statmentElement : statmentElements) {
				statmentElement = Introspector.decapitalize(statmentElement);
				if (i != 0) {
					Field[] fields = clazz.getDeclaredFields();
					for (Field clazzField : fields) {
						if (clazzField.getName().equals(statmentElement)){
							contextualElement = clazzField;
						}
					}
				}
				i++;
			}
			return contextualElement;
		}

		private Class<?> getClazz(List<Variable> vars, String[] statmentElements) {
			String statementVariable = statmentElements[0];
			Class<?> clazz = null;
			for (Variable var : vars) {
				if (var.getName().equals(statementVariable)) {
					try {
						clazz = Thread.currentThread().getContextClassLoader().loadClass(var.getType().getStringType());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			return clazz;
		}

		private String parseConstraint(Constraint constraint) {
			String condition = constraint.getConstraint();
            condition.toLowerCase();
            condition = condition.replace("return ", "").replace(".equals", "  ")
            					 .replace("!", "    ").replace("&&", "  ")
            					 .replace("||", " ").replace('(', '\0')
            					 .replace(')', '\0').replace(';', '\0')
            					 .replace("'", "").replace("\"", "")
            					 .replace(".get", ".").replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","")
            					 .trim().replaceAll(" +", " ");
			return condition;
		}

		// TODO - Retirar parse do diagrama. O codigo anotado como context source deve estar sendo geraodo 
	    private List<String> getSensorsFromContextualElement(String clazz, String attribute) {
	    	List<String>  sensors = new ArrayList<String>();	
	    	UmlUtils uml = new UmlUtils();
			this.package_ = uml.load(file);
	    	List<org.eclipse.uml2.uml.Class> classes = UmlUtils.getClasses(package_);
	    	for (org.eclipse.uml2.uml.Class class1 : classes) {
	    		//obtem a classe do elemento contextual
	    		if (!clazz.equals(class1.getName())){
	    			continue;
	    		}
	    	
    			//o elemento contextual esta associado a qual fonte de contexto?
    			List<Association> associations = UmlUtils.getAssociations(class1);
    			for (Association association : associations) {
    				association.getAppliedStereotypes();
    				if (!(UmlUtils.hasStereotype(association, UmlUtils.ACQUISITION_ASSOCIATION_STEREOTYPE) &&
    					attribute.equals(UmlUtils.getElementTaggedValue(association, UmlUtils.ACQUISITION_ASSOCIATION_STEREOTYPE, "element")))){
    					continue;
    				}
					System.out.println("Contextual Element identified on Node: " + clazz+ "." + attribute);
					EList<Type> types = association.getEndTypes();
					List<EObject> sensorList = new ArrayList<EObject>();
					for (Type type : types) {
						Stereotype ster = type.getAppliedStereotype(UmlUtils.CONTEXT_SOURCE_STEREOTYPE); 
						if (ster == null){
							continue;
						}
						System.out.println("Context Source of Contextual Element.: " + type.getName());
						//a fonte de contexto esta associada a quais APIs?
						List<Association> CSAssociations = type.getAssociations();
						for (Association CSAssociation : CSAssociations) {
							//encontra as classes do endpoint da associacao
							EList<Type> CSEndTypes = CSAssociation.getEndTypes();
							for (Type CSEndType : CSEndTypes) {
								//encontra associacoes da fonte de contexto
								Stereotype CSEndTypeSter = CSEndType.getAppliedStereotype(UmlUtils.CONTEXT_SOURCE_API_STEREOTYPE);
								//a classe eh uma CSAPI?
								if (CSEndTypeSter == null){
									continue;
								}
								System.out.println("Context Source API ................:" + CSEndType.getName());
								sensorList = (List<EObject>) CSEndType.getValue(CSEndTypeSter, "sensors");
								if (!sensorList.isEmpty()){
									System.out.print("Sensors from Context Source..........: ");
								}
								for (EObject eObject : sensorList) {
									sensors.add(eObject.toString());
									System.out.print(eObject.toString() + " ");
								}
								System.out.println("");
								
							}
						}
					}
				}
	    		
			}
	    	return sensors;
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
	
}
