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

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;
import at.tuwien.prip.model.utils.DOMHelper;


public class Attr_TextTimeDate
implements Serializable, IAttribute {

    /**
     *
     */
    private static final long serialVersionUID = -3525411140535317068L;

    private final Attr_Type type = Attr_Type.time_dateText;

    private String regex = ".*[0-9]*:[0-9]*.*";

    private Element sourceElement = null;
    /**
     * Constructor.
     */
    public Attr_TextTimeDate (Element e) {
        String textContent = e.getTextContent();
        if (textContent!=null)
            if (Pattern.matches(regex, textContent))
                sourceElement = e;

    }

    public boolean test(Element e) {
        String elText = DOMHelper.Text.getElementText_WithoutDescendantElements(e).trim();
        if(Pattern.matches(regex, elText))
            return true;
        return false;
    }

    public Attr_Type getType() {
        return type;
    }

    public boolean isValidAttribute() {
        if (sourceElement != null)
            return true;
        return false;
    }
    /**
     *
     */
    public String toString () {
        return (new String("@TimeDate_Text("+sourceElement.getTextContent()+")"));
    }
    /**
     *
     */
    public void print () {
        if (sourceElement!=null)
            ErrorDump.debug(this, "Time/Date feature set");
    }

    public boolean equals(IAttribute attr) {
        return false;
    }

    public IAttribute mergeWith(IAttribute other) {
        // TODO Auto-generated method stub
        return null;
    }

}
