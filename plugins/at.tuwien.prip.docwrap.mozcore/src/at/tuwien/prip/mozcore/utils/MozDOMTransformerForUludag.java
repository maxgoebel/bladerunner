package at.tuwien.prip.mozcore.utils;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.interfaces.nsIBoxObject;
import org.mozilla.interfaces.nsIDOMCSS2Properties;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentTraversal;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNSDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeFilter;
import org.mozilla.interfaces.nsIDOMNodeIterator;
import org.mozilla.interfaces.nsIDOMTreeWalker;
import org.mozilla.interfaces.nsIDOMViewCSS;
import org.w3c.dom.Document;

/**
 * @type: MozDOMTransformerForUludag
 * 
 * Change mozilla dom tree to be feed to Uludag.
 * 
 * @created: May 13, 2010 5:04:54 PM
 * @author Ruslan Fayzrakhmanov (ruslanrf@gmail.com)
 *
*/
public class MozDOMTransformerForUludag {
	
	private static final String textWrapperTagName = "x";
	private static final String wordWrapperTagName = "x";
	
	private static final String attrIdName = "wh_id";
	private static final String attrFontName= "wh_fi";
	private static final String attrColorName = "wh_color";
	private static final String attrBoundsName = "wh_bounds";
	private static final String attrBgColorName = "wh_bgcolor";
	
	public static Document process(nsIDOMDocument doc) 
	{
		final nsIDOMDocumentView documentView = (nsIDOMDocumentView)doc
			.queryInterface( nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID );
		final nsIDOMViewCSS viewCss = (nsIDOMViewCSS)documentView.getDefaultView()
			.queryInterface( nsIDOMViewCSS.NS_IDOMVIEWCSS_IID );
		final nsIDOMDocumentTraversal traversal = (nsIDOMDocumentTraversal)
		doc.queryInterface(nsIDOMDocumentTraversal.NS_IDOMDOCUMENTTRAVERSAL_IID);
		wrapText(traversal, doc, viewCss);
		
		final nsIDOMTreeWalker treeWalker = traversal.createTreeWalker(doc, nsIDOMNodeFilter.SHOW_ALL, null, true);
		final nsIDOMNSDocument nsDocument = (nsIDOMNSDocument)doc.queryInterface(nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID);
		addAttributes(treeWalker, viewCss, nsDocument, 1);
		
		return null;
	}
	
	/**
	 * Wrap visible text nodes without splitting words in original DOM Tree.
	 * 
	 * @param doc
	 * @param viewCss
	 */
	private static void wrapText(final nsIDOMDocumentTraversal traversal
			, final nsIDOMDocument doc, final nsIDOMViewCSS viewCss) 
	{
		// === Get text nodes ===
		final int arrInitialSize = 1000;
		final List<nsIDOMNode> textNodes = new ArrayList<nsIDOMNode>(arrInitialSize);
		
		final nsIDOMNodeIterator nodeIterator = traversal.createNodeIterator(doc, nsIDOMNodeFilter.SHOW_TEXT, null, true);
		// add visible text nodes into the array
		nsIDOMNode node = null;
		while ( (node = nodeIterator.nextNode()) != null) {
			if (node.getNodeValue().replaceAll("(\\s|\u0020|\u00A0|\u200B|\u3000)+", "").length() != 0) {
				final nsIDOMNode parentNode = node.getParentNode(); // actually every text node must have parent which is element.
				if (parentNode != null && parentNode.getNodeType() == nsIDOMNode.ELEMENT_NODE) {
					final nsIDOMElement parentElement = (nsIDOMElement)parentNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
					final nsIDOMCSSStyleDeclaration computedCSS = viewCss.getComputedStyle(parentElement, null);
					if (!"none".equals(computedCSS.getPropertyCSSValue("display").getCssText()))
						textNodes.add(node);
				}
			}
		}
		nodeIterator.detach(); // release resources
		
		// --- for every text node ---
		for (int i=0; i<textNodes.size(); i++) {
			nsIDOMElement textWrapper = doc.createElement(textWrapperTagName);
			nsIDOMElement wordWrapper = doc.createElement(wordWrapperTagName);
			textWrapper.appendChild(wordWrapper);
			textWrapper.setAttribute("class", "wh_markupcontainer");
			wordWrapper.setAttribute("class", "wh_markuptoken");
			textWrapper.setAttribute("style", "border:none, padding:0px; margin 0px;");
			wordWrapper.setAttribute("style", "border:none, padding:0px; margin 0px;");
			wordWrapper.appendChild(doc.createTextNode(textNodes.get(i).getNodeValue()));
			textNodes.get(i).getParentNode().replaceChild(textWrapper, textNodes.get(i));
		}

	}


	/**
	 * Add attributes to visible elements for Uludag.
	 * Set id for elements according depth-first search.
	 *
	 * @param doc
	 * @param viewCss
	 * @param traversal
	 */
	private static int addAttributes(final nsIDOMTreeWalker treeWalker, final nsIDOMViewCSS viewCss
			, final nsIDOMNSDocument nsDocument, int elementID)
	{
		nsIDOMNode curNode = treeWalker.getCurrentNode();
		
		if (curNode.getNodeType() == nsIDOMNode.ELEMENT_NODE) {
			nsIDOMElement curElement =  (nsIDOMElement)curNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			curElement.setAttribute(attrIdName, "i"+String.valueOf(elementID));
			
			final nsIDOMCSS2Properties css2Prop = (nsIDOMCSS2Properties)viewCss.getComputedStyle(curElement, null)
				.queryInterface(nsIDOMCSS2Properties.NS_IDOMCSS2PROPERTIES_IID);
			
			// --- if element is visible ---
			if (!"none".equals(css2Prop.getDisplay())) {
				// --- add bounds ---
				final nsIBoxObject elementBox = (nsIBoxObject)nsDocument.getBoxObjectFor( curElement );
				curElement.setAttribute(attrBoundsName, elementBox.getX() + " " + elementBox.getY()
					+ " "+ elementBox.getWidth() + " " + elementBox.getHeight());
				
				String classAttrValue = curElement.getAttribute("class");
				if (curElement.getNodeName().equalsIgnoreCase(wordWrapperTagName)
						&& classAttrValue != null && classAttrValue.equalsIgnoreCase("wh_markuptoken")
						) {
					// --- add font info ---
					curElement.setAttribute(attrFontName, css2Prop.getFontFamily().replace("\"", "'")+";"
							+css2Prop.getFontSize()+";"+css2Prop.getFontStyle()+";"
							+ css2Prop.getFontWeight());
					// --- add color ---
					curElement.setAttribute(attrColorName, css2Prop.getColor());
				}
				else if (curElement.getNodeName().equalsIgnoreCase("img")){;}
				else {
					// --- add bg color ---
					curElement.setAttribute(attrBgColorName, css2Prop.getBackgroundColor());
				}
			}
		}
		
		for (nsIDOMNode childNode = treeWalker.firstChild(); childNode!=null; childNode = treeWalker.nextSibling()) {
			elementID = addAttributes(treeWalker, viewCss, nsDocument, elementID+1);
		}
		treeWalker.setCurrentNode(curNode);
		
		return elementID;
	}
}
