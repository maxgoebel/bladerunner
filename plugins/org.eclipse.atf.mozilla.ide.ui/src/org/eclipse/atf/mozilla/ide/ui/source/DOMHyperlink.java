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
package org.eclipse.atf.mozilla.ide.ui.source;

import org.eclipse.atf.mozilla.ide.ui.common.configs.HTMLDOMSourceViewerConfiguration;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * 
 * Represents a dom hyperlink in the source that changes
 * the selection of the browser
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class DOMHyperlink implements IHyperlink {
	
	private IRegion region;
	private String urlLabel;
	private HTMLDOMSourceViewerConfiguration config;
	public DOMHyperlink( IRegion region, String url, HTMLDOMSourceViewerConfiguration config ){
		this.region = region;
		this.urlLabel = url;
		this.config = config;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getHyperlinkText() {
		return urlLabel;
	}

	public String getTypeLabel() {
		return null;
	}

	public void open() {
		config.changeSelection(region);
	}

}
