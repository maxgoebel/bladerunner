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

package org.eclipse.atf.mozilla.ide.ui.console;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mozilla.interfaces.nsIConsoleMessage;
import org.mozilla.interfaces.nsIScriptError;

public class ConsoleMessageLabelProvider extends LabelProvider implements ITableLabelProvider {

	protected static final int IMAGE_COLUMN_INDEX = 0;
	protected static final int CATEGORY_COLUMN_INDEX = 1;
	protected static final int MSG_COLUMN_INDEX = 1;
	protected static final int SRCNAME_COLUMN_INDEX = 2;
	protected static final int LINENUM_COLUMN_INDEX = 3;

	public Image getColumnImage(Object element, int columnIndex) {

		//check if it's the first column (the only one that contains an image)
		if (columnIndex == IMAGE_COLUMN_INDEX) {

			//handle simple String messages
			if (element instanceof String) {
				return getInfoImage();
			}

			nsIConsoleMessage consoleMessage = (nsIConsoleMessage) element;

			//test if it is an nsIScriptError type
			try {
				nsIScriptError scriptError = (nsIScriptError) consoleMessage.queryInterface(nsIScriptError.NS_ISCRIPTERROR_IID);

				if (scriptError.getFlags() == nsIScriptError.errorFlag)
					return getErrorImage();
				else if (scriptError.getFlags() == nsIScriptError.warningFlag)
					return getWarningImage();
				else if (scriptError.getFlags() == nsIScriptError.exceptionFlag)
					return getExceptionImage();
			} catch (Exception e) {
				//nothing , the message is not an nsIScriptError type
			}
			return getInfoImage();
		} else if (columnIndex == CATEGORY_COLUMN_INDEX) {
			if (element instanceof nsIConsoleMessage) {
				nsIConsoleMessage consoleMessage = (nsIConsoleMessage) element;

				//test if it is an nsIScriptError type
				try {
					nsIScriptError scriptError = (nsIScriptError) consoleMessage.queryInterface(nsIScriptError.NS_ISCRIPTERROR_IID);
					String category = scriptError.getCategory();
					if (category.indexOf("CSS") != -1) {
						return getCSSImage();
					}
					if (category.indexOf("javascript") != -1) {
						return getJSImage();
					}
					if (category.indexOf("XML") != -1 || category.indexOf("malformed-xml") != -1) {
						return getXMLImage();
					}
				} catch (Exception e) {
					//nothing , the message is not an nsIScriptError type
				}
			}
		}

		return null;

	}

	public String getColumnText(Object element, int columnIndex) {

		//first column is always an image
		if (columnIndex == IMAGE_COLUMN_INDEX)
			return null;

		if (columnIndex == MSG_COLUMN_INDEX) {
			return getMessage(element);
		}

		if (columnIndex == SRCNAME_COLUMN_INDEX) {
			return getSourceName(element);
		}

		if (columnIndex == LINENUM_COLUMN_INDEX) {
			int line = getLineNum(element);
			return line == -1 ? "" : Integer.toString(line);
		}

		return null;
	}

	public static int getLineNum(Object element) {
		if (!(element instanceof nsIConsoleMessage))
			return -1;

		nsIConsoleMessage consoleMessage = (nsIConsoleMessage) element;

		//test if it is an nsIScriptError type
		try {
			nsIScriptError scriptError = (nsIScriptError) consoleMessage.queryInterface(nsIScriptError.NS_ISCRIPTERROR_IID);
			String line = scriptError.getSourceLine();

			// support location information in error message, like:
			// uncaught exception: [Exception... "Access to restricted URI denied"  code: "1012" nsresult: "0x805303f4 (NS_ERROR_DOM_BAD_URI)"  location: "file:///some/file/sample.html Line: 18"]
			if ("".equals(line)) {
				String error = scriptError.getErrorMessage();
				String location = getQuotedStringAfter("location:", error);
				if (location != null) {
					int lineOffset = location.indexOf("Line:");
					String linePart = location.substring(lineOffset + "Line:".length()).trim();

					try {
						return Integer.parseInt(linePart);
					} catch (NumberFormatException e) {
						// not what we looked for
					}
				}

			}
			return (int) scriptError.getLineNumber();
		} catch (Exception e) {
			//nothing , the message is not an nsIScriptError type
		}

		return -1;
	}

	private static String getQuotedStringAfter(String string, String text) {
		int offset = text.indexOf(string);
		if (offset == -1)
			return null;

		int startQuote = text.indexOf('"', offset);
		if (startQuote == -1)
			return null;

		int endQuote = text.indexOf('"', startQuote + 1);

		return text.substring(startQuote + 1, endQuote);
	}

	public static String getSourceName(Object element) {
		if (!(element instanceof nsIConsoleMessage))
			return null;

		nsIConsoleMessage consoleMessage = (nsIConsoleMessage) element;

		//test if it is an nsIScriptError type
		try {
			nsIScriptError scriptError = (nsIScriptError) consoleMessage.queryInterface(nsIScriptError.NS_ISCRIPTERROR_IID);
			String sourceName = scriptError.getSourceName();

			// support location information in error message, like:
			// uncaught exception: [Exception... "Access to restricted URI denied"  code: "1012" nsresult: "0x805303f4 (NS_ERROR_DOM_BAD_URI)"  location: "file:///some/file/sample.html Line: 18"]
			if ("".equals(sourceName)) {
				String error = scriptError.getErrorMessage();
				String location = getQuotedStringAfter("location:", error);

				if (location != null) {
					int urlEnd = location.indexOf(' ');
					String url = urlEnd == -1 ? location : location.substring(0, urlEnd);

					try {
						new URL(url);
						return url;
					} catch (MalformedURLException e) {
						// not what we looked for
					}
				}
			}
			return sourceName;
		} catch (Exception e) {
			//nothing , the message is not an nsIScriptError type
		}

		return null;
	}

	private String getMessage(Object element) {
		if (element instanceof String) {
			return element.toString();
		}

		if (!(element instanceof nsIConsoleMessage))
			return null;

		nsIConsoleMessage consoleMessage = (nsIConsoleMessage) element;

		//test if it is an nsIScriptError type
		try {
			nsIScriptError scriptError = (nsIScriptError) consoleMessage.queryInterface(nsIScriptError.NS_ISCRIPTERROR_IID);
			// support location information in error message, like:
			// uncaught exception: [Exception... "Access to restricted URI denied"  code: "1012" nsresult: "0x805303f4 (NS_ERROR_DOM_BAD_URI)"  location: "file:///some/file/sample.html Line: 18"]
			String message = scriptError.getErrorMessage();
			//if (message.startsWith(""))
			//scriptError.queryInterface(nsIE);
			//String location = getQuotedStringAfter("location:", error);

			return message;
		} catch (Exception e) {
			//nothing , the message is not an nsIScriptError type
		}

		return consoleMessage.getMessage();
	}

	protected Image getCSSImage() {
		return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.CSSFILTER_ID);
	}

	protected Image getJSImage() {
		return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.JSFILTER_ID);
	}

	protected Image getXMLImage() {
		return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.HTMLFILTER_ID);
	}

	protected Image getInfoImage() {
		return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.INFO_IMG_ID);
	}

	protected Image getErrorImage() {
		return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.ERROR_IMG_ID);
	}

	protected Image getWarningImage() {
		return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.WARNING_IMG_ID);
	}

	protected Image getExceptionImage() {
		return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.EXCEPTION_IMG_ID);
	}

}
