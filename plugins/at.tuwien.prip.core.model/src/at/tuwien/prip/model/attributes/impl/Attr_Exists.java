/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.prip.model.attributes.impl;

import java.io.Serializable;

import org.w3c.dom.Element;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;


/**
 * 
 * Attr_Exists.java
 *
 * An HTML attribute is present, with arbitrary value
 *
 *
 * Created: Apr 26, 2009 9:30:15 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Attr_Exists
implements Serializable, IAttribute {

    /**
	 *
	 */
	private static final long serialVersionUID = 7844881681656258706L;

	private final Attr_Type type = Attr_Type.anyText;

    private String attribute = null;

    /**
     * 
     * Constructor.
     * 
     * @param e
     */
    public Attr_Exists (Element e) {
        this.attribute = e.getNodeName();
    }

    public boolean test(Element e) {

        if (attribute==null) {
            ErrorDump.debug(this, "no attribute specified");
            return false;
        }
        if (e.getAttributeNode(attribute)!=null)
            return true;
        return false;
    }

    /**
     * @return Returns the type.
     */
    public Attr_Type getType() {
        return type;
    }

    /**
     * @return Returns the attribute.
     */
    public String getAttribute() {
        return attribute;
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
        ErrorDump.debug(this, "attribute: "+attribute);
    }
    /**
     * Returns the string representation of the attribute.
     */
    public String toString () {
        return new String("@Exists("+attribute+")");
    }
    /**
     * Returns true if the two attributes are equal.
     */
    public boolean equals(IAttribute attr) {
        if (this.type==attr.getType())
            if (this.attribute.compareTo(((Attr_Exists)attr).getAttribute())==0)
                return true;

        return false;
    }

    public IAttribute mergeWith(IAttribute other) {
        if (this.type==other.getType()) return this;
        return null;
    }

}
