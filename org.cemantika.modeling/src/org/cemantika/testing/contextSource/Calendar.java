package org.cemantika.testing.contextSource;

import org.cemantika.testing.model.ContextDefectPattern;
import org.cemantika.testing.model.PhysicalContext;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author MHL
 */
public class Calendar extends PhysicalContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4598288158432074459L;
	// Model
	private String appointmentName;
	private String startTime;
	private String endTime;
	private String date;

	public Calendar() {
		super(Constants.CALENDAR);

		getContextDefectPatterns().add(ContextDefectPattern.SLOW_SENSING_OUT_OF_DATENESS);
		// getContextDefectPatterns().add(ContextDefectPattern.SLOW_SENSING_WRONG_INTERPRETATION);
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {

		createPhysicalContextDetailLabel(group, "Appointment Name");
		Text txtAppointmentName = createPhysicalContextDetailText(group);
		addFocusListener(txtAppointmentName, Calendar.class.getDeclaredField("appointmentName"), this);
		txtAppointmentName.setText((appointmentName != null) ? appointmentName : "");

		createPhysicalContextDetailLabel(group, "Start (hh:mm:ss)");
		Text txtStartTime = createPhysicalContextDetailText(group);
		addFocusListener(txtStartTime, Calendar.class.getDeclaredField("startTime"), this);
		startTime = (startTime == null || startTime.isEmpty()) ? "hh:mm:ss" : startTime;
		txtStartTime.setText(startTime);
		addVerifyTimeListener(txtStartTime);

		createPhysicalContextDetailLabel(group, "End (hh:mm:ss)");
		Text txtEndTime = createPhysicalContextDetailText(group);
		addFocusListener(txtEndTime, Calendar.class.getDeclaredField("endTime"), this);
		endTime = (endTime == null || endTime.isEmpty()) ? "hh:mm:ss" : endTime;
		txtEndTime.setText(endTime);
		addVerifyTimeListener(txtEndTime);

		createPhysicalContextDetailLabel(group, "Date (dd.mm.yyyy)");
		Text txtDate = createPhysicalContextDetailText(group);
		addFocusListener(txtDate, Calendar.class.getDeclaredField("date"), this);
		date = (date == null || date.isEmpty()) ? "dd.mm.yyyy" : date;
		txtDate.setText(date);
		addVerifyDateListener(txtDate);
	}

}
