/**
 *
 */
package at.tuwien.prip.model.attributes.impl.tag;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;
import at.tuwien.prip.model.utils.DOMHelper;

/**
 * 
 * Attr_TagLinkWithSubURL.java
 *
 * Attribute defines link element with a URL
 * of same host value.
 *
 * Created: Apr 26, 2009 9:34:40 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Attr_TagLinkWithSubURL implements IAttribute {

	/**
	 *
	 */
    private final Attr_Type type = Attr_Type.subURL;

    /**
     *
     */
    private List<String> regexList = new LinkedList<String>();

    /**
     * 
     * Constructor.
     * 
     * @param node
     */
    public Attr_TagLinkWithSubURL (Element node) {
        if (node.getNodeName().compareTo("a")==0) {
            String linkHref = node.getAttribute("href");
            String[] stringArr = linkHref.split("/");
            for (int i=0; i<stringArr.length; i++) {
                regexList.add(stringArr[i]);
            }
        }
    }

    /**
     * Returns true if element e qualifies 'this' attribute.
     * @param e
     * @return
     */
    public boolean test(Element e) {
        String elText = DOMHelper.Text.getElementText_WithoutDescendantElements(e).trim();
        Iterator<String> it = regexList.iterator();
        while (it.hasNext()) {
            String regex = (String)it.next();
            if(Pattern.matches(regex, elText))
                return true;
        }
        return false;
    }

    public Attr_Type getType() {
        return type;
    }

    public boolean isValidAttribute() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Prints the attribute.
     */
    public void print() {
        ErrorDump.debug(this, "SUB_URL attribute:");
        for (String s : regexList)
            ErrorDump.debug(this, s+" || ");
    }

    /**
     * Returns the string representation of the attribute.
     */
    public String toString () {
        String regexString = "";
        for (String s : regexList) {
            regexString += s+" || ";
        }
        return new String("@Sub_Url("+regexString.substring(0, regexString.length()-2)+")");
    }

    /**
     * Returns true if the two attributes are equal.
     */
    public boolean equals(IAttribute attr) {
        if (this.type.equals(attr.getType()))
        	return true;
        return false;
    }

    public IAttribute mergeWith(IAttribute other) {
        // TODO Auto-generated method stub
        return null;
    }

}
