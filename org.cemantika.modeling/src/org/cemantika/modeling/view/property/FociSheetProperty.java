package org.cemantika.modeling.view.property;

import java.util.List;

import org.cemantika.modeling.form.CemantikaForm;
import org.cemantika.modeling.form.ContextSpecification;
import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.uml.model.Focus;
import org.cemantika.uml.util.UmlUtils;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.uml2.diagram.usecase.edit.parts.PackageEditPart;
import org.eclipse.uml2.uml.Package;

public class FociSheetProperty extends AbstractPropertySection {

	private FormText fociForm;
	private org.eclipse.uml2.uml.Package package_;

	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		fociForm = getWidgetFactory().createFormText(composite, true);
	}

	protected Object transformSelection(Object selected) {

		if (selected instanceof EditPart) {
			Object model = ((EditPart) selected).getModel();
			return model instanceof View ? ((View) model).getElement() : null;
		}

		return selected;
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		if (input instanceof PackageEditPart) {
			PackageEditPart p = (PackageEditPart) input;
			package_ = (Package) transformSelection(p);
			this.refresh(false);

			p.addEditPartListener(new EditPartListener() {

				@Override
				public void selectedStateChanged(EditPart editpart) {
					refresh(false);
				}

				@Override
				public void removingChild(EditPart child, int index) {
					refresh(false);
				}

				@Override
				public void partDeactivated(EditPart editpart) {
					refresh(true);
				}

				@Override
				public void partActivated(EditPart editpart) {
					refresh(false);
				}

				@Override
				public void childAdded(EditPart child, int index) {
					refresh(false);
				}
			});
		}
	}

	@Override
	public void refresh() {
		super.refresh();
		this.refresh(true);
	}

	private String listFoci() {
		UmlUtils uml = new UmlUtils();
		List<Focus> foci = uml.showFoci(package_);
		StringBuffer html = new StringBuffer();
		html = new StringBuffer();
		html.append("<form>");
		for (Focus focus : foci) {
			html.append("<li>").append(focus.toString()).append("</li>");
		}
		html.append("</form>");
		return html.toString();
	}

	public void refresh(boolean closing) {
		if (!closing) {
			String foci = this.listFoci();
			fociForm.setText(foci, true, true);
		}

		PluginManager manager = CemantikaForm.currentInstance();
		if (manager != null) {
			CemantikaForm form = (CemantikaForm) manager;
			ContextSpecification specification = form.getContextSpecification();
			specification.updateIdentifyBehaviorVariations(specification
					.listFoci(true, "<p>", "</p>"));
		}
	}
}
