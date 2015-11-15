package at.tuwien.dbai.bladeRunner.editors.html;

import static at.tuwien.prip.mozcore.utils.ProxyUtils.qi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.RGB;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * The highlighting gateway. Provides an easy way how to highlight an item
 * like element, attribute or text in an element.
 *
 * @author Ondrej Jaura
 */
public class Highlighter2
{

    HashMap<nsIDOMElement, String> items = new HashMap<nsIDOMElement, String>();

    /**
     * Creates an instance of the <code>Highlighter</code> class.
     */
    public Highlighter2()
    {
    }

    /**
     * Remove all highlightings owned by this highlighter
     * for the given node.
     */
    public void remove(final nsIDOMNode node)
    {
        if (items.containsKey(node) &&
            node.getNodeType()==nsIDOMNode.ELEMENT_NODE)
        {
            nsIDOMElement elem = qi(node, nsIDOMElement.class);
            String origStyleValue = items.remove(elem);
            if (origStyleValue!=null) {
                elem.setAttribute("style", origStyleValue);
            } else {
                elem.removeAttribute("style");
            }
        }
    }

    /**
     * Remove all highlights owned by this highlighter
     * for the given set of node.
     */
    public void remove(List<nsIDOMNode> nodes)
    {
        for (nsIDOMNode node : nodes) {
            remove(node);
        }
    }

    /**
     * Remove all highlightings owned by this highlighter.
     */
    public void removeAll()
    {
        List<nsIDOMElement> todel = new LinkedList<nsIDOMElement>(items.keySet());
        for (nsIDOMNode node : todel) {
            remove(node);
        }
    }

    /**
     * Highlights the specified element.
     */
    public void addElement(nsIDOMElement elem, RGB bg, RGB border)
    {
        if (!items.containsKey(elem))
        {
            boolean origHasStyleAtt = elem.hasAttribute("style");
            String origStyleValue =
                origHasStyleAtt ?
                elem.getAttribute("style") : null;
            items.put(elem, origStyleValue);
        }
        if (bg==null)
        {
            String style = String.format(
                    "border: %s 2px solid;",
                    colorToHTMLColor(border));
            elem.setAttribute("style", style);
        }
        else
        {
            String style = String.format(
                    "background-color: %s;"+
                    "border: %s 2px solid;"+
                    "opacity: 0.75;",
                    colorToHTMLColor(bg),
                    colorToHTMLColor(border));
            String s = elem.getAttribute("style");
            if (s!=null)
            {
            	elem.removeAttribute("style");
            }
            elem.setAttribute("style", style);
        }
    }

    /**
     * 
     */
    protected boolean isIgnoredNode(Node n)
    {
        /* remark: there should be no "weblearn" elements anymore,
         * but we have still marking with a "weblearn" floating div;
         * so the following conditions should be removed
         * when all highlighting sh.. tasks are solved
         */
        if (n.getNodeType()==Node.ELEMENT_NODE) {
            Element e = (Element) n;

            //do not highlight a highlighter element
            if (e.getAttribute("id").
                startsWith(FloatingDiv.WEBLEARN_ID_PREFIX))
            {
                return true;
            }

        }
        else if (n.getNodeType()==Node.ATTRIBUTE_NODE) {
            Attr a = (Attr) n;
            if (a.getNodeName().
                startsWith(FloatingDiv.WEBLEARN_ID_PREFIX))
            {
                return true;
            }
        }

        /* yes, you are right, the following condition(s) should
         * not be deleted, because it is the filtering out of
         * mozilla elements like scrollbar that, obviously,
         * is not a highlighting h4ck
         */
        //ignore mozilla elements
        if (n.getNodeName().equalsIgnoreCase("scrollbar"))
        {
            return true;
        }

        return false;
    }

    /**
     * 
     * @param c
     * @return
     */
    public static String colorToHTMLColor(RGB c)
    {
        return
            String.format("#%02x%02x%02x",
                          c.red, c.green, c.blue);
    }


}
