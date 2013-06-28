package at.tuwien.prip.mozcore.utils;

import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.dom.NodeImpl;
import org.mozilla.interfaces.nsIBoxObject;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNSDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMViewCSS;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 *
 * MozCssUtils.java
 *
 * A collection of utilities for Mozilla embedding
 *
 * created: Sep 20, 2009 8:27:31 PM
 * @author Max Goebel
 */
public class MozCssUtils implements ICssUtils
{

	/**
	 * Get font properties from a node.
	 *
	 * @param e
	 * @return
	 */
	public static Font getFont (Node n)
	{
		Font result = null;
		Map<String, String> visMap;

		try
		{
			visMap = MozCssUtils.getCSSProperties(n);

			String name = visMap.get("font-name");
			String family = visMap.get("font-family");
			String fontSize = visMap.get("font-size");
			String fontStyle = visMap.get("font-style");
			String fontWeight = visMap.get("font-weight");
			int size = -1, weight = -1;

			if (fontSize!=null)
			{
				if (fontSize.endsWith("px")) {
					fontSize = fontSize.substring(0,fontSize.length()-2);
				}
				if (fontSize.contains(".")) {
					fontSize = fontSize.substring(0, fontSize.indexOf("."));
				}
				size = Integer.parseInt(fontSize);
			}
			if (fontWeight!=null)
			{
				if (fontWeight.endsWith("px")) {
					fontWeight = fontSize.substring(0,fontSize.length()-2);
				}
				if (fontWeight.contains(".")) {
					fontWeight = fontSize.substring(0, fontSize.indexOf("."));
				}
				weight = Integer.parseInt(fontSize);
			}

			result = new Font(name, weight, size);
		}
		catch (at.tuwien.prip.mozcore.utils.CSSException e)
		{
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 *
	 * @param document
	 */
	public static void setUserData (Document document)
	{
		// this cast is checked on Apache implementation (Xerces):
	    DocumentTraversal traversal = (DocumentTraversal) document;

	    TreeWalker walker = traversal.createTreeWalker(
	      document.getDocumentElement(),
	      NodeFilter.SHOW_ELEMENT, null, true);

	    traverseLevel(walker, "");
	}

	/**
	 *
	 * @param walker
	 * @param indent
	 */
	private static final void traverseLevel(TreeWalker walker,
			String indent) {

		// describe current node:
		Node parent = walker.getCurrentNode();

		// traverse children:
		for (Node n = walker.firstChild(); n != null;
				n = walker.nextSibling())
		{
			setUserData(n);
			traverseLevel(walker, indent + '\t');
		}

		// return position to the current (level up):
		walker.setCurrentNode(parent);
	}

	/**
	 *
	 * Get a map representation of the CSS attributes
	 * of an element.
	 * See http://www.w3schools.com/CSS/CSS_reference.asp
	 * for a list of available keys.
	 *
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String> getCSSProperties (Element element)
	{
		//		SimpleTimer timer = new SimpleTimer();
		//		timer.startTask(0);

		Object obj = element.getUserData("css");
		if (obj!=null) {
			return (Map<String,String>) obj;
		}

		Map<String,String> result = new HashMap<String, String>();
		nsIDOMElement mozElement =
				(nsIDOMElement)
				((NodeImpl) element).getInstance().
				queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		nsIDOMViewCSS cssView = getCSSView(mozElement);
		nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle( mozElement, "" );
		for( int i = 0; i < computedStyle.getLength(); i++ ) {
			result.put(computedStyle.item(i),computedStyle.getPropertyCSSValue(computedStyle.item(i)).getCssText());
		}

		element.setUserData("css", result, null);

		//		timer.stopTask(0);
		//		ErrorDump.debug(MozCssUtils.class, "CSS time taken: "+timer.getTimeMillis(0)+"ms");
		return result;
	}

	/**
	 *
	 * Get a map representation of the CSS attributes
	 * of an element.
	 * See http://www.w3schools.com/CSS/CSS_reference.asp
	 * for a list of available keys.
	 *
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String> getCSSProperties (Node node) throws CSSException
	{
		//		SimpleTimer timer = new SimpleTimer();
		//		timer.startTask(0);

		Object obj = node.getUserData("css");
		if (obj!=null) {
			return (Map<String,String>) obj;
		}

		Map<String,String> result = new HashMap<String, String>();
		try
		{
			nsIDOMElement mozElement = null;
			if ((node.getNodeType()==Node.TEXT_NODE))
			{
				Element e = (Element) node;
				mozElement =
						(nsIDOMElement) ((NodeImpl)e).getInstance().
						queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			}
			else
			{
				mozElement =
						(nsIDOMElement) ((NodeImpl)node).getInstance().
						queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			}
			//			nsIDOMElement mozElement =
			//				(nsIDOMElement)
			//				((NodeImpl) node).getInstance().
			//				queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

			nsIDOMViewCSS cssView = getCSSView(mozElement);
			nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle( mozElement, "" );
			for( int i = 0; i < computedStyle.getLength(); i++ )
			{
				result.put(computedStyle.item(i),computedStyle.getPropertyCSSValue(computedStyle.item(i)).getCssText());
			}
		}
		catch (Exception e)
		{
			//throw new CSSException("CSS Exception for node");
		}

		node.setUserData("css", result, null);

		//		timer.stopTask(0);
		//		ErrorDump.debug(MozCssUtils.class, "CSS time taken: "+timer.getTimeMillis(0)+"ms");
		return result;
	}

	/**
	 *
	 * Load the CSS view from an element.
	 *
	 * @param element
	 * @return
	 */
	public static nsIDOMViewCSS getCSSView (nsIDOMElement element)
	{
		nsIDOMDocumentView documentView = (nsIDOMDocumentView)element.getOwnerDocument().queryInterface( nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID );
		return (nsIDOMViewCSS)documentView.getDefaultView().queryInterface( nsIDOMViewCSS.NS_IDOMVIEWCSS_IID );
	}

	/**
	 *
	 * @param e
	 * @return
	 */
	public static Rectangle getElementDimension (Element e)
	{
		Rectangle result = null;
		//		String tagName = e.getTagName();
		//		String value = e.getNodeValue();
		if (
				e.getTagName().equalsIgnoreCase("body") ||
				e.getTagName().equalsIgnoreCase("span") ||
				e.getTagName().equalsIgnoreCase("div"))
		{
			result = getDivSpanDimensions(e);
		}
		else {
			result = MozCssUtils.getBBoxRectangle(e);
		}
		return result;
	}

	/**
	 *
	 * @param e
	 * @return
	 */
	public static Rectangle getDivSpanDimensions (Element e)
	{
		//		String tagName = e.getTagName();
		//		String value = e.getNodeValue();

		Rectangle result = null;;
		NodeList nlist = e.getChildNodes();
		if (nlist.getLength()==0) {
			result = MozCssUtils.getBBoxRectangle(e);
			return result;
		}

		for (int i=0; i<nlist.getLength(); i++) {

			Rectangle box = null;
			Node c = nlist.item(i);
			if (c instanceof Element) {
				Element ce = (Element) c;
				box = MozCssUtils.getElementDimension(ce);

				if (result==null) {
					result = box;
				} else {
					result = result.union(box);
				}
			}
		}

		if (result==null) {
			result = MozCssUtils.getBBoxRectangle(e);
		}

		return result;
	}

	static String nodeType(int type)
	{
		switch(type)
		{
		case Node.ELEMENT_NODE:                return "Element";
		case Node.DOCUMENT_TYPE_NODE:          return "Document type";
		case Node.ENTITY_NODE:                 return "Entity";
		case Node.ENTITY_REFERENCE_NODE:       return "Entity reference";
		case Node.NOTATION_NODE:               return "Notation";
		case Node.TEXT_NODE:                   return "Text";
		case Node.COMMENT_NODE:                return "Comment";
		case Node.CDATA_SECTION_NODE:          return "CDATA Section";
		case Node.ATTRIBUTE_NODE:              return "Attribute";
		case Node.PROCESSING_INSTRUCTION_NODE: return "Attribute";
		}
		return "Unidentified";
	}

	/**
	 * return the bounding box of a node.
	 * @param node
	 * @return
	 */
	public static Rectangle getBBoxRectangle(Node node)
	{
//		Object obj = node.getUserData("box");
//		if (obj!=null) {
//			return (Rectangle) obj;
//		}

		nsIDOMNode mozNode =
				(nsIDOMNode)
				((NodeImpl) node).getInstance().
				queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

		String type = nodeType(mozNode.getNodeType());
		Rectangle r = getBBoxRectangle2(mozNode);
		node.setUserData("tag", mozNode.getNodeName(), null);
		node.setUserData("value", mozNode.getNodeValue(), null);
		node.setUserData("type", type, null);
		node.setUserData("box", r, null);
		return r;
	}

	/**
	 *
	 * @param node
	 */
	public static void setUserData (Node node)
	{
		nsIDOMNode mozNode =
				(nsIDOMNode)
				((org.mozilla.dom.NodeImpl) node).getInstance().
				queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

		String type = nodeType(mozNode.getNodeType());
		Rectangle r = getBBoxRectangle2(mozNode);
		node.setUserData("tag", mozNode.getNodeName(), null);
		node.setUserData("value", mozNode.getNodeValue(), null);
		node.setUserData("type", type, null);
		node.setUserData("box", r, null);
	}

	/**
	 *
	 * @param node
	 * @return
	 */
	public static Rectangle getBBoxRectangle(nsIDOMNode node)
	{
		nsIDOMElement element = (nsIDOMElement)node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMDocument document = node.getOwnerDocument();
		nsIDOMNSDocument nsDocument = (nsIDOMNSDocument)document.queryInterface( nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID );
		nsIBoxObject elementBox = (nsIBoxObject)nsDocument.getBoxObjectFor( element );

		int x = elementBox.getX();
		int y = elementBox.getY();
		int w = elementBox.getWidth();
		int h = elementBox.getHeight();

		return new Rectangle(x,y,w,h);
	}

	/**
	 *
	 * @param node
	 * @return
	 */
	public static Rectangle getBBoxRectangle2(nsIDOMNode node)
	{
		nsIDOMElement element = (nsIDOMElement)node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMDocument document = node.getOwnerDocument();
		nsIDOMNSDocument nsDocument = (nsIDOMNSDocument)document.queryInterface( nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID );

		nsIBoxObject elementBox = (nsIBoxObject)nsDocument.getBoxObjectFor( element );

		int x = elementBox.getX();
		int y = elementBox.getY();
		int w = elementBox.getWidth();
		int h = elementBox.getHeight();

		return new Rectangle(x,y,w,h);
	}

}//MozCssUtils

/**
 * Object obj = node.getUserData("css");
		if (obj!=null) {
			return (Map<String,String>) obj;
		}

		Map<String,String> result = new HashMap<String, String>();
		try
		{
			nsIDOMElement mozElement = null;
			nsIDOMText ni = ((TextImpl) node).getInstance();
			if ((node.getNodeType()==Node.TEXT_NODE))
			{
			    Node n = NodeFactory.getNodeInstance(ni);

			    Element e = (Element) n.getParentNode();
			    mozElement = (nsIDOMElement) ((NodeImpl) e).getInstance();
			}
			else
			{
				mozElement =
					(nsIDOMElement) ((NodeImpl)node).getInstance().
					queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			}
			nsIDOMViewCSS cssView = getCSSView(mozElement);
			nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle( mozElement, "" );
			for( int i = 0; i < computedStyle.getLength(); i++ )
			{
				result.put(
						computedStyle.item(i),
						computedStyle.getPropertyCSSValue(computedStyle.item(i)).getCssText());
			}
		}
		catch (Exception e)
		{
			throw new CSSException("CSS Exception for node");
		}

		node.setUserData("css", result, null);

		//		timer.stopTask(0);
		//		ErrorDump.debug(MozCssUtils.class, "CSS time taken: "+timer.getTimeMillis(0)+"ms");
		return result;

 **/
