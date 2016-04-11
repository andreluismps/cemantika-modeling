/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cemantika.testing.model;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 *
 * @author MHL
 */
public class Situation extends AbstractContext{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6275195839246421095L;
	
	private String expectedBehavior; 

	public Situation(String name){
      setName(name);    
    }
        
    @Override
    public void addChildContext(AbstractContext context){
        if(!(context instanceof Scenario)){
          getContextList().add(context);  
        }
    }

	public void setExpectedBehavior(String expectedBehavior) {
		this.expectedBehavior = expectedBehavior;
	}

	public String getExpectedBehavior() {
		return expectedBehavior;
	}

	public static Situation newInstance(Situation context) {
		Situation newInstance = new Situation(context.getName());
		newInstance.setContextList(new ArrayList<AbstractContext>(context.getContextList()));
		return newInstance;
	}
	
	public void createLogicalContextDetails(Group group) throws SecurityException, NoSuchFieldException{
		
		createSituationDetailLabel(group, "Situation Name");
		Text txtSituationName = createSituationDetailText(group, SWT.NONE, 1);
        addFocusListener(txtSituationName, AbstractContext.class.getDeclaredField("name"), this);
        txtSituationName.setText((getName() != null) ? getName() : "");
		
        createSituationDetailLabel(group, "");
        createSeparatorLine(group);
        createSituationDetailLabel(group, "");
        
        createSituationDetailLabel(group, "Logical Contexts in Situation");
		for (AbstractContext abstractContext : this.getContextList()) {
			if (abstractContext instanceof LogicalContext)
				createSituationDetailLabel(group, " - " + abstractContext.getName());
		}
		
		createSituationDetailLabel(group, "");
		createSeparatorLine(group);
		createSituationDetailLabel(group, "");
		
		createSituationDetailLabel(group, "Expected Behavior");
		Text txtExpectedBehavior = createSituationDetailText(group, SWT.MULTI| SWT.WRAP | SWT.V_SCROLL, 5);
        addFocusListener(txtExpectedBehavior, Situation.class.getDeclaredField("expectedBehavior"), this);
        txtExpectedBehavior.setText((expectedBehavior != null) ? expectedBehavior : "");
		
	}

	private void createSeparatorLine(Group group) {
		Label separator = new Label(group, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
    
	protected void createSituationDetailLabel(Group group, String labelText) {
		Label label = new Label(group, SWT.NONE | SWT.WRAP);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        label.setText(labelText);
	}
	
	
	
	protected Text createSituationDetailText(Group group, int style, int lines) {
		final Text text = new Text(group, style);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = lines * text.getLineHeight();
        text.setLayoutData(gridData);
		return text;
	}


}
