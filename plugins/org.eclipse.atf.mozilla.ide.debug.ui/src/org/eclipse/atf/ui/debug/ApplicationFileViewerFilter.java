/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/


package org.eclipse.atf.ui.debug;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author sedota
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ApplicationFileViewerFilter extends ViewerFilter {


	protected String[] validExtensions;
	protected String[] requiredNatures;
	
	public ApplicationFileViewerFilter(String[] requiredNatures, String[] validExtensions) {
		this.requiredNatures = requiredNatures;
		this.validExtensions = validExtensions;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return isValid(element);
	}
	
	public boolean isValid(Object element) {
		if(IFile.class.isInstance(element)) return isValidFile((IFile)element);
		if(IContainer.class.isInstance(element)) return isValidDirectory((IContainer)element);
		return false;
	}
	
	/**
	 * Returns boolean indicating whether the specified IFile is valid.
	 */
	public boolean isValidFile(IFile file) {
		String ext = file.getFileExtension();
		for(int i=0; i<validExtensions.length; i++) {
			if(validExtensions[i].equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
		
	}
		
	/**
	 * Returns boolean indicating whether the specified IContainer is valid.
	 * @param container
	 * @return
	 */
	public boolean isValidDirectory(IContainer container) {
		try {
			if(projectHasRequiredNatures(container.getProject()) && !container.getName().startsWith(".")) {
				//if(J2EEProjectUtils.isContainerInsideWebContent(container))
				return true;
			}
			return false;
		} catch(CoreException e) {
			return false;
		}
		
	}
	
	private boolean projectHasRequiredNatures(IProject project) throws CoreException {
		if(requiredNatures != null) {
			for(int i=0; i<requiredNatures.length; i++) {
				if(!project.hasNature(requiredNatures[i]))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns boolean indicating whether the specified IContainer contains any file in its tree
	 * with a valid extension.
	 * @param container
	 * @return
	 * @throws CoreException
	 */
	private boolean hasValidFile(IContainer container) throws CoreException {
		IResource[] children = container.members();
		for(int i=0; i<children.length; i++) {
			if(children[i] instanceof IContainer && hasValidFile((IContainer)children[i])) {
				return true;
			}
			else if(children[i] instanceof IFile) {
				String extension = ((IFile)children[i]).getFileExtension();
				for(int j=0; j<validExtensions.length; j++) {
					if(validExtensions[j].equals(extension))
						return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns boolean indicating whether the specified IContainer is inside
	 * the WebContent directory.
	 * 
	 * @param container
	 * @param project
	 * @return
	 */
	/*private boolean isContainerInsideWebContent(IContainer container) {
		
		IPath projRelPath = container.getProjectRelativePath();
		if(projRelPath.segmentCount() == 1)
			return true;
		else if(projRelPath.segmentCount() > 1 && projRelPath.segment(1).equals("WebContent"))
			return true;
		
		return false;
	}*/
		
}
