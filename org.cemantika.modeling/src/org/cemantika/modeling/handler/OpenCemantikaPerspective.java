package org.cemantika.modeling.handler;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.perspective.CemantikaPerspectiveFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenCemantikaPerspective extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbench = HandlerUtil
				.getActiveWorkbenchWindow(event);

		try {
			this.openCemantikaPerspective(workbench);
			this.openCemantikaFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private void openCemantikaFile() throws PartInitException {
		Activator activator = Activator.getDefault();
		IProject project = activator.getActiveProject();
		System.out.println("project: "+project);
		if (project != null) {
			IFile file = project.getFile(Activator.CEMANTIKA_CONFIG_FILE);
			activator.openEditor(file);
		}
	}

	private void openCemantikaPerspective(IWorkbenchWindow workbench)
			throws WorkbenchException {
		workbench.getWorkbench().showPerspective(
				CemantikaPerspectiveFactory.ID, workbench);

	}

}
