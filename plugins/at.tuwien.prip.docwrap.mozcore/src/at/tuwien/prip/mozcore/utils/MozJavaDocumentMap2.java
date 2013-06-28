package at.tuwien.prip.mozcore.utils;

import static at.tuwien.prip.mozcore.utils.ProxyUtils.qi;

import java.util.Stack;

import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMHTMLFrameElement;
import org.mozilla.interfaces.nsIDOMHTMLIFrameElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MozJavaDocumentMap2 {

	public static final String ATTRIBUTE_NAME_MANGLING_REGEXP = "[^a-zA-Z_0-9-]";
	
//    private BidiMap<nsIDOMDocument, Document> mozDoc2javaDoc = new BidiMap<nsIDOMDocument, Document>();
//
//    private Document map_mozDoc2javaDoc(nsIDOMDocument mozDoc) {
//        Document javaDoc = mozDoc2javaDoc.get(mozDoc);
//
//        if (javaDoc==null) {
//            //ErrorDump.debug(this, "cloning document");
//            javaDoc = MozDOMHelper.importMozDoc(mozDoc);
//            mozDoc2javaDoc.put(mozDoc, javaDoc);
//            //ErrorDump.debug(this, "done");
//            //ErrorDump.debug(MozDOMHelper.class,
//            //                DOMHelper.LoadSave.writeDOMToString(javaDoc));
//        }
//
//        return javaDoc;
//    }

	/**
	 * 
	 */
    public static nsIDOMNode nodeCorespodence(Node in, nsIDOMDocument dout) 
    {
        if ((in instanceof Document) && in.getParentNode()==null) return dout;

        Stack<String> names = new Stack<String>();
        Stack<Integer> indices = new Stack<Integer>();
        Node n = in;
        while (n!=null) {
            int idx = -1;
            Node ps = n;
            while (ps!=null) {
                idx++;
                ps = ps.getPreviousSibling();
            }
            String name = n.getNodeName();
            names.push(name);
            indices.push(idx);
            n = n.getParentNode();
        }
        //System.err.println("names="+names);
        //System.err.println("indices="+indices);

        assert names.peek().equals("#document");
        names.pop();
        indices.pop();

        //System.err.println("names="+names);
        //System.err.println("indices="+indices);
        boolean first = true;
        nsIDOMNode out = null;
        while (!indices.isEmpty()) {
            int idx = indices.pop();
            String name = names.pop();

            if (first) {
                first = false;
                out = dout.getDocumentElement();
            } else {

                if (name.equalsIgnoreCase("html")) {
                    String nn = out.getNodeName();
                    if (nn.equalsIgnoreCase("frame")) {
                        //workaround for DOMClone not coping DocumentType nodes,
                        //otherwise 0 should be used
                        //idx = 1;
                        nsIDOMHTMLFrameElement e = qi(out, nsIDOMHTMLFrameElement.class);
                        out = e.getOwnerDocument().getDocumentElement();
                    } else if (nn.equalsIgnoreCase("iframe")) {
                        nsIDOMHTMLIFrameElement e = qi(out, nsIDOMHTMLIFrameElement.class);
                        out = e.getOwnerDocument().getDocumentElement();
                    } else {
                        out = out.getChildNodes().item(idx);
                    }
                } else {
                    out = out.getChildNodes().item(idx);
                }

            }

            if (out==null) break;

            name = name.replace(ATTRIBUTE_NAME_MANGLING_REGEXP, "x");
            String oname = out.getNodeName().replace(ATTRIBUTE_NAME_MANGLING_REGEXP, "x");
            if (//ignorecase due XHTML to HTML conversion
                !name.equalsIgnoreCase(oname)) {
                //matching failed
                out = null;
                break;
            }
        }

        return out;
    }

    public static Node nodeCorespodence(nsIDOMNode in, Document dout) {
        //FIXME fix for frames here
        if ((in instanceof nsIDOMDocument) && in.getParentNode()==null) return dout;

        Stack<String> names = new Stack<String>();
        Stack<Integer> indices = new Stack<Integer>();
        nsIDOMNode n = in;
        while (n!=null) {
            int idx = -1;
            nsIDOMNode ps = n;
            while (ps!=null) {
                idx++;
                ps = ps.getPreviousSibling();
            }
            String name = n.getNodeName();
            names.push(name);
            indices.push(idx);
            //FIXME fix for frames here
            n = n.getParentNode();
        }
        //System.err.println("names="+names);
        //System.err.println("indices="+indices);

        assert names.peek().equals("#document");
        names.pop();
        indices.pop();

        //System.err.println("names="+names);
        //System.err.println("indices="+indices);
        boolean first = true;
        Node out = null;
        while (!indices.isEmpty()) {
            int idx = indices.pop();
            String name = names.pop();

            if (first) {
                first = false;
                out = dout.getDocumentElement();
            } else {
                //workaround for DOMClone not coping DocumentType nodes
//                if (!fromMoz) {
//                    if (name.equalsIgnoreCase("html")) {
//                        String nn = out.getNodeName();
//                        if (nn.equalsIgnoreCase("frame") ||
//                            nn.equalsIgnoreCase("iframe"))
//                        {
//                            idx = 1;
//                        }
//                    }
//                }

                out = out.getChildNodes().item(idx);
            }

            if (out==null) break;

            name = name.replace(ATTRIBUTE_NAME_MANGLING_REGEXP, "x");
            String oname = out.getNodeName().replace(ATTRIBUTE_NAME_MANGLING_REGEXP, "x");
            if (//ignorecase due XHTML to HTML conversion
                !name.equalsIgnoreCase(oname)) {
                //matching failed
                out = null;
                break;
            }
        }

        return out;
    }

//    private static Node nodeCorespodence(Node in, Document dout) {
//        if (in instanceof Document) return dout;
//
//        Stack<String> names = new Stack<String>();
//        Stack<Integer> indices = new Stack<Integer>();
//        Node n = in;
//        while (n!=null) {
//            int idx = -1;
//            Node ps = n;
//            while (ps!=null) {
//                idx++;
//                ps = ps.getPreviousSibling();
//            }
//            names.push(n.getNodeName());
//            indices.push(idx);
//            n = n.getParentNode();
//        }
//
//        boolean first = true;
//        Node out = null;
//        while (!indices.isEmpty()) {
//            int idx = indices.pop();
//            String name = names.pop();
//
//            if (first) {
//                first = false;
//                out = dout;
//
//                //Mozilla XHTML hack:
//                //
//                //mozilla XHTML documents have namespace,
//                //and they look like
//                // #document
//                //    html  (some strange type node)
//                //    html  (real data node)
//                //and the java documents look like:
//                // #document
//                //    HTML  (real data node)
//                if (!indices.isEmpty()) {
//                    if (MozDOMHelper.isFromMozillaDOM(dout)) {
//                        assert names.peek().equalsIgnoreCase("HTML");
//                        assert indices.peek()==1;
//                        int i = indices.pop();
//                        indices.push(i+1);
//                    } else if (MozDOMHelper.isFromMozillaDOM(in.getOwnerDocument())) {
//                        assert names.peek().equalsIgnoreCase("HTML");
//                        assert indices.peek()==2;
//                        int i = indices.pop();
//                        indices.push(i-1);
//                    }
//                }
//            } else {
//                out = out.getChildNodes().item(idx);
//            }
//
//            if (out==null ||
//                //ignorecase due XHTML to HTML conversion
//                !name.equalsIgnoreCase(out.getNodeName())) {
//                //matching failed
//                out = null;
//                break;
//            }
//        }
//
//        return out;
//    }

//    //calling Document.getOwnerDocument on mozilla-dom
//    //implementation returns null. this is an workaround
//    private static Document getJavaOwnerDocument(Node n) {
//        if (n instanceof Document) return (Document) n;
//        else return n.getOwnerDocument();
//    }
//    private static nsIDOMDocument getMozOwnerDocument(nsIDOMNode n) {
//        if (n.getNodeType()==nsIDOMNode.DOCUMENT_NODE)
//            return qi(n, nsIDOMDocument.class);
//        else
//            return n.getOwnerDocument();
//    }

    public static Node mozNode2javaNode(nsIDOMNode mozNode, Document javaDoc) {
        if (mozNode==null) return null;

        //Node javaNode = mozNode2javaNode.get(mozNode);
        //if (javaNode!=null) return javaNode; //already in cache

//        nsIDOMDocument mozDoc = getMozOwnerDocument(mozNode);
//        Document javaDoc = map_mozDoc2javaDoc(mozDoc);
//        if (javaDoc==null) return null;

        Node javaNode = nodeCorespodence(mozNode, javaDoc);
        //if (javaNode!=null) {
        //    mozNode2javaNode.put(mozNode, javaNode);
        //}

        return javaNode;
    }

    public static Element mozElement2javaElement(nsIDOMElement mozNode, Document javaDoc) {
        return (Element) mozNode2javaNode(mozNode, javaDoc);
    }

//    public Document mozDoc2javaDoc(nsIDOMDocument mozDoc) {
//        return (Document) mozNode2javaNode((nsIDOMNode) mozDoc);
//    }

    /**
     * 
     */
    public static nsIDOMNode javaNode2mozNode(Node javaNode, nsIDOMDocument mozDoc) 
    {
        if (javaNode==null) return null;

        //Node mozNode = javaNode2mozNode.get(javaNode);
        //if (mozNode!=null) return mozNode; //already in cache

//        Document javaDoc = getJavaOwnerDocument(javaNode);
//        nsIDOMDocument mozDoc = mozDoc2javaDoc.reverseGet(javaDoc);
//        if (mozDoc==null) return null;

        nsIDOMNode mozNode = nodeCorespodence(javaNode, mozDoc);
        //if (mozNode!=null) {
        //    javaNode2mozNode.put(javaNode, mozNode);
        //}

        return mozNode;
    }

    public static nsIDOMElement javaElement2mozElement(Element javaNode, nsIDOMDocument mozDoc) 
    {
        return qi(javaNode2mozNode(javaNode, mozDoc), nsIDOMElement.class);
    }

    //mcg: this does not make any sense, DO NOT USE...
//    public static nsIDOMDocument javaDoc2mozDoc(Document javaDoc, nsIDOMDocument mozDoc) {
//        return qi(javaNode2mozNode((Node) javaDoc, mozDoc), nsIDOMDocument.class);
//    }
    
//    public static Document mozDocument2javaDocument(nsIDOMDocument doc, Document javaDoc) {
//    	  return (Document) nodeCorespodence(doc, javaDoc);
//    }

}
