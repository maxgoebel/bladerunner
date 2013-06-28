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
package org.eclipse.atf.mozilla.ide.debug.internal.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.atf.mozilla.ide.debug.model.IJSDebugScriptElement;
import org.eclipse.debug.core.model.IDebugTarget;

/*
 * The jsdIScript objects in Mozilla have a heirarchical relationship based on the line
 * information that they provide. What this means is that from the line information,
 * we can tell that a jsdIScript element is really a child of another.
 * 
 * By creating a tree, it should provide a better look at the structure of the JavaScript
 * in the application.
 */
public class JSDebugScriptElement extends JSDebugElement implements IJSDebugScriptElement {

	public static final String UNNAMED = "__unnamed__";
	public static final String ANONYMOUS = "anonymous";

	/*
	 * Can be the name of the File (relative), a function name or unnamed
	 */
	protected String name = UNNAMED;

	/*
	 * Parent element that encompasses this JSDebugScriptElement (the closure)
	 */
	protected IJSDebugScriptElement parent = null;

	/*
	 * JSDebugScriptElement that are enclosed by this script element.
	 */
	protected List children = new ArrayList();

	/*
	 * Start line of this script element
	 */
	protected int lineStart = 0;

	/*
	 * Total number of lines that encompasses this script element
	 */
	protected int lineTotal = 0;

	public JSDebugScriptElement(IDebugTarget target) {
		super(target);
		// TODO Auto-generated constructor stub
	}

	/*
	 * @see org.eclipse.atf.mozilla.ide.debug.model.IJSDebugScriptElement
	 */

	public String getName() {
		return name;
	}

	public String getLocation() {
		if (parent != null)
			return parent.getLocation();
		else
			return UNNAMED;
	}

	public IJSDebugScriptElement getParent() {
		return parent;
	}

	public List getChildren() {
		return children;
	}

	public int getLineStart() {
		return lineStart;
	}

	public int getLineTotal() {
		return lineTotal;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParent(IJSDebugScriptElement parent) {
		this.parent = parent;
	}

	public void setLineStart(int lineStart) {
		this.lineStart = lineStart;
	}

	public void setLineTotal(int lineTotal) {
		this.lineTotal = lineTotal;
	}

	/*
	 * This is the main interface to assemble the tree structure. Internally, each element
	 * must see where the param element falls in its portion of the tree and delegate to
	 * chidren if necessary. At the end the parent relationship must be set.
	 */
	public void insert(JSDebugScriptElement scriptElement) {
		this.insertAfter(scriptElement, -1);
	}

	/*
	 * This method allows skipping over certain children when inserting a scriptElement
	 */
	protected void insertAfter(JSDebugScriptElement scriptElement, int idx) {

		// don't put script as child of itself
		if (this == scriptElement) {
			return;
		}

		//iterate through children to check how to delegate.
		List childrenToRemove = new ArrayList();

		//MozillaDebugPlugin.debug( "insert: ["+scriptElement+"]" );
		int i = 0;
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			if (i > idx) {
				JSDebugScriptElement child = (JSDebugScriptElement) iter.next();

				//MozillaDebugPlugin.debug( "insert: child["+child+"]" );

				/*
				 * There might be situations where a child must be removed because it is an ancestor
				 * below the passed argument.
				 */

				//check if scriptElement is a descendant but not a direct child
				if (child.isScriptElementInside(scriptElement)) {
					child.insert(scriptElement);
					return; //done
				}

				//check if any of the children are really below the passed scriptElement 
				else if (scriptElement.isScriptElementInside(child)) {
					scriptElement.insert(child);
					childrenToRemove.add(child);
					//keep going because there might be more that one
				}
			} else {
				break;
			}
			i++;
		}

		//at this point, scriptElement is determined to be a direct child
		children.add(scriptElement);
		scriptElement.setParent(this);

		//NOTE: Need to fix the lineTotal value in the case that a child extend beyond the reported
		//line information by Mozilla

		fixLineInfo(scriptElement);

		//remove children that changed parents
		for (Iterator iter = childrenToRemove.iterator(); iter.hasNext();) {
			JSDebugScriptElement child = (JSDebugScriptElement) iter.next();
			children.remove(child);
		}
	}

	/*
	 * This method checks if the parameter is inside.
	 * 
	 * This will check that the lineStart of the parameter is inside of this scriptElement's
	 * lineStart and lineTotal.
	 * 
	 * The check of equality should take care of multiple scripts in the
	 * same line and because of the order of processing, the parent and child relation should be
	 * correct.
	 * 
	 * NOTE: We are only checking the lineStart because the lineTotal information provided by
	 * Mozilla is not always correct.
	 *  
	 * NOTE: how to calculate if script is all in one line ????
	 */
	protected boolean isScriptElementInside(JSDebugScriptElement scriptElement) {

		return (this.getLineStart() <= scriptElement.getLineStart()) &&
		//((this.getLineStart()+this.getLineTotal()) >= (scriptElement.getLineStart()+scriptElement.getLineTotal()));
				((this.getLineStart() + this.getLineTotal() - 1) >= scriptElement.getLineStart());
	}

	protected void fixLineInfo(JSDebugScriptElement childElement) {
		if ((childElement.getLineStart() + childElement.getLineTotal()) > (this.lineStart + this.lineTotal)) {
			this.lineTotal = childElement.getLineStart() + childElement.getLineTotal();
		}
	}

	public String getLabel() {

		return getName();
	}

	public String toString() {
		return "scriptelement location<" + getLocation() + "> name<" + getName() + "> start<" + getLineStart() + "> total<" + getLineTotal() + ">";
	}

}
