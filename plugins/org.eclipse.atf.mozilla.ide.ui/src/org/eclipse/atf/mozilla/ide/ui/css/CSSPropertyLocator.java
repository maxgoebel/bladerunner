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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.core.XPCOMThreadProxy;
import org.eclipse.atf.mozilla.ide.events.DOMMutationListener;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.XPCOMThreadProxyHelper;
import org.eclipse.atf.mozilla.ide.ui.browser.SelectionBox;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.mozilla.interfaces.inIDOMUtils;
import org.mozilla.interfaces.nsIDOMCSSRule;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMCSSStyleRule;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsISupportsArray;
import org.mozilla.xpcom.Mozilla;

/**
 * Locates and highlights selected CSS properties
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * 
 */
public class CSSPropertyLocator {

	private XPCOMThreadProxyHelper proxyHelper = new XPCOMThreadProxyHelper(Display.getDefault());
	private nsIDOMDocument document;
	private boolean on = false;
	private List<SelectionBox> boxes = new ArrayList<SelectionBox>();
	private CSSJobSearch cts = new CSSJobSearch();
	private IWebBrowser container;
	private nsIDOMNode currNode;
	private nsIDOMElement currElement;
	private DOMMutationListener domMutationListener = new DOMMutationListener() {
		public void attributeAdded(nsIDOMElement ownerElement, String attributeName) {
			if (currElement != null && currElement.equals(ownerElement) && attributeName.equals("style")) {
				addAttribute(ownerElement);
			}
		}

		public void attributeModified(nsIDOMElement ownerElement, String attributeName, String newValue, String previousValue) {
			if (currElement != null && currElement.equals(ownerElement) && attributeName.equals("style")) {
				addAttribute(ownerElement);
			}
		}

		public void attributeRemoved(nsIDOMElement ownerElement, String attributeName) {
			if (currElement != null && currElement.equals(ownerElement) && attributeName.equals("style")) {
				removeAttribute(ownerElement);
			}
		}

		public void nodeInserted(nsIDOMNode parentNode, nsIDOMNode insertedNode) {
			add(insertedNode);
			addAttribute(insertedNode);
		}

		public void nodeRemoved(nsIDOMNode parentNode, nsIDOMNode removedNode) {
			remove(removedNode);
		}
	};

	private List<nsIDOMNode> removed = new ArrayList<nsIDOMNode>();

	private class CSSJobSearch extends Job {

		private nsIDOMNode node;
		/* maps CSS property name to (map of nsIDOMElement to rule key)*/
		private Map<String, Map<nsIDOMElement, String>> cssProperties = new HashMap<String, Map<nsIDOMElement, String>>();
		private Map<String, List<nsIDOMElement>> rulesToElements = new HashMap<String, List<nsIDOMElement>>();
		private Map<String, List<nsIDOMElement>> rulesToProperties = new HashMap<String, List<nsIDOMElement>>();

		private inIDOMUtils service;

		public CSSJobSearch() {
			super("Indexing CSS for page ");
			service = (inIDOMUtils) XPCOMThreadProxy.createProxy(Mozilla.getInstance().getServiceManager().getServiceByContractID("@mozilla.org/inspector/dom-utils;1", inIDOMUtils.INIDOMUTILS_IID), proxyHelper);
		}

		public void setNode(nsIDOMNode node) {
			this.node = (nsIDOMNode) XPCOMThreadProxy.createProxy(node, proxyHelper);
		}

		public IStatus run(IProgressMonitor monitor) {
			reset();
			IStatus result = parse(node, monitor);
			return result;
		}

		private IStatus parse(nsIDOMNode node, IProgressMonitor monitor) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			if (node.getNodeType() != nsIDOMNode.ELEMENT_NODE) {
				return Status.OK_STATUS;
			}

			try {
				nsIDOMElement e = (nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
				nsISupportsArray rules = null;
				if (e != null && service != null) {
					rules = service.getCSSStyleRules(e);
				}
				if (rules != null) {
					long numRules = rules.count();
					for (long i = 0; i < numRules; i++) {
						if (monitor.isCanceled())
							return Status.CANCEL_STATUS;
						nsIDOMCSSRule ret = (nsIDOMCSSRule) (rules.getElementAt(i).queryInterface(nsIDOMCSSRule.NS_IDOMCSSRULE_IID));
						parseDOMCSSRule(e, ret);
					}
				}
				String inlineStyle = e.getAttribute("style");
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				if (inlineStyle != null && !inlineStyle.equals("")) {
					parseInlineStyle(e, inlineStyle);
				}
				nsIDOMNodeList list = e.getChildNodes();
				for (int i = 0; i < list.getLength(); i++) {
					parse(list.item(i), monitor);
				}
			} catch (Exception ex) {
				MozIDEUIPlugin.log(ex);
			}

			return Status.OK_STATUS;
		}

		private void parseDOMCSSRule(nsIDOMElement e, nsIDOMCSSRule ret) {
			nsIDOMCSSStyleRule styleRule = (nsIDOMCSSStyleRule) ret.queryInterface(nsIDOMCSSStyleRule.NS_IDOMCSSSTYLERULE_IID);
			String ruleSelectorText = styleRule.getSelectorText();
			nsIDOMCSSStyleDeclaration styleDec = styleRule.getStyle();
			String lineNumber = Long.toString(service.getRuleLine(styleRule));
			String url = styleRule.getParentStyleSheet().getHref();

			for (long j = 0; j < styleDec.getLength(); j++) {
				String name = styleDec.item(j);
				String value = styleDec.getPropertyValue(name);
				CSSProperty cssProperty = new CSSProperty(name, value, url, lineNumber, ruleSelectorText);

				List<nsIDOMElement> ruleProperties = rulesToProperties.get(cssProperty.getURL() + cssProperty.getRule());
				if (ruleProperties == null) {
					ruleProperties = new ArrayList<nsIDOMElement>();
					rulesToProperties.put(cssProperty.getURL() + cssProperty.getRule(), ruleProperties);
				}
				if (!ruleProperties.contains(e)) {
					ruleProperties.add(e);
				}

				String rulesToElementsKey = cssProperty.getURL() + cssProperty.getRule() + cssProperty.getName();
				List<nsIDOMElement> ruleElements = rulesToElements.get(rulesToElementsKey);
				if (ruleElements == null) {
					ruleElements = new ArrayList<nsIDOMElement>();
					rulesToElements.put(rulesToElementsKey, ruleElements);
				}
				ruleElements.add(e);

				Map<nsIDOMElement, String> elementsToRules = cssProperties.get(cssProperty.getName());
				if (elementsToRules == null) {
					elementsToRules = new HashMap<nsIDOMElement, String>();
					cssProperties.put(cssProperty.getName(), elementsToRules);
				}
				elementsToRules.put(e, rulesToElementsKey);
			}
		}

		private void parseInlineStyle(nsIDOMElement e, String inlineStyle) {
			StringTokenizer token = new StringTokenizer(inlineStyle, ";");
			if (token.hasMoreTokens()) {
				while (token.hasMoreTokens()) {
					String cssDec = (String) token.nextToken();
					if (cssDec.indexOf(':') != -1) {
						String name = cssDec.substring(0, cssDec.indexOf(':'));
						name = name.trim();
						String value = cssDec.substring(cssDec.indexOf(':') + 1, cssDec.length());
						value = value.trim();
						CSSProperty cssProperty = new CSSProperty(name, value, "", "", CSSContentProvider.INLINE_STYLE);
						cssProperty.setInline(true);
						cssProperty.setPresent(true);
						cssProperty.setProperty(true);

						Map<nsIDOMElement, String> elementsToRules = cssProperties.get(cssProperty.getName());
						if (elementsToRules == null) {
							elementsToRules = new HashMap<nsIDOMElement, String>();
							cssProperties.put(cssProperty.getName(), elementsToRules);
						}
						elementsToRules.put(e, cssProperty.getURL() + cssProperty.getRule() + cssProperty.getName());
					}
				}
			}
		}

		private void reset() {
			rulesToElements.clear();
			rulesToProperties.clear();
			cssProperties.clear();
		}

	}

	public void add(nsIDOMNode node) {
		if (node.getNodeType() == nsIDOMNode.ELEMENT_NODE) {
			try {
				nsIDOMElement e = (nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
				inIDOMUtils service = (inIDOMUtils) Mozilla.getInstance().getServiceManager().getServiceByContractID("@mozilla.org/inspector/dom-utils;1", inIDOMUtils.INIDOMUTILS_IID);
				nsISupportsArray rules = service.getCSSStyleRules(e);
				long numRules = rules.count();
				nsIDOMCSSStyleDeclaration styleDec = null;
				for (long i = 0; i < numRules; i++) {
					nsIDOMCSSRule ret;
					ret = (nsIDOMCSSRule) (rules.getElementAt(i).queryInterface(nsIDOMCSSRule.NS_IDOMCSSRULE_IID));
					nsIDOMCSSStyleRule styleRule = (nsIDOMCSSStyleRule) ret.queryInterface(nsIDOMCSSStyleRule.NS_IDOMCSSSTYLERULE_IID);
					styleDec = styleRule.getStyle();
					String lineNumber = Long.toString(service.getRuleLine(styleRule));
					String url = styleRule.getParentStyleSheet().getHref();
					for (long j = 0; j < styleDec.getLength(); j++) {
						String name = styleDec.item(j);
						String value = styleDec.getPropertyValue(name);
						CSSProperty cssProperty = new CSSProperty(name, value, url, lineNumber, styleRule.getSelectorText());

						List<nsIDOMElement> ruleProperties = cts.rulesToProperties.get(cssProperty.getURL() + cssProperty.getRule());
						if (ruleProperties == null) {
							ruleProperties = new ArrayList<nsIDOMElement>();
							cts.rulesToProperties.put(cssProperty.getURL() + cssProperty.getRule(), ruleProperties);
						}
						if (!ruleProperties.contains(e)) {
							ruleProperties.add(e);
						}

						String rulesToElementsKey = cssProperty.getURL() + cssProperty.getRule() + cssProperty.getName();
						List<nsIDOMElement> elements = cts.rulesToElements.get(rulesToElementsKey);
						if (elements == null) {
							elements = new ArrayList<nsIDOMElement>();
							cts.rulesToElements.put(rulesToElementsKey, elements);
						}
						elements.add(e);

						Map<nsIDOMElement, String> elementsToRules = cts.cssProperties.get(cssProperty.getName());
						if (elementsToRules == null) {
							elementsToRules = new HashMap<nsIDOMElement, String>();
							cts.cssProperties.put(cssProperty.getName(), elementsToRules);
						}
						elementsToRules.put(e, rulesToElementsKey);
					}
				}
			} catch (Exception e) {
				MozIDEUIPlugin.log(e);
			}
		}
	}

	public void remove(nsIDOMNode node) {
		if (node.getNodeType() == nsIDOMNode.ELEMENT_NODE) {
			removed.add(node);
		}
	}

	public void load(IJobChangeListener listener) {
		nsIDOMNode node = document.getElementsByTagName("HTML").item(0);

		if (cts.getState() == Job.RUNNING) {
			cts.cancel();
			try {
				cts.join();
			} catch (InterruptedException e) {
				// interrupted
			}
		}

		cts.setNode(node);
		cts.setPriority(Job.BUILD);
		cts.addJobChangeListener(listener);
		cts.schedule();
	}

	public void disable() {
		Iterator<SelectionBox> iter = boxes.iterator();
		while (iter.hasNext()) {
			SelectionBox box = iter.next();
			box.hide();
		}
		if (cts != null) {
			cts.cancel();
		}
		boxes.clear();
		on = false;
	}

	public void match(CSSProperty property) {
		if (on) {
			disable();
		} else {
			if (cts.getResult() == null) {
				return;
			}
			if (!property.isRule() && !property.isInline()) {
				List<nsIDOMElement> elements = cts.rulesToElements.get(property.getURL() + property.getRule() + property.getName());
				if (elements != null) {
					Iterator<nsIDOMElement> iter = elements.iterator();
					while (iter.hasNext()) {
						nsIDOMElement e = iter.next();
						Map<nsIDOMElement, String> elementsToRules = cts.cssProperties.get(property.getName());
						if (elementsToRules != null && elementsToRules.get(e) != null) {
							String rule = property.getURL() + property.getRule() + property.getName();
							if (rule.equals(((String) elementsToRules.get(e))) && !removed.contains(e)) {
								SelectionBox box = new SelectionBox(document);
								box.highlight(e, "#00FF00");
								boxes.add(box);
							}
						}
					}
				}
			} else if (!property.isInline()) {
				List<nsIDOMElement> elements = cts.rulesToProperties.get(property.getURL() + property.getRule());
				if (elements != null) {
					Iterator<nsIDOMElement> iter = elements.iterator();
					while (iter.hasNext()) {
						nsIDOMElement e = iter.next();
						if (!removed.contains(e)) {
							SelectionBox box = new SelectionBox(document);
							box.highlight(e, "#00FF00");
							boxes.add(box);
						}
					}
				}
			} else {
				if (!removed.contains(currNode)) {
					SelectionBox box = new SelectionBox(document);
					box.highlight((nsIDOMElement) (currNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID)), "#00FF00");
					boxes.add(box);
				}
			}
			on = true;
		}
	}

	public void addAttribute(nsIDOMNode node) {
		if (node.getNodeType() == nsIDOMNode.ELEMENT_NODE) {
			try {
				nsIDOMElement e = (nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
				String inlineStyle = e.getAttribute("style");
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
								CSSProperty cssProperty = new CSSProperty(name, value, "", "", CSSContentProvider.INLINE_STYLE);
								cssProperty.setInline(true);
								cssProperty.setPresent(true);
								cssProperty.setProperty(true);

								Map<nsIDOMElement, String> elementsToRules = cts.cssProperties.get(cssProperty.getName());
								if (elementsToRules == null) {
									elementsToRules = new HashMap<nsIDOMElement, String>();
									cts.cssProperties.put(cssProperty.getName(), elementsToRules);
								}
								elementsToRules.put(e, cssProperty.getURL() + cssProperty.getRule() + cssProperty.getName());
							}
						}
					}
				}
			} catch (Exception e) {
				MozIDEUIPlugin.log(e);
			}
		}
	}

	public void removeAttribute(nsIDOMElement domElement) {
		add(domElement);
	}

	public void setDocumentContainer(IWebBrowser doc) {
		nsIDOMDocument newDocument = doc.getDocument();

		this.document = (nsIDOMDocument) XPCOMThreadProxy.createProxy(newDocument, proxyHelper);
		if (this.container != null) {
			MozIDEUIPlugin.getDefault().getApplicationEventAdmin().removeEventListener(container, domMutationListener);
		}
		this.container = doc;
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().addEventListener(container, domMutationListener);
	}

	public void setNode(nsIDOMNode node) {
		this.currNode = node;
		currElement = (nsIDOMElement) (currNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
	}

	public nsIDOMDocument getDocument() {
		return document;
	}

	public void dispose() {
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().removeEventListener(container, domMutationListener);
	}
}