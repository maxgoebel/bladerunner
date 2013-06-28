/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.jseval.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class JSTypeFactory {

	private IJSValue jsType = null;

	protected static JSTypeFactory instance = null;

	public static JSTypeFactory getInstance() {
		if (instance == null) {
			instance = new JSTypeFactory();
		}
		return instance;
	}

	private JSTypeFactory() {
	};

	/*
	 * IMPORTANT: This handler only supports one level of object properties.
	 */
	class JSEvalParserHandler extends DefaultHandler {
		private JSObjectProperty currentProp = null;

		private boolean isString = false;
		private boolean isError = false;
		private boolean isNumber = false;
		private boolean isBoolean = false;

		public void startDocument() throws SAXException {
			jsType = null;
			currentProp = null;

			isString = false;
			isError = false;
			isNumber = false;
			isBoolean = false;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(IJSValue.NUMBER)) {
				if (currentProp == null) {
					jsType = new JSNumber();
				} else {
					currentProp.value = new JSNumber();
					((BaseJSValue) currentProp.value).setParentProperty(currentProp);
				}

				isNumber = true;
			} else if (qName.equalsIgnoreCase(IJSValue.BOOLEAN)) {
				if (currentProp == null) {
					jsType = new JSBoolean();
				} else {
					currentProp.value = new JSBoolean();
					((BaseJSValue) currentProp.value).setParentProperty(currentProp);
				}
				isBoolean = true;
			} else if (qName.equalsIgnoreCase(IJSValue.STRING)) {
				if (currentProp == null) {
					jsType = new JSString();
				} else {
					currentProp.value = new JSString();
					((BaseJSValue) currentProp.value).setParentProperty(currentProp);
				}
				isString = true;
			} else if (qName.equalsIgnoreCase(IJSValue.NULL)) {
				if (currentProp == null) {
					jsType = new JSNull();
				} else {
					currentProp.value = new JSNull();
					((BaseJSValue) currentProp.value).setParentProperty(currentProp);
				}
			} else if (qName.equalsIgnoreCase(IJSValue.UNDEFINED)) {
				if (currentProp == null) {
					jsType = new JSUndefined();
				} else {
					currentProp.value = new JSUndefined();
					((BaseJSValue) currentProp.value).setParentProperty(currentProp);
				}
			} else if (qName.equalsIgnoreCase(IJSValue.FUNCTION)) {
				if (currentProp == null) {
					jsType = new JSFunction();
					((JSFunction) jsType).evaluated = true;
				} else {
					currentProp.value = new JSFunction();
					((BaseJSValue) currentProp.value).setParentProperty(currentProp);
				}
			} else if (qName.equalsIgnoreCase(IJSValue.OBJECT)) {
				if (currentProp == null) {
					jsType = new JSObject();
					((JSObject) jsType).evaluated = true;
				} else {
					currentProp.value = new JSObject();
					((BaseJSValue) currentProp.value).setParentProperty(currentProp);
				}
			} else if (qName.equalsIgnoreCase(IJSValue.ERROR)) {
				if (currentProp == null) {
					jsType = new JSError();
				} else {
					currentProp.value = new JSError();
					((BaseJSValue) currentProp.value).setParentProperty(currentProp);
				}
				isError = true;
			}

			else if (qName.equalsIgnoreCase("property")) {
				//if we are here, jsType must be object 
				//this parser only supports on level of object
				currentProp = new JSObjectProperty();
				currentProp.name = attributes.getValue("name");
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equalsIgnoreCase(IJSValue.NUMBER)) {
				isNumber = false;
			} else if (qName.equalsIgnoreCase(IJSValue.BOOLEAN)) {
				isBoolean = false;
			} else if (qName.equalsIgnoreCase(IJSValue.STRING)) {
				isString = false;
			} else if (qName.equalsIgnoreCase(IJSValue.NULL)) {
			} else if (qName.equalsIgnoreCase(IJSValue.UNDEFINED)) {
			} else if (qName.equalsIgnoreCase(IJSValue.FUNCTION)) {
			} else if (qName.equalsIgnoreCase(IJSValue.OBJECT)) {

			} else if (qName.equalsIgnoreCase(IJSValue.ERROR)) {
				isError = false;
			} else if (qName.equalsIgnoreCase("property")) {
				//jsType should be an object
				if (jsType instanceof JSObject && currentProp != null) {
					((JSObject) jsType).addProperty(currentProp);
				}
				currentProp = null;
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException {

			if (isString) {
				String str = new String(ch, start, length);
				try {
					str = URLDecoder.decode(str, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					MozIDEUIPlugin.log(e);
				}
				System.out.println(str);

				if (currentProp == null) {
					//support possible chunked data
					if (((JSString) jsType).value == null) {
						((JSString) jsType).value = str;
					} else {
						((JSString) jsType).value += str;
					}
				} else {
					if (((JSString) currentProp.value).value == null) {
						((JSString) currentProp.value).value = str;
					} else {
						((JSString) currentProp.value).value += str;
					}
				}

			} else if (isError) {
				if (currentProp == null) {
					((JSError) jsType).message = new String(ch, start, length);
				} else {
					((JSError) currentProp.value).message = new String(ch, start, length);
				}

			} else if (isNumber) {
				if (currentProp == null) {
					((JSNumber) jsType).value = new String(ch, start, length);
				} else {
					((JSNumber) currentProp.value).value = new String(ch, start, length);
				}

			} else if (isBoolean) {
				if (currentProp == null) {
					((JSBoolean) jsType).value = new String(ch, start, length);
				} else {
					((JSBoolean) currentProp.value).value = new String(ch, start, length);
				}
			}
		}

	};

	protected JSEvalParserHandler handler = new JSEvalParserHandler();

	public IJSValue create(String xml) {
		//MozIDEUIPlugin.debug( xml );

		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

			//parser.setProperty( "http://xml.org/sax/properties/lexical-handler", handler );
			parser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")), handler);
			return jsType;

		} catch (ParserConfigurationException e) {
			MozIDEUIPlugin.log(e);
		} catch (SAXException e) {
			MozIDEUIPlugin.log(e);
		} catch (FactoryConfigurationError e) {
			MozIDEUIPlugin.log(e);
		} catch (IOException e) {
			MozIDEUIPlugin.log(e);
		}

		return null;
	}

}
