/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. All rights reserved. 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/

package org.eclipse.swt.browser;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

public class XULRunnerInitializer {

	static String[] bundles = { "org.mozilla.xulrunner", "org.mozilla.xulrunner.win32.win32.x86", "org.mozilla.xulrunner.carbon.macosx", "org.mozilla.xulrunner.gtk.linux.x86" };

	static final String PLUGIN_ID = "org.eclipse.atf.mozilla.swt.browser";
	static final String XULRUNNER_PATH = "org.eclipse.swt.browser.XULRunnerPath"; //$NON-NLS-1$

	static {
		initialize();
	}

	private static void initialize() {
		/* TODO REMOVE ALL org.mozilla.xulrunner.<platform>
		 * This is 1.8 relic. We keep it for transition period backward compatibility but this should be removed
		 * once we fully switch to 1.9 xulrunner packages.
		 */
		URL url = null;
		for (int i = 0; i < bundles.length && url == null; i++) {
			Bundle bundle = Platform.getBundle(bundles[i]);
			if (bundle != null)
				url = bundle.getResource("/xulrunner");
		}

		if (url == null) {
			log(new Status(IStatus.ERROR, PLUGIN_ID, "XULRunner not found", null));
			return;
		}

		File xulRunnerRoot = null;
		try {
			URL localPath = FileLocator.resolve(url);
			xulRunnerRoot = new File(FileLocator.toFileURL(localPath).getFile());
		} catch (IOException e) {
			log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
		}

		if (xulRunnerRoot != null) {
			System.setProperty(XULRUNNER_PATH, xulRunnerRoot.getAbsolutePath());
		}
	}

	private static void log(IStatus status) {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		Platform.getLog(bundle).log(status);
	}
}
