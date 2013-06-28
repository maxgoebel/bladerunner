/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.dbai.bladeRunner.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import at.tuwien.prip.model.token.StringTokenSequence;

public class TextUtils {

	// contains at least 1 punctuation
	public static String punctRegex = "^[\\p{Punct}]+$";

	public static Pattern punctPattern;

	static {
		punctPattern = Pattern.compile(punctRegex);
	}

	/**
	 * 
	 * Extract all sentences from an input string. Can understand abbreviations.
	 * 
	 * @param input
	 * @return
	 */
	public static List<StringTokenSequence> splitIntoSentences(String input) {
		List<StringTokenSequence> result = new ArrayList<StringTokenSequence>();

		String[] sents = input.split("\\.|\\?|\\!");
		String tmp = "";

		for (int i = 0; i < sents.length; i++) {
			tmp += sents[i];

			StringTokenSequence sts = new StringTokenSequence(tmp);

			if (i < sents.length - 1) {
				String lookAhead = sents[i + 1];
				if (lookAhead.length() > 1) {
					int c1 = lookAhead.charAt(0);
					int c2 = lookAhead.charAt(1);
					if (c1 == 32 && c2 >= 65 && c2 <= 90) { // space then
															// capital letter
						result.add(sts);
						tmp = "";
					} else {
						tmp += ".";// + Character.toString(lookAhead.charAt(0));
					}
				}
			} else {
				result.add(sts);
			}
		}

		return result;
	}

	/**
	 * Extract individual words from a string...
	 * 
	 * @param input
	 * @return
	 */
	public static List<String> splitIntoWords(String input) {
		List<String> result = new LinkedList<String>();

		// mcg: careful, add \xA0 explicitly for non-breaking space (ascii 160)
		String[] words = input.split("\\n|\\r|\\t|\\s|\\xA0");
		for (String word : words) {
			String[] parts = word.split("\\p{Punct}");
			if (parts.length > 0) {
				String s = "";
				for (int i = 0; i < word.length(); i++) {
					char iChar = word.charAt(i);
					if (checkPunct("" + iChar)) {
						result.add(s);
						result.add("" + iChar);
						s = "";
					} else {
						s += iChar;
					}
				}
				if (s.length() > 0) {
					result.add(s);
				}
			} else {
				result.add(word);
			}
		}
		return result;
	}

	/**
	 * 
	 * First, generate a string of individual regex matches. Then, parse
	 * generated string against concept regexs.
	 * 
	 * @param input
	 * @return
	 */
	public static boolean checkPunct(String input) {
		if (punctPattern.matcher(input).find())
			return true;

		return false;
	}
}
