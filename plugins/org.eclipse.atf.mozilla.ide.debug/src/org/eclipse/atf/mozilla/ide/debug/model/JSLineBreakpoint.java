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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.debug.JSDebugCoreMessages;
import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.ILineBreakpoint;

public class JSLineBreakpoint extends JSBreakpoint implements ILineBreakpoint {

	// defined in org.eclipse.wst.sse.ui.internal.StructuredResourceMarkerAnnotationModel
	public static final String STORAGE_ID = "org.eclipse.wst.sse.ui.extensions.breakpoint.path";
	public static final String BREAKPOINT_ID = "org.eclipse.atf.mozilla.ide.debug.JSLineBreakpointMarker";

	private int _lineNumber;
	private String _id;

	/**
	 * Required for Debug API to persist breakpoints
	 */
	public JSLineBreakpoint() {
		super();
	}

	/**
	 * The constructor required by the editor.
	 */
	public JSLineBreakpoint(IStorage storage, int lineNumber, int charStart, int charEnd, boolean register, Map map) {
		if (map == null)
			map = new HashMap();

		IResource resource;

		if (storage instanceof IFile) {
			resource = (IFile) storage;
		} else {
			resource = getStorageMarkerResource();
			map.put(STORAGE_ID, storage.getName());
		}

		map.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
		map.put(IMarker.CHAR_START, new Integer(charStart));
		map.put(IMarker.CHAR_END, new Integer(charEnd));
		createMarker(resource, BREAKPOINT_ID, register, map);
	}

	public void setMarker(IMarker marker) throws CoreException {
		super.setMarker(marker);
		_lineNumber = ((Integer) marker.getAttribute(IMarker.LINE_NUMBER)).intValue();
		_id = (String) marker.getAttribute(STORAGE_ID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.Breakpoint#setAttribute(java.lang.String, boolean)
	 */
	protected void setAttribute(String attributeName, boolean value) throws CoreException {

		super.setAttribute(attributeName, value);

		if (ENABLED.equals(attributeName))
			DebugPlugin.getDefault().getBreakpointManager().fireBreakpointChanged(this);
	}

	public String getLabel() {
		try {
			IMarker marker = getMarker();
			IResource res = marker.getResource();
			String file;
			if (res instanceof IWorkspaceRoot) {
				file = getID();
			} else {
				file = marker.getResource().getName();
			}
			String line = Integer.toString(getLineNumber());
			return MessageFormat.format(JSDebugCoreMessages.JSDebugCore_5, new Object[] { file, line });
		} catch (CoreException ce) {
			MozillaDebugPlugin.log(ce);
			return JSDebugCoreMessages.JSDebugCore_4;
		}
	}

	public String getID() {
		return _id;
	}

	public int getLineNumber() throws CoreException {
		// Try to get the line number from the marker. Use the stored line number
		// when the marker is not accessable. This seems to occur in the 
		// Mozilla callback when removing a breakpoint.
		try {
			_lineNumber = ((Integer) getMarker().getAttribute(IMarker.LINE_NUMBER)).intValue();
		} catch (CoreException e1) {

		}
		return _lineNumber;
	}

	public int getCharEnd() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IMarker.CHAR_END, -1);
		}
		return -1;
	}

	public int getCharStart() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IMarker.CHAR_START, -1);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	//	public boolean equals(Object that) {
	//		if (that instanceof JSLineBreakpoint) {
	//			try {
	//				JSLineBreakpoint bp = (JSLineBreakpoint)that;
	//				return (bp.getLineNumber() == getLineNumber()) &&
	//					getResource().equals(bp.getResource());
	//			} catch (CoreException ce) {
	//			}
	//		}
	//		return super.equals(that);
	//	}
}
