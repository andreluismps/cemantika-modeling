package org.cemantika.modeling.contextual.graph;

import org.cemantika.modeling.generator.ITranslationUnit;

public class ContextualGraph implements ITranslationUnit {

	private String name;
	private String id;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getTargetFile() {
		return this.id + ".rf";
		
	}


}
