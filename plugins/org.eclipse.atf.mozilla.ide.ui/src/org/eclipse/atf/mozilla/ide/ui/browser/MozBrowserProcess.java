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

package org.eclipse.atf.mozilla.ide.ui.browser;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.mozilla.interfaces.nsIBaseWindow;
import org.mozilla.interfaces.nsIWebBrowser;

/**
 * Implements an IProcess which is associated with a MozillaBrowser.
 * Watches for changes which imply that the original debugging
 * session is no longer active: closing of the browser or changing
 * to another page.  Also able to terminate the browser by triggering
 * a close event which the browser's container (e.g. an EditorPart)
 * will handle.
 * 
 * Could track new browser's spawned by the original browser someday,
 * if that is helpful.
 * 
 * @author peller
 *
 */
public class MozBrowserProcess extends PlatformObject implements IProcess {
	private IWebBrowser _webBrowser;
	private Browser _browser;
	private CloseWindowListener _closeListener;
	private DisposeListener _disposeListener;
	private ILaunch _launch;
	private int _exitValue = 0;
	private boolean _terminated;

	public MozBrowserProcess(IWebBrowser webBrowser, ILaunch launch) {
		_webBrowser = webBrowser;
		_browser = (Browser) webBrowser.getAdapter(Browser.class);
		_launch = launch;
		launch.addProcess(this);
		fireCreationEvent();

		_closeListener = new CloseWindowListener() {
			public void close(WindowEvent event) {
				processTermination();
			}
		};

		_browser.addCloseWindowListener(_closeListener);

		_disposeListener = new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				processTermination();
			}
		};

		_browser.addDisposeListener(_disposeListener);
	}

	public boolean canTerminate() {
		return !isTerminated();
	}

	public boolean isTerminated() {
		return _terminated;
	}

	public void terminate() throws DebugException {
		if (isTerminated())
			return;

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Browser browser = _browser;
				processTermination();
				browser.dispose();
			}
		});
	}

	protected void processTermination() {
		if (_browser != null && !_browser.isDisposed()) {
			if (_closeListener != null)
				_browser.removeCloseWindowListener(_closeListener);
		}
		fireTerminateEvent();
		_browser = null;
		_closeListener = null;

		_terminated = true;
	}

	public Object getAdapter(Class adapter) {
		if (adapter.equals(IProcess.class)) {
			return this;
		}
		if (adapter.equals(IDebugTarget.class)) {
			ILaunch launch = getLaunch();
			IDebugTarget[] targets = launch.getDebugTargets();
			for (int i = 0; i < targets.length; i++) {
				if (this.equals(targets[i].getProcess())) {
					return targets[i];
				}
			}
			return null;
		}
		if (nsIBaseWindow.class.equals(adapter)) {
			return (nsIBaseWindow) ((nsIWebBrowser) _browser.getWebBrowser()).queryInterface(nsIBaseWindow.NS_IBASEWINDOW_IID);
		}
		if (nsIWebBrowser.class.equals(adapter)) {
			return (_browser != null) ? _browser.getWebBrowser() : null;
		}
		if (IWebBrowser.class.equals(adapter)) {
			return _webBrowser;
		}
		if (Browser.class.equals(adapter))
			return _browser;

		return super.getAdapter(adapter);
	}

	public String getAttribute(String key) {
		return null;
	}

	public int getExitValue() throws DebugException {
		if (!isTerminated())
			throw new DebugException(new Status(IStatus.ERROR, MozIDEUIPlugin.PLUGIN_ID, IStatus.OK, "process still running. exit value unknown", null));
		return _exitValue;
	}

	public String getLabel() {
		return "Mozilla Browser"; //TODO
	}

	public ILaunch getLaunch() {
		return _launch;
	}

	public IStreamsProxy getStreamsProxy() {
		return null;
	}

	public void setAttribute(String key, String value) {
	}

	/**
	 * Fires a creation event.
	 */
	protected void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	/**
	 * Fires the given debug event.
	 * 
	 * @param event debug event to fire
	 */
	protected void fireEvent(DebugEvent event) {
		DebugPlugin manager = DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(new DebugEvent[] { event });
		}
	}

	/**
	 * Fires a terminate event.
	 */
	protected void fireTerminateEvent() {
		DebugEvent event = new DebugEvent(this, DebugEvent.TERMINATE);
		event.setData(_browser);
		fireEvent(event);
	}

	/**
	 * Fires a change event.
	 */
	protected void fireChangeEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
	}

};