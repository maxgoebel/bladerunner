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

import java.net.URL;

import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

public class WebLaunchShortcut implements ILaunchShortcut {

	public WebLaunchShortcut() {
		super();
	}

	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			searchAndLaunch(((IStructuredSelection) selection).toArray(), mode, getWebLaunchConfigType());
		}

	}

	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IFile file = (IFile) input.getAdapter(IFile.class);
		if (file != null) {
			searchAndLaunch(new Object[] { file }, mode, getWebLaunchConfigType());
		}

	}

	protected ILaunchConfigurationType getWebLaunchConfigType() {
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		return lm.getLaunchConfigurationType("org.eclipse.atf.ui.debug.webApplication");
	}

	public void searchAndLaunch(Object[] search, String mode, ILaunchConfigurationType configType) {

		IFile file = null;
		int entries = search == null ? 0 : search.length;
		for (int i = 0; i < entries; i++) {
			try {
				String appPathString = null;
				IProject project = null;
				Object obj = search[i];
				boolean isURL = false;

				//TODO: if IProject, offer choices?
				if (obj instanceof IFile) {
					file = (IFile) obj;
					project = file.getProject();
					appPathString = file.getFullPath().toPortableString();
					isURL = false;
				} else if (obj instanceof URL) {
					URL u = (URL) obj;
					appPathString = u.toExternalForm();
					isURL = true;
				} else {
					throw new CoreException(new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, IStatus.OK, "Selection must be a valid application file", null));
				}

				if (appPathString == null) {
					// Could not find target to launch
					throw new CoreException(new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, IStatus.OK, "Could not find launch target for selection", null));
				}

				// Launch the app
				String moduleName = ""; //getModuleName(file);
				ILaunchConfiguration config = findLaunchConfiguration(project.getName(), moduleName, appPathString, isURL, mode, configType);
				if (config != null)
					DebugUITools.launch(config, mode);
				else
					// Could not find launch configuration
					throw new CoreException(new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, IStatus.OK, "Could not find corresponding launch configuration", null));
			} catch (CoreException ce) {
				final IStatus stat = ce.getStatus();
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "An unexpected error has occurred", "Error launching web application", stat);
					}
				});
			}
		}
	}

	/**
	 * Locate a configuration to relaunch for the given type.  If one cannot be found, create one.
	 * 
	 * @return a re-useable config or <code>null</code> if none
	 */
	protected ILaunchConfiguration findLaunchConfiguration(String project, String module, String appPathString, boolean isURL, String mode, ILaunchConfigurationType configType) {
		// TODO: Share shortcut code? or use DebugUITools.openLaunchConfigurationDialog?
		ILaunchConfiguration config = null;

		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);

			int numConfigs = configs == null ? 0 : configs.length;
			for (int i = 0; i < numConfigs; i++) {
				String cPathString = configs[i].getAttribute(ILaunchConfigurationConstants.APP_PATH, (String) null);
				String cProject = configs[i].getAttribute(ILaunchConfigurationConstants.PROJECT, (String) null);
				String cBrowserExec = configs[i].getAttribute(ILaunchConfigurationConstants.BROWSER_EXEC, (String) null);
				boolean cIsURL = configs[i].getAttribute(ILaunchConfigurationConstants.IS_URL, false);
				if (!cIsURL) {
					//TODO normalized path comparison
					if (appPathString.equals(cPathString) && configs[i].supportsMode(mode) && project.equals(cProject) && cBrowserExec != null) {
						config = configs[i].getWorkingCopy();
						break;
					}
				} else {
					if (appPathString.equals(cPathString) && configs[i].supportsMode(mode) && cIsURL == isURL) {
						config = configs[i].getWorkingCopy();
						break;
					}
				}
			}

			if (config == null)
				config = createConfiguration(project, module, appPathString, isURL, configType);
		} catch (CoreException ce) {
			MozillaDebugUIPlugin.log(ce);
		}

		return config;
	}

	/**
	 * Create & return a new configuration
	 */
	protected ILaunchConfiguration createConfiguration(String project, String module, String appPathString, boolean isURL, ILaunchConfigurationType configType) throws CoreException {
		ILaunchConfiguration config = null;
		boolean isInternalBrowser = true;

		String browserLocation = ""; /*CorePlugin.getDefault().getPreferenceStore().getString(IPreferenceConstants.WEB_BROWSER_LOCATION);
										if(browserLocation.equals("") && !isInternalBrowser) {
										throw new CoreException(
										new Status(
										IStatus.ERROR,
										MozillaDebugUIPlugin.ID,
										IStatus.OK,
										"Mozilla executable location preference has not been specified",
										null));
										}*/

		ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, DebugPlugin.getDefault().getLaunchManager().generateUniqueLaunchConfigurationNameFrom(module + "-" + new Path(appPathString).lastSegment())); //TODO 
		wc.setAttribute(ILaunchConfigurationConstants.PROJECT, project);
		wc.setAttribute(ILaunchConfigurationConstants.PROCESS_TYPE, "Mozilla");
		wc.setAttribute(ILaunchConfigurationConstants.IS_URL, isURL);
		wc.setAttribute(ILaunchConfigurationConstants.APP_PATH, appPathString);
		wc.setAttribute(ILaunchConfigurationConstants.BROWSER_EXEC, browserLocation);
		wc.setAttribute(ILaunchConfigurationConstants.IS_INTERNAL_BROWSER, true);
		// Set to empty String and let WebLaunchConfigurationTab build the path.
		wc.setAttribute(ILaunchConfigurationConstants.FULL_PATH, "");

		config = wc.doSave();
		return config;

		//return wc;

	}

}
