package org.eclipse.epsilon.workflow.tasks;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.googlecode.pongo.PongoGenerator;

public class PongoTask extends Task {
	
	protected File file;
	private Shell shell;
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File f) {
		file = f;
	}

	@Override
	public void execute() throws BuildException {
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(getFile().getAbsolutePath()); 
		IFile iFile = workspace.getRoot().getFileForLocation(location);
		
		PongoGenerator generator = new PongoGenerator(true);
		
		try {
			generator.generate(new File(iFile.getLocation().toOSString()));
			iFile.getProject().refreshLocal(IFile.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (Exception e) {
			System.err.println(e);
			MessageDialog.openError(shell, "Error", e.getMessage());
		}
		
	}
	
}
