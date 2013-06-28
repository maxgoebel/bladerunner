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
import org.mozilla.interfaces.nsIDOMClientRect;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNSElement;
import org.mozilla.interfaces.nsIDOMNode;

/*
 * This implementation knows how to get BOX model values for a given DOM node
 */
public class BoxModelProperty implements IDOMInspectorProperty {

	public static String X_PROP = "x";
	public static String Y_PROP = "y";
	public static String WIDTH_PROP = "width";
	public static String HEIGHT_PROP = "height";

	protected String displayName = "";
	protected String propertyName = "";

	public BoxModelProperty(String propertyName, String displayName) {
		this.propertyName = propertyName;
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getValue(nsIDOMNode node) {

		try {
			nsIDOMElement element = (nsIDOMElement) node
					.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

			nsIDOMNSElement nselement = (nsIDOMNSElement) element
					.queryInterface(nsIDOMNSElement.NS_IDOMNSELEMENT_IID);
			nsIDOMClientRect elementBox = nselement.getBoundingClientRect();

			if (X_PROP.equals(propertyName))
				return String.valueOf(elementBox.getTop());

			else if (Y_PROP.equals(propertyName))
				return String.valueOf(elementBox.getLeft());

			else if (WIDTH_PROP.equals(propertyName))
				return String.valueOf(elementBox.getWidth());

			else if (HEIGHT_PROP.equals(propertyName))
				return String.valueOf(elementBox.getHeight());

			else
				return "INVALID PROPERTY NAME!";
		} catch (Exception e) {
			MozIDEUIPlugin.log(e);
			return "ERROR!";
		}

	}

}
