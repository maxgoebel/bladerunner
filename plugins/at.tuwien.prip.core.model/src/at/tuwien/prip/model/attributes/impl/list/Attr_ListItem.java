/**
 * Attr_ListItem.java
 *
 *
 *
 * Created: Apr 27, 2009 8:55:42 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
package at.tuwien.prip.model.attributes.impl.list;

import java.io.Serializable;

import org.w3c.dom.Element;

import at.tuwien.prip.model.attributes.AttributeNotSupportedException;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;

/**
 * Attr_ListItem.java
 *
 *
 *
 * Created: Apr 27, 2009 8:55:42 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Attr_ListItem 
implements Serializable, IAttribute{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		// TODO Auto-generated method stub
		return false;
	}

}
