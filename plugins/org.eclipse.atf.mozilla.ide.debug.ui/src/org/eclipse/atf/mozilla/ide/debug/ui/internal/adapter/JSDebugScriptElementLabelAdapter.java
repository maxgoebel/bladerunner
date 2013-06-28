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
package org.eclipse.atf.mozilla.ide.debug.ui.internal.adapter;

import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugScriptElement;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugTopScriptElement;
import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.atf.mozilla.ide.debug.ui.util.FunctionNameLookupUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.model.elements.ElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreePath;

public class JSDebugScriptElementLabelAdapter extends ElementLabelProvider {

	protected ImageDescriptor topScriptElementImageDesc = MozillaDebugUIPlugin.getImageDescriptor( "icons/topScriptElement.gif");
	protected ImageDescriptor scriptElementImageDesc = MozillaDebugUIPlugin.getImageDescriptor( "icons/scriptElement.gif");

	protected ImageDescriptor[] getImageDescriptors(Object element,
			IPresentationContext context) throws CoreException {
		
		if( element instanceof JSDebugTopScriptElement ){
			return new ImageDescriptor[] { topScriptElementImageDesc };
		}
		else if ( element instanceof JSDebugScriptElement ){
			return new ImageDescriptor[] { scriptElementImageDesc };
		}
		else
			return null;
	}

	protected ImageDescriptor getImageDescriptor( TreePath elementPath, IPresentationContext presentationContext, String columnId) throws CoreException {
		Object element = elementPath.getLastSegment();
		
		if( element instanceof JSDebugTopScriptElement ){
			return topScriptElementImageDesc;
		}
		else if ( element instanceof JSDebugScriptElement ){
			return scriptElementImageDesc;
		}
		else
			return null;
	}

	protected String getLabel(TreePath elementPath, IPresentationContext presentationContext, String columnId) throws CoreException {
		Object element = elementPath.getLastSegment();
		
		//return fast for top level element because they represent files
		if( element instanceof JSDebugTopScriptElement ){
			JSDebugTopScriptElement topScriptElement = (JSDebugTopScriptElement)element;
			
			//@GINO: Should consider computing a relative URL rather than the full name
			return ((JSDebugTopScriptElement)element).getName();
		}
		
		JSDebugScriptElement scriptElement = (JSDebugScriptElement)element;
		
		String _displayFunction = scriptElement.getName();
		
		/*
		 * Try to resolve to a better name by peeking at the file
		 */
		if (JSDebugScriptElement.ANONYMOUS.equals(_displayFunction)) {
			// note: might really be a function called "anonymous"
			String guess;
			try{
				
				synchronized (this) {
					guess = FunctionNameLookupUtil.getInstance().guessFunction(scriptElement);
				}
				
				if (guess != null) {
					_displayFunction= "[" + guess + "]";
					scriptElement.setName( _displayFunction );
				}
			}
			catch( Exception e ){}
		}
		
		/*
		 * Check if the name is set to UNAMED and set it to script and line info
		 */
		else if( _displayFunction == JSDebugScriptElement.UNNAMED ){
			StringBuffer buf = new StringBuffer();
			buf.append( "<script (" );
			buf.append( scriptElement.getLineStart() );
			buf.append( ":" );
			buf.append( scriptElement.getLineStart()+scriptElement.getLineTotal() );
			buf.append( ")/>" );
			
			scriptElement.setName( buf.toString() );
		}
		
		return scriptElement.getName();
	}
}
