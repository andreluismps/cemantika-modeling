package org.cemantika.modeling.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CemantikaPerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = CemantikaPerspectiveFactory.class.getName();

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		String packageExplorer = "org.eclipse.jdt.ui.PackageExplorer";
		String outline = "org.eclipse.ui.views.ContentOutline";
		String navigator = "org.eclipse.ui.views.ResourceNavigator";

		// Add shortcus
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");

		// Add "show views".
		layout.addShowViewShortcut(outline);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);

		IFolderLayout topLeft = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.3F, editorArea);
		topLeft.addView(packageExplorer);
		topLeft.addView(navigator);
		topLeft.addPlaceholder(packageExplorer);

		IFolderLayout bottomLeft = layout.createFolder("bottomLeft",
				IPageLayout.BOTTOM, 0.3F, "topLeft");

		bottomLeft.addView(outline);
		bottomLeft.addPlaceholder(outline);

		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.66F, editorArea);

		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IPageLayout.ID_TASK_LIST);
		bottom.addPlaceholder(IPageLayout.ID_PROBLEM_VIEW);
	}
}
