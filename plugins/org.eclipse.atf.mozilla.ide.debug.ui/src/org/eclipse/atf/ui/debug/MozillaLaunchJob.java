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

package org.eclipse.atf.ui.debug;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.core.IXPCOMThreadProxyHelper;
import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugTarget;
import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.XPCOMThreadProxyHelper;
import org.eclipse.atf.mozilla.ide.ui.browser.MozBrowserProcess;
import org.eclipse.atf.mozilla.ide.ui.browser.util.MozBrowserUtil;
import org.eclipse.atf.mozilla.ide.ui.internal.browser.support.InternalWebBrowser;
import org.eclipse.atf.mozilla.ide.ui.internal.browser.support.WorkbenchBrowserSupport;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

public class MozillaLaunchJob extends UIJob {

	protected ILaunch launch;
	protected URL url;

	public MozillaLaunchJob(String name, ILaunch launch, URL url) {
		super(name);
		this.launch = launch;
		this.url = url;
	}

	public IStatus runInUIThread(IProgressMonitor monitor) {

		IStatus status = Status.OK_STATUS;

		try {
			launchApp();
		} catch (Exception e) {
			status = new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, IStatus.OK, "Error launching Mozilla application in debug mode", e);
		}

		return status;
	}

	protected void launchApp() throws CoreException {
		boolean useInternalBrowser = launch.getLaunchConfiguration().getAttribute(ILaunchConfigurationConstants.IS_INTERNAL_BROWSER, true);

		if (useInternalBrowser) {
			launchInternalBrowser();
		} else {
			launchExternalBrowser();
		}
	}

	private void launchExternalBrowser() throws CoreException {
		String browserExec = launch.getLaunchConfiguration().getAttribute(ILaunchConfigurationConstants.BROWSER_EXEC, "");
		String[] args = new String[] { browserExec, url.toString() };
		String[] args2 = new String[] { browserExec, "-venkman" };

		try {
			Process myProcess = Runtime.getRuntime().exec(args);
			String myLabel = url.toExternalForm();
			HashMap attrs = new HashMap();
			attrs.put(IProcess.ATTR_PROCESS_TYPE, getName());
			attrs.put(IProcess.ATTR_PROCESS_LABEL, getName() + " Application"); // label on the console
			DebugPlugin.newProcess(launch, myProcess, myLabel, attrs);
			Runtime.getRuntime().exec(args2);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, IStatus.OK, "Error launching application", e));
		}
	}

	private void launchInternalBrowser() throws CoreException, DebugException {
		IWebBrowser webBrowser = getBrowser();

		if ("debug".equals(launch.getLaunchMode())) {
			configureJSDebugTarget(webBrowser);
		}

		if ("run".equals(launch.getLaunchMode())) {
			MozBrowserUtil.openMozillaPerspective();
		}
	}

	private IWebBrowser getBrowser() throws CoreException {
		String browserId = launch.getLaunchConfiguration().getAttribute(ILaunchConfigurationConstants.PICK_BROWSER, (String) null);

		IWebBrowser webBrowser = null;

		if (browserId != null) {
			org.eclipse.ui.browser.IWebBrowser browser = WorkbenchBrowserSupport.getInstance().getBrowser(browserId);
			if ((browser != null) && (browser instanceof InternalWebBrowser)) {
				webBrowser = ((InternalWebBrowser) browser).getWebBrowser();
			}
		}

		if (webBrowser == null) {
			webBrowser = MozBrowserUtil.openMozillaBrowser(url.toString());
		}

		return webBrowser;
	}

	private void configureJSDebugTarget(IWebBrowser webBrowser) throws CoreException, DebugException {
		// Start the debugger on the Display thread
		String appPath = launch.getLaunchConfiguration().getAttribute(ILaunchConfigurationConstants.APP_PATH, (String) null);
		String projectName = launch.getLaunchConfiguration().getAttribute(ILaunchConfigurationConstants.PROJECT, (String) null);
		IProject project = null;
		if (projectName != null && !projectName.equals(""))
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		Display display = Display.getDefault();
		// XPCOM calls in the debugger will be delegated to the Display thread by this helper object
		IXPCOMThreadProxyHelper proxyHelper = new XPCOMThreadProxyHelper(display);
		NestedEventLoop eventLoop = new NestedEventLoop(display);
		JSDebugTarget debugTarget = (JSDebugTarget) MozillaDebugPlugin.launchDebugTarget(launch, url, project, appPath, eventLoop, proxyHelper);
		launch.addDebugTarget(debugTarget);
		debugTarget.setProcess(new MozBrowserProcess(webBrowser, launch));
	}
}
