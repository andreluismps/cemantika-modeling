/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.util.ArrayList;
import java.util.List;

import org.cemantika.testing.contextSource.Calendar;
import org.cemantika.testing.contextSource.GPS;
import org.cemantika.testing.contextSource.WiFi;
import org.cemantika.testing.util.Constants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author MHL
 */
public class PhysicalContext extends AbstractContext{
 //   ContextSource
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -437658113632518586L;
	
	private transient List<ContextDefectPattern> contextDefectPatterns = new ArrayList<ContextDefectPattern>();

	public PhysicalContext(){
		contextDefectPatterns.add(ContextDefectPattern.INCOMPLETE_UNAIVALABALITY);
    }
	
	public PhysicalContext(String name) {
		setName(name);
	}
    
    @Override
    public void addChildContext(AbstractContext context){
        //Physical Context can`t have children
    }
    
    

    public void createPhysicalContextDetails(Group group) throws SecurityException, NoSuchFieldException{}
    
    protected Text createPhysicalContextDetailText(Group group) {
		final Text text = new Text(group, SWT.NONE);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1 ));
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
    
	public static PhysicalContext getBySensorName(String sensor){
		PhysicalContext physicalContext = null;
		
		if (sensor.equals(Constants.WIFI))
			physicalContext = new WiFi();
		else if (sensor.equals(Constants.GPS))
			physicalContext = new GPS();
		else if (sensor.equals(Constants.CALENDAR))
			physicalContext = new Calendar();
		return physicalContext;
	}
    
}
