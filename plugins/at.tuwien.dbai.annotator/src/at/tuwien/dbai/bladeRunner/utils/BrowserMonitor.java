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
package at.tuwien.dbai.bladeRunner.utils;

import java.util.Observable;

import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;

/**
 * BrowserMonitor.java
 * 
 * 
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Feb 23, 2011
 */
public class BrowserMonitor extends Observable {

	boolean isInit = false;

	/**
	 * Constructor.
	 */
	public BrowserMonitor() {

	}

	public void fireDocumentLoaded() {
		if (!isInit) {
			// DocWrapUIUtils.getWrapperEditor().getWrapperBuilder().attach();
			isInit = true;
		}
		setChanged();
		notifyObservers();
	}
}
