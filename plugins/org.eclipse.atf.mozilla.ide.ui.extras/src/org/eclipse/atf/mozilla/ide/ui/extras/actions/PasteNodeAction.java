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

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.mozilla.interfaces.nsIDOMComment;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Pastes a node on the clipboard by appending it to the currently selected
 * node.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * 
 */
public class PasteNodeAction extends DOMSelectionAction {
	
	private nsIDOMDocument domDoc = null;
	private Clipboard clipboard;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction#run()
	 */
	public void run() {
		if( getSelection() != null && !getSelection().isEmpty() ) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				clipboard = new Clipboard(Display.getCurrent());
				String nodeString = (String)clipboard.getContents(TextTransfer.getInstance());
				if( nodeString != null ) {
					nsIDOMNode node = getSelection().getSelectedNode();
					Document xmlDoc = builder.parse( new InputSource(new StringReader(nodeString)));
					Node top = xmlDoc.getFirstChild();
					domDoc = node.getOwnerDocument();
					nsIDOMElement domTop = domDoc.createElement(top.getNodeName());
					NodeList subs = top.getChildNodes();
					NamedNodeMap attrs = top.getAttributes();
					for( int h = 0; h < attrs.getLength(); h++ ) {
						domTop.setAttribute(attrs.item(h).getNodeName(), attrs.item(h).getNodeValue());
					}
					domTop.setNodeValue(top.getNodeValue());
					for( int i = 0; i < subs.getLength(); i++ ) {
						parse( subs.item(i), domTop );
					}
					node.appendChild(domTop);
				}
			} catch( Exception e ) {
				
			} finally {
				if( clipboard != null ) {
					clipboard.dispose();
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	public void menuAboutToShow(IMenuManager manager) {
		super.menuAboutToShow(manager);
		if( isEnabled() ) {
			try {
				clipboard = new Clipboard(Display.getCurrent());
				String nodeString = (String)clipboard.getContents(TextTransfer.getInstance());
				
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				builder.setErrorHandler(new ErrorHandler() {
				
					public void warning(SAXParseException exception) throws SAXException {}
					public void fatalError(SAXParseException exception) throws SAXException {}
					public void error(SAXParseException exception) throws SAXException {}
				
				});
				builder.parse( new InputSource(new StringReader(nodeString)) );
			} catch( Exception e ) {
				setEnabled(false);
			} finally {
				if( clipboard != null ) {
					clipboard.dispose();
				}
			}
		}
	}

	/**
	 * Converts a XML node to a nsIDOMNode and all sub nodes.
	 * 
	 * @param node - XML node
	 * @param domNode - nsIDOMNode node
	 */
	private void parse( Node node, nsIDOMNode domNode  ) {
		if( node.getNodeName().equals("#text") ) {
			String testEmpty = node.getNodeValue().replace( '\n',' ' );
			testEmpty = testEmpty.replace('\t', ' ');
			testEmpty = testEmpty.trim();
			nsIDOMText text = domDoc.createTextNode(node.getNodeValue());
			domNode.appendChild(text);
		} else if( node.getNodeName().equals("#comment") ) {
			nsIDOMComment comment = domDoc.createComment(node.getNodeValue());
			domNode.appendChild(comment);
		} else {
			nsIDOMElement el = domDoc.createElement(node.getNodeName());
			domNode.appendChild(el);
			el.setNodeValue(node.getNodeValue());
			NodeList subNodes = node.getChildNodes();
			NamedNodeMap attrs = node.getAttributes();
			for( int h = 0; h < attrs.getLength(); h++ ) {
				el.setAttribute(attrs.item(h).getNodeName(), attrs.item(h).getNodeValue());
			}
			for( int i = 0; i < subNodes.getLength(); i++ ) {
				parse( subNodes.item(i), el );
			}
		}
	}

}
