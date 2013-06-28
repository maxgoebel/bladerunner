/*******************************************************************************
 * Copyright (c) 2009 nexB Inc. and EasyEclipse.org. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     nexB Inc. and EasyEclipse.org - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/
package org.eclipse.atf.debug.test;

import static org.eclipse.atf.debug.test.ATFDebugConstants.HTML_EXT;
import static org.eclipse.atf.debug.test.ATFDebugConstants.LAUNCH_CONFIGURATIONS_DIR;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.atf.mozilla.ide.debug.model.JSLineBreakpoint;
import org.eclipse.atf.ui.debug.ILaunchConfigurationConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;

/**
 * {@link TestHelper} is a collection of utility methods that aggregate common
 * functions required by debugger test cases.
 */
public class TestHelper {

	/**
	 * The name of the HTTP preview server used for deployment of the test
	 * programs.
	 */
	protected static final String HTTP_SERVER_NAME = "TestServer"; //$NON-NLS-1$

	/**
	 * ID for the ATF launch configuration type we're using.
	 * 
	 * TODO: See if we can reuse some ATF constant that contains it.
	 */
	private static final String WEB_APP_TYPE = "org.eclipse.atf.ui.debug.webApplication"; //$NON-NLS-1$

	/**
	 * Default time (in milliseconds) we're willing to wait for a breakpoint
	 * before declaring failure.
	 */
	private static final int BKP_TIMEOUT = 10000;

	/**
	 * Default time (in milliseconds) we're willing to wait for a process to
	 * terminate before declaring failure.
	 */
	private static final int TERMINATE_TIMEOUT = 20000;

	/**
	 * Reference to the test project.
	 */
	private IProject _project;

	/**
	 * Reference to this project's associated {@link IVirtualComponent}.
	 */
	private IVirtualComponent _component;

	/**
	 * Reference to the {@link ILaunch} object for the last successful launch
	 * operation.
	 */
	private ILaunch _current;

	/**
	 * Returns the launch manager.
	 */
	public ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	/**
	 * Creates a new, pre-configured launch configuration for the test project (
	 * {@link ATFDebugConstants#DEBUG_PROJECT_NAME}). The created launch
	 * configuration will have the same name as the HTML file it launches.
	 * 
	 * @param mainHtml
	 *            the HTML file that should be launched.
	 * 
	 * @throws CoreException
	 *             if something goes wrong.
	 */
	public void createLaunchConfiguration(String mainHtml) throws CoreException {

		ILaunchConfigurationType type = getLaunchManager()
				.getLaunchConfigurationType(WEB_APP_TYPE);

		ILaunchConfigurationWorkingCopy config = type.newInstance(getProject()
				.getFolder(LAUNCH_CONFIGURATIONS_DIR), mainHtml);

		// Performs the configuration.
		config.setAttribute(ILaunchConfigurationConstants.IS_INTERNAL_BROWSER,
				true);
		config.setAttribute(ILaunchConfigurationConstants.PROCESS_TYPE,
				getProject().getName());
		config.setAttribute(ILaunchConfigurationConstants.APP_PATH, mainHtml
				+ HTML_EXT);
		config.setAttribute(ILaunchConfigurationConstants.PROJECT,
				ATFDebugConstants.DEBUG_PROJECT_NAME);
		config.doSave();
	}

	/**
	 * Returns the test project.
	 */
	public IProject getProject() {

		if (_project == null) {
			_project = ResourcesPlugin.getWorkspace().getRoot().getProject(
					ATFDebugConstants.DEBUG_PROJECT_NAME);
		}

		return _project;
	}

	/**
	 * Synchronously sets a breakpoint at the specified line of the specified
	 * file. This method never returns <b>null</b>. If the breakpoint isn't
	 * placed inside of the allowed time interval (see
	 * {@link TestHelper#BKP_TIMEOUT}), an {@link AssertionFailedError} will be
	 * thrown.
	 * 
	 * @param filename
	 *            The name of the file in which to set the breakpoint into.
	 * 
	 * @param line
	 *            The line in <b>filename</b> that should contain the
	 *            breakpoint.
	 * 
	 * @return The created {@link IBreakpoint}.
	 * 
	 * @throws CoreException
	 *             if something goes wrong while setting the breakpoint.
	 */
	public IBreakpoint setBreakpoint(String filename, int line)
			throws CoreException {

		IResource resource = getProject().findMember(filename);

		if (!(resource instanceof IFile)) {
			return null;
		}

		IFile file = (IFile) resource;

		BreakpointWaiter waiter = new BreakpointWaiter(resource, line,
				BreakpointWaiter.ADDITION);

		JSLineBreakpoint bkp = new JSLineBreakpoint(file, line, -1, -1, true,
				null);

		Assert.assertTrue("Breakpoint was not placed in the allowed time.",
				waiter.await(BKP_TIMEOUT));

		return bkp;
	}

	/**
	 * Synchronously removes a breakpoint that has been previously set. If the
	 * breakpoint isn't removed inside of the allowed time interval (see
	 * {@link TestHelper#BKP_TIMEOUT}), an {@link AssertionFailedError} will be
	 * thrown.
	 * 
	 * @param bkp
	 */
	public void removeBreakpoint(IBreakpoint bkp) {

		BreakpointWaiter waiter = new BreakpointWaiter(bkp,
				BreakpointWaiter.REMOVAL);
		try {
			bkp.delete();
		} catch (CoreException ex) {
			ex.printStackTrace();
		}

		Assert.assertTrue("Breakpoint was not removed in the allowed time.", //$NON-NLS-1$ 
				waiter.await(BKP_TIMEOUT));
	}

	/**
	 * Launches a previously created ATF configuration.
	 * 
	 * @param configName
	 *            the name of the configuration to be launched.
	 * 
	 * @throws CoreException
	 *             if launch fails.
	 * 
	 * @throws IllegalArgumentException
	 *             if an ATF launch configuration named <code>configName</code>
	 *             isn't found.
	 * 
	 * @throws IllegalStateException
	 *             if {@link #terminateAndWait(boolean)} hasn't been called
	 *             after the last call to this method.
	 * 
	 */
	public void launch(String configName) throws CoreException {

		if (_current != null) {
			throw new IllegalStateException(
					"Previous launch has not been terminated.");
		}

		ILaunchManager mgr = getLaunchManager();

		ILaunchConfigurationType type = mgr
				.getLaunchConfigurationType(WEB_APP_TYPE);

		ILaunchConfiguration configs[] = mgr.getLaunchConfigurations(type);
		for (ILaunchConfiguration config : configs) {
			if (config.getName().equalsIgnoreCase(configName)) {
				_current = config.launch(ILaunchManager.DEBUG_MODE,
						new NullProgressMonitor());
				return;
			}
		}

		throw new IllegalArgumentException(
				"Could not find launch configuration " + configName + "."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Terminates the process launched by the last call to
	 * {@link #launch(String)}. If the process cannot be terminated inside of
	 * the allowed time ({@link #TERMINATE_TIMEOUT}), an
	 * {@link AssertionFailedError} will be thrown.
	 * 
	 * @throws Exception
	 *             if something goes wrong while terminating.
	 * 
	 * @throws IllegalStateException
	 *             if there is no process to terminate (i.e. if the last
	 *             launched process (by {@link #launch(String)}) has already
	 *             been terminated by a previous call to this method).
	 * 
	 */
	public void terminateAndWait() throws Exception {

		if (_current == null) {
			throw new IllegalStateException("Nothing to terminate.");
		}

		ILaunch launch = _current;
		_current = null;

		DebugEventWaiter waiter = new DebugEventWaiter(DebugEvent.TERMINATE,
				null, IDebugTarget.class);

		launch.getDebugTarget().terminate();

		Assert.assertTrue("Process took too long to terminate. " + //$NON-NLS-1$
				"Remaining tests may not run correctly. ", waiter
				.doWait(TERMINATE_TIMEOUT)); //$NON-NLS-1$

		Assert.assertEquals(launch.getDebugTarget(), waiter.getSource());
	}

	/**
	 * Convenience method. Launches the specified configuration, and waits until
	 * a certain breakpoint has been hit before returning. If the breakpoint
	 * isn't hit by any thread inside of the allowed timeframe (
	 * {@link #BKP_TIMEOUT}), an {@link AssertionFailedError} will be thrown.
	 * 
	 * @param bkp
	 *            The breakpoint that must be hit in order for this method to
	 *            return normally.
	 * 
	 * @param configName
	 *            The name of the configuration to be launched.
	 * 
	 * @param deleteAfterwards
	 *            If set to true, causes the {@link IBreakpoint} specified in
	 *            paramter <b>bkp</b> to be deleted when this method returns
	 *            (either normally, or abnormally).
	 * 
	 * @return The first thread to hit the breakpoint. This method never returns
	 *         <b>null</b>.
	 * 
	 * @throws Exception
	 *             if something goes wrong during the process.
	 * 
	 */
	public IThread launchToBreakpoint(IBreakpoint bkp, String configName,
			boolean deleteAfterwards) throws Throwable {

		try {
			ATFBreakpointWaiter waiter = new ATFBreakpointWaiter(bkp);

			launch(configName);

			Assert.assertTrue("Breakpoint was " + //$NON-NLS-1$
					"not hit inside of the allowed time.", waiter //$NON-NLS-1$
					.doWait(BKP_TIMEOUT));

			return waiter.getSource();
		} finally {
			if (deleteAfterwards) {
				bkp.delete();
			}
		}
	}

	public void step(IThread thread, IStepper stepper) throws Exception {

		TestCase.assertTrue(thread.isSuspended());

		ThreadEventWaiter waiter = new ThreadEventWaiter(thread,
				DebugEvent.SUSPEND, DebugEvent.STEP_END);

		stepper.doStep(thread);

		Assert.assertTrue("Step event took too long to be produced.", waiter
				.doWait(BKP_TIMEOUT));
	}

	public void verifyThreadState(IThread thread, int line, int stacksize,
			String topFunctionName) throws DebugException {

		verifyThreadState(thread, line, stacksize,
				new String[] { topFunctionName });

	}

	public void verifyThreadState(IThread thread, int line, int stacksize,
			String[] functionNames) throws DebugException {

		IStackFrame[] frames = thread.getStackFrames();

		TestCase.assertEquals(stacksize, frames.length);
		TestCase.assertEquals(line, frames[0].getLineNumber());
		TestCase.assertTrue("Too few stackframes.", //$NON-NLS-1$ 
				frames.length >= functionNames.length);

		for (int i = 0; i < Math.min(frames.length, functionNames.length); i++) {
			TestCase.assertEquals(frames[i].getName(), functionNames[i]);
		}
	}

	public IServer getHTTPServer() throws Exception {

		IServer httpServer = findServer(HTTP_SERVER_NAME);

		if (httpServer == null) {
			httpServer = createHTTPServer(HTTP_SERVER_NAME);
		}

		return httpServer;
	}

	public IServer createHTTPServer(String httpServerName) throws Exception {

		NullProgressMonitor mon = new NullProgressMonitor();

		IRuntimeType runtimeType = ServerCore
				.findRuntimeType("org.eclipse.wst.server.preview.runtime"); //$NON-NLS-1$

		IRuntimeWorkingCopy runtimeCopy = runtimeType.createRuntime(
				"HTTP Preview", mon); //$NON-NLS-1$

		IRuntime runtime = runtimeCopy.save(true, mon);

		IServerType serverType = ServerCore
				.findServerType("org.eclipse.wst.server.preview.server"); //$NON-NLS-1$

		IServerWorkingCopy workingCopy = serverType.createServer(
				httpServerName, null, runtime, mon);
		workingCopy.setName(httpServerName);
		workingCopy.setHost("localhost"); //$NON-NLS-1$

		return workingCopy.save(true, mon);
	}

	public IPath getWebContentsDir() {
		return getVirtualComponent().getRootFolder().getProjectRelativePath();
	}

	private IVirtualComponent getVirtualComponent() {
		if (_component == null) {
			_component = ComponentCore.createComponent(getProject());
		}

		return _component;
	}

	public IBreakpoint ensureBreakpoint(String file, int line)
			throws CoreException {
		IBreakpoint jsBkp = setBreakpoint(
				getWebContentsDir() + "/" + file, line); //$NON-NLS-1$
		TestCase.assertNotNull(jsBkp);
		return jsBkp;
	}

	private IServer findServer(String httpServerName) {
		IServer servers[] = ServerCore.getServers();
		for (int i = 0; i < servers.length; i++) {
			if (servers[i].getId().equals(httpServerName)) {
				return servers[i];
			}
		}

		return null;
	}

	public static interface IStepper {
		public boolean canStep(IThread thread);

		public void doStep(IThread thread) throws DebugException;

		public int getDetail();
	}

	public static final IStepper STEP_INTO = new IStepper() {

		public boolean canStep(IThread thread) {
			return thread.canStepInto();
		}

		public void doStep(IThread thread) throws DebugException {
			thread.stepInto();
		}

		public int getDetail() {
			return DebugEvent.STEP_INTO;
		}

	};

	public static final IStepper STEP_OVER = new IStepper() {

		public boolean canStep(IThread thread) {
			return thread.canStepOver();
		}

		public void doStep(IThread thread) throws DebugException {
			thread.stepOver();
		}

		public int getDetail() {
			return DebugEvent.STEP_OVER;
		}
	};

	public static final IStepper STEP_RETURN = new IStepper() {

		public boolean canStep(IThread thread) {
			return thread.canStepReturn();
		}

		public void doStep(IThread thread) throws DebugException {
			thread.stepReturn();
		}

		public int getDetail() {
			return DebugEvent.STEP_RETURN;
		}
	};
}
