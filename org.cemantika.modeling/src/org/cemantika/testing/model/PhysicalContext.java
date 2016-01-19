/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
	
	private List<ContextDefectPattern> contextDefectPatterns = new ArrayList<ContextDefectPattern>();

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
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label.setText(labelText);
	}
	
	protected Button createPhysicalContextDetailCheckField(Group group, String text) {
		Button check = new Button(group, SWT.CHECK);
        check.setText(text);
		check.setSelection(true);
        check.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        return check;
	}
	
	protected void addFocusListener(final Text textField, final Field classField, final Object instance){
		textField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				try {
					classField.setAccessible(true);
					if (classField.getType().equals(double.class))
						classField.setDouble(instance, Double.parseDouble(textField.getText()));
					else if (classField.getType().equals(String.class))
						classField.set(instance, textField.getText());
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}				
			}
			
			@Override
			public void focusGained(FocusEvent e) {}
		});
	}
	
	protected void addSelectionListener(final Button checkField, final Field classField, final Object instance){
		checkField.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				classField.setAccessible(true);
				
				try {
					classField.setBoolean(instance, checkField.getSelection());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public void setContextDefectPatterns(List<ContextDefectPattern> contextDefectPatterns) {
		this.contextDefectPatterns = contextDefectPatterns;
	}

	public List<ContextDefectPattern> getContextDefectPatterns() {
		return contextDefectPatterns;
	}
    
    
}
