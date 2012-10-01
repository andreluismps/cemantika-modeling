package org.cemantika.modeling.listener.overview;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.modeling.wizard.ImportUmlArtifactWizard;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

public class ImportUmlArtifact implements Listener {

	private Shell shell;
	private PluginManager manager;
	private String diagram;
	private String model;
	private String title;
	private String description;
	private String extension;

	public ImportUmlArtifact(Shell shell, PluginManager manager, String model, String diagram, String title, String description, String extension) {
		this.shell = shell;
		this.manager = manager;
		this.model = model;
		this.diagram = diagram;
		this.title = title;
		this.description = description;
		this.extension = extension;
	}

	@Override
	public void handleEvent(Event event) {
		Activator plugin = Activator.getDefault();
		IWorkbench workbench = plugin.getWorkbench();

		ImportUmlArtifactWizard wizard = new ImportUmlArtifactWizard(manager, model, diagram, title, description, extension);
		wizard.init(workbench, new StructuredSelection());
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		dialog.open();
	}

}
