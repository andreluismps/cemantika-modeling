package org.cemantika.modeling.view.outline;

import org.cemantika.modeling.view.tableviewer.model.NamedElement;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class CemantikaOutlineContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[1];

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof NamedElement) {
			NamedElement process = (NamedElement) parentElement;
			return process.getChildren().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof NamedElement) {
			return ((NamedElement) element).getParent();
		}
		return null;

	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
