package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author MHL
 */
public class Battery extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2506576756874125301L;

	// Model
	private int value1;

	public Battery() {
		super(Constants.BATTERY);
	}

	public int getValue1() {
		return value1;
	}

	public void setValue1(int value1) {
		this.value1 = value1;
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		createPhysicalContextDetailLabel(group, "Battery Level in %");
		Text txtAltitude = createPhysicalContextDetailText(group);
		addFocusListener(txtAltitude, Battery.class.getDeclaredField("value1"), this);
		txtAltitude.setText(String.valueOf(value1));

	}

}
