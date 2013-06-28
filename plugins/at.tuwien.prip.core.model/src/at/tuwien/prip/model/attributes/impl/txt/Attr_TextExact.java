/**
 * Attr_TextExact.java
 *
 *
 *
 * Created: Apr 27, 2009 12:37:30 AM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
package at.tuwien.prip.model.attributes.impl.txt;

import java.io.Serializable;

import org.w3c.dom.Element;

import at.tuwien.prip.model.attributes.AttributeNotSupportedException;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;

/**
 * Attr_TextExact.java
 *
 *
 *
 * Created: Apr 27, 2009 12:37:30 AM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Attr_TextExact 
implements Serializable, IAttribute {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final String att_name;

	public final String text_content;

	/**
	 * 
	 * Constructor.
	 * 
	 * @param att_name
	 * @param text_content
	 */
	public Attr_TextExact(String att_name, Element e) {
		this.att_name = att_name;
		this.text_content = e.getTextContent();
	}

    public String getAttName() {
        return att_name;
    }
    
	public boolean equals(IAttribute attr) {
		// TODO Auto-generated method stub
		return false;
	}

	public Attr_Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isValidAttribute() {
		// TODO Auto-generated method stub
		return false;
	}

	public IAttribute mergeWith(IAttribute other) {
		// TODO Auto-generated method stub
		return null;
	}

	public void print() {
		// TODO Auto-generated method stub

	}

	public boolean test(Element e) throws AttributeNotSupportedException {
        if (e.getTextContent().equals(text_content)) return true;
        if (text_content.equals("*")) return true;
        return false;
	}

}//Attr_TextExact
