/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.ui.console;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mozilla.interfaces.nsIConsoleMessage;
import org.mozilla.interfaces.nsIScriptError;

public class ConsoleMessageFilter extends ViewerFilter {

	protected int showMode = IJavaScriptConsole.SHOW_ALL;
	
	public ConsoleMessageFilter() {
		super();
	}

	public void setShowMode( int showMode ){
		this.showMode = showMode;
	}
	
	
	public boolean isFilterProperty(Object element, String property) {
		return super.isFilterProperty(element, property);
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		
		//check if it is an error and category contains XPConnect
		//these errors are internal and should always be filtered out
		try{
			nsIConsoleMessage consoleMessage = (nsIConsoleMessage)element;
			nsIScriptError scriptError = (nsIScriptError)consoleMessage.queryInterface( nsIScriptError.NS_ISCRIPTERROR_IID );
			
			if( scriptError.getCategory().indexOf("XPConnect") >= 0 ){
				return false;
			}
		}
		catch( Exception e ){
			//keep going
		}
		
		switch( showMode ){
		
		case IJavaScriptConsole.SHOW_ALL:
			return true;
			
		case IJavaScriptConsole.SHOW_ERRORS:
			try{
				nsIConsoleMessage consoleMessage = (nsIConsoleMessage)element;
				nsIScriptError scriptError = (nsIScriptError)consoleMessage.queryInterface( nsIScriptError.NS_ISCRIPTERROR_IID );
				return scriptError.getFlags() == nsIScriptError.errorFlag || scriptError.getFlags() == nsIScriptError.exceptionFlag;
			}
			catch( Exception e ){
				return false; //simple message
			}
		
		
		case IJavaScriptConsole.SHOW_WARNINGS:
			try{
				nsIConsoleMessage consoleMessage = (nsIConsoleMessage)element;
				nsIScriptError scriptError = (nsIScriptError)consoleMessage.queryInterface( nsIScriptError.NS_ISCRIPTERROR_IID );
				return scriptError.getFlags() == nsIScriptError.warningFlag;
			}
			catch( Exception e ){
				return false; //simple message
			}
	
		case IJavaScriptConsole.SHOW_MESSAGES:
			try{
				nsIConsoleMessage consoleMessage = (nsIConsoleMessage)element;
				consoleMessage.queryInterface( nsIScriptError.NS_ISCRIPTERROR_IID );
				return false; //if able to QI to ScriptError then it is not a message
			}
			catch( Exception e ){
				return true; //simple message
			}
		
		default:
			return true;
		}
	}
}
