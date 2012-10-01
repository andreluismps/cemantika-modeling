package org.cemantika.modeling.nature;

import org.cemantika.modeling.builder.CemantikaAuditor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class ContextNature implements IProjectNature {

	public static final String ID = ContextNature.class.getName();
	public static final String DROOLS_ID = "org.drools.eclipse.droolsbuilder";

	private IProject project;

	@Override
	public void configure() throws CoreException {
		CemantikaAuditor.addBuilderToProject(project, CemantikaAuditor.ID);
		CemantikaAuditor.addBuilderToProject(project, DROOLS_ID);
	}

	@Override
	public void deconfigure() throws CoreException {
		System.out.println("removing context nature");
		//remove builder

	}

	@Override
	public IProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
