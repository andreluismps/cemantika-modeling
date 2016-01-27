package org.cemantika.testing.util;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.cemantika.testing.cxg.xsd.Connection;
import org.cemantika.testing.cxg.xsd.Connections;
import org.cemantika.testing.cxg.xsd.Constraint;
import org.cemantika.testing.cxg.xsd.Constraints;
import org.cemantika.testing.cxg.xsd.End;
import org.cemantika.testing.cxg.xsd.Header;
import org.cemantika.testing.cxg.xsd.Nodes;
import org.cemantika.testing.cxg.xsd.Process;
import org.cemantika.testing.cxg.xsd.Split;
import org.cemantika.testing.cxg.xsd.Start;
import org.cemantika.testing.cxg.xsd.Type;
import org.cemantika.testing.cxg.xsd.Variable;
import org.cemantika.testing.cxg.xsd.Variables;
import org.cemantika.testing.model.AbstractContext;
import org.cemantika.testing.model.ContextDefectPattern;
import org.cemantika.testing.model.CxG;
import org.cemantika.testing.model.Graph;
import org.cemantika.testing.model.LogicalContext;
import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.uml.util.UmlUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;

public class CxGUtils {
	public static Map<String, LogicalContext> getLogicalContexts(IFile contextualGraph, IFile conceptualModel) {
		
		Process extractedCxG = extractProcessFromCxG(contextualGraph);

        CxG internalCxG = new CxG();
        
        List<ArrayList<String>> caminhos = getPathsFromCxG(extractedCxG, internalCxG);
        
        //obtem os sensores
        fillInternalModel(internalCxG, caminhos, conceptualModel);		
		//return dominiosGrafo;
        System.out.println("");
        
        return getLogicalContexts(internalCxG, caminhos, conceptualModel);
	}

	public static List<ArrayList<String>> getPathsFromCxG(IFile conceptualModel, IFile contextualGraph, Process extractedCxG, CxG internalCxG){
		extractedCxG = extractProcessFromCxG(contextualGraph);
        
        return getPathsFromCxG(extractedCxG, internalCxG);
	}

	private static List<ArrayList<String>> getPathsFromCxG(Process p, CxG internalCxG) {
		for (Object nodesOrConnectionsOrHeaders : p.getHeaderOrNodesOrConnections()) {
        	
        	if (nodesOrConnectionsOrHeaders instanceof Header){
        		
        		internalCxG.setVariables(extractVariables(nodesOrConnectionsOrHeaders));
        	
        	}else if (nodesOrConnectionsOrHeaders instanceof Nodes){
        		
        		internalCxG = extractNodes(nodesOrConnectionsOrHeaders, internalCxG);
        	
        	}else if (nodesOrConnectionsOrHeaders instanceof Connections) {
        		
				internalCxG.setConnections((Connections) nodesOrConnectionsOrHeaders);
				
			}
		}
        
        //popula grafo
        Graph grafo = createGrafo(internalCxG);
        
        //encontra caminhos
        List<ArrayList<String>> caminhos = grafo.listarCaminhos(grafo, internalCxG.getStart().getId(), internalCxG.getEnd().getId());
		return caminhos;
	}


	private static Map<String, LogicalContext> getLogicalContexts(CxG internalCxG, List<ArrayList<String>> caminhos, IFile conceptualModel) {
		int i = 0;
        int pos = 0;
        int ctCount = 0;
        Constraints constraints;
        Map<String, LogicalContext> logicalContexts = new HashMap<String, LogicalContext>();
        LogicalContext logicalContext;
        
        for(ArrayList<String> path : caminhos){
        	i = 0;
        	ctCount++;
	        for (String node : path) {
	            if (pos != 0){
	            	for (Split noContextual :internalCxG.getContextualNodes()) {
	            		if (!noContextual.getId().equals(path.get(pos))){
	            			continue;
	            		}
            			for (Object o : noContextual.getMetaDataOrConstraints()) {
            				if(!(o instanceof Constraints)){
            					continue;
            				}
        					constraints = (Constraints) o;
        					for (Constraint constraint : constraints.getConstraint()) {
        						if(constraint.getToNodeId().equals(node)){
        							logicalContext = new LogicalContext(constraint.getName());
        							
        							Set<PhysicalContext> physicalContexts = getPhysicalContexts(constraint.getValue(), conceptualModel, internalCxG);
        							for (PhysicalContext physicalContext : physicalContexts) {
        								logicalContext.addChildContext(physicalContext);
									}
        							
        							logicalContexts.put(logicalContext.getName(), logicalContext);
								}
							}
						}	            		
	            	}
	            	pos = 0;
	            }
	            for (Split noContextual : internalCxG.getContextualNodes()) {
					if (noContextual.getId().equals(node)){
						pos = i;
						break;
					}
				}
	            i++;
	        }
	    }
		return logicalContexts;
	}


	private static Set<PhysicalContext> getPhysicalContexts(String constraint, IFile conceptualModel, CxG internalCxG) {
		Set<PhysicalContext> physicalContexts = new HashSet<PhysicalContext>();
		
        String condition = parseConstraint(constraint);
        
        String [] statements = condition.split(" ");
        
        UmlUtils uml = new UmlUtils();
        org.eclipse.uml2.uml.Package package_ = uml.load(conceptualModel);
    	List<Class> classes = UmlUtils.getClasses(package_);
        
        for (String statement : statements) {
        	statement = statement.trim();
        	String [] statmentElements = statement.split("\\.");
        	Class clazz = getUMLClazz(internalCxG.getVariables(), statmentElements, classes);
        	
        	if (clazz == null ){
        		continue;
        	}
        	String contextualElement = getContextualElement(statmentElements, clazz);
        	
        	if (contextualElement != null)
        		physicalContexts.addAll(getPhysicalContextFromContextualElement(clazz.getName(), contextualElement, classes));
		}
		//return sensors;
		return physicalContexts;
	}

	@SuppressWarnings("unchecked")
	private static Set<PhysicalContext> getPhysicalContextFromContextualElement(String clazz, String attribute, List<Class> classes) {
		Set<PhysicalContext> physicalContexts = new HashSet<PhysicalContext>();	
    	
    	
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
				EList<org.eclipse.uml2.uml.Type> types = association.getEndTypes();
				List<EObject> sensorList = new ArrayList<EObject>();
				for (org.eclipse.uml2.uml.Type type : types) {
					Stereotype ster = type.getAppliedStereotype(UmlUtils.CONTEXT_SOURCE_STEREOTYPE); 
					if (ster == null){
						continue;
					}
					System.out.println("Context Source of Contextual Element.: " + type.getName());
					//a fonte de contexto esta associada a quais APIs?
					List<Association> CSAssociations = type.getAssociations();
					for (Association CSAssociation : CSAssociations) {
						//encontra as classes do endpoint da associacao
						EList<org.eclipse.uml2.uml.Type> CSEndTypes = CSAssociation.getEndTypes();
						for (org.eclipse.uml2.uml.Type CSEndType : CSEndTypes) {
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
								addPhysicalContextToSet(physicalContexts, eObject);
								System.out.print(eObject.toString() + " ");
							}
							System.out.println("");
							
						}
					}
				}
			}
    		
		}
    	return physicalContexts;
	}


	@SuppressWarnings("unchecked")
	private static void addPhysicalContextToSet(Set<PhysicalContext> physicalContexts, EObject eObject) {
		java.lang.Class<? extends PhysicalContext> clz = null;
		try {
			clz = (java.lang.Class<? extends PhysicalContext>) java.lang.Class.forName("org.cemantika.testing.contextSource." + eObject.toString());
			physicalContexts.add(clz.newInstance());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}


	private static void fillInternalModel(CxG internalCxG, List<ArrayList<String>> caminhos, IFile conceitualModel) {
		int i = 0;
        int pos = 0;
        int ctCount = 0;
        Constraints constraints;
        //List<Dominio> dominios;
        //Dominio dominio;
        for(ArrayList<String> path : caminhos){
        	i = 0;
        	ctCount++;
        	//dominios = new ArrayList<Dominio>();
        	//System.out.println("Dominio " + ctCount + ":");
	        for (String node : path) {
	            //System.out.print(node);
	            //System.out.print(" - ");
	            if (pos != 0){
	            	System.out.print("no contextual: " + path.get(pos));
	            	System.out.println(";   no proximo: " + node);
	            	for (Split noContextual :internalCxG.getContextualNodes()) {
	            		if (!noContextual.getId().equals(path.get(pos))){
	            			continue;
	            		}
            			for (Object o : noContextual.getMetaDataOrConstraints()) {
            				if(!(o instanceof Constraints)){
            					continue;
            				}
        					constraints = (Constraints) o;
        					for (Constraint constraint : constraints.getConstraint()) {
        						if(constraint.getToNodeId().equals(node)){
        							constraint.setSensors(extractSensors(constraint.getValue(), conceitualModel, internalCxG));
								}
							}
						}	            		
	            	}
	            	pos = 0;
	            }
	            for (Split noContextual : internalCxG.getContextualNodes()) {
					if (noContextual.getId().equals(node)){
						pos = i;
						break;
					}
				}
	            i++;
	        }
	    }
	}

	private static List<String> extractSensors(String constraint, IFile conceitualModel, CxG internalCxG) {
		List<String> sensors = new ArrayList<String>();
        		
        String condition = parseConstraint(constraint);
        
        String [] statements = condition.split(" ");
        
        UmlUtils uml = new UmlUtils();
        org.eclipse.uml2.uml.Package package_ = uml.load(conceitualModel);
    	List<Class> classes = UmlUtils.getClasses(package_);
        
        for (String statement : statements) {
        	statement = statement.trim();
        	String [] statmentElements = statement.split("\\.");
        	Class clazz = getUMLClazz(internalCxG.getVariables(), statmentElements, classes);
        	
        	if (clazz == null ){
        		continue;
        	}
        	String contextualElement = getContextualElement(statmentElements, clazz);
        	
        	if (contextualElement != null)
        		sensors.addAll(getSensorsFromContextualElement(clazz.getName(), contextualElement, classes));
		}
		return sensors;
	}
	
	@SuppressWarnings("unchecked")
	private static List<String> getSensorsFromContextualElement(String clazz, String attribute, List<Class> classes) {
    	List<String>  sensors = new ArrayList<String>();	
    	
    	
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
				EList<org.eclipse.uml2.uml.Type> types = association.getEndTypes();
				List<EObject> sensorList = new ArrayList<EObject>();
				for (org.eclipse.uml2.uml.Type type : types) {
					Stereotype ster = type.getAppliedStereotype(UmlUtils.CONTEXT_SOURCE_STEREOTYPE); 
					if (ster == null){
						continue;
					}
					System.out.println("Context Source of Contextual Element.: " + type.getName());
					//a fonte de contexto esta associada a quais APIs?
					List<Association> CSAssociations = type.getAssociations();
					for (Association CSAssociation : CSAssociations) {
						//encontra as classes do endpoint da associacao
						EList<org.eclipse.uml2.uml.Type> CSEndTypes = CSAssociation.getEndTypes();
						for (org.eclipse.uml2.uml.Type CSEndType : CSEndTypes) {
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
		
	private static Class getUMLClazz(Variables vars, String[] statmentElements, List<Class> classes) {
		String statementVariable = statmentElements[0];
		Class clazz = null;
		for (Variable var : vars.getVariable()) {
			if (var.getName().equals(statementVariable)) {
				clazz = UmlUtils.getClassByName(classes, getType(var));
			}
		}
		return clazz;
	}
	
	private static String getContextualElement(String[] statmentElements, Class clazz) {
		int i = 0;
		String contextualElement = null;
		for (String statmentElement : statmentElements) {
			statmentElement = Introspector.decapitalize(statmentElement);
			if (i != 0) {				
				for (Property clazzField : clazz.getAllAttributes()) {
					if (clazzField.getName().equals(statmentElement)){
						contextualElement = clazzField.getName();
						return contextualElement;
					}
				}
				List<Association> associations = UmlUtils.getAssociations(clazz);
				for (Association association : associations) {
					association.getAppliedStereotypes();
					if ((UmlUtils.hasStereotype(association, UmlUtils.CONTEXT_ELEMENT_STEREOTYPE) &&
						statmentElement.equals(association.getMemberEnds().get(1).getName()))){
						contextualElement = association.getMemberEnds().get(1).getName();
						return contextualElement;
					}
				}
			}
			i++;
		}
		return contextualElement;
	}
	
	
	private static String getType(Variable variable){
		for(Object typeOrValue : variable.getTypeOrValue()){
			if (typeOrValue instanceof Type){
				Type type = (Type) typeOrValue;
				return type.getClassName();
			}
		}
		return null;
	}
	
	private static String parseConstraint(String constraint) {
		constraint.toLowerCase();
		constraint = constraint.replace("return ", "").replace(".equals", "  ")
        					   .replace("!", "    ").replace("&&", "  ")
        					   .replace("||", " ").replace('(', '\0')
        					   .replace(')', '\0').replace(';', '\0')
        					   .replace("'", "").replace("\"", "")
        					   .replace(".get", ".").replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","")
        					   .trim().replaceAll(" +", " ");
		return constraint;
	}

	private static Graph createGrafo(CxG internalCxG) {
		Graph grafo = new Graph();
        if (internalCxG.getConnections() != null){
        	for (Connection connection : internalCxG.getConnections().getConnection()) {
        		grafo.adicionaAresta(connection.getFrom(), connection.getTo());
			}
        }
		return grafo;
	}

	private static CxG extractNodes(Object nodesOrConnectionsOrHeaders, CxG internalCxG) {
		
		Nodes nos = (Nodes) nodesOrConnectionsOrHeaders;
		for (Object no : nos.getStartOrEndOrActionNode()){
			if (no instanceof Start){
				internalCxG.setStart((Start) no);
			}else if (no instanceof End) {
				internalCxG.setEnd((End) no);					
			}else if (no instanceof Split) {;
				internalCxG.getContextualNodes().add((Split) no);
			}
		}
		return internalCxG;
	}

	private static Variables extractVariables(Object headerTag) {
		Header header;
		Variables variables = null;
		
		header = (Header) headerTag;
		for (Object vars : header.getImportsOrFunctionImportsOrGlobals()) {
			if (vars instanceof Variables){
				variables = (Variables) vars;
			}
		}

		return variables;
	}
	
	private static Process extractProcessFromCxG(IFile contextualGraph)	throws FactoryConfigurationError {
		XMLInputFactory xif = XMLInputFactory.newFactory();
        StreamSource xml = new StreamSource(contextualGraph.getRawLocation().makeAbsolute().toFile());
        XMLStreamReader xsr = null;
		try {
			xsr = xif.createXMLStreamReader(xml);
			xsr.nextTag();
			while(!xsr.getLocalName().equals("process")) {	         
				xsr.nextTag();				
	        }
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}        
 
        JAXBContext jc;
        Unmarshaller unmarshaller;
        JAXBElement<Process> jb = null;
		try {
			jc = JAXBContext.newInstance(Process.class);
			unmarshaller = jc.createUnmarshaller();
			jb = unmarshaller.unmarshal(xsr, Process.class);
			xsr.close();
			
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		return  jb.getValue();
	}

	public static Map<String, LogicalContext> getLogicalContextsFaults(Map<String, LogicalContext> logicalContextCxG) {
		//combine faults with logical contexts
		Map<String, LogicalContext> createdLogicalContexts = new HashMap<String, LogicalContext>();
		for (Entry<String, LogicalContext> logicalContextEntry : logicalContextCxG.entrySet()) {
			LogicalContext logicalContext = logicalContextEntry.getValue();
			createdLogicalContexts.putAll(createLogicalContextsFalts(logicalContext));
		}
		
		logicalContextCxG.putAll(createdLogicalContexts);
		
		return logicalContextCxG;
	}


	private static Map<? extends String, ? extends LogicalContext> createLogicalContextsFalts(LogicalContext logicalContext) {
		Map<String, LogicalContext> createdLogicalContexts = new HashMap<String, LogicalContext>();
		for (AbstractContext abstractContext : logicalContext.getContextList()) {
			
			for(ContextDefectPattern contextDefectPattern : ((PhysicalContext)abstractContext).getContextDefectPatterns()){
				LogicalContext createdLogicalContext = (LogicalContext) logicalContext.clone();
				createdLogicalContext.setContextList(new ArrayList<AbstractContext>());
				for (AbstractContext physicalContext : logicalContext.getContextList()){
					createdLogicalContext.addChildContext(physicalContext.clone());
				}
				createdLogicalContext.setName(createdLogicalContext.getName() + " - " + abstractContext.getName() + " - " + contextDefectPattern.toString());
				createdLogicalContexts.put(createdLogicalContext.getName(), createdLogicalContext);
			}
		}
		return createdLogicalContexts;
	}
}