/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.prip.model.project.document;

import java.util.List;

import org.w3c.dom.Document;

import at.tuwien.prip.core.model.document.example.SelectionExample;
import at.tuwien.prip.core.model.document.views.DocumentView;

/**
 * DocumentEntry.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Feb 16, 2011
 */
public class DocumentEntry {

	private Document cachedJavaDOM;
	
//	private List<DocumentView> views;
	
	private String uri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<SelectionExample> getUserSelections() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCachedJavaDOM(Document document) {
		cachedJavaDOM = document;
	}

	public Document getCachedJavaDOM() {
		return cachedJavaDOM;
	}

	public List<DocumentView> getViews() {
		return views;
	}

	public void setViews(List<DocumentView> views) {
		this.views = views;
	}
	
}
