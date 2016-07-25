package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author MHL
 */
public class Barometer extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8793645815819328918L;

	// Model
	private int value1;

	public Barometer() {
		super(Constants.BAROMETER);
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		createPhysicalContextDetailLabel(group, "Barometer in hPa");
		Text txtBarometer = createPhysicalContextDetailText(group);
		addFocusListener(txtBarometer, Barometer.class.getDeclaredField("value1"), this);
		txtBarometer.setText(String.valueOf(value1));

	}

}
