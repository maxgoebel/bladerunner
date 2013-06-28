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

package org.eclipse.atf.mozilla.ide.core;

import org.eclipse.atf.mozilla.ide.events.IApplicationEventAdmin;
import org.eclipse.atf.mozilla.ide.internal.debug.ApplicationEventAdmin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MozideCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.atf.mozilla.ide.core";
	public static final String ATF_INTERNAL = "___ATF_INTERNAL";

	// The shared instance.
	private static MozideCorePlugin plugin;
	private ApplicationEventAdmin eventAdm;

	/**
	 * The constructor.
	 */
	public MozideCorePlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		eventAdm = new ApplicationEventAdmin();
		context.registerService(IApplicationEventAdmin.class.getName(), eventAdm, null);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		eventAdm.terminate();
		eventAdm = null;

		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MozideCorePlugin getDefault() {
		return plugin;
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
}
