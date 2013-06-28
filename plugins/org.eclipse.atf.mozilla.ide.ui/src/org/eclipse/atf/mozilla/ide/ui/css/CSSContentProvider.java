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
package org.eclipse.atf.mozilla.ide.ui.css;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mozilla.interfaces.inIDOMUtils;
import org.mozilla.interfaces.nsIDOMCSSRule;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMCSSStyleRule;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsISupportsArray;
import org.mozilla.xpcom.Mozilla;

/**
 * Content provider for different views of current CSS
 * for a DOM element
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class CSSContentProvider implements ITreeContentProvider {

	//Current DOM node
	protected nsIDOMNode currentNode;

	//CSSProperty-->List<CSSProperty>
	protected Map nodeMap = new TreeMap();

	//List<CSSProperty>-->nsISupportsArray
	protected Map parentMap = new HashMap();

	//String-->nsIDOMCSSStyleDeclaration
	protected Map propertiesToRules = new HashMap();

	//String-->CSSProperty
	protected Map definedProperties = new HashMap();

	protected static final String INLINE_STYLE = "Inline_Styles";
	protected static final String PROPERTY_ADDED = "(**Property Added)";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof nsIDOMNode) {
			return nodeMap.keySet().toArray();
		}
		if (nodeMap.containsKey(parentElement)) {
			return ((List) nodeMap.get(parentElement)).toArray();
		}
		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		String retVal = null;
		if (element instanceof Map) {
			return parentMap.get(element);
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof CSSProperty) {
			if (((CSSProperty) element).getName().equals("") && ((CSSProperty) element).getValue().equals("")) {
				return nodeMap.containsKey(element);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(currentNode);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {

	}

	/**
	 * 
	 *
	 */
	protected void parseNodes() {
		nodeMap.clear();
		propertiesToRules.clear();
		parentMap.clear();
		definedProperties.clear();
		nsIDOMNode node = (nsIDOMNode) currentNode;
		nsIDOMElement domElement = (nsIDOMElement) (node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
		try {
			inIDOMUtils service = (inIDOMUtils) Mozilla.getInstance().getServiceManager().getServiceByContractID("@mozilla.org/inspector/dom-utils;1", inIDOMUtils.INIDOMUTILS_IID);
			nsISupportsArray rules = (nsISupportsArray) service.getCSSStyleRules(domElement);
			long numRules = rules.count();

			for (long i = 0; i < numRules; i++) {
				nsIDOMCSSRule ret;
				ret = (nsIDOMCSSRule) (rules.getElementAt(i).queryInterface(nsIDOMCSSRule.NS_IDOMCSSRULE_IID));

				nsIDOMCSSStyleRule styleRule = (nsIDOMCSSStyleRule) ret.queryInterface(nsIDOMCSSStyleRule.NS_IDOMCSSSTYLERULE_IID);
				nsIDOMCSSStyleDeclaration styleDec = styleRule.getStyle();
				String lineNumber = Long.toString(service.getRuleLine(styleRule));
				String url = styleRule.getParentStyleSheet().getHref();
				CSSProperty ruleProperty;
				List properties = new ArrayList();
				for (long j = 0; j < styleDec.getLength(); j++) {
					String name = styleDec.item(j);
					String value = styleDec.getPropertyValue(name);
					CSSProperty cssProperty = new CSSProperty(name, value, url, lineNumber, styleRule.getSelectorText());
					properties.add(cssProperty);
					if (definedProperties.containsKey(name)) {
						CSSProperty tempProp = (CSSProperty) definedProperties.get(name);
						tempProp.setPresent(false);
					}
					cssProperty.setPresent(true);
					cssProperty.setProperty(true);
					definedProperties.put(name, cssProperty);

				}
				ruleProperty = new CSSProperty("", "", url, lineNumber, styleRule.getSelectorText());
				ruleProperty.setProperty(false);
				propertiesToRules.put(ruleProperty.getHash(), styleDec);
				nodeMap.put(ruleProperty, properties);
				parentMap.put(properties, rules);
			}
		} catch (Exception e) {
			MozIDEUIPlugin.log(e);
		}
		String inlineStyle = domElement.getAttribute("style");
		if (inlineStyle != null && !inlineStyle.equals("")) {
			StringTokenizer token = new StringTokenizer(inlineStyle, ";");
			if (token.hasMoreTokens()) {
				CSSProperty inlineRuleProperty = new CSSProperty("", "", "", "", INLINE_STYLE);
				inlineRuleProperty.setProperty(false);
				inlineRuleProperty.setInline(true);
				List inlineProperties = new ArrayList();
				while (token.hasMoreTokens()) {
					String cssDec = (String) token.nextToken();
					if (cssDec.indexOf(':') != -1) {
						String name = cssDec.substring(0, cssDec.indexOf(':'));
						name = name.trim();
						String value = cssDec.substring(cssDec.indexOf(':') + 1, cssDec.length());
						value = value.trim();
						CSSProperty inlineProperty = new CSSProperty(name, value, "", "", INLINE_STYLE);
						if (definedProperties.containsKey(name)) {
							CSSProperty tempProp = (CSSProperty) definedProperties.get(name);
							tempProp.setPresent(false);
						}
						inlineProperty.setInline(true);
						inlineProperty.setPresent(true);
						inlineProperty.setProperty(true);
						definedProperties.put(name, inlineProperty);
						inlineProperties.add(inlineProperty);
					}
				}
				nodeMap.put(inlineRuleProperty, inlineProperties);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null) {
			if (newInput instanceof nsIDOMNode) {
				currentNode = (nsIDOMNode) newInput;
				parseNodes();
			}
		}
	}

}
