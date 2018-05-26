package jp.sf.amateras.stepcounter;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class EclipseFileEncodingDetector implements FileEncodingDetector {

	@Override
	public String getEncoding(File file) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile resource = workspace.getRoot().getFileForLocation(location);

		if (resource != null) {
			try {
				return resource.getCharset();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}

}
