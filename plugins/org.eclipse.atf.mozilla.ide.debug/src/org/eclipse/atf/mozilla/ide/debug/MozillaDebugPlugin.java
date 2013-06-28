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

package org.eclipse.atf.mozilla.ide.debug;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.atf.mozilla.ide.core.IXPCOMThreadProxyHelper;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugTarget;
import org.eclipse.atf.mozilla.ide.debug.model.JSSourceLocator;
import org.eclipse.atf.mozilla.ide.events.IApplicationEventAdmin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ISourceLocator;
import org.mozilla.interfaces.jsdIDebuggerService;
import org.mozilla.interfaces.nsIObserver;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class MozillaDebugPlugin extends Plugin {

	public static boolean logging = false;
	public static boolean events = false;
	public static String DBG = null;
	public static String EVT = null;
	public static String ERR = null;

	final public static int ERROR_STATUS = 100;
	final public static String PLUGIN_ID = "org.eclipse.atf.mozilla.ide.debug";
	final public static String DEBUG_MODEL_ID = "org.eclipse.atf.mozilla.ide.debug";

	public static final String DEBUG_JSDCALLHOOK = PLUGIN_ID + "/jsdCallHook";
	public static final String DEBUG_JSDSCRIPTHOOK = PLUGIN_ID + "/jsdScriptHook";

	//    // Workaround in 2.2.1.  To avoid recursion in the debugger, evals give
	//    // properties without ids, so we have to inspect each individual property
	//    // to get its id as a workaround.  This is very slow and should be removed
	//    // once LZDebug is fixed.
	//    public static final boolean propertyIDGiven = false;
	//
	//    // If false, set variables using eval
	//    public static final boolean isSetSupported = false;

	//The shared instance.
	private static MozillaDebugPlugin _plugin;
	//Resource bundle.
	private ResourceBundle _resourceBundle;

	private ServiceTracker appEventAdmTracker;

	/**
	 * The constructor.
	 */
	public MozillaDebugPlugin() {
		super();
		_plugin = this;
		try {
			_resourceBundle = ResourceBundle.getBundle("org.eclipse.atf.mozilla.ide.debug.DebugPluginResources");
		} catch (MissingResourceException x) {
			_resourceBundle = null;
		}

		// load the logging settings only if the main debug setting is on
		if (isDebugging()) {

			String id = PLUGIN_ID;
			String test = Platform.getDebugOption(id + "/debug/events");
			if (test != null)
				events = test.equals("true");
			test = Platform.getDebugOption(id + "/debug/logging");
			if (test != null)
				logging = test.equals("true");

			test = Platform.getDebugOption(id + "/debug/dbg");
			if (test != null)
				DBG = test;

			test = Platform.getDebugOption(id + "/debug/evt");
			if (test != null)
				EVT = test;

			test = Platform.getDebugOption(id + "/debug/err");
			if (test != null)
				ERR = test;
		}
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		ServiceReference reference = context.getServiceReference(IApplicationEventAdmin.class.getName());
		appEventAdmTracker = new ServiceTracker(context, reference, null);
		appEventAdmTracker.open();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		appEventAdmTracker.close();
		super.stop(context);
		_plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MozillaDebugPlugin getDefault() {
		return _plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MozillaDebugPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return _resourceBundle;
	}

	public static jsdIDebuggerService createDebuggerService() throws DebugException {

		jsdIDebuggerService debuggerService;
		try {
			Mozilla browser = Mozilla.getInstance();
			debuggerService = (jsdIDebuggerService) browser.getServiceManager().getServiceByContractID("@mozilla.org/js/jsd/debugger-service;1", jsdIDebuggerService.JSDIDEBUGGERSERVICE_IID);
			// debuggerService.on(); // will crash. can only be called from JS
			// do this instead:
			String jsdObserverCID = "2fd6b7f6-eb8c-4f32-ad26-113f2c02d0fe";
			nsIObserver jsdObserver = (nsIObserver) Mozilla.getInstance().getComponentManager().createInstance(jsdObserverCID, null, nsIObserver.NS_IOBSERVER_IID);
			jsdObserver.observe(null, "", "");
		} catch (XPCOMException xpcome) {
			throw new DebugException(new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, DebugException.INTERNAL_ERROR, "Error initializing debugger service", xpcome));
		}

		debug("Mozilla JS Debugger: " + debuggerService.getImplementationString());
		debug("Is on:" + debuggerService.getIsOn());

		if (compareVersion(debuggerService, 1, 2) >= 0)
			debuggerService.setFlags(jsdIDebuggerService.DISABLE_OBJECT_TRACE);

		return debuggerService;
	}

	static private IDebugTarget _debugTarget;
	static private DebugException _debugException;

	static synchronized public IDebugTarget launchDebugTarget(final ILaunch launch, final URL appURL, final IProject project, final String appPath, final INestedEventLoop eventLoop, final IXPCOMThreadProxyHelper proxyHelper) throws DebugException {

		// Pass the project to the source locator so that files can be mapped to local resources
		ISourceLocator locator = launch.getSourceLocator();
		if (locator != null && locator instanceof JSSourceLocator) {
			JSSourceLocator jslocator = (JSSourceLocator) locator;
			jslocator.setAppBase(appURL.toString());
		}

		// Create the JSDebugTarget on the same thread as the proxyHelper such that all calls through the
		// proxyHelper will be on the same thread to satisfy Mozilla.
		proxyHelper.syncExec(new Runnable() {
			public void run() {
				try {
					_debugTarget = new JSDebugTarget(launch, appURL, eventLoop, proxyHelper);
				} catch (DebugException de) {
					_debugException = de;
				}
			}
		});

		if (_debugException != null)
			throw _debugException;

		return _debugTarget;
	}

	public static int compareVersion(jsdIDebuggerService debuggerService, int maj, int min) {
		if (debuggerService.getImplementationMajor() < maj)
			return -1;

		if (debuggerService.getImplementationMajor() > maj)
			return 1;

		if (debuggerService.getImplementationMinor() < min)
			return -1;

		if (debuggerService.getImplementationMinor() > min)
			return 1;

		return 0;
	}

	/**
	 * Logs the specified throwable with this plug-in's log.
	 * @param t throwable to log 
	 */
	public static void log(Throwable t) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}

	/**
	 * Logs message with this plug-in's log.
	 * @param message
	 */
	public static void log(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, message, new Exception()));
	}

	/**
	 * Logs IStatus with this plug-in's log.
	 * @param status
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * If debugging is enabled for this plugin, Logs debug message.
	 * @param message
	 */
	public static void debug(String message) {
		if (getDefault().isDebugging()) {
			log(message);
		}
	}

	/**
	 * If debugging is enabled for this plugin debug key, Logs debug message.
	 * See DEBUG_* constants for available keys.
	 * @param key
	 * @param message
	 */
	public static void debug(String key, String message) {
		if (getDefault().isDebugging()) {
			String debug = Platform.getDebugOption(key);
			if ((debug != null) && (Boolean.parseBoolean(debug) == true))
				log(message);
		}
	}

	public boolean isProfiling() {
		// TODO Auto-generated method stub
		return false;
	}

	public IApplicationEventAdmin getApplicationEventAdmin() {
		return (IApplicationEventAdmin) appEventAdmTracker.getService();
	}
}
