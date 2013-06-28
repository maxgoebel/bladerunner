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
package at.tuwien.prip.model.attributes.impl.xpath;

import java.io.Serializable;

import org.w3c.dom.Element;

import at.tuwien.prip.model.attributes.AttributeNotSupportedException;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;
import at.tuwien.prip.model.utils.DOMHelper;



/**
 * 
 * Attr_XPath_Exact.java
 *
 *
 *
 * Created: Apr 27, 2009 12:42:30 AM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Attr_XPath_Exact 
implements Serializable, IAttribute {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private final String att_name;
    /**
     * enumeration of allowed positive values
     * (if positive-definition part is a finite
     * set, null otherwise)
     */
    private final String location;
    
    /**
     * 
     * Constructor.
     * 
     * @param attName
     * @param e
     */
    public Attr_XPath_Exact(String attName, Element e) {
    	this.att_name = attName;
    	this.location = DOMHelper.XPath.getExactXPath(e);
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

    public String getAttName() {
        return att_name;
    }
    
	public boolean test(Element e) throws AttributeNotSupportedException {
		String xpath = DOMHelper.XPath.getExactXPath(e);
        if (xpath.equals(location)) return true;

        String[] sArrV = xpath.split("\\/");
        String[] sArrW = location.split("\\/");

        for (int i=1; i<sArrV.length; i++) {
            if (i<sArrW.length) {
                if (sArrW[i].equals("*")) continue;         // check tag for '*'
                if (sArrW[i].contains("*")) continue;       // check index for '*'
                if (!sArrW[i].equals(sArrV[i])) return false;
            }
        }
        return true;
	}

}//Attr_XPath_Exact
