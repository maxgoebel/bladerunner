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
package org.eclipse.atf.compatibility.jsdt.internal.debug.ui;

import java.util.Map;

import org.eclipse.atf.compatibility.jsdt.Activator;
import org.eclipse.atf.mozilla.ide.debug.model.JSLineBreakpoint;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.core.JavaScriptCore;

/**
 * Utility class for Java breakpoints 
 */
public class BreakpointUtils {

	/**
	 * Marker attribute storing the handle id of the 
	 * Java element associated with a Java breakpoint
	 */
	private static final String HANDLE_ID = Activator.PLUGIN_ID + ".JAVA_ELEMENT_HANDLE_ID"; //$NON-NLS-1$

	/**
	 * Marker attribute used to denote a run to line breakpoint
	 */
	private static final String RUN_TO_LINE = Activator.PLUGIN_ID + ".run_to_line"; //$NON-NLS-1$

	/**
	 * Marker attribute used to denote the start of the region within a Java
	 * member that the breakpoint is located within.
	 */
	private static final String MEMBER_START = Activator.PLUGIN_ID + ".member_start"; //$NON-NLS-1$

	/**
	 * Marker attribute used to denote the end of the region within a Java
	 * member that the breakpoint is located within.
	 */
	private static final String MEMBER_END = Activator.PLUGIN_ID + ".member_end"; //$NON-NLS-1$

	/**
	 * Returns the resource on which a breakpoint marker should
	 * be created for the given member. The resource returned is the 
	 * associated file, or workspace root in the case of a binary in 
	 * an external archive.
	 * 
	 * @param member member in which a breakpoint is being created
	 * @return resource the resource on which a breakpoint marker
	 *  should be created
	 */
	public static IResource getBreakpointResource(IJavaScriptElement member) {
		IJavaScriptUnit cu = null;
		if (member instanceof IMember)
			cu = ((IMember) member).getJavaScriptUnit();
		else if (member instanceof IJavaScriptUnit)
			cu = (IJavaScriptUnit) member;
		if (cu != null && cu.isWorkingCopy()) {
			member = member.getPrimaryElement();
		}
		IResource res = member.getResource();
		if (res == null) {
			res = ResourcesPlugin.getWorkspace().getRoot();
		} else if (!res.getProject().exists()) {
			res = ResourcesPlugin.getWorkspace().getRoot();
		}
		return res;
	}

	/**
	 * Adds attributes to the given attribute map:<ul>
	 * <li>Java element handle id</li>
	 * <li>Attributes defined by <code>JavaCore</code></li>
	 * </ul>
	 * 
	 * @param attributes the attribute map to use
	 * @param element the Java element associated with the breakpoint
	 * @exception CoreException if an exception occurs configuring
	 *  the marker
	 */
	public static void addJavaBreakpointAttributes(Map attributes, IJavaScriptElement element) {
		String handleId = element.getHandleIdentifier();
		attributes.put(HANDLE_ID, handleId);
		JavaScriptCore.addJavaScriptElementMarkerAttributes(attributes, element);
	}

	/**
	 * Adds attributes to the given attribute map:<ul>
	 * <li>Java element handle id</li>
	 * <li>Member start position</li>
	 * <li>Member end position</li>
	 * <li>Attributes defined by <code>JavaCore</code></li>
	 * </ul>
	 * 
	 * @param attributes the attribute map to use
	 * @param element the Java element associated with the breakpoint
	 * @param memberStart the start position of the Java member that the breakpoint is positioned within
	 * @param memberEnd the end position of the Java member that the breakpoint is positioned within
	 * @exception CoreException if an exception occurs configuring
	 *  the marker
	 */
	public static void addJavaBreakpointAttributesWithMemberDetails(Map attributes, IJavaScriptElement element, int memberStart, int memberEnd) {
		addJavaBreakpointAttributes(attributes, element);
		attributes.put(MEMBER_START, new Integer(memberStart));
		attributes.put(MEMBER_END, new Integer(memberEnd));
	}

	/**
	 * Adds attributes to the given attribute map to make the
	 * breakpoint a run-to-line breakpoint:<ul>
	 * <li>PERSISTED = false</li>
	 * <li>RUN_TO_LINE = true</li>
	 * </ul>
	 * 
	 * @param attributes the attribute map to use
	 * @param element the Java element associated with the breakpoint
	 * @exception CoreException if an exception occurs configuring
	 *  the marker
	 */
	public static void addRunToLineAttributes(Map attributes) {
		attributes.put(IBreakpoint.PERSISTED, Boolean.FALSE);
		attributes.put(RUN_TO_LINE, Boolean.TRUE);
	}

	/**
	 * Returns whether the given breakpoint is a run to line
	 * breakpoint
	 * 
	 * @param breakpoint line breakpoint
	 * @return whether the given breakpoint is a run to line
	 *  breakpoint
	 */
	public static boolean isRunToLineBreakpoint(JSLineBreakpoint breakpoint) {
		return breakpoint.getMarker().getAttribute(RUN_TO_LINE, false);
	}

	public static boolean doesBreakpointExist(IResource res, int lineNumber) {
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = manager.getBreakpoints();
		for (int i = 0; i < breakpoints.length; i++) {
			if (!(breakpoints[i] instanceof JSLineBreakpoint))
				continue;
			JSLineBreakpoint breakpoint = (JSLineBreakpoint) breakpoints[i];
			try {
				if (breakpoint.getMarker().getResource().equals(res) && breakpoint.getLineNumber() == lineNumber) {
					return true;
				}
			} catch (CoreException e) {
				return true;
			}
		}
		return false;
	}

	public static JSLineBreakpoint lineBreakpointExists(IResource resource, String typeName, int lineNumber) {
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = manager.getBreakpoints();
		for (int i = 0; i < breakpoints.length; i++) {
			if (!(breakpoints[i] instanceof JSLineBreakpoint))
				continue;
			JSLineBreakpoint breakpoint = (JSLineBreakpoint) breakpoints[i];
			try {
				if (breakpoint.getMarker().getResource().equals(resource) && breakpoint.getLineNumber() == lineNumber) {
					return breakpoint;
				}
			} catch (CoreException e) {
				return null;
			}
		}
		return null;
	}

	public static JSLineBreakpoint createLineBreakpoint(IDocument document, IEditorInput input, int lineNumber, int charStart, int charEnd, int hitCount, boolean register, Map attributes) {
		if (input instanceof IStorageEditorInput) {
			IStorage storage;
			try {
				storage = ((IStorageEditorInput) input).getStorage();
				return new JSLineBreakpoint(storage, lineNumber, -1, -1, register, attributes);
			} catch (CoreException e) {
				Activator.log(e);
			}
		}
		return null;
	}

}
