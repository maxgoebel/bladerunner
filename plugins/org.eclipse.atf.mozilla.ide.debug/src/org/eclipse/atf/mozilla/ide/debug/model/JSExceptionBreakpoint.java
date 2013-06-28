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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;

public class JSExceptionBreakpoint extends JSBreakpoint {

	public static final String BREAKPOINT_ID = "org.eclipse.atf.mozilla.ide.debug.JSExceptionBreakpointMarker";

	public static JSExceptionBreakpoint createBreakpoint() {
		IResource resource = getStorageMarkerResource();

		try {
			IMarker[] markers = resource.findMarkers(BREAKPOINT_ID, false, IResource.DEPTH_ZERO);
			if (markers.length > 0) {
				return (JSExceptionBreakpoint) DebugPlugin.getDefault().getBreakpointManager().getBreakpoint(markers[0]);
			}
		} catch (CoreException e) {
		}

		JSExceptionBreakpoint breakpoint = new JSExceptionBreakpoint();
		breakpoint.createMarker(resource, BREAKPOINT_ID, true, null);
		return breakpoint;
	}

	public String getLabel() {
		return "JavaScript Exception Breakpoint";
	}
}
