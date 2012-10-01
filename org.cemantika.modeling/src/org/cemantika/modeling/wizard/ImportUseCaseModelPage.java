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

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.view.filter.SelectFiles;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ImportUseCaseModelPage extends WizardPage {

	protected ElementTreeSelectionDialog editorDiagramModel;
	protected ElementTreeSelectionDialog editorDomainModel;
	private String modelFile;
	private String diagramFile;
	protected ContainerSelectionDialog dialog;
	private Text domainFileField;
	private Text diagramFileField;
	private String diagramExtension;

	public ImportUseCaseModelPage(String pageName, String description, String diagramExtension) {
		super("selectFiles");
		setTitle(pageName); // NON-NLS-1
		setDescription(description); // NON-NLS-1
		this.diagramExtension = diagramExtension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.WizardNewFileCreationPage#createAdvancedControls
	 * (org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		final Label domainModelMessage = new Label(container, SWT.NONE);
		final GridData gridData = new GridData();
		gridData.horizontalSpan = 3;

		domainModelMessage.setLayoutData(gridData);
		domainModelMessage.setText("Select the domain model.");
		final Label domainModel = new Label(container, SWT.NONE);
		final GridData gridData_1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		domainModel.setLayoutData(gridData_1);
		domainModel.setText("Domain Model (*.uml):");
		domainFileField = new Text(container, SWT.BORDER);
		domainFileField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updatePageComplete();
			}
		});
		domainFileField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Button browserModel = new Button(container, SWT.NONE);
		browserModel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseForDomainModel();
			}
		});
		browserModel.setText("Browse...");
		final Label label_2 = new Label(container, SWT.NONE);
		final GridData gridData_2 = new GridData();
		gridData_2.horizontalSpan = 3;
		label_2.setLayoutData(gridData_2);

		final Label diagramModelMessage = new Label(container, SWT.NONE);
		final GridData gridData_3 = new GridData();
		gridData_3.horizontalSpan = 3;
		diagramModelMessage.setLayoutData(gridData_3);
		diagramModelMessage.setText("Select the diagram model.");
		final Label diagramModel = new Label(container, SWT.NONE);
		final GridData gridData_4 = new GridData();
		gridData_4.horizontalIndent = 20;
		diagramModel.setLayoutData(gridData_4);
		diagramModel.setText(String.format("Diagram Model (*.%s):", diagramExtension));
		diagramFileField = new Text(container, SWT.BORDER);
		diagramFileField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updatePageComplete();
			}
		});
		diagramFileField.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_FILL));
		final Button browseDiagram = new Button(container, SWT.NONE);
		browseDiagram.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseForDiagramModel();
			}
		});
		browseDiagram.setText("Browse...");
		initContents();
	}

	private void initContents() {
		updatePageComplete();
	}

	protected void updatePageComplete() {
		setPageComplete(false);
		IPath domainLoc = getDomainLocation();
		if (domainLoc == null || !domainLoc.toFile().exists()) {
			setMessage(null);
			setErrorMessage("Please select an uml domain model.");
			return;
		}
		IPath diagramLoc = getDiagramLocation();
		if (diagramLoc == null) {
			setMessage(null);
			setErrorMessage("Please select an uml diagram model.");
			return;
		}
		setPageComplete(true);
		setErrorMessage(null);

	}

	private IPath getLocation(Text fileField) {
		String text = fileField.getText().trim();
		if (text.length() == 0)
			return null;
		IPath path = new Path(text);
		if (!path.isAbsolute())
			path = Activator.getDefault().getWorkspaceRoot().getLocation()
					.append(path);
		return path;
	}

	private IPath getDiagramLocation() {
		return this.getLocation(diagramFileField);
	}

	private IPath getDomainLocation() {
		return this.getLocation(domainFileField);
	}

	private String browse(String title, String message, final String extension,
			Text fileField) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(this
				.getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());

		IProject project = Activator.getDefault().getActiveProject();
		dialog.setTitle(title);
		dialog.setMessage(message);

		if (project == null)
			return null;
		dialog.setInput(project);
		dialog.addFilter(new SelectFiles(extension));
		dialog.setAllowMultiple(false);
		dialog.setValidator(new ISelectionStatusValidator() {
			@Override
			public IStatus validate(Object[] selection) {
				return validUseCase(selection, extension);
			}
		});
		if (dialog.open() == Window.CANCEL)
			return null;
		Object[] selectedFiles = dialog.getResult();
		if (validUseCase(selectedFiles, extension) == Status.OK_STATUS) {
			IFile file = (IFile) selectedFiles[0];
			IPath path = file.getLocation();
			IPath rootLoc = Activator.getDefault().getWorkspaceRoot()
					.getLocation();
			if (rootLoc.isPrefixOf(path))
				path = path.setDevice(null).removeFirstSegments(
						rootLoc.segmentCount());
			String fileStr = path.toString();
			fileField.setText(fileStr);
			return fileStr;
		}
		return null;
	}

	protected void browseForDiagramModel() {
		this.diagramFile = this.browse("Import Diagram",
				"Select the elements from the tree:", diagramExtension,
				this.diagramFileField);
	}

	protected void browseForDomainModel() {
		this.modelFile = this.browse("Import Model",
				"Select the elements from the tree:", "uml",
				this.domainFileField);
	}

	private IStatus validUseCase(Object[] selection, String extension) {
		if (selection.length != 1)
			return Status.CANCEL_STATUS; // Erro
		Object resource = selection[0];
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			if (file.getFileExtension().equals(extension))
				return Status.OK_STATUS;
		}
		return Status.CANCEL_STATUS;// erro
	}

	public String getModelFile() {
		return modelFile;
	}

	public String getDiagramFile() {
		return diagramFile;
	}

}
