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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

/**
 * @author sedota
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ApplicationFileSelectionDialog	extends ElementTreeSelectionDialog {

	protected String[] fExtensions = null;
	protected String[] fRequiredNatures = null;
	
	protected static BaseWorkbenchContentProvider cp = new BaseWorkbenchContentProvider() {
	
		protected Map wcfs = new HashMap();
		
		public boolean hasChildren(Object element) {
			if( element instanceof IContainer ) {
				try {
					return ((IContainer)element).members().length > 0;
				} catch (CoreException e) {}
			}
			return false;
		}
	
		public Object[] getElements(Object element) {
			return (IProject[])element;
		}
	
		public Object[] getChildren(Object element) {
			if( element instanceof IContainer ) {
				try {
					return ((IContainer)element).members();
				} catch (CoreException e) {
				}
			};
			
			return new Object[0];
		}
	
	};
	
	/**
	 * ApplicationFileSelectionDialog constructor comment.
	 * @param parent Shell
	 * @parent extensions String[]
	 */
	/*public ApplicationFileSelectionDialog(Shell parent, String[] extensions, String[] natureIds) {
		this(parent, null, null, extensions, false);
		fRequiredNatures = natureIds;
	}*/
	
	/**
	 * FilteredFileSelectionDialog constructor comment.
	 * @param parent Shell
	 * @param title String
	 * @param message String
	 * @parent extensions String[]
	 * @param allowMultiple boolean
	 */
	/*public ApplicationFileSelectionDialog(Shell parent, String title, String message, String[] extensions, boolean allowMultiple) {
		super(parent, new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
	
		setTitle(title);	
		if(title == null) setTitle("Title"); //$NON-NLS-1$
		if(message == null) message = "Message"; //$NON-NLS-1$
		setMessage(message);
		setAllowMultiple(allowMultiple);
		setExtensions(extensions);
		addFilter( new ApplicationFileViewerFilter(fRequiredNatures, fExtensions));
		
		setValidator( new TypedElementSelectionValidator(new Class[] {IFile.class}, allowMultiple) );
		
	}*/
		
	/**
	 * FilteredFileSelectionDialog constructor comment.
	 * @param parent Shell
	 * @param title String
	 * @param message String
	 * @parent extensions String[]
	 * @param allowMultiple boolean
	 */
	public ApplicationFileSelectionDialog(Shell parent, ILabelProvider labelProvider, String title, String message, IProject[] projects, String[] requiredNatures, boolean allowMultiple) {
		super(parent, labelProvider, cp );
		
		fRequiredNatures = requiredNatures;
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		setTitle(title);	
		if(title == null) setTitle("Title"); //$NON-NLS-1$
		if(message == null) message = "Message"; //$NON-NLS-1$
		setMessage(message);
		setAllowMultiple(allowMultiple);
		setValidator( new TypedElementSelectionValidator(new Class[] {IFile.class}, allowMultiple) );
		
	}
	
	public String[] getExtensions() {
		return fExtensions;
	}
	public void setExtensions(String[] extensions) {
		fExtensions = extensions;
	}
			
}
