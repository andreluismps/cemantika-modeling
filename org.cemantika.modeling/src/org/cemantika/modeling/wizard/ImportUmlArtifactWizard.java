/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.cemantika.modeling.wizard;

import org.cemantika.modeling.internal.manager.PluginManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

public class ImportUmlArtifactWizard extends Wizard implements IImportWizard {

	ImportUseCaseModelPage mainPage;
	PluginManager manager;
	private String modelKey;
	private String diagramKey;
	private String title;
	private String description;
	private String diagramExtension;

	public ImportUmlArtifactWizard(PluginManager manager, String modelKey, String diagramKey, String title, String description, String diagramExtension) {
		super();
		this.manager = manager;
		this.modelKey = modelKey;
		this.diagramKey = diagramKey;
		this.title = title;
		this.description = description;
		this.diagramExtension = diagramExtension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		this.manager
				.save(modelKey, mainPage.getModelFile());
		this.manager.save(diagramKey, mainPage
				.getDiagramFile());

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("File Import Wizard"); // NON-NLS-1
		setNeedsProgressMonitor(true);
		mainPage = new ImportUseCaseModelPage(title, description, diagramExtension); // NON-NLS-1
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		addPage(mainPage);
	}

}
