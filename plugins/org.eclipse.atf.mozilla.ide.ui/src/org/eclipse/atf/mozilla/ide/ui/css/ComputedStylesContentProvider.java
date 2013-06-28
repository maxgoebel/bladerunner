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
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMViewCSS;

/**
 * Content provider for the Computed Styles tab in the CSS view
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class ComputedStylesContentProvider extends CSSContentProvider {
		
	public ComputedStylesContentProvider() {
		super();
	}
	
	private void parseComputedStyles() {
		parseNodes();
		nodeMap.clear();
		List computedStyles = new ArrayList();
		nsIDOMElement domElement = (nsIDOMElement)(currentNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
		nsIDOMDocumentView documentView = (nsIDOMDocumentView)domElement.getOwnerDocument().queryInterface( nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID );
		nsIDOMViewCSS cssView = (nsIDOMViewCSS)documentView.getDefaultView().queryInterface( nsIDOMViewCSS.NS_IDOMVIEWCSS_IID );
		nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle( domElement, "" );
		CSSProperty property;
		for( int i = 0; i < computedStyle.getLength(); i++ ) {
			property = new CSSProperty(computedStyle.item(i),
										computedStyle.getPropertyCSSValue(computedStyle.item(i)).getCssText(),
										"","","Computed Styles");
			computedStyles.add(property);
			property.setProperty(true);
			if( definedProperties.containsKey(property.getName()) ) {
				property.setPresent(true);
				CSSProperty defined = (CSSProperty)definedProperties.get(property.getName());
				property.setURL(defined.getURL());
				property.setLineNumber(defined.getLineNumber());
				property.setComputed(true);
			}
		}
		property = new CSSProperty("","","","","Computed Styles");
		property.setProperty(false);
		nodeMap.put(property, computedStyles);
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if( newInput != null ) {
			if( newInput instanceof nsIDOMNode ) {
				currentNode = (nsIDOMNode)newInput;
				parseComputedStyles();
			}
		}
	}
	
	public Map getComputedStyles() {
		return nodeMap;
	}

}
