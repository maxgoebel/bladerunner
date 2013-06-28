/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.dbai.bladeRunner;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * This is the central singleton for the Learn editor plugin.
 */
public final class LearnUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	//	public static final String PLUGIN_ID = "annotationTool"; //$NON-NLS-1$

	// The shared instance.
	private static LearnUIPlugin plugin;

	public LearnUIPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		// register the demo webapp, if available
		// Bundle demoPlugin = Platform.getBundle("org.weblearn.demo");
		// if (demoPlugin!=null) demoPlugin.start();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static LearnUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	// public static ImageDescriptor getImageDescriptor(String path) {
	// return AbstractUIPlugin.imageDescriptorFromPlugin("org.weblearn.ui",
	// path);
	// }

	/**
	 * Returns the standard display to be used. The method first checks, if the
	 * thread calling this method has an associated display. If so, this display
	 * is returned. Otherwise the method returns the default display.
	 */
	public static Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	/**
	 * Logs the specified throwable with this plug-in's log.
	 * 
	 * @param t
	 *            throwable to log
	 */
	public static void log(Throwable t) {
		String pluginId = LearnUIPlugin.getDefault().getBundle()
				.getSymbolicName();
		int INTERNAL_ERROR = 120;
		IStatus status = new Status(IStatus.ERROR, pluginId, INTERNAL_ERROR,
				"WebLearn internal error", t); //$NON-NLS-1$
		log(status);
	}

	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status
	 *            status
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Writes the message to the plug-in's log
	 * 
	 * @param message
	 *            the text to write to the log
	 */
	public static void log(String message, Throwable exception) {
		IStatus status = newErrorStatus(message, exception);
		log(status);
	}

	/**
	 * Returns a new <code>IStatus</code> for this plug-in
	 */
	private static IStatus newErrorStatus(String message, Throwable exception) {
		if (message == null) {
			message = ""; //$NON-NLS-1$
		}
		String pluginId = LearnUIPlugin.getDefault().getBundle()
				.getSymbolicName();
		return new Status(IStatus.ERROR, pluginId, 0, message, exception);
	}

	@Override
	protected ImageRegistry createImageRegistry() {
		return LearnUIImages.initializeImageRegistry();
	}
}
