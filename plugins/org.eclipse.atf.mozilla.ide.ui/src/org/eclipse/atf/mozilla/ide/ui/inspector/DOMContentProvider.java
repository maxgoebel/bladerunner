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

package org.eclipse.atf.mozilla.ide.ui.inspector;

import java.util.HashMap;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mozilla.interfaces.nsIDOMAbstractView;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMHTMLFrameElement;
import org.mozilla.interfaces.nsIDOMHTMLIFrameElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMWindowInternal;
import org.mozilla.xpcom.XPCOMException;


/*
 * ContentProvider for the nsIDOM elements
 */
public class DOMContentProvider implements ITreeContentProvider {

	protected IWebBrowser input = null;
	
	protected HashMap documentToFrameMap = new HashMap();
	
	//this is a special not that is inserted to show a message in the DOMInspector Tree
	protected class MessageNode{
		
		public String message = "";
		public Object parent = null;
		
		public MessageNode( String message, Object parent ){
			this.message = message;
			this.parent = parent;
			
		}
	}
	
	public Object[] getChildren(Object parentElement) {
		
		if( parentElement == input ){
			
			//check if the document is loading (only show when it is available, else display a loading message)
			if( !input.isDocumentLoading() )
				return new Object[]{ ((IWebBrowser)parentElement).getDocument() };
			else
				return new Object[]{ new MessageNode("Loading...", input ) };
		}
		
		else if( parentElement instanceof MessageNode ){
			return new Object[]{}; //MessageNode objects have no children
		}
		
		else{
			nsIDOMNode node = (nsIDOMNode)parentElement;
			
			if( node.getNodeType() == nsIDOMNode.DOCUMENT_NODE )
				return new Object[]{ ((nsIDOMDocument)node).getDocumentElement() };
			
			else{
					
				//special case of for IFRAME, FRAME
				if( "FRAME".equalsIgnoreCase(node.getNodeName()) ){
					
					nsIDOMHTMLFrameElement frame = (nsIDOMHTMLFrameElement)node.queryInterface( nsIDOMHTMLFrameElement.NS_IDOMHTMLFRAMEELEMENT_IID );
					nsIDOMDocument frameDoc = frame.getContentDocument();
					
					if( frameDoc != null ){
						//important to keep this relationship because there is no way to map the document back to the frame through API
						documentToFrameMap.put( frameDoc, frame );
						
						return new Object[]{ frameDoc };
					}
					else
						return new Object[]{};
					
				}
				
				else if( "IFRAME".equalsIgnoreCase(node.getNodeName()) ){
					nsIDOMHTMLIFrameElement iframe = (nsIDOMHTMLIFrameElement)node.queryInterface( nsIDOMHTMLIFrameElement.NS_IDOMHTMLIFRAMEELEMENT_IID );
					nsIDOMDocument iframeDoc = iframe.getContentDocument();
					
					if( iframeDoc != null ){
						//important to keep this relationship because there is no way to map the document back to the frame through API
						documentToFrameMap.put( iframeDoc, iframe );
						return new Object[]{ iframeDoc };
					}
					else
						return new Object[]{};
				}
				else{
				
				
					nsIDOMNodeList childrenList = node.getChildNodes();
					Object [] children = new Object[ (int)childrenList.getLength() ];
					for( int i=0; i<(int)childrenList.getLength(); i++ ){
									
						children[i] = childrenList.item( i );
					}
					return children;
				}
			}

		}
	}

	public Object getParent(Object element){
		
		//return null if at the root
		if( element == input ){
			return null;
		}
		
		//this is the case when a message is showing
		else if ( element instanceof MessageNode ){
			return ((MessageNode)element).parent;
			
		}
		else{
			Object parent = null;
			nsIDOMNode node = (nsIDOMNode)element;
			
			if( node.getNodeType() == nsIDOMNode.DOCUMENT_NODE ){
				
				nsIDOMDocument doc = (nsIDOMDocument)node.queryInterface(nsIDOMDocument.NS_IDOMDOCUMENT_IID);
				//need to check if the document is inside of a FRAME/IFRAME
				
				//first check to see if it's been mapped
				if( documentToFrameMap.containsKey(doc) ){
					parent = documentToFrameMap.get(doc);
				}
				else{
					//lookup for FRAME/IFRAME
					try{
						nsIDOMDocumentView documentView = (nsIDOMDocumentView)doc.queryInterface( nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID );
						
						nsIDOMAbstractView defaultDocView = documentView.getDefaultView();
						
						if( defaultDocView != null ){
							nsIDOMWindowInternal iWin = (nsIDOMWindowInternal)documentView.getDefaultView().queryInterface(nsIDOMWindowInternal.NS_IDOMWINDOWINTERNAL_IID);
							parent = iWin.getFrameElement();
							
							//cache it
							if( parent != null ){
								documentToFrameMap.put( doc, parent );
							}
						}
					}
					catch( XPCOMException e ){
						//do nothing, at this point assume it is the root document
					}
				}
				
				if( parent == null ){
					parent = input;
				}
			}
			else{
				parent = node.getParentNode();
			}
			
			return parent;
		}
	}

	public boolean hasChildren(Object element) {
		
		if( element == input ){
			return input.getDocument() != null;			
		}
		else if ( element instanceof MessageNode ){
			return false;
		}
		else{
			nsIDOMNode node = (nsIDOMNode)element;
			
			if( "FRAME".equalsIgnoreCase(node.getNodeName()) ){
					
				nsIDOMHTMLFrameElement frame = (nsIDOMHTMLFrameElement)node.queryInterface( nsIDOMHTMLFrameElement.NS_IDOMHTMLFRAMEELEMENT_IID );
				nsIDOMDocument frameDoc = frame.getContentDocument();
				
				//important to keep this relationship because there is no way to map the document back to the frame through API
				documentToFrameMap.put( frameDoc, frame );
				return frameDoc != null;
			}
			
			else if( "IFRAME".equalsIgnoreCase(node.getNodeName()) ){
				nsIDOMHTMLIFrameElement iframe = (nsIDOMHTMLIFrameElement)node.queryInterface( nsIDOMHTMLIFrameElement.NS_IDOMHTMLIFRAMEELEMENT_IID );
				nsIDOMDocument iframeDoc = iframe.getContentDocument();
				
				//important to keep this relationship because there is no way to map the document back to the frame through API
				documentToFrameMap.put( iframeDoc, iframe );
				return iframeDoc != null;
			}
			else{
				return node.hasChildNodes();
			}
		}
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.input = (IWebBrowser)newInput;
		documentToFrameMap.clear();
	}
}
