package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author MHL
 */
public class CPU extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7917638447320087240L;

	// Model
	private int value1;

	public CPU() {
		super(Constants.CPU);
	}
	
	public int getValue1() {
		return value1;
	}

	public void setValue1(int value1) {
		this.value1 = value1;
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		createPhysicalContextDetailLabel(group, "CPU Usage in %");
		Text txtUsage = createPhysicalContextDetailText(group);
		addFocusListener(txtUsage, CPU.class.getDeclaredField("value1"), this);
		txtUsage.setText(String.valueOf(value1));

	}

}
