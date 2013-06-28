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
package at.tuwien.prip.model.attributes.impl.table;

import java.io.Serializable;

import org.w3c.dom.Element;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;
import at.tuwien.prip.model.utils.DOMHelper;

/**
 * 
 * Attr_ExactHeaderCell.java
 *
 * ExactHeaderCell attribute is defined as the text value
 * of the closest ancestor node of TD or TH value. The idea
 * is to find a column descriptor of a HTML table. This should
 * only be tried if it is certain that an element is part of
 * a table structure. Other methods should be prefered.
 *
 * Created: Apr 26, 2009 9:24:08 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Attr_ExactTableHeaderCell
implements Serializable, IAttribute {
    /**
	 *
	 */
	private static final long serialVersionUID = -519137886598435872L;
	/**
     *
     */
    private final Attr_Type type = Attr_Type.exactHeaderCell;
    /**
     *
     */
    private String cellHeader = null;
    /**
     * Constructor.
     */
    public Attr_ExactTableHeaderCell (Element node) {
        cellHeader = extractDeepestCellHeader(node);
    }
    /**
     *
     */
    public boolean test(Element e) {
        if (cellHeader.compareTo(extractDeepestCellHeader(e))==0)
            return true;
        return false;
    }
    /**
     * 
     * Returns the header string of the deepest
     * TD table column for the given node.
     * 
     * @param node
     * @return
     */
    public String extractDeepestCellHeader (Element node) {
        while (DOMHelper.Tree.Parent.getParentElement(node)!=null) {
            if (node.getNodeName().compareTo("td")==0 ||
                    node.getNodeName().compareTo("span")==0)
                return node.getTextContent();
            node = DOMHelper.Tree.Parent.getParentElement(node);
        }
        return null;
    }
    /**
     *
     */
    public Attr_Type getType() {
        return type;
    }
    /**
     *
     */
    public boolean isValidAttribute () {
        if (cellHeader!=null)
            return true;
        return false;
    }
    /**
     * Prints the attribute.
     */
    public void print () {
        if (cellHeader!=null)
            ErrorDump.debug(this, "Header: "+cellHeader);
    }
    /**
     * Returns the string representation of this attribute.
     */
    public String toString () {
        return new String("@ExactHeader_Cell("+cellHeader+")");
    }
    /**
     * Returns true if the two attributes are equal.
     */
    public boolean equals(IAttribute attr) {
        if (this.type==attr.getType())
            if (this.cellHeader.compareTo(((Attr_ExactTableHeaderCell)attr).getCellHeader())==0)
                return true;

        return false;
    }
    /**
     * @return Returns the cellHeader.
     */
    public String getCellHeader() {
        return cellHeader;
    }
    /**
     * @param cellHeader The cellHeader to set.
     */
    public void setCellHeader(String cellHeader) {
        this.cellHeader = cellHeader;
    }
    /**
     * Returns this if the two features to be merged are equal, null otherwise.
     */
    public IAttribute mergeWith(IAttribute other) {
        if (!this.equals(other)) return null;
        return this;
    }

} //AttrExactHeader
