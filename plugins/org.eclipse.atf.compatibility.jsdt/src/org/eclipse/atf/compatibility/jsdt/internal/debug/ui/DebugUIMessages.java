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
package org.eclipse.atf.compatibility.jsdt.internal.debug.ui;

import org.eclipse.osgi.util.NLS;

public class DebugUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.atf.compatibility.jsdt.internal.debug.ui.DebugUIMessages";//$NON-NLS-1$
    public static String JDIDebugUIPlugin_0;
	public static String JDIDebugUIPlugin_Error_1;
    public static String JDIDebugUIPlugin_4;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, DebugUIMessages.class);
	}

}
