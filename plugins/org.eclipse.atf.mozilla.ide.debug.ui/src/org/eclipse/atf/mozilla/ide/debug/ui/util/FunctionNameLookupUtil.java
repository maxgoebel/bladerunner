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
package org.eclipse.atf.mozilla.ide.debug.ui.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.atf.mozilla.ide.core.util.SourceLocatorUtil;
import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugScriptElement;
import org.eclipse.atf.mozilla.ide.debug.model.JSSourceLocator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;

/*
 * This is a singleton utility class used to guess the name of a 
 * JavaScript closure. It caches the latest InputStream and marks
 * it at the end of every guess. This is done to speed up the common
 * case of getting all the labels for all the closures inside a 
 * JavaScript file. As long as they come from the same file and in
 * ascending line number order, this utility class will perform faster
 * because it does not go back to re-read the file.
 */
public class FunctionNameLookupUtil {

	private static FunctionNameLookupUtil instance = null;

	public static FunctionNameLookupUtil getInstance() {
		if (instance == null)
			instance = new FunctionNameLookupUtil();

		return instance;
	}

	private FunctionNameLookupUtil() {
	};

	/*cached elements*/
	private InputStream cachedIS = null;
	private String cachedISLocation = "";
	private int linesRead = 0;

	private InputStream getScriptContents(JSDebugScriptElement scriptElement) throws CoreException {
		String location = scriptElement.getLocation();
		try {
			if (location != null) {
				//cache match
				if (cachedISLocation.equals(location) && cachedIS != null) {
					return cachedIS;
				} else {
					//re-read
					URL locationURL = new URL(location);
					String appBase = ((JSSourceLocator) scriptElement.getLaunch().getSourceLocator()).getAppBase();
					IProject project = ((JSSourceLocator) scriptElement.getLaunch().getSourceLocator()).getProject();
					cachedIS = SourceLocatorUtil.getInstance().getSourceContent(locationURL, appBase, project);
					cachedISLocation = location;
					linesRead = 0;

					if (cachedIS.markSupported())
						cachedIS.mark(1024); //only buffers 1k
					return cachedIS;
				}
			}
		} catch (MalformedURLException mue) {
			IStatus status = new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, "Invalid URL supplied by Mozilla.  Unable to open source.", mue); //$NON-NLS-1$
			DebugPlugin.log(status);

			throw new CoreException(new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, "Incorrect URL <" + location + ">" /*TODO:i18n*/, mue));
		}

		return null;
	}

	private void clearCache() {
		cachedIS = null;
		cachedISLocation = "";
		linesRead = 0;
	}

	private final Pattern _propertyPattern = Pattern.compile("(\\w+)\\s*[:=]\\s*$");

	public synchronized String guessFunction(JSDebugScriptElement scriptElement) {
		String guess = null;
		//TODO: need to cache these
		try {
			int i, ch;
			Vector lines = new Vector();
			long base = scriptElement.getLineStart();

			if (base < linesRead) {
				clearCache();
			}

			InputStream stream = getScriptContents(scriptElement);
			stream.reset(); //go back to last mark

			for (i = linesRead; i < base; i++) {

				//always mark at the begining of the last line read
				if (i == base - 1 && cachedIS.markSupported()) {
					cachedIS.mark(1024); //only buffers 1k
				}

				StringBuffer line = new StringBuffer();
				do {
					ch = stream.read();
					if (ch == -1)
						return null;
					if (ch != '\n')
						line.append((char) ch);
				} while (ch != '\n');

				lines.add(line.toString());
			}
			linesRead = (int) base - 1; //this reader is always one line behind

			String line = (String) lines.lastElement();
			String match = "function";//script.getFunctionSource().split("\n")[0];
			int offset = line.indexOf(match);
			if (offset == -1)
				return null; // something went wrong; function wasn't declared on the line reported?
			StringBuffer region = new StringBuffer(line.substring(0, offset));
			//inserting up to previous 3 lines into the search
			int linesJustReadCount = lines.size();
			for (int j = 2; (j < 5) && (linesJustReadCount - j > 0); j++) {
				region.insert(0, (String) lines.elementAt(linesJustReadCount - j));
			}

			Matcher m = _propertyPattern.matcher(region.toString());
			if (m.find()) {
				guess = m.group(1);
			}
			//			MozillaDebugUIPlugin.debug("tag="+script.getTag());
		} catch (CoreException ce) {
			MozillaDebugPlugin.log(ce);
			//abort and return null
		} catch (IOException ioe) {
			MozillaDebugPlugin.log(ioe);
		}
		return guess;
	}
}
