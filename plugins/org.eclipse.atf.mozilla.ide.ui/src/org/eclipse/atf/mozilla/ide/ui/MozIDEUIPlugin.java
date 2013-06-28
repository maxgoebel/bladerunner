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

package org.eclipse.atf.mozilla.ide.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.core.MozideCorePlugin;
import org.eclipse.atf.mozilla.ide.events.IApplicationEventAdmin;
import org.eclipse.atf.mozilla.ide.ui.netmon.CallListsManager;
import org.eclipse.atf.mozilla.ide.ui.preferences.IPreferenceConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class MozIDEUIPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static MozIDEUIPlugin plugin;

	public static final String PLUGIN_ID = "org.eclipse.atf.mozilla.ide.ui";

	//unique CLASS name to identify elements that are injected by ATF into the DOM and should be
	//filtered out of tooling (i.e. DOMInspector)
	public static final String ATF_INTERNAL = MozideCorePlugin.ATF_INTERNAL;

	public static final String DEFAULT_IMAGE_ID = "__DEFAULT";

	public static final String E_BACK_IMG_ID = "E_BACK_IMG_ID";
	public static final String D_BACK_IMG_ID = "D_BACK_IMG_ID";
	public static final String E_FORWARD_IMG_ID = "E_FORWARD_IMG_ID";
	public static final String D_FORWARD_IMG_ID = "D_FORWARD_IMG_ID";
	public static final String E_REFRESH_IMG_ID = "E_REFRESH_IMG_ID";
	public static final String D_REFRESH_IMG_ID = "D_REFRESH_IMG_ID";
	public static final String E_STOP_IMG_ID = "E_STOP_IMG_ID";
	public static final String D_STOP_IMG_ID = "D_STOP_IMG_ID";
	public static final String E_GO_IMG_ID = "E_GO_IMG_ID";
	public static final String D_GO_IMG_ID = "D_GO_IMG_ID";
	public static final String SHOWMENU_IMG_ID = "SHOWMENU_IMG_ID";

	//console
	public static final String INFO_IMG_ID = "INFO_IMG_ID";
	public static final String ERROR_IMG_ID = "ERROR_IMG_ID";
	public static final String WARNING_IMG_ID = "WARNING_IMG_ID";
	public static final String EXCEPTION_IMG_ID = "EXCEPTION_IMG_ID";
	public static final String CLEAR_IMG_ID = "CLEAR_IMG_ID";

	//inspector
	public static final String DOCUMENT_IMG_ID = "DOCUMENT_IMG_ID";
	public static final String ELEMENT_IMG_ID = "ELEMENT_IMG_ID";
	public static final String TEXT_IMG_ID = "TEXT_IMG_ID";
	public static final String COMMENT_IMG_ID = "COMMENT_IMG_ID";
	public static final String WAITING_IMG_ID = "WAITING_IMG_ID";
	public static final String FILTERBYNAME_IMG_ID = "FILTERBYNAME_IMG_ID";
	public static final String FILTERBYCLASS_IMG_ID = "FILTERBYCLASS_IMG_ID";
	public static final String FILTERBYID_IMG_ID = "FILTERBYID_IMG_ID";

	//source
	public static final String E_UPDATE_ID = "E_UPDATE_ID";
	public static final String E_REFRESH_ID = "E_REFRESH_ID";
	public static final String VALIDATE_ID = "VALIDATE_ID";

	//css
	public static final String HIGHLIGHT_ID = "HIGHLIGHT_ID";
	public static final String ADDPROPERTY_ID = "ADDPROPERTY_ID";
	public static final String EDITPROPERTY_ID = "EDITPROPERTY_ID";
	public static final String OPENFILE_ID = "OPENFILE_ID";

	//xhrmon
	public static final String COPYRESPONSE_ID = "COPYRESPONSE_ID";
	public static final String FILTERHTTP_ID = "FILTERHTTP_ID";
	public static final String FILTERXHR_ID = "FILTERXHR_ID";
	public static final String CSSFILTER_ID = "CSSFILTER_ID";
	public static final String HTMLFILTER_ID = "HTMLFILTER_ID";
	public static final String JSFILTER_ID = "JSFILTER_ID";
	public static final String IMGFILTER_ID = "IMGFILTER_ID";

	public static final String DONE_IMAGE = "done";
	public static final String ERROR_IMAGE = "error";
	public static final String WARNING_IMAGE = "warning";
	public static final String RUNNING_IMAGE = "waiting";

	//toolbar
	public static final String CLOSE_TB_ID = "CLOSE_TB_ID";

	//watcher
	public static final String E_STARTWATCHER_ID = "E_STARTWATCHER_ID";
	public static final String D_STARTWATCHER_ID = "D_STARTWATCHER_ID";
	public static final String E_STOPWATCHER_ID = "E_STOPWATCHER_ID";
	public static final String D_STOPWATCHER_ID = "D_STOPWATCHER_ID";

	public static final String CSS_VIEWER_FONT = "CSS_VIEWER_FONT";

	private Map swtResources = new HashMap();

	private ServiceTracker appEventAdmTracker;

	private CallListsManager callListManager;

	/**
	 * The constructor.
	 */
	public MozIDEUIPlugin() {
		plugin = this;
	}

	protected void initializeImageRegistry(ImageRegistry reg) {
		reg.put(DEFAULT_IMAGE_ID, getImageDescriptor("icons/sample.gif").createImage());

		//common images
		reg.put(CLEAR_IMG_ID, getImageDescriptor("icons/common/clear.gif"));

		//browser images
		reg.put(E_BACK_IMG_ID, getImageDescriptor("icons/browser/e_back.gif"));
		reg.put(D_BACK_IMG_ID, getImageDescriptor("icons/browser/d_back.gif"));
		reg.put(E_FORWARD_IMG_ID, getImageDescriptor("icons/browser/e_forward.gif"));
		reg.put(D_FORWARD_IMG_ID, getImageDescriptor("icons/browser/d_forward.gif"));
		reg.put(E_REFRESH_IMG_ID, getImageDescriptor("icons/browser/e_refresh.gif"));
		reg.put(D_REFRESH_IMG_ID, getImageDescriptor("icons/browser/d_refresh.gif"));
		reg.put(E_STOP_IMG_ID, getImageDescriptor("icons/browser/e_stop.gif"));
		reg.put(D_STOP_IMG_ID, getImageDescriptor("icons/browser/d_stop.gif"));
		reg.put(E_GO_IMG_ID, getImageDescriptor("icons/browser/e_go.gif"));
		reg.put(D_GO_IMG_ID, getImageDescriptor("icons/browser/d_go.gif"));
		reg.put(SHOWMENU_IMG_ID, getImageDescriptor("icons/browser/showMenu.gif"));

		//js console images
		reg.put(ERROR_IMG_ID, getImageDescriptor("icons/console/error.gif"));
		reg.put(EXCEPTION_IMG_ID, getImageDescriptor("icons/console/exception.gif"));
		reg.put(INFO_IMG_ID, getImageDescriptor("icons/console/info.gif"));
		reg.put(WARNING_IMG_ID, getImageDescriptor("icons/console/warning.gif"));

		//inspector images
		reg.put(DOCUMENT_IMG_ID, getImageDescriptor("icons/inspector/document.gif"));
		reg.put(ELEMENT_IMG_ID, getImageDescriptor("icons/inspector/element.gif"));
		reg.put(TEXT_IMG_ID, getImageDescriptor("icons/inspector/text.gif"));
		reg.put(WAITING_IMG_ID, getImageDescriptor("icons/inspector/waiting.gif"));
		reg.put(COMMENT_IMG_ID, getImageDescriptor("icons/inspector/comment.gif"));
		reg.put(FILTERBYNAME_IMG_ID, getImageDescriptor("icons/inspector/filterByName.gif"));
		reg.put(FILTERBYCLASS_IMG_ID, getImageDescriptor("icons/inspector/filterByClass.gif"));
		reg.put(FILTERBYID_IMG_ID, getImageDescriptor("icons/inspector/filterById.gif"));

		//css images
		reg.put(HIGHLIGHT_ID, getImageDescriptor("icons/css/highlight.gif"));
		reg.put(ADDPROPERTY_ID, getImageDescriptor("icons/css/add_correction.gif"));
		reg.put(EDITPROPERTY_ID, getImageDescriptor("icons/css/write_obj.gif"));
		reg.put(OPENFILE_ID, getImageDescriptor("icons/css/goto_input.gif"));

		//source images
		reg.put(E_UPDATE_ID, getImageDescriptor("icons/source/e_prev_nav.gif"));
		reg.put(E_REFRESH_ID, getImageDescriptor("icons/source/e_next_nav.gif"));
		reg.put(VALIDATE_ID, getImageDescriptor("icons/source/testok.gif"));

		//xhrmon images
		reg.put(COPYRESPONSE_ID, getImageDescriptor("icons/xhrmon/copy_edit.gif"));
		reg.put(FILTERHTTP_ID, getImageDescriptor("icons/xhrmon/synced.gif"));
		reg.put(FILTERXHR_ID, getImageDescriptor("icons/xhrmon/cfilter.gif"));
		reg.put(CSSFILTER_ID, getImageDescriptor("icons/xhrmon/css_filter.gif"));
		reg.put(HTMLFILTER_ID, getImageDescriptor("icons/xhrmon/html_filter.gif"));
		reg.put(JSFILTER_ID, getImageDescriptor("icons/xhrmon/js_filter.gif"));
		reg.put(IMGFILTER_ID, getImageDescriptor("icons/xhrmon/image_filter.gif"));
		reg.put(DONE_IMAGE, getImageDescriptor("icons/xhrmon/done.gif"));
		reg.put(ERROR_IMAGE, getImageDescriptor("icons/xhrmon/error.gif"));
		reg.put(WARNING_IMAGE, getImageDescriptor("icons/xhrmon/warning.gif"));
		reg.put(RUNNING_IMAGE, getImageDescriptor("icons/xhrmon/waiting.gif"));

		//toolbar images
		reg.put(CLOSE_TB_ID, getImageDescriptor("icons/toolbar/close_toolbar.gif"));

		//watcher images
		reg.put(E_STARTWATCHER_ID, getImageDescriptor("icons/watcher/startWatcher.gif"));
		reg.put(D_STARTWATCHER_ID, getImageDescriptor("icons/watcher/startWatcher_d.gif"));
		reg.put(E_STOPWATCHER_ID, getImageDescriptor("icons/watcher/stopWatcher.gif"));
		reg.put(D_STOPWATCHER_ID, getImageDescriptor("icons/watcher/stopWatcher_d.gif"));

	}

	// TODO Inline
	public Image getImage(String imageID) {
		return getImageRegistry().get(imageID);

	}

	// TODO Inline
	public ImageDescriptor getImageDescriptorFromRegistry(String imageID) {
		return getImageRegistry().getDescriptor(imageID);
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initializePreferences();

		ServiceReference reference = context.getServiceReference(IApplicationEventAdmin.class.getName());
		appEventAdmTracker = new ServiceTracker(context, reference, null);
		appEventAdmTracker.open();

		callListManager = new CallListsManager();
		getApplicationEventAdmin().addEventListener(callListManager);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		getApplicationEventAdmin().removeEventListener(callListManager);
		callListManager = null;

		appEventAdmTracker.close();
		disposeSWTResources();
		super.stop(context);
		plugin = null;
	}

	public CallListsManager getCallListsManager() {
		return callListManager;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MozIDEUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.atf.mozilla.ide.ui", path);
	}

	/**
	 * This method will initialize all the relevant preference in the store if they are not set.
	 */
	protected void initializePreferences() {
		IPreferenceStore store = getPreferenceStore();

		// setting up popup handling to prompt as default
		if (!store.contains(IPreferenceConstants.POPUP_HANDLING)) {
			store.setDefault(IPreferenceConstants.POPUP_HANDLING, IPreferenceConstants.POPUP_HANDLING_PROMPT);
		}
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
	 * If debugging is enabled for this plugin, Logs throwable.
	 * @param message
	 */
	public static void debug(Throwable e) {
		if (getDefault().isDebugging()) {
			log(e);
		}
	}

	private Resource createSWTResource(String key) {
		if (CSS_VIEWER_FONT.equals(key)) {
			FontData fontData = new FontData("Courier New", 9, SWT.NORMAL);
			return new Font(Display.getCurrent(), fontData);
		}

		throw new IllegalArgumentException();
	}

	/**
	 * Used to cache plug-in SWT resources. Only one resource for given key will be created.
	 * Resources are disposed in {@link #stop()}. Resources are created in {@link #createImageRegistry()}.
	 * Only resources that are constantly used over whole plug-in life should be stored here. 
	 * 
	 * @param key
	 * @return
	 */
	public Resource getSWTResource(String key) {
		Resource resource = (Resource) swtResources.get(key);
		if (resource == null) {
			resource = createSWTResource(key);
			swtResources.put(key, resource);
		}

		return resource;
	}

	private void disposeSWTResources() {
		for (Iterator i = swtResources.values().iterator(); i.hasNext();) {
			Resource resource = (Resource) i.next();
			if (!resource.isDisposed())
				resource.dispose();
		}
		swtResources.clear();
	}

	public IApplicationEventAdmin getApplicationEventAdmin() {
		return (IApplicationEventAdmin) appEventAdmTracker.getService();
	}
}