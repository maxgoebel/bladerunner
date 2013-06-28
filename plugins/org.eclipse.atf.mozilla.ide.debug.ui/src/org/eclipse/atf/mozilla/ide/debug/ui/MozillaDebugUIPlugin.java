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

package org.eclipse.atf.mozilla.ide.debug.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElement;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugTarget;
import org.eclipse.atf.mozilla.ide.debug.model.JSBreakpoint;
import org.eclipse.atf.mozilla.ide.debug.ui.internal.adapter.JSBreakpointTypeAdapterFactory;
import org.eclipse.atf.mozilla.ide.debug.ui.scriptview.ScriptView;
import org.eclipse.atf.mozilla.ide.ui.browser.MozBrowserProcess;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MozillaDebugUIPlugin extends AbstractUIPlugin implements IStartup, IDebugEventSetListener, IPerspectiveListener {

	//The shared instance.
	private static MozillaDebugUIPlugin _plugin;
	//Resource bundle.
	private ResourceBundle _resourceBundle;
	final public static String PLUGIN_ID = "org.eclipse.atf.mozilla.ide.debug.ui"; //$NON-NLS-1$
	private EventListener eventListener;

	protected static String[] JSDEBUGVIEWS = { ScriptView.ID

	};

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin = this;

		try {
			_resourceBundle = ResourceBundle.getBundle("org.eclipse.atf.mozilla.ide.debug.launch.internal.MozillaDebugUIResources"); //$NON-NLS-1$
		} catch (MissingResourceException mre) {
			_resourceBundle = null;
		}

		//This is used to control the opening of certain JS Debug View only when 
		//debugging JS
		IWorkbenchWindow w = MozillaDebugUIPlugin.getActiveWorkbenchWindow();
		if (w != null)
			w.addPerspectiveListener(this);
		else {
			//because of earlystartup, need to hook listener to wait for tha Active Workbench Window to open
			getWorkbench().addWindowListener(new IWindowListener() {

				public void windowActivated(IWorkbenchWindow window) {
					if (window == MozillaDebugUIPlugin.getActiveWorkbenchWindow()) {
						window.addPerspectiveListener(MozillaDebugUIPlugin.this);
						getWorkbench().removeWindowListener(this);
					}
				}

				public void windowClosed(IWorkbenchWindow window) {
				}

				public void windowDeactivated(IWorkbenchWindow window) {
				}

				public void windowOpened(IWorkbenchWindow window) {
				}

			});
		}

		//EventListener to detect if code changes while debugging
		eventListener = new EventListener();
		DebugPlugin.getDefault().addDebugEventListener(eventListener);

		IAdapterManager manager = Platform.getAdapterManager();
		IAdapterFactory typeFactory = new JSBreakpointTypeAdapterFactory();
		manager.registerAdapters(typeFactory, JSBreakpoint.class);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		if (eventListener != null) {
			DebugPlugin.getDefault().removeDebugEventListener(eventListener);
		}

		IWorkbenchWindow w = MozillaDebugUIPlugin.getActiveWorkbenchWindow();
		if (w != null)
			w.removePerspectiveListener(this);

		_plugin = null;
		_resourceBundle = null;

	}

	/**
	 * Returns the shared instance.
	 */
	public static MozillaDebugUIPlugin getDefault() {
		return _plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	//	public static ImageDescriptor getImageDescriptor(String path) {
	//		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.atf.mozilla.ide.debug.ui", path);
	//	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MozillaDebugUIPlugin.getDefault().getResourceBundle();
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

	/**
	 * Returns a unique string representing the Flash Debug Plug-in.
	 * @return String
	 */
	//	public static String getPluginId() {
	//		return getDefault().getDescriptor().getUniqueIdentifier();
	//	}	

	/**
	 * Returns the active workbench window.
	 * @return IWorkbenchWindow
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		IWorkbenchWindow win = null;
		IWorkbench w = getDefault().getWorkbench();
		if (w != null)
			win = w.getActiveWorkbenchWindow();

		return win;
	}

	/**
	 * Returns the active workbench shell.
	 * Note: This method should only be called from a UI thread.
	 * When running on a non-UI thread, use getShell() method.
	 * @return Shell
	 */
	public static Shell getActiveWorkbenchShell() {
		Shell s = null;
		IWorkbenchWindow win = getActiveWorkbenchWindow();
		if (win != null)
			s = win.getShell();
		return s;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * This method listents to the DebugEvent to open the necessary Views for debugging JavaScript
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		if (events != null) {
			int size = events.length;
			for (int i = 0; i < size; i++) {
				DebugEvent event = (DebugEvent) events[i];
				if (event.getKind() == DebugEvent.CREATE) {
					Object obj = events[i].getSource();

					if (!(obj instanceof JSDebugElement))
						continue;

					IWorkbenchWindow window = getDefault().getWorkbench().getActiveWorkbenchWindow();
					if (window != null) {
						final IWorkbenchPage page = window.getActivePage();

						Job job = new UIJob("debug output") {
							public IStatus runInUIThread(IProgressMonitor monitor) {
								openJSDebugViews(page);
								return Status.OK_STATUS;
							}
						};
						job.schedule();
					}

				}
			}
		}
	}

	protected void openJSDebugViews(IWorkbenchPage page) {

		for (int i = 0; i < MozillaDebugUIPlugin.JSDEBUGVIEWS.length; i++) {
			String viewID = MozillaDebugUIPlugin.JSDEBUGVIEWS[i];
			IViewPart part = page.findView(viewID);
			if (part == null) {
				try {
					page.showView(viewID);
				} catch (PartInitException e) {
					ErrorDialog.openError(page.getWorkbenchWindow().getShell(), "Error Opening View <" + viewID + ">!", //$NON-NLS-1$
							e.getMessage(), e.getStatus());
				}
			}
		}

	}

	public void perspectiveActivated(final IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		//check if the perspective is the debug perspective
		if ("org.eclipse.debug.ui.DebugPerspective".equals(perspective.getId())) {
			//look to see of there is a JSDebugElement active in the Debug View
			IDebugTarget[] targets = DebugPlugin.getDefault().getLaunchManager().getDebugTargets();

			boolean openedViews = false;
			for (int i = 0; i < targets.length && !openedViews; i++) {
				if (targets[i] instanceof JSDebugTarget) {

					//ignore all terminated and disconnected targets
					if (!targets[i].isTerminated() && !targets[i].isDisconnected()) {
						Job job = new UIJob("debug output") {
							public IStatus runInUIThread(IProgressMonitor monitor) {
								openJSDebugViews(page);
								return Status.OK_STATUS;
							}
						};
						job.schedule();
						openedViews = true;
					}

					break;
				}
			}

			//check if the views where opened, if not, then listen to DebugEvents for the creation of one
			//add as DebugEventSetListener
			DebugPlugin.getDefault().addDebugEventListener(this);
		} else {
			//remove the listening to DebugEvents if the DebugPerspective is not the one Active
			DebugPlugin.getDefault().removeDebugEventListener(this);

		}

	}

	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
	}

	public void earlyStartup() {
	}

	private class EventListener implements IDebugEventSetListener {
		public void handleDebugEvents(DebugEvent[] events) {
			if (events != null) {
				int size = events.length;
				for (int i = 0; i < size; i++) {
					DebugEvent event = (DebugEvent) events[i];
					int kind = event.getKind();
					if (event.getKind() == DebugEvent.MODEL_SPECIFIC) {
						final Object obj = events[i].getSource();

						if (!(obj instanceof JSDebugTarget))
							continue;
						final Object data = events[i].getData();
						if (data instanceof IStatus) {
							IStatus status = (IStatus) data;
							Job job = new org.eclipse.ui.progress.UIJob("debug output") { //$NON-NLS-1$
								public IStatus runInUIThread(IProgressMonitor monitor) {
									IStatus status = (IStatus) data;
									JSDebugTarget target = (JSDebugTarget) obj;
									String title = MozillaDebugUIMessages.MozillaDebugUI_3;
									int severity = MessageDialog.WARNING;
									String[] buttons = new String[] { MozillaDebugUIMessages.MozillaDebugUI_4, MozillaDebugUIMessages.MozillaDebugUI_5 };
									if (status.getSeverity() == IStatus.ERROR) {
										title = MozillaDebugUIMessages.MozillaDebugUI_6;
										severity = MessageDialog.ERROR;
										buttons = new String[] { MozillaDebugUIMessages.MozillaDebugUI_7 };
									}
									Shell shell = getActiveWorkbenchShell();
									MessageDialog dialog = new MessageDialog(shell, title, null, status.getMessage(), severity, buttons, 0);
									int button = dialog.open();
									if (button == 1) {
										try {
											target.terminate();
										} catch (DebugException e) {
											String errorMessage = MozillaDebugUIMessages.MozillaDebugUI_9;
											status = new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, errorMessage, e);
											ErrorDialog.openError(shell, null, null, status);
										}
									}
									return Status.OK_STATUS;
								}
							};
							job.schedule();
						}

					} else if (event.getKind() == DebugEvent.TERMINATE) {
						final Object obj = events[i].getSource();
						if (obj instanceof MozBrowserProcess) {
							final Object data = events[i].getData();
							Job job = new org.eclipse.ui.progress.UIJob("debug output") { //$NON-NLS-1$
								public IStatus runInUIThread(IProgressMonitor monitor) {
									IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
									if (activeWindow != null) {
										IWorkbenchPage activePage = activeWindow.getActivePage();
										if (activePage != null) {
											IEditorReference references[] = activePage.getEditorReferences();
											for (int j = 0; j < references.length; j++) {
												IEditorPart part = references[j].getEditor(false);
												if (part != null) {
													Object browser = part.getAdapter(Browser.class);
													if (browser != null) {
														if (browser == (Browser) data) {
															activePage.closeEditor(part, false);
														}
													}
												}

											}
										}

									}
									return Status.OK_STATUS;
								}
							};
							job.schedule();
						}
					}
				}
			}
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
}
