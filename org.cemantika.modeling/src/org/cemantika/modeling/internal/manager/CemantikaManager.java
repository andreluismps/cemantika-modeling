package org.cemantika.modeling.internal.manager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;

import org.cemantika.modeling.Activator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class CemantikaManager extends Observable  {

//	Map<IProject, Properties> managers = new HashMap<IProject, Properties>();
//	private static CemantikaManager manager;
//
//	private CemantikaManager() {
//		
//	}
//	
//	private Properties getProjectProperties() {
//		IProject project = Activator.getDefault().getActiveProject();
//		Properties map = null;
//		if (project != null) {
//			map = this.managers.get(project);
//			if (map == null) {
//				map = new Properties();
//				this.managers.put(project, map);
//			}
//		}
//		return map;
//	}
//
//	@Override
//	public boolean alreadyImportUseCase() {
//		Properties map = getProjectProperties();
//		
//		return map.get(PluginManager.USE_CASE_MODEL) != null
//				&& map.get(PluginManager.USE_CASE_DIAGRAM) != null;
//	}
//
//	@Override
//	public String get(String key) {
//		Properties map = getProjectProperties();
//		
//		return (String) map.get(key);
//	}
//
//	@Override
//	public void save(String key, String value) {
//		IProject project = Activator.getDefault().getActiveProject();
//		IFile storage = null;
//		if (project != null) {
//			 storage = project.getFile(Activator.CEMANTIKA_CONFIG_FILE);
//		}
//		Properties map = getProjectProperties();		
//		map.put(key, value);
//		String content = storage.getContents();
//		try {
//			File file = new File(storage.getLocation().toOSString());
//			Writer writer = new FileWriter(file);
//			map.store(writer, "Cemantika file");
//			
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			this.setChanged();
//			this.notifyObservers(content);
//		}
//	}
//
//	public static CemantikaManager getDefault() {
//		if (manager == null) {
//			manager = new CemantikaManager();
//		}
//		return manager;
//	}
//
//	@Override
//	public void reload() {
//		Properties map = getProjectProperties();
//		map.clear();
//		System.out.println("clearing");
//		try {
//			IProject project = Activator.getDefault().getActiveProject();
//			IFile storage = null;
//			if (project != null) {
//				 storage = project.getFile(Activator.CEMANTIKA_CONFIG_FILE);
//			}			
//			InputStream inputStream = storage.getContents();
//			System.out.println("loading again");
//			byte[] b = new byte[1024];
//			inputStream.read(b, 0, b.length);
//			System.out.println(new String(b));
//			map.load(inputStream);
//			inputStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (CoreException c) {
//			c.printStackTrace();
//		}		
//	}

}
