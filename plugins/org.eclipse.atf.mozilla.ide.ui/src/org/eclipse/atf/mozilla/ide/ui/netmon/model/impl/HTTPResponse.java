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

import org.eclipse.atf.mozilla.ide.network.IHTTPResponse;
import org.eclipse.atf.mozilla.ide.network.IHeaderContainer;

/**
 * 
 * @author Gino Bustelo
 *
 */
public class HTTPResponse implements IHTTPResponse, IHeaderContainer {

	private String status;

	private Map headers;

	private Object body;

	private URL url;

	public HTTPResponse(URL url, String status, Object body, Map headers) {
		this.headers = headers;
		this.body = body;
		this.url = url;
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public URL getURL() {
		return url;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;

	}

	public String toString() {
		return "HTTPResponse [headers=" + headers + ", status=" + status + ", url=" + url + "]";
	}
}
