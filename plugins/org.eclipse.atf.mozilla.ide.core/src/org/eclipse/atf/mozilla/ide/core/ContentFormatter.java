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
package org.eclipse.atf.mozilla.ide.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Formats string content to a specified content type.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class ContentFormatter {

	private static Pattern INTERNAL_SCRIPT_START = Pattern.compile("<SCRIPT class=\"___ATF_INTERNAL", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static Pattern INTERNAL_SCRIPT_END = Pattern.compile("</SCRIPT>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static Pattern INTERNAL_DIV_START = Pattern.compile("<DIV class=\"___ATF_INTERNAL", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static Pattern INTERNAL_DIV_END = Pattern.compile("</DIV>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	/**
	 * Formats string according to desired format type
	 * @param body - text to format
	 * @param formatType - type of formatting
	 * @return - formatted text
	 */
	public static String format(String body, String formatType) {
		String formatted = body;
		if (formatType.indexOf("xml") != -1 || formatType.indexOf("html") != -1 || body.startsWith("<")) {
			formatted = formatXML(body);
		} else if (formatType.indexOf("json") != -1 || body.startsWith("{") || body.startsWith("[")) {
			formatted = formatJSON(body);
		}
		return formatted;
	}

	private static String formatXML(String body) {
		body = body.replace('\r', '\n');
		body = body.replace('\f', ' ');
		body = body.replace('\t', ' ');
		StringBuffer sb = new StringBuffer(body);
		int level = 0;
		boolean removePreceeding = false;
		boolean removeInternal = false;
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '>') {
				removePreceeding = true;
				removeInternal = false;
			} else if (sb.charAt(i) == '<') {
				while (i - 1 > 0 && (sb.charAt(i - 1) == ' ' || sb.charAt(i - 1) == '\n')) {
					sb.deleteCharAt(i - 1);
					i--;
				}
				removePreceeding = false;
				removeInternal = true;
			} else if (removePreceeding && (sb.charAt(i) == '\n' || sb.charAt(i) == ' ')) {
				sb = sb.deleteCharAt(i);
				i--;
			} else if (removeInternal && (sb.charAt(i) == '\n')) {
				sb = sb.replace(i, i + 1, " ");
			} else {
				removePreceeding = false;
			}
		}
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '<' && i > 0) {
				sb.insert(i, '\n');
				i++;
			} else if (i + 1 < sb.length() && sb.charAt(i) == '>' && sb.charAt(i + 1) != '<') {
				sb.insert(i + 1, '\n');
				i++;
			}
		}
		int prev_level = level;
		for (int i = 0; i < sb.length(); i++) {
			if (i + 3 < sb.length() && sb.charAt(i) == '<' && sb.charAt(i + 1) == '!' && sb.charAt(i + 2) == '-' && sb.charAt(i + 3) == '-') {
				i += 3;
				prev_level = level;
			} else if (i + 2 < sb.length() && sb.charAt(i) == '-' && sb.charAt(i + 1) == '-' && sb.charAt(i + 2) == '>') {
				level = prev_level;
				i += 2;
			}
			if (i + 1 < sb.length() && sb.charAt(i) == '<' && sb.charAt(i + 1) != '/' && sb.charAt(i + 1) != '!') {
				for (int tabs = 0; tabs < level; tabs++) {
					sb.insert(i, '\t');
					i++;
				}
				level++;
			} else if (i + 1 < sb.length() && sb.charAt(i) == '<' && sb.charAt(i + 1) == '/') {
				level--;
				for (int tabs = 0; tabs < level; tabs++) {
					sb.insert(i, '\t');
					i++;
				}
			} else if (i + 1 < sb.length() && sb.charAt(i) == '/' && sb.charAt(i + 1) == '>') {
				level--;
			} else if ((i + 1 < sb.length() && sb.charAt(i) == '\n' && sb.charAt(i + 1) != '<') || (i + 2 < sb.length() && sb.charAt(i + 1) == '<' && sb.charAt(i + 2) == '!')) {
				for (int tabs = 0; tabs < level; tabs++) {
					sb.insert(i + 1, '\t');
					i++;
				}
			}
		}
		return sb.toString();
	}

	private static String formatJSON(String body) {
		StringBuffer sb = new StringBuffer(body);
		boolean waitQuotes = false;
		int level = 0;
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '"' && i > 0 && sb.charAt(i) != '\\') {
				waitQuotes = !waitQuotes;
			} else if (!waitQuotes) {
				if (sb.charAt(i) == '\n' || sb.charAt(i) == '\t') {
					sb.deleteCharAt(i);
					i--;
				}
			}
		}
		waitQuotes = false;
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '"' && i > 0 && sb.charAt(i) != '\\') {
				waitQuotes = !waitQuotes;
			} else if (!waitQuotes) {
				if (sb.charAt(i) == ' ') {
					sb.deleteCharAt(i);
					i--;
				} else if (i + 1 < sb.length() && sb.charAt(i) == '{') {
					level++;
					sb.insert(i + 1, "\n");
					i++;
					for (int tabs = 0; tabs < level; tabs++) {
						sb.insert(i + 1, '\t');
						i++;
					}
				} else if (sb.charAt(i) == '}') {
					level--;
					sb.insert(i, "\n");
					i++;
					for (int tabs = 0; tabs < level; tabs++) {
						sb.insert(i, '\t');
						i++;
					}
				} else if (sb.charAt(i) == ',') {
					sb.insert(i + 1, "\n");
					i++;
					for (int tabs = 0; tabs < level; tabs++) {
						sb.insert(i + 1, '\t');
						i++;
					}
				} else if (sb.charAt(i) == '[') {
					level++;
					sb.insert(i + 1, "\n");
					i++;
					for (int tabs = 0; tabs < level; tabs++) {
						sb.insert(i + 1, '\t');
						i++;
					}
				} else if (i - 1 > 0 && sb.charAt(i) == ']') {
					level--;
					sb.insert(i, "\n");
					i++;
					for (int tabs = 0; tabs < level; tabs++) {
						sb.insert(i, '\t');
						i++;
					}
				} else if (sb.charAt(i) == '\n') {
					for (int tabs = 0; tabs < level; tabs++) {
						sb.insert(i + 1, '\t');
						i++;
					}
				} else if (sb.charAt(i) == ':') {
					sb.insert(i + 1, ' ');
					i++;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Removes the flashing div element from the HTML source using regular expression
	 * matching to the MozideCorePlugin.ATF_INTERNAL
	 * @param htmlNode - html source
	 * @return - html source without flashing div text
	 */
	public static String removeFlashingDiv(String htmlNode) {
		return removeInternalNode(INTERNAL_DIV_START, INTERNAL_DIV_END, htmlNode);
	}

	public static String removeInternalScriptNode(String htmlNode) {
		return removeInternalNode(INTERNAL_SCRIPT_START, INTERNAL_SCRIPT_END, htmlNode);
	}

	private static String removeInternalNode(Pattern start, Pattern end, String htmlNode) {
		Matcher startM = start.matcher(htmlNode);
		Matcher endM = end.matcher(htmlNode);

		List<int[]> regions = new ArrayList();

		while (startM.find()) {
			int startIdx = startM.start();
			int endIdx = -1;
			if (endM.find(startIdx)) {
				endIdx = endM.end();
				regions.add(new int[] { startIdx, endIdx });
			}
		}

		StringBuffer sb = new StringBuffer(htmlNode);
		for (int i = regions.size() - 1; i >= 0; i--) {
			int startIdx = regions.get(i)[0];
			int endIdx = regions.get(i)[1];
			sb.replace(startIdx, endIdx, "");
		}

		return sb.toString();
	}

}
