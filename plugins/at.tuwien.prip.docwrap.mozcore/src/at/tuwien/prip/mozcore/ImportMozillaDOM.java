package at.tuwien.prip.mozcore;

import org.mozilla.dom.html.HTMLDocumentImpl;
import org.mozilla.dom.html.HTMLElementImpl;
import org.mozilla.dom.html.HTMLHtmlElementImpl;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLAnchorElement;

public class ImportMozillaDOM 
{

	/**
	 * 
	 * @param nsDoc
	 */
	public static void importMozDOM (nsIDOMDocument nsDoc)
	{
		Document d = HTMLDocumentImpl.getDOMInstance(nsDoc);
		
		HTMLHtmlElementImpl elem = (HTMLHtmlElementImpl) NodeFactory.getNodeInstance(nsDoc.getDocumentElement());
		
		 // Get all anchors from the loaded HTML document
       nsIDOMNodeList nodeList = nsDoc.getElementsByTagName("a");
       
       analyzeAnchors(nodeList);
	}

	/**
	 * 
	 * @param nodeList
	 */
	public static void analyzeAnchors(nsIDOMNodeList nodeList) 
	{
		for (int i = 0; i < nodeList.getLength(); i++) 
		{	
			// Get Mozilla DOM node
			nsIDOMNode mozNode = nodeList.item(i);

			// We are supposing that the NodeList contains only HTMLElements
			// because we only call this method over HTML nodes
			// (NodeFactory.getNodeInstance could returns another node
			//  descendants, depends on the input Mozilla DOM node)
			HTMLElementImpl htmlElement = (HTMLElementImpl) NodeFactory.getNodeInstance(mozNode);

			// We only are interested in anchors
			if (htmlElement instanceof HTMLAnchorElement)
			{
				HTMLAnchorElement a = (HTMLAnchorElement) htmlElement;

				// Test the HTML element
				System.out.println("Tag Name: " + a.getNodeName()
						+ " -- Text: " + a.getTextContent()
						+ " -- Href: " + a.getHref());
			}
		}
	}
}
