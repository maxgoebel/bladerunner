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
package at.tuwien.prip.common.utils;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class PropertiesParser extends DefaultHandler {

	public static void main(String args[]) throws SAXException {

		PropertyFileParser pfp = new PropertyFileParser();
		pfp.setContentHandler(new PropertiesParser());
		pfp.parse(buildProperties());
	}

	public static Properties buildProperties() {
		Properties props = new Properties();
		for (int i = 0; i < 10; i++)
			props.setProperty("key" + i, "value" + i);
		return props;
	}

	public void startDocument() {
		System.out.println("<keys>");
	}

	public void endDocument() {
		System.out.println("</keys>");
	}

	public void characters(char[] data, int start, int end) {
		String str = new String(data, start, end);
		System.out.print(str);
	}

	public void startElement(String uri, String qName, String lName, Attributes atts) {
		System.out.print("<" + lName + ">");
	}

	public void endElement(String uri, String qName, String lName) {
		System.out.println("</" + lName + ">");
	}
}

class PropertyFileParser extends SAXParser {

	//  private Properties props = null;

	private ContentHandler handler = null;

	public void parse(Properties props) throws SAXException {
		handler = getContentHandler();
		handler.startDocument();
		Enumeration<?> e = props.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String val = (String) props.getProperty(key);
			handler.startElement("", key, key, new AttributesImpl());
			char[] chars = getChars(val);
			handler.characters(chars, 0, chars.length);
			handler.endElement("", key, key);
		}
		handler.endDocument();
	}

	private char[] getChars(String value) {
		char[] chars = new char[value.length()];
		value.getChars(0, value.length(), chars, 0);
		return chars;
	}

}
