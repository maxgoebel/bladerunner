/**
 *
 */
package at.tuwien.prip.model.attributes.impl.txt;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.attributes.AttributeNotSupportedException;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;


/**
 * 
 * Attr_TextSubset.java
 *
 * Attribute is defined as a collection of text contents.
 * 
 *
 * Created: Apr 26, 2009 9:52:18 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Attr_TextSubset
implements Serializable, IAttribute {
    /**
	 *
	 */
	private static final long serialVersionUID = -410773775642577588L;
	
	/**
     *
     */
    private List<String> expressionStrings = new LinkedList<String>();

    /**
     * 
     * Constructor.
     * 
     * @parma e
     */
    public Attr_TextSubset (Element e) {
        String txt = e.getTextContent();
        if (txt==null || txt.length()<1) {
        	return;
        }
        if (isNewString(txt)) {
            expressionStrings.add(txt);
        }
    }
    
    /**
     *
     * Check if a text has already been seen before. If
     * false, ignore. Otherwise, add to memory.
     *
     * @param txt
     * @return
     */
    private boolean isNewString (String txt) {
        for (int i=0; i<expressionStrings.size(); i++) {
            if (expressionStrings.get(i).compareTo(txt)==0)
                return false;
        }
        return true;
    }
    
    /**
     *
     * Test attribute.
     *
     * @throws AttributeNotSupportedException
     * @see at.tuwien.prip.core.model.attributes2.weblearn.core.tools.learn.impl.features.IAttribute#test(org.weblearn.core.tools.learn.impl.example.Example)
     */
    public boolean test(Element e) throws AttributeNotSupportedException {
    	String otherText = e.getTextContent();
    	
    	if (this.expressionStrings.contains(otherText)) {
    		return true;
    	}
    	return false;
    }
    
    /**
     *
     * @see at.tuwien.prip.core.model.attributes2.weblearn.core.tools.learn.impl.features.IAttribute#getTypes()
     */
    public Attr_Type getType() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     *
     */
    public boolean isValidAttribute() {
        // TODO Auto-generated method stub
        return false;
    }
    
    /**
     * Prints the attribute.
     */
    public void print() {
        ErrorDump.debug(this, "Subset feature set");
        for (String s : expressionStrings)
            ErrorDump.debug(this, s+" || ");
    }
    
    /**
     * Returns the string representation of the attribute.
     */
    public String toString () {
        String regexString = "";
        for (String s : expressionStrings) {
            regexString += s+" || ";
        }
        return new String("@Sub_Text("+regexString.substring(0, regexString.length()-2)+")");
    }
    
    /**
     * @return Returns the expressionStrings.
     */
    public List<String> getExpressionStrings() {
        return expressionStrings;
    }
    
    /**
     * @param expressionStrings The expressionStrings to set.
     */
    public void setExpressionStrings(List<String> expressionStrings) {
        this.expressionStrings = expressionStrings;
    }
    
    /**
     * Returns true if the two attributes are equal.
     */
    public boolean equals(IAttribute attr) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public IAttribute mergeWith(IAttribute other) {
        // TODO Auto-generated method stub
        return null;
    }

}//Attr_TextSubset
