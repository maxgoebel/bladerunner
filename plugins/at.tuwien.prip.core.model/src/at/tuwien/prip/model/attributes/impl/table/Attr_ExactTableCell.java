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


public class Attr_ExactTableCell
implements Serializable, IAttribute {

    /**
	 *
	 */
	private static final long serialVersionUID = -2807872970084802584L;

	private final int MAX_TR = 2;

    private final int MAX_TD = 0;

    private final int MAX_TABLE = 2;

    private final Attr_Type type = Attr_Type.exactTableCell;

    private String nodePath = null;

    /**
     * Constructor.
     */
    public Attr_ExactTableCell (Element e) {
        String nodePath = DOMHelper.XPath.getExactXPath(e, true, false);
        this.nodePath = extractTableDelimiters(nodePath);
    }

    public boolean test(Element e) {
        String nodePath = extractTableDelimiters(
                DOMHelper.XPath.getExactXPath(e, true, false));
        if (this.nodePath.compareTo(nodePath)==0)
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
     * Extracts the substring containing only table delimiters.
     * @param path
     * @return
     */
    public String extractTableDelimiters(String path) {
        int tbCount = 0, trCount = 0, tdCount = 0;
        String reducedPath = new String("");
        String[] tagArr = path.split("/");
        for (int i=tagArr.length-1; i>0; i--) {
            String tagName = tagArr[i].substring(0,tagArr[i].indexOf('['));
            int tagIndex   = Integer.parseInt(
                    tagArr[i].substring(tagArr[i].indexOf('[')+1,tagArr[i].indexOf(']')));
            if (tagName.compareTo("table")==0) {
                if (tbCount<MAX_TABLE) {
                    reducedPath = "/table["+tagIndex+"]"+reducedPath;
                    tbCount++;
                }
                else {
                    reducedPath = "/table[*]"+reducedPath;
                }
            }
            else if (tagName.compareTo("tr")==0) {
                if (trCount<MAX_TR) {
                    reducedPath = "/tr["+tagIndex+"]"+reducedPath;
                    trCount++;
                }
                else {
                    reducedPath = "/tr[*]"+reducedPath;
                }
            }
            else if (tagName.compareTo("td")==0) {
                if (tdCount<MAX_TD) {
                    reducedPath = "/td["+tagIndex+"]"+reducedPath;
                    tdCount++;
                }
                else {
                    reducedPath = "/td[*]"+reducedPath;
                }
            }
        }
        if (reducedPath.compareTo("")!=0)
            return reducedPath;
        return null;
    }

    public boolean isValidAttribute () {
        if (nodePath!=null)
            return true;
        return false;
    }
    /**
     * Prints the attribute.
     */
    public void print () {
        if (nodePath != null)
            ErrorDump.debug(this, "node path: "+nodePath);
    }
    /**
     * Returns the string representation of the attribute.
     */
    public String toString () {
        return "@ExactTable_Cell"+nodePath;
    }
    /**
     *
     */
    public boolean equals(IAttribute attr) {
        if (this.type==attr.getType())
            if (this.nodePath.compareTo(((Attr_ExactTableCell)attr).toString())==0)
                return true;

        return false;
    }

    public IAttribute mergeWith(IAttribute other) {
        if (this.type==other.getType()) {
            //TODO: merge here
        }
        return null;
    }

}
