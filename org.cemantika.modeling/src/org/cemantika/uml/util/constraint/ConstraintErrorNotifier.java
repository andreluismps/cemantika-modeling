package org.cemantika.uml.util.constraint;

import org.cemantika.modeling.Activator;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class ConstraintErrorNotifier {

	private IResource resource;
	public static final String MARKER_ID = Activator.PLUGIN_ID
			+ ".cemantikamarker";

	public ConstraintErrorNotifier(IResource resource) {
		this.resource = resource;
	}

	public void clean() {
		try {
			resource.deleteMarkers(MARKER_ID, false, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void addProblem(String message) throws CoreException {
		IMarker marker = resource.createMarker(MARKER_ID);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		marker.setAttribute(IMarker.LOCATION, "");
	}

}
