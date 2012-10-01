package org.cemantika.modeling;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cemantika.uml.util.constraint.ConstraintErrorNotifier;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.cemantika.modeling";
	public static final String CEMANTIKA_LIB = "cemantika.jar";
	public static final String CEMANTIKA_CONFIG_FILE = ".cemantika";
	public static final String CEMANTIKA_LOGO_IMAGE = "/icons/cemantika/logo.png";
	public static final String CEMANTIKA_ACTIVITY = "/icons/cemantika/activity.gif";
	public static final String CEMANTIKA_ARTIFACT = "/icons/cemantika/artifact.gif";
	public static final String CEMANTIKA_DELIVERABLE = "/icons/cemantika/deliverable.gif";
	public static final String CEMANTIKA_TASK = "/icons/cemantika/task.gif";
	public static final String CEMANTIKA_PROCESS = "/icons/cemantika/css_design_process.gif";
	private static final String CEMANTIKA_TEMPLATE_DIR = "templates";

	// The shared instance
	private static Activator plugin;
	private IPath libPath;
	private IPath rootPath;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		String root = this.getRoot();
		this.rootPath = new Path(root);
		this.libPath = new Path(root + File.separator + CEMANTIKA_LIB);
	}

	private String getRoot() throws IOException {
		Bundle bundle = Activator.getDefault().getBundle();
		URL root = bundle.getEntry("/");

		String strRoot = FileLocator.resolve(root).getFile();

		return strRoot;
	}

	public String getTemplateDir() throws IOException {
		return getRoot() + CEMANTIKA_TEMPLATE_DIR;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		loadImage(reg, CEMANTIKA_LOGO_IMAGE);
		loadImage(reg, CEMANTIKA_PROCESS);
		loadImage(reg, CEMANTIKA_ACTIVITY);
		loadImage(reg, CEMANTIKA_DELIVERABLE);
		loadImage(reg, CEMANTIKA_TASK);
		loadImage(reg, CEMANTIKA_ARTIFACT);
	}

	protected void loadImage(ImageRegistry registry, String id) {
		loadImage(registry, id, getBundle().getSymbolicName());
	}

	protected void loadImage(ImageRegistry registry, String id, String bundleId) {
		ImageDescriptor descriptor = imageDescriptorFromPlugin(bundleId, id);
		if (descriptor != null) {
			registry.put(id, descriptor);
		}
	}

	public IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public List<IProject> getSelectedProjects() {
		IWorkbenchWindow workbench = plugin.getWorkbench()
				.getActiveWorkbenchWindow();

		List<IProject> selectedProjects = new ArrayList<IProject>();
		ISelection selection = workbench.getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Iterator<?> iterator = structuredSelection.iterator();
			while (iterator.hasNext()) {
				Object elem = iterator.next();
				if (!(elem instanceof IResource)) {
					if (!(elem instanceof IAdaptable))
						continue;
					elem = ((IAdaptable) elem).getAdapter(IResource.class);
					if (!(elem instanceof IResource))
						continue;
				}
				if (!(elem instanceof IProject)) {
					elem = ((IResource) elem).getProject();
					if (!(elem instanceof IProject))
						continue;
				}
				selectedProjects.add((IProject) elem);
			}
		}
		return selectedProjects;
	}

	public IResource[] getUmlResources() {
		SearchUmlFiles uml = new SearchUmlFiles(this.getActiveProject());
		try {
			List<IResource> resources = uml.getUmlFiles();
			uml.execute(new NullProgressMonitor());
			return resources.toArray(new IResource[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public IProject getActiveProject() {
		IEditorPart editorPart = plugin.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (editorPart != null) {
			IFileEditorInput input = (IFileEditorInput) editorPart
					.getEditorInput();
			IFile file = input.getFile();
			IProject activeProject = file.getProject();

			return activeProject;
		} else {
			List<IProject> projects = this.getSelectedProjects();
			if (!projects.isEmpty()) {
				return projects.get(0);
			}
		}
		return null;

	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public IPath getCemantikaLibPath() {
		return this.libPath;
	}

	public IPath getRootPath() {
		return rootPath;
	}

	public Shell getShell() {
		IWorkbench workbench = plugin.getWorkbench();
		Shell shell = workbench.getActiveWorkbenchWindow().getShell();

		return shell;
	}

	public IFile open(String file) {
		return getWorkspace().getRoot().getFile(new Path(file));
	}

	public IWorkbenchPage getActivePage() {
		IWorkbenchWindow activeWorkbenchWindow = plugin.getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return null;
		}
		return activeWorkbenchWindow.getActivePage();
	}

	public IEditorPart openEditor(IFile file) throws PartInitException {
		IWorkbenchPage page = plugin.getActivePage();
		return IDE.openEditor(page, file);
	}

	private class SearchUmlFiles extends WorkspaceModifyOperation implements
			IResourceProxyVisitor {

		private IProject activeProject;

		public SearchUmlFiles(IProject project) {
			this.activeProject = project;
		}

		private List<IResource> umlFiles = new ArrayList<IResource>();

		public List<IResource> getUmlFiles() {
			return umlFiles;
		}

		@Override
		protected void execute(IProgressMonitor monitor) throws CoreException,
				InvocationTargetException, InterruptedException {
			ResourcesPlugin.getWorkspace().getRoot().accept(this,
					IResource.DEPTH_INFINITE);

		}

		protected boolean isMatch(IFile file) {
			return file.getProject().equals(activeProject)
					&& file.getFileExtension().equals("uml");
		}

		@Override
		public boolean visit(IResourceProxy proxy) throws CoreException {
			if (proxy.getType() == IResource.FILE) {
				IFile file = (IFile) proxy.requestResource();
				if (isMatch(file)) {
					umlFiles.add(file);
				}
			}
			return true;

		}
	}

	public void showMessage(String message, int style) {
		MessageBox messageBox = new MessageBox(getShell(), style);
		messageBox.setText("Contextual Entities Classes were generated");
		messageBox.setMessage(message);
		messageBox.open();

	}

	public IMarker[] findCemantikaProblems(IResource target)
			throws CoreException {
		String type = ConstraintErrorNotifier.MARKER_ID;
		IMarker[] markers = target.findMarkers(type, false,
				IResource.DEPTH_INFINITE);
		return markers;
	}

	public boolean hasErrors(IResource resource) throws CoreException {
		return this.findCemantikaProblems(resource).length > 0;
	}

	public void showView(String idProblemView) {
		IWorkbenchPage page = getActivePage();
		if (page != null)
			try {
				page.showView(idProblemView);
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
