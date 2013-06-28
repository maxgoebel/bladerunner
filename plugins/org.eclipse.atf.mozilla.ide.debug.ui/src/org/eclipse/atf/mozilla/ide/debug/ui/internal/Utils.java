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

package org.eclipse.atf.mozilla.ide.debug.ui.internal;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Utility methods for the Javascript debug adapter
 */
public class Utils {
	private static ResourceBundle _resourceBundle;

	//The resource bundle for help id Strings
	private static ResourceBundle _helpResourceBundle;

	// The image registry which holds <code>Image</code>s
	private static ImageRegistry _imageRegistry;

	// <code>ImageDescriptor</code>s
	private static HashMap _imageDescriptors;

	private static URL ICON_BASE_URL = null;

	private final static String OBJECT = "obj16/";

	/**
	 * Retrieve the requested String resource. Practice lazy retrieval on the
	 * resource bundle.
	 */
	public static String getResourceString(String key) {
		if (_resourceBundle == null) {
			_resourceBundle = getResourceBundle();
		}
		if (_resourceBundle != null) {
			return _resourceBundle.getString(key);
		} else {
			return "!" + key + "!";
		}
	}

	/**
	 * Retrieve the requested help id. Practice lazy retrieval on the resource
	 * bundle.
	 */
	public static String getHelpResourceString(String key) {
		if (_helpResourceBundle == null) {
			_helpResourceBundle = getHelpResourceBundle();
		}
		if (_helpResourceBundle != null) {
			return _helpResourceBundle.getString(key);
		} else {
			return "!" + key + "!";
		}
	}

	/**
	 * Plug in the single argument to the resource String for the key to get a
	 * formatted resource String
	 */
	public static String getFormattedString(String key, String arg) {
		String string = getResourceString(key);
		return MessageFormat.format(string, new String[] { arg });
	}

	/**
	 * Plug in the arguments to the resource String for the key to get a
	 * formatted resource String
	 */
	public static String getFormattedString(String key, String[] args) {
		String string = getResourceString(key);
		return MessageFormat.format(string, args);
	}

	/**
	 * Returns the resource bundle used by all parts of the debug ui package.
	 */
	public static ResourceBundle getResourceBundle() {
		return MozillaDebugUIPlugin.getDefault().getResourceBundle();
	}

	/**
	 * Returns the resource bundle used by all parts of the debug ui package.
	 */
	public static ResourceBundle getHelpResourceBundle() {
		try {
			return ResourceBundle.getBundle("com.ibm.rct.laszlo.LaszloHelpResources");
		} catch (MissingResourceException e) {
		}
		return null;
	}

	//	/**
	//	 * Returns a string that is used to identify an object as being associated
	//	 * with the IBM debugger plugin NOTE: this returns the same value as
	//	 * IDebugElement.getModelIdentifier()
	//	 * 
	//	 * @return String that represents the modelIdentifier
	//	 * @see PICLDebugElement
	//	 */
	//
	//	public static String getModelIdentifier() {
	//		return LaszloDebugUIConstants.ACTIONSCRIPT_MODEL_IDENTIFIER;
	//	}
	//
	//	public static ImageRegistry getImageRegistry() {
	//		if (_imageRegistry == null)
	//			_imageRegistry = new ImageRegistry();
	//		return _imageRegistry;
	//	}
	//
	//	public static ImageRegistry initializeImageRegistry() {
	//		_imageRegistry = new ImageRegistry();
	//		_imageDescriptors = new HashMap(30);
	//		declareImages();
	//		return _imageRegistry;
	//	}
	//
	//	private final static void declareRegistryImage(String key, String path) {
	//		ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
	//		try {
	//			desc = ImageDescriptor.createFromURL(makeIconFileURL(path));
	//		} catch (MalformedURLException me) {
	//		}
	//		_imageRegistry.put(key, desc);
	//		_imageDescriptors.put(key, desc);
	//	}
	//
	//	private static URL makeIconFileURL(String iconPath)
	//			throws MalformedURLException {
	//		if (ICON_BASE_URL == null) {
	//			try {
	//				ICON_BASE_URL = new URL(MozillaDebugUIPlugin.getDefault()
	//						.getDescriptor().getInstallURL(), "icons/full/");
	//			} catch (MalformedURLException ex) {
	//			}
	//		}
	//
	//		if (ICON_BASE_URL == null) {
	//			throw new MalformedURLException();
	//		}
	//
	//		return new URL(ICON_BASE_URL, iconPath);
	//	}
	//
	//	public static ImageDescriptor getImageDescriptor(String key) {
	//		if (_imageDescriptors == null) {
	//			initializeImageRegistry();
	//		}
	//		return (ImageDescriptor) _imageDescriptors.get(key);
	//	}
	//
	//	public static Image getImage(String key) {
	//		if (_imageRegistry == null) {
	//			initializeImageRegistry();
	//		}
	//		return getImageRegistry().get(key);
	//	}
	//
	//	private static void declareImages() {
	//		declareRegistryImage(LaszloDebugUIConstants.ACTIONSCRIPT_ICON_VARIABLE,
	//				OBJECT + "variable_obj.gif");
	//		declareRegistryImage(
	//				LaszloDebugUIConstants.ACTIONSCRIPT_ICON_VARIABLE_CHANGED, OBJECT
	//						+ "variablechanged_obj.gif");
	//		declareRegistryImage(
	//				LaszloDebugUIConstants.ACTIONSCRIPT_ICON_VARIABLE_DISABLED, OBJECT
	//						+ "variabledisabled_obj.gif");
	//		declareRegistryImage(
	//				LaszloDebugUIConstants.ACTIONSCRIPT_ICON_VARIABLE_ARG, OBJECT
	//						+ "variable_arg.gif");
	//		declareRegistryImage(
	//				LaszloDebugUIConstants.ACTIONSCRIPT_ICON_VARIABLE_LOCAL, OBJECT
	//						+ "variable_local.gif");
	//
	//		declareRegistryImage(LaszloDebugUIConstants.FLASH_LAUNCHER_CONFIG_TAB,
	//				OBJECT + "launch_config.gif");
	//	}
	//
	//	/**
	//	 * Returns a String that is the current path to the this plugin's directory
	//	 * This is more accurate that asking the plugin directly since it seems to
	//	 * always give back "plugin"
	//	 * 
	//	 * @return a string that contains the full path to the debugger's plugin
	//	 *         directory
	//	 */
	//	public static String getPluginPath() {
	//		return ((PluginDescriptor) MozillaDebugPlugin.getDefault()
	//				.getDescriptor()).getInstallURLInternal().getPath();
	//	}
	//
	//	public static void displayError(String titleCode, String msgCode) {
	//		MessageDialog.openError(MozillaDebugUIPlugin.getActiveWorkbenchShell(),
	//				titleCode, msgCode);
	//	}
	//
	/**
	 * Returns the active workbench window
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return MozillaDebugUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Returns the standard display to be used. The method first checks, if
	 * the thread calling this method has an associated display. If so, this
	 * display is returned. Otherwise the method returns the default display.
	 */
	public static Display getStandardDisplay() {
		Display display;
		display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}

	//	public static IWorkbenchPage getActivePage() {
	//		IWorkbenchWindow w = getActiveWorkbenchWindow();
	//		if (w != null) {
	//			return w.getActivePage();
	//		}
	//		return null;
	//	}
	//
	/**
	 * Returns the active workbench shell or <code>null</code> if none
	 * 
	 * @return the active workbench shell or <code>null</code> if none
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}

	//	public static void errorDialog(String message, IStatus status) {
	//		log(status);
	//		Shell shell = getActiveWorkbenchShell();
	//		if (shell != null) {
	//			ErrorDialog.openError(shell, DebugUIMessages
	//					.getString("JDIDebugUIPlugin.Error_1"), message, status); //$NON-NLS-1$
	//		}
	//	}
	//
	//	/**
	//	 * Utility method with conventions
	//	 */
	//	public static void errorDialog(String message, Throwable t) {
	//		logException(t);
	//		Shell shell = getActiveWorkbenchShell();
	//		if (shell != null) {
	//			IStatus status = new Status(IStatus.ERROR, MozillaDebugUIPlugin
	//					.getPluginId(), IStatus.ERROR,
	//					"Error logged from Flash Debug UI: ", t); //$NON-NLS-1$	
	//			ErrorDialog.openError(shell, DebugUIMessages
	//					.getString("JDIDebugUIPlugin.Error_1"), message, status); //$NON-NLS-1$
	//		}
	//	}

	private static final String IBMCopyRight = "(C) Copyright IBM Corp. 2002. All rights reserved.";
}
