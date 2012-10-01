package org.cemantika.modeling.generator.model;

public class Attribute {

	private String type;
	private String name;
	private boolean multiple;

	public Attribute(String type, String name) {
		this(type, name, false);
	}

	public Attribute(String type, String name, boolean multiple) {
		super();
		this.type = type;
		this.name = name;
		this.multiple = multiple;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		if (this.multiple) {
			this.type = String.format("java.util.Collection<%s>", this.type);
		}
		return String.format("private %s %s", type, name);
	}

	public String toGetMethod() {
		return ("boolean".equals(getType())) ? "is" + getCappedName() : "get"
				+ getCappedName();
	}

	public String toSetMethod() {
		return "set" + getCappedName();
	}

	public String getCappedName() {
		return NameUtil.capName(getName());
	}

}
