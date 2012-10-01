package org.cemantika.modeling.generator.java;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.cemantika.modeling.Activator;
import org.cemantika.modeling.generator.GenerationException;
import org.cemantika.modeling.generator.ICemantikaGenerator;
import org.cemantika.modeling.generator.ITranslationUnit;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.codegen.CodeGen;
import org.eclipse.emf.codegen.jet.JETEmitter;
import org.eclipse.emf.codegen.jet.JETException;

public class JetCemantikaGenerator implements ICemantikaGenerator {

	public static final String CEMANTIKA_MODEL_PACKAGE = "cemantika.model";
	public static final String CONTEXTUAL_GRAPH_PACKAGE = "cemantika.contextual.graph";
	private String templateUri;
	private ITranslationUnit model;
	private IProgressMonitor monitor;
	private String packageName;

	public JetCemantikaGenerator(ITranslationUnit model, String templateUri, String packageName,
			IProgressMonitor monitor) {
		this.packageName = packageName;
		this.model = model;
		this.templateUri = templateUri;
		this.monitor = monitor;
	}

	@Override
	public void generate() throws GenerationException {
		monitor.beginTask("Generating Contextual Entities", 1);

		String result;
		try {
			String template = getFullUri(templateUri);
			JETEmitter emitter = new JETEmitter(template, getClass()
					.getClassLoader());
			emitter.addVariable("CEMANTIKA_LIB", Activator.PLUGIN_ID);
			result = emitter.generate(monitor, new Object[] { model });
			save(monitor, result.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenerationException();
		}
		monitor.worked(1);
	}

	private String getFullUri(String template) {
		Activator activator = Activator.getDefault();
		String root;
		try {
			root = activator.getTemplateDir();
			if (template != null) {
				return root + File.separator + template;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IFile save(IProgressMonitor monitor, byte[] contents)
			throws CoreException, JETException {

		IContainer container = findOrCreateContainer(monitor,
				getTargetFolder(), getPackageName());
		if (container == null) {
			throw new JETException(
					"Cound not find or create container for package "
							+ getPackageName() + " in " + getTargetFolder());
		}
		IFile targetFile = container.getFile(new Path(getTargetFile()));
		IFile result = getWritableTargetFile(targetFile, container,
				getTargetFile());

		InputStream newContents = new ByteArrayInputStream(contents);
		if (result.exists()) {
			result.setContents(newContents, true, true, new SubProgressMonitor(
					monitor, 1));
		} else {
			result
					.create(newContents, true, new SubProgressMonitor(monitor,
							1));
		}
		return result;
	}

	private String getTargetFile() {
		return model.getTargetFile();
	}

	@SuppressWarnings("deprecation")
	private IFile getWritableTargetFile(IFile targetFile, IContainer container,
			String targetFile2) {
		if (targetFile.isReadOnly()) {
			targetFile.setReadOnly(false);
		}
		return targetFile;
	}

	private String getTargetFolder() {
		IProject project = Activator.getDefault().getActiveProject();
		return project.getName() + "/" + "src";
	}

	private String getPackageName() {
		return this.packageName;
	}

	@SuppressWarnings("deprecation")
	private IContainer findOrCreateContainer(IProgressMonitor progressMonitor,
			String targetDirectory, String packageName) throws CoreException {

		IPath outputPath = new Path(targetDirectory + "/"
				+ packageName.replace('.', '/'));
		progressMonitor.beginTask("", 4);

		IProgressMonitor sub = new SubProgressMonitor(progressMonitor, 1);
		IPath localLocation = null; // use default
		IContainer container = CodeGen.findOrCreateContainer(outputPath, true,
				localLocation, sub);
		return container;
	}

}
