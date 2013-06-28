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

package org.eclipse.atf.mozilla.ide.debug.internal.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.atf.mozilla.ide.core.IXPCOMThreadProxyHelper;
import org.eclipse.atf.mozilla.ide.core.XPCOMThreadProxy;
import org.eclipse.atf.mozilla.ide.debug.INestedEventLoop;
import org.eclipse.atf.mozilla.ide.debug.JSDebugCoreMessages;
import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.model.JSBreakpoint;
import org.eclipse.atf.mozilla.ide.debug.model.JSDebuggerKeywordBreakpoint;
import org.eclipse.atf.mozilla.ide.debug.model.JSErrorBreakpoint;
import org.eclipse.atf.mozilla.ide.debug.model.JSExceptionBreakpoint;
import org.eclipse.atf.mozilla.ide.debug.model.JSLineBreakpoint;
import org.eclipse.atf.mozilla.ide.debug.model.JSSourceLocator;
import org.eclipse.atf.mozilla.ide.debug.model.JSStartupBreakpoint;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.mozilla.interfaces.jsdIDebuggerService;
import org.mozilla.interfaces.jsdIExecutionHook;
import org.mozilla.interfaces.jsdIScript;
import org.mozilla.interfaces.jsdIScriptEnumerator;
import org.mozilla.interfaces.nsIRequest;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIURI;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebProgress;
import org.mozilla.interfaces.nsIWebProgressListener;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;

/**
 * @author Adam
 *
 */
public class JSDebugTarget extends JSDebugElement implements IDebugTarget, IDebugEventSetListener, IBreakpointListener, IBreakpointManagerListener {

	private static final long NS_ERROR_NOT_AVAILABLE = 0x80040111l;
	private ILaunch _launch;
	private IProcess _process;
	private JSDebugThread[] _threads = new JSDebugThread[0];
	private URL _appURL;
	private boolean _terminated = false;
	private boolean _connected = false;
	private String _errorString = null;
	private ILog _logger = null;

	private jsdIDebuggerService _debuggerService;
	private INestedEventLoop _eventLoop;
	private IXPCOMThreadProxyHelper _proxyHelper;
	private IBreakpointManager _breakpointManager;
	private BreakpointsChangedListener _fBreakpointsChangedListener;

	class WebProgressListener implements nsIWebProgressListener {

		public void onLocationChange(nsIWebProgress progress, nsIRequest request, nsIURI location) {
			MozillaDebugPlugin.debug("nsIWebProgressListener.onLocationChange");
			if (location.schemeIs("javascript"))
				return;

			resetDebugger();
		}

		public void onProgressChange(nsIWebProgress progress, nsIRequest request, int aCurSelfProgress, int aMaxSelfProgress, int aCurTotalProgress, int aMaxTotalProgress) {
			// empty
		}

		public void onSecurityChange(nsIWebProgress progress, nsIRequest request, long state) {
			// empty
		}

		public void onStateChange(nsIWebProgress progress, nsIRequest request, long stateFlags, long status) {
			// empty

		}

		public void onStatusChange(nsIWebProgress progress, nsIRequest arg1, long status, String message) {
			// empty

		}

		public nsISupports queryInterface(String string) {
			return Mozilla.queryInterface(this, string);
		}

	}

	private nsIWebProgressListener webProgressListener = new WebProgressListener();

	public JSDebugTarget(ILaunch launch, URL appURL, INestedEventLoop eventLoop, IXPCOMThreadProxyHelper proxyHelper) throws DebugException {

		super(null);

		_launch = launch;
		_appURL = appURL;

		//TODO: can we defer creation of the thread until connection is received?
		JSDebugThread thread = new JSDebugThread(this);
		_threads = new JSDebugThread[] { thread };
		// TODO setup listener for terminate

		_proxyHelper = proxyHelper;
		_debuggerService = (jsdIDebuggerService) XPCOMThreadProxy.createProxy(MozillaDebugPlugin.createDebuggerService(), proxyHelper);

		_eventLoop = eventLoop;

		setupDebuggerHooks(thread);

		_debuggerService.setFlags(jsdIDebuggerService.ENABLE_NATIVE_FRAMES);

		handleConnectionStarted();

		_breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		_breakpointManager.addBreakpointListener(this);
		_breakpointManager.addBreakpointManagerListener(this);
		_breakpointManager.addBreakpointListener(_fBreakpointsChangedListener = new BreakpointsChangedListener());
		DebugPlugin.getDefault().addDebugEventListener(this);

	}

	protected void setupDebuggerHooks(JSDebugThread thread) {
		thread.setScriptHook();
		thread.setErrorHook();
		thread.setBreakpointHook();
		thread.setFunctionHook();
		thread.setTopLevelHook();
		thread.setDebugHook(); //not sure why we need this hook
		thread.setThrowHook();
	}

	protected void removeDebuggerHooks() {
		MozillaDebugPlugin.debug("JSDebugTarget.removeDebuggerHooks");
		unsetTopLevelHook();
		unsetFunctionHook();
		unsetBreakpointHook();
		unsetDebuggerHook();
		unsetErrorHook();
		unsetThrowHook();
		unsetScriptHook();
		unsetInterruptHook();
	}

	public IXPCOMThreadProxyHelper getProxyHelper() {
		return _proxyHelper;
	}

	protected jsdIDebuggerService getDebuggerService() {
		return _debuggerService;
	}

	public interface INestedCallback {
		public void onNest();
	}

	public long enterNestedEventLoop(INestedCallback callback) {
		//TODO synchronization?

		// we should stop processing any events while we're in a callback

		if (callback != null)
			callback.onNest();

		return _eventLoop.runEventLoop();
	}

	public void exitNestedEventLoop(long rv) {
		_eventLoop.stopEventLoop(rv);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getProcess()
	 */
	public IProcess getProcess() {
		return _process;
	}

	public void setProcess(IProcess process) {
		removeWebBrowserListener();
		_process = process;
		addWebBrowserListener();
	}

	private void addWebBrowserListener() {
		nsIWebBrowser browser = (nsIWebBrowser) _process.getAdapter(nsIWebBrowser.class);
		browser.addWebBrowserListener(webProgressListener, nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);
	}

	private void removeWebBrowserListener() {
		if (_process != null) {
			nsIWebBrowser browser = (nsIWebBrowser) _process.getAdapter(nsIWebBrowser.class);
			if (browser != null)
				browser.removeWebBrowserListener(webProgressListener, nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getThreads()
	 */
	public IThread[] getThreads() throws DebugException {
		return _threads;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#hasThreads()
	 */
	public boolean hasThreads() throws DebugException {
		return getThreads().length > 0;
	}

	protected void removeThread(IThread thread) {
		List newThreads = new ArrayList(_threads.length);
		for (int i = 0; i < _threads.length; i++)
			newThreads.add(_threads[i]);
		newThreads.remove(thread);
		_threads = (JSDebugThread[]) newThreads.toArray(new JSDebugThread[newThreads.size()]);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	public String getName() throws DebugException {
		return _appURL == null ? JSDebugCoreMessages.JSDebugCore_0 : _appURL.toExternalForm();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#supportsBreakpoint(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return breakpoint instanceof JSBreakpoint;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return !isTerminated();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return _terminated;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 * 
	 * NOTE: This method, at least, needs to be Thread safe because it can get
	 * call multiple times by the separate Threads.
	 */
	synchronized public void terminate() throws DebugException {
		if (isTerminated())
			return;

		shutdown();
		if (_threads.length > 0) {
			for (int i = 0; i < _threads.length; i++)
				_threads[i].terminate();
			_threads = new JSDebugThread[0];
		}
		IProcess proc = getProcess();
		if (proc != null)
			proc.terminate();
		_terminated = true;
		fireTerminateEvent();
	}

	/**
	 * DebugTarget object cleanup common to terminate and disconnect
	 */
	private void shutdown() {
		// Unregister as breakpoint listener
		_breakpointManager.removeBreakpointListener(this);
		_breakpointManager.removeBreakpointManagerListener(this);
		_breakpointManager.removeBreakpointListener(_fBreakpointsChangedListener);
		DebugPlugin.getDefault().removeDebugEventListener(this);

		stopDebugger();
	}

	public void resetDebugger() {
		if (_debuggerService == null)
			return;
		JSDebugThread thread = (JSDebugThread) _threads[0];
		thread.clearCX();

		try {
			_debuggerService.clearAllBreakpoints();
			_debuggerService.gC();
		} catch (XPCOMException e) {
			if (e.errorcode == NS_ERROR_NOT_AVAILABLE) {
				MozillaDebugPlugin.log(e);
				// just log, because otherwise this error won't let user terminate the debug launch
			} else {
				throw e;
			}
		}

		if (isSuspended())
			thread.resumeAbortScript();

	}

	private void stopDebugger() {
		if (_debuggerService == null)
			return;

		removeDebuggerHooks();
		try {
			_debuggerService.clearAllBreakpoints();
		} catch (XPCOMException e) {
			if (e.errorcode == NS_ERROR_NOT_AVAILABLE) {
				MozillaDebugPlugin.log(e);
				// just log, because otherwise this error won't let user terminate the debug launch
			} else {
				throw e;
			}
		}

		if (isSuspended())
			exitNestedEventLoop(jsdIExecutionHook.RETURN_CONTINUE);

		_debuggerService.gC();

		if (!_debuggerService.getInitAtStartup())
			_debuggerService.off();

		_debuggerService = null;
		_connected = false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		boolean atLeastOneThreadCanSuspend = false;
		for (int i = 0; i < _threads.length; i++) {
			atLeastOneThreadCanSuspend |= _threads[i].canSuspend();
		}
		return !isTerminated() && !isDisconnected() && !isSuspended() && atLeastOneThreadCanSuspend;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return _threads.length > 0 ? _threads[0].isSuspended() : false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		JSDebugThread thread = (JSDebugThread) _threads[0];
		thread.resume();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		_proxyHelper.syncExec(new Runnable() {
			public void run() {
				((JSDebugThread) _threads[0]).setInterruptHook();
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public void breakpointAdded(final IBreakpoint breakpoint) {
		if (supportsBreakpoint(breakpoint)) {
			final JSSourceLocator locator = (JSSourceLocator) getLaunch().getSourceLocator();
			_debuggerService.enumerateScripts(new jsdIScriptEnumerator() {
				public void enumerateScript(jsdIScript script) {
					establishBreakpoint(true, locator, script, breakpoint);
				}

				public nsISupports queryInterface(String id) {
					return Mozilla.queryInterface(this, id);
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointRemoved(final IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			final JSSourceLocator locator = (JSSourceLocator) getLaunch().getSourceLocator();
			_debuggerService.enumerateScripts(new jsdIScriptEnumerator() {
				public void enumerateScript(jsdIScript script) {
					establishBreakpoint(false, locator, script, breakpoint);
				}

				public nsISupports queryInterface(String id) {
					return Mozilla.queryInterface(this, id);
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointChanged(final IBreakpoint breakpoint, IMarkerDelta delta) {

		// Don't process change when all breakpoints are disabled or
		// there is no marker delta				
		if (_breakpointManager.isEnabled() && delta != null) {
			if (supportsBreakpoint(breakpoint)) {
				boolean deltaEnabled;
				deltaEnabled = delta.getAttribute(IBreakpoint.ENABLED, false);

				final int deltaLNumber = delta.getAttribute(IMarker.LINE_NUMBER, 0);
				IMarker marker = breakpoint.getMarker();
				int lineNumber = -1;
				lineNumber = marker.getAttribute(IMarker.LINE_NUMBER, 0);

				try {
					if (lineNumber != deltaLNumber) {
						breakpoint.setMarker(marker);
						// TODO: Should breakpoints be removed on line number change?
						/*						if (breakpoint.isEnabled()) {
														final JSSourceLocator locator = (JSSourceLocator)getLaunch().getSourceLocator();
														_debuggerService.enumerateScripts(new jsdIScriptEnumerator() {
															public void enumerateScript(jsdIScript script) {
																establishBreakpoint(locator, script, breakpoint, deltaLNumber);
															}
														
															public nsISupports queryInterface(String id) {
																return Mozilla.queryInterface(this, id);
															}			
														});
													} */
					}
					if (deltaEnabled != breakpoint.isEnabled()) {
						if (breakpoint.isEnabled()) {
							breakpointAdded(breakpoint);
						} else {
							breakpointRemoved(breakpoint, null);
						}
					}
				} catch (CoreException ce) {
					// Breakpoint doesn't throw this exception 
					MozillaDebugPlugin.log(ce);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointManagerListener#breakpointManagerEnablementChanged(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointManagerEnablementChanged(boolean enabled) {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(getModelIdentifier());
		for (int i = 0; i < breakpoints.length; i++) {
			if (supportsBreakpoint(breakpoints[i])) {
				try {
					if (breakpoints[i].isEnabled()) {
						if (enabled) {
							breakpointAdded(breakpoints[i]);
						} else {
							breakpointRemoved(breakpoints[i], null);
						}
					}
				} catch (CoreException e) {
					//					 Breakpoint doesn't throw this exception 
					MozillaDebugPlugin.log(e);
				}
			}
		}
	}

	public boolean establishBreakpoint(boolean activate, JSSourceLocator locator, jsdIScript script, IBreakpoint breakpoint) {

		if (breakpoint instanceof JSDebuggerKeywordBreakpoint) {
			if (activate) {
				_threads[0].setDebuggerHook();
			} else {
				unsetDebuggerHook();
			}
			return true;
		}

		if (breakpoint instanceof JSErrorBreakpoint) {
			_threads[0].setSuspendOnErrors(activate);
			return true;
		}

		if (breakpoint instanceof JSExceptionBreakpoint) {
			_threads[0].setSuspendOnExceptions(activate);
			return true;
		}

		if (breakpoint instanceof JSStartupBreakpoint) {
			_threads[0].setSuspendOnTopLevel(activate);
			return true;
		}

		if (!(breakpoint instanceof JSLineBreakpoint)) {
			return false;
		}

		boolean match = locator.matches(breakpoint, script.getFileName());
		if (match) {
			JSLineBreakpoint jsBPoint = (JSLineBreakpoint) breakpoint;
			try {
				int lineNumber = jsBPoint.getLineNumber();
				match = establishBreakpoint(activate, locator, script, lineNumber);
			} catch (CoreException e) {
				IStatus status = new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, IStatus.ERROR, JSDebugCoreMessages.JSDebugCore_3, e);
				MozillaDebugPlugin.getDefault().getLog().log(status);
			}
		}
		return match;
	}

	static private boolean establishBreakpoint(boolean activate, JSSourceLocator locator, jsdIScript script, int line) {

		final int PCMAP_SOURCETEXT = 1;
		long baseLineNumber = script.getBaseLineNumber();
		long lineExtent = script.getLineExtent();
		boolean match = line >= baseLineNumber && line <= baseLineNumber + lineExtent && (script.isLineExecutable(line, PCMAP_SOURCETEXT) || baseLineNumber == line);
		if (match) {
			long pc = script.lineToPc(line, PCMAP_SOURCETEXT);
			if (activate) {
				MozillaDebugPlugin.debug("setting breakpoint at " + script.getFileName() + " line=" + line + " pc=" + pc);
				script.setBreakpoint(pc);
			} else {
				MozillaDebugPlugin.debug("clearing breakpoint at " + script.getFileName() + " line=" + line + " pc=" + pc);
				script.clearBreakpoint(pc);
			}
		}

		return match;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	public boolean canDisconnect() {
		return !isDisconnected();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	public void disconnect() throws DebugException {
		shutdown();

		if (_threads.length > 0) {
			for (int i = 0; i < _threads.length; i++)
				;//TODO _threads[i].terminate();
			_threads = new JSDebugThread[0];
		}

		fireChangeEvent(DebugEvent.CONTENT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	public boolean isDisconnected() {
		return !_connected;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
	 */
	public boolean supportsStorageRetrieval() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
	 */
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		notSupported("", null); //$NON-NLS-1$
		return null; // will never get here
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return _launch;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */

	public IDebugTarget getDebugTarget() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElement#getLabel()
	 */
	public String getLabel() {
		String urlString = "<unknown>"; //TODO i18n
		try {
			urlString = getName();
		} catch (DebugException e) {
		}

		int paramIndex = urlString.indexOf('?');
		StringBuffer label = new StringBuffer((paramIndex > 0) ? urlString.substring(0, paramIndex) : urlString);
		if (_errorString != null) {
			label.append(" error=");
			label.append(_errorString);
		}
		label.append(" (");
		if (isTerminated()) {
			label.append("Terminated"); //TODO i18n			
		} else if (isSuspended()) {
			label.append("Stopped"); //TODO i18n			
		} else if (isDisconnected()) {
			label.append((_debuggerService == null) ? "Disconnected" : "Waiting"); //TODO i18n
		} else {
			label.append("Running"); //TODO i18n			
		}
		label.append(')');
		return label.toString();
	}

	public void handleConnectionStarted() {
		_connected = true;
		fireChangeEvent(DebugEvent.STATE);
		_threads[0].fireCreationEvent();
	}

	public void handleConnectionTerminated(IStatus status) {
		_connected = false;
		fireChangeEvent(DebugEvent.STATE);

		if (status.getCode() != IStatus.OK)
			_errorString = status.getMessage();

		//TODO
		//		if (_connector == null) {
		//			// Event initiated by a disconnect or terminate.  Do nothing.
		//		} else {
		//			// User-terminated process or unexpected disconnect.
		//			// Corresponding app instance is gone. Terminate the debug target.
		//			_connector = null;
		try {
			terminate();
		} catch (DebugException de) {
			// Not much we can do except log it
			MozillaDebugPlugin.log(de);
		}
		//		}
	}

	public void handleLogMessage(IStatus status) {
		if (_logger != null)
			_logger.log(status);

		MozillaDebugPlugin.getDefault().getLog().log(status);
	}

	public void setLogger(ILog logger) {
		_logger = logger;
	}

	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			DebugEvent event = events[i];
			switch (event.getKind()) {
				case DebugEvent.TERMINATE: {
					Object source = event.getSource();

					if (source.equals(_process) || source.equals(_threads[0])) {

						try {
							terminate();
						} catch (DebugException de) {
							MozillaDebugPlugin.log(de);
						}
					}
					break;
				}
				default:
			}
		}
	}

	/**
	 * Throws a IStatus in a Debug Event
	 * 
	 */
	private void fireError(IStatus status) {
		DebugEvent event = new DebugEvent(this, DebugEvent.MODEL_SPECIFIC);
		event.setData(status);
		fireEvent(event);
	}

	// This class could be up in the Debug UI plugin, but will probably need to 
	// be in the model when multiple debug session are supported.
	private class BreakpointsChangedListener implements IBreakpointsListener {

		public void breakpointsAdded(IBreakpoint[] breakpoints) {

		}

		public void breakpointsChanged(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {

			try {
				if (_breakpointManager.isEnabled()) {
					for (int i = 0; i < breakpoints.length; i++) {
						if (supportsBreakpoint(breakpoints[i]) && breakpoints[i].isEnabled() && deltas[i] != null) {
							int deltaLNumber = deltas[i].getAttribute(IMarker.LINE_NUMBER, 0);
							int lineNumber = -1;
							IMarker marker = breakpoints[i].getMarker();
							lineNumber = marker.getAttribute(IMarker.LINE_NUMBER, 0);
							if (lineNumber != deltaLNumber) {
								String errorMessage = JSDebugCoreMessages.JSDebugCore_1;
								Status status = new Status(IStatus.WARNING, MozillaDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, errorMessage, null);
								fireError(status);
								break;
							}
						}

					}
				}
			} catch (CoreException ce) {
				// Breakpoint doesn't throw this exception 
				MozillaDebugPlugin.log(ce);
			}

		}

		public void breakpointsRemoved(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {

		}

	}

	public void unsetDebuggerHook() {
		_debuggerService.setDebuggerHook(null);
	}

	public void unsetInterruptHook() {
		MozillaDebugPlugin.debug("setInterruptHook(null)");
		_debuggerService.setInterruptHook(null);
	}

	public void unsetTopLevelHook() {
		_debuggerService.setTopLevelHook(null);
		MozillaDebugPlugin.debug("setTopLevelHook(null)");
	}

	public void unsetFunctionHook() {
		MozillaDebugPlugin.debug("setFunctionHook(null)");
		_debuggerService.setFunctionHook(null);
	}

	private void unsetErrorHook() {
		MozillaDebugPlugin.debug("setErrorHook(null)");
		_debuggerService.setErrorHook(null);
	}

	private void unsetScriptHook() {
		MozillaDebugPlugin.debug("setScriptHook(null)");
		_debuggerService.setScriptHook(null);
	}

	private void unsetThrowHook() {
		MozillaDebugPlugin.debug("setThrowHook(null)");
		_debuggerService.setThrowHook(null);
	}

	private void unsetBreakpointHook() {
		MozillaDebugPlugin.debug("setBreakpointHook(null)");
		_debuggerService.setBreakpointHook(null);
	}
}
