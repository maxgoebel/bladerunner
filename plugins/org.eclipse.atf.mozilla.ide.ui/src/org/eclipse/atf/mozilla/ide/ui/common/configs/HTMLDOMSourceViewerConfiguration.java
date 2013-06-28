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

package org.eclipse.atf.mozilla.ide.ui.common.configs;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.atf.mozilla.ide.ui.common.SelectionProviderHandler;
import org.eclipse.atf.mozilla.ide.ui.source.DOMHyperlink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
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

public class HTMLDOMSourceViewerConfiguration extends TextSourceViewerConfiguration implements IHyperlinkDetector {
	
	private HTMLDOMRuleScanner scanner = null;
	private nsIDOMNode node;
	private List nodes = new ArrayList();
	private IDocument document;
	private nsIDOMDocument domDoc;
	public static final String VALIDATE = "DOM Source validates without error.";
	public static final String ERROR = "Validation error: ";
	protected int errorOffset = -1;
	private SelectionProviderHandler provider;
	
	private static Color DEFAULT_TAG_COLOR = new Color (Display.getCurrent(), new RGB (200,0,0));
	
	public HTMLDOMSourceViewerConfiguration( SelectionProviderHandler provider ) {
		super();
		this.provider = provider;
	}
	
	public HTMLDOMSourceViewerConfiguration() {
		super();
	}

	protected HTMLDOMRuleScanner getTagScanner () {
		if (scanner == null) {
			scanner = new HTMLDOMRuleScanner();
		    scanner.setDefaultReturnToken(
		    	new Token(
		    		new TextAttribute(
		    		DEFAULT_TAG_COLOR)));
		    }
		return scanner;
	}
	
   public IPresentationReconciler getPresentationReconciler (ISourceViewer sourceView) {
	    FontData fontData = new FontData("Courier New", 9, SWT.NORMAL);
	    Font font = new Font(sourceView.getTextWidget().getDisplay(), fontData);
	   	sourceView.getTextWidget().setFont(font);
	    PresentationReconciler reconciler = new PresentationReconciler();
	  	DefaultDamagerRepairer dr = new DefaultDamagerRepairer (getTagScanner());
	  	reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
	  	reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
	  	return reconciler;
   }
   
   public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
	   return new IHyperlinkDetector[]{this};
   }	
	
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		return super.getAutoEditStrategies(sourceViewer, contentType);
	}
	
	public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
		return super.getHyperlinkPresenter(sourceViewer);
	}
	
	public void setNode( nsIDOMNode node ) {
		this.node = node;
	}
	
	public nsIDOMNode getNode() {
		return node;
	}

	public void parse( Node node, nsIDOMNode domNode ) {
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
	
	public void createTop(Node top) {
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
		node.getParentNode().replaceChild(domTop, node);
		final nsIDOMNode n = domTop;
		IDOMNodeSelection selection = new IDOMNodeSelection() {
			public boolean isEmpty() {
				return node == null;
			}
			public nsIDOMNode getSelectedNode() {
				return n;
			}
		};
		provider.fireSelection(selection);
		
		setNode(domTop);
		clear();
		getNodes(domTop,0);
	}
		
	public boolean save() {
		boolean saved = false;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document xmlDoc = builder.parse( new InputSource(new StringReader(document.get())));
			domDoc = node.getOwnerDocument();
			createTop(xmlDoc.getFirstChild());
			saved = true;
			setErrorOffset(-1);
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		return saved;
	}	
	
	public String validate() {
		String returnCode = VALIDATE;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler( new ErrorHandler() {
				
				public void warning(SAXParseException exception) throws SAXException {}
			
				public void fatalError(SAXParseException exception) throws SAXException {
					try {
						int lineOffset = document.getLineOffset(exception.getLineNumber()-1);
						setErrorOffset(lineOffset+exception.getColumnNumber()-1);
					} catch (BadLocationException e) {
						setErrorOffset(-1); 
					}					
				}
			
				public void error(SAXParseException exception) throws SAXException {
					try {
						int lineOffset = document.getLineOffset(exception.getLineNumber()-1);
						setErrorOffset(lineOffset+exception.getColumnNumber()-1);
					} catch (BadLocationException e) {
						setErrorOffset(-1); 
					}
				}
			});
			builder.parse( new InputSource(new StringReader(document.get())));
			setErrorOffset(-1);
		} catch (ParserConfigurationException e) {
			returnCode = ERROR+e.getMessage();
		} catch (SAXException e) {
			returnCode = ERROR+e.getMessage();
		} catch (IOException e) {
			returnCode = ERROR+e.getMessage();
		}
		return returnCode;
	}
	
	public int getErrorOffset() {
		return errorOffset;
	}
	
	private void setErrorOffset(int offset) {
		errorOffset = offset;
	}

	public void setDocument( IDocument document ) {
		this.document = document;
	}

	public void getNodes(nsIDOMNode n, int level) {
		if (n == null) return;
		if( n.getNodeType() == nsIDOMNode.ELEMENT_NODE ) {
			add(n);
		}
		if (n.hasChildNodes()) {
			nsIDOMNode child = n.getFirstChild();
			if (child != null) {
				getNodes(child, level+1);
				while ((child = child.getNextSibling()) != null) {
					getNodes(child, level+1);
				}
			}
		}
		return;
	}
	
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null)
			return null;
	
		IDocument document= textViewer.getDocument();
		int offset= region.getOffset();
	
		String urlString= null;
		if (document == null)
			return null;
	
		IRegion lineInfo;
		String line;
		try {
			lineInfo= document.getLineInformationOfOffset(offset);
			line= document.get(lineInfo.getOffset(), lineInfo.getLength());
		} catch (BadLocationException ex) {
			return null;
		}
	
		int urlSeparatorOffset = line.indexOf("<");
		if (urlSeparatorOffset < 0)
			return null;
		String end = line.replaceAll("\t", "");
		if( end.startsWith("</") || end.startsWith("<!") ) {
			return null;
		}
	
		int urlOffsetInLine= urlSeparatorOffset;
	
		String space = line.substring(urlSeparatorOffset);
		int urlLength = space.indexOf(" ");
		if( urlLength == -1 ) {
			urlLength = space.length();
		}
		urlString = line.substring(urlSeparatorOffset,urlSeparatorOffset+urlLength);
		IRegion urlRegion= new Region(lineInfo.getOffset() + urlOffsetInLine, urlLength);
		return new IHyperlink[] {new DOMHyperlink(urlRegion, urlString, this)};
	}
	
	public void clear() {
		nodes.clear();
	}
	
	public void add(nsIDOMNode n) {
		if( n.getNodeType() == nsIDOMNode.ELEMENT_NODE ) {
			nodes.add(n);
		}
	}
	
	public void changeSelection( IRegion region ) {
		int nodeCounter = 0;
		boolean found = false;
		for( int i = 0; i < document.getNumberOfLines() && !found; i++ ) {
			try {
				String currLine = document.get(document.getLineOffset(i),document.getLineLength(i));
				currLine = currLine.replaceAll("\t", "");
				if( currLine.startsWith("<") && !currLine.startsWith("</") && !currLine.startsWith("<!") ) {
					if( region.getOffset() >= document.getLineOffset(i) && region.getOffset() <= document.getLineOffset(i)+document.getLineLength(i) ) {
						found = true;
					} else {
						nodeCounter++;
					}
				}
			} catch (BadLocationException e) {
				return;
			}
		}
		if( found ) {
			final nsIDOMNode n = (nsIDOMNode)nodes.get(nodeCounter);
			IDOMNodeSelection selection = new IDOMNodeSelection() {
				public boolean isEmpty() {
					return node == null;
				}
		
				public nsIDOMNode getSelectedNode() {
					return n;
				}
			
			}; 
			provider.fireSelection(selection);
		}
	}

}