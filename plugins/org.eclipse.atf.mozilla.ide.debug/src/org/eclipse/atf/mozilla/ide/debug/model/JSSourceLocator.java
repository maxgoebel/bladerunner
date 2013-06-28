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

package org.eclipse.atf.mozilla.ide.debug.model;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.atf.mozilla.ide.core.util.SourceLocatorUtil;
import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugStackFrame;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IStackFrame;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JSSourceLocator implements IPersistableSourceLocator {

	private IProject _project;
	private String _appBase;

	protected SourceLocatorUtil locatorUtil = SourceLocatorUtil.getInstance();

	public String getMemento() throws CoreException {
		// TODO Auto-generated method stub
		MozillaDebugPlugin.debug("getMemento");
		return null;
	}

	public void initializeDefaults(ILaunchConfiguration configuration) throws CoreException {
		// TODO Auto-generated method stub
		MozillaDebugPlugin.debug("initializeDefaults");

	}

	public void initializeFromMemento(String memento) throws CoreException {
		// TODO Auto-generated method stub
		MozillaDebugPlugin.debug("initializeFromMemento");
	}

	/**
	 * Get the path of the url reference relative to the application base 
	 * 
	 * @param location url reference to code which may or may not be in the current project
	 * @return path relative to application base, which should match path relative to "WebContent" directory
	 * 	or null if it does not match the application base
	 */
	public IPath getContentRelativePath(String location) {
		if (_project != null && location.startsWith(_appBase)) {
			return new Path(location.substring(_appBase.length()));
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISourceLocator#getSourceElement(org.eclipse.debug.core.model.IStackFrame)
	 */
	public Object getSourceElement(IStackFrame stackFrame) {

		Object result = null;
		String location = ((JSDebugStackFrame) stackFrame).getLocation();
		try {
			if (location != null) {
				URL locationURL = new URL(location);
				result = locatorUtil.getSourceElement(locationURL, _appBase, _project);
			}
		} catch (MalformedURLException mue) {
			IStatus status = new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, "Invalid URL supplied by Mozilla.  Unable to open source.", mue); //$NON-NLS-1$
			DebugPlugin.log(status);
			//fallthrough
		}

		return result;
	}

	public boolean matches(IBreakpoint breakpoint, String scriptURI) {
		IResource res = breakpoint.getMarker().getResource();
		boolean match = false;
		JSLineBreakpoint jsBPoint = null;
		if (breakpoint instanceof JSLineBreakpoint) {
			jsBPoint = (JSLineBreakpoint) breakpoint;
		} else {
			return match;
		}
		if (res instanceof IWorkspaceRoot) {
			try {
				String id = jsBPoint.getID();
				URL url = new URL(scriptURI);
				String path = url.toString();
				match = path.equals(id);
			} catch (MalformedURLException mue) {
				match = false;
			}
		} else {
			//This block assumes that the breakpoint must be in the local workbench
			try {
				IStorage localResource = SourceLocatorUtil.getInstance().getSourceElement(new URL(scriptURI), _appBase, _project);
				match = res.equals(localResource);
			} catch (MalformedURLException mue) {
				IStatus status = new Status(IStatus.WARNING, MozillaDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, "javascript: URLs are not handled.", mue); //$NON-NLS-1$
				DebugPlugin.log(status);
				match = false;
			}
		}
		return match;
	}

	public void setProject(IProject project) {
		_project = project;
	}

	public void setAppBase(String appBase) {
		_appBase = appBase;
	}

	//TEMPORARY GETTERS UNTIL BETTER APPROACH
	public IProject getProject() {
		return _project;
	}

	public String getAppBase() {
		return _appBase;
	}

}