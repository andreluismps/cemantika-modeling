package org.cemantika.uml.util;

import org.eclipse.uml2.uml.NamedElement;

public interface FilterElement {

	boolean include(NamedElement namedElement);

}
