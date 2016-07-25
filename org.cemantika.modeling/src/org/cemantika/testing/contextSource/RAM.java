package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author MHL
 */
public class RAM extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4131785591185834361L;

	// Model
	private int value1;

	public RAM() {
		super(Constants.RAM);
	}

	public int getValue1() {
		return value1;
	}

	public void setValue1(int value1) {
		this.value1 = value1;
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		createPhysicalContextDetailLabel(group, "RAM Memory in MB");
		Text txtRAMSize = createPhysicalContextDetailText(group);
		addFocusListener(txtRAMSize, RAM.class.getDeclaredField("value1"), this);
		txtRAMSize.setText(String.valueOf(value1));

	}
}
