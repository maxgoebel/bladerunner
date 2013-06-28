

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

//Java imports
import java.util.concurrent.Callable;

import org.mozilla.dom.events.EventImpl;
import org.mozilla.dom.ranges.RangeImpl;
import org.mozilla.dom.traversal.NodeFilterImpl;
import org.mozilla.dom.traversal.NodeIteratorImpl;
import org.mozilla.dom.traversal.TreeWalkerImpl;
import org.mozilla.dom.views.AbstractViewImpl;
import org.mozilla.interfaces.nsIDOM3Document;
import org.mozilla.interfaces.nsIDOMAbstractView;
import org.mozilla.interfaces.nsIDOMAttr;
import org.mozilla.interfaces.nsIDOMCDATASection;
import org.mozilla.interfaces.nsIDOMComment;
import org.mozilla.interfaces.nsIDOMDOMConfiguration;
import org.mozilla.interfaces.nsIDOMDOMImplementation;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentEvent;
import org.mozilla.interfaces.nsIDOMDocumentFragment;
import org.mozilla.interfaces.nsIDOMDocumentRange;
import org.mozilla.interfaces.nsIDOMDocumentTraversal;
import org.mozilla.interfaces.nsIDOMDocumentType;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEntityReference;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeFilter;
import org.mozilla.interfaces.nsIDOMNodeIterator;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMProcessingInstruction;
import org.mozilla.interfaces.nsIDOMRange;
import org.mozilla.interfaces.nsIDOMText;
import org.mozilla.interfaces.nsIDOMTreeWalker;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.views.AbstractView;


public class DocumentImpl extends NodeImpl implements org.w3c.dom.Document, org.w3c.dom.views.DocumentView, org.w3c.dom.ranges.DocumentRange, org.w3c.dom.events.DocumentEvent, org.w3c.dom.traversal.DocumentTraversal
{
	private String url;

    public nsIDOMDocument getInstance()
    {
	return getInstanceAsnsIDOMDocument();
    }

    /***************************************************************
     *
     * Document implementation code
     *
     ***************************************************************/

    public DocumentImpl(nsIDOMDocument mozInst)
    {
        super( mozInst );
    }

    public static DocumentImpl getDOMInstance(nsIDOMDocument mozInst)
    {

        DocumentImpl node = (DocumentImpl) instances.get(mozInst);
        return node == null ? new DocumentImpl(mozInst) : node;
    }

    public nsIDOMDocument getInstanceAsnsIDOMDocument()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMDocument) moz.queryInterface(nsIDOMDocument.NS_IDOMDOCUMENT_IID);
    }

    public Attr createAttribute(final String name)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Attr> c = new Callable<Attr>() { public Attr call() {
            nsIDOMAttr result = getInstanceAsnsIDOMDocument().createAttribute(name);
            return (Attr) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node adoptNode(final Node source)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozSource = source!=null ? ((NodeImpl) source).getInstance() : null;
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = DOM3MozObject.adoptNode(mozSource);
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Element createElement(final String tagName)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Element> c = new Callable<Element>() { public Element call() {
            nsIDOMElement result = getInstanceAsnsIDOMDocument().createElement(tagName);
            return (Element) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public ProcessingInstruction createProcessingInstruction(final String target, final String data)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<ProcessingInstruction> c = new Callable<ProcessingInstruction>() { public ProcessingInstruction call() {
            nsIDOMProcessingInstruction result = getInstanceAsnsIDOMDocument().createProcessingInstruction(target, data);
            return (ProcessingInstruction) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public DOMConfiguration getDomConfig()
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        Callable<DOMConfiguration> c = new Callable<DOMConfiguration>() { public DOMConfiguration call() {
            nsIDOMDOMConfiguration result = DOM3MozObject.getDomConfig();
            return new DOMConfigurationImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Comment createComment(final String data)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Comment> c = new Callable<Comment>() { public Comment call() {
            nsIDOMComment result = getInstanceAsnsIDOMDocument().createComment(data);
            return (Comment) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public DocumentType getDoctype()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<DocumentType> c = new Callable<DocumentType>() { public DocumentType call() {
            nsIDOMDocumentType result = getInstanceAsnsIDOMDocument().getDoctype();
            return (DocumentType) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void normalizeDocument()
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        final Runnable r = new Runnable() { public void run() {
            DOM3MozObject.normalizeDocument();
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getInputEncoding()
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        Callable<String> c = new Callable<String>() { public String call() {
            String result = DOM3MozObject.getInputEncoding();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getXmlStandalone()
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = DOM3MozObject.getXmlStandalone();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setXmlVersion(final String xmlVersion)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        final Runnable r = new Runnable() { public void run() {
            DOM3MozObject.setXmlVersion(xmlVersion);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public Node importNode(final Node importedNode, final boolean deep)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozImportednode = importedNode!=null ? ((NodeImpl) importedNode).getInstance() : null;
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMDocument().importNode(mozImportednode, deep);
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setDocumentURI(final String documentURI)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        final Runnable r = new Runnable() { public void run() {
            DOM3MozObject.setDocumentURI(documentURI);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public CDATASection createCDATASection(final String data)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<CDATASection> c = new Callable<CDATASection>() { public CDATASection call() {
            nsIDOMCDATASection result = getInstanceAsnsIDOMDocument().createCDATASection(data);
            return (CDATASection) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getXmlVersion()
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        Callable<String> c = new Callable<String>() { public String call() {
            String result = DOM3MozObject.getXmlVersion();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public NodeList getElementsByTagName(final String tagname)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<NodeList> c = new Callable<NodeList>() { public NodeList call() {
            nsIDOMNodeList result = getInstanceAsnsIDOMDocument().getElementsByTagName(tagname);
            return new NodeListImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Element createElementNS(final String namespaceURI, final String qualifiedName)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Element> c = new Callable<Element>() { public Element call() {
            nsIDOMElement result = getInstanceAsnsIDOMDocument().createElementNS(namespaceURI, qualifiedName);
            return (Element) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<NodeList> c = new Callable<NodeList>() { public NodeList call() {
            nsIDOMNodeList result = getInstanceAsnsIDOMDocument().getElementsByTagNameNS(namespaceURI, localName);
            return new NodeListImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node renameNode(final Node n, final String namespaceURI, final String qualifiedName)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozN = n!=null ? ((NodeImpl) n).getInstance() : null;
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = DOM3MozObject.renameNode(mozN, namespaceURI, qualifiedName);
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public EntityReference createEntityReference(final String name)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<EntityReference> c = new Callable<EntityReference>() { public EntityReference call() {
            nsIDOMEntityReference result = getInstanceAsnsIDOMDocument().createEntityReference(name);
            return (EntityReference) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setStrictErrorChecking(final boolean strictErrorChecking)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        final Runnable r = new Runnable() { public void run() {
            DOM3MozObject.setStrictErrorChecking(strictErrorChecking);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public DocumentFragment createDocumentFragment()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<DocumentFragment> c = new Callable<DocumentFragment>() { public DocumentFragment call() {
            nsIDOMDocumentFragment result = getInstanceAsnsIDOMDocument().createDocumentFragment();
            return (DocumentFragment) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public DOMImplementation getImplementation()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<DOMImplementation> c = new Callable<DOMImplementation>() { public DOMImplementation call() {
            nsIDOMDOMImplementation result = getInstanceAsnsIDOMDocument().getImplementation();
            return new DOMImplementationImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getStrictErrorChecking()
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = DOM3MozObject.getStrictErrorChecking();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setXmlStandalone(final boolean xmlStandalone)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        final Runnable r = new Runnable() { public void run() {
            DOM3MozObject.setXmlStandalone(xmlStandalone);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getXmlEncoding()
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        Callable<String> c = new Callable<String>() { public String call() {
            String result = DOM3MozObject.getXmlEncoding();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Element getDocumentElement()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Element> c = new Callable<Element>() { public Element call() {
            nsIDOMElement result = getInstanceAsnsIDOMDocument().getDocumentElement();
            return (Element) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Element getElementById(final String elementId)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Element> c = new Callable<Element>() { public Element call() {
            nsIDOMElement result = getInstanceAsnsIDOMDocument().getElementById(elementId);
            return (Element) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getDocumentURI()
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOM3Document DOM3MozObject = (nsIDOM3Document) getInstance().queryInterface(nsIDOM3Document.NS_IDOM3DOCUMENT_IID);
        Callable<String> c = new Callable<String>() { public String call() {
            String result = DOM3MozObject.getDocumentURI();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Attr> c = new Callable<Attr>() { public Attr call() {
            nsIDOMAttr result = getInstanceAsnsIDOMDocument().createAttributeNS(namespaceURI, qualifiedName);
            return (Attr) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Text createTextNode(final String data)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Text> c = new Callable<Text>() { public Text call() {
            nsIDOMText result = getInstanceAsnsIDOMDocument().createTextNode(data);
            return (Text) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    /***************************************************************
     *
     * DocumentView implementation code
     *
     ***************************************************************/

    public DocumentImpl(nsIDOMDocumentView mozInst)
    {
        super( mozInst );
    }

    public static DocumentImpl getDOMInstance(nsIDOMDocumentView mozInst)
    {

        DocumentImpl node = (DocumentImpl) instances.get(mozInst);
        return node == null ? new DocumentImpl(mozInst) : node;
    }

    public nsIDOMDocumentView getInstanceAsnsIDOMDocumentView()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMDocumentView) moz.queryInterface(nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID);
    }

    public AbstractView getDefaultView()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<AbstractView> c = new Callable<AbstractView>() { public AbstractView call() {
            nsIDOMAbstractView result = getInstanceAsnsIDOMDocumentView().getDefaultView();
            return new AbstractViewImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    /***************************************************************
     *
     * DocumentRange implementation code
     *
     ***************************************************************/

    public DocumentImpl(nsIDOMDocumentRange mozInst)
    {
        super( mozInst );
    }

    public static DocumentImpl getDOMInstance(nsIDOMDocumentRange mozInst)
    {

        DocumentImpl node = (DocumentImpl) instances.get(mozInst);
        return node == null ? new DocumentImpl(mozInst) : node;
    }

    public nsIDOMDocumentRange getInstanceAsnsIDOMDocumentRange()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMDocumentRange) moz.queryInterface(nsIDOMDocumentRange.NS_IDOMDOCUMENTRANGE_IID);
    }

    public Range createRange()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Range> c = new Callable<Range>() { public Range call() {
            nsIDOMRange result = getInstanceAsnsIDOMDocumentRange().createRange();
            return new RangeImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    /***************************************************************
     *
     * DocumentEvent implementation code
     *
     ***************************************************************/

    public DocumentImpl(nsIDOMDocumentEvent mozInst)
    {
        super( mozInst );
    }

    public static DocumentImpl getDOMInstance(nsIDOMDocumentEvent mozInst)
    {

        DocumentImpl node = (DocumentImpl) instances.get(mozInst);
        return node == null ? new DocumentImpl(mozInst) : node;
    }

    public nsIDOMDocumentEvent getInstanceAsnsIDOMDocumentEvent()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMDocumentEvent) moz.queryInterface(nsIDOMDocumentEvent.NS_IDOMDOCUMENTEVENT_IID);
    }

    public Event createEvent(final String eventType)
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Event> c = new Callable<Event>() { public Event call() {
            nsIDOMEvent result = getInstanceAsnsIDOMDocumentEvent().createEvent(eventType);
            return new EventImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    /***************************************************************
     *
     * DocumentTraversal implementation code
     *
     ***************************************************************/

    public DocumentImpl(nsIDOMDocumentTraversal mozInst)
    {
        super( mozInst );
    }

    public static DocumentImpl getDOMInstance(nsIDOMDocumentTraversal mozInst)
    {

        DocumentImpl node = (DocumentImpl) instances.get(mozInst);
        return node == null ? new DocumentImpl(mozInst) : node;
    }

    public nsIDOMDocumentTraversal getInstanceAsnsIDOMDocumentTraversal()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMDocumentTraversal) moz.queryInterface(nsIDOMDocumentTraversal.NS_IDOMDOCUMENTTRAVERSAL_IID);
    }

    public TreeWalker createTreeWalker(final Node root, final int whatToShow, final NodeFilter filter, final boolean entityReferenceExpansion)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozRoot = root!=null ? ((NodeImpl) root).getInstance() : null;
        final nsIDOMNodeFilter mozFilter = filter!=null ? ((NodeFilterImpl) filter).getInstance() : null;
        Callable<TreeWalker> c = new Callable<TreeWalker>() { public TreeWalker call() {
            nsIDOMTreeWalker result = getInstanceAsnsIDOMDocumentTraversal().createTreeWalker(mozRoot, whatToShow, mozFilter, entityReferenceExpansion);
            return new TreeWalkerImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public NodeIterator createNodeIterator(final Node root, final int whatToShow, final NodeFilter filter, final boolean entityReferenceExpansion)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozRoot = root!=null ? ((NodeImpl) root).getInstance() : null;
        final nsIDOMNodeFilter mozFilter = filter!=null ? ((NodeFilterImpl) filter).getInstance() : null;
        Callable<NodeIterator> c = new Callable<NodeIterator>() { public NodeIterator call() {
            nsIDOMNodeIterator result = getInstanceAsnsIDOMDocumentTraversal().createNodeIterator(mozRoot, whatToShow, mozFilter, entityReferenceExpansion);
            return new NodeIteratorImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }



}
