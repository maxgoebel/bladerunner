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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import at.tuwien.prip.common.datastructures.Pair;


/**
 * 
 * XMLDocument.java
 *
 *
 * XML Utilities. Read and write xml documents.
 *
 * Created: Jun 16, 2009 6:33:40 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class XMLDocument 
{

	static String INDENT = "	   ";
	
	XMLObject root;
	
	public XMLDocument() 
	{
		root = new XMLObject("root");
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	public Document parse2document(URI uri)
	{
		Document doc = null;
		try 
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(uri.toString());
		} 
		catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	public XMLDocument parse (URI uri) 
	{
		XMLDocument document = new XMLDocument();
		XMLObject parent = document.root;
		
		Stack<XMLObject> stack = new Stack<XMLObject>();
		stack.push(new XMLObject());
		try 
		{
			InputStream fin = new FileInputStream(uri.toString());
			InputStream bin = new BufferedInputStream(fin);
			InputStreamReader in = new InputStreamReader(bin, "UTF-8");
			
			boolean attFlag = false;
			String attCache = null;
		    StringBuffer sb = new StringBuffer();
		   
		    int c,d;
		    while ((c = in.read()) != -1) 
		    {
		    	if (((char)c)=='<') 
		    	{
		    		sb = new StringBuffer();
		    		
		    		if((d = (char) in.read()) != -1 && ((char)d) == '/') 
		    		{
		    			parent.addChild(stack.pop());
		    			stack.push(new XMLObject());
		    		}
		    			
		    		else sb.append((char)d);
		    	}
		    	
	    		else if(((char)c)==' ')
	    		{
	    			stack.pop().name = sb.toString();
	    			sb = new StringBuffer();
	    			attFlag = true;
	    		}
		    	
	    		else if (((char)c)=='=') 
	    		{
	    			if (attFlag) {
	    				attCache = sb.toString();
	    				sb = new StringBuffer();
	    			}
	    			else sb.append((char)c);
	    		}
		    	
	    		else if (((char)c)=='>') 
	    		{
	    			if (attFlag && attCache!=null) {
	    				stack.pop().attributes.put(attCache, sb.toString());
	    				attFlag = false;
	    				sb = new StringBuffer();
	    			}
	    			else {
	    				stack.pop().name = sb.toString();
		    			sb = new StringBuffer();
	    			}
	    		}
		    	
		    	sb.append((char) c);
		    }
		    
			in.read();
			in.close();
			
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}
	
	/**
	 * 
	 * @param uri
	 */
	public void write(URI uri) 
	{
		try 
		{
			OutputStream fout = new FileOutputStream(uri.toString());//"/home/max/datafile.xml");		
			OutputStream bout= new BufferedOutputStream(fout);
			OutputStreamWriter out = new OutputStreamWriter(bout, "UTF-8");
			
			String xml = toXML();
			out.write(xml);
			
			out.flush();  // Don't forget to flush!
			out.close();
			
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private String createHeader () {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" ");
		sb.append("encoding=\"UTF-8\"?>\r\n");  
		return sb.toString();
	}
	
	public Document newDocument () {
		return null;
	}
	
	public XMLObject addXMLObject (XMLObject parent, String s) {
		XMLObject obj = new XMLObject(s);
		
		obj.setParent(parent);
		parent.children.add(obj);
		return obj;
	}
	
	public String toXML () {
		StringBuffer sb = new StringBuffer();
		sb.append(createHeader());
		
		if (root!=null) {
			sb.append(root.toXML());
		}
		return sb.toString();
	}
	
	/**
	 * @return the root
	 */
	public XMLObject getRoot() {
		return root;
	}
		
	public XMLObject createXMLObject (String s) {
		return new XMLObject(s);
	}
	
	/**
	 * Base XML object
	 */
	public class XMLObject {
		int indent = 0;
		String name, value;
		LinkedHashMap<String, String> attributes;
		
		XMLObject parent;
		ArrayList<XMLObject> children;
		
		public XMLObject() {
			attributes = new LinkedHashMap<String, String>();
			children = new ArrayList<XMLObject>();
			parent = null;
		}
		
		public XMLObject(String name) {
			this();
			this.name = name;
		}
		
		public XMLObject(String name, String value) {
			this(name);
			this.value = value;
		}
		
		public XMLObject(String name, String value, Pair<String,String>... atts) {
			this(name, value);
			for (Pair<String,String>val : atts) {
				attributes.put(val.getFirst(),val.getSecond());
			}
		}
		
		public void setParent (XMLObject parent) {
			this.parent = parent;
			this.indent = parent.indent + 1;
		}
		
		public void addChild (XMLObject child) {
			if (!children.contains(child))
				this.children.add(child);
			
			child.setParent(this);
		}
		
		public XMLObject addChild (String name, String value, Pair<String,String>... atts) {
			XMLObject child = new XMLObject(name, value, atts);
			addChild(child);
			return child;
		}
		
		public String toXML () {
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<indent; i++) sb.append(INDENT); 
			sb.append("<"+name);
			for (String key : attributes.keySet()) {
				sb.append(" "+key+"="+attributes.get(key));
			}

			if (value!=null) {
				sb.append(">"+value+"</"+name+">\r\n");
			}
			
			else {
				sb.append(">\r\n");
			
				for (XMLObject child : children) {
					sb.append(child.toXML());
				}
				for (int i=0; i<indent; i++) sb.append(INDENT); 
				sb.append("</"+name+">\r\n");
			}
			return sb.toString();
		}
		
		@Override
		public String toString() {
			return toXML();
		}
	}
	
}//XMLDocument
