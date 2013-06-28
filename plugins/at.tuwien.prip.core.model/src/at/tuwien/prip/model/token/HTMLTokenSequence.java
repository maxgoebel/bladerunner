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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;

import org.w3c.dom.Element;

import at.tuwien.prip.model.utils.DOMHelper;



/**
 * 
 * HTMLTokenSequence.java
 *
 *
 *
 * Created: Apr 30, 2009 5:30:31 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class HTMLTokenSequence extends TokenSequence<HTMLTagToken> {

	/**
	 * Constructor.
	 *
	 */
	public HTMLTokenSequence() {
	}

	/**
	 * Copy constructor.
	 * 
	 * @param sequence
	 */
	public HTMLTokenSequence(HTMLTokenSequence sequence) {
		super();
		this.tokenList = new ArrayList<HTMLTagToken>(sequence.tokenList);
	}
	
	/**
	 * Checks if HTML sequence is valid.
	 * 
	 * @return
	 */
	public boolean isValidHTMLSequence () {

		if (length()==0) {
			return true;
		}
		HTMLTagToken first = get(0);
		Element element = first.getElement();
		
		int tokenDepth = DOMHelper.Tree.Descendant.getDepth(element);
		int seqDepth = getLowestHTMLDepth();

		return tokenDepth==seqDepth;
	}
	
	/**
	 * 
	 * 
	 * 
	 * @return
	 */
	public BitSet getRootElementIndices () {
		BitSet result = new BitSet(length());
		int rootDepth = getLowestHTMLDepth();
		
		//iterate over self
		for (int i=0; i<length(); i++) {
			HTMLTagToken token = get(i);
			int td = DOMHelper.Tree.Descendant.getDepth(token.getElement());
			if (td==rootDepth) {
				result.set(i);
			}
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public int getLowestHTMLDepth () {
		int result = 1000;
		Iterator<HTMLTagToken> it = iterator();
		while (it.hasNext()) {
			HTMLTagToken tok = it.next();
			Element e = tok.getElement();
			if (e!=null) {
				int depth = DOMHelper.Tree.Descendant.getDepth(e);
				if (depth<result) {
					result = depth;
				}
			}
		}
		return result==1000?-1:result; //if unchanged, return fail code -1
	}

	/**
	 * 
	 * @return
	 */
	public HTMLTokenSequence removeOverlap () {
		
//		int split = length()/2;
		HTMLTokenSequence result = new HTMLTokenSequence(this);
		HTMLTokenSequence start = subsequence(0, 3);

		int i=0;
		int j = indexOf(start, 2);

		if (j==-1) {
			return result;
		}
		
		int tmp = j;
		
		while (i<j && j<length()) {
			if (!get(i).equals(get(j))) {
				return result; // no overlap, abort...
			}
			i++; j++;
		}
		
		return (HTMLTokenSequence) this.subsequence(0, tmp);
	}

	/**
	 * Count the frequency of subsequence subseq to occur
	 * in this frequency. Based on string value of token
	 * sequences.
	 * 
	 * @param subseq
	 * @return
	 */
	public int countSubSequenceFrequency (TokenSequence<?> subseq) {

		TokenSequence<?> toks = this;
		int result = 0;
		if (this.length()<subseq.length()) {
			return result;
		}
		String current = this.toString();
		int idx = current.toString().indexOf(subseq.toString());
		while (idx>=0) {
			toks = toks.subsequence(idx + subseq.length());
			current = toks.toString();
			idx = current.toString().indexOf(subseq.toString());
			result++;
		}

		return result;
	}
	
	@Override
	public Iterator<HTMLTagToken> iterator() {
		return tokenList.iterator();
	}
	
	/**
	 * 
	 */
	@Override
	public HTMLTokenSequence subsequence(int start) {
		return subsequence(start, length());
	}
	
	/**
	 * 
	 */
	@Override
	public HTMLTokenSequence subsequence(int start, int end) {
		HTMLTokenSequence result = new HTMLTokenSequence();
		for (int i=start; i<end; i++) {
			result.append(get(i));
		}
		return result;
	}

}//HTMLTokenSequence
