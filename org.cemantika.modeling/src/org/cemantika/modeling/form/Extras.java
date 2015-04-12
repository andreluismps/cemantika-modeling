package org.cemantika.modeling.form;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.cemantika.metamodel.structure.ContextualElement;
import org.cemantika.metamodel.structure.UpdateType;
import org.cemantika.modeling.Activator;
import org.cemantika.modeling.generator.java.JetCemantikaGenerator;
import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.uml.model.Focus;
import org.cemantika.uml.util.UmlUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.process.Node;
import org.drools.io.ResourceFactory;
import org.drools.process.core.Context;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.runtime.StatefulKnowledgeSession;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPageLayout;
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
					//generateContextualGraphs(id);
					activator
							.showMessage(
									"The behavioral model does not exist for the selected focus. Please generate them at Context Specification form",
									SWT.ICON_INFORMATION | SWT.OK);
				}

				try {
					//Activator.getDefault()Extras.openEditor(contextualGraph);
					generateTestCases(contextualGraph);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// TODO open
			}
		}


		private void generateTestCases(IFile contextualGraph) {
	    	ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
			
			try {
	        	
		    	addCurrentProjectClassPathEntriesToCurrentClassLoader(parentClassLoader);
	        	
	            KnowledgeBase knowledgeBase = readRule(contextualGraph);
	            StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();
	                        
	            RuleFlowProcess ruleFlowProcess = (RuleFlowProcess)  ksession.getKnowledgeBase().getProcess(contextualGraph.getName().replace(".rf", ""));
	            
	            // C—digo para teste!
	            // DRF Parse OK. Para este passo, precisa do fonte gerado pelo plugin.
	            // Reflection on project classes OK. 
	            // Diagram elements load: OK
	            testParse(ruleFlowProcess);
	            
	        } catch ( Throwable t ) {
	            t.printStackTrace();
	        }finally{
	        	restoreOriginalClassLoader(parentClassLoader);
	        }
	    }
		

		private void addCurrentProjectClassPathEntriesToCurrentClassLoader(ClassLoader parentClassLoader)
				throws CoreException, MalformedURLException {

			IProject project = Activator.getDefault().getActiveProject();
			IJavaProject javaProject = JavaCore.create(project);
			String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);

			List<URL> urlList = new ArrayList<URL>();
			for (int i = 0; i < classPathEntries.length; i++) {
				String entry = classPathEntries[i];
				IPath path = new Path(entry);
				URL url = path.toFile().toURI().toURL();
				urlList.add(url);
			}

			Thread current = Thread.currentThread();

			URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
			URLClassLoader classLoader = new URLClassLoader(urls, parentClassLoader);

			current.setContextClassLoader(classLoader);

		}
		
		private void restoreOriginalClassLoader(ClassLoader classLoader){
			Thread.currentThread().setContextClassLoader(classLoader);
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
	    
	    private void testParse(RuleFlowProcess ruleFlowProcess) throws ClassNotFoundException{
	    	for (Node node : ruleFlowProcess.getNodes()) {
				if (node instanceof org.drools.workflow.core.node.Split){
					org.drools.workflow.core.node.Split split = (org.drools.workflow.core.node.Split) node;
					List<org.drools.definition.process.Connection> conns = split.getOutgoingConnections("DROOLS_DEFAULT");
					for (org.drools.definition.process.Connection conn : conns) {
						org.drools.workflow.core.Constraint constraint = split.getConstraint(conn);
						//TODO - Colocar o nome no contexto l—gico
						// Este contexto —gico vai receber os dados de todos os sensores.
			            String condition = constraint.getConstraint();
			            System.out.println("Contextual Node Constraint...........: " + condition);
			            condition.toLowerCase();
			            condition = condition.replace("return ", "").replace(".equals", "  ")
			            					 .replace("!", "    ").replace("&&", "  ")
			            					 .replace("||", " ").replace('(', '\0')
			            					 .replace(')', '\0').replace(';', '\0')
			            					 .replace(".get", ".").replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","")
			            					 .trim().replaceAll(" +", " ");
			            List<Context>contexts = ruleFlowProcess.getContexts("VariableScope");
			            VariableScope varscope = (VariableScope) contexts.get(0);
			            List<org.drools.process.core.context.variable.Variable> vars = varscope.getVariables();
			            
			            String [] statements = condition.split(" ");
			            for (String statement : statements) {
			            	statement = statement.trim();
			            	String [] statmentElements = statement.split("\\.");
			            	String statementVariable = statmentElements[0];
			            	Class clazz = null;
			            	org.drools.process.core.context.variable.Variable variable = null;
			            	for (org.drools.process.core.context.variable.Variable var : vars) {
								if (var.getName().equals(statementVariable)) {
									variable = var;
									clazz = Thread.currentThread().getContextClassLoader().loadClass(var.getType().getStringType());
								}
							}
			            	
			            	int i = 0;
			            	Field field = null;
			            	List<UpdateType> updateTypes = new ArrayList<UpdateType>();
			            	for (String statmentElement : statmentElements) {
			            		statmentElement = Introspector.decapitalize(statmentElement);
			            		if (i != 0) {
			            			Field[] fields = clazz.getDeclaredFields();
			            			for (Field field2 : fields) {
										if (field2.getName().equals(statmentElement)){
											field = field2;
										}
									}
			            			if (field.getAnnotation(ContextualElement.class) != null){
			            				testParseDiagram(clazz.getSimpleName(), field.getName());
			            			}
								}
								i++;
							}
						}
			            
			            System.out.println("Logical Context......................: " + constraint.getName() + "\n\n");
					}
				}
			}
	    }
	    // TODO - Retirar parse do diagrama. O codigo anotado como context source deve estar sendo geraodo 
	    private void testParseDiagram(String clazz, String attribute) {
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
	    				if (UmlUtils.hasStereotype(association, "cemantika_class::AcquisitionAssociation")){
	    					System.out.println("Contextual Element identified on Node: " + clazz+ "." + attribute);
	    					EList<Type> types = association.getEndTypes();
	    					List<EObject> sensores = new ArrayList<EObject>();
	    					for (Type type : types) {
	    						Stereotype ster = type.getAppliedStereotype("cemantika_class::PhysicalSensorContextSource"); 
								if (ster != null){
									System.out.println("Context Source of Contextual Element.: " + type.getName());
									//a fonte de contexto esta associada a quais sensores?
									sensores = (List<EObject>) type.getValue(ster, "type");
									if (!sensores.isEmpty()){
										System.out.print("Sensors from Context Source..........: ");
									}
									for (EObject eObject : sensores) {
										System.out.print(eObject.toString() + " ");
									}
									System.out.println("");
								}
							}
	    				}
	    			
					}
	    		}
				
			}
		}
		
	}

}
