package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author MHL
 */
public class Thermometer extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3812780275455481058L;

	// Model
	private int value1;

	public Thermometer() {
		super(Constants.THERMOMETER);
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		createPhysicalContextDetailLabel(group, "Temperature in Celsius");
		Text txtThermometer = createPhysicalContextDetailText(group);
		addFocusListener(txtThermometer, Thermometer.class.getDeclaredField("value1"), this);
		txtThermometer.setText(String.valueOf(value1));

	}

}
