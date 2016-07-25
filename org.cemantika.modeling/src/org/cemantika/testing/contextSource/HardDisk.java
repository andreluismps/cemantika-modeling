package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author MHL
 */
public class HardDisk extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2531644377220241996L;

	// Model
	private int value1;

	public HardDisk() {
		super(Constants.DISK);
	}

	public int getValue1() {
		return value1;
	}

	public void setValue1(int value1) {
		this.value1 = value1;
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		createPhysicalContextDetailLabel(group, "Hard Disk Memory in MB");
		Text txtHDMemory = createPhysicalContextDetailText(group);
		addFocusListener(txtHDMemory, HardDisk.class.getDeclaredField("value1"), this);
		txtHDMemory.setText(String.valueOf(value1));

	}

}
