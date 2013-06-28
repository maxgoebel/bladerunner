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

public interface ATFDebugConstants {

	/**
	 * Name of the project which contains the programs used for testing the
	 * debugger.
	 */
	public static final String DEBUG_PROJECT_NAME = "DebugTests";

	/**
	 * Plug-in-relative path which contains the source code of the programs used
	 * for testing the debugger.
	 */
	public static final String TEST_SRC_DIR = "testprograms";

	/**
	 * Folder for launch configurations.
	 */
	public static final String LAUNCH_CONFIGURATIONS_DIR = "launchConfigurations";

	/**
	 * HTML file extension. I'm sure there is some shared constant somewhere for
	 * this.
	 * 
	 * TODO find (and use) the shared constant for this.
	 */
	public static final String HTML_EXT = ".html";

}
