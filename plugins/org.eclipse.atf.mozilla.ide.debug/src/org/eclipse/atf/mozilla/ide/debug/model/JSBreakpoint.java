/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.debug.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;

public abstract class JSBreakpoint extends Breakpoint {

	public String getModelIdentifier() {
		return MozillaDebugPlugin.DEBUG_MODEL_ID;
	}

	/**
	 * Returns the Workspace resource used to contain markers for IStorage resources,
	 * which can't contain markers by themselves.
	 * @return IResource
	 */
	public static IResource getStorageMarkerResource() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Adds the breakpoint information to the BreakpointManager
	 * @exception CoreException
	 */
	private void registerBreakpoint() throws CoreException {
		DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(this);
	}

	protected void createMarker(final IResource resource, final String markerType, final boolean register, final Map attributes) {
		IWorkspaceRunnable body = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker(markerType);

				Map fAttrs = (attributes != null) ? attributes : new HashMap();
				fAttrs.put(IBreakpoint.ENABLED, new Boolean(true));
				fAttrs.put(IBreakpoint.ID, getModelIdentifier());
				marker.setAttributes(fAttrs);
				setMarker(marker);

				if (register) {
					registerBreakpoint();
				}
				setPersisted(true);
			}
		};

		try {
			ResourcesPlugin.getWorkspace().run(body, null);
		} catch (CoreException ce) {
			MozillaDebugPlugin.log(ce);
		}
	}

	public abstract String getLabel();
}
