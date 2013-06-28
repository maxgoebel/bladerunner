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
package at.tuwien.prip.model.agent;

/**
 * AC.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 4, 2011
 */
public class AC 
{
	public class A
	{
		public static final int CHARACTER = 0;
		public static final int WORD = 1;
		public static final int WORD_BLOCK = 2; //few words with semantic annotation
		public static final int TEXTLINE = 3;
		public static final int TEXTBLOCK = 4;
		public static final int IMAGE = 5;
		public static final int COLUMN = 6;
		public static final int REGION = 7;
		public static final int OBJECT = 8;
		public static final int PAGE = 9;
	}

	public class B
	{
		public static final int ALIGNMENT = 20;
		public static final int PROXIMITY = 21;
		public static final int CONTEXT = 22;
		public static final int NEIGHBOR = 23;
	}

	public class C
	{
		public static final int TABLECELL = 40;
		public static final int TABLE = 41;
		public static final int PARAGRAPH = 42;
		public static final int LIST_ITEM = 43;
		public static final int LIST = 44;
		public static final int SECTION = 45;
		public static final int SEMANTIC = 46;
		public static final int TABLECOL = 47;
		public static final int TABLEROW = 48;
		public static final int KEYVAL = 49;
		public static final int TABLEPART = 50;
	}

	//attributes
	public class D
	{
		public static final int SEMANTIC = 80;
		public static final int HEADING = 81;
		public static final int SALIENCY = 82;
		public static final int TOC_LEVEL = 83;
		public static final int TABLESTATS = 84;
	}
	
	/**
	 * Convert to string.
	 * @param level
	 * @return
	 */
	public static String toStringValue (int level) 
	{
		switch (level) {
		case A.CHARACTER:
			return "CHARACTER";
		case A.WORD:
			return "TXT_WORD";
		case A.WORD_BLOCK:
			return "SEM_WORD";
		case A.TEXTLINE:
			return "TEXTLINE";
		case A.TEXTBLOCK:
			return "TEXTBLOCK";
		case A.IMAGE:
			return "IMAGE";
		case A.COLUMN:
			return "COLUMN";
		case A.REGION:
			return "REGION";
		case A.PAGE:
			return "PAGE";
			
		case B.ALIGNMENT:
			return "ALIGNMENT"; 
		case B.PROXIMITY:
			return "PROXIMITY"; 
			
		case C.TABLECELL:
			return "TABLECELL";
		case C.PARAGRAPH:
			return "PARAGRAPH";
		}
		return "";
	}
}
