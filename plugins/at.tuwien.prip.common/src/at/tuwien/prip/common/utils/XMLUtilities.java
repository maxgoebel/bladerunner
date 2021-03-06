/*
 Copyright 2000-2005 University Duisburg-Essen, Working group "Information Systems"

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 this file except in compliance with the License. You may obtain a copy of the
 License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software distributed
 under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 CONDITIONS OF ANY KIND, either express or implied. See the License for the
 specific language governing permissions and limitations under the License.
 */

// $Id: XMLUtilities.java,v 1.15 2005/03/19 17:13:19 nottelma Exp $
package at.tuwien.prip.common.utils;

import iiuf.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;

/**
 * A class providing static utility methods for XML.
 * <p>
 *
 * NOTE: This class currently reads XML documents only with the ISO-8859-1
 * encoding. This will change in the near future, after extended testing.
 *
 * @author Henrik Nottelmann
 * @since 2002-09-25
 * @version $Revision: 1.15 $, $Date: 2005/03/19 17:13:19 $
 */
public class XMLUtilities {

	/**
	 * The default encoding, set to ISO-8859-1.
	 */
	private static String defaultEncoding = "ISO-8859-1";

	/**
	 * Flag indicating whether the parser is aware of namespaces, set to true.
	 */
	private static boolean namespaceAware = true;

	/**
	 * Parses an XML file (default encoding) and returns a DOM document node.
	 *
	 * @param url
	 *                   URL of the XML file
	 * @return DOM representation
	 */
	public static Document parse(URL url) {
		return parse(url, defaultEncoding);
	}

	/**
	 * Parses an XML file and returns a DOM document node.
	 *
	 * @param url
	 *                   URL of the XML file
	 * @param encoding
	 *                   file encoding
	 * @return DOM representation
	 */
	public static Document parse(URL url, String encoding) {
		try {
			return parse(new InputStreamReader(url.openStream(), encoding));
		} catch (Exception ex) {
			Log.error(ex);
		}
		return null;
	}

	/**
	 * Parses an XML file (default encoding) and returns a DOM document node.
	 *
	 * @param filename
	 *                   name of the XML file
	 * @return DOM representation
	 */
	public static Document parse(String filename) {
		return parse(new File(filename), defaultEncoding);
	}

	/**
	 * Parses an XML file and returns a DOM document node.
	 *
	 * @param filename
	 *                   name of the XML file
	 * @param encoding
	 *                   file encoding
	 * @return DOM representation
	 */
	public static Document parse(String filename, String encoding) {
		return parse(new File(filename), encoding);
	}

	/**
	 * Parses an XML file (default encoding) and returns a DOM document node.
	 *
	 * @param file
	 *                   XML file
	 * @return DOM representation
	 */
	public static Document parse(File file) {
		return parse(file, defaultEncoding);
	}

	/**
	 * Parses an XML file and returns a DOM document node.
	 *
	 * @param file
	 *                   XML file
	 * @param encoding
	 *                   file encoding
	 * @return DOM representation
	 */
	public static Document parse(File file, String encoding) {
		try {
			return parse(file.toURI().toURL(), encoding);
		} catch (Exception ex) {
			Log.error(ex);
		}
		return null;
	}

	/**
	 * Parses an XML file and returns a DOM document node.
	 *
	 * @param reader
	 *                   reader for the XML file
	 * @return DOM representation
	 */
	public static Document parse(Reader reader) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(namespaceAware);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			BufferedReader in = new BufferedReader(reader);
			return builder.parse(new InputSource(in));
		} catch (Exception ex) {
			Log.error(ex);
		}
		return null;
	}

	/**
	 * Parses an XML string and returns a DOM document node.
	 *
	 * @param text
	 *                   XML string
	 * @return DOM representation
	 */
	public static Document parseText(String text) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			BufferedReader in = new BufferedReader(new StringReader(text));
			return builder.parse(new InputSource(in));
		} catch (Exception ex) {
			Log.error(ex);
		}
		return null;
	}

	/**
	 * Returns a string representation of the complete XML tree, properly
	 * formatted.
	 *
	 * @param element
	 *                   XML tree
	 * @return string representation of the specified XML tree
	 */
	public static String toString(Element element) {
		return toString(element.getOwnerDocument());
	}

	/**
	 * Returns a string representation of the complete XML tree, properly
	 * formatted.
	 *
	 * @param doc
	 *                   XML tree
	 * @return string representation of the specified XML tree
	 */
	public static String toString(Document doc) {
		try {
			Writer writer = new StringWriter();
			OutputFormat of = new OutputFormat(doc);
			of.setIndent(2);
			of.setOmitXMLDeclaration(true);
			XMLSerializer serializer = new XMLSerializer(writer, of);
			serializer.setOutputCharStream(writer);
			serializer.serialize(doc);
			return writer.toString();
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * Downloads a document given by the URL and converts it to XHTML.
	 *
	 * @param url
	 *                   URL of the document
	 * @return root element of the X(HT)ML file
	 */
	public static Element getXHTML(String url) {
		try {
			return getXHTML(new URL(url));
		} catch (Exception ex) {
			Log.error(ex);
		}
		return null;
	}

	/**
	 * Downloads a document given by the URLand converts it to XHTML.
	 *
	 * @param url
	 *                   URL of the document
	 * @return root element of the X(HT)ML file
	 */
	public static Element getXHTML(URL url) {
		Element e = null;
		try {
			URLConnection conn = url.openConnection();
			//conn.setConnectTimeout(30000);
			e = getXHTML(conn.getInputStream());
		} catch (Exception ex) {
			Log.error(ex);
		}
		return e;
	}

	/**
	 * Downloads an HTML file and converts it to XHTML.
	 *
	 * @param source
	 *                   HTML source stream
	 * @return root element of the X(HT)ML file
	 */
	public static Element getXHTML(InputStream source) {
		try {
			Tidy tidy = new Tidy();
			tidy.setMakeClean(true);
			tidy.setQuiet(true);
			tidy.setShowWarnings(false);
			Document doc = tidy.parseDOM(source, null);
			source.close();
			return doc.getDocumentElement();
		} catch (Exception ex) {
			Log.error(ex);
		}
		return null;
	}

	/**
	 * Replaces all unicode entities "&#...;" by a space.
	 *
	 * @param str
	 *                   string to convert
	 * @return converted string
	 */
	protected static String convert(String str) {
		if (str == null)
			return null;
		int i;
		int j = 0;
		StringBuffer returnStr = new StringBuffer();
		while ((i = str.indexOf("&#", j)) != -1) {
			returnStr.append(str.substring(j, i));
			returnStr.append(" ");
			j = str.indexOf(";", i) + 1;
		}
		returnStr.append(str.substring(j));
		return returnStr.toString();
	}

	/**
	 * Returns the value of the first node matching the specified XPATH
	 * expression.
	 *
	 * @param document
	 *                   XML document from which the search should start
	 * @param xpath
	 *                   XPATH expression
	 * @return value of the first matching node
	 */
	public static String extract(Document document, String xpath) {
		return extract(document.getDocumentElement(), xpath);
	}

	/**
	 * Returns the value of the first node matching the specified XPATH
	 * expression.
	 *
	 * @param element
	 *                   XML element from which the search should start
	 * @param xpath
	 *                   XPATH expression
	 * @return value of the first matching node
	 */
	public static String extract(Element element, String xpath) {
		try {
			NodeIterator iterator = XPathAPI.selectNodeIterator(element, xpath);
			return convert(iterator.nextNode().getNodeValue().trim());
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * Returns the value of the first node matching the specified XPATH
	 * expression.
	 *
	 * @param document
	 *                   XML document from which the search should start
	 * @param xpath
	 *                   XPATH expression
	 * @return value of the first matching node as a double
	 */
	public static double extractDouble(Document document, String xpath) {
		return extractDouble(document.getDocumentElement(), xpath);
	}

	/**
	 * Returns the value of the first node matching the specified XPATH
	 * expression.
	 *
	 * @param element
	 *                   XML element from which the search should start
	 * @param xpath
	 *                   XPATH expression
	 * @return value of the first matching node as a double
	 */
	public static double extractDouble(Element element, String xpath) {
		try {
			return Double.parseDouble(extract(element, xpath));
		} catch (Exception ex) {
		}
		return 1;
	}

	/**
	 * Returns the value of the first node matching the specified XPATH
	 * expression.
	 *
	 * @param document
	 *                   XML document from which the search should start
	 * @param xpath
	 *                   XPATH expression
	 * @return value of the first matching node as an int
	 */
	public static int extractInt(Document document, String xpath) {
		return extractInt(document.getDocumentElement(), xpath);
	}

	/**
	 * Returns the value of the first node matching the specified XPATH
	 * expression.
	 *
	 * @param element
	 *                   XML element from which the search should start
	 * @param xpath
	 *                   XPATH expression
	 * @return value of the first matching node as an int
	 */
	public static int extractInt(Element element, String xpath) {
		try {
			return Integer.parseInt(extract(element, xpath));
		} catch (Exception ex) {
		}
		return 1;
	}

	/**
	 * Copies the text subnodes into the specified string buffer and appends two
	 * empty lines.
	 *
	 * @param element
	 *                   XML element whose text subnodes have to be added
	 * @param buffer
	 *                   string buffer for the result
	 */
	public static void extract(Element element, StringBuffer buffer) {
		try {
			NodeIterator ni = XPathAPI.selectNodeIterator(element, ".//text()");
			Node n;
			while ((n = ni.nextNode()) != null)
				buffer.append(convert(n.getNodeValue().trim()));
			buffer.append("\n\n");
		} catch (Exception ex) {
			Log.error(ex);
		}
	}

	/**
	 * Returns the default encoding.
	 *
	 * @return default encoding
	 */
	public static String getDefaultEncoding() {
		return defaultEncoding;
	}

	/**
	 * Sets the default encoding.
	 *
	 * @param encoding
	 *                   default encoding
	 */
	public static void setDefaultEncoding(String encoding) {
		defaultEncoding = encoding;
	}

	/**
	 * Returns a unique XPath expression (like /foo[1]/bar[42]) for the given
	 * node.
	 *
	 * @param node
	 *                   XML node
	 * @return unique XPath expression
	 */
	public static String getXPath(Node node) {
		String xpath = new String(node.getNodeName());
		if (xpath.equals("#text"))
			xpath = new String("text()");
		if (node.getParentNode() != null) {
			int index = 0;
			Node prec = node;
			while (prec != null) {
				if (prec.getNodeName().toString().equals(
						node.getNodeName().toString())) {
					index++;
				}
				prec = prec.getPreviousSibling();
			}
			if (node.getParentNode() instanceof Document) {
			} else {
				xpath = getXPath(node.getParentNode()) + "/" + xpath + "["
						+ String.valueOf(index) + "]";
			}
		}
		return xpath;
	}

	/**
	 * Returns whether the parser is aware of namespaces.
	 *
	 * @return true iff the parser cares about namespaces
	 */
	public static boolean isNamespaceAware() {
		return namespaceAware;
	}

	/**
	 * Sets whether the parser is aware of namespaces.
	 *
	 * @param namespaceAware
	 *                   is true, the parser cares about namespaces
	 */
	public static void setNamespaceAware(boolean namespaceAware) {
		XMLUtilities.namespaceAware = namespaceAware;
	}

}