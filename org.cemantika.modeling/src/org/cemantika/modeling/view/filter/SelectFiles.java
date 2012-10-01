package org.cemantika.modeling.view.filter;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class SelectFiles extends ViewerFilter {

	private String extension;

	public SelectFiles(String extension) {
		this.extension = extension;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IFile) {
			IFile file = (IFile) element;
			String extension = file.getFileExtension();
			return extension.equals(this.extension);
		} else {
			return true;
		}
	}
}