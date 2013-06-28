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
package org.eclipse.atf.mozilla.ide.ui.netmon.model.impl;

import java.net.URL;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.network.IHTTPRequest;
import org.eclipse.atf.mozilla.ide.network.IHeaderContainer;

/**
 * 
 * @author Gino Bustelo
 *
 */
public class HTTPRequest implements IHTTPRequest, IHeaderContainer {

	//URL of the http request
	private URL url;

	//HTTP method value
	private String method;

	//Body content of the request
	private String body;

	private Map headers;

	//marks if this request is started using XHR
	protected boolean xhr;

	public HTTPRequest(URL url, String method, String body, Map headers, boolean isXhr) {
		this.url = url;
		this.method = method;
		this.body = body;
		this.headers = headers;
		this.xhr = isXhr;
	}

	public String getBody() {
		return body;
	}

	public String getMethod() {
		return method;
	}

	public URL getURL() {
		return url;
	}

	public boolean isXHR() {
		return xhr;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String toString() {
		return "HTTPRequest [body=" + body + ", headers=" + headers + ", method=" + method + ", url=" + url + ", xhr=" + xhr + "]";
	}
}
