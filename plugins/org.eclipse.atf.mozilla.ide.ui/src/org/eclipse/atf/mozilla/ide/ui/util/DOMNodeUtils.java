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
package org.eclipse.atf.mozilla.ide.ui.util;

import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

public class DOMNodeUtils {
	
	static public String nodeToString(nsIDOMNode node) {
		StringBuffer buffer = new StringBuffer();
		
		while( node != null ){
			buffer.append( node.getNodeName() );
			
			if( node.getNodeType() == nsIDOMNode.ELEMENT_NODE ){
				nsIDOMElement element = (nsIDOMElement)node.queryInterface( nsIDOMElement.NS_IDOMELEMENT_IID );
				
				if( element.hasAttribute("id") ){
					buffer.append( "[" );
					buffer.append( element.getAttribute("id") );
					buffer.append( "]" );
				}
				else if( element.hasAttribute("class") ){
					buffer.append( "[" );
					buffer.append( element.getAttribute("class") );
					buffer.append( "]" );
				}
				else{
					//determing the index based on siblings with the same
					//nodeName
					nsIDOMNode sibling = element.getPreviousSibling();
					int index = 0;
					while( sibling != null ){
						if( sibling.getNodeType() == nsIDOMNode.ELEMENT_NODE ){
							if( sibling.getNodeName().equalsIgnoreCase(element.getNodeName()) )
								index++;							
						}						
						sibling = sibling.getPreviousSibling();
					}
					
					if( index > 0 ){
						buffer.append( "[" );
						buffer.append( index );
						buffer.append( "]" );
					}
				}
				
			}
			
			if( node.getNodeType() != nsIDOMNode.DOCUMENT_NODE ){
				buffer.append( "/" );
			}
			
			node = node.getParentNode();
		}
		
		return buffer.toString();
	}

}
