/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.netmon.model.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.network.IHTTPRequest;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.mozilla.interfaces.nsIHttpChannel;
import org.mozilla.interfaces.nsIHttpHeaderVisitor;
import org.mozilla.interfaces.nsIInputStream;
import org.mozilla.interfaces.nsIScriptableInputStream;
import org.mozilla.interfaces.nsISeekableStream;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIUploadChannel;
import org.mozilla.interfaces.nsIXMLHttpRequest;
import org.mozilla.xpcom.Mozilla;

/**
 *
 * This class is used to extract the HTTPCall request and response information from the
 * XPCOM couterpart.
 */
public class NetworkFactory {

	private static class MozHeadersVisitor implements nsIHttpHeaderVisitor {
		private Map result = new HashMap();

		public void visitHeader(String headerName, String headerValue) {
			result.put(headerName, headerValue);
		}

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface(this, id);
		}

		public Map getHeaders() {
			return result;
		}
	}

	private static NetworkFactory _instance = new NetworkFactory();

	public static NetworkFactory getInstance() {
		return _instance;
	}

	private NetworkFactory() {
		// singleton
	}

	public IHTTPRequest createHTTPRequest(nsIHttpChannel httpChannel, boolean xhr) {
		//URL of the request
		String urlStr = httpChannel.getURI().getAsciiSpec();
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			MozIDEUIPlugin.log(e);
		}
		//HTTP method used
		String method = httpChannel.getRequestMethod();
		String body = NetworkFactory.extractBody(httpChannel);

		MozHeadersVisitor visitor = new MozHeadersVisitor();
		httpChannel.visitRequestHeaders(visitor);
		Map headers = visitor.getHeaders();
		return new HTTPRequest(url, method, body, headers, xhr);
	}

	public IHTTPRequest createHTTPRequest(nsIHttpChannel httpChannel) {
		return createHTTPRequest(httpChannel, false);
	}

	/*
	 * Currently handling POSTs and GETs
	 */
	private static String extractBody(nsIHttpChannel httpChannel) {
		String method = httpChannel.getRequestMethod();
		if (IHTTPRequest.POST_METHOD.equals(method) || IHTTPRequest.PUT_METHOD.equals(method)) {

			// set the POST body as the body

			//The POST body needs to be read from an InputStream
			nsIScriptableInputStream scriptableIS = null;

			//need it so that we can rewind the stream back
			nsISeekableStream seekableStream = null;
			nsIInputStream is = null;
			try {
				nsIUploadChannel upChannel = (nsIUploadChannel) httpChannel.queryInterface(nsIUploadChannel.NS_IUPLOADCHANNEL_IID);

				is = upChannel.getUploadStream();
				if (is != null) {
					seekableStream = (nsISeekableStream) is.queryInterface(nsISeekableStream.NS_ISEEKABLESTREAM_IID);

					scriptableIS = (nsIScriptableInputStream) Mozilla.getInstance().getComponentManager().createInstanceByContractID("@mozilla.org/scriptableinputstream;1", null, nsIScriptableInputStream.NS_ISCRIPTABLEINPUTSTREAM_IID);

					scriptableIS.init(is);

					//read the entire stream into a StringBuffer
					StringBuffer postBuf = new StringBuffer();
					long count;
					while ((count = scriptableIS.available()) > 0)
						//some left to read{
						postBuf.append(scriptableIS.read(count));

					return postBuf.toString();

				}
			} catch (Exception e) {
				//Any errors here, the POST body will be empty
				MozIDEUIPlugin.log(e);
			} finally {
				//rewind
				if (seekableStream != null) {
					seekableStream.seek(nsISeekableStream.NS_SEEK_SET, 0);
				}
			}
		} else {
			// set the body to the parameters in the URL
			return httpChannel.getURI().getPath();
		}

		return null;
	}

	public HTTPResponse createHTTPResponse(nsIHttpChannel httpChannel) {
		String urlStr = httpChannel.getURI().getAsciiSpec();
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			MozIDEUIPlugin.log(e);
		}
		String status = httpChannel.getResponseStatus() + " " + httpChannel.getResponseStatusText();

		MozHeadersVisitor visitor = new NetworkFactory.MozHeadersVisitor();
		httpChannel.visitResponseHeaders(visitor);
		Map headers = visitor.getHeaders();

		return new MozHTTPResponse(url, status, null, headers);
	}

	/**
	 * This method is called to set the information about the response
	 * side of the HTTP call.
	 * 
	 * @param in the case of an onError handler, need to bypass creating the response object
	 */
	public static void captureResponse(BaseNetworkCall call, nsIXMLHttpRequest xhr, boolean error) {

		try {

			if (!error) {
				nsIHttpChannel httpChannel = (nsIHttpChannel) (xhr.getChannel().queryInterface(nsIHttpChannel.NS_IHTTPCHANNEL_IID));
				captureResponse(call, httpChannel, BaseNetworkCall.STOP);

				//The XHR carries the response text so might as well set it here
				((HTTPResponse) call.response).setBody(xhr.getResponseText());
			} else {
				call.state = BaseNetworkCall.ERROR_STATE;
			}
		} catch (Exception e) {
			call.state = BaseNetworkCall.ERROR_STATE;
		}
		call.addStatusChange(BaseNetworkCall.STOP, System.currentTimeMillis());
	}

	/**
	 * This method is called to set the information about the request
	 * side of the HTTP call.
	 * 
	 * @param httpChannel nsIHTTPChannel representing the call on the XPCOM side
	 */
	public static void captureRequest(BaseNetworkCall call, nsIHttpChannel httpChannel, boolean xhr) {
		call.addStatusChange(BaseNetworkCall.START, System.currentTimeMillis());
		call.state = BaseNetworkCall.ACTIVE_STATE;

		call.request = NetworkFactory.getInstance().createHTTPRequest(httpChannel, xhr);
	}

	/**
	 * This method is called to set the information about the response
	 * side of the HTTP call.
	 * 
	 * @param httpChannel nsIHTTPChannel representing the call on the XPCOM side
	 */
	public static void captureResponse(BaseNetworkCall call, nsIHttpChannel httpChannel, long stateFlag) {

		long endTime = System.currentTimeMillis();

		try {
			call.response = NetworkFactory.getInstance().createHTTPResponse(httpChannel);

			if (httpChannel.getResponseStatus() == 200) {
				call.state = BaseNetworkCall.SUCCESS_STATE;
			} else {
				call.state = BaseNetworkCall.WARNING_STATE;
			}
		} catch (Exception e) {
			call.state = BaseNetworkCall.ERROR_STATE;
		}
		call.addStatusChange(stateFlag, endTime);
	}
}
