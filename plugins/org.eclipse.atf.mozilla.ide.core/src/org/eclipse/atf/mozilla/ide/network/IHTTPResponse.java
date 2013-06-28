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
package org.eclipse.atf.mozilla.ide.network;

import java.net.URL;

public interface IHTTPResponse extends IResponse, IHeaderContainer {

	/**
	 * Status code of the HTTP response (404, 200, ...)
	 * @return
	 */
	String getStatus();

	/**
	 * Returns the payload coming back from in the response
	 * @return
	 */
	Object getBody();

	URL getURL();

}
