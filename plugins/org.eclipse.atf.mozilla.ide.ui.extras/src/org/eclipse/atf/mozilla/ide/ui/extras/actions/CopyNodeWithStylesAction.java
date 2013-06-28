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
package org.eclipse.atf.mozilla.ide.ui.extras.actions;

import org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMSerializer;
import org.mozilla.interfaces.nsIDOMViewCSS;

/**
 * Copies a DOM node with CSS on the style attribute
 * to a string and puts it on the clipboard.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class CopyNodeWithStylesAction extends DOMSelectionAction {

	public static String CLIPBOARD = null;
	
	private Clipboard clipboard;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction#run()
	 */
	public void run() {
		if( getSelection() != null && !getSelection().isEmpty() ) {
			try {
				nsIDOMNode node = getSelection().getSelectedNode();
				nsIDOMNode cloned = node.cloneNode(true);
				/**
				 * Problem with cloneNode is that some computed styles
				 * don't get passed to the cloned version so the original
				 * node is parsed and its computed style is passed
				 * to the cloned node by iterating over all nodes
				 * found in the selected node.
				 */
				setStyles(node, cloned);
				nsIDOMSerializer domSerializer = 
					(nsIDOMSerializer)Mozilla.getInstance().getComponentManager().
						createInstanceByContractID("@mozilla.org/xmlextras/xmlserializer;1", 
													null, nsIDOMSerializer.NS_IDOMSERIALIZER_IID);
				clipboard = new Clipboard(Display.getCurrent());
				clipboard.setContents(new Object[]{domSerializer.serializeToString(cloned)}, new Transfer[]{TextTransfer.getInstance()});
			} catch( Exception e ) {
				
			} finally {
				if( clipboard != null ) {
					clipboard.dispose();
				}
			}
			
		}
	}
	
	/**
	 * Sets the style attribute for all nodes to the computed styles returned
	 * from the browser.
	 * 
	 * @param node - root node
	 * @param cloned - cloned node to set styles for
	 */
	private void setStyles(nsIDOMNode node, nsIDOMNode cloned) {
		nsIDOMNodeList list = node.getChildNodes();
		nsIDOMNodeList clonedList = cloned.getChildNodes();
		if( node.getNodeType() == nsIDOMNode.ELEMENT_NODE ) {
			nsIDOMElement domElement = (nsIDOMElement)node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			nsIDOMDocumentView documentView = (nsIDOMDocumentView)domElement.getOwnerDocument().queryInterface( nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID );
			nsIDOMViewCSS cssView = (nsIDOMViewCSS)documentView.getDefaultView().queryInterface( nsIDOMViewCSS.NS_IDOMVIEWCSS_IID );
			nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle( domElement, "" );
			String style = domElement.getAttribute("style");
			for( int j = 0; j < computedStyle.getLength(); j++ ) {
				style += computedStyle.item(j)+":"+computedStyle.getPropertyCSSValue(computedStyle.item(j)).getCssText()+";";
			}
			nsIDOMElement domElement2 = (nsIDOMElement)cloned.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			domElement2.setAttribute("style", style);
		}
		for( long i = 0; i < list.getLength(); i++ ) {
			nsIDOMNode curr = list.item(i);
			nsIDOMNode curr2 = clonedList.item(i);
			if( curr.getNodeType() == nsIDOMNode.ELEMENT_NODE ) {
				nsIDOMElement domElement = (nsIDOMElement)curr.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
				nsIDOMDocumentView documentView = (nsIDOMDocumentView)domElement.getOwnerDocument().queryInterface( nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID );
				nsIDOMViewCSS cssView = (nsIDOMViewCSS)documentView.getDefaultView().queryInterface( nsIDOMViewCSS.NS_IDOMVIEWCSS_IID );
				nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle( domElement, "" );
				String style = domElement.getAttribute("style");
				for( int j = 0; j < computedStyle.getLength(); j++ ) {
					style += computedStyle.item(j)+":"+computedStyle.getPropertyCSSValue(computedStyle.item(j)).getCssText()+";";
				}
				nsIDOMElement domElement2 = (nsIDOMElement)curr2.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
				domElement2.setAttribute("style", style);
				setStyles(curr,curr2);
			}
		}
	}

}
