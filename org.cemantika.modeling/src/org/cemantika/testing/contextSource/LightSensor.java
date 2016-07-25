package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author MHL
 */
public class LightSensor extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7944838634690493498L;

	// Model
	private int value1;

	public LightSensor() {
		super(Constants.LIGHTSENSOR);
	}

	public int getValue1() {
		return value1;
	}

	public void setValue1(int value1) {
		this.value1 = value1;
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		createPhysicalContextDetailLabel(group, "Light Sensor in Lux");
		Text txtLight = createPhysicalContextDetailText(group);
		addFocusListener(txtLight, LightSensor.class.getDeclaredField("value1"), this);
		txtLight.setText(String.valueOf(value1));

	}
}
