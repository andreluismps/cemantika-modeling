package org.cemantika.modeling.listener.overview;

import org.cemantika.modeling.internal.manager.PluginManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ImportContextKnowledgeTestBase implements Listener {

	private Shell shell;
	private PluginManager manager;
	private String cktb;
	private String title;
	private String extension;

	public ImportContextKnowledgeTestBase(Shell shell, PluginManager manager, String cktb, String title, String extension) {
		this.shell = shell;
		this.manager = manager;
		this.cktb = cktb;
		this.title = title;
		this.extension = extension;
	}

	@Override
	public void handleEvent(Event event) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText(title);
		dialog.setFilterExtensions(new String [] {"*."+ extension});
		dialog.setFilterNames(new String[] { "Context Knowledge Test Base File" });
		String result = dialog.open();
		manager.save(cktb, result);
		
	}

}
