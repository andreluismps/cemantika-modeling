package org.cemantika.modeling.view.outline;

import org.cemantika.modeling.view.tableviewer.model.NamedElement;
import org.cemantika.modeling.view.tableviewer.model.NamedElement.ElementType;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class CemantikaOutline extends ContentOutlinePage {

	private IEditorInput input;

	public CemantikaOutline(IEditorInput iEditorInput) {
		this.setInput(iEditorInput);
	}

	public void createControl(Composite parent) {

		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new CemantikaOutlineContentProvider());
		viewer.setLabelProvider(new CemantikaOutlineLabelProvider());
		viewer.addSelectionChangedListener(this);

		viewer.setInput(getInialInput());

	}

	private Object getInialInput() {
		NamedElement process = new NamedElement("Cemantika Design Process",
				ElementType.PROCESS);

		NamedElement specification = new NamedElement("Context Specification",
				ElementType.ACTIVITY);
		NamedElement management = new NamedElement("Context Management",
				ElementType.ACTIVITY);

		process.addChildren(specification).addChildren(management);

		NamedElement identifyFocus = new NamedElement("Identify Focus",
				ElementType.TASK);
		NamedElement identifyBehaviorVariations = new NamedElement(
				"Identify Behavior Variations", ElementType.TASK);

		specification.addChildren(identifyFocus).addChildren(
				identifyBehaviorVariations);

		return process;
	}

	public void setInput(IEditorInput input) {
		this.input = input;
	}

	public IEditorInput getInput() {
		return input;
	}

}
