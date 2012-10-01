package org.cemantika.modeling.view.tableviewer.model;

import java.util.ArrayList;
import java.util.List;

public class NamedElement implements INamedElement {
	
	private List<NamedElement> children = new ArrayList<NamedElement>();	
	private String name;
	private ElementType type;
	private NamedElement parent;
	
	public enum ElementType {PROCESS, ACTIVITY, TASK, DELIVERABLE};
	
    public NamedElement(String name, ElementType type) {
    	this.name = name;
    	this.type = type;
    }
	
	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NamedElement> getChildren() {
		return children;
	}
	
	public NamedElement addChildren(NamedElement namedElement) {
		this.children.add(namedElement);
		return this;
	}

	public ElementType getType() {
		return type;
	}

	public void setType(ElementType type) {
		this.type = type;
	}

	public NamedElement getParent() {
		return parent;
	}

	public void setParent(NamedElement parent) {
		this.parent = parent;
	}

}
