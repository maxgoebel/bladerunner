/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.css;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.atf.mozilla.ide.ui.inspector.properties.CompositeProperty;
import org.eclipse.atf.mozilla.ide.ui.inspector.properties.ComputedStyleProperty;
import org.eclipse.jface.viewers.Viewer;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMClientRect;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNSElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMViewCSS;

/**
 * Content provider for the Box Model tab in the CSS view
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * 
 */
public class BoxModelContentProvider extends CSSContentProvider {

	public BoxModelContentProvider() {
		super();
	}

	private void parseBoxModel() {
		nodeMap.clear();
		nsIDOMElement element = (nsIDOMElement) currentNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMNSElement nselement = (nsIDOMNSElement) element
				.queryInterface(nsIDOMNSElement.NS_IDOMNSELEMENT_IID);
		nsIDOMClientRect elementBox = nselement.getBoundingClientRect();
		nsIDOMDocumentView documentView = (nsIDOMDocumentView) element
				.getOwnerDocument().queryInterface(
						nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID);
		nsIDOMViewCSS cssView = (nsIDOMViewCSS) documentView.getDefaultView()
				.queryInterface(nsIDOMViewCSS.NS_IDOMVIEWCSS_IID);
		nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle(
				element, "");

		List boxModels = new ArrayList();
		String x_val = String.valueOf(elementBox.getTop());
		String y_val = String.valueOf(elementBox.getLeft());
		String width = String.valueOf(elementBox.getWidth());
		String height = String.valueOf(elementBox.getHeight());
		CSSProperty property = new CSSProperty("x", x_val, "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("y", y_val, "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("width", width, "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("height", height, "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("margin-top", computedStyle
				.getPropertyCSSValue("margin-top").getCssText(), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("margin-bottom", computedStyle
				.getPropertyCSSValue("margin-bottom").getCssText(), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("margin-right", computedStyle
				.getPropertyCSSValue("margin-right").getCssText(), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("margin-left", computedStyle
				.getPropertyCSSValue("margin-left").getCssText(), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("padding-top", computedStyle
				.getPropertyCSSValue("padding-top").getCssText(), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("padding-bottom", computedStyle
				.getPropertyCSSValue("padding-bottom").getCssText(), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("padding-right", computedStyle
				.getPropertyCSSValue("padding-right").getCssText(), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		property = new CSSProperty("padding-left", computedStyle
				.getPropertyCSSValue("padding-left").getCssText(), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);

		CompositeProperty borderTopProp = new CompositeProperty("border-top");
		borderTopProp.addProperties(new ComputedStyleProperty(
				"border-top-width", "border-top-width"));
		borderTopProp.addProperties(new ComputedStyleProperty(
				"border-top-style", "border-top-style"));
		borderTopProp.addProperties(new ComputedStyleProperty(
				"border-top-color", "border-top-color"));
		property = new CSSProperty(borderTopProp.getDisplayName(),
				borderTopProp.getValue(currentNode), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		CompositeProperty borderBottomProp = new CompositeProperty(
				"border-bottom");
		borderBottomProp.addProperties(new ComputedStyleProperty(
				"border-bottom-width", "border-bottom-width"));
		borderBottomProp.addProperties(new ComputedStyleProperty(
				"border-bottom-style", "border-bottom-style"));
		borderBottomProp.addProperties(new ComputedStyleProperty(
				"border-bottom-color", "border-bottom-color"));
		property = new CSSProperty(borderBottomProp.getDisplayName(),
				borderBottomProp.getValue(currentNode), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		CompositeProperty borderRightProp = new CompositeProperty(
				"border-right");
		borderRightProp.addProperties(new ComputedStyleProperty(
				"border-right-width", "border-right-width"));
		borderRightProp.addProperties(new ComputedStyleProperty(
				"border-right-style", "border-right-style"));
		borderRightProp.addProperties(new ComputedStyleProperty(
				"border-right-color", "border-right-color"));
		property = new CSSProperty(borderRightProp.getDisplayName(),
				borderRightProp.getValue(currentNode), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);
		CompositeProperty borderLeftProp = new CompositeProperty("border-left");
		borderLeftProp.addProperties(new ComputedStyleProperty(
				"border-left-width", "border-left-width"));
		borderLeftProp.addProperties(new ComputedStyleProperty(
				"border-left-style", "border-left-style"));
		borderLeftProp.addProperties(new ComputedStyleProperty(
				"border-left-color", "border-left-color"));
		property = new CSSProperty(borderLeftProp.getDisplayName(),
				borderLeftProp.getValue(currentNode), "", "", "");
		property.setPresent(true);
		property.setComputed(true);
		boxModels.add(property);

		property = new CSSProperty("", "", "", "", "Box Model");
		property.setProperty(false);
		nodeMap.put(property, boxModels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.atf.mozilla.ide.ui.css.CSSContentProvider#inputChanged(org
	 * .eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null) {
			if (newInput instanceof nsIDOMNode) {
				currentNode = (nsIDOMNode) newInput;
				parseBoxModel();
			}
		}
	}

}
