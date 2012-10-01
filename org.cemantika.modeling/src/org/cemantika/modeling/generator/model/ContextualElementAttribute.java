package org.cemantika.modeling.generator.model;

public class ContextualElementAttribute extends Attribute {
	
	private String contextType;

	public ContextualElementAttribute(String type, String value, String contextType) {
		super(type, value);
		this.contextType = contextType;
	}

	public String getContextType() {
		return contextType;
	}

	public void setContextType(String contextType) {
		this.contextType = contextType;
	}
	
	@Override
	public String toString() {
		return String.format("@ContextualElement(type=%s)\n\t", contextType) + super.toString();
	}

}
