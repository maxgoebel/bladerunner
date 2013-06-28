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
package at.tuwien.prip.model.attributes.impl.txt;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;
import at.tuwien.prip.model.utils.DOMHelper;


/**
 * text content of node is NOT whitespace-only
 */
public class Attr_TextNoWS
implements Serializable, IAttribute {


    /**
     *
     */
    private static final long serialVersionUID = 7849573106507898490L;

    private final Attr_Type type = Attr_Type.no_WS_Text;

    private final String regex = new String("^.*\\S.*");

    private Element sourceElement = null;

    private String textContent = null;
    /**
     * Constructor.
     */
    public Attr_TextNoWS (Element e) {
        String elText = e.getTextContent();
//		if (textContent!=null)
//			sourceElement = e;

        // alternatively:
        if (e instanceof Text) {
            elText = e.getNodeValue();
            if(Pattern.matches(regex, elText)) {
                sourceElement = e;
                textContent   = elText;
            }
        }
    }
    /**
     *
     */
    public boolean test(Element e) {
        String elText = DOMHelper.Text.getElementText_WithoutDescendantElements(e).trim();

        // alternatively:
        if (e instanceof Text) {
            elText = e.getNodeValue();

            if(Pattern.matches(regex, elText))
                return true;
        }
        return false;
    }
    /**
     * @return Returns the type.
     */
    public Attr_Type getType() {
        return type;
    }
    /**
     *
     */
    public boolean isValidAttribute() {
        if (sourceElement!=null)
            return true;
        return false;
    }
    /**
     * Prints the attribute.
     */
    public void print() {
        if (sourceElement!=null)
            ErrorDump.debug(this, "No_WS feature set");
    }
    /**
     * Returns the string representation of the attribute.
     */
    public String toString () {
        return new String("@NoWS_Text("+textContent+")");
    }
    /**
     * Returns true if the two attributes are equal.
     */
    public boolean equals(IAttribute attr) {
        if (this.type==attr.getType()) return true;
        return false;
    }
    public IAttribute mergeWith(IAttribute other) {
        return this;
    }

} //Attr_TextNoWS
