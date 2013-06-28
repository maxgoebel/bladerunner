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


import at.tuwien.prip.common.datastructures.Rank;


/**
 * 
 * WordTypeRank.java
 *
 * An ordered list of word semantics.
 *
 * Created: Jul 9, 2009 8:02:01 AM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class WordTypeRank extends Rank<RankedSemanticText> {

	public WordTypeRank(WordSemantics type) {
		super (new RankedSemanticText(type));
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("<");
		for (RankedSemanticText e : elements) {
			sb.append(e.toString()+", ");
		}
		sb.replace(sb.length()-2, sb.length(), ">");
		return sb.toString();
	}
	
//	public WordTypeRank(WordSemantics type) {
//		super (new RWordType(type));
//	}
//
//	@Override
//	public String toString() {
//		StringBuffer sb = new StringBuffer("<");
//		for (RWordType e : elements) {
//			sb.append(e.toString()+", ");
//		}
//		sb.replace(sb.length()-2, sb.length(), ">");
//		return sb.toString();
//	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WordTypeRank) {
			WordTypeRank other = (WordTypeRank) obj;
			return other.toString().equals(toString());
		}
		return false;
	}
}
