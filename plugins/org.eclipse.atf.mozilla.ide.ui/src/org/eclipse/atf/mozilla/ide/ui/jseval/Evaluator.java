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
package org.eclipse.atf.mozilla.ide.ui.jseval;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.atf.mozilla.ide.core.MozideCorePlugin;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.IJSValue;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.JSTypeFactory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentEvent;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMHTMLInputElement;
import org.mozilla.interfaces.nsIDOMHTMLScriptElement;
import org.mozilla.interfaces.nsIDOMHTMLTextAreaElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMText;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;

public class Evaluator {

	protected static final String EVAL_READY = "ATF_EVAL_READY";
	protected static final String EVAL_RECONNECT = "ATF_EVAL_RECONNECT";
	protected static final String EVAL_SET = "ATF_EVAL_SET";
	protected static final String EVAL_DONE = "ATF_EVAL_DONE";
	protected static final String EVAL_ERROR = "ATF_EVAL_ERROR";

	protected static final String PAGE_SCRIPT_URL = "/js/ATF_Eval.js";

	protected nsIDOMEventListener listener = new nsIDOMEventListener() {

		public void handleEvent(nsIDOMEvent event) {
			if (EVAL_READY.equals(event.getType())) {
				handleEvalReady(event);

			} else if (EVAL_DONE.equals(event.getType())) {
				handleEvalDone(event);

			} else if (EVAL_ERROR.equals(event.getType())) {
				handleEvalError(event);

			}

		}

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface(this, id);
		}

	};

	protected nsIDOMDocument document = null;
	protected nsIDOMHTMLInputElement evalIn = null;

	/*
	 * Initialize the document by injecting the script needed on the page in order to do
	 * interactive evals.
	 */
	public void init(nsIDOMDocument document) {
		//avoid multiple inits on same document
		if (this.document == document)
			return;

		this.document = document;

		nsIDOMEventTarget docTarget = (nsIDOMEventTarget) document.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
		docTarget.addEventListener(EVAL_READY, listener, true);
		docTarget.addEventListener(EVAL_DONE, listener, true);
		docTarget.addEventListener(EVAL_ERROR, listener, true);

		//setup the document with the script needed for eval
		try {

			//check if script was added already
			nsIDOMNode evalContainer = document.getElementById(MozideCorePlugin.ATF_INTERNAL + "_EVAL");

			if (evalContainer == null) {
				//inject script
				nsIDOMNode head = document.getElementsByTagName("HEAD").item(0);
				nsIDOMHTMLScriptElement script = (nsIDOMHTMLScriptElement) (document.createElement("SCRIPT").queryInterface(nsIDOMHTMLScriptElement.NS_IDOMHTMLSCRIPTELEMENT_IID));

				//tag it so that it is hidden from the tools
				script.setAttribute("class", MozIDEUIPlugin.ATF_INTERNAL);

				//read script into buffer because it needs to be written to the document
				//a file:// ref will violate browser security
				StringBuffer buffer = new StringBuffer();
				Reader reader = null;
				InputStream io = null;
				try {

					io = FileLocator.openStream(MozIDEUIPlugin.getDefault().getBundle(), new Path(PAGE_SCRIPT_URL), false);

					reader = new InputStreamReader(io);

					char readbuff[] = new char[1000];

					int readCount;
					while ((readCount = reader.read(readbuff)) != -1) {
						buffer.append(readbuff, 0, readCount);
					}
				} catch (Exception e) {
					MozIDEUIPlugin.log(e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (Exception e) {
							MozIDEUIPlugin.log(e);
						}
					}

					if (io != null) {
						try {
							io.close();
						} catch (Exception e) {
							MozIDEUIPlugin.log(e);
						}
					}
				}
				nsIDOMText scriptContent = document.createTextNode(buffer.toString());
				script.appendChild(scriptContent);

				head.appendChild(script);

				//at this point the script will execute and signal back when ready
			} else {
				//since the script is already in the page, we need to send a signal to reconnect with it
				nsIDOMDocumentEvent docEvent = (nsIDOMDocumentEvent) document.queryInterface(nsIDOMDocumentEvent.NS_IDOMDOCUMENTEVENT_IID);

				nsIDOMEvent event = docEvent.createEvent("Event");
				event.initEvent(EVAL_RECONNECT, false, false);

				//setting the container div as the target
				nsIDOMEventTarget target = (nsIDOMEventTarget) evalContainer.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
				target.dispatchEvent(event);
			}
		} catch (Exception e) {
			MozIDEUIPlugin.log(e);
		}
	}

	private IJSValue evalRet = null;

	public IJSValue evaluate(String expression) {

		//clear the previous eval return
		evalRet = null;

		evalIn.setValue(expression);

		//send event to kick start eval
		nsIDOMDocumentEvent docEvent = (nsIDOMDocumentEvent) document.queryInterface(nsIDOMDocumentEvent.NS_IDOMDOCUMENTEVENT_IID);

		nsIDOMEvent event = docEvent.createEvent("Event");
		event.initEvent(EVAL_SET, false, false);

		//setting the target as evalIn tells the JavaScript to do the eval from the expression set on the input element
		nsIDOMEventTarget target = (nsIDOMEventTarget) evalIn.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
		target.dispatchEvent(event);

		//since this is all single threaded through the UI thread, control is passed to the page side
		//and returns back to the Java side handler which sets evalRet. At the end, it all returns
		//to this point.

		return evalRet;
	}

	public IJSValue evaluate(nsIDOMNode node, String expression) {
		//clear the previous eval return
		evalRet = null;

		//expression relative to the node
		evalIn.setValue(expression);

		//send event to kick start eval
		nsIDOMDocumentEvent docEvent = (nsIDOMDocumentEvent) document.queryInterface(nsIDOMDocumentEvent.NS_IDOMDOCUMENTEVENT_IID);

		nsIDOMEvent event = docEvent.createEvent("Event");
		event.initEvent(EVAL_SET, false, false);

		//setting the input node as the target tells the JavaScript to do an eval with the target node as context
		nsIDOMEventTarget target = (nsIDOMEventTarget) node.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
		target.dispatchEvent(event);

		//since this is all single threaded through the UI thread, control is passed to the page side
		//and returns back to the Java side handler which sets evalRet. At the end, it all returns
		//to this point.
		return evalRet;
	}

	public boolean isReady() {
		return evalIn != null;
	}

	protected void handleEvalReady(nsIDOMEvent event) {
		evalIn = (nsIDOMHTMLInputElement) event.getTarget().queryInterface(nsIDOMHTMLInputElement.NS_IDOMHTMLINPUTELEMENT_IID);

		//no longer need this listener
		event.getCurrentTarget().removeEventListener(EVAL_READY, listener, true);
	}

	protected void handleEvalDone(nsIDOMEvent event) {
		//the target of this event is where the eval result is placed
		nsIDOMHTMLTextAreaElement evalOut = (nsIDOMHTMLTextAreaElement) event.getTarget().queryInterface(nsIDOMHTMLTextAreaElement.NS_IDOMHTMLTEXTAREAELEMENT_IID);

		//parse the respose to the eval which is encoded in xml in the value of the event target
		//the returned represents only a single level of properties in the case of an Object
		evalRet = JSTypeFactory.getInstance().create(evalOut.getValue());
	}

	protected void handleEvalError(nsIDOMEvent event) {
		//the target of this event is where the eval result is placed
		nsIDOMHTMLTextAreaElement evalOut = (nsIDOMHTMLTextAreaElement) event.getTarget().queryInterface(nsIDOMHTMLTextAreaElement.NS_IDOMHTMLTEXTAREAELEMENT_IID);

		//parse the respose to the eval which is encoded in xml in the value of the event target
		//the returned represents an error during the eval
		evalRet = JSTypeFactory.getInstance().create(evalOut.getValue());
	}

}
