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
package org.eclipse.atf.mozilla.ide.events;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;

public class ApplicationEvent implements ITimedEvent {

	private long start;
	private long length;
	private ITimedEvent parent;
	private Object data;
	private String type;
	private IWebBrowser browser;

	public ApplicationEvent(IWebBrowser browser, String type, ITimedEvent parent, Object data, long start, long length) {
		this.type = type;
		this.browser = browser;
		this.parent = parent;
		this.data = data;
		this.start = start;
		this.length = length;
	}

	public void setLength(long length) {
		length = length;
	}

	public ApplicationEvent(IWebBrowser browser, String type, ITimedEvent parent, Object data) {
		this(browser, type, parent, data, System.currentTimeMillis(), 0);
	}

	public Object getData() {
		return data;
	}

	public ITimedEvent getParent() {
		return parent;
	}

	public long getStartTime() {
		return start;
	}

	public long getLength() {
		return length;
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return "ApplicationEvent [data=" + data + ", length=" + length + ", parent=" + parent + ", start=" + start + ", type=" + type + "]";
	}

	public IWebBrowser getBrowser() {
		return browser;
	}

}
