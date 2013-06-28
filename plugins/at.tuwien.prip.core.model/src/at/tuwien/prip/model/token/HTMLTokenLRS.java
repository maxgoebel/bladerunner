/**
 * HTMLTokenLRS.java
 *
 *
 *
 * Created: Apr 26, 2009 11:59:48 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
package at.tuwien.prip.model.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import at.tuwien.prip.common.datastructures.StatMap;


/**
 * HTMLTokenLRS.java
 *
 *
 * Longest Repetitive Substring matching for
 * HTMLToken case.
 * 
 * This is adapted from the web resource:
 * http://www.cs.princeton.edu/introcs/42sort/
 *  
 * Credits: Sedgewick R., Wayne K.
 *
 * Created: Apr 26, 2009 11:59:48 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class HTMLTokenLRS {

	/**
	 * 
	 * Return the longest common prefix of s and t.
	 * 
	 * @param s
	 * @param t
	 */
	public static HTMLTokenSequence lcp(HTMLTokenSequence s, HTMLTokenSequence t) {
		HTMLTokenSequence result = new HTMLTokenSequence();

		int n = Math.min(30, Math.min(s.length(), t.length()));
		for (int i = 0; i < n; i++) {
			if (!s.get(i).equals(t.get(i))) {
				break;
			}
			result.add(s.get(i));
		}

//		// check if merely a multiple of existent subsequence?
//		List<HTMLTokenSequence> accepted = new LinkedList<HTMLTokenSequence>(statMap.getKeys());
//		Collections.sort(accepted, new Comparator<HTMLTokenSequence>() {
//
//			public int compare(HTMLTokenSequence o1, HTMLTokenSequence o2) {
//				if (o1.length()>o2.length()) {
//					return 1;
//				} else if (o1.length()==o2.length()) {
//					return 0;
//				}
//				return -1;
//			}
//			
//		});
//		for (HTMLTokenSequence key : accepted) {
//			int counter = result.countSubSequenceFrequency(key);
//			if (counter>1 && (result.length() * 0.75) <= (counter * key.length())) {
//				return key; //don't accept previous solution...
//			}
//		}

		return result;
	}

	static List<HTMLTokenSequence> accepted;

	private static StatMap<HTMLTokenSequence> statMap;
	
	/**
	 * 
	 * Return the longest repeated subsequence in s.
	 * 
	 * @param s
	 */
	public static HTMLTokenSequence lrs(HTMLTokenSequence s) {

		accepted = new ArrayList<HTMLTokenSequence>();
		statMap = new StatMap<HTMLTokenSequence>();

		// form the N suffixes
		int N = s.length();
		HTMLTokenSequence[] suffixes = new HTMLTokenSequence[N];
		for (int i = 0; i<N; i++) {
			HTMLTokenSequence tmpList = new HTMLTokenSequence();
			for (int j=i; j<N; j++) {
				tmpList.add(s.get(j));
			}
			suffixes[i] = tmpList;	//=substring(i, N);
		}

		Arrays.sort(suffixes); //sort suffixes

//		for (HTMLTokenSequence ts : suffixes) {
//		System.out.println(ts.toString());
//		}

		// find longest repeated substring by comparing adjacent sorted suffixes
		HTMLTokenSequence lrs = new HTMLTokenSequence();
		for (int i = 0; i < N - 1; i++) {
			HTMLTokenSequence x = lcp(suffixes[i], suffixes[i+1]);
			if (x.length() > 4) {
				if (x.length() > lrs.length()) {
					lrs = x;
				}
				statMap.increment(x);
			}
		}
		
		// sort all suffix sequences by frequency
		List<HTMLTokenSequence> tokList = new ArrayList<HTMLTokenSequence>(statMap.getKeys());
		Collections.sort(tokList, new Comparator<HTMLTokenSequence>() {

			public int compare(HTMLTokenSequence o1, HTMLTokenSequence o2) {
				if (statMap.getCount(o1)>statMap.getCount(o2)) {
					return -1;
				} else if (statMap.getCount(o1)==statMap.getCount(o2)) {
					return 0;
				}
				return 1;
			}
			
		});
		
		// only allow sequences as solutions where the 1st symbol
		// of the sequence has the common lowest depth (e.g. root)
		// of all symbols used in the sequence
		Iterator<HTMLTokenSequence> it = tokList.iterator();
		while (it.hasNext()) {
			HTMLTokenSequence seq = it.next();
			
			if (!seq.isValidHTMLSequence()) {
				it.remove();
			} 
//			else {
//				System.out.println(seq +" "+ statMap.getCount(seq));
//			}
		}
		
		//select solution:
		HTMLTokenSequence solution = tokList.get(0);
		solution = solution.removeOverlap();
		return solution;
//		return lrs;
	}

}//HTMLTokenLRS
