package at.tuwien.prip.mozcore.utils;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.tuwien.dbai.core.exceptions.CSSException;
import at.tuwien.dbai.core.exceptions.InternalAnalysisException;
import at.tuwien.prip.common.datastructures.HashMap2;
import at.tuwien.prip.common.datastructures.Map2;
import at.tuwien.prip.model.core.Orientation;
import at.tuwien.prip.model.document.Font;
import at.tuwien.prip.model.utils.DOMHelper;
import at.tuwien.prip.mozcore.utils.MozCssUtils;

/**
 * CSSUtils.java
 *
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jul 5, 2012
 */
public class CSSUtils
{
	/**
	 *
	 * Get the common dimension of multiple elements.
	 *
	 * @param e
	 * @return the dimension of the element
	 * @throws InternalAnalysisException
	 */
	public static Rectangle2D getElementDimension (List<Element> elements)
	{
		List<Rectangle2D> bounds = new LinkedList<Rectangle2D>();
		return Graphics2DUtils.Rectangle.union(bounds);
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
	 * Get the dimension of a node.
	 *
	 * @param e
	 * @return the dimension of the element
	 * @throws InternalAnalysisException
	 */
	public static Rectangle getNodeDimension (Node n)
	throws CSSException
	{
		Rectangle result = null;
		if (n.getNodeType()==Node.ELEMENT_NODE)
		{
			result = MozCssUtils.getBBoxRectangle((Element) n);
		}
		else
		{
			int x, y, width, height;

			int nodeWith = DomUtils.Text.getCleanedNodeText(n).length();
			Element p = (Element) n.getParentNode();
			if (p==null) throw new CSSException ("Node element without a parent element");

			Node prevSib = DOMHelper.Tree.Sibling.getPreviousSiblingElement(n);
			Node nextSib = DOMHelper.Tree.Sibling.getNextSiblingElement(n);

			Rectangle parentRec = CSSUtils.getElementDimension(p);

			//get the absolute bounds for this node
			int startX = 0;
			if (prevSib==null) {
				startX = parentRec.x;
				prevSib = p.getFirstChild();
			} else {
				Rectangle prevRec = CSSUtils.getElementDimension((Element) prevSib);
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
				stopX = CSSUtils.getElementDimension((Element)nextSib).x;
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
					int length = DomUtils.Text.getCleanedNodeText(prevSib).length();
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

//	/**
//	*
//	* Get the box coordinates of the ns element.
//	*
//	* @deprecated see org.weblearn.ui.utils.BoxModelUtils
//	* @param elem
//	* @return
//	*/
//	public static Rectangle getBoundingBoxProperties (nsIDOMElement elem) {
//	nsIDOMDocument doc = elem.getOwnerDocument();
//	nsIDOMNSDocument nsdoc = qi(doc, nsIDOMNSDocument.class);
//	nsIBoxObject box = nsdoc.getBoxObjectFor(elem);
//	int sx = box.getScreenX();
//	int sy = box.getScreenY();
//	int w  = box.getWidth();
//	int h  = box.getHeight();
//	Rectangle rectangle = new Rectangle();
//	rectangle.setBounds(sx, sy, w, h);
//	return rectangle;
//	}

	/**
	 *
	 * Correct the box model coordinates of the complete
	 * document.
	 *
	 * @param doc
	 */
	public static void correctBoxModel (Document doc) {
		correctBoxModel(doc.getDocumentElement());
	}

	/**
	 *
	 * Correct the box model coordinates of elements to
	 * take into account positionings relative to the parent node.
	 *
	 * @param doc
	 */
	public static void correctBoxModel (Node root) {

//		if (root.getNodeType()!=Node.ELEMENT_NODE) {
//			return;
//		}
//
//		Element rootE = (Element) root;
//
//		Stack<Element> stack = new Stack<Element> ();
//		stack.add(rootE);
//
//		while (!stack.isEmpty()) {
//
//			Element parent = stack.firstElement();
//			boolean add_x  = false, add_y  = false;
//
//			Stack<Element> child_queue = new Stack<Element> ();
//			List<Element> children = DOMHelper.Tree.Children.getChildElements(parent);
//			child_queue.addAll(children);
//
//			//get box dimensions of parent
//			Rectangle r = MozCssUtils.getBBoxRectangle(parent);
//
//			int offset_x=0, offset_y=0;
//
//			while (!child_queue.isEmpty()) {
//
//				Element child = child_queue.firstElement();
//
//				//get box dimensions of element
//				Rectangle r_child = MozCssUtils.getBBoxRectangle(child);
//				r_child.width -= 2;
//				r_child.height -= 2;
//
//				int index = DOMHelper.Tree.Children.getChildElementIndex(parent, child);
//				if (index==0) {
////					if (
////					parent.getTagName().equals("SPAN") ||
////					parent.getTagName().equals("TD") ||
////					parent.getTagName().equals("TH") ||
////					parent.getTagName().equals("TR") ||
////					parent.getTagName().equals("TBODY")) {
//					offset_x = r_child.x;
//					offset_y = r_child.y;
////					}
//					if (r_child.x<r.x)
//						add_x = true;
//					if (r_child.y<r.y)
//						add_y = true;
//				}
//
//				int x_new = r_child.x, y_new = r_child.y;
//				if ( add_x ) {
//					x_new = r_child.x + r.x - offset_x;
//				}
//				if ( add_y ) {
//					y_new = r_child.y + r.y - offset_y;
//				}
//				if ( add_x || add_y ) {
//					//update dimensions
//					Map<String,Integer> map = new LinkedHashMap <String,Integer>();
//					map.put("position-x",        x_new);
//					map.put("position-y",        y_new);
//					map.put("dimenstion-width",  r_child.width);
//					map.put("dimenstion-height", r_child.height);
//					child.setUserData("boxprop", map, null);
//				}
//				child_queue.remove(child);
//				stack.add(child);
//			}
//			stack.remove(parent);
//		}
	}

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
			String style = visMap.get("font-style");
			String fontWeight = visMap.get("font-weight");
			int fs = -1, weight = -1;

			if (fontSize!=null)
			{
				if (fontSize.endsWith("px")) {
					fontSize = fontSize.substring(0,fontSize.length()-2);
				}
				if (fontSize.contains(".")) {
					fontSize = fontSize.substring(0, fontSize.indexOf("."));
				}
				fs = Integer.parseInt(fontSize);
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

			result = new Font(fs, name, family, style, weight);
		}
		catch (at.tuwien.prip.mozcore.utils.CSSException e)
		{
			e.printStackTrace();
		}

		return result;
	}

	/**
	 *
	 * @param e
	 * @return
	 */
	public static String[] getFontProperties (Element e)
	{
		Map<String,String> visMap = MozCssUtils.getCSSProperties(e);

		String ffamily= (String) visMap.get("font-family");
		String fsize = (String) visMap.get("font-size");
		String fstyle = (String) visMap.get("font-style");
		String weight = (String) visMap.get("font-weight");

		return new String[]{ffamily,fsize,fstyle,weight};
	}

	/**
	 *
	 * @param e
	 * @return
	 */
	public static Map<String,String> getFontPropertiesMap (Element e)
	{
		Map<String,String> visMap = MozCssUtils.getCSSProperties(e);
		Map<String,String> result = new HashMap<String,String>();
		result.put("font-size", visMap.get("font-size"));
		result.put("font-style", visMap.get("font-style"));
		result.put("font-weight", visMap.get("font-weight"));
		result.put("font-family", visMap.get("font-family"));

		return result;
	}

	/**
	 *
	 * Obtain the borders from all elements located under the
	 * 'root' element.
	 *
	 * @param root
	 * @return
	 * @throws InternalAnalysisException
	 */
	public static List<Line2D.Double> getVisualTableBorders (Element root)
	throws CSSException
	{
		Map2<Point2D, Orientation, Line2D.Double> map =
			new HashMap2<Point2D, Orientation, Line2D.Double>(null,null,null);
		List<Line2D.Double> all_borders = new Vector<Line2D.Double>();

		/*
		 * get all table borders
		 */
		List<Element> tableElements = new LinkedList<Element>();
		tableElements.addAll(DOMHelper.Tree.Descendant.getNamedDescendantsAndSelfElements(root,"TABLE"));
		tableElements.addAll(DOMHelper.Tree.Descendant.getNamedDescendantsAndSelfElements(root,"TR"));
		tableElements.addAll(DOMHelper.Tree.Descendant.getNamedDescendantsAndSelfElements(root,"TD"));
		tableElements.addAll(DOMHelper.Tree.Descendant.getNamedDescendantsAndSelfElements(root,"TH"));

//		ListUtils.unique(tableElements);

		for (Element e : tableElements)
		{
			Rectangle2D e_box = getElementDimension(e);
			Map<String,String> visMap = MozCssUtils.getCSSProperties(e);
			String l = (String)visMap.get("border-left-style");
			String r = (String)visMap.get("border-right-style");
			String t = (String)visMap.get("border-top-style");
			String b = (String)visMap.get("border-bottom-style");
			Line2D.Double line = null;

			if (l.equals("solid"))
			{
				double startX = e_box.getMinX();
				double startY = e_box.getMinY();
				double endX   = e_box.getMinX();
				double endY   = e_box.getMaxY();
				line = new Line2D.Double(startX, startY, endX, endY);
				map.put(line.getP1(), Orientation.VERTICAL, line);
				all_borders.add(line);
			}
			if (r.equals("solid"))
			{
				double startX = e_box.getMaxX();
				double startY = e_box.getMinY();
				double endX   = e_box.getMaxX();
				double endY   = e_box.getMaxY();
				line = new Line2D.Double(startX, startY, endX, endY);
				map.put(line.getP1(), Orientation.VERTICAL, line);
				all_borders.add(line);
			}
			if (t.equals("solid"))
			{
				double startX = e_box.getMinX();
				double startY = e_box.getMinY();
				double endX   = e_box.getMaxX();
				double endY   = e_box.getMinY();
				line = new Line2D.Double(startX, startY, endX, endY);
				map.put(line.getP1(), Orientation.HORIZONTAL, line);
				all_borders.add(line);
			}
			if (b.equals("solid"))
			{
				double startX = e_box.getMinX();
				double startY = e_box.getMaxY();
				double endX   = e_box.getMaxX();
				double endY   = e_box.getMaxY();
				line = new Line2D.Double(startX, startY, endX, endY);
				map.put(line.getP1(), Orientation.HORIZONTAL, line);
				all_borders.add(line);
			}
		}

		return all_borders;
	}
}
