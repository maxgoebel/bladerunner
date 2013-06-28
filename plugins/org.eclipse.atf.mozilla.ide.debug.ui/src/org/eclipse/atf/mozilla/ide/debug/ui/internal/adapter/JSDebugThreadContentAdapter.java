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

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugThread;
import org.eclipse.atf.mozilla.ide.debug.model.IJSDebugScriptElement;
import org.eclipse.atf.mozilla.ide.debug.ui.scriptview.ScriptView;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.model.elements.ThreadContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;

public class JSDebugThreadContentAdapter extends ThreadContentProvider{

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.elements.ElementContentProvider#getChildCount(java.lang.Object, org.eclipse.debug.internal.ui.viewers.provisional.IPresentationContext)
	 */
	protected int getChildCount(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		
		String id = context.getId();
		//special handle for when the context is the ScriptView
		if( ScriptView.ID.equals(id) ){
			JSDebugThread thread = (JSDebugThread)element;
			
			IJSDebugScriptElement [] topScripts = thread.getTopScriptElements();
						
			return topScripts.length;
			
		}
		else
			return super.getChildCount(element, context, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.elements.ElementContentProvider#supportsContextId(java.lang.String)
	 */
	protected boolean supportsContextId(String id) {
		return ScriptView.ID.equals(id) || super.supportsContextId(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.elements.ElementContentProvider#getChildren(java.lang.Object, int, int, org.eclipse.debug.internal.ui.viewers.provisional.IPresentationContext)
	 */
	protected Object[] getChildren(Object parent, int index, int length, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		
		String id = context.getId();
		//special handle for when the context is the ScriptView
		if( ScriptView.ID.equals(id) ){
			JSDebugThread thread = (JSDebugThread)parent;
			
			IJSDebugScriptElement [] topScripts = thread.getTopScriptElements();
			
			Arrays.sort(topScripts, 0, topScripts.length, new Comparator(){

				public int compare(Object val1, Object val2) {
					
					try{
						String name1 = ((IJSDebugScriptElement)val1).getName();
						String name2 = ((IJSDebugScriptElement)val2).getName();
						return name1.compareTo(name2);
					}catch( Exception e ){
						MozillaDebugPlugin.log(e);
						return 0;
					}
				}
				
				
			});
			
			return getElements( topScripts, index, length );
			
		}
		else
			return super.getChildren(parent, index, length, context, monitor);
	}

	protected boolean hasChildren(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		String id = context.getId();
		//special handle for when the context is the ScriptView
		if( ScriptView.ID.equals(id) ){
			JSDebugThread thread = (JSDebugThread)element;
			
			IJSDebugScriptElement [] topScripts = thread.getTopScriptElements();
						
			return topScripts.length>0;
			
		}
		else
			return super.hasChildren(element, context, monitor);
	}
}
