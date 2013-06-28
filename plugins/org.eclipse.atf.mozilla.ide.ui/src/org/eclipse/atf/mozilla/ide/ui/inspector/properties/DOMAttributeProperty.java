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

package org.eclipse.atf.mozilla.ide.ui.inspector.properties;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/*
 * This implementation knows how to get the value for a DOM attribute for a given DOM node
 */
public class DOMAttributeProperty implements IDOMInspectorEditableProperty {

	protected String displayName = "";
	protected String attributeName = "";

	public DOMAttributeProperty(String attributeName, String displayName) {
		this.attributeName = attributeName;
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getName() {
		return attributeName;
	}

	public String getValue(nsIDOMNode node) {

		try {

			nsIDOMElement element = (nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

			return element.getAttribute(attributeName);

		} catch (Exception e) {
			MozIDEUIPlugin.log(e);
			return "ERROR!";
		}
	}

	public void setValue(nsIDOMNode node, String value) {

		try {

			nsIDOMElement element = (nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

			element.setAttribute(attributeName, value);

		} catch (Exception e) {
			MozIDEUIPlugin.log(e);
		}

	}

}
