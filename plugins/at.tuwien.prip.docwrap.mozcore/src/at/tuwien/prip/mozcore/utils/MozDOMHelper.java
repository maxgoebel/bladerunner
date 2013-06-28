package at.tuwien.prip.mozcore.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import at.tuwien.prip.common.log.ErrorDump;

public class MozDOMHelper {

//	public static boolean isFromMozillaDOM(Node n) {
//	final DOMImplementation di;
//	if (n instanceof Document) {
//	Document d = (Document) n;
//	di = d.getImplementation();
//	} else {
//	di = n.getOwnerDocument().getImplementation();
//	}
//	try {
//	return di.getClass().getName().startsWith("org.mozilla");
//	}
//	catch (NullPointerException e) {
//	return false;
//	}
//	}

	public static final String ATTRIBUTE_NAME_MANGLING_REGEXP = "[^a-zA-Z_0-9-]";

//	public static Document importMozDoc(nsIDOMDocument nsdoc)
//	{
//		wlIJavaObjContainer jc = ProxyUtils.create("@weblearn.org/java-obj-container;1", wlIJavaObjContainer.class);
//		Document javaDoc = createEmptyDocument();
//
//		WebLearnNative nat = WebLearnNative.getInstance();
//		long ref = nat.newGlobalReference(javaDoc);
//		jc.setJObjRef(ref);
//		nat.deleteGlobalReference(ref);
//
//		try {
//			SimpleTimer st = new SimpleTimer();
//			st.startTask(1);
//			wlIDOMUtils du = ProxyUtils.createProxied("@weblearn.org/domutils;1", wlIDOMUtils.class);
//			du._clone(nsdoc, jc, wlIDOMUtils.DOM_ONLY);//DOM_AND_SELECTED_VISUAL_PROPERTIES);
//
////			for (Element e : DOMHelper.Tree.Descendant.getDescendantsAndSelfElements(javaDoc.getDocumentElement())) {
////			HashMap m1 = (HashMap) e.getUserData("visprop");
////			ErrorDump.debug(MozDOMHelper.class, ""+m1);
////			HashMap m2 = (HashMap) e.getUserData("boxprop");
////			ErrorDump.debug(MozDOMHelper.class, ""+m2.size());
////			}
//
//			nsIDOM3Document nsdoc3 = ProxyUtils.qi(nsdoc, nsIDOM3Document.class);
//			if (nsdoc3!=null) {
//				String uri = nsdoc3.getDocumentURI();
//				ErrorDump.debug(MozDOMHelper.class, "document : %s", uri);
//				javaDoc.setDocumentURI(uri);
//			}
//
//			st.stopTask(1);
//			ErrorDump.debug(MozDOMHelper.class, "document cloning: %dms", st.getTimeMillis(1));
//			//ErrorDump.debug(MozDOMHelper.class, DOMHelper.LoadSave.writeDOMToString(javaDoc));
//		} catch (Throwable e) {
//			return null;
////			ErrorDump.error(MozDOMHelper.class, e);
//		}
//
//		return javaDoc;
//	}


	/**
	 * creates an empty DOM document
	 */
	public static Document createEmptyDocument(String root_elem_name)
	{
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			Element elm = doc.createElement(root_elem_name);
			doc.appendChild(elm);
			return doc;
		}
		catch (ParserConfigurationException e) {
			ErrorDump.error(MozDOMHelper.class, e);
			return null;
		}
	}

	/**
	 * creates an empty DOM document without root element
	 */
	public static Document createEmptyDocument()
	{
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			return doc;
		}
		catch (ParserConfigurationException e) {
			ErrorDump.error(MozDOMHelper.class, e);
			return null;
		}
	}

}
//public static Document importMozDoc(Document mozDoc) {
//if (!isFromMozillaDOM(mozDoc)) return mozDoc;

//nsIDOMDocument dd = ((org.mozilla.dom.DocumentImpl) mozDoc).getInstance();
//return importMozDoc(dd);
//}

///**
//* CeMi: copied from com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl in jdk1.5.0_05
//*/
//private static Node importMozNode(DocumentImpl javaDoc,
//Node source,
//boolean deep,
//boolean cloningDoc)
//throws DOMException
//{
//Node newnode=null;

//int type = source.getNodeType();
//switch (type) {
//case Document.ELEMENT_NODE: {
//Element newElement;

//boolean domLevel20 = source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0");
//// Create element according to namespace support/qualification.
//if(domLevel20 == false || source.getLocalName() == null)
//newElement = javaDoc.createElement(source.getNodeName());
//else
//newElement = javaDoc.createElementNS(source.getNamespaceURI(),
//source.getNodeName());

//// Copy element's attributes, if any.
//NamedNodeMap sourceAttrs = source.getAttributes();
//if (sourceAttrs != null) {
//int length = sourceAttrs.getLength();
//for (int index = 0; index < length; index++) {
//Attr attr = (Attr)sourceAttrs.item(index);

//// NOTE: this methods is used for both importingNode
//// and cloning the document node. In case of the
//// clonning default attributes should be copied.
//// But for importNode defaults should be ignored.
//if (attr.getSpecified() || cloningDoc) {
//Attr newAttr = (Attr)importMozNode(javaDoc, attr, true, cloningDoc);
//// Attach attribute according to namespace
//// support/qualification.
//if (domLevel20 == false ||
//attr.getLocalName() == null)
//newElement.setAttributeNode(newAttr);
//else
//newElement.setAttributeNodeNS(newAttr);
//}
//}
//}

//newnode = newElement;
//break;
//}

//case Document.DOCUMENT_TYPE_NODE: {
//// unless this is used as part of cloning a Document
//// forbid it for the sake of being compliant to the DOM spec
//if (!cloningDoc) {
//String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
//throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
//}
//DocumentType srcdoctype = (DocumentType)source;
//DocumentTypeImpl newdoctype = (DocumentTypeImpl)
//javaDoc.createDocumentType(srcdoctype.getNodeName(),
//srcdoctype.getPublicId(),
//srcdoctype.getSystemId());
//// Values are on NamedNodeMaps
//NamedNodeMap smap = srcdoctype.getEntities();
//NamedNodeMap tmap = newdoctype.getEntities();
//if(smap != null) {
//for(int i = 0; i < smap.getLength(); i++) {
//tmap.setNamedItem(importMozNode(javaDoc, smap.item(i), true, true));
//}
//}
//smap = srcdoctype.getNotations();
//tmap = newdoctype.getNotations();
//if (smap != null) {
//for(int i = 0; i < smap.getLength(); i++) {
//tmap.setNamedItem(importMozNode(javaDoc, smap.item(i), true, true));
//}
//}

//// NOTE: At this time, the DOM definition of DocumentType
//// doesn't cover Elements and their Attributes. domimpl's
//// extentions in that area will not be preserved, even if
//// copying from domimpl to domimpl. We could special-case
//// that here. Arguably we should. Consider. ?????
//newnode = newdoctype;
//break;
//}


//case Document.ATTRIBUTE_NODE:

////ErrorDump.debugf(MozDOMHelper.class, "name=<%s>", source.getNodeName());

////CeMi:
////workaround for HTML pages (not XHTML)
////that define somewhere an xmlns attribute
////e.g.
////benchmark/corpus_julien/s10-a-0/page-0.html
//Attr ain = (Attr) source;
//String aname = ain.getName();
//String nu = ain.getNamespaceURI();
//if ((nu==null || nu.equals("")) &&
//aname.equalsIgnoreCase("xmlns"))
//{
//Attr a = javaDoc.createAttribute("XMLNS");
//a.setValue(a.getValue());
//newnode = a;
//break;
//}

////CeMi:
////workaround for strange attributes such as ";"
////appears e.g. on www.lemonde.fr
//if (aname.matches(".*"+ATTRIBUTE_NAME_MANGLING_REGEXP+".*")) {
//String aname2 = aname.replaceAll(ATTRIBUTE_NAME_MANGLING_REGEXP, "x");
//Attr a = javaDoc.createAttribute(aname2);
//a.setValue(a.getValue());
//newnode = a;
//break;
//}

//case Document.PROCESSING_INSTRUCTION_NODE:
//case Document.NOTATION_NODE:
//case Document.CDATA_SECTION_NODE:
//case Document.ENTITY_REFERENCE_NODE:
//case Document.ENTITY_NODE:
//case Document.COMMENT_NODE:
//case Document.DOCUMENT_FRAGMENT_NODE:
//case Document.TEXT_NODE:
//{
//newnode = javaDoc.importNode(source, true);
//break;
//}

//case Document.DOCUMENT_NODE : // Can't import document nodes
//default: {           // Unknown node type
//String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
//throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
//}
//}

////if(userData != null)
////    importer.callUserDataHandlers(source, newnode, UserDataHandler.NODE_IMPORTED,userData);

//// If deep, replicate and attach the kids.
//if (deep) {
//for (Node srckid = source.getFirstChild();
//srckid != null;
//srckid = srckid.getNextSibling()) {
//newnode.appendChild(importMozNode(javaDoc, srckid, true, cloningDoc));
//}
//}
//if (newnode.getNodeType() == Node.ENTITY_NODE) {
//((NodeImpl)newnode).setReadOnly(true, true);
//}
//return newnode;

//}


