/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is mozdom4java
 *
 * The Initial Developer of the Original Code is
 * Peter Szinek, Lixto Software GmbH, http://www.lixto.com.
 * Portions created by the Initial Developer are Copyright (C) 2005-2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *  Peter Szinek (peter@rubyrailways.com)
 *  Michal Ceresna (michal.ceresna@gmail.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package org.mozilla.dom;

import org.mozilla.interfaces.nsIDOMAttr;
import org.mozilla.interfaces.nsIDOMCDATASection;
import org.mozilla.interfaces.nsIDOMComment;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentFragment;
import org.mozilla.interfaces.nsIDOMDocumentType;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEntity;
import org.mozilla.interfaces.nsIDOMEntityReference;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNotation;
import org.mozilla.interfaces.nsIDOMProcessingInstruction;
import org.mozilla.interfaces.nsIDOMText;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Node;

public class NodeFactory
{
    private NodeFactory()
    {}

    public static Node getNodeInstance( nsIDOMEventTarget eventTarget )
    {
        if (eventTarget == null ) return null;
        nsIDOMNode node;
        try {
            node = (nsIDOMNode) eventTarget.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);
        } catch (XPCOMException e) {
            node = null;
        }
        return getNodeInstance(node);
    }

    public static Node getNodeInstance( nsIDOMNode node )
    {
        if (node == null) return null;

        switch ( node.getNodeType() )
        {
            case nsIDOMNode.ELEMENT_NODE: return ElementImpl.getDOMInstance((nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
            case nsIDOMNode.ATTRIBUTE_NODE: return AttrImpl.getDOMInstance((nsIDOMAttr) node.queryInterface(nsIDOMAttr.NS_IDOMATTR_IID));
            case nsIDOMNode.TEXT_NODE: return TextImpl.getDOMInstance((nsIDOMText) node.queryInterface(nsIDOMText.NS_IDOMTEXT_IID));
            case nsIDOMNode.CDATA_SECTION_NODE: return CDATASectionImpl.getDOMInstance((nsIDOMCDATASection) node.queryInterface(nsIDOMCDATASection.NS_IDOMCDATASECTION_IID));
            case nsIDOMNode.ENTITY_REFERENCE_NODE: return EntityReferenceImpl.getDOMInstance((nsIDOMEntityReference) node.queryInterface(nsIDOMEntityReference.NS_IDOMENTITYREFERENCE_IID));
            case nsIDOMNode.ENTITY_NODE: return EntityImpl.getDOMInstance((nsIDOMEntity) node.queryInterface(nsIDOMEntity.NS_IDOMENTITY_IID));
            case nsIDOMNode.PROCESSING_INSTRUCTION_NODE: return ProcessingInstructionImpl.getDOMInstance((nsIDOMProcessingInstruction) node.queryInterface(nsIDOMProcessingInstruction.NS_IDOMPROCESSINGINSTRUCTION_IID));
            case nsIDOMNode.COMMENT_NODE: return CommentImpl.getDOMInstance((nsIDOMComment) node.queryInterface(nsIDOMComment.NS_IDOMCOMMENT_IID));
            case nsIDOMNode.DOCUMENT_NODE: return DocumentImpl.getDOMInstance((nsIDOMDocument) node.queryInterface(nsIDOMDocument.NS_IDOMDOCUMENT_IID));
            case nsIDOMNode.DOCUMENT_TYPE_NODE: return DocumentTypeImpl.getDOMInstance((nsIDOMDocumentType) node.queryInterface(nsIDOMDocumentType.NS_IDOMDOCUMENTTYPE_IID));
            case nsIDOMNode.DOCUMENT_FRAGMENT_NODE: return DocumentFragmentImpl.getDOMInstance((nsIDOMDocumentFragment) node.queryInterface(nsIDOMDocumentFragment.NS_IDOMDOCUMENTFRAGMENT_IID));
            case nsIDOMNode.NOTATION_NODE: return NotationImpl.getDOMInstance((nsIDOMNotation) node.queryInterface(nsIDOMNotation.NS_IDOMNOTATION_IID));
            default: return NodeImpl.getDOMInstance(node);
        }
    }

    public static nsIDOMNode getnsIDOMNode( Node node )
    {
        if (node instanceof NodeImpl) {
            NodeImpl ni = (NodeImpl) node;
            return ni.getInstance();
        }
        else {
            return null;
        }
    }

    private static boolean toLower = true;
    public static boolean getConvertNodeNamesToLowerCase()
    {
        return toLower;
    }
    public static void setConvertNodeNamesToLowerCase(boolean convert)
    {
        toLower = convert;
    }

    private static boolean expandFrames = false;
    public static boolean getExpandFrames()
    {
        return expandFrames;
    }
    public static void setExpandFrames(boolean expand)
    {
        expandFrames = expand;
    }

}
