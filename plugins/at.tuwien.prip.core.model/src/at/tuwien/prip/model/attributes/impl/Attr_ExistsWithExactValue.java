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
import org.w3c.dom.Node;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;


/**
 * attribute is present, with exact value
 */
public class Attr_ExistsWithExactValue
implements Serializable, IAttribute {


    /**
	 *
	 */
	private static final long serialVersionUID = -2126520424913971583L;

	private final Attr_Type type = Attr_Type.exactValue;

    private String attribute = null;

    private String value = null;


    /**
     * Constructor
     */
    public Attr_ExistsWithExactValue (Node e) {
        this.attribute = e.getNodeName();
        this.value = e.getNodeValue();
    }

    /**
     * Returns true if element e qualifies 'this' attribute.
     * @param e
     * @return
     */
    public boolean test(Element e) {

        if (attribute==null) {
            ErrorDump.debug(this, "no attribute specified");
            return false;
        }
        if (e.getAttributeNode(attribute)!=null) {
            if ((e.getAttribute(attribute).compareTo(value))==0)
                return true;
        }
        return false;
    }

    /**
     * @return Returns the attribute.
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @return Returns the type.
     */
    public Attr_Type getType() {
        return type;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
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
        ErrorDump.debug(this, "value: "+value);
    }
    /**
     * Returns the string representation of the attribute.
     */
    public String toString () {
        return "@ExactValue_Exists("+attribute+"="+value+")";
    }
    /**
     * Returns true if the two attributes are equal.
     */
    public boolean equals(IAttribute attr) {
        if (this.type==attr.getType())
            if (this.attribute.compareTo(((Attr_ExistsWithExactValue)attr).getAttribute())==0 &&
                    this.value.compareTo(((Attr_ExistsWithExactValue)attr).getValue())==0 )
                return true;

        return false;
    }

    public IAttribute mergeWith(IAttribute other) {
        if (this.type==other.getType()) return this;
        return null;
    }


}
