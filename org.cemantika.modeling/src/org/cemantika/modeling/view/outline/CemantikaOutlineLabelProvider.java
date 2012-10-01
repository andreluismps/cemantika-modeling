package org.cemantika.modeling.view.outline;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.view.tableviewer.model.INamedElement;
import org.cemantika.modeling.view.tableviewer.model.NamedElement;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class CemantikaOutlineLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof INamedElement) {
			INamedElement named = (INamedElement) element;
			return named.getName();
		}
		return null;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof NamedElement) {
			NamedElement named = (NamedElement) element;
			
			switch (named.getType()) {
			case PROCESS:
				return Activator.getDefault().getImageRegistry().get(Activator.CEMANTIKA_PROCESS);
			case ACTIVITY:
				return Activator.getDefault().getImageRegistry().get(Activator.CEMANTIKA_ACTIVITY);
			case TASK:
				return Activator.getDefault().getImageRegistry().get(Activator.CEMANTIKA_TASK);
			case DELIVERABLE:
				return Activator.getDefault().getImageRegistry().get(Activator.CEMANTIKA_DELIVERABLE);

			}
		}
			
		return super.getImage(element);
	}

}

	