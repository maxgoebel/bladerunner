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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;

public class JSFunctionBreakpoint extends JSBreakpoint {

	public static final String BREAKPOINT_ID = "org.eclipse.atf.mozilla.ide.debug.JSFunctionBreakpointMarker";

	/**
	 * Required for Debug API to persist breakpoints
	 */
	public JSFunctionBreakpoint() {
		super();
	}

	public JSFunctionBreakpoint(String functionName) {
		IResource resource = getStorageMarkerResource();
		Map map = new HashMap();
		map.put("functionName", functionName);
		createMarker(resource, BREAKPOINT_ID, true, map);
	}

	public String getLabel() {
		return getFunctionName();
	}

	public static JSFunctionBreakpoint createBreakpoint(String functionName) {
		IResource resource = getStorageMarkerResource();

		try {
			IMarker[] markers = resource.findMarkers(BREAKPOINT_ID, false, IResource.DEPTH_ZERO);
			if (markers.length > 0) {
				for (int i = 0; i < markers.length; i++) {
					if (functionName.equals(markers[i].getAttribute("functionName", null)))
						return (JSFunctionBreakpoint) DebugPlugin.getDefault().getBreakpointManager().getBreakpoint(markers[0]);
				}
			}
		} catch (CoreException e) {
		}

		JSFunctionBreakpoint breakpoint = new JSFunctionBreakpoint(functionName);
		return breakpoint;
	}

	public String getFunctionName() {
		String functionName = getMarker().getAttribute("functionName", null);
		return functionName;
	}

}
