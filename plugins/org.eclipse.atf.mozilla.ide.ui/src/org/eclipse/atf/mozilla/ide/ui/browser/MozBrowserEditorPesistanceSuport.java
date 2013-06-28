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

package org.eclipse.atf.mozilla.ide.ui.browser;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class MozBrowserEditorPesistanceSuport implements IPersistableElement, IElementFactory{

	protected static final String ID = "org.eclipse.atf.mozilla.ide.ui.browser.memento";
	protected static final String MEMENTO_URL = "url";
	
	protected String url = null;
	
	public void setURL( String url ){
		this.url = url;
	}
	
	public void saveState(IMemento memento){
		memento.putString(MEMENTO_URL, url);		
	}

	public IAdaptable createElement(IMemento memento) {
		String persistedURL = null;
		

		try {
			persistedURL = memento.getString(MEMENTO_URL);
		} catch (Exception e) {
			//error processing memento
		}

		return new MozBrowserEditorInput(persistedURL);
	}

	public String getFactoryId() {
		return ID;
	}

}
