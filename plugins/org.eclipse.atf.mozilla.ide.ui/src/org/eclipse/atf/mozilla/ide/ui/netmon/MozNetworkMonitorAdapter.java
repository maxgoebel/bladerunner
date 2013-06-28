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

package org.eclipse.atf.mozilla.ide.ui.netmon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.netmon.model.impl.BaseNetworkCall;
import org.eclipse.atf.mozilla.ide.ui.netmon.model.impl.NetworkEventsPublisher;
import org.eclipse.atf.mozilla.ide.ui.netmon.model.impl.NetworkFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.browser.Browser;
import org.mozilla.interfaces.nsIChannel;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIHttpChannel;
import org.mozilla.interfaces.nsIInterfaceRequestor;
import org.mozilla.interfaces.nsILoadGroup;
import org.mozilla.interfaces.nsIObserver;
import org.mozilla.interfaces.nsIObserverService;
import org.mozilla.interfaces.nsIRequest;
import org.mozilla.interfaces.nsIRequestObserver;
import org.mozilla.interfaces.nsISocketTransport;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIURI;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebProgress;
import org.mozilla.interfaces.nsIWebProgressListener;
import org.mozilla.interfaces.nsIXMLHttpRequest;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;

/**
 * This class is used to listen to all the network communication done by the
 * browser.
 * 
 * Each Browser Editor that is opened in the Eclipse environment will have its
 * own instance of this class.
 * 
 * @author Gino Bustelo
 *
 */
public class MozNetworkMonitorAdapter implements nsIObserver, nsIWebProgressListener {

	protected static final String HTTP_ON_MODIFY_REQUEST_TOPIC = "http-on-modify-request";
	protected static final String HTTP_ON_EXAMINE_RESPONSE_TOPIC = "http-on-examine-response";

	/*
	 * This inner class is used to track the response of an XHR Call. It is implemented
	 * as an inner class because it is meant to be used only internally and it has
	 * access to the Adapter's (the host class) handler methods.
	 *
	 * FOR SYNC CALLS AND ASYNCH CALLS:
	 * It straps itself to the nsIXMLHTMLRequest as a nsIDOMEventListener to the
	 * onerror, onload and readystatechange handlers (like in JavaScript). 
	 * 
	 * By looking at the nsXMLHTTPRequest.cpp source (http://lxr.mozilla.org/mozilla/source/extensions/xmlextras/base/src/nsXMLHttpRequest.cpp#1442)
	 * the order of notifications when the call is completed is as follows:
	 * 
	 * - readystatechange handler (only async)
	 * - onload, onerror and onprogress handlers (JavaScript)
	 * - nsIDOMEventListener for load and error
	 */
	protected class XHRHandler implements nsIDOMEventListener {

		// Mozilla XHR object
		protected nsIXMLHttpRequest xhr;
		private String name;

		public XHRHandler(nsIXMLHttpRequest xhr, String name) {
			// cache the xhr to be able to query on ready-state changes
			this.xhr = xhr;
			this.name = name;

			nsIDOMEventTarget eventListener = (nsIDOMEventTarget) xhr.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
			eventListener.addEventListener(ERROR_TYPE, this, false);
			eventListener.addEventListener(LOAD_TYPE, this, false);
			eventListener.addEventListener(READYSTATE_TYPE, this, false);
		}

		protected final static int COMPLETE_READYSTATE = 4;
		protected final static int OK_STATUS = 200;

		protected static final String LOAD_TYPE = "load";
		protected static final String ERROR_TYPE = "error";
		protected static final String READYSTATE_TYPE = "readystatechange";

		/*
		 *  (non-Javadoc)
		 * @see org.mozilla.xpcom.nsIDOMEventListener#handleEvent(org.mozilla.xpcom.nsIDOMEvent)
		 */
		public void handleEvent(nsIDOMEvent event) {
			if (xhr == null)
				return;

			if (xhr.getReadyState() != COMPLETE_READYSTATE) {
				return;
			}

			//this is always called after the XHR is complete (either an error or load)
			try {
				// check the type of event
				handleXHRResponse(xhr, ERROR_TYPE.equals(event.getType()), name);
			}
			/*
			 * this outer try/finally is to protect against exceptions in the Eclipse side
			 * stopping the proper execution of the Web Application.
			 */
			finally {
				nsIDOMEventTarget eventListener = (nsIDOMEventTarget) xhr.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
				eventListener.removeEventListener(ERROR_TYPE, this, false);
				eventListener.removeEventListener(READYSTATE_TYPE, this, false);
				xhr = null;
			}

		}

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface(this, id);
		}
	}

	/*
	 * Collects all the ongoing and finished network calls.
	 */
	protected Map<String, BaseNetworkCall> ongoingCalls = new HashMap<String, BaseNetworkCall>();
	/*
	 * Used for tracing coming events to properly update network calls
	 */
	private Map<String, List> ongoingCallsEvents = new HashMap<String, List>();

	/*
	 * This is the context that this observer cares about. The nsIObserver interface is
	 * added into a global topic, so we need some sort of context to filter out the relevant
	 * events.
	 */
	protected IWebBrowser browserContext = null;
	private NetworkEventsPublisher eventsPublisher;

	public MozNetworkMonitorAdapter(IWebBrowser browserContext) {
		this.browserContext = browserContext;
		eventsPublisher = new NetworkEventsPublisher(browserContext);
	}

	private boolean connected = false;

	/**
	 * hook to mozilla
	 * 
	 * there are two listeners,
	 * 
	 * webprogresslistener gets everything but not the XHR.
	 * obsever to request topic is used to get to XHR calls.
	 */
	public void connect() {
		if (!connected) {

			// web progress connection
			nsIWebBrowser mozBrowser = (nsIWebBrowser) browserContext.getAdapter(nsIWebBrowser.class);
			mozBrowser.addWebBrowserListener(this, nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);

			// observer connection
			nsIObserverService observerService = (nsIObserverService) Mozilla.getInstance().getServiceManager().getServiceByContractID("@mozilla.org/observer-service;1", nsIObserverService.NS_IOBSERVERSERVICE_IID);
			observerService.addObserver(this, HTTP_ON_MODIFY_REQUEST_TOPIC, false);
			observerService.addObserver(this, HTTP_ON_EXAMINE_RESPONSE_TOPIC, false);

			connected = true;
		}
	}

	/**
	 * unhook from mozilla
	 */
	public void disconnect() {
		if (connected) {

			// web progress connection
			nsIWebBrowser mozBrowser = (nsIWebBrowser) browserContext.getAdapter(nsIWebBrowser.class);
			Browser swtBrowser = (Browser) browserContext.getAdapter(Browser.class);

			if (swtBrowser != null && (!swtBrowser.isDisposed())) {
				mozBrowser.removeWebBrowserListener(this, nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);

				// observer connection
				nsIObserverService observerService = (nsIObserverService) Mozilla.getInstance().getServiceManager().getServiceByContractID("@mozilla.org/observer-service;1", nsIObserverService.NS_IOBSERVERSERVICE_IID);
				observerService.removeObserver(this, HTTP_ON_MODIFY_REQUEST_TOPIC);
				observerService.removeObserver(this, HTTP_ON_EXAMINE_RESPONSE_TOPIC);
			}

			connected = false;
		}
	}

	/**
	 * There is one observer registered for each browser open. Since the observers are global, need to check if the notification
	 * is within the context of this listener.
	 */
	public void observe(nsISupports subject, String topic, String data) {

		try {
			// need to filter for this browser context
			nsIRequest request = (nsIRequest) subject.queryInterface(nsIRequest.NS_IREQUEST_IID);
			//MozIDEUIPlugin.debug( "MozNetworkMonitorAdaptre:observe() -- " + request.getName() );

			// top window for the request
			nsIWebProgress webProgress = null;

			try {
				nsILoadGroup loadGroup = request.getLoadGroup();
				if (loadGroup != null) {
					nsIRequestObserver observer = loadGroup.getGroupObserver();
					if (observer != null)
						webProgress = (nsIWebProgress) observer.queryInterface(nsIWebProgress.NS_IWEBPROGRESS_IID);
				}
			} catch (Exception e) {
			}

			if (webProgress == null) {
				// first try to get webProgress failed, try a different way
				nsIChannel channel = (nsIChannel) request.queryInterface(nsIChannel.NS_ICHANNEL_IID);
				webProgress = (nsIWebProgress) channel.getNotificationCallbacks().getInterface(nsIWebProgress.NS_IWEBPROGRESS_IID);
			}

			nsIDOMWindow requestOwnerWindow = webProgress.getDOMWindow().getTop();

			nsIDOMWindow contextWindow = (nsIDOMWindow) browserContext.getAdapter(nsIDOMWindow.class);

			if (requestOwnerWindow != contextWindow) {
				return; // ignore this notification
			}

			if (HTTP_ON_MODIFY_REQUEST_TOPIC.equals(topic)) {
				inc(request.getName(), "observe");
				observeRequest(request);
			}

			else if (HTTP_ON_EXAMINE_RESPONSE_TOPIC.equals(topic)) {
				try {
					observeResponse(request);
				} catch (Exception e) {
				}
				dec(request.getName(), "observe", false);
			}
		} catch (Exception e) {
			// errors in QI are ignored
			MozIDEUIPlugin.log(e);
		}
	}

	/*
	 * Handle the request side for the observer
	 * 
	 */
	protected void observeRequest(nsIRequest request) {
		//MozIDEUIPlugin.debug( "MozNetworkMonitorAdaptre:observeRequest() -- " + request.getName() );

		// LOAD_BACKGROUND seems to be set for all XHR calls
		if ((request.getLoadFlags() & nsIChannel.LOAD_BACKGROUND) != 0) {

			//			MozIDEUIPlugin.debug( "XHRMonitor:handleRequest() -- status flags<"+Long.toHexString(httpChannel.getStatus())+">." );

			try {

				nsIHttpChannel httpChannel = (nsIHttpChannel) request.queryInterface(nsIHttpChannel.NS_IHTTPCHANNEL_IID);

				/*
				 * In nsIXMLHTTPRequest.cpp line 1593
				 * mChannel->SetNotificationCallbacks(this); //instance of XHR sets itself as notificationCallback
				 * 
				 * This means that I can QI to nsIXMLHTTPRequest
				 */
				nsIInterfaceRequestor intReq = httpChannel.getNotificationCallbacks();
				nsIXMLHttpRequest xhr = (nsIXMLHttpRequest) intReq.getInterface(nsIXMLHttpRequest.NS_IXMLHTTPREQUEST_IID);

				handleXHRRequest(xhr, request.getName());

				// MozIDEUIPlugin.debug( "XHRMonitor:handleRequest() -- xhr" );
				return;
			} catch (XPCOMException e) {
			}
		}

		try {
			nsIHttpChannel httpRequest = (nsIHttpChannel) request.queryInterface(nsIHttpChannel.NS_IHTTPCHANNEL_IID);

			BaseNetworkCall call = new BaseNetworkCall();

			NetworkFactory.captureRequest(call, httpRequest, false);

			ongoingCalls.put(request.getName(), call);
		} catch (Exception e) {
			MozIDEUIPlugin.log(e);
		}

	}

	/*
	 * Handle the response side for the observer
	 */
	protected void observeResponse(nsIRequest request) {

		//MozIDEUIPlugin.debug( "MozNetworkMonitorAdaptre:observeResponse() -- " + request.getName() );

		// ignore xhr calls because they are handled by the XHR Handler
		if ((request.getLoadFlags() & nsIChannel.LOAD_BACKGROUND) != 0) {

			//MozIDEUIPlugin.debug( "XHRMonitor:observe() -- POSSIBLE AJAX attaching webProgressListener" );
			//			MozIDEUIPlugin.debug( "XHRMonitor:handleRequest() -- status flags<"+Long.toHexString(httpChannel.getStatus())+">." );

			try {

				nsIHttpChannel httpChannel = (nsIHttpChannel) request.queryInterface(nsIHttpChannel.NS_IHTTPCHANNEL_IID);

				/*
				 * In nsIXMLHTTPRequest.cpp line 1593
				 * mChannel->SetNotificationCallbacks(this); //instance of XHR sets itself as notificationCallback
				 * 
				 * This means that I can QI to nsIXMLHTTPRequest
				 */
				nsIInterfaceRequestor intReq = httpChannel.getNotificationCallbacks();

				// can we QI to XHR?
				intReq.getInterface(nsIXMLHttpRequest.NS_IXMLHTTPREQUEST_IID);

				return;
			} catch (XPCOMException e) {
			}
		}

		BaseNetworkCall call = (BaseNetworkCall) ongoingCalls.get(request.getName());
		if (call != null) {
			nsIHttpChannel httpRequest = (nsIHttpChannel) request.queryInterface(nsIHttpChannel.NS_IHTTPCHANNEL_IID);
			NetworkFactory.captureResponse(call, httpRequest, nsISocketTransport.STATUS_RECEIVING_FROM);
		}
	}

	/**
	 * An XHR request has been identified, grab the information, set
	 * handlers to sniff response, and add to call list.
	 * 
	 * @param xhr
	 */
	protected void handleXHRRequest(nsIXMLHttpRequest xhr, String name) {
		try {
			BaseNetworkCall call = new BaseNetworkCall();

			nsIHttpChannel httpChannel = (nsIHttpChannel) (xhr.getChannel().queryInterface(nsIHttpChannel.NS_IHTTPCHANNEL_IID));
			NetworkFactory.captureRequest(call, httpChannel, true);

			// hook to listen to the xhr response
			new XHRHandler(xhr, name);

			// map the call in progress with the xhr object
			ongoingCalls.put(name, call);
			inc(name, "xhr");
		} catch (Exception e) {
			MozIDEUIPlugin.log(new Status(IStatus.ERROR, MozIDEUIPlugin.PLUGIN_ID, "Failed handleXHRRequest: " + e.getMessage(), e));
		}
	}

	/**
	 * The response for an XHR call has been detected. Grap the call in progress
	 * from the call list, capture the response information, and send an update
	 * message.
	 * 
	 * @param xhr
	 * @param error true if the handler detected an error (onError handler)
	 */
	protected void handleXHRResponse(nsIXMLHttpRequest xhr, boolean error, String name) {

		BaseNetworkCall call = (BaseNetworkCall) ongoingCalls.get(name);

		//this notifies the call list that it needs to notify its listeners that a call
		//object has changed
		NetworkFactory.captureResponse(call, xhr, error);
		eventsPublisher.postNetworkEvent(NetworkEventsPublisher.NETWORK_EVENT, call.getStartTime(), call.getEndTime(), call);
		dec(name, "xhr", true);
	}

	/**
	 * nsIWebProgressListener seem to be consistent in that there is a START and
	 * a STOP for every request. 
	 * 
	 * There are several issues:
	 * - It does not fire for XHR request
	 * - Image request come in as imgIRequest instances which have no access to
	 *   request/response headers.
	 *   
	 * Note:
	 * 	The Start is ignored because we are using the observer to detect start of
	 * request and possible response. The Stop remains because there might be
	 * cased that the Response topic is not fired for the observer and the STOP here
	 * does, giving the opportunity of capturing the response information.
	 */
	public void onStateChange(nsIWebProgress webProgress, nsIRequest request, long stateFlags, long status) {
		if ((stateFlags & STATE_IS_REQUEST) == 0) // ignore all non-request state changes, e.g. Window or Document
			return;

		if (request.getName().startsWith("about:")) // ignore about:document-onload-blocker
			return;

		//only care about HTTP request (should care about other requests such as file://)
		//nsIHttpChannel httpChannel = (nsIHttpChannel)request.queryInterface( nsIHttpChannel.NS_IHTTPCHANNEL_IID );
		if ((stateFlags & STATE_START) != 0) {
			inc(request.getName(), "onStateChange");
			//	handleStartRequest(webProgress, request, stateFlags, status);
		} else if ((stateFlags & STATE_STOP) != 0) {
			handleStopRequest(webProgress, request, stateFlags, status);
			dec(request.getName(), "onStateChange", true);
		}
	}

	/*
	 * handle the start for a request
	 * 
	 * capture the request information, add to list with a mapping to the nsIRequest instance so that it can
	 * be retrieved once the response is detected.
	 * 
	 * NOT USED
	 */
	protected void handleStartRequest(nsIWebProgress webProgress, nsIRequest request, long stateFlags, long status) {
		// MozIDEUIPlugin.debug( "START: " + request.getName() );

		BaseNetworkCall call = new BaseNetworkCall();

		// handle HTTP requests
		try {
			nsIHttpChannel httpRequest = (nsIHttpChannel) request.queryInterface(nsIHttpChannel.NS_IHTTPCHANNEL_IID);
			NetworkFactory.captureRequest(call, httpRequest, false);
		} catch (Exception e) {

		}

		ongoingCalls.put(request.getName(), call);
	}

	/*
	 * handle the stop for a request
	 */
	protected void handleStopRequest(nsIWebProgress webProgress, nsIRequest request, long stateFlags, long status) {
		BaseNetworkCall call = (BaseNetworkCall) ongoingCalls.get(request.getName()); // no need to keep around the map
		if (call == null) {
			//System.out.println("STOP request, call not found: " + request.getName() + " (" + stateFlags + " " + status + ")");
			return;
		}

		try {
			nsIHttpChannel httpRequest = (nsIHttpChannel) request.queryInterface(nsIHttpChannel.NS_IHTTPCHANNEL_IID);
			NetworkFactory.captureResponse(call, httpRequest, BaseNetworkCall.STOP);
		} catch (XPCOMException e) {
			// not an nsIHttpChannel event.
		}
	}

	public void onLocationChange(nsIWebProgress webProgress, nsIRequest request, nsIURI location) {
	}

	public void onProgressChange(nsIWebProgress webProgress, nsIRequest request, int curSelfProgress, int maxSelfProgress, int curTotalProgress, int maxTotalProgress) {
	}

	public void onSecurityChange(nsIWebProgress webProgress, nsIRequest request, long state) {
	}

	public void onStatusChange(nsIWebProgress webProgress, nsIRequest request, long status, String message) {
		BaseNetworkCall call = (BaseNetworkCall) ongoingCalls.get(request.getName());
		if (call != null) {
			call.addStatusChange(status, System.currentTimeMillis());
		}
	}

	public nsISupports queryInterface(String uuid) {
		return Mozilla.queryInterface(this, uuid);
	}

	/*
	 * Used for tracing coming events to properly update network calls
	 */
	private void inc(String key, String token) {
		List r = ongoingCallsEvents.get(key);
		if (r == null) {
			r = new ArrayList();
			ongoingCallsEvents.put(key, r);
		}

		r.add(token);
		//System.out.println(key + " + " + token + " = " + r);
	}

	/*
	 * Used for tracing coming events to properly update network calls
	 */
	private void dec(String key, String token, boolean removeItem) {
		List i = ongoingCallsEvents.get(key);
		if (i == null) {
			throw new IllegalStateException();
		}

		boolean result = i.remove(token);
		if (!result) {
			//System.out.println("ERROR: remove unexisting token: " + token + " (" + key + ")");
		}

		if ((i.size() <= 1) && removeItem) {
			ongoingCallsEvents.remove(key);
			BaseNetworkCall call = (BaseNetworkCall) ongoingCalls.remove(key);
			if (call != null) {
				eventsPublisher.postNetworkEvent(NetworkEventsPublisher.NETWORK_EVENT, call.getStartTime(), call.getEndTime(), call);
			}
		}
		//System.out.println(key + " - " + token + " = " + i + "(total " + ongoingCallsEvents.size() + " left)\n\t+" + ongoingCallsEvents);
	}
}
