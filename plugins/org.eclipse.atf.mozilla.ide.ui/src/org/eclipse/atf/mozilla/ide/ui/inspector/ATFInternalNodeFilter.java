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
package org.eclipse.atf.mozilla.ide.ui.inspector;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/*
 * Basic filter that removes any elements that are inserted by ATF and are not
 * meant to be seen by the end user.
 */
public class ATFInternalNodeFilter extends ViewerFilter {

	/*
	 * Always exclude the Elements that are inserted by ATF
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return !(isATFInternal(parentElement) || isATFInternal(element)); //check if either the parent or the element is an internal DOM node
	}
	
	public boolean isATFInternal( Object element ){
		if( element instanceof nsIDOMNode && ((nsIDOMNode)element).getNodeType() == nsIDOMNode.ELEMENT_NODE ){
			nsIDOMElement domElement = (nsIDOMElement)(((nsIDOMNode)element).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
			
			return MozIDEUIPlugin.ATF_INTERNAL.equals(domElement.getAttribute("class"));
		}
		
		return false;
		
	}	
	
}
