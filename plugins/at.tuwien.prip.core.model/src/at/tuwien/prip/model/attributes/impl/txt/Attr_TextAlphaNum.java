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
 * 
 * Attr_TextAlphaNum.java
 *
 * Attribute defines an element where the 
 * text content is strictly alpha-numeric.
 *
 * Created: Apr 26, 2009 9:36:29 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Attr_TextAlphaNum
implements Serializable, IAttribute {

    /**
     *
     */
    private static final long serialVersionUID = -6161801755354363248L;

    private final Attr_Type type = Attr_Type.alphaNumText;

    private char num_digits = '0';

    private char num_alphas = '0';

    private  String regex = "/w";

    private String textContent = null;


    /**
     * Constructor.
     * @param num_digits
     * @param num_alphas
     */
    public Attr_TextAlphaNum (Element e) {
        String elText = e.getTextContent();
        // alternatively:
        if (e instanceof Text) {
            elText = e.getNodeValue();
            if(Pattern.matches(regex, elText)) {
//                sourceElement = e;
                textContent   = elText;
            }
        }
        int[] count = countAlphasDigits(elText);
        if (count[0]>1)
            num_alphas = '*';
        else
            num_alphas = (char)count[0];
        if (count[1]>1)
            num_digits = '*';
        else
            num_alphas = (char)count[1];

        // redefine regex accordingly
        if (this.num_digits == '*' || this.num_alphas == '*') {
            this.regex = "[\\w]*";
            if (this.num_digits != '*') {
                int numDigits = this.num_digits;
                for (int i=0; i<numDigits; i++)
                    this.regex = this.regex + "[a-zA-Z]\\w*";
            }
            else if (this.num_alphas != '*') {
                int numAlphas = this.num_alphas;
                for (int i=0; i<numAlphas; i++)
                    this.regex = this.regex + "\\d\\w*";
            }
        }
        // some special cases
        else if (this.num_digits==0 && this.num_alphas==0)
            this.regex = "\\W";
        else if (this.num_digits==0 && this.num_alphas==1)
            this.regex = "[a-zA-Z]";
        else if (this.num_digits==1 && this.num_alphas==0)
            this.regex = "\\d";

    }

    public boolean test(Element e) {
    	String elText = DOMHelper.Text.getElementText_WithoutDescendantElements(e).trim();

        // alternatively
        if (e instanceof Text) {
            elText = e.getNodeValue();
            if(Pattern.matches(regex, elText)) {
//                sourceElement = e;
                textContent   = elText;
            }
        }
        if(Pattern.matches(regex, elText))
            return true;
        return false;
    }
    /**
     * Returns the number of alpha and digit characters in the given string.
     * @param s
     * @return
     */
    public int[] countAlphasDigits(String s) {
        int[] result = new int[]{0,0};
        char[] charArr = s.trim().toCharArray();
        for (int i=0; i<charArr.length; i++) {
            int t = charArr[i];
            if (t>=48 && t<=57)
                result[1]++;
            if ((t>=65 && t<=90) || (t>=97 && t<=122))
                result[0]++;
        }
        return result;
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
        ErrorDump.debug(this, "AlphaNum feature set to "+regex+" ("+num_alphas+","+num_digits+")");
    }
    /**
     * Returns the string representation of the attribute.
     */
    public String toString () {
        return "@AlphaNum_Text("+textContent+")";
    }
    /**
     * Returns true if the two attributes are equal.
     */
    public boolean equals(IAttribute attr) {
        return false;
    }

    public IAttribute mergeWith(IAttribute other) {
        return null;
    }

}
