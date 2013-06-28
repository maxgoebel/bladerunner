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
package at.tuwien.prip.model.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import at.tuwien.prip.common.exceptions.DOMDTDException;
import at.tuwien.prip.common.exceptions.DOMElementException;
import at.tuwien.prip.common.exceptions.DOMValueException;
import at.tuwien.prip.common.exceptions.XPathSyntaxException;
import at.tuwien.prip.common.log.ErrorDump;


/**
 * DOMHelper
 *
 * utility functions for working with
 * org.w3c.dom documents
 *
 * Created: Mon Mar 13 05:37:17 2002
 *
 * @author Peter Kolenic, Michal Ceresna, Max Goebel
 * @version
 */
public class DOMHelper
{

	public static class LoadSave {

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
				ErrorDump.error(LoadSave.class, e);
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
				ErrorDump.error(LoadSave.class, e);
				return null;
			}
		}

		private static DocumentBuilder createDocBuilder(EntityResolver er,
				ErrorHandler eh)
		throws SAXException
		{
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

				if (er!=null) {
					//do DTD validation
					dbf.setValidating(true);
				}
				else {
					//no validation against DTD
					dbf.setValidating(false);
				}
				dbf.setNamespaceAware(true);

				DocumentBuilder db = dbf.newDocumentBuilder();
				db.setErrorHandler(eh);
				db.setEntityResolver(er);

				return db;
			}
			catch (ParserConfigurationException e) {
				throw new SAXException(e);
			}
		}

		/**
		 * Read DOM from file.
		 *
		 * If resolver!=null use dtd validation and resolver
		 * to resolve dtd's location
		 */
		public static Document readDOMFromFile(File f,
				EntityResolver resolver)
		throws
		IOException,
		SAXException,
		DOMDTDException
		{
			InputStream is = null;
			try {
				is = new FileInputStream(f);
				return readDOMFromStream(is, resolver);
			}
			finally {
				if (is!=null) is.close();
			}
		}

		public static Document readDOMFromFile(String fname,
				EntityResolver resolver)
		throws
		IOException,
		SAXException,
		DOMDTDException
		{
			File f = new File(fname);
			return readDOMFromFile(f, resolver);
		}

		/**
		 * Read DOM from stream.
		 *
		 * If resolver!=null use dtd validation and resolver
		 * to resolve dtd's location
		 */
		public static Document readDOMFromStream(InputStream is,
				EntityResolver resolver)
		throws
		IOException,
		SAXException,
		DOMDTDException
		{
			InputSource isrc = new InputSource(is);
			return readDOMFromSource(isrc, resolver);
		}

		/**
		 * Read DOM from stream without dtd validation
		 */
		public static Document readDOMFromStream(InputStream is)
		throws
		IOException,
		SAXException,
		DOMDTDException
		{
			return readDOMFromStream(is, null);
		}

		/**
		 * Read DOM from string.
		 *
		 * If resolver!=null use dtd validation and resolver
		 * to resolve dtd's location
		 */
		public static Document readDOMFromString(String xml,
				EntityResolver er)
		throws
		IOException,
		SAXException,
		DOMDTDException
		{
			Reader r = null;
			try {
				r = new StringReader(xml);
				InputSource is = new InputSource(r);
				return readDOMFromSource(is, er);
			}
			finally {
				if (r!=null) r.close();
			}
		}

		/**
		 * Read DOM from string.
		 * If resolver!=null use dtd validation and resolver
		 * to resolve dtd's location
		 */
		private static Document readDOMFromSource(InputSource is,
				EntityResolver er)
		throws
		IOException,
		SAXException,
		DOMDTDException
		{
			ErrorHandler eh = getErrorHandler(er);
			try {
				//create the document parser
				DocumentBuilder db = createDocBuilder(er, eh);

				//create the document
				Document doc = db.parse(is);

				return doc;
			}
			catch (SAXException e) {
				if (eh instanceof PessimisticErrorHandler &&
						((PessimisticErrorHandler) eh).isDTDError())
					throw new DOMDTDException(e);

				throw e;
			}
		}


		public static void writeDOMToFile(Document doc,
				File f)
		throws IOException
		{
			OutputStream os = null;
			try {
				os = new BufferedOutputStream(new FileOutputStream(f));
				writeDOMToStream(doc, os);
			}
			finally {
				//to be sure, that we close the stream
				//also in case, when an error occurs
				if (os!=null) os.close();
			}
		}

		public static String writeDOMToString( Document doc )
		{
			try {
				StringWriter w = new StringWriter();
				try {
					StreamResult sr = new StreamResult(w);
					writeDOMToSource(doc, sr,  "UTF-8");
					return w.toString();
				}
				finally {
					//to be sure, that we close the writer
					//also in case, when an error occurs
					if (w!=null) w.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public static String writeDOMToString(Node n)
		{
			try {
				StringWriter w = new StringWriter();
				try {
					StreamResult sr = new StreamResult(w);
					writeDOMToSource(n, sr,  "UTF-8");
					return w.toString();
				} finally {
					//to be sure, that we close the writer
					//also in case, when an error occurs
					if (w!=null) w.close();
				}
			} catch (IOException e) {
				ErrorDump.error(LoadSave.class, e);
				return null;
			}
		}

		/**
		 * Read DOM from stream.
		 * If resolver!=null use dtd validation and resolver
		 * to resolve dtd's location
		 */
		public static void writeDOMToStream(Document doc,
				OutputStream os)
		throws IOException
		{
			writeDOMToStream(doc, os, "UTF-8");
		}
		public static void writeDOMToStream(Node n,
				OutputStream os)
		throws IOException
		{
			StreamResult sr = new StreamResult(os);
			writeDOMToSource(n, sr, "UTF-8");
		}

		public static void writeDOMToStream(Document doc,
				OutputStream os,
				String encoding)
		throws IOException
		{
			StreamResult sr = new StreamResult(os);
			writeDOMToSource(doc, sr, encoding);
		}

		public static void writeDOMToSource(Node n,
				StreamResult sr,
				String encoding)
		throws IOException
		{
			try {
				//create transformer
				TransformerFactory trf = TransformerFactory.newInstance();
				Transformer tr = trf.newTransformer();
				tr.setOutputProperty(OutputKeys.METHOD, "html");
				tr.setOutputProperty(OutputKeys.ENCODING, encoding);
				tr.setOutputProperty(OutputKeys.INDENT, "yes");

				//serialize the DOM tree
				tr.transform(new DOMSource(n), sr);
			}
			catch (TransformerException e) {
				//wrap to IOException
				IOException ioe = new IOException();
				ioe.initCause(e);
				throw ioe;
			}
		}

		/*
         //options for xerces's parser
         private static String FEATURE_VALIDATION =
             "http://xml.org/sax/features/validation";
         private static String FEATURE_DYNAMIC_VALIDATION =
             "http://apache.org/xml/features/validation/dynamic";
         private static String FEATURE_LOAD_EXTERNAL_DTD =
             "http://apache.org/xml/features/nonvalidating/load-external-dtd";
         private static String FEATURE_INCLUDE_WHITESPACE =
             "http://apache.org/xml/features/dom/include-ignorable-whitespace";
		 */

		private static ErrorHandler getErrorHandler(EntityResolver er)
		throws SAXException
		{
			if (er!=null) {
				//do DTD validation
				return new PessimisticErrorHandler();
			}
			//no validation against DTD
			return new OptimisticErrorHandler();
		}

		static class PessimisticErrorHandler implements ErrorHandler
		{
			private boolean DTD_error = false;

			protected boolean isDTDError()
			{
				return DTD_error;
			}

			public void warning(SAXParseException e)
			throws SAXException
			{
				DTD_error = true;
				throw e;
			}

			public void error(SAXParseException e)
			throws SAXException
			{
				DTD_error = true;
				throw e;
			}

			public void fatalError(SAXParseException e)
			throws SAXException
			{
				throw e;
			}
		}

		static class OptimisticErrorHandler implements ErrorHandler
		{
			public void warning(SAXParseException e)
			throws SAXException
			{
				//ignore this error
			}

			public void error(SAXParseException e)
			throws SAXException
			{
				//ignore this error
			}

			public void fatalError(SAXParseException e)
			throws SAXException
			{
				throw e;
			}
		}
	}


	public static class Copy {

		public static void copyAllChildNodes(Element fromNode, Element toNode) {
			Document doc = toNode.getOwnerDocument();
			NodeList nl = fromNode.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				n = doc.importNode(n, true);
				toNode.appendChild(n);
			}
		}

		public static void copyAllAttributes(Element fromNode, Element toNode) {
			NamedNodeMap map = fromNode.getAttributes();
			Document doc = toNode.getOwnerDocument();
			for (int i = 0; i < map.getLength(); i++) {
				Node n = map.item(i);
				Node newNode = doc.importNode(n, false);
				toNode.setAttributeNode((Attr)newNode);
			}
		}

	}

	public static class XPath {

		public static String getExactXPath(Node n) {
			return getExactXPath(n, true, false);
		}

		public static String getExactXPath(Node n,
				boolean addIndices,
				boolean addAttributes)
		{
			return getExactXPath(n, true, addIndices, false, addAttributes, false);
		}

		public static String getExactXPath(Node n,
				boolean addNodeNames,
				boolean addIndices,
				boolean addCount,
				boolean addAttributes,
				boolean addLeafText)
		{
			return getRelativeXPath(null, n, addNodeNames, addIndices, addCount, addAttributes, addLeafText);
		}

		public static String getIndirectExactXPath(Node context, Node target)
		{
			Node common = Tree.Ancestor.getClosestCommonAncestor(context, target);
			String xpup = getRelativeUpwardXPath(common, context, true, false);
			String xpdown = getRelativeExactXPath(common, target);

			final String xp;
			if (xpdown.equals("."))
				xp = xpup;
			else if (xpdown.startsWith("./"))
				xp = xpup + xpdown.substring(1);
			else
				xp = xpup + "/" + xpdown;

			return xp;
		}

		public static String
		getRelativeUpwardXPath(Node ancestor,
				Node n,
				boolean addNodeNames,
				boolean addAttributes)
		{
			LinkedList<String> names = new LinkedList<String>();
			LinkedList<String> attribs = new LinkedList<String>();

			boolean reachedAncestor = false;
			while (n!=null && n.getNodeType()==Node.ELEMENT_NODE) {

				String name = n.getNodeName();
				//ErrorDump.debug(this, "name="+name);
				names.add(name);

				if (addAttributes) {
					attribs.add(getAttributeXPath(n));
				}

				if (ancestor!=null && n.isSameNode(ancestor)) {
					reachedAncestor = true;
					break;
				}

				//move to parent
				n = n.getParentNode();
			}

			if (ancestor!=null && !reachedAncestor)
				return null;

			StringBuffer sb = new StringBuffer();
			boolean first = true;
			while (!names.isEmpty()) {
				String name = names.removeFirst();
				if (first) {
					sb.append(".");
					first = false;
				} else {
					sb.append("/parent::");
					if (addNodeNames) sb.append(name);
					else sb.append('*');
				}

				if (addAttributes) {
					String a = attribs.removeFirst();
					if (a.length()>0) sb.append("["+a+"]");
				}
			}

			return sb.toString();
		}

		public static String getRelativeExactXPath(Node ancestor, Node descendant)
		{
			return getRelativeXPath(ancestor, descendant, true, true, false, false, false);
		}

		/**
		 *
		 * @param context
		 * @param target
		 * @param addNodeNames
		 * @param addIndices
		 * @param addCount
		 * @param addAttributes
		 * @param addLeafText
		 * @return
		 */
		public static String getRelativeXPath(Node context,
				Node target,
				boolean addNodeNames,
				boolean addIndices,
				boolean addCount,
				boolean addAttributes,
				boolean addLeafText)
		{
			Stack<String> names = new Stack<String>();
			Stack<Integer> indices = new Stack<Integer>();
			Stack<Integer> counts = new Stack<Integer>();
			Stack<String> attribs = new Stack<String>();

			Node n = target;
			boolean reachedContext = false;
			while (n!=null && n.getNodeType()==Node.ELEMENT_NODE)
			{
				if (context!=null && n.isSameNode(context))
				{
					reachedContext = true;
					break;
				}

				String name = n.getNodeName();
				//ErrorDump.debug(this, "name="+name);
				if (addNodeNames)
				{
					names.push(name);
				} else
				{
					names.push("*");
				}

				if (addIndices)
				{
					int idx = 1;
					Node x = n.getPreviousSibling();
					while (x!=null)
					{
						//						ErrorDump.debug(DOMHelper.class, "    prev="+x.getNodeName()+" "+x.getNodeType());
						if (x.getNodeType()==Node.ELEMENT_NODE &&
								(!addNodeNames ||
										x.getNodeName().equalsIgnoreCase(name)))
						{
							idx++;
						}
						x = x.getPreviousSibling();
					}
					//ErrorDump.debug(this, "idx="+idx);
					indices.push(idx);

					if (addCount)
					{
						int cnt;
						Node p = n.getParentNode();
						if (p==null) cnt=1;
						else
						{
							cnt = 0;
							x = p.getFirstChild();
							while (x!=null) {
								if (x.getNodeType()==Node.ELEMENT_NODE &&
										(!addNodeNames ||
												x.getNodeName().equalsIgnoreCase(name)))
								{
									cnt++;
								}
								x = x.getNextSibling();
							}
						}
						//ErrorDump.debug(this, "cnt="+cnt);
						counts.push(cnt);
					}
				}

				if (addAttributes) {
					attribs.push(getAttributeXPath(n));
				}

				//move to parent
				n = n.getParentNode();
			}

			if (context!=null && !reachedContext)
				return null;

			StringBuffer sb = new StringBuffer();
			if (context!=null) sb.append(".");
			while (!names.isEmpty())
			{
				//if (!name.equalsIgnoreCase("#document")) {
				String name = names.pop();
				sb.append('/'+name);
				//}
				if (addIndices) {
					int index = indices.pop();
					sb.append("["+index);
					if (addCount) {
						int cnt = counts.pop();
						sb.append("of"+cnt);
					}
					sb.append("]");
				}
				if (addAttributes) {
					String a = attribs.pop();
					if (a.length()>0) sb.append("["+a+"]");
				}
			}

			if (addLeafText) {
				String text = Text.getNodeText_WithoutDescendatElements(target);
				sb.append("[text()="+text+"]");
			}

			return sb.toString();
		}

		/**
		 *
		 * @param n
		 * @return
		 */
		private static String getAttributeXPath(Node n)
		{
			StringBuffer asb = new StringBuffer();
			NamedNodeMap as = n.getAttributes();
			for (int i=0; i<as.getLength(); i++)
			{
				Attr a = (Attr) as.item(i);
				String sa = getSingleAttributeXPath(a);
				if (asb.length()>0) asb.append(" and ");
				asb.append(sa);
			}
			return asb.toString();
		}

		/**
		 *
		 * @param a
		 * @return
		 */
		private static String getSingleAttributeXPath(Attr a)
		{
			String sa =
				String.format("@%s=\"%s\"",
						a.getName(),
						a.getValue());
			return sa;
		}

		/**
		 * Gets the the xpath for all kind of nodes and not just elements
		 * @param n Attributes, text, comment, processing instruction, element
		 * @return exact xpath to this node without attributes on the way
		 */
		public static String getExactXPathFromNode(Node n)
		{
			String suffix = "";
			Node parent = n;
			if (!(n instanceof Element))
			{
				//in case that n is not an element we have to append the necessary XPATH
				parent = n.getParentNode();  //needed for the attribute
				if (n instanceof Attr) {
					Attr a = (Attr)n;
					parent = a.getOwnerElement();
					suffix = "["+getSingleAttributeXPath(a)+"]";
				} else if (n instanceof Text){
					int index = Tree.Children.getChildNodeTypeIndex(n, Node.TEXT_NODE);
					if (index>=0) { //found an index
						suffix="/text()["+index+"]";
					}
				} else if (n instanceof Comment) {
					int index = Tree.Children.getChildNodeTypeIndex(n, Node.COMMENT_NODE);
					if (index>=0) { //found an index
						suffix="/comment()["+index+"]";
					}
				} else if (n instanceof ProcessingInstruction) {
					int index = Tree.Children.getChildNodeTypeIndex(n, Node.PROCESSING_INSTRUCTION_NODE);
					if (index>=0) { //found an index
						suffix="/processing-instruction()["+index+"]";
					}
				} else if (n instanceof Document) {
					return "/";
				}
			}
			String xpath = getExactXPath(parent);
			return xpath+suffix;
		}

		/**
		 * Evaluate the string xp on node input.
		 * @param input
		 * @param xp
		 * @return A list of elements below the input node that match the path xp.
		 * @throws XPathSyntaxException
		 */
		public static List<Node> evaluateXPath(Node input, String xp)
		throws XPathSyntaxException
		{
			try
			{
				javax.xml.xpath.XPath evaluator = XPathFactory.newInstance().newXPath();
				NodeList nodes = (NodeList) evaluator.evaluate(xp, input, XPathConstants.NODESET);

				List<Node> result = new LinkedList<Node>();
				int len = nodes.getLength();
				for (int i=0; i<len; i++)
				{
					Node n = nodes.item(i);
					result.add(n);
				}

				return result;
			}
			catch (XPathExpressionException e)
			{
				throw new XPathSyntaxException(e);
			}
		}

		/**
		 * Evaluate the string xpath from the input node and return
		 * only element nodes that match the xpath.
		 * @param input
		 * @param xp
		 * @return
		 * @throws XPathSyntaxException
		 */
		public static List<Element> evaluateXPathElements(Node input, String xp)
		throws XPathSyntaxException
		{
			List<Element> elements = new LinkedList<Element>();

			List<Node> nodes = evaluateXPath(input, xp);
			for (Node n : nodes)
			{
				if (n.getNodeType()==Node.ELEMENT_NODE)
				{
					Element e = (Element) n;
					elements.add(e);
				}
			}

			return elements;
		}

		/**
		 *
		 * @param input
		 * @param xp
		 * @return
		 * @throws XPathSyntaxException
		 */
		public static Node evaluateXPathFirst(Node input, String xp)
		throws XPathSyntaxException
		{
			List<Node> matches = evaluateXPath(input, xp);
			if (matches.isEmpty())
				return null;
			return matches.get(0);
		}

		/**
		 * Evaluate the xpath xp on input node and return the first match.
		 * @param input
		 * @param xp
		 * @return
		 * @throws XPathSyntaxException
		 */
		public static Element evaluateXPathFirstElement(Node input, String xp)
		throws XPathSyntaxException
		{
			List<Element> matches = evaluateXPathElements(input, xp);
			if (matches.isEmpty())
				return null;
			return matches.get(0);
		}

		/**
		 * Get the list of the nodes from the root to the input node.
		 *
		 * Root (#document node) is the first item in the list.
		 */
		public static List<Node> getNodesOnPathFromRoot(Node n)
		{
			List<Node> path = new LinkedList<Node>();
			while (n!=null)
			{
				path.add(n);
				n = n.getParentNode();
			}
			Collections.reverse(path);
			return path;
		}

		/**
		 *
		 * @param n
		 * @return
		 */
		public static LinkedList<Element> getElementsOnPathFromRoot(Node n)
		{
			LinkedList<Element> elems = new LinkedList<Element>();
			while (n!=null)
			{
				if (n.getNodeType()==Node.ELEMENT_NODE)
				{
					Element e = (Element) n;
					elems.add(e);
				}
				//move to parent
				n = n.getParentNode();
			}
			Collections.reverse(elems);

			return elems;
		}

		/**
		 * Returns elements on branch between ancestor and descendant,
		 * including both of them.
		 */
		public static List<Element> getElementsOnBranch(Node ancestor, Node descendant)
		{
			if (descendant==null) return new LinkedList<Element>();

			List<Element> elems = new LinkedList<Element>();
			Node n = descendant;
			while (true)
			{
				if (n.getNodeType()==Node.ELEMENT_NODE) {
					Element e = (Element) n;
					elems.add(e);
				}
				if (n.isSameNode(ancestor))
					break;

				//move to parent
				n = n.getParentNode();
				if (n==null) {
					//descendant outside of ancestor subtree
					return new LinkedList<Element>();
				}
			}
			Collections.reverse(elems);

			return elems;
		}

		/**
		 *
		 * @param n
		 * @return
		 */
		public static List<String> getTagsOnPathFromRoot(Node n)
		{
			List<String> names = new LinkedList<String>();
			for (Element e : getElementsOnPathFromRoot(n)) {
				names.add(e.getTagName());
			}
			return names;
		}

	}

	public static class Text
	{

		public static Element addStringElement(Element parent,
				String name,
				String value)
		{
			if (parent==null)
				throw new NullPointerException("[DomHelper:addStringElement] The parent "+
				"element cannot be null");
			if (name==null)
				throw new NullPointerException("[DomHelper:addStringElement] The name of an "+
				"element cannot be null");

			Document doc = parent.getOwnerDocument();
			Element e = doc.createElement(name);
			e.appendChild(doc.createTextNode(value));
			parent.appendChild(e);
			return e;
		}


		public static String getStringFromElement(Element root)
		{
			return getElementText_WithoutDescendantElements(root).trim();
		}

		public static String getStringFromElement(Node node)
		throws DOMValueException
		{
			if (node==null)
				throw new DOMValueException("Node is null. Cannot retrieve any value.");
			if (node instanceof Element)
				return getElementWithChildrenText((Element)node);
			else if (node instanceof Attr)
				return node.getNodeValue();
			else if (node instanceof Text ||
					node instanceof CDATASection ||
					node instanceof Comment)
				return node.getNodeValue();
			else if (node instanceof Document) {
				return getElementWithChildrenText(((Document)node).getDocumentElement());
			} else
				throw new DOMValueException("Node type not supported.");
		}

		public static String getFirstChildElementAsString(Element parent,
				String name)
		throws DOMElementException
		{
			Element e = Tree.Children.getFirstChildElementWithName(parent, name);
			return getStringFromElement(e);
		}

		public static String getFirstChildElementAsString(Element parent,
				String name,
				String defval)
		{
			Element e = Tree.Children.getFirstChildElementWithName_NullOk(parent, name);
			return
			e!=null?
					getStringFromElement(e):
						defval;
		}


		/**
		 * appends string into attribute, if attribute
		 * doesn't exist then it is created
		 */
		public static void appendToAttribute(Element elem,
				String att_name,
				String value,
				String separator)
		{
			String val = elem.getAttribute(att_name);
			if (val==null || val.equals("")) {
				val=value;
			}
			else {
				val = val+separator+value;
			}

			elem.setAttribute(att_name, val);
		}


		public static String getElementText_WithoutDescendantElements(Element elem)
		{
			if (elem==null)
				throw new NullPointerException("[DomHelper:getElementText_WithoutDescendatElements] Input "+
				"parameter elem is null.");
			StringBuffer b = new StringBuffer();
			NodeList nl = elem.getChildNodes();
			for (int i=0; i<nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n.getNodeType() == Node.TEXT_NODE ||
						n.getNodeType() == Node.CDATA_SECTION_NODE)
				{
					String s = n.getNodeValue();
					if (s!=null) {
						b.append(s);
					}
				}
			}
			return b.toString();
		}

		public static String getNodeText_WithoutDescendatElements(Node n) {
			if (n instanceof Element) {
				Element e = (Element) n;
				return getElementText_WithoutDescendantElements(e);
			}
			return n.getNodeValue();
		}

		public static String getElementWithChildrenText(Element elem)
		{
			if (elem==null)
				throw new NullPointerException("[DomHelper:getElementWithChildrenText] Input "+
				"parameter elem is null.");
			StringBuffer b = new StringBuffer();
			NodeList nl = elem.getChildNodes();
			for (int i=0; i<nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n.getNodeType() == Node.TEXT_NODE ||
						n.getNodeType() == Node.CDATA_SECTION_NODE)
				{
					String s = n.getNodeValue();
					if (s!=null) {
						b.append(s);
					}
				} else if (n.getNodeType() == Node.ELEMENT_NODE) {
					b.append(getElementWithChildrenText((Element)n));
				}
			}
			return b.toString();
		}

		public static void removeElementText(Element elem)
		{
			if (elem==null)
				throw new NullPointerException("[DomHelper:removeElementText] Input "+
				"parameter elem is null.");
			NodeList nl = elem.getChildNodes();
			for (int i=0; i<nl.getLength(); i++)
			{
				Node n = nl.item( i );
				if (n.getNodeType() == Node.TEXT_NODE ||
						n.getNodeType() == Node.CDATA_SECTION_NODE)
					elem.removeChild(n);
			}
		}

		/**
		 * Obtain the document title.
		 *
		 * @param doc
		 * @return
		 */
		public static String getDocumentTitle(Document doc) {
			String title = "";
			List<Element> titleElems = Tree.Descendant.getFirstNamedDescendantsAndSelfElements(
					doc.getDocumentElement(), "title");
			if (titleElems!=null && titleElems.size()>0) {
				Node titleNode = titleElems.get(0);
				title = titleNode.getTextContent();
			}
			return title;
		}

	}

	public static class Values {

		public static Element addIntElement(Element parent,
				String name,
				int value)
		{
			if (parent==null)
				throw new NullPointerException("[DomHelper:addIntElement] The parent "+
				"element cannot be null");
			if (name==null)
				throw new NullPointerException("[DomHelper:addIntElement] The name of an "+
				"element cannot be null");

			Document doc = parent.getOwnerDocument();
			/** @todo what should be done with the domexception */
			Element e = doc.createElement(name);
			e.appendChild(doc.createTextNode(String.valueOf(value)));
			parent.appendChild(e);
			return e;
		}

		public static Element addDoubleElement(Element parent,
				String name,
				double d)
		{
			if (parent==null || name==null)
				throw new NullPointerException("input argument is null");

			Document doc = parent.getOwnerDocument();
			Element e = doc.createElement(name);
			String s = String.format("%.9f", d);
			Node t = doc.createTextNode(s);
			e.appendChild(t);
			parent.appendChild(e);
			return e;
		}

		public static Element addLongElement(Element parent,
				String name,
				long l)
		{
			if (parent==null || name==null)
				throw new NullPointerException("input argument is null");

			Document doc = parent.getOwnerDocument();
			Element e = doc.createElement(name);
			String s = String.valueOf(l);
			Node t = doc.createTextNode(s);
			e.appendChild(t);
			parent.appendChild(e);
			return e;
		}

		public static Element addBooleanElement(Element parent,
				String name,
				boolean value)
		{
			if (parent==null)
				throw new NullPointerException("[DomHelper:addBooleanElement] The parent "+
				"element cannot be null");
			if (name==null)
				throw new NullPointerException("[DomHelper:addBooleanElement] The name of an "+
				"element cannot be null");

			Document doc = parent.getOwnerDocument();
			Element e = doc.createElement(name);
			e.appendChild(doc.createTextNode(String.valueOf(value)));
			parent.appendChild(e);
			return e;
		}


		public static int getIntFromElement(Element root)
		throws DOMValueException
		{
			try {
				return
				Integer.parseInt(Text.getElementText_WithoutDescendantElements(root).trim());
			}
			catch (Throwable e) {
				//parse error
				throw new DOMValueException(e);
			}
		}

		public static int getIntFromAttribute(Element root,
				String attribute)
		throws DOMValueException
		{
			if (root.hasAttribute(attribute)) {
				return
				Integer.parseInt(root.getAttribute(attribute).trim());
			}
			throw new DOMValueException("missing attribute: "+
					attribute);
		}

		public static long getLongFromElement(Element root)
		throws DOMValueException
		{
			try {
				return
				Long.parseLong(Text.getElementText_WithoutDescendantElements(root).trim());
			}
			catch (Throwable e) {
				//parse error
				throw new DOMValueException(e);
			}
		}

		public static double getDoubleFromElement(Element root)
		throws DOMValueException
		{
			try {
				return
				Double.parseDouble(Text.getElementText_WithoutDescendantElements(root).trim());
			}
			catch (Throwable e) {
				//parse error
				throw new DOMValueException(e);
			}
		}

		public static double getDoubleFromAttribute(Element root,
				String attribute)
		throws DOMValueException
		{
			if (root.hasAttribute(attribute)) {
				return
				Double.parseDouble(root.getAttribute(attribute).trim());
			}
			throw new DOMValueException("missing attribute: "+
					attribute);
		}

		public static boolean getBooleanFromElement(Element root)
		throws DOMValueException
		{
			return
			"true".equalsIgnoreCase(Text.getElementText_WithoutDescendantElements(root).trim());
		}

		public static boolean getBooleanFromAttribute(Element elem,
				String attribute)
		{
			return
			(elem.hasAttribute(attribute) &&
					elem.getAttribute(attribute).toLowerCase().equals("true"));
		}

		public static boolean getFirstChildElementAsBoolean(Element parent,
				String name)
		throws
		DOMElementException,
		DOMValueException
		{
			Element e = Tree.Children.getFirstChildElementWithName(parent, name);
			return getBooleanFromElement(e);
		}

		public static boolean getFirstChildElementAsBoolean(Element parent,
				String name,
				boolean defval)
		throws DOMValueException
		{
			Element e = Tree.Children.getFirstChildElementWithName_NullOk(parent, name);
			return
			e!=null?
					getBooleanFromElement(e):
						defval;
		}

		public static double getFirstChildElementAsDouble(Element parent,
				String name)
		throws
		DOMElementException,
		DOMValueException
		{
			Element e = Tree.Children.getFirstChildElementWithName(parent, name);
			return getDoubleFromElement(e);
		}

		public static double getFirstChildElementAsDouble(Element parent,
				String name,
				double defval)
		throws DOMValueException
		{
			Element e = Tree.Children.getFirstChildElementWithName_NullOk(parent, name);
			return
			e!=null?
					getDoubleFromElement(e):
						defval;
		}

		public static int getFirstChildElementAsInt(Element parent,
				String name)
		throws
		DOMElementException,
		DOMValueException
		{
			Element e = Tree.Children.getFirstChildElementWithName(parent, name);
			return getIntFromElement(e);
		}

		public static int getFirstChildElementAsInt(Element parent,
				String name,
				int defval)
		throws DOMValueException
		{
			Element e = Tree.Children.getFirstChildElementWithName_NullOk(parent, name);
			return
			e!=null?
					getIntFromElement(e):
						defval;
		}

		public static long getFirstChildElementAsLong(Element parent,
				String name)
		throws
		DOMElementException,
		DOMValueException
		{
			Element e = Tree.Children.getFirstChildElementWithName(parent, name);
			return getLongFromElement(e);
		}

		public static long getFirstChildElementAsLong(Element parent,
				String name,
				long defval)
		throws DOMValueException
		{
			Element e = Tree.Children.getFirstChildElementWithName_NullOk(parent, name);
			return
			e!=null?
					getLongFromElement(e):
						defval;
		}

	}

	public static class Debug {

		public static void printNode(Node n)
		{
			printNode("", n);
		}

		public static void printNode(String label, Node n)  {
			try {
				ErrorDump.debug(Debug.class, label + ": ");
				if (n==null)
					ErrorDump.debug(Debug.class, "Node is null");
				else if (n.getNodeType() == Node.COMMENT_NODE)
					ErrorDump.debug(Debug.class, "[comment]");
				else if (n.getNodeType() == Node.TEXT_NODE)
					ErrorDump.debug(Debug.class, "[#text]"+n.getNodeValue());
				else {
					String nodeText = n.getTextContent().trim();
					String stringToDisplay = nodeText.length() > 30 ? nodeText.trim().substring(0,30) : nodeText;
					ErrorDump.debug(Debug.class, "<" + n.getNodeName() + ">" + stringToDisplay.trim() + "..." + "</" + n.getNodeName() + ">");
				}
			}
			catch(Exception e) {
				ErrorDump.error(Debug.class, e);
			}
		}

		public static void printPath(List<Node> path)
		{
			int numTabs = 0;
			for (Node node : path)
			{
				for (int i = 0; i < numTabs; i++)
					ErrorDump.debug(Debug.class, "\t");
				printNode(node);
				numTabs++;
			}
		}

	}

	public static class Tree {



		/**
		 * Returns the size of the input tree. Size means number of nodes.
		 * @param parent
		 * @return int
		 */
		public static int treeSize (Element parent) {
			int size = 0;
			if (parent==null)// || matchDict(parent)==2)
				return size;

			List<Element> elist = Children.getChildElements(parent);
			for (Element el : elist) {
				size  += treeSize(el);
			}

			return size+1;
		}

		/**
		 *
		 * Calculate the depth of a DOM tree allowing for ALL
		 * tag elements.
		 *
		 * @param doc
		 * @return
		 */
		public static int treeDepth (Document doc) {
			Element root = doc.getDocumentElement();
			DOMHelper.Tree domTree = new DOMHelper.Tree();
			return domTree.treeDepth(root, 0);
		}

		protected int treeDepth (Element root, int startDepth) {
			int max = -1;
			List<Element> children = DOMHelper.Tree.Children.getChildElements(root);
			if (children.size()==0)
				return startDepth+1;

			for (Element child : children) {
				int tmp = startDepth + treeDepth(child, startDepth+1);
				if (tmp>max) max = tmp;
			}
			return max+1;
		}

//		/**
//		 *
//		 * Calculate the depth of a DOM tree allowing for ALL
//		 * tag elements.
//		 *
//		 * @param doc
//		 * @return
//		 */
//		public static int treeDepthProper (Document doc) {
//			Element root = doc.getDocumentElement();
//			DOMHelper.Tree domTree = new DOMHelper.Tree();
//			return domTree.treeDepthProper(root, 0);
//		}

//		protected int treeDepthProper (Element root, int startDepth) {
//			int max = -1;
//			List<Element> children = DOMHelper.Tree.Children.getProperTagChildElements(root);
//			if (children.size()==0)
//				return startDepth+1;
//
//			for (Element child : children) {
//				int tmp = startDepth + treeDepthProper(child, startDepth+1);
//				if (tmp>max) max = tmp;
//			}
//			return max+1;
//		}

		public static class Children {

			public static Element getChildElementAt_NullOk(Element parent, int child_idx)
			{
				if (parent==null)
					throw new NullPointerException("[DomHelper:getChildElement] The parent "+
					"element cannot be null");

				NodeList nl = parent.getChildNodes();
				int num_of_elems = 0;
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						if (num_of_elems==child_idx) {
							return (Element) n;
						}
						num_of_elems++;
					}
				}

				return null;
			}

			public static Element getChildAt_NullOk(Node parent, int child_idx)
			{
				if (parent==null)
					throw new NullPointerException("[DomHelper:getChildElement] The parent "+
					"element cannot be null");

				NodeList nl = parent.getChildNodes();
				int num_of_elems = 0;
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						if (num_of_elems==child_idx) {
							return (Element) n;
						}
						num_of_elems++;
					}
				}

				return null;
			}

			public static Node getChildAt(Node parent, int child_idx)
			throws DOMElementException
			{
				Element e = getChildAt_NullOk(parent, child_idx);
				if (e==null)
					throw new DOMElementException("invalid child index: "+ child_idx+
							" in element: "+ parent.toString());
				return e;
			}

			public static Element getChildElementAt(Element parent, int child_idx)
			throws DOMElementException
			{
				Element e = getChildElementAt_NullOk(parent, child_idx);
				if (e==null)
					throw new DOMElementException("invalid child index: "+ child_idx+
							" in element: "+ parent.getTagName());
				return e;
			}

			public static int getChildElementIndex(Element parent,
					Element child)
			{
				if (parent == null)
					throw new NullPointerException("[DomHelper:getChildElementIndex] The parent " +
					"element cannot be null");
				NodeList nl = parent.getChildNodes();
				int idx = -1;
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						idx++;
						if (n.isSameNode(child)) {
							return idx;
						}
					}
				}
				return -1;
			}


			/**
			 * returns number of children
			 */
			public static int getChildElementCount(Element parent)
			{
				if (parent==null)
					throw new NullPointerException("[DomHelper:getChildElementCount] "+
					"The parent element cannot be null");
				NodeList nl = parent.getChildNodes();
				int num_of_elems = 0;
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						num_of_elems++;
					}
				}
				return num_of_elems;
			}

			public static void replaceChildWith (Node before, Node after)
			throws DOMElementException {
				Node parent = before.getParentNode();
				int index = getChildNodeIndex(parent, before);
				Node n = getChildAt(parent, index+1);

				if (parent.getChildNodes().getLength()<index) {
					parent.appendChild(n);
				} else {
					parent.insertBefore(n, after);
				}
			}

			public static int getChildNodeIndex(Node parent,
					Node child)
			{
				if (parent == null)
					throw new NullPointerException("[DomHelper:getChildElementIndex] The parent " +
					"element cannot be null");
				NodeList nl = parent.getChildNodes();
				int num_of_elems = -1;
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					num_of_elems++;
					if (child==n) {
						return num_of_elems;
					}

				}
				return num_of_elems;
			}

			/**
			 * Returns named index of the child element in
			 * the parent.
			 * (Number of equally named left sibling).
			 */
			public static int getNamedChildElementIndex (Element e)
			{
				if (e==null) throw new IllegalArgumentException();
				Element parent = Parent.getParentElement(e);
				if (parent==null) return 0;
				String chname = e.getNodeName();

				NodeList nl = parent.getChildNodes();
				int idx = 0;
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						if (e==n) {
							return idx;
						}
						else if (chname.equals(n.getNodeName())) {
							idx++;
						}
					}
				}
				return idx;
			}

			/**
			 * Returns all child elements of parent where the tag name
			 * returns name. 'name' argument should be upper-case, e.g.
			 * "TD".
			 */
			public static List<Element> getNamedChildElements(Element parent, String name)
			{
				List<Element> result = new LinkedList<Element>();
				if (name==null) throw new NullPointerException();
				if (parent==null) return new LinkedList<Element>();

				NodeList nl = parent.getChildNodes();
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						if (name.equalsIgnoreCase(((Element)n).getTagName())) {
							result.add((Element)n);
						}
					}
				}
				return result;
			}

			/**
			 * returns number of children with the given name
			 */
			public static int getNamedChildElementCount(Element parent,
					String name)
			{
				if (parent==null)
					throw new NullPointerException("[DomHelper:getChildElementCount] "+
					"The parent element cannot be null");

				NodeList nl = parent.getChildNodes();
				int num_of_elems = 0;
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						Element e = (Element) n;
						if (name.equals(e.getTagName())) {
							num_of_elems++;
						}
					}
				}
				return num_of_elems;
			}

			public static List<Element> getChildElements(Element parent)
			{
				if (parent == null)
					throw new NullPointerException("[DomHelper:getChildElements] The parent "+
					"element cannot be null");
				List<Element> children = new LinkedList<Element>();
				NodeList nl = parent.getChildNodes();
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						children.add((Element) n);
					}
				}
				return children;
			}



			public static List<Element> getChildElementsNS(Element parent, String namespace, String anElementName)
			{
				if (parent == null)
					throw new NullPointerException("[DomHelper:getChildElements] The parent "+
					"element cannot be null");

				List<Element> children = new LinkedList<Element>();
				NodeList nl = parent.getElementsByTagNameNS( namespace, anElementName );
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						children.add((Element) n);
					}
				}
				return children;
			}

			/**
			 * returns list of child element with the given name
			 * inside of the given parent element
			 */

			public static List<Element> getChildElements(Element parent, String anElementName)
			{
				if (parent == null)
					throw new NullPointerException("[DomHelper:getChildElements] The parent "+
					"element cannot be null");
				List<Element> children = new LinkedList<Element>();
				NodeList nl = parent.getChildNodes();
				for (int i=0; i<nl.getLength(); i++)
				{
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE &&
							n.getNodeName().equals(anElementName))
					{
						children.add( (Element) n );
					}
				}
				return children;
			}

			public static Element getFirstChildElement(Element parent)
			throws DOMElementException
			{
				if (parent==null)
					throw new NullPointerException("[DomHelper:getFirstChildElement] The parent "+
					"element cannot be null");

				NodeList nl = parent.getChildNodes();
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						Element child = (Element) n;
						return child;
					}
				}

				//no child node
				throw new DOMElementException("missing child element: "+
						parent.getTagName());
			}


			public static Element getFirstChildElementWithName_NullOk(Element parent,
					String name)
			{
				if (parent==null)
					throw new NullPointerException("[DomHelper:getFirstChildElementWithName_NullOk] The parent "+
					"element cannot be null");

				NodeList nl = parent.getChildNodes();
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						Element child = (Element) n;
						if (child.getTagName().equals(name)) {
							return child;
						}
					}
				}

				return null;
			}

			public static Element getFirstChildElementWithName(Element parent,
					String name)
			throws DOMElementException
			{
				if (parent==null)
					throw new NullPointerException("[DomHelper:getFirstChildElementWithName] The parent "+
					"element cannot be null");

				Element e = getFirstChildElementWithName_NullOk(parent, name);
				if (e==null) {
					//no child node
					throw new DOMElementException("missing child element: "+
							name+
							" in element: "+
							parent.getTagName());
				}
				return e;
			}


			/**
			 * Gets the position of a node in the parent with the specific type (e.g. Node.text,
			 * @param node node for which the position should be retrieved.
			 * @param type Node.?? e.g. Node.TEXT_NODE
			 * @return position of this or null
			 */
			private static int getChildNodeTypeIndex(Node node, int type)
			{
				if (node==null)
					throw new NullPointerException("[DomHelper:getChildNodeIndex] Node cannot be null.");
				NodeList nl = ((Element)node.getParentNode()).getChildNodes();
				int num_of_elems = 0;
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeType()==type) {
						num_of_elems++;
						if (node==n) {
							return num_of_elems;
						}
					}
				}
				return -1;
			}

			public static Element removeChild (Element parent, Element child) {
				if (child==null)
					throw new NullPointerException("[DomHelper:removeChild] Child cannot be null.");
				if (parent==null)
					throw new NullPointerException("[DomHelper:removeChild] Parent cannot be null.");

				Element result = null;
				NodeList nl = parent.getChildNodes();
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.isSameNode(child))
						result = (Element)parent.removeChild(n);
				}
				return result;
			}

		}

		public static class Ancestor {

			public static Element getClosestAncestorElement(Node n) {
				while (n!=null) {
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						return (Element) n;
					}
					n = n.getParentNode();
				}

				//not found
				return null;
			}

			/**
			 * Returns n-th element ancestor.
			 *
			 * 0th ancestor of an element 'e' is 'e' itself;
			 * 1th ancestor of an element 'e' is parent element of 'e',
			 * etc.
			 */
			public static Element getNthAncestorOrSelfElement(Node child, int nth)
			{
				int ith = 0;
				Node n = child;
				while (n!=null) {
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						if (ith==nth)
							return (Element) n;
						ith++;
					}
					n = n.getParentNode();
				}

				//not found
				return null;
			}

			public static Element
			getFirstAncestorOrSelfElementWithName(Element child,
					List<String> names)
			{
				Node n = child;
				while (n!=null) {
					if (n.getNodeType()==Node.ELEMENT_NODE &&
							names.contains(n.getNodeName()))
					{
						return (Element) n;
					}
					n = n.getParentNode();
				}

				//not found
				return null;
			}

			/**
			 * Check if arg2 is a descendant of arg1 or the same node
			 * @param ancestor
			 * @param descendant
			 * @return
			 */
			public static boolean isAncestorOrSelfOf(Node ancestor, Node descendant)
			{
				if (ancestor==null) return false;
				Node n = descendant;
				while (n!=null) {
					if (ancestor.isSameNode(n)) return true;
					n = n.getParentNode();
				}
				return false;
			}


			public static boolean haveCommonAncestorElement(Node n1,Node n2)
			{
				return getClosestCommonAncestorElement(n1, n2)!=null;
			}

			/*
			 * Find lowest common ancestor
			 */
			public static Element getClosestCommonAncestorElement(Node sn1, Node sn2)
			{
				Node closure = getClosestCommonAncestor(sn1, sn2);
				return getClosestAncestorElement(closure);
			}

			public static Node getClosestCommonAncestor(Node sn1, Node sn2)
			{
				if (sn1.isSameNode(sn2)) return sn1;

				Stack<Node> l1 = new Stack<Node>();
				Node n = sn1;
				while (n!=null) {
					l1.push(n);
					n = n.getParentNode();
				}

				Stack<Node> l2 = new Stack<Node>();
				n = sn2;
				while (n!=null) {
					l2.push(n);
					n = n.getParentNode();
				}

				//find the common ancestor node
				Node closure = null;
				while (!l1.isEmpty() && !l2.isEmpty()) {
					Node n1 = l1.pop();
					Node n2 = l2.pop();

					if (n1.isSameNode(n2)) {
						closure = n1;
					}
					else {
						break;
					}
				}

				return closure;
			}

			/**
			 *
			 * @param nodes
			 * @return
			 */
			public static Element getClosestCommonAncestor (List<Node> nodes)
			{
				if (nodes.size()==0) return null;
				Node ancestor = nodes.get(0);
				for (int i=1; i<nodes.size(); i++) {
					ancestor = getClosestCommonAncestorElement(ancestor, nodes.get(i));
				}
				return (Element) ancestor;
			}

			/**
			 *
			 * @param sns
			 * @return
			 */
			public static List<Node> getClosestCommonAncestorBranchChildren(
					Collection<? extends Node> sns)
			{
				if (sns.size()<2) throw new RuntimeException(); //not handled case
				if (allSame(sns)) return null;

				List<Stack<Node>> ls = new LinkedList<Stack<Node>>();
				for (Node sn : sns) {
					Stack<Node> l = new Stack<Node>();
					Node n = sn;
					while (n!=null) {
						l.push(n);
						n = n.getParentNode();
					}
					ls.add(l);
				}

				//find the common ancestor node
				List<Node> closure = null;
				while (!allEmpty(ls)) {
					List<Node> ns = new LinkedList<Node>();
					for (Stack<Node> s : ls) ns.add(s.pop());

					if (allSame(ns)) {
						//closure = n1;
					}
					else {
						closure = ns;
						break;
					}
				}

				return closure;
			}

			/**
			 *
			 * @param ns
			 * @return
			 */
			private static boolean allSame(Collection<? extends Node> ns) {
				if (ns.size()<2) return true;
				Iterator<? extends Node> it = ns.iterator();
				Node prev = it.next();
				while (it.hasNext()) {
					Node n = it.next();
					if (!prev.isSameNode(n)) return false;
					prev = n;
				}
				return true;
			}

			/**
			 *
			 * @param lv
			 * @return
			 */
			private static boolean allEmpty(List<? extends Vector<?>> lv) {
				for (Vector<?> v : lv) {
					if (!v.isEmpty()) return false;
				}
				return true;
			}
		}

		public static class Descendant
		{

			public static int getDepth (Element e) {
				int result = 1;
				while (e.getParentNode()!=null && e.getParentNode().getNodeType()==Node.ELEMENT_NODE) {
					e = (Element) e.getParentNode();
					result++;
				}
				return result;
			}

			public static List<Node> getLeafTextNodes(Element root) {

				if (root==null) {
					throw new NullPointerException("The element cannot be null");
				}

				List<Node> result = new LinkedList<Node>();
				LinkedList<Node> queue = new LinkedList<Node>();
				queue.add(root);
				while (!queue.isEmpty()) {
					Node n = queue.removeFirst();

					if (n.getNodeType()==Node.ELEMENT_NODE)
					{
						Element e = (Element) n;
						if (Children.getChildElementCount(e)>0)
						{
							queue.addAll(Children.getChildElements(e));
						}
					}
					else if (n.getNodeType()==Node.TEXT_NODE &&
							n.getTextContent()!=null && n.getTextContent().length()>0)
					{
						result.add(n);
					}
				}
				return result;
			}

			/**
			 *
			 * @param root
			 * @return
			 */
			public static List<Node> getAllLeafs(Element root)
			{
				if (root==null) {
					throw new NullPointerException("The element cannot be null");
				}

				List<Node> result = new LinkedList<Node>();
				LinkedList<Node> queue = new LinkedList<Node>();
				queue.add(root);
				while (!queue.isEmpty())
				{
					Node n = queue.removeFirst();

					if (n.getNodeType()==Node.ELEMENT_NODE)
					{
						Element e = (Element) n;
						if (Children.getChildElementCount(e)==0)
						{
							result.add(n); //is leaf
						} else {
							queue.addAll(Children.getChildElements(e));
						}
					} else {
						result.add(n);
					}
				}
				return result;
			}

//			public static List<Node> getLeafTextNodes (Element root) {
//				List<Node> result = new LinkedList<Node> ();
//				NodeList nl = root.getChildNodes();
//				for (int i=0; i<nl.getLength(); i++) {
//					Node n = nl.item(i);
//					if (n.getNodeType()==Node.TEXT_NODE &&
//							n.getTextContent()!=null && n.getTextContent().length()>0)
//						result.add(n);
//				}
//				return result;
//			}

			public static List<Element> getDescendantsAndSelfElements(Element root) {
				List<Element> result = new LinkedList<Element>();

				LinkedList<Element> queue = new LinkedList<Element>();
				queue.add(root);
				while (!queue.isEmpty()) {
					Element e = queue.removeFirst();
					result.add(e);
					queue.addAll(Children.getChildElements(e));
				}

				return result;
			}

			public static List<Element> getFirstNamedDescendantsAndSelfElements(
					Element root, String name) {
				List<Element> result = new LinkedList<Element>();

				LinkedList<Element> queue = new LinkedList<Element>();
				queue.add(root);
				while (!queue.isEmpty()) {
					Element e = queue.removeFirst();
					if (e.getTagName().equalsIgnoreCase(name.toUpperCase())) {
						result.add(e);
						break;
					}
					queue.addAll(Children.getChildElements(e));
				}

				return result;
			}

			public static List<Element> getNamedDescendantsAndSelfElements (
					Element root, String name)
			{
				List<Element> result = new LinkedList<Element>();

				LinkedList<Node> queue = new LinkedList<Node>();
				queue.add(root);
				while (!queue.isEmpty()) {
					Node n = queue.removeFirst();
					String tagName = null;
					if (n instanceof Element) {
						Element e = (Element) n;
						tagName = e.getTagName();
					} else {
						tagName = n.getNodeName();
					}
					if (tagName.equalsIgnoreCase(name.toUpperCase()) &&
							n.getNodeType()==Node.ELEMENT_NODE)
					{
						result.add((Element) n);
					}
					NodeList children = n.getChildNodes();
					for (int i=0; i<children.getLength(); i++) {
						Node nn = children.item(i);
//						String tn = nn.getNodeName();
						queue.add(nn);
					}
				}

				return result;
			}

			public static List<Element> getDescendantsAndSelfElements(Element root, int limit) {

				if (root==null)
					throw new NullPointerException("The element cannot be null");

				List<Element> result = new LinkedList<Element>();

				LinkedList<Element> queue = new LinkedList<Element>();
				queue.add(root);
				while (!queue.isEmpty() && limit>0) {
					Element e = queue.removeFirst();
					result.add(e);
					queue.addAll(Children.getChildElements(e));
					limit--;
				}

				return result;
			}

			public static List<Element> getDescendantsNotSelfElements(Element root) {
				List<Element> result = getDescendantsAndSelfElements(root);
				result.remove(0);
				return result;
			}

			public static List<Node> getDescendantsAndSelfNodes(Node root, int limit) {

				if (root==null)
					throw new NullPointerException("The element cannot be null");

				List<Node> result = new LinkedList<Node>();

				LinkedList<Node> queue = new LinkedList<Node>();
				queue.add(root);
				while (!queue.isEmpty() && limit>0) {
					Node n = queue.removeFirst();
					result.add(n);
					NodeList children = n.getChildNodes();
					for (int i=0; i<children.getLength(); i++) {
						queue.add(children.item(i));
					}
					limit--;
				}

				return result;
			}

			public static List<Element> getLeafDescendants(Element root) {

				if (root==null) {
					throw new NullPointerException("The element cannot be null");
				}

				List<Element> result = new LinkedList<Element>();
				LinkedList<Element> queue = new LinkedList<Element>();
				queue.add(root);
				while (!queue.isEmpty()) {
					Object e = queue.removeFirst();
					Element im = (Element) e;
					if (Children.getChildElementCount(im)==0) {
						result.add(im); //is leaf
					} else {
						queue.addAll(Children.getChildElements(im));
					}
				}
				return result;
			}

//			/**
//			 * Node validity is defined by CoreProperties.java
//			 *
//			 * @param root
//			 * @return
//			 */
//			public static List<Node> getValidLeafNodes(Node root) {
//
//				if (root==null)
//					throw new NullPointerException("The element cannot be null");
//
//				List<Node> result = new LinkedList<Node>();
//				LinkedList<Node> queue = new LinkedList<Node>();
//				queue.add(root);
//				while (!queue.isEmpty()) {
//					Node n = queue.removeFirst();
//					List<Node> children = Children.getProperTagChildNodes(n);
//					if (children.size()==0) {
//						result.add(n); //is leaf
//					} else {
//						queue.addAll(children);
//					}
//				}
//				return result;
//			}

		}

		public static class Sibling {


			public static Element getNextSiblingElement(Node node)
			{
				if (node==null)
					throw new NullPointerException("The element cannot be null");

				Node n = node.getNextSibling();
				while (n!=null) {
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						Element e = (Element) n;
						return e;
					}
					n = n.getNextSibling();
				}

				return null;
			}

			public static Element getPreviousSiblingElement(Node node)
			{
				if (node==null)
					throw new NullPointerException("The element cannot be null");

				Node n = node.getPreviousSibling();
				while (n!=null) {
					if (n.getNodeType()==Node.ELEMENT_NODE) {
						Element e = (Element) n;
						return e;
					}
					n = n.getPreviousSibling();
				}

				return null;
			}

			public static boolean areSiblings(Node n1, Node n2)
			{
				Node p1 = n1.getParentNode();
				Node p2 = n2.getParentNode();
				if (p1!=null && p2!=null) {
					return p1.isSameNode(p2);
				}
				return false;
			}

			public static int getSiblingCount (Element elem) {
				int count = 0;
				Element initial = elem;
				while (getPreviousSiblingElement(elem)!=null) {
					elem = getPreviousSiblingElement(elem);
					count++;
				}
				elem = initial;
				while (getNextSiblingElement(elem)!=null) {
					elem = getNextSiblingElement(elem);
					count++;
				}
				return count;
			}

			public static int getSiblingNodeCount (Node node) {
				int count = 0;
				Node initial = node;
				while (node.getPreviousSibling()!=null) {
					node = node.getPreviousSibling();
					count++;
				}
				node = initial;
				while (node.getNextSibling()!=null) {
					node = node.getNextSibling();
					count++;
				}
				return count;
			}

			public static int getNamedSiblingCount (Element elem) {
				String name = elem.getTagName();
				int count = 0;
				Element initial = elem;
				while (getPreviousSiblingElement(elem)!=null) {
					elem = getPreviousSiblingElement(elem);
					if (elem.getTagName().equals(name))
						count++;
				}
				elem = initial;
				while (getNextSiblingElement(elem)!=null) {
					elem = getNextSiblingElement(elem);
					if (elem.getTagName().equals(name))
						count++;
				}
				return count;
			}

			/**
			 * Returns named index of the child element in
			 * the parent.
			 * (Number of equally named left sibling).
			 */
			public static int
			getRelativeNamedSiblingElementDistance(Element from,
					Element to)
			{
				if (from==null || to==null) throw new NullPointerException();
				if (from==to) return 0;

				String toname = to.getNodeName();
				String fromname = from.getNodeName();
				int idx = toname.equals(fromname) ? 0 : 1;
				Node x = from;
				while (x!=null) {
					if (x.getNodeType()==Node.ELEMENT_NODE) {
						if (x==to)
							return idx;
						if (x.getNodeName().equalsIgnoreCase(toname))
							idx--;
					}

					x = x.getPreviousSibling();
				}

				idx = toname.equals(fromname) ? 0 : 1;
				x = from;
				while (x!=null) {
					if (x.getNodeType()==Node.ELEMENT_NODE) {
						if (x==to)
							return idx;
						if (x.getNodeName().equalsIgnoreCase(toname))
							idx++;
					}

					x = x.getNextSibling();
				}

				return -1;
			}
		}

		public static class Parent {
			/**
			 * returns parent element if exists
			 */
			public static Element getParentElement(Node n)
			{
				Node p = n.getParentNode();
				if (p!=null && p.getNodeType()==Node.ELEMENT_NODE) {
					return (Element) p;
				}
				return null;
			}

			/**
			 *
			 * @param child
			 * @return
			 */
			public static int getElementIndexInParent(Element child) {
				Element parent = getParentElement(child);
				if (parent==null) {
					//return 0, if no parent exists,
					//so that /tag[getIndexInParent(...)+1]
					//matches for document root elements
					return 0;
				}
				return Children.getChildElementIndex(parent, child);
			}

			/**
			 *
			 * Move up the DOM tree from a node, creating an ordered
			 * bottom-up list of parent nodes. Stop after k parents.
			 *
			 * @param e
			 * @return
			 */
			public static List<Node> getOrderedParentPathElements (Node e, int k) {
				List<Node> result = new LinkedList<Node>();

				int i = 0;
				while (i<k && getParentElement(e)!=null) {
					e = getParentElement(e);
					result.add(e);
				}
				return result;
			}
		}
	}

	public static class Assert {

		public static void assertAllFromSameDoc(Collection<Node> nodes)
		{
			if (nodes.isEmpty()) return;

			Node prev = null;
			Iterator<Node> it = nodes.iterator();
			while (it.hasNext()) {
				Node n = (Node) it.next();
				if (prev!=null) {
					Document d1 = prev.getOwnerDocument();
					Document d2 = n.getOwnerDocument();
					assert d1==d2 || d1.isSameNode(d2);
				}
				prev =  n;
			}
		}

	}

	//	public static NodeWrapper wrapNode(Node node)
	//	{
	//	DocumentWrapper dw =
	//	new DocumentWrapper(node.getOwnerDocument(),
	//	node.getBaseURI(),
	//	new Configuration());
	//	return dw.wrap(node);
	//	}

	/*
    public static NodeIterator getElementIterator(Document doc)
    {
        if (doc==null)
         throw new NullPointerException("[DomHelper:getElementIterator] The input "+
                                        "document cannot be null" );
        return getElementIterator( doc.getDocumentElement() );
    }
	 */

	/*
    public static NodeIterator getElementIterator(Element elem)
    {
        if (elem == null)
         throw new NullPointerException("[DomHelper:getElementIterator] The elem "+
                                        "element cannot be null");

        return
            new NodeIteratorImpl((DocumentImpl) elem.getOwnerDocument(),
                                 elem,
                              NodeFilter.SHOW_ELEMENT,
                              new NodeFilter() {
                                 public short acceptNode(Node n) {
                                    return NodeFilter.FILTER_ACCEPT;
                                 }
                              },
                              true );
     }
	 */

	//	this methods works only on mozilla dom, so disabled
	//	public static Node[] sortNodesInAscendingOrder(List<Node> input_nodes)
	//	{

	//	Node[] inputs =   new Node[input_nodes.size()];
	//	for (int i=0;i<input_nodes.size();i++) {
	//	inputs[i] = input_nodes.get(i);
	//	}


	//	//Node[] results = new Node[input_nodes.size()];

	//	for (int i=0;i<inputs.length;i++) {
	//	for (int j=0;j<inputs.length;j++) {
	//	if (inputs[j].compareDocumentPosition(inputs[i])==2) {
	//	// swap
	//	Node n = inputs[j];
	//	inputs[j] = inputs[i];
	//	inputs[i] = n;

	//	}
	//	else if (inputs[j].compareDocumentPosition(inputs[i])==10) {
	//	Node n = inputs[j];
	//	inputs[j] = inputs[i];
	//	inputs[i] = n;

	//	}
	//	}

	//	}


	//	return inputs;


	//	}



}
