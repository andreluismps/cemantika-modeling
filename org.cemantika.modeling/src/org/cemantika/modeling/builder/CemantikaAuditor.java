package org.cemantika.modeling.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.cemantika.modeling.Activator;
import org.cemantika.uml.util.UmlUtils;
import org.cemantika.uml.util.constraint.ConstraintErrorNotifier;
import org.cemantika.uml.util.constraint.OCLConstraints;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class CemantikaAuditor extends IncrementalProjectBuilder {

	public static final String ID = Activator.PLUGIN_ID + ".cemantikaAuditor";
	private List<IResource> changed = new ArrayList<IResource>();

	public CemantikaAuditor() {
	}

	@SuppressWarnings("unchecked")
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {

		if (shouldAudit(kind)) {
			auditCemantika(monitor);
		}
		return null;
	}

	private void auditCemantika(IProgressMonitor monitor) {
		UmlUtils uml = new UmlUtils();
		OCLConstraints ocl = new OCLConstraints(uml);
		List<IResource> changes = this.modelChange();
		monitor.beginTask("Checking Cemantika Files", changes.size());
		int i = 0;
		for (IResource resource : changes) {
			List<String> errors = new ArrayList<String>();
			monitor.worked(i++);
			boolean valid = ocl.validateResource(resource, errors);
			if (!valid) {
				showErrors(resource, errors);
				showProblemView();
			} else {
				clearProblems(resource);
			}
		}
		monitor.done();
	}

	private void showProblemView() {
		// TODO switch do problem view
		
	}

	private void showErrors(IResource resource, List<String> errors) {
		ConstraintErrorNotifier notifier = new ConstraintErrorNotifier(resource);
		notifier.clean();
		for (String error : errors) {
			reportProblem(resource, error);
		}
	}

	private void clearProblems(IResource resource) {
		ConstraintErrorNotifier notifier = new ConstraintErrorNotifier(resource);
		notifier.clean();
	}
	
	

	private void reportProblem(IResource resource, String message) {
		ConstraintErrorNotifier notifier = new ConstraintErrorNotifier(resource);
		try {			
			notifier.addProblem(message);
		} catch (CoreException e) {
			System.out.println(message);
			e.printStackTrace();
		}
	}

	private boolean shouldAudit(int kind) {
		if (kind == FULL_BUILD)
			return true;
		IResourceDelta delta = getDelta(getProject());
		if (delta == null)
			return false;
		try {
			this.changed.clear();

			delta.accept(new IResourceDeltaVisitor() {

				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {
					if (delta.getKind() == IResourceDelta.CHANGED) {
						changed.add(delta.getResource());
						return true;
					}
					return false;
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return !changed.isEmpty();
	}

	private List<IResource> modelChange() {
		List<IResource> umlChanded = new ArrayList<IResource>();

		for (IResource resource : this.changed) {
			String extension = resource.getFileExtension();
			if (extension != null) {
				boolean modelChanged = extension.equals("uml");
				if (modelChanged) {
					umlChanded.add(resource);
				}
			}
		}
		return umlChanded;
	}

	public static void addBuilderToProject(IProject project, String builderId) {
		// Cannot modify closed projects.
		if (!project.isOpen())
			return;
		// Get the description.
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			e.printStackTrace();
			return;
		}
		// Look for builder already associated.
		ICommand[] cmds = description.getBuildSpec();
		for (int j = 0; j < cmds.length; j++) {
			if (cmds[j].getBuilderName().equals(builderId))
				return;
		}
		// Associate builder with project.
		ICommand newCmd = description.newCommand();
		newCmd.setBuilderName(builderId);
		List<ICommand> newCmds = new ArrayList<ICommand>();
		newCmds.addAll(Arrays.asList(cmds));
		newCmds.add(newCmd);
		description.setBuildSpec((ICommand[]) newCmds
				.toArray(new ICommand[newCmds.size()]));
		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
