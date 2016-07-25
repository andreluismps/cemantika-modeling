package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author MHL
 */
public class Gyroscope extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2732730733392471458L;
	// Model
	private double value1;
	private double value2;
	private double value3;

	public Gyroscope() {
		super(Constants.GYROSCOPE);
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		createPhysicalContextDetailLabel(group, "X-Axis");
		Text txtXAxis = createPhysicalContextDetailText(group);
		addFocusListener(txtXAxis, Gyroscope.class.getDeclaredField("value1"), this);
		txtXAxis.setText(String.valueOf(value1));

		createPhysicalContextDetailLabel(group, "Y-Axis");
		Text txtYAxis = createPhysicalContextDetailText(group);
		addFocusListener(txtYAxis, Gyroscope.class.getDeclaredField("value2"), this);
		txtYAxis.setText(String.valueOf(value2));

		createPhysicalContextDetailLabel(group, "Z-Axis");
		Text txtZAxis = createPhysicalContextDetailText(group);
		addFocusListener(txtZAxis, Gyroscope.class.getDeclaredField("value3"), this);
		txtZAxis.setText(String.valueOf(value3));
	}

}
