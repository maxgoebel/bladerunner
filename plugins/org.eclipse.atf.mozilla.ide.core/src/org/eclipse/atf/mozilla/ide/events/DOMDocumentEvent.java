/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. -ongoing improvements
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.events;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.mozilla.interfaces.nsIDOMDocument;

/**
 * This event object contains context information relevant to event
 * related to DOM documents
 * 
 * @author Gino Bustelo
 */
public class DOMDocumentEvent implements IApplicationEvent {
	protected nsIDOMDocument targetDocument;
	public static final String UNLOAD_EVENT = "org.eclipse.atf.mozilla.ide.events.DOMDocumentEvent.unload";
	public static final String LOAD_EVENT = "org.eclipse.atf.mozilla.ide.events.DOMDocumentEvent.load";

	protected boolean isTop; //true if topmost document, false if document in frame
	private String type;
	private IWebBrowser browser;

	public DOMDocumentEvent(IWebBrowser browser, nsIDOMDocument targetDocument, boolean isTop, String type) {
		this.targetDocument = targetDocument;
		this.isTop = isTop;
		this.type = type;
		this.browser = browser;
	}

	public boolean isTop() {
		return isTop;
	}

	public nsIDOMDocument getTargetDocument() {
		return targetDocument;
	}

	public String getType() {
		return type;
	}

	public IWebBrowser getBrowser() {
		return browser;
	}

}
