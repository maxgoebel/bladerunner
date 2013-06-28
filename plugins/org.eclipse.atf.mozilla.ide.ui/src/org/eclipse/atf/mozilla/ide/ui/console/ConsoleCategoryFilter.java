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

public class ConsoleCategoryFilter extends ViewerFilter {
	
	private boolean displayCSS = false;
	private boolean displayJS = false;
	private boolean displayChrome = false;
	private boolean displayXML = false;
	
	public void displayCSS(boolean shouldDisplay) {
		this.displayCSS = shouldDisplay;
	}
	
	public void displayJavascript(boolean shouldDisplay) {
		this.displayJS = shouldDisplay;
	}
	
	public void displayXML(boolean shouldDisplay) {
		this.displayXML = shouldDisplay;
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		try{
			nsIConsoleMessage consoleMessage = (nsIConsoleMessage)element;
			nsIScriptError scriptError = (nsIScriptError)consoleMessage.queryInterface( nsIScriptError.NS_ISCRIPTERROR_IID );
			String category = scriptError.getCategory();
			if( displayCSS && category.indexOf("CSS") != -1 ) {
				return true;
			}
			if( displayJS &&  category.indexOf("javascript") != -1 ) {
				return true;
			}
			if( displayChrome  &&  
					( category.indexOf("XPConnect") != -1
							|| category.indexOf("XUL") != -1
							|| category.indexOf("chrome") != -1
							|| category.indexOf("component") != -1 ) ) {
				return true;
			}
			if( displayXML && ( category.indexOf("XML") != -1 
								|| category.indexOf("malformed-xml") != -1 ) ) {
				return true;
			}
		}
		catch( Exception e ){
			return true;
		}
		return false;
	}

}
