/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.compatibility.jsdt.debug.ui.actions;

import org.eclipse.osgi.util.NLS;

public class ActionMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.atf.compatibility.jsdt.internal.debug.ui.actions.ActionMessages";//$NON-NLS-1$

	public static String BreakpointLocationVerifierJob_0;
	public static String BreakpointLocationVerifierJob_breakpoint_location;
	public static String BreakpointLocationVerifierJob_not_valid_location;
	public static String BreakpointLocationVerifierJob_breakpoint_set;
	public static String BreakpointLocationVerifierJob_breakpointRemoved;
	public static String BreakpointLocationVerifierJob_breakpointMovedToValidPosition;
	public static String ToggleBreakpointAdapter_3;
	public static String BreakpointLocationVerifierJob_breakpointSetToRightType;
	
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, ActionMessages.class);
	}


}
