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
package org.eclipse.atf.mozilla.ide.ui.inspector.search;

import org.eclipse.atf.mozilla.ide.ui.inspector.ATFInternalNodeFilter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

public class DOMTreePatternFilter extends PatternFilter {

	public static final int ELEMENTNAME_FILTERTYPE = 0;
	public static final int ELEMENTID_FILTERTYPE = 1;
	public static final int ELEMENTCLASS_FILTERTYPE = 2;

	//need it here also to avoid showing parents of filtered out elements
	protected ATFInternalNodeFilter atfInternalFilter = new ATFInternalNodeFilter();
	
	protected int filterType = ELEMENTNAME_FILTERTYPE;
	
	public void setFilterType( int filterType ){
		//@GINO: Should check that the int is witin the valid values
		this.filterType = filterType;
		
		setPattern( null );
	}
	
	/*
	 * Override this method so that the matched
	 */
	protected boolean isLeafMatch(Viewer viewer, Object element){
				
		if( element instanceof nsIDOMNode ){
			nsIDOMNode node = (nsIDOMNode)element;
			if( node.getNodeType() == nsIDOMNode.ELEMENT_NODE ){
				
				if( !atfInternalFilter.isATFInternal(element) ){
					String stringToMatch = getStringToMatch( (nsIDOMElement)node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
					
					if(stringToMatch == null) {
						return false;
					}				
			        return wordMatches(stringToMatch);  
				}
				else
					return false;
			}
			else
				return false;
			
		}
		else
			return true; //there are message nodes that should always go pass the filter
        
    }
	
	protected String getStringToMatch( nsIDOMElement element ){
		
		String matchString = null;
		
		switch( filterType ){
		
		case ELEMENTNAME_FILTERTYPE:
			matchString = element.getNodeName();
			break;
			
		case ELEMENTID_FILTERTYPE:
			matchString = element.getAttribute("id");
			break;
		
		case ELEMENTCLASS_FILTERTYPE:
			matchString = element.getAttribute("class");
			break;
		}
		
		return matchString;
		
	}
}
