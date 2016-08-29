/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;

import org.cemantika.testing.contextSource.CPU;
import org.cemantika.testing.contextSource.Calendar;
import org.cemantika.testing.contextSource.GPS;
import org.cemantika.testing.contextSource.WiFi;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.google.common.primitives.Doubles;

/**
 * 
 * @author MHL
 */
public class PhysicalContext extends AbstractContext {
	// ContextSource

	/**
	 * 
	 */
	private static final long serialVersionUID = -437658113632518586L;

	private transient List<ContextDefectPattern> contextDefectPatterns = new ArrayList<ContextDefectPattern>();

	public PhysicalContext() {
		contextDefectPatterns.add(ContextDefectPattern.INCOMPLETE_UNAIVALABALITY);

		contextDefectPatterns.add(ContextDefectPattern.PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_LOW_RAM);
		contextDefectPatterns.add(ContextDefectPattern.PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_LOW_DISK);
		contextDefectPatterns.add(ContextDefectPattern.PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_100_PERCENT_CPU);
		contextDefectPatterns.add(ContextDefectPattern.PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_PLUGGED_SDCARD);
		contextDefectPatterns.add(ContextDefectPattern.PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_UNPLUGGED_SDCARD);
		contextDefectPatterns.add(ContextDefectPattern.PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_PLUGGED_USB_CABLE);
		contextDefectPatterns.add(ContextDefectPattern.PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_UNPLUGGED_USB_CABLE);
		contextDefectPatterns.add(ContextDefectPattern.PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_15_PERCENT_BATTERY);
		contextDefectPatterns.add(ContextDefectPattern.PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_5_PERCENT_BATTERY);
		contextDefectPatterns.add(ContextDefectPattern.PROBLEMATIC_RULE_LOGIC_WRONG_BEHAVIOR_1_PERCENT_BATTERY);

	}

	public PhysicalContext(String name) {
		this();
		setName(name);
	}

	@Override
	public void addChildContext(AbstractContext context) {
		// Physical Context can`t have children
	}

	public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException {
	}

	protected Text createPhysicalContextDetailText(Group group) {
		final Text text = new Text(group, SWT.NONE);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		return text;
	}

	protected void createPhysicalContextDetailLabel(Group group, String labelText) {
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		label.setText(labelText);
	}

	protected Button createPhysicalContextDetailCheckField(Group group, String text) {
		Button check = new Button(group, SWT.CHECK);
		check.setText(text);
		check.setSelection(true);
		check.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		return check;
	}

	public void addVerifyDateListener(final Text text) {
		text.addListener(SWT.Verify, new Listener() {
			boolean ignore;

			@Override
			public void handleEvent(Event e) {
				if (ignore)
					return;
				e.doit = false;
				StringBuffer buffer = new StringBuffer(e.text);
				char[] chars = new char[buffer.length()];
				buffer.getChars(0, chars.length, chars, 0);
				if (e.character == '\b') {
					for (int i = e.start; i < e.end; i++) {
						// "dd.mm.yyyy"
						switch (i) {
						case 6: /* [Y]YYY */
						case 7: /* Y[Y]YY */
						case 8: /* YY[Y]Y */
						case 9: /* YYY[Y] */{
							buffer.append('y');
							break;
						}
						case 3: /* [M]M */
						case 4: /* M[M] */{
							buffer.append('m');
							break;
						}
						case 0: /* [D]D */
						case 1: /* D[D] */{
							buffer.append('d');
							break;
						}
						case 2: /* dd[.]mm */
						case 5: /* mm[.]yyyy */{
							buffer.append('.');
							break;
						}
						default:
							return;
						}
					}
					text.setSelection(e.start, e.start + buffer.length());
					ignore = true;
					text.insert(buffer.toString());
					ignore = false;
					text.setSelection(e.start, e.start);
					return;
				}

				int start = e.start;
				if (start > 9)
					return;
				int index = 0;
				for (int i = 0; i < chars.length; i++) {
					if (start + index == 2 || start + index == 5) {
						if (chars[i] == '.') {
							index++;
							continue;
						}
						buffer.insert(index++, '.');
					}
					if (chars[i] < '0' || '9' < chars[i])
						return;
					if (start + index == 3 && '1' < chars[i])
						return; /* [M]M */
					if (start + index == 0 && '3' < chars[i])
						return; /* [D]D */
					index++;
				}
				String newText = buffer.toString();
				int length = newText.length();
				StringBuffer date = new StringBuffer(text.getText());
				date.replace(e.start, e.start + length, newText);
				java.util.Calendar calendar = java.util.Calendar.getInstance();
				calendar.set(java.util.Calendar.YEAR, 2000);
				calendar.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY);
				calendar.set(java.util.Calendar.DATE, 1);
				String yyyy = date.substring(6, 10);
				if (yyyy.indexOf('y') == -1) {
					int year = Integer.parseInt(yyyy);
					calendar.set(java.util.Calendar.YEAR, year);
				}
				String mm = date.substring(3, 5);
				if (mm.indexOf('m') == -1) {
					int month = Integer.parseInt(mm) - 1;
					int maxMonth = calendar.getActualMaximum(java.util.Calendar.MONTH);
					if (0 > month || month > maxMonth)
						return;
					calendar.set(java.util.Calendar.MONTH, month);
				}
				String dd = date.substring(0, 2);
				if (dd.indexOf('d') == -1) {
					int day = Integer.parseInt(dd);
					int maxDay = calendar.getActualMaximum(java.util.Calendar.DATE);
					if (1 > day || day > maxDay)
						return;
					calendar.set(java.util.Calendar.DATE, day);
				} else {
					if (calendar.get(java.util.Calendar.MONTH) == java.util.Calendar.FEBRUARY) {
						char firstChar = date.charAt(0);
						if (firstChar != 'd' && '2' < firstChar)
							return;
					}
				}
				text.setSelection(e.start, e.start + length);
				ignore = true;
				text.insert(newText);
				ignore = false;
			}
		});
	}

	public void addVerifyTimeListener(final Text text) {
		text.addListener(SWT.Verify, new Listener() {
			boolean ignore;

			@Override
			public void handleEvent(Event e) {
				if (ignore)
					return;
				e.doit = false;
				StringBuffer buffer = new StringBuffer(e.text);
				char[] chars = new char[buffer.length()];
				buffer.getChars(0, chars.length, chars, 0);
				if (e.character == '\b') {
					for (int i = e.start; i < e.end; i++) {
						// "hh:mm:ss"
						switch (i) {
						case 0: /* [h]h */
						case 1: /* h[h] */{
							buffer.append('h');
							break;
						}
						case 3: /* [m]m */
						case 4: /* m[m] */{
							buffer.append('m');
							break;
						}
						case 6: /* [s]s */
						case 7: /* s[s] */{
							buffer.append('s');
							break;
						}

						case 2: /* hh[:]mm */
						case 5: /* mm[:]ss */{
							buffer.append(':');
							break;
						}
						default:
							return;
						}
					}
					text.setSelection(e.start, e.start + buffer.length());
					ignore = true;
					text.insert(buffer.toString());
					ignore = false;
					text.setSelection(e.start, e.start);
					return;
				}

				int start = e.start;
				if (start > 7)
					return;
				int index = 0;
				for (int i = 0; i < chars.length; i++) {
					if (start + index == 2 || start + index == 5) {
						if (chars[i] == ':') {
							index++;
							continue;
						}
						buffer.insert(index++, ':');
					}
					if (chars[i] < '0' || '9' < chars[i])
						return;
					if (start + index == 0 && '2' < chars[i])
						return; /* [D]D */
					if (start + index == 3 && '5' < chars[i])
						return; /* [M]M */
					if (start + index == 6 && '5' < chars[i])
						return; /* [D]D */

					index++;
				}
				String newText = buffer.toString();
				int length = newText.length();
				StringBuffer date = new StringBuffer(text.getText());
				date.replace(e.start, e.start + length, newText);
				java.util.Calendar calendar = java.util.Calendar.getInstance();
				calendar.set(java.util.Calendar.YEAR, 2000);
				calendar.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY);
				calendar.set(java.util.Calendar.DATE, 1);
				String ss = date.substring(6, 8);
				if (ss.indexOf('s') == -1) {
					int second = Integer.parseInt(ss);
					calendar.set(java.util.Calendar.SECOND, second);
				}
				String mm = date.substring(3, 5);
				if (mm.indexOf('m') == -1) {
					int minute = Integer.parseInt(mm);
					int maxMinute = calendar.getActualMaximum(java.util.Calendar.MINUTE);
					if (0 > minute || minute > maxMinute)
						return;
					calendar.set(java.util.Calendar.MINUTE, minute);
				}
				String hh = date.substring(0, 2);
				if (hh.indexOf('h') == -1) {
					int hour = Integer.parseInt(hh);
					int maxHour = calendar.getActualMaximum(java.util.Calendar.HOUR_OF_DAY);
					if (0 > hour || hour > maxHour)
						return;
					calendar.set(java.util.Calendar.DATE, hour);
				}
				text.setSelection(e.start, e.start + length);
				ignore = true;
				text.insert(newText);
				ignore = false;
			}
		});
	}

	public void addVerifyDoubleDigitListener(final Text text) {
		text.addListener(SWT.Verify, new Listener() {
			
			@Override
			public void handleEvent(Event e) {
				
				String allowedCharacters = "0123456789.-";
			    String buffer = e.text;
			    int position = e.start;
			    
			    if (e.character == '-' && position != 0){
			    	e.doit = false;
			    	return;
			    }
			    
			    buffer += text.getText();
			    int decimalSeparatorCount = 0, minusCount = 0;
			    for (int index = 0; index < buffer.length(); index++) {
			        char character = buffer.charAt(index);
			        if(character == '-') minusCount++;
			        if(character == '.') decimalSeparatorCount++;
			        boolean isAllowed = allowedCharacters.indexOf(character) > -1 && decimalSeparatorCount <= 1 && minusCount <= 1;

			        if (!isAllowed) {
			            e.doit = false;
			            return;
			        }
			    }
			}
		});
	}

	public void setContextDefectPatterns(List<ContextDefectPattern> contextDefectPatterns) {
		this.contextDefectPatterns = contextDefectPatterns;
	}

	public List<ContextDefectPattern> getContextDefectPatterns() {
		return contextDefectPatterns;
	}

	public static PhysicalContext newInstance(PhysicalContext context) {
		// TODO Auto-generated method stub
		return context;
	}

	public static PhysicalContext getBySensorName(String sensor) {
		PhysicalContext physicalContext = null;

		if (sensor.equals(Constants.WIFI))
			physicalContext = new WiFi();
		else if (sensor.equals(Constants.GPS))
			physicalContext = new GPS();
		else if (sensor.equals(Constants.CALENDAR))
			physicalContext = new Calendar();
		else if (sensor.equals(Constants.CPU))
			physicalContext = new CPU();
		return physicalContext;
	}

}
