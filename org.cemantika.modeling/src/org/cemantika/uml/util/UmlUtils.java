package org.cemantika.uml.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cemantika.uml.model.Focus;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.DynamicValueHolder;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.UseCase;
import org.eclipse.uml2.uml.util.UMLUtil;

public class UmlUtils {

	private static final String CEMANTIKA_USE_CASE_PROFILE = "platform:/plugin/org.cemantika.modeling/profile/cemantika.profile.uml#0";
	private static final String CEMANTIKA_CLASS_PROFILE = "platform:/plugin/org.cemantika.modeling/profile/cemantika_class.profile.uml#0";
	public static final String FOCUS_STEREOTYPE = "contextprofile::executes";
	public static final String CONTEXT_ENTITY_STEREOTYPE = "cemantika_class::ContextualEntity";
	public static final String CONTEXT_ELEMENT_STEREOTYPE = "cemantika_class::ContextualElement";
	public static final String CONTEXT_SOURCE_API_STEREOTYPE = "cemantika_class::ContextSourceAPI";
	public static final String CONTEXT_SOURCE_STEREOTYPE = "cemantika_class::ContextSource";
	public static final String ACQUISITION_ASSOCIATION_STEREOTYPE = "cemantika_class::AcquisitionAssociation";
	
	private ResourceSet resourceSet;
	private URI cemantikaUseCaseProfile;
	private URI cemantikaClassProfile;
	public static FilterElement NOT_FOCUS_ASSOCIATIONS;

	public enum ProfileType {
		USE_CASE, CLASS_DIAGRAM
	};

	static {
		NOT_FOCUS_ASSOCIATIONS = new FilterElement() {

			@Override
			public boolean include(NamedElement namedElement) {
				return namedElement instanceof Association
						&& !hasStereotype(namedElement, FOCUS_STEREOTYPE);
			}
		};
	}

	public UmlUtils() {
		this.resourceSet = new ResourceSetImpl();
		cemantikaUseCaseProfile = URI.createURI(CEMANTIKA_USE_CASE_PROFILE);
		cemantikaClassProfile = URI.createURI(CEMANTIKA_CLASS_PROFILE);
	}

	public ResourceSet getResourceSet() {
		return resourceSet;
	}

	protected void save(URI uri) {
		Resource resource = resourceSet.createResource(uri);

		try {
			resource.save(null);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void applyProfile(org.eclipse.uml2.uml.Package package_,
			Profile profile) {
		try {
			package_.applyProfile(profile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean alreadyApplied(org.eclipse.uml2.uml.Package package_,
			String profile) {
		return package_.getAppliedProfile(profile) != null;
	}

	public org.eclipse.uml2.uml.Package load(URI uri) {
		org.eclipse.uml2.uml.Package package_ = null;
		try {
			Resource resource = resourceSet.getResource(uri, true);
			package_ = (org.eclipse.uml2.uml.Package) EcoreUtil
					.getObjectByType(resource.getContents(),
							UMLPackage.Literals.PACKAGE);
		} catch (Exception we) {
			we.printStackTrace();
		}
		return package_;
	}

	@SuppressWarnings("unchecked")
	public void save(org.eclipse.uml2.uml.Package package_, URI uri) {
		Resource resource = resourceSet.createResource(uri);
		EList contents = resource.getContents();

		contents.add(package_);

		for (Iterator allContents = UMLUtil.getAllContents(package_, true,
				false); allContents.hasNext();) {

			EObject eObject = (EObject) allContents.next();

			if (eObject instanceof Element) {
				contents
						.addAll(((Element) eObject).getStereotypeApplications());
			}
		}

		try {
			resource.save(null);
			this.resourceSet = new ResourceSetImpl();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void save(org.eclipse.uml2.uml.Package package_, IFile file) {
		URI uri = URI.createURI(file.getLocationURI().toString());
		save(package_, uri);
	}

	public void applyCemantikaProfile(IFile file, ProfileType type) {
		Profile profile = null;
		profile = getCemantikaProfile(type);
		URI uri = URI.createURI(file.getLocationURI().toString());
		org.eclipse.uml2.uml.Package useCase = this.load(uri);
		boolean already = alreadyApplied(useCase, profile.getName());
		if (!already) {
			this.applyProfile(useCase, profile);
			save(useCase, uri);
		}
	}

	public Profile getCemantikaProfile(ProfileType type) {
		Profile profile = null;
		if (type == ProfileType.USE_CASE) {
			profile = (Profile) this.load(cemantikaUseCaseProfile);
		} else if (type == ProfileType.CLASS_DIAGRAM) {
			profile = (Profile) this.load(cemantikaClassProfile);
		}

		return profile;
	}

	public List<Focus> showFoci(org.eclipse.uml2.uml.Package useCase) {
		List<Focus> foci = new ArrayList<Focus>();
		Actor agent = null;
		UseCase task = null;
		for (Association focus : this.getFoci(useCase)) {
			for (Type type : focus.getEndTypes()) {
				if (type instanceof UseCase) {
					task = (UseCase) type;
				} else if (type instanceof Actor) {
					agent = (Actor) type;
				}
			}
			if (agent != null && task != null) {
				Focus foco = new Focus();
				foco.setAgent(agent);
				foco.setAssociation(focus);
				foco.setTask(task);
				foci.add(foco);
			}
		}
		return foci;
	}

	public List<Element> getElementsWithStereotype(
			org.eclipse.uml2.uml.Package package_, Stereotype stereotype) {
		List<Element> elements = new ArrayList<Element>();
		for (Element element : package_.getMembers()) {
			if (UmlUtils.hasStereotype(element, stereotype.getQualifiedName()))
				elements.add(element);
			for (Element ownedElement : element.getOwnedElements()) {
				if (UmlUtils.hasStereotype(ownedElement, stereotype
						.getQualifiedName()))
					elements.add(ownedElement);
			}
		}
		return elements;
	}

	public org.eclipse.uml2.uml.Package load(IResource file) {
		URI uri = URI.createURI(file.getLocationURI().toString());
		return this.load(uri);
	}

	public Object[] showFoci(IResource file) {
		URI uri = URI.createURI(file.getLocationURI().toString());
		org.eclipse.uml2.uml.Package useCase = this.load(uri);
		return new Object[] { this.showFoci(useCase), useCase };
	}

	public List<Association> getFoci(org.eclipse.uml2.uml.Package package_) {
		List<Association> foci = new ArrayList<Association>();

		EList<NamedElement> elements = package_.getMembers();
		for (NamedElement namedElement : elements) {
			// namedElement.getApplicableStereotype()
			if (namedElement instanceof Association) {
				Association association = (Association) namedElement;
				if (UmlUtils.hasStereotype(association, FOCUS_STEREOTYPE)) {
					foci.add(association);
				}
			}
		}

		return foci;
	}

	public List<Element> getElementsByFilter(
			org.eclipse.uml2.uml.Package package_, FilterElement filter) {
		List<Element> foci = new ArrayList<Element>();

		EList<NamedElement> elements = package_.getMembers();
		for (NamedElement namedElement : elements) {
			if (filter.include(namedElement)) {
				foci.add(namedElement);
			}
		}

		return foci;
	}

	public static boolean hasStereotype(Element element, String stereotype) {
		return UmlUtils.findStereotype(element, stereotype) != null;
	}

	public static Stereotype findStereotype(Element namedElement,
			String stereotype) {

		return namedElement.getAppliedStereotype(stereotype);
	}

	public static Object getStereotypePropertyValue(NamedElement namedElement,
			String stereotypeStr, String propertyName) {
		Stereotype stereotype = findStereotype(namedElement, stereotypeStr);
		Object value = null;
		if (stereotype != null) {
			value = namedElement.getValue(stereotype, propertyName);
		}

		return value;
	}

	public static void setStereotypePropertyValue(NamedElement namedElement,
			String stereotypeStr, String property, Object value) {

		Stereotype stereotype = findStereotype(namedElement, stereotypeStr);
		namedElement.setValue(stereotype, property, value);
	}

	public static List<org.eclipse.uml2.uml.Class> getClasses(
			org.eclipse.uml2.uml.Package package_) {
		List<org.eclipse.uml2.uml.Class> classes = new ArrayList<org.eclipse.uml2.uml.Class>();
		for (NamedElement element : package_.getMembers()) {
			if (element instanceof org.eclipse.uml2.uml.Class) {
				classes.add((Class) element);
			}
		}

		return classes;
	}
	
	public static org.eclipse.uml2.uml.Class getClassByName(List<Class> classes, String className) {
		org.eclipse.uml2.uml.Class retorno = null;
		for (org.eclipse.uml2.uml.Class clazz : classes) {
			if (clazz.getName().equals(className)) {
				retorno = clazz;
			}
		}

		return retorno;
	}

	public static List<Property> getProperties(org.eclipse.uml2.uml.Class clazz) {
		return clazz.getAttributes();
	}

	public static String getPropertyType(Property property) {
		Type type = property.getType();
		return type == null ? "Object" : type.getName();
	}

	public static String getTaggedValue(NamedElement namedElement, String stereotype,
			String name) {
		Stereotype ster = findStereotype(namedElement, stereotype);

		EnumerationLiteral literal = (EnumerationLiteral) namedElement.getValue(
				ster, name);
		return literal.getName().toUpperCase();
	}
	
	public static String getElementTaggedValue(NamedElement namedElement, String stereotype,
			String name) {
		String element = null;
		Stereotype ster = findStereotype(namedElement, stereotype);

		DynamicValueHolder literal = (DynamicValueHolder) namedElement.getValue(ster, name);
		Property property = (Property) literal.dynamicGet(0);
		Association association = (Association) literal.dynamicGet(1);
		if (property != null){
			element = property.getName();
		}else if (association != null){
			element = association.getMemberEnds().get(1).getName();
		}
		return element;
	}

	public static List<Association> getAssociations(
			org.eclipse.uml2.uml.Class clazz) {
		return clazz.getAssociations();
	}
	
	public static boolean isAutoRelationShip(Association association) {
		if (!association.isBinary())
			return false;
		List<Type> types = new ArrayList<Type>();
		for (Property property : association.getMemberEnds()) {
			types.add(property.getType());
		}
		
		return sameTypes(types);	
	}

	private static boolean sameTypes(List<Type> types) {
		return types.get(0).equals(types.get(1));
	}

}
