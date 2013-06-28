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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMViewCSS;

/*
 * This implementation knows how to get the computed style for a given DOM node
 */
public class ComputedStyleProperty implements IDOMInspectorProperty {

	public String displayName = "";
	public String styleName = "";

	public ComputedStyleProperty(String styleName, String displayName) {
		this.styleName = styleName;
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getValue(nsIDOMNode node) {

		try {

			nsIDOMElement element = (nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

			nsIDOMDocumentView documentView = (nsIDOMDocumentView) element.getOwnerDocument().queryInterface(nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID);
			nsIDOMViewCSS cssView = (nsIDOMViewCSS) documentView.getDefaultView().queryInterface(nsIDOMViewCSS.NS_IDOMVIEWCSS_IID);

			nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle(element, "");

			return computedStyle.getPropertyCSSValue(styleName).getCssText();

		} catch (Exception e) {
			MozIDEUIPlugin.log(new Status(IStatus.ERROR, MozIDEUIPlugin.PLUGIN_ID, "Failed getting computed style <" + styleName + "> with error<" + e.getMessage() + ">!", e));
			return "ERROR!";
		}
	}

}
