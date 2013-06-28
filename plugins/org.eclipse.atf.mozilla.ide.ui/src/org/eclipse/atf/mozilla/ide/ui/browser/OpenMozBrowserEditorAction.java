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

package org.eclipse.atf.mozilla.ide.ui.browser;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.browser.util.MozBrowserUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


public class OpenMozBrowserEditorAction extends Action implements IWorkbenchWindowActionDelegate {
	
	protected static final String ERROR_MSG = "Error opening Mozilla Browser!";
	
	public void run(){
		
		try{
			IWorkbenchWindow activeWindow= MozIDEUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			
			if (activeWindow == null) {
				throw new CoreException( new Status( IStatus.ERROR, MozIDEUIPlugin.PLUGIN_ID, IStatus.ERROR, "Error opening Mozilla Browser... failed to retrieve active workbench window!", null ));
			}
			IWorkbenchPage activePage= activeWindow.getActivePage();
			if (activePage == null) {
				throw new CoreException( new Status( IStatus.ERROR, MozIDEUIPlugin.PLUGIN_ID, IStatus.ERROR, "Error opening Mozilla Browser... could not retrieve active page!", null ));
			}
			
			InputDialog urlDialog = new InputDialog( activePage.getWorkbenchWindow().getShell(), "Open URL...", "Enter URL: ", "http://", null );
			int rc = urlDialog.open();
		
			//return from the action if the user cancels in the dialog
			if( rc != InputDialog.OK )
				return;
				
			String urlString = urlDialog.getValue();
			
			MozBrowserUtil.openMozillaBrowser( urlString, activePage );
			
		} catch (CoreException e) {
			showError( e.getStatus() );			
		}
		
	}
	
	protected void showError( IStatus status ){
		try{
			ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, ERROR_MSG, status );
		}
		catch( Exception e ){
			//in case of an NPE getting the Active Workbench Window
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		this.run();
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
