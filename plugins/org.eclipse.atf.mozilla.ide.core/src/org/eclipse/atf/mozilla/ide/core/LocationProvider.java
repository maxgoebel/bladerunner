/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies - ongoing enhancements
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.mozilla.xpcom.IAppFileLocProvider;

public class LocationProvider implements IAppFileLocProvider {

	private File libXULPath;
	private File profile;
	private File history;

	public LocationProvider(File aBinPath, File aProfileDir) throws IOException {
		libXULPath = aBinPath;
		profile = aProfileDir;

		if (!libXULPath.exists() || !libXULPath.isDirectory()) {
			throw new FileNotFoundException("libxul directory specified is not valid: " + libXULPath.getAbsolutePath());
		}
		if (profile != null && (!profile.exists() || !profile.isDirectory())) {
			throw new FileNotFoundException("profile directory specified is not valid: " + profile.getAbsolutePath());
		}

		// create history file
		if (profile != null) {
			setupProfile();
		}
	}

	private void setupProfile() throws IOException {
		history = new File(profile, "history.dat");
		if (!history.exists()) {
			history.createNewFile();
		}
	}

	public File getFile(String aProp, boolean[] aPersistent) {
		File file = null;
		if (aProp.equals("GreD") || aProp.equals("GreComsD")) { //$NON-NLS-1$
		//			file = new File(grePath);
			file = libXULPath;
			if (aProp.equals("GreComsD")) { //$NON-NLS-1$
				file = new File(file, "components"); //$NON-NLS-1$
			}
		} else if (aProp.equals("MozBinD") || //$NON-NLS-1$
				aProp.equals("CurProcD") || //$NON-NLS-1$
				aProp.equals("ComsD")) //$NON-NLS-1$
		{
			file = libXULPath;
			if (aProp.equals("ComsD")) { //$NON-NLS-1$
				file = new File(file, "components"); //$NON-NLS-1$
			}
		} else if (aProp.equals("ProfD")) { //$NON-NLS-1$
			return profile;
		} else if (aProp.equals("UHist")) { //$NON-NLS-1$
			return history;
		}
		//		else {
		//			MozideCorePlugin.debug("LocationProvider::getFile() => unhandled property = " + aProp);
		//		}

		return file;
	}

	public File[] getFiles(String aProp) {
		File[] files = null;
		if (aProp.equals("APluginsDL")) { //$NON-NLS-1$
			files = new File[1];
			files[0] = new File(libXULPath, "plugins");
			//		} else {
			//			MozideCorePlugin.debug("LocationProvider::getFiles() => unhandled property = " + aProp);
		}

		return files;
	}

}
