package org.cemantika.modeling.form;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.generator.java.JetCemantikaGenerator;
import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.testing.model.Grafo;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.Scenario;
import org.cemantika.testing.model.Situation;
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
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.Split;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
	
	private FormToolkit toolkit;
	private ScrolledForm scrolledForm;
	private FormText behaviorModel;
	private PluginManager manager;
	
	private ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
	private ClassLoader projectClassLoader = getProjectClassLoader();
	
	public static final String ID = ContextTesting.class.getName();
	private static final String TITLE = "Testing";
	
	private static final String TEST_CASE_GENERATION = 
		  "The objective of this task is to generate test cases for context simulators test execution. " +
		  "The inputs to this task are: Context Conceptual Model and its generated code and a Context Behavior Model.\n" +
		  "Generate a test suit for each Context Behavior Model constructed based on identified focus below:";
	
	public ContextTesting(FormEditor editor) {
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
					//generateContextualGraphs(id);
					activator
							.showMessage(
									"The behavioral model does not exist for the selected focus. Please generate them at Context Specification form",
									SWT.ICON_INFORMATION | SWT.OK);
				}
				generateTestSuit(contextualGraph);
				
			}
		}


		private void generateTestSuit(IFile contextualGraph) {
			
	    	
			
			try {
	        	
				Thread.currentThread().setContextClassLoader(projectClassLoader);
	        	
	            KnowledgeBase knowledgeBase = readRule(contextualGraph);
	            StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();
	                        
	            RuleFlowProcess ruleFlowProcess = (RuleFlowProcess)  ksession.getKnowledgeBase().getProcess(contextualGraph.getName().replace(".rf", ""));
	            
	            // C—digo para teste!
	            // DRF Parse OK. Para este passo, precisa do fonte gerado pelo plugin.
	            // Reflection on project classes OK. 
	            // Diagram elements load: OK
	            parseProcess(ruleFlowProcess);
	            
	        } catch ( Throwable t ) {
	            t.printStackTrace();
	        }finally{
	        	Thread.currentThread().setContextClassLoader(originalClassLoader);
	        }
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
	    		if (clazz.equals(class1.getName())){
	    			//o elemento contextual esta associado a qual fonte de contexto?
	    			List<Association> associations = UmlUtils.getAssociations(class1);
	    			for (Association association : associations) {
	    				association.getAppliedStereotypes();
	    				if (UmlUtils.hasStereotype(association, "cemantika_class::AcquisitionAssociation") &&
	    					attribute.equals(UmlUtils.getElementTaggedValue(association, "cemantika_class::AcquisitionAssociation", "element"))){
	    					System.out.println("Contextual Element identified on Node: " + clazz+ "." + attribute);
	    					EList<Type> types = association.getEndTypes();
	    					List<EObject> sensorList = new ArrayList<EObject>();
	    					for (Type type : types) {
	    						Stereotype ster = type.getAppliedStereotype("cemantika_class::ContextSource"); 
								if (ster != null){
									System.out.println("Context Source of Contextual Element.: " + type.getName());
									//a fonte de contexto esta associada a quais APIs?
									List<Association> CSAssociations = type.getAssociations();
									for (Association CSAssociation : CSAssociations) {
										//encontra as classes do endpoint da associacao
										EList<Type> CSEndTypes = CSAssociation.getEndTypes();
										for (Type CSEndType : CSEndTypes) {
											//encontra associacoes da fonte de contexto
											Stereotype CSEndTypeSter = CSEndType.getAppliedStereotype("cemantika_class::ContextSourceAPI");
											//a classe eh uma CSAPI?
											if (CSEndTypeSter != null){
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
									
									
									
									//Quais os sensores da CSAPI?
								
								}
							}
	    				}
	    			
					}
	    		}
			}
	    	return sensors;
		}
		
	}

}
