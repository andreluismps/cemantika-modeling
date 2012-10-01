package org.cemantika.modeling.listener.overview;

import org.cemantika.modeling.Activator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.uml2.diagram.usecase.part.UMLCreationWizard;

public class CreateUseCaseModel implements Listener {

	private Shell shell;

	public CreateUseCaseModel(Shell shell) {
		this.shell = shell;
	}

	@Override
	public void handleEvent(Event event) {
		Activator plugin = Activator.getDefault();
		IWorkbench workbench = plugin.getWorkbench();

		UMLCreationWizard wizard = new UMLCreationWizard();
		wizard.init(workbench, new StructuredSelection());
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		dialog.open();
	}

}
