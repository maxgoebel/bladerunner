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
package org.eclipse.atf.mozilla.ide.common;

public class WebBrowserType {

	private String type = "";
	private String version = "";

	public WebBrowserType(String type, String version) {
		this.type = type;
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

}
