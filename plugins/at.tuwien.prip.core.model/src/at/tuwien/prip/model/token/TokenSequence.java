/**
 * TokenSequence.java
 *
 *
 *
 * Created: Apr 27, 2009 1:29:58 AM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
package at.tuwien.prip.model.token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * TokenSequence.java
 *
 *
 * A basic sequence of tokens.
 *
 * Created: Apr 27, 2009 1:29:58 AM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class TokenSequence <T extends Token>
implements Comparable<TokenSequence<T>>, Iterable<T> 
{

	protected List<T> tokenList = new ArrayList<T>();

	public void add (T token) {
		this.tokenList.add(token);
	}

	public T get (int i) {
		if (length()>i) {
			return this.tokenList.get(i);
		}
		return null;
	}
	public int length() {
		return tokenList.size();
	}

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring. The integer returned is the smallest value
	 * <i>k</i> such that:
	 * <blockquote><pre>
	 * this.startsWith(str, <i>k</i>)
	 * </pre></blockquote>
	 * is <code>true</code>.
	 *
	 * @param   str   any string.
	 * @return  if the string argument occurs as a substring within this
	 *          object, then the index of the first character of the first
	 *          such substring is returned; if it does not occur as a
	 *          substring, <code>-1</code> is returned.
	 */
	public int indexOf(TokenSequence<T> tok) {
		return indexOf(tok, 0);
	}

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring, starting at the specified index.  The integer
	 * returned is the smallest value <tt>k</tt> for which:
	 * <blockquote><pre>
	 *     k &gt;= Math.min(fromIndex, this.length()) && this.startsWith(str, k)
	 * </pre></blockquote>
	 * If no such value of <i>k</i> exists, then -1 is returned.
	 *
	 * @param   str         the substring for which to search.
	 * @param   fromIndex   the index from which to start the search.
	 * @return  the index within this string of the first occurrence of the
	 *          specified substring, starting at the specified index.
	 */
	public int indexOf(TokenSequence<T> tok, int fromIndex) {
		return indexOf(
				toArray(), //	ListUtils.toArray(tokenList, getClass()),
				0, //offset
				length(),
				tok.toArray(),
				0, //tok.offset 
				tok.length(), 
				fromIndex);
	}

	/**
	 * 
	 * Construct a subsequence of a token sequence starting
	 * with the start'th element.
	 * 
	 * @param start
	 * @return
	 */
	public TokenSequence<T> subsequence (int start) {
		return subsequence(start, length());
	}
	
	/**
	 * 
	 * Construct a subsequence of a token sequence starting
	 * with the start'th element and ending with the end.
	 * 
	 * @param start
	 * @return
	 */
	public TokenSequence<T> subsequence (int start, int end) {
		TokenSequence<T> result = new TokenSequence<T>();
		for (int i=start; i<end; i++) {
			result.append(get(i));
		}
		return result;
	}
	
	/**
	 * 
	 * Code shared by String and StringBuffer to do searches. The
	 * source is the character array being searched, and the target
	 * is the string being searched for.
	 *
	 * @param   source       the characters being searched.
	 * @param   sourceOffset offset of the source string.
	 * @param   sourceCount  count of the source string.
	 * @param   target       the characters being searched for.
	 * @param   targetOffset offset of the target string.
	 * @param   targetCount  count of the target string.
	 * @param   fromIndex    the index to begin searching from.
	 */
	int indexOf(T[] source, int sourceOffset, int sourceCount,
			T[] target, int targetOffset, int targetCount,
			int fromIndex) {
		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		Token first  = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			/* Look for first character. */
			if (source[i] != first) {
				while (++i <= max && !source[i].equals(first));
			}

			/* Found first character, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && source[j].equals(target[k]); j++, k++);

				if (j == end) {
					/* Found whole string. */
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

	/**
	 * 
	 * Compare to token sequences token-wise.
	 * 
	 * @param o
	 */
	@Override
	public int compareTo(TokenSequence<T> o) {

		for (int i=0; i<this.tokenList.size(); i++) {
			if (o.tokenList.size()<=i) {
				return 1;
			}
			Token a = this.get(i);
			Token b = o.get(i);

			int r = a.compareTo(b);
			if (r==0) {
				continue;
			}
			return r;
		}
		return 0; //equal
	}

	public int indexOf (Token tok) {
		return tokenList.indexOf(tok);
	}

	public int lastIndexOf (Token tok) {
		return tokenList.lastIndexOf(tok);
	}

	public TokenSequence<T> subSequence (int start, int end) {
		TokenSequence<T> result = new TokenSequence<T>();
		for (int i=start; i<end; i++) {
			result.add(this.get(i));
		}
		return result;
	}

	public void prepend (T token) {
		tokenList.add(0, token);
	}

	public void append (T token) {
		tokenList.add(token);
	}
	
	public void append (TokenSequence<T> sequence) {
		Iterator<T> it = sequence.iterator();
		while (it.hasNext()) {
			this.add(it.next());
		}
	}

	public void removeFront () {
		tokenList.remove(0);
	}

	public void removeEnd () {
		tokenList.remove(tokenList.size());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(" ");
		for (Token token : this.tokenList) {
			sb.append(token.getName() + "-");
		}
		return sb.substring(0, sb.length()-1).toString().trim();
	}

	public Iterator<T> iterator() {
		return tokenList.iterator();
	}

	/**
	 * 
	 * Hash code calculated only based on string content of tokens.
	 * 
	 */
	@Override
	public int hashCode() {
		int hashcode = 0;
		for (int i=0; i<tokenList.size(); i++) {
			hashcode+=tokenList.get(i).hashCode();
		}
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		return (this.hashCode()==obj.hashCode());
	}
	
	@SuppressWarnings("unchecked")
	public T[] toArray() {
		Token[] result = new Token[length()];
		for (int i=0; i<length(); i++) {
			result[i] = get(i);
		}
		return (T[]) result;
	}

	public List<T> getTokenList() {
		return tokenList;
	}

//	@Override
//	protected Object clone() throws CloneNotSupportedException {
//		HTMLTokenSequence clone = new HTMLTokenSequence();
//
//		Iterator<Token> it = iterator();
//		while (it.hasNext()) {
//			clone.add(it.next());
//		}
//		return clone;
//	}

}//TokenSequence
