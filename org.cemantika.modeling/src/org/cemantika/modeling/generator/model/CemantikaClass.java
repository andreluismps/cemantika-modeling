package org.cemantika.modeling.generator.model;

import java.util.ArrayList;
import java.util.List;

import org.cemantika.modeling.generator.ITranslationUnit;

public class CemantikaClass implements ITranslationUnit {

	private String packageName;
	private String className;
	private String annotation;
	private List<String> importStatements = new ArrayList<String>();
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private String superClass;

	
	public String getAnnotation() {
		return annotation == null ? "" : annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String> getImportStatements() {
		return importStatements;
	}

	public void setImportStatements(List<String> importStatements) {
		this.importStatements = importStatements;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public CemantikaClass addImport(String importStatement) {
		this.importStatements.add(importStatement);
		return this;
	}

	@Override
	public String getTargetFile() {
		return this.getClassName() + ".java";
	}

	public CemantikaClass addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
		return this;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	public String getSuperClass() {
		return superClass;
	}
	
}
