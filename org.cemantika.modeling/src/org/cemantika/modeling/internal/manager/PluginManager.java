package org.cemantika.modeling.internal.manager;


public interface PluginManager {

	String USE_CASE_MODEL = "USE_CASE_MODEL";
	String USE_CASE_DIAGRAM = "USE_CASE_DIAGRAM";
	String CONCEPTUAL_MODEL = "CONCEPTUAL_MODEL";
	String CONCEPTUAL_DIAGRAM = "CONCEPTUAL_DIAGRAM";

	void save(String key, String value);
	String get(String key);
	boolean alreadyImportUseCase();
	boolean alreadyImportConceptualModel();
}
