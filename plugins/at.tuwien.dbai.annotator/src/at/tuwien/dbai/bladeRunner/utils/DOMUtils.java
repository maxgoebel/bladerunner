package at.tuwien.dbai.bladeRunner.utils;

import java.awt.Rectangle;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.tuwien.prip.model.utils.DOMHelper;
import at.tuwien.prip.mozcore.utils.CSSException;
import at.tuwien.prip.mozcore.utils.MozCssUtils;

public class DOMUtils {

//	/**
//	 * 
//	 * @param document
//	 * @param newRoot
//	 * @return
//	 */
//	public static Document subtree(Document document, Element newRoot)
//	{
//		Document result = null;
//		DocumentFragment fragment = document.createDocumentFragment();
//		fragment.
//		return result;
//	}
		
	/**
	 * 
	 * @param n
	 * @return
	 * @throws CSSException
	 */
	public static Rectangle getNodeDimensions(Node n) throws CSSException
	{
		Rectangle result = null;
		if (n.getNodeType()==Node.ELEMENT_NODE)
		{
			result = MozCssUtils.getBBoxRectangle((Element) n);
		}
		else
		{
			int x, y, width, height;

			int nodeWith = getCleanedNodeText(n).length();
			Element p = (Element) n.getParentNode();
			if (p==null) throw new CSSException ("Node element without a parent element");

			Node prevSib = DOMHelper.Tree.Sibling.getPreviousSiblingElement(n);
			Node nextSib = DOMHelper.Tree.Sibling.getNextSiblingElement(n);

			Rectangle parentRec = getElementDimension(p);

			//get the absolute bounds for this node
			int startX = 0;
			if (prevSib==null) {
				startX = parentRec.x;
				prevSib = p.getFirstChild();
			} else {
				Rectangle prevRec = getElementDimension((Element) prevSib);
				startX = prevRec.x + prevRec.width;
				prevSib = prevSib.getNextSibling(); //move right one to first node...
			}

			int stopX = 0;
			if (nextSib==null)
			{
				stopX = parentRec.x + parentRec.width;
				nextSib = p.getLastChild();
			}
			else
			{
				stopX = getElementDimension((Element)nextSib).x;
			}

			y = parentRec.y;
			height = parentRec.height;

			int totalGapWidth = stopX - startX;
			if (totalGapWidth<=0)
			{
				x = startX;
				width = 0;
			}
			else
			{
				int totalGapTextWidth = 0;
				int beforeGapTextWidth = 0;
				boolean isBefore = true;
				while (prevSib!=nextSib && prevSib!=null)
				{
					int length = getCleanedNodeText(prevSib).length();
					if (prevSib==n) {
						isBefore = false;
					}
					if (isBefore) {
						beforeGapTextWidth+=length;
					}
					totalGapTextWidth += length;
					prevSib = prevSib.getNextSibling();
				}

				int characterWidth = totalGapTextWidth==0? 0 : totalGapWidth / totalGapTextWidth;

				x = startX + (beforeGapTextWidth * characterWidth);
				y = parentRec.y;
				width = nodeWith * characterWidth;
				height = parentRec.height;
			}
			result = new Rectangle (x, y, width, height);
		}

		if (n.getNodeName().equals("img"))
		{
			System.out.println();
		}
		return result;
	}

	/**
	 *
	 * Get the dimension of an element.
	 *
	 * @param e
	 * @return the dimension of the element
	 * @throws InternalAnalysisException
	 */
	public static Rectangle getElementDimension (Element e)
	throws CSSException
	{
		return MozCssUtils.getBBoxRectangle(e);
	}
	
	/**
	 *
	 * Get the text content of a node without its descendants
	 * in a cleaned up way (e.g. trailing whitespaces and
	 * line spaces are removed).
	 *
	 * @param n
	 * @return
	 */
	public static String getCleanedNodeText (Node n)
	{
		String result;
		if (n.getNodeType()==Node.ELEMENT_NODE) {
			result = DOMHelper.Text.getElementText_WithoutDescendantElements(
					(Element)n);
		} else {
			result = n.getTextContent();
		}
		return result.trim().replaceAll("[\\r\\f\\n]","");
	}
}
