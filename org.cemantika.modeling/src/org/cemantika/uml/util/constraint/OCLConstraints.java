package org.cemantika.uml.util.constraint;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.cemantika.uml.util.UmlUtils;
import org.cemantika.uml.util.UmlUtils.ProfileType;
import org.eclipse.core.resources.IResource;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.uml.UMLEnvironmentFactory;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.ValueSpecification;

public class OCLConstraints {

	/*
	 * 
	 * Adicionar marcas
	 */

	@SuppressWarnings("unchecked")
	private OCL ocl;
	UMLEnvironmentFactory factory;
	private UmlUtils uml;
	private static final Map<String, String> ERROR_MESSAGES = new Hashtable<String, String>();
	public static final String EXECUTES_OCL = "executes_constraint";
	public static final String AGENT_OCL = "agent_constraint";
	public static final String TASK_OCL = "task_constraint";
	public static final String CONTEXTUAL_ELEMENT_TYPE_DEFINED = "element_type_defined";
	public static final String CONTEXTUAL_ENTITY_HAS_ELEMENTS = "entity_has_elements";
	public static final String CONTEXTUAL_ELEMENT_BELONGS_TO_ENTITY = "ce_belongs_to_entity";

	static {
		ERROR_MESSAGES
				.put(
						EXECUTES_OCL,
						"Association: Focus is not configured properly. Associates an Agent to a Task through <<executes>> Association");
		ERROR_MESSAGES.put(AGENT_OCL,
				"Actor: Agent must have at least one Focus Association");
		ERROR_MESSAGES.put(TASK_OCL,
				"UseCase: Task must have at least one Focus Association");
		ERROR_MESSAGES
				.put(CONTEXTUAL_ELEMENT_TYPE_DEFINED,
						"Property: Contextual Element must define type Who, What, Where, When or Why");
		ERROR_MESSAGES
				.put(CONTEXTUAL_ENTITY_HAS_ELEMENTS,
						"Class: Contextual Entity must have at least one Contextual Element");
		ERROR_MESSAGES.put(CONTEXTUAL_ELEMENT_BELONGS_TO_ENTITY, "Property or Association: Contextual Element must belong to Contextual Entity");
	}

	public OCLConstraints(UmlUtils uml) {
		this.uml = uml;
		factory = new UMLEnvironmentFactory(uml.getResourceSet());
	}

	public List<String> getConstraint(NamedElement namedElement,
			Stereotype stereotype) {
		if (stereotype != null) {
			for (Constraint c : stereotype.getOwnedRules()) {
				ValueSpecification specification = c.getSpecification();
				OpaqueExpression expression = (OpaqueExpression) specification;
				return expression.getBodies();
			}
		}
		return null;

	}

	public Element getConstrainedElements(Constraint constraint) {
		List<Element> elements = constraint.getConstrainedElements();
		if (elements == null || elements.isEmpty())
			return null;
		return elements.get(0);
	}

	public List<String> getConstraintBody(Constraint constraint) {
		ValueSpecification specification = constraint.getSpecification();
		OpaqueExpression expression = (OpaqueExpression) specification;
		return expression.getBodies();
	}

	public List<Constraint> getConstraints(Element element,
			Stereotype stereotype) {
		if (stereotype != null) {
			return stereotype.getOwnedRules();
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	public boolean validatesContraint(Element constrainedElement,
			Element toValid, String expression) {
		ocl = OCL.newInstance(factory);
		OCLHelper<Element, Constraint, ?, ?> helper = ocl.createOCLHelper();
		helper.setContext(constrainedElement);
		Constraint constraint = null;
		try {
			constraint = (Constraint) helper.createInvariant(expression);
		} catch (ParserException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException(e.getLocalizedMessage());
		}
		return ocl.check(toValid, constraint);
	}

	public boolean validateResource(IResource resource, List<String> messages) {
		return validateUseCase(resource, messages)
				&& validateConceptualDiagram(resource, messages);
	}

	public boolean validateUseCase(IResource resource, List<String> messages) {
		return validateStereotypeConstraints(resource, messages,
				ProfileType.USE_CASE);
	}

	public boolean validateConceptualDiagram(IResource resource,
			List<String> messages) {
		return validateStereotypeConstraints(resource, messages,
				ProfileType.CLASS_DIAGRAM);
	}

	private boolean validateStereotypeConstraints(IResource resource,
			List<String> messages, ProfileType type) {
		List<Boolean> errors = new ArrayList<Boolean>();
		Profile profile = uml.getCemantikaProfile(type);
		List<Stereotype> stereotypes = profile.getOwnedStereotypes();
		org.eclipse.uml2.uml.Package package_ = uml.load(resource);
		for (Stereotype stereotype : stereotypes) {
			List<Element> elements = uml.getElementsWithStereotype(package_,
					stereotype);
			for (Element element : elements) {
				List<Constraint> constraints = getConstraints(element,
						stereotype);
				for (Constraint constraint : constraints) {
					NamedElement constrainedElement = UmlUtils.findStereotype(
							element, stereotype.getQualifiedName());
					String constraintName = constraint.getName();
					if (constrainedElement != null) {
						for (String expression : getConstraintBody(constraint)) {
							boolean valid = validatesContraint(
									constrainedElement, element, expression);
							if (!valid) {
								String message = appendClassifierIdentification(element);
								errors.add(valid);
								messages.add(message
										+ ERROR_MESSAGES.get(constraintName));
							}
						}
					}
				}
			}
		}
		return errors.isEmpty();
	}

	private String appendClassifierIdentification(Element element) {
		StringBuffer append = new StringBuffer();
		Element owner = element.getOwner();
		append.append(getName(owner)).append(":");
		append.append(getName(element)).append(" - ");

		return append.toString();
	}

	private String getName(Element element) {
		String name = null;
		if (element instanceof NamedElement) {
			name = (((NamedElement) element).getName());
		}
		return name == null ? "" : name;
	}
}
