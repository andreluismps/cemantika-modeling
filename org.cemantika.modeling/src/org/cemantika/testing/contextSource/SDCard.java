package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;

/**
 * 
 * @author MHL
 */
public class SDCard extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4250270265749791102L;

	// Model
	boolean value1;

	public SDCard() {
		super(Constants.SDCARD);
	}

	public boolean isValue1() {
		return value1;
	}

	public void setValue1(boolean value1) {
		this.value1 = value1;
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		Button chkSDCardAvaliable = createPhysicalContextDetailCheckField(group, "SD Card Available");
		addSelectionListener(chkSDCardAvaliable, SDCard.class.getDeclaredField("value1"), this);
		chkSDCardAvaliable.setSelection(value1);

	}

}
