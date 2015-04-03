package org.cemantika.modeling.form;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import org.cemantika.modeling.internal.manager.PluginManager;
import org.cemantika.modeling.view.outline.CemantikaOutline;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class CemantikaForm extends FormEditor implements PluginManager {

	private static final int CEMANTIKA_OVERVIEW = 0;
	private static final int CONTEXT_SPECIFICATION = 1;
	private static final int CONTEXT_MANAGEMENT = 2;
	private static final int EXTRAS = 3;
	private static final int TEXT_EDITOR = 4;
	private IContentOutlinePage contentOutline;
	private TextEditor textEditor;
	private CemantikaOverView cemantikaOverView;
	private ContextSpecification contextSpecification;
	private ContextManagement contextManagement;
	private Extras extras;
	Properties map = new Properties();
	private boolean hasChanged;
	private static PluginManager CURRENT_INSTANCE;

	public CemantikaForm() {
		CURRENT_INSTANCE = this;
	}

	public static PluginManager currentInstance() {
		return CURRENT_INSTANCE;
	}

	public ContextSpecification getContextSpecification() {
		return contextSpecification;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (!(input instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input");

		super.init(site, input);
	}

	@Override
	protected void addPages() {
		try {
			cemantikaOverView = new CemantikaOverView(this);
			contextSpecification = new ContextSpecification(this);
			contextManagement = new ContextManagement(this);
			extras = new Extras(this);
			textEditor = new TextEditor();

			this.addPage(cemantikaOverView);

			this.addPage(contextSpecification);
			this.addPage(contextManagement);
			this.addPage(extras);
			int index = this.addPage(textEditor, getEditorInput());
			setPageText(index, "Source");
			textEditor.getDocumentProvider().getDocument(
					textEditor.getEditorInput()).addDocumentListener(
					new IDocumentListener() {

						@Override
						public void documentChanged(DocumentEvent event) {
							hasChanged = true;
							load();
						}

						@Override
						public void documentAboutToBeChanged(DocumentEvent event) {
							// TODO Auto-generated method stub

						}
					});
			this.load();
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		textEditor.doSave(monitor);
	}

	public FormToolkit createToolkit(Display display) {
		return new FormToolkit(display);
	}

	@Override
	public void doSaveAs() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IContentOutlinePage.class)) {
			if (contentOutline == null) {
				contentOutline = new CemantikaOutline(this.getEditorInput());
			}
			return contentOutline;
		}
		return super.getAdapter(adapter);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public static Section createSection(FormToolkit toolkit,
			final ScrolledForm form, int style, String title, String content) {
		Section section = toolkit.createSection(form.getBody(), style);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText(title);
		section.setDescription(content);

		return section;
	}

	@Override
	protected void pageChange(int newPageIndex) {
		if (this.isDirty() || hasChanged) {
			updateCemantikaOverviewPage();
			updateCemantikaSpecification();
			hasChanged = false;
		}
		super.pageChange(newPageIndex);
	}

	private void updateCemantikaSpecification() {
		contextSpecification.updateFocusSection();
		// contextSpecification.updateIdentifyBehaviorVariations();
	}

	private void updateCemantikaOverviewPage() {
		cemantikaOverView.updateImportSection();
	}

	@Override
	public void setFocus() {
		switch (getActivePage()) {
		case CEMANTIKA_OVERVIEW:
			cemantikaOverView.setFocus();
			break;
		case CONTEXT_SPECIFICATION:
			contextSpecification.setFocus();
			break;
		case CONTEXT_MANAGEMENT:
			contextManagement.setFocus();
			break;
		case EXTRAS:
			extras.setFocus();
			break;
		case TEXT_EDITOR:
			textEditor.setFocus();
			break;
		}

	}

	public void setStatusMessage(String message) {
		getEditorSite().getActionBars().getStatusLineManager().setMessage(
				message);
	}

	public void load() {
		this.map.clear();

		String text = textEditor.getDocumentProvider().getDocument(
				textEditor.getEditorInput()).get();
		Reader reader = new StringReader(text);

		try {
			this.map.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String get(String key) {
		return (String) this.map.get(key);
	}

	@Override
	public void save(String key, String value) {
		this.map.put(key, value);
		Writer writer = new StringWriter();

		try {
			this.map.store(writer, "Cemantika file");
			textEditor.getDocumentProvider().getDocument(
					textEditor.getEditorInput()).set(writer.toString());
			this.doSave(new NullProgressMonitor());
			this.load();
			hasChanged = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean alreadyImportUseCase() {
		return this.map.get(PluginManager.USE_CASE_MODEL) != null
				&& this.map.get(PluginManager.USE_CASE_DIAGRAM) != null;
	}

	@Override
	public IFormPage setActivePage(String pageId) {
		return super.setActivePage(pageId);
	}

	@Override
	public boolean alreadyImportConceptualModel() {
		return this.map.get(PluginManager.CONCEPTUAL_MODEL) != null
				&& this.map.get(PluginManager.CONCEPTUAL_DIAGRAM) != null;
	}
	
}
