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
package at.tuwien.prip.model.attributes.impl.tag;

import java.io.Serializable;

import org.w3c.dom.Element;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;


/**
 * 
 * Attr_ExactImage.java
 *
 *
 *
 * Created: Apr 26, 2009 9:29:38 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Attr_TagExactImage
implements Serializable, IAttribute {

    /**
	 *
	 */
	private static final long serialVersionUID = -2556473629702661677L;

	private final Attr_Type type = Attr_Type.exactImage;

    private String imgName = null;
    
    private String altName = null;

    /**
     * 
     * Constructor.
     * 
     * @param imgName
     */
    public Attr_TagExactImage (Element e) {
        if (e.getNodeName().compareTo("IMG")==0) {
            this.imgName = e.getAttribute("src");
            this.altName = e.getAttribute("alt");
        }
    }

    public boolean test(Element e) {

        if (imgName==null) {
            ErrorDump.debug(this, "no image name specified");
            return false;
        }
        String imgSrc = e.getAttribute("src");
        if (imgSrc!=null) {
            if (this.imgName.compareTo(imgSrc)==0)
            return true;
        }
        return false;
    }

    public Attr_Type getType() {
        return type;
    }

    public boolean isValidAttribute() {
        if (imgName != null)
            return true;
        return false;
    }
    /**
     * Prints the attribute.
     */
    public void print() {
        System.out.printf("image: %s\n", this.imgName);
        ErrorDump.debug(this, "Image Attribute: "+imgName);
    }
    /**
     * Returns the string representation of this attribute.
     */
    public String toString () {
        return "@Image("+imgName+")";
    }
    /**
     * Returns true if the two attributes are equal.
     */
    public boolean equals(IAttribute attr) {
        if (this.type==attr.getType())
            if (this.imgName.compareTo(((Attr_TagExactImage)attr).getImgName())==0)
                return true;

        return false;
    }
    /**
     * @return Returns the imgName.
     */
    public String getImgName() {
        return imgName;
    }
    /**
     * 
     * @return
     */
    public String getAltName() {
		return altName;
	}
    /**
     * Returns this if the two features to be merged are equal, null otherwise.
     */
    public IAttribute mergeWith(IAttribute other) {
        if (!this.equals(other)) return null;
        return this;
    }

} //Attr_ExactImage
