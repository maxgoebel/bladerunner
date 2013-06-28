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
package org.eclipse.atf.mozilla.ide.ui.actions;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;

/**
 * Action utilizing current DOM node selection. Is only enabled for declared
 * types in extension point or all if unspecified.
 * 
 * Subclasses must implement the run() method that will be called when the
 * action is selected.
 * 
 * By default DOMSelectionActions are enabled for all DOM node types and are
 * overriden by declaring one of the DOM_TYPES fields in the types attribute in
 * the extension point XML.
 * 
 * The menuAboutToShow method can be overriden by subclasses to provide custom
 * enabling instead of relying on the DOM node type.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * 
 */
public abstract class DOMSelectionAction extends Action implements IMenuListener {
	
	/**
	 * Types of DOM nodes, as specified in nsIDOMNode
	 */
	public static final String[] DOM_TYPES = {"ALL",
												"ELEMENT_NODE",
												"ATTRIBUTE_NODE",
												"TEXT_NODE",
												"CDATA_SECTION_NODE",
												"ENTITY_REFERENCE_NODE",
												"ENTITY_NODE",
												"PROCESSING_INSTRUCTION_NODE",
												"COMMENT_NODE",
												"DOCUMENT_NODE",
												"DOCUMENT_TYPE_NODE",
												"DOCUMENT_FRAGMENT_NODE",
												"NOTATION_NODE" };
	
	//Current selection
	protected IDOMNodeSelection selection = null;
	
	//Types supported, all by default
	protected String types = DOM_TYPES[0];
	
	protected String path = "";

	/**
	 * Sets the current selection for this action
	 * @param selection - selection to use
	 */
	public void setSelection(IDOMNodeSelection selection) {
		this.selection = selection;
	}
	
	/**
	 * Retrieves the current dom node selection
	 * @return - current node selection
	 */
	public IDOMNodeSelection getSelection() {
		return selection;
	}
	
	/**
	 * Sets the types that this action should
	 * be enabled for.
	 * @param types - types specified in the extension point
	 */
	public void setTypes(String types) {
		this.types = types;
	}

	/**
	 * Menu listener that sets whether this action is enabled for the current
	 * dom node selection.
	 */
	public void menuAboutToShow(IMenuManager manager) {
		if( selection != null && !selection.isEmpty() ) {
			String domType = DOM_TYPES[selection.getSelectedNode().getNodeType()];
			boolean enabled = types.equals(DOM_TYPES[0]) || types.indexOf(domType) != -1;
			setEnabled(enabled);
		} else {
			setEnabled(false);
		}
	}
	
	/**
	 * Run method that extending actions will implement.
	 */
	public abstract void run();

	/**
	 * Returns the path for this action
	 * @return - path location for action
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path of this actoin
	 * @param path - action path
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
