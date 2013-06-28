/*******************************************************************************
 * Copyright (c) 2008 nexB Inc. and EasyEclipse.org. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     nexB Inc. and EasyEclipse.org - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.debug.test;

import static org.eclipse.atf.debug.test.ATFDebugConstants.DEBUG_PROJECT_NAME;
import static org.eclipse.atf.debug.test.ATFDebugConstants.LAUNCH_CONFIGURATIONS_DIR;
import static org.eclipse.atf.debug.test.ATFDebugConstants.TEST_SRC_DIR;

import java.io.File;

import junit.framework.Assert;

import org.eclipse.atf.debug.testplugin.ATFDebugTestPlugin;
import org.eclipse.atf.debug.testplugin.ATFProjectHelper;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.ServerPlugin;

/**
 * This is not really a test case, but just a code snippet that is run before
 * all the tests in the test suite, and sets up the projects used for testing.
 */
public class ProjectCreatorTest extends AbstractATFTest {

	public void testBuildTestProjects() throws Exception {
		setUpPreferences();
		IProject project = createBaseProject();
		createLaunchConfigurations(project);
		initializeServer(project);
	}

	private IProject createBaseProject() throws Exception {

		IProject project = getHelper().getProject();
		if (project.exists()) {
			project.delete(true, true, null);
		}

		project = ATFProjectHelper.createATFProject(DEBUG_PROJECT_NAME);

		Assert.assertTrue(project.exists());

		// Imports the source.
		File source = ATFDebugTestPlugin.getDefault().getFileInPlugin(
				new Path(TEST_SRC_DIR));

		ATFProjectHelper.importFilesFromDirectory(source, new Path(
				DEBUG_PROJECT_NAME + "/" + getHelper().getWebContentsDir())); //$NON-NLS-1$

		return project;
	}

	private void createLaunchConfigurations(IProject project)
			throws CoreException {

		// create launch configuration folder
		IFolder folder = project.getFolder(LAUNCH_CONFIGURATIONS_DIR);
		if (folder.exists()) {
			folder.delete(true, null);
		}
		folder.create(true, true, null);

		// delete any existing launch configs
		ILaunchConfiguration[] configs = getHelper().getLaunchManager()
				.getLaunchConfigurations();
		for (int i = 0; i < configs.length; i++) {
			configs[i].delete();
		}

		// Create our launch configurations.
		getHelper().createLaunchConfiguration("Breakpoints"); //$NON-NLS-1$
		getHelper().createLaunchConfiguration("Eval"); //$NON-NLS-1$

	}

	private void initializeServer(IProject project) throws Exception {

		IModuleArtifact[] artifacts = ServerPlugin.getModuleArtifacts(project);
		Assert.assertNotNull(artifacts);
		Assert.assertEquals(1, artifacts.length);
		IModule module = artifacts[0].getModule();

		IServer httpServer = getHelper().getHTTPServer();
		Assert.assertNotNull(httpServer);

		httpServer.stop(true);

		ServerCore.setDefaultServer(module, httpServer,
				new NullProgressMonitor());
	}

	private void setUpPreferences() {
		IPreferenceStore debugUIPreferences = DebugUIPlugin.getDefault()
				.getPreferenceStore();

		// Don't prompt for perspective switching
		debugUIPreferences.setValue(
				IInternalDebugUIConstants.PREF_SWITCH_PERSPECTIVE_ON_SUSPEND,
				MessageDialogWithToggle.ALWAYS);
		debugUIPreferences.setValue(
				IInternalDebugUIConstants.PREF_SWITCH_TO_PERSPECTIVE,
				MessageDialogWithToggle.ALWAYS);
		debugUIPreferences.setValue(
				IInternalDebugUIConstants.PREF_RELAUNCH_IN_DEBUG_MODE,
				MessageDialogWithToggle.NEVER);
		debugUIPreferences.setValue(
				IInternalDebugUIConstants.PREF_WAIT_FOR_BUILD,
				MessageDialogWithToggle.ALWAYS);
		debugUIPreferences.setValue(
				IInternalDebugUIConstants.PREF_CONTINUE_WITH_COMPILE_ERROR,
				MessageDialogWithToggle.ALWAYS);
		debugUIPreferences
				.setValue(
						IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH,
						MessageDialogWithToggle.NEVER);

		String property = System.getProperty("debug.workbenchActivation");

		boolean activate = property != null && property.equals("on");

		debugUIPreferences.setValue(
				IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, activate);
		debugUIPreferences.setValue(
				IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, activate);
		debugUIPreferences.setValue(
				IInternalDebugUIConstants.PREF_ACTIVATE_DEBUG_VIEW, activate);
		debugUIPreferences.setValue(IDebugUIConstants.PREF_ACTIVATE_WORKBENCH,
				activate);
	}
}
