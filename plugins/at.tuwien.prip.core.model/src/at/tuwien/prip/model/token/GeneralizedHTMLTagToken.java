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
package at.tuwien.prip.model.token;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * 
 * GeneralizedHTMLTagToken.java
 *
 *
 *
 * Created: Apr 27, 2009 11:52:46 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class GeneralizedHTMLTagToken extends HTMLTagToken {

	List<Element> elements;
	
	/**
	 * 
	 * Constructor.
	 * 
	 * @param elements
	 */
	public GeneralizedHTMLTagToken(Element...elements) {
		super("");
		this.elements = new ArrayList<Element>();
		for (Element e : elements) {
			addElement(e);
		}
	}
	
	public GeneralizedHTMLTagToken(HTMLTagToken...tokens) {
		super("");
		this.elements = new ArrayList<Element>();
		for (HTMLTagToken e : tokens) {
			if (e.getElement()!=null) {
				addElement(e.getElement());
			}
		}
	}
	
	public void addElement(Element e) {
		elements.add(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Element> getElements() {
		return elements;
	}

	
}
