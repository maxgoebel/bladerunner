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
import org.eclipse.atf.mozilla.ide.ui.inspector.DOMContentProvider.MessageNode;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;


public class DOMLabelProvider extends LabelProvider {

	public Image getImage(Object element) {
		
		//MessageNode(s) do not have images
		if( element instanceof MessageNode )
			return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.WAITING_IMG_ID);
		
		switch(((nsIDOMNode)element).getNodeType()){
		
		case nsIDOMNode.DOCUMENT_NODE:
			return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.DOCUMENT_IMG_ID);
		
		case nsIDOMNode.ELEMENT_NODE:
			return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.ELEMENT_IMG_ID);
		
		case nsIDOMNode.TEXT_NODE:
			return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.TEXT_IMG_ID);
			
		case nsIDOMNode.COMMENT_NODE:
			return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.COMMENT_IMG_ID);
		
		case nsIDOMNode.CDATA_SECTION_NODE:
			
		default:
			return null;
		}
	}

	public String getText(Object element) {
		
		//MessageNode(s) do not have images
		if( element instanceof MessageNode )
			return ((MessageNode)element).message;
		
		nsIDOMNode node = (nsIDOMNode)element;
		
		StringBuffer buf = new StringBuffer();
		
		buf.append( node.getNodeName() );
		
		//try to get the id if available
		if( node.getNodeType() == nsIDOMNode.ELEMENT_NODE ){
			try{
				nsIDOMElement e = (nsIDOMElement)node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
				String id = e.getAttribute("id");
				
				if( !(id == null || "".equals(id)) ){
					
					buf.append( " [" );
					buf.append( id );
					buf.append( "]" );
				}
			}
			catch( Exception e ){
				//do nothing
			}
		}
		
		return buf.toString();
	}
}
