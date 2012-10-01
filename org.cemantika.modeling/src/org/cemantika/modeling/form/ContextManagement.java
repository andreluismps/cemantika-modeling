package org.cemantika.modeling.form;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


public class ContextManagement extends FormPage {
	
	private FormToolkit toolkit;
	private ScrolledForm scrolledForm;
	
	public static final String ID = ContextManagement.class.getName();
	private static final String TITLE = "Context Management";
	
	public ContextManagement(FormEditor editor) {
		super(editor, ID, TITLE);
	}	
	
	protected void createFormContent(IManagedForm managedForm) {
		toolkit = managedForm.getToolkit();
		scrolledForm = managedForm.getForm();
		
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 2;

        scrolledForm.getBody().setLayout(layout);
        
        scrolledForm.setText(TITLE);
        
        addControls();		
	}

	private void addControls() {
		
	}	
		

}
