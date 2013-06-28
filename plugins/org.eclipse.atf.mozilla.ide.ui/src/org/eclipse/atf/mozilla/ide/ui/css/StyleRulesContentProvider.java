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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.jface.viewers.Viewer;
import org.mozilla.interfaces.inIDOMUtils;
import org.mozilla.interfaces.nsIDOMCSSRule;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMCSSStyleRule;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMViewCSS;
import org.mozilla.interfaces.nsISupportsArray;
import org.mozilla.xpcom.Mozilla;

/**
 * Content provider for the Style Rules tab in the CSS view
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class StyleRulesContentProvider extends CSSContentProvider {

	//String-->Map<String,String>
	private Map originals = new TreeMap();
	private Map modifieds = new TreeMap();

	private String original;
	private String modified;

	private Set propertyNamesCache;

	private List modifiedStyleSheets = new ArrayList();

	public StyleRulesContentProvider() {
		super();
	}

	public boolean isPropertyName(String name) {
		if (propertyNamesCache == null) {
			Set tmp = new HashSet();

			nsIDOMElement domElement = (nsIDOMElement) (currentNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
			nsIDOMDocumentView documentView = (nsIDOMDocumentView) domElement.getOwnerDocument().queryInterface(nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID);
			nsIDOMViewCSS cssView = (nsIDOMViewCSS) documentView.getDefaultView().queryInterface(nsIDOMViewCSS.NS_IDOMVIEWCSS_IID);
			nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle(domElement, "");
			for (int i = 0; i < computedStyle.getLength(); i++) {
				String aName = computedStyle.item(i);
				tmp.add(aName);

				// for properties like margin-left-width, adds also margin-left and margin
				while (aName.lastIndexOf('-') > -1) {
					aName = aName.substring(0, aName.lastIndexOf('-'));
					tmp.add(aName);
				}
			}

			propertyNamesCache = tmp;

		}

		return propertyNamesCache.contains(name);
	}

	/**
	 * Sets a css property for a particular rule
	 * @param newProperty - the property to update
	 * @return true, if successful
	 */
	public void updateProperty(CSSProperty newProperty) {
		nsIDOMCSSStyleDeclaration rule = (nsIDOMCSSStyleDeclaration) propertiesToRules.get(newProperty.getHash());

		if (rule != null) {
			modifiedStyleSheets.add(rule);
			rule.setProperty(newProperty.getName(), newProperty.getValue(), "");

		} else if (newProperty.isInline()) {
			nsIDOMElement domElement = (nsIDOMElement) (currentNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
			List properties = (List) nodeMap.get(new CSSProperty("", "", "", "", INLINE_STYLE));
			Iterator iter = properties.iterator();
			String styleAttribute = "";
			if (newProperty.isNewRule()) {
				styleAttribute += newProperty.getName() + ":" + newProperty.getValue() + ";";
			}
			while (iter.hasNext()) {
				CSSProperty prop = (CSSProperty) iter.next();
				if (!prop.getName().equals(newProperty.getName())) {
					styleAttribute += prop.getName() + ":" + prop.getValue() + ";";
				} else {
					styleAttribute += newProperty.getName() + ":" + newProperty.getValue() + ";";
				}
			}
			domElement.setAttribute("style", styleAttribute);
		}

		saveDiffs(newProperty);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null) {
			if (newInput instanceof CSSProperty) {
				updateProperty((CSSProperty) newInput);
			} else if (newInput instanceof nsIDOMNode) {
				currentNode = (nsIDOMNode) newInput;
				parseNodes();
			}
		}
	}

	/**
	 * 
	 * @param property
	 */
	public void deleteProperty(CSSProperty property) {
		nsIDOMCSSStyleDeclaration rule = (nsIDOMCSSStyleDeclaration) propertiesToRules.get(property.getHash());
		if (rule != null) {
			rule.removeProperty(property.getName());
		} else {
			nsIDOMElement domElement = (nsIDOMElement) (currentNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
			List properties = (List) nodeMap.get(new CSSProperty("", "", "", "", INLINE_STYLE));
			Iterator iter = properties.iterator();
			String styleAttribute = "";
			while (iter.hasNext()) {
				CSSProperty prop = (CSSProperty) iter.next();
				if (!prop.getName().equals(property.getName())) {
					styleAttribute += prop.getName() + ":" + prop.getValue() + ";";
				}
			}
			if (styleAttribute.equals("")) {
				domElement.removeAttribute("style");
			} else {
				domElement.setAttribute("style", styleAttribute);
			}
		}
		saveDiffs(property);
	}

	private void saveDiffs(CSSProperty newProperty) {
		String value = newProperty.getOriginalValue();
		if (newProperty.isNewRule()) {
			value = PROPERTY_ADDED;
		}
		if (!originals.containsKey(formatURL(newProperty.getURL()) + "\n" + newProperty.getRule())) {
			Map cssProperties = new TreeMap();
			cssProperties.put(newProperty.getOriginalName(), value);

			originals.put(formatURL(newProperty.getURL()) + "\n" + newProperty.getRule(), cssProperties);
		} else {
			Map cssProperties = (Map) originals.get(formatURL(newProperty.getURL()) + "\n" + newProperty.getRule());
			if (!cssProperties.containsKey(newProperty.getOriginalName())) {
				cssProperties.put(newProperty.getOriginalName(), value);
			}
		}
	}

	public void createDiff() {
		modifieds.clear();
		nsIDOMNode node = (nsIDOMNode) currentNode;
		nsIDOMElement domElement = (nsIDOMElement) (node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
		try {
			inIDOMUtils service = (inIDOMUtils) Mozilla.getInstance().getServiceManager().getServiceByContractID("@mozilla.org/inspector/dom-utils;1", inIDOMUtils.INIDOMUTILS_IID);
			nsISupportsArray rules = service.getCSSStyleRules(domElement);
			long numRules = rules.count();
			for (long i = 0; i < numRules; i++) {
				nsIDOMCSSRule ret;
				ret = (nsIDOMCSSRule) (rules.getElementAt(i).queryInterface(nsIDOMCSSRule.NS_IDOMCSSRULE_IID));
				nsIDOMCSSStyleRule styleRule = (nsIDOMCSSStyleRule) ret.queryInterface(nsIDOMCSSStyleRule.NS_IDOMCSSSTYLERULE_IID);
				nsIDOMCSSStyleDeclaration styleDec = styleRule.getStyle();
				String lineNumber = Long.toString(service.getRuleLine(styleRule));
				String url = styleRule.getParentStyleSheet().getHref();

				for (long j = 0; j < styleDec.getLength(); j++) {
					String name = styleDec.item(j);
					String value = styleDec.getPropertyValue(name);
					CSSProperty cssProperty = new CSSProperty(name, value, url, lineNumber, styleRule.getSelectorText());
					if (modifieds.containsKey(formatURL(cssProperty.getURL()) + "\n" + cssProperty.getRule())) {
						Map properties = (Map) modifieds.get(formatURL(cssProperty.getURL()) + "\n" + cssProperty.getRule());
						properties.put(cssProperty.getName(), cssProperty.getValue());
					} else {
						Map properties = new TreeMap();
						properties.put(cssProperty.getName(), cssProperty.getValue());
						modifieds.put(formatURL(cssProperty.getURL()) + "\n" + cssProperty.getRule(), properties);
					}

				}
			}
		} catch (Exception e) {
			MozIDEUIPlugin.log(e);
		}
		String inlineStyle = domElement.getAttribute("style");
		if (inlineStyle != null && !inlineStyle.equals("")) {
			StringTokenizer token = new StringTokenizer(inlineStyle, ";");
			if (token.hasMoreTokens()) {
				while (token.hasMoreTokens()) {
					String cssDec = (String) token.nextToken();
					if (cssDec.indexOf(':') != -1) {
						String name = cssDec.substring(0, cssDec.indexOf(':'));
						name = name.trim();
						String value = cssDec.substring(cssDec.indexOf(':') + 1, cssDec.length());
						value = value.trim();
						CSSProperty inlineProperty = new CSSProperty(name, value, "", "", INLINE_STYLE);
						if (modifieds.containsKey(formatURL("") + "\n" + INLINE_STYLE)) {
							Map properties = (Map) modifieds.get(formatURL("") + "\n" + INLINE_STYLE);
							properties.put(inlineProperty.getName(), inlineProperty.getValue());
						} else {
							Map properties = new TreeMap();
							properties.put(inlineProperty.getName(), inlineProperty.getValue());
							modifieds.put(formatURL("") + "\n" + INLINE_STYLE, properties);
						}
					}
				}
			}
		}
	}

	public void generateGlobalDiff() {
		modifieds.clear();
		Iterator iter = modifiedStyleSheets.iterator();
		inIDOMUtils service = (inIDOMUtils) Mozilla.getInstance().getServiceManager().getServiceByContractID("@mozilla.org/inspector/dom-utils;1", inIDOMUtils.INIDOMUTILS_IID);
		while (iter.hasNext()) {
			try {
				nsIDOMCSSStyleDeclaration styleDec = (nsIDOMCSSStyleDeclaration) iter.next();
				nsIDOMCSSStyleRule styleRule = (nsIDOMCSSStyleRule) styleDec.getParentRule().queryInterface(nsIDOMCSSStyleRule.NS_IDOMCSSSTYLERULE_IID);
				String lineNumber = Long.toString(service.getRuleLine(styleRule));
				String url = styleRule.getParentStyleSheet().getHref();
				for (long i = 0; i < styleDec.getLength(); i++) {
					String name = styleDec.item(i);
					String value = styleDec.getPropertyValue(name);
					CSSProperty cssProperty = new CSSProperty(name, value, url, lineNumber, styleRule.getSelectorText());
					if (modifieds.containsKey(formatURL(cssProperty.getURL()) + "\n" + cssProperty.getRule())) {
						Map properties = (Map) modifieds.get(formatURL(cssProperty.getURL()) + "\n" + cssProperty.getRule());
						properties.put(cssProperty.getName(), cssProperty.getValue());
					} else {
						Map properties = new TreeMap();
						properties.put(cssProperty.getName(), cssProperty.getValue());
						modifieds.put(formatURL(cssProperty.getURL()) + "\n" + cssProperty.getRule(), properties);
					}
				}
			} catch (Exception e) {
				MozIDEUIPlugin.log(e);
			}
		}
		generatedDiffStrings();
	}

	private String formatURL(String url) {
		return "/* " + url + " */";
	}

	public void generateSelectionDiff() {
		createDiff();
		generatedDiffStrings();
	}

	public void generatedDiffStrings() {
		boolean diffsFound;
		modified = "/* Modified Style Rules: */\n\n";
		original = "/* Original Style Rules: */\n\n";
		Iterator moditer = originals.keySet().iterator();
		while (moditer.hasNext()) {
			String rule = (String) moditer.next();
			Map mod_map = (Map) modifieds.get(rule);
			Map org_map = (Map) originals.get(rule);
			Iterator nameiter = org_map.keySet().iterator();
			diffsFound = false;
			String t_original = rule + " {\n";
			String t_modified = rule + " {\n";
			while (nameiter.hasNext()) {
				String name = (String) nameiter.next();
				if (mod_map != null) {
					if (mod_map.get(name) == null) {
						t_original += "\t" + name + ":" + org_map.get(name) + "\n";
						t_modified += "\t" + "/* " + name + ":" + "(**Property Deleted) */" + "\n";
						diffsFound = true;
					} else if (!mod_map.get(name).equals(org_map.get(name))) {
						if (org_map.get(name).equals(PROPERTY_ADDED)) {
							t_original += "\t" + "/* " + name + ":" + org_map.get(name) + " */" + "\n";
						} else {
							t_original += "\t" + name + ":" + org_map.get(name) + "\n";
						}
						t_modified += "\t" + name + ":" + mod_map.get(name) + "\n";
						diffsFound = true;
					}
				}
			}
			t_original += "}\n\n";
			t_modified += "}\n\n";
			if (diffsFound) {
				modified += t_modified;
				original += t_original;
			}
		}
	}

	/**
	 * Gets the modified CSS rules.
	 * @return - String of modifications
	 */
	public String getModified() {
		return modified;
	}

	/**
	 * Gets the original CSS rules.
	 * @return - String of originals
	 */
	public String getOriginal() {
		return original;
	}

}
