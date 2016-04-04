package org.cemantika.modeling.listener.overview;

import java.io.File;
import java.io.IOException;

import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.testing.cktb.db.DataBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class CreateContextKnowledgeTestBase implements Listener {

	private Shell shell;
	private PluginManager manager;
	private String cktb;
	private String title;
	private String extension;

	public CreateContextKnowledgeTestBase(Shell shell, PluginManager manager, String cktb, String title, String extension) {
		this.shell = shell;
		this.manager = manager;
		this.cktb = cktb;
		this.title = title;
		this.extension = extension;
	}

	@Override
	public void handleEvent(Event event) {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText(title);
		dialog.setFilterExtensions(new String [] {"*."+ extension});
		dialog.setFilterNames(new String[] { "Context Knowledge Test Base File" });
		dialog.setFileName("base.cktb");
		String fileName = dialog.open();
		
		File file = new File(fileName);
		try {
			file.createNewFile();
			manager.save(cktb, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DataBase.createDb(DataBase.getConnection(fileName + ".db"));
		
	}

}
