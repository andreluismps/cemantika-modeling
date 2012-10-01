package org.cemantika.modeling.form;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.modeling.listener.overview.CreateConceptualModel;
import org.cemantika.modeling.listener.overview.CreateUseCaseModel;
import org.cemantika.modeling.listener.overview.ImportUmlArtifact;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class CemantikaOverView extends FormPage {

	private FormToolkit toolkit;
	private FormText doneText;
	private ScrolledForm scrolledForm;
	private PluginManager manager;
	private FormEditor editor;
	private static final String TITLE = "Getting Started";
	private static final String ABOUT = "CEManTIKA is a framework to support context modeling and"
			+ " Context-Sensitive System design, in a generic, domain-independent way. To learn more about it follow the "
			+ "Cemantika Design Process.\nTo get started with Cemantika Project you need use case model and conceptual model artifacts.";
	public static final String ID = CemantikaOverView.class.getName();
	private static final String IMPORT_DESCRIPTION = "Use Case.";
	private static final String IMPORT_CONCEPTUAL_DESCRIPTION = "Conceptual Model";

	public CemantikaOverView(FormEditor editor) {
		super(editor, ID, TITLE);
		this.editor = editor;
		this.manager = (PluginManager) editor;
	}

	protected void createFormContent(IManagedForm managedForm) {
		toolkit = managedForm.getToolkit();
		scrolledForm = managedForm.getForm();

		scrolledForm.setText(TITLE);

		TableWrapLayout layout = new TableWrapLayout();
		scrolledForm.getBody().setLayout(layout);
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;

		addOverviewSection();
		addImportUseCaseSection();
		addImportConceptualModel();
	}

	private void addImportConceptualModel() {
		Section importConceptualModel = CemantikaForm.createSection(toolkit,
				scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Import Conceptual Model", IMPORT_CONCEPTUAL_DESCRIPTION);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		importConceptualModel.setLayoutData(td);

		Composite sectionClient = toolkit.createComposite(importConceptualModel);

		TableWrapLayout layout = new TableWrapLayout();
		sectionClient.setLayout(layout);
		layout.numColumns = 1;

		Activator plugin = Activator.getDefault();
		IWorkbench workbench = plugin.getWorkbench();
		Shell shell = workbench.getActiveWorkbenchWindow().getShell();

		td = new TableWrapData();
		FormText formText = toolkit.createFormText(sectionClient, true);
		formText.setLayoutData(td);
		formText
				.addHyperlinkListener(new ImportSectionListener(shell, manager));

		StringBuffer html = new StringBuffer();
		html
				.append("<form>")
				.append(
						"<li><a href=\"Create Conceptual Model\">Create</a> it if your project does not have one.</li>")
				.append(
						"<li>After that <a href=\"Import Conceptual Model\">Import</a> it to project.</li>")
				.append("</form>");
		formText.setText(html.toString(), true, true);

		importConceptualModel.setClient(sectionClient);
	}

	private void addImportUseCaseSection() {
		Section importUseCase = CemantikaForm.createSection(toolkit,
				scrolledForm, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				"Import Use Case", IMPORT_DESCRIPTION);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		importUseCase.setLayoutData(td);

		Composite sectionClient = toolkit.createComposite(importUseCase);

		TableWrapLayout layout = new TableWrapLayout();
		sectionClient.setLayout(layout);
		layout.numColumns = 1;

		Activator plugin = Activator.getDefault();
		IWorkbench workbench = plugin.getWorkbench();
		Shell shell = workbench.getActiveWorkbenchWindow().getShell();

		td = new TableWrapData();
		FormText formText = toolkit.createFormText(sectionClient, true);
		formText.setLayoutData(td);
		formText
				.addHyperlinkListener(new ImportSectionListener(shell, manager));

		StringBuffer html = new StringBuffer();
		html
				.append("<form>")
				.append(
						"<li><a href=\"Create\">Create</a> it if your project does not have one.</li>")
				.append(
						"<li>After that <a href=\"Import\">Import</a> it to project.</li>")
				.append("</form>");
		formText.setText(html.toString(), true, true);

		importUseCase.setClient(sectionClient);
	}

	private void addOverviewSection() {
		Section overview = CemantikaForm.createSection(toolkit, scrolledForm,
				Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED,
				"Overview", ABOUT);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		overview.setLayoutData(td);

		Composite sectionClient = toolkit.createComposite(overview);
		sectionClient.setLayout(new TableWrapLayout());

		doneText = toolkit.createFormText(sectionClient, true);
		td = new TableWrapData();
		doneText.setLayoutData(td);
		doneText.addHyperlinkListener(new GotoPage());
		StringBuffer html = new StringBuffer();
		html = new StringBuffer();
		html
				.append("<form>")
				.append(
						"<p><span color=\"done\">Congratulations!</span> You have already imported Use Case and Conceptual Models.</p>")
				.append(
						"<p>Go to <a href=\"Specification\">first Activity</a> of Cemantika.</p>")
				.append("</form>");
		doneText.setColor("done", ColorConstants.red);
		doneText.setText(html.toString(), true, true);
		updateImportSection();
		overview.setClient(sectionClient);
	}

	private class GotoPage extends HyperlinkAdapter {

		@Override
		public void linkActivated(HyperlinkEvent e) {
			editor.setActivePage(ContextSpecification.ID);
		}

	}

	private class ImportSectionListener extends HyperlinkAdapter {

		private Shell shell;
		private PluginManager manager;

		public ImportSectionListener(Shell shell, PluginManager manager) {
			this.shell = shell;
			this.manager = manager;
		}

		@Override
		public void linkActivated(HyperlinkEvent e) {
			String label = (String) e.getHref();
			if (label.equals("Create")) {
				new CreateUseCaseModel(shell).handleEvent(null);
			} else if (label.equals("Import")) {
				new ImportUmlArtifact(shell, manager, PluginManager.USE_CASE_MODEL, PluginManager.USE_CASE_DIAGRAM, "Import Use Case Model", "Import Use Case Domain Model and Use Case Diagram Model", "umlusc").handleEvent(null);
				updateImportSection();
			} else if (label.equals("Create Conceptual Model")) {
				new CreateConceptualModel(shell).handleEvent(null);
			} else if (label.equals("Import Conceptual Model")) {
				new ImportUmlArtifact(shell, manager, PluginManager.CONCEPTUAL_MODEL, PluginManager.CONCEPTUAL_DIAGRAM, "Import Conceptual Model", "Import Conceptual Domain Model and Conceptual Diagram Model", "umlclass").handleEvent(null);
			}
		}

	}

	public void updateImportSection() {
		doneText.setVisible(manager.alreadyImportUseCase() && manager.alreadyImportConceptualModel());
	}

}
