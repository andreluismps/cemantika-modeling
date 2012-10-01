package org.cemantika.modeling.handler;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.nature.ContextNature;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class AddContextNature extends AbstractHandler implements IHandler {

	private static final String NATURE_ID = ContextNature.ID;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Collection<IProject> projects = Activator.getDefault().getSelectedProjects();
		for (IProject project : projects) {
			this.addContextNature(project);
			try {
				this.addCemantikaToBuildPath(project);
				this.createCemantikaFile(project);
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (CoreException c) {
				c.printStackTrace();
			}
		}

		return null;
	}

	/*
	 * Create Cemantika File and open them
	 */
	private void createCemantikaFile(IProject project) throws CoreException {
		IFile cemantikaFile = project.getFile(Activator.CEMANTIKA_CONFIG_FILE);
		StringBuffer strInitial = new StringBuffer();
		strInitial.append("#CemantikaProject: ")
				  .append(project.getName());
		
		ByteArrayInputStream initial = new ByteArrayInputStream(strInitial.toString().getBytes());
		cemantikaFile.create(initial, true, new NullProgressMonitor());
		
	}

	private void addContextNature(IProject project) {
		if (!project.isOpen())
			return;

		// Get the description.
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			return;
		}
		// Determine if the project already has the nature.
		List<String> newIds = new ArrayList<String>();
		newIds.addAll(Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf(NATURE_ID);
		if (index != -1)
			return;
		// Add the nature
		newIds.add(0, NATURE_ID);
		description.setNatureIds(newIds.toArray(new String[newIds.size()]));
		// Save the description.
		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	

	public void addCemantikaToBuildPath(IProject project)
			throws JavaModelException {

		IPath path = Activator.getDefault().getCemantikaLibPath();
		IClasspathEntry contextPath = JavaCore
				.newLibraryEntry(path, null, null);

		// Copy nem library to classpath
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] originalCP;
		originalCP = javaProject.getRawClasspath();
		int originalCPLength = originalCP.length;
		IClasspathEntry[] newClasspath = new IClasspathEntry[originalCPLength + 1];
		System.arraycopy(originalCP, 0, newClasspath, 0, originalCPLength);
		newClasspath[originalCPLength] = contextPath;
		javaProject.setRawClasspath(newClasspath, null);
	}

}
