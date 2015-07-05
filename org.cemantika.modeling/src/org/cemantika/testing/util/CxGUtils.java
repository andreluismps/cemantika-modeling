package org.cemantika.testing.util;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.List;

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
import org.cemantika.testing.model.Grafo;
import org.cemantika.uml.util.UmlUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;

public class CxGUtils {
	public void readCxG(IFile contextualGraph, IFile conceitualModel) {
		
		Process p = extractProcessFromCxG(contextualGraph);

        InternalCxG internalCxG = new InternalCxG();
        
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
        Grafo grafo = createGrafo(internalCxG);
        
        //encontra caminhos
        List<ArrayList<String>> caminhos = grafo.listarCaminhos(grafo, internalCxG.getStart().getId(), internalCxG.getEnd().getId());
        
        //obtem os sensores
        fillInternalModel(internalCxG, caminhos, conceitualModel);		
		//return dominiosGrafo;
        System.out.println("");
	}

	private void fillInternalModel(InternalCxG internalCxG, List<ArrayList<String>> caminhos, IFile conceitualModel) {
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

	private List<String> extractSensors(String constraint, IFile conceitualModel, InternalCxG internalCxG) {
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
	private List<String> getSensorsFromContextualElement(String clazz, String attribute, List<Class> classes) {
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
		
	private Class getUMLClazz(Variables vars, String[] statmentElements, List<Class> classes) {
		String statementVariable = statmentElements[0];
		Class clazz = null;
		for (Variable var : vars.getVariable()) {
			if (var.getName().equals(statementVariable)) {
				clazz = UmlUtils.getClassByName(classes, getType(var));
			}
		}
		return clazz;
	}
	
	private String getContextualElement(String[] statmentElements, Class clazz) {
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
	
	
	private String getType(Variable variable){
		for(Object typeOrValue : variable.getTypeOrValue()){
			if (typeOrValue instanceof Type){
				Type type = (Type) typeOrValue;
				return type.getClassName();
			}
		}
		return null;
	}
	
	private String parseConstraint(String constraint) {
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

	private Grafo createGrafo(InternalCxG internalCxG) {
		Grafo grafo = new Grafo();
        if (internalCxG.getConnections() != null){
        	for (Connection connection : internalCxG.getConnections().getConnection()) {
        		grafo.adicionaAresta(connection.getFrom(), connection.getTo());
			}
        }
		return grafo;
	}

	private InternalCxG extractNodes(Object nodesOrConnectionsOrHeaders, InternalCxG internalCxG) {
		
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
	
	private static class InternalCxG{
		private Variables variables;
		private Start start;
		private End end;
		private List<Split> contextualNodes = new ArrayList<Split>();
		private Connections connections;
		
		public void setStart(Start start) {
			this.start = start;
		}
		public Start getStart() {
			return start;
		}
		public void setEnd(End end) {
			this.end = end;
		}
		public End getEnd() {
			return end;
		}
		
		public List<Split> getContextualNodes() {
			return contextualNodes;
		}
		public void setVariables(Variables variables) {
			this.variables = variables;
		}
		public Variables getVariables() {
			return variables;
		}
		public void setConnections(Connections connections) {
			this.connections = connections;
		}
		public Connections getConnections() {
			return connections;
		}
	}
}