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
package at.tuwien.prip.model.document.semantics;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * SemanticText.java
 *
 *
 *
 * Created: Jul 8, 2009 3:06:40 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class SemanticText implements Comparable<SemanticText> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7271482718764827274L;
	
	/* The word */
	private String value;
	
	/* The associated semantics */
	private List<WordSemantics> semantics;
	
	/**
	 * Constructor.
	 * 
	 * @param a
	 * @param sem
	 */
	public SemanticText(String a, WordSemantics sem) {
		this.value = a;
		this.semantics = new ArrayList<WordSemantics>();
		this.semantics.add(sem);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param a
	 * @param b
	 */
	public SemanticText(String a, WordTypeRank b) 
	{
		this.value = a;
		this.semantics = new ArrayList<WordSemantics>();
		b.rankDescending();
		semantics.add(b.next().getType());
	}
	
	public boolean containsSemantics (WordSemantics sem) 
	{
		return this.semantics.contains(sem);
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (!(obj instanceof SemanticText)) { 
			return false; 
		}
		
		if (this.semantics!=null && this.semantics.size()>0) 
		{
			if (obj instanceof SemanticText)
			{
				SemanticText other = (SemanticText) obj;
				if (other.semantics!=null && other.semantics.size()>0)
				{
					if (!this.value.equalsIgnoreCase(other.value)) {
						return false;
					}
					for (WordSemantics wti : this.semantics)
					{
						if (!other.semantics.contains(wti))
						{
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int compareTo(SemanticText o) {
		int t1 = this.wg2int();
		int t2 = o.wg2int();
		if ( t1<t2 ) {
			return -1;
		}
		else if (t1>t2) {
			return 1;
		}
		return 0;
	}

	/**
	 * 
	 * There are three priorities:
	 * 
	 * HIGH:
	 * 		date, address, phone, prize, time, isbn, uri, email
	 * MEDIUM:
	 * 		name, percentage, digit
	 * LOW:
	 * 		alpha, alphanum, bracket, punctuation, delim, text, whitespace
	 * 
	 * 
	 * @return
	 */
	private int wg2int () {
		int result = 0;
		
		// the simple cases, add +1
		if (this.semantics.contains(WordSemantics.ALPHA) )
			result++;
		if (this.semantics.contains(WordSemantics.ALPHANUM) )
			result++;
		if (	this.semantics.contains(WordSemantics.BRACKET) )
			result++;
		if (	this.semantics.contains(WordSemantics.PUNCTUATION) )
			result++;
		if (	this.semantics.contains(WordSemantics.ALPHACAPS) )
			result++;
		if (	this.semantics.contains(WordSemantics.TEXT) )
			result++;
		if (	this.semantics.contains(WordSemantics.WHITESPACE) )
			result++;
			
		// the more abstract case, add +3
		if (this.semantics.contains(WordSemantics.DIGIT) ) 
			result+=3;
		if (this.semantics.contains(WordSemantics.DATE) ) 
			result+=3;
		if (		this.semantics.contains(WordSemantics.PERSON) ) 
			result+=3;
		if (this.semantics.contains(WordSemantics.LOCATION) ) 
			result+=3;
		if (this.semantics.contains(WordSemantics.ADDRESS) ) 
			result+=3;
		if (this.semantics.contains(WordSemantics.PHONE)  ) 
			result+=3;
		if (this.semantics.contains(WordSemantics.PERCENTAGE) ) 
			result+=3;
		if (this.semantics.contains(WordSemantics.PRIZE) ) 
			result+=3;
		if (this.semantics.contains(WordSemantics.TIME) ) 
			result+=3;
		if (this.semantics.contains(WordSemantics.ISBN) ) 
			result+=3;
		if (this.semantics.contains(WordSemantics.URI) ) 
			result+=3;
		if (this.semantics.contains(WordSemantics.EMAIL) ) 
			result+=3;

		return result;

	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (WordSemantics sem : semantics) {
			sb.append(sem.toString() + "|");
		}
		String semantics = "";
		if (sb.length()>0) {
			semantics = sb.substring(0, sb.length()-1);
		}
		return "["+value.toString()+"::"+semantics+"]";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<WordSemantics> getSemantics() {
		return semantics;
	}

	public void setSemantics(List<WordSemantics> semantics) {
		this.semantics = semantics;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
