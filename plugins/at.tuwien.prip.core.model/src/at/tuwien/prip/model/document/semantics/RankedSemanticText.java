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




/**
 * 
 * RWordType.java
 *
 *
 * A WordType implementation with a priority ranking.
 *
 * Created: Jul 9, 2009 2:41:20 AM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class RankedSemanticText implements Comparable<RankedSemanticText> {

	private WordSemantics type;

	public  RankedSemanticText (WordSemantics type) {
		this.type = type;
	}

	public int compareTo(RankedSemanticText o) {
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
		if (this.type.equals(WordSemantics.ALPHA) ||
				this.type.equals(WordSemantics.ALPHANUM) ||
				this.type.equals(WordSemantics.BRACKET) ||
				this.type.equals(WordSemantics.PUNCTUATION) ||
				this.type.equals(WordSemantics.ALPHACAPS) ||
				this.type.equals(WordSemantics.TEXT) ||
				this.type.equals(WordSemantics.WHITESPACE) ) {
			return -1;
		} else if (this.type.equals(WordSemantics.DIGIT) ) {
			return 0;
		} else if (this.type.equals(WordSemantics.DATE) ||
				this.type.equals(WordSemantics.PERSON) ||
				this.type.equals(WordSemantics.LOCATION) ||
				this.type.equals(WordSemantics.ADDRESS) ||
				this.type.equals(WordSemantics.PHONE) ||
				this.type.equals(WordSemantics.PERCENTAGE) ||
				this.type.equals(WordSemantics.PRIZE) ||
				this.type.equals(WordSemantics.TIME) ||
				this.type.equals(WordSemantics.ISBN) ||
				this.type.equals(WordSemantics.URI) ||
				this.type.equals(WordSemantics.EMAIL) ) {
			return 1;
		} else {
			return -1; //DEFAULT
		}

	}

	public WordSemantics getType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}

}
