package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;

/**
 * 
 * @author MHL
 */
public class Bluetooth extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7463349899162958539L;

	// Model
	boolean value1;

	public Bluetooth() {
		super(Constants.BLUETOOTH);
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		Button chkBluetoothAvaliable = createPhysicalContextDetailCheckField(group, "Active Bluetooth-Connection");
		addSelectionListener(chkBluetoothAvaliable, Bluetooth.class.getDeclaredField("value1"), this);
		chkBluetoothAvaliable.setSelection(value1);

	}

}
