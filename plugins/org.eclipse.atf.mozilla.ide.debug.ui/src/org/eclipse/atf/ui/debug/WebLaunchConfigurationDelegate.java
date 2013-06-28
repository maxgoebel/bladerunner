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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

public class WebLaunchConfigurationDelegate extends LaunchConfigurationDelegate /*implements IServerListener*/{

	protected String appPath;
	protected String projectName;
	protected IProject project;
	protected String applicationName;
	protected String moduleName;
	protected boolean isUrl;
	protected ILaunch launch;
	protected String serverMode = "run";
	protected String mode = "run";

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {

		this.launch = launch;
		this.mode = mode;
		int work = 100;
		monitor.beginTask("Launching web application", work);

		// TODO remove when multiple debug targets supported
		if (mode.equals("debug")) {
			IDebugTarget[] targets = DebugPlugin.getDefault().getLaunchManager().getDebugTargets();
			for (int i = 0; i < targets.length; i++) {
				if (targets[i].getModelIdentifier().equals(MozillaDebugPlugin.DEBUG_MODEL_ID)) {
					if (!targets[i].isTerminated()) {
						String errorMessage = "Multiple JavaScript Debug Sessions is not supported. " + "Terminate the active session before starting a new session.";
						IStatus status = new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, Status.ERROR, errorMessage, null);
						throw new CoreException(status);
					}
				}
			}
		}

		appPath = configuration.getAttribute(ILaunchConfigurationConstants.APP_PATH, (String) null);
		applicationName = configuration.getAttribute(ILaunchConfigurationConstants.PROCESS_TYPE, (String) null);
		isUrl = configuration.getAttribute(ILaunchConfigurationConstants.IS_URL, false);

		launchApp();

		monitor.worked(work / 2);
		monitor.done();

	}

	private void launchApp() throws CoreException {

		URL url = null;
		if (isUrl) {
			try {
				url = new URL(appPath);
			} catch (MalformedURLException e) {
				throw new CoreException(new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, IStatus.OK, "Unable to parse URL:" + appPath, e));
			}
		} else {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(appPath));
			//			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(FlexibleProjectUtils.getWebContentPath(project,appPath));
			if (!file.exists()) {
				throw new CoreException(new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, IStatus.OK, "File doesn't exists in workspace: " + appPath, null));
			}

			try {
				url = file.getLocationURI().toURL();
			} catch (MalformedURLException e) {
				throw new CoreException(new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, IStatus.OK, "Malformed URL:" + file.getLocationURI(), e));
			}
		}
		
		Job mozillaJob = new MozillaLaunchJob(applicationName, launch, url);
		mozillaJob.schedule();
	}

	private void showError(String title, String message, Exception e) {
		StatusManager.getManager().handle(new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, message, e));
	}

	private IWorkbenchWindow getActiveWorkbenchWindow() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows.length > 0)
				window = windows[0];
		}
		return window;
	}

}
