package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;

/**
 * 
 * @author MHL
 */
public class USBCable extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6364060773441832300L;

	// Model
	boolean value1;

	public USBCable() {
		super(Constants.USBCABLE);
	}

	public boolean isValue1() {
		return value1;
	}

	public void setValue1(boolean value1) {
		this.value1 = value1;
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		Button chkUSBCableConnected = createPhysicalContextDetailCheckField(group, "USB-Cable connected");
		addSelectionListener(chkUSBCableConnected, USBCable.class.getDeclaredField("value1"), this);
		chkUSBCableConnected.setSelection(value1);

	}

}
