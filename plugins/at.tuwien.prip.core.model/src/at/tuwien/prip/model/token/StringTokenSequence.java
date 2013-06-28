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
package at.tuwien.prip.model.token;

/**
 * 
 * @author max
 *
 */
public class StringTokenSequence extends TokenSequence<StringToken> {

	protected final String SEPARATOR = " ";

	public StringTokenSequence() {
		super();
	}
	
	/**
	 * Constructor.
	 * @param input
	 */
	public StringTokenSequence(String input) {
		String[] words = input.split("\\s");

		for (int i=0; i<words.length; i++) {
			String word = words[i];
//			word = word.replaceAll("\\W", ""); //remove non word characters
			
			if (word.length()>1) {
				StringToken wordToken = new StringToken(word);
				add(wordToken);
			}
				
		}
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (StringToken token : tokenList) {
			sb.append(token.name+SEPARATOR);
		}
		return sb.toString();
	}
}
