package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;

/**
 * 
 * @author MHL
 */
public class Infrared extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = -209700603369259323L;

	// Model
	boolean value1;

	public Infrared() {
		super(Constants.INFRARED);
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		Button chkInfraredAvaliable = createPhysicalContextDetailCheckField(group, "Active Bluetooth-Connection");
		addSelectionListener(chkInfraredAvaliable, Infrared.class.getDeclaredField("value1"), this);
		chkInfraredAvaliable.setSelection(value1);

	}

}
