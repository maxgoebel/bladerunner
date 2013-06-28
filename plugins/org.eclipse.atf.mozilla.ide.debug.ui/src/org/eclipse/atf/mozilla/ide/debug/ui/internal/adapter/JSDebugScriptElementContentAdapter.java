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

import java.util.List;

import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugScriptElement;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugTopScriptElement;
import org.eclipse.atf.mozilla.ide.debug.model.IJSDebugScriptElement;
import org.eclipse.atf.mozilla.ide.debug.ui.scriptview.ScriptView;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.model.elements.ElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;

public class JSDebugScriptElementContentAdapter extends ElementContentProvider {

	protected int getChildCount(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		IJSDebugScriptElement scriptElement = (IJSDebugScriptElement)element;
		
		//This is a special case to compress the tree representation
		if( element instanceof JSDebugTopScriptElement ){
			List directChildren = scriptElement.getChildren();
			
			if( directChildren.size() == 1 ){
				//check if the direct child is top level
				JSDebugScriptElement onlyChild = (JSDebugScriptElement)directChildren.get(0);
				
				if( onlyChild.getName() == JSDebugScriptElement.UNNAMED || onlyChild.getName().startsWith("<script") ){
					
					return onlyChild.getChildren().size();
					
				}
			}
			
		}
		
		return scriptElement.getChildren().size();
	}

	protected Object[] getChildren(Object parent, int index, int length, IPresentationContext context, IViewerUpdate monitor) throws CoreException {

		IJSDebugScriptElement scriptElement = (IJSDebugScriptElement)parent;
		
		//This is a special case to compress the tree representation
		if( parent instanceof JSDebugTopScriptElement ){
			List directChildren = scriptElement.getChildren();
			
			if( directChildren.size() == 1 ){
				//check if the direct child is top level
				JSDebugScriptElement onlyChild = (JSDebugScriptElement)directChildren.get(0);
				
				if( onlyChild.getName() == JSDebugScriptElement.UNNAMED || onlyChild.getName().startsWith("<script") ){
					
					return getElements( onlyChild.getChildren().toArray(), index, length );
					
				}
			}
			
		}
		
		
		return getElements( scriptElement.getChildren().toArray(), index, length );
	}

	protected boolean supportsContextId(String id) {
		return ScriptView.ID.equals( id );
	}

}
