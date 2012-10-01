package org.cemantika.modeling.view.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ContextBehaviorDialog extends Dialog {

	private Text behaviorVariations;
	private String behavior;

	public ContextBehaviorDialog(Shell parentShell, String behavior) {
		super(parentShell);
		this.behavior = behavior;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);

		final Label filterLabel = new Label(container, SWT.NONE);
		filterLabel.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.CENTER, false, false));
		filterLabel.setText("Enter Focus Behavior Variations:");

		behaviorVariations = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		behaviorVariations.setLayoutData(new GridData(GridData.FILL_BOTH));
		

		initContent();
		return container;
	}

	private void initContent() {
		behaviorVariations.setText(behavior);
		this.behaviorVariations.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				behavior = behaviorVariations.getText();
			}
		});
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Foco Y:" + " Context Behavior Variations");
		newShell.setSize(500, 350);
		Monitor primary = newShell.getDisplay().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = newShell.getBounds();		
		int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    newShell.setLocation(x, y);		
	}

	public String getBehavior() {
		return this.behavior;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

}
