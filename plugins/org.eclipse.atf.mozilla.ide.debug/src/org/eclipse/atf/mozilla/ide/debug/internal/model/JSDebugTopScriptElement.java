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
package org.eclipse.atf.mozilla.ide.debug.internal.model;

import org.eclipse.atf.mozilla.ide.debug.model.IJSDebugScriptElement;
import org.eclipse.debug.core.model.IDebugTarget;

/*
 * This class represents the top level element that should map to a file
 * The lineStart and lineTotal represents the entire file.
 * 
 * lineStart = 0 and lineTotal = -1 (-1 represents the entire file)
 */
public class JSDebugTopScriptElement extends JSDebugScriptElement {
	
	/*
	 * Absolute URL for the Script file
	 */
	protected String location = "";
	protected int lastTopClosureIdx = -1;
	
	public JSDebugTopScriptElement(IDebugTarget target) {
		super(target);
		lineStart = 1;
		lineTotal = -1; //represents all the file
		parent = null;
	}
	
	/*
	 * Special logic to handle code that is added through eval
	 */
	public void insert(JSDebugScriptElement scriptElement) {
		
		//treat this one as new top containers and change the currentElementForInsert
		if( scriptElement.getName() == JSDebugScriptElement.UNNAMED ){
			lastTopClosureIdx = this.children.size();
			return; //ignore the scriptElements that do
		}
		else{
			super.insertAfter(scriptElement, lastTopClosureIdx);
		}
	}

	public String getLocation(){
		return location;
	}
	
	public void setParent( IJSDebugScriptElement parent ) {
		//do nothing
	}
	
	public void setLocation( String location){
		this.location = location;
	}
	
	public void setLineStart(int lineStart) {
		//do nothing
	}

	public void setLineTotal(int lineTotal) {
		//do nothing
	}
	
	protected void fixLineInfo( JSDebugScriptElement childElement ){
		//do nothing because this elemet always represent the entire file
	}
	
}
