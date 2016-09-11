package org.cemantika.modeling.view.property.filter;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.IFilter;

public class CssFociFilter implements IFilter {
	
	@Override
	public boolean select(Object toTest) {
		Object transformed = transformSelection(toTest);
		return isValid(transformed);
	}

	private boolean isValid(Object transformed) {
		return true;
//		if (package_ != null)
//			return true;
//		System.out.println(transformed.getClass());
//		if (transformed instanceof org.eclipse.uml2.uml.Package) {
//			package_ = (org.eclipse.uml2.uml.Package) transformed;
//			UmlUtils uml = new UmlUtils();
//			Profile profile = uml.getCemantikaProfile();
//			System.out.println("here");
//			return uml.alreadyApplied(package_, profile);
//		}
//		return false;
	}

	protected Object transformSelection(Object selected) {
		if (selected instanceof EditPart) {
			Object model = ((EditPart) selected).getModel();
			return model instanceof View ? ((View) model).getElement() : null;
		}
		if (selected instanceof View) {
			return ((View) selected).getElement();
		}
		if (selected instanceof IAdaptable) {
			View view = (View) ((IAdaptable) selected).getAdapter(View.class);
			if (view != null) {
				return view.getElement();
			}
		}
		return selected;
	}

}
