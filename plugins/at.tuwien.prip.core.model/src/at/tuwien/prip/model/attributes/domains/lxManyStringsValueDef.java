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
package at.tuwien.prip.model.attributes.domains;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.tuwien.prip.common.log.ErrorDump;

/**
 * lxManyStringsValueDef.java
 *
 * value is from a known list (domain)
 *
 * Created: Wed Aug 13 15:53:38 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class lxManyStringsValueDef
implements lxValueDef
{
	private static final String POS_ENUMERATE = "pos_enumerate";
	private static final String POS_ANY = "pos_any";
	private static final String POS_ENUMERATE_NEG_ENUMERATE = "pos_enumerate_neg_enumerate";
	private static final String POS_ANY_NEG_ENUMERATE = "pos_any_neg_enumerate";

	private final String match_mode;
	private final String att_name;
	/**
	 * enumeration of allowed positive values
	 * (if positive-definition part is a finite
	 * set, null otherwise)
	 */
	private final Set<String> pos_enum;
	/**
	 * enumeration of disallowed negative values
	 * (if negative-definition part is a finite
	 * set, null otherwise)
	 */
	private final Set<String> neg_enum;

	private lxManyStringsValueDef(String att_name,
			String match_mode,
			Set<String> pos_enum,
			Set<String> neg_enum)
	{
		this.match_mode = match_mode;
		this.att_name = att_name;
		this.pos_enum = pos_enum;
		this.neg_enum = neg_enum;
	}

	public boolean matches(String v) {
		if (match_mode.equals(POS_ENUMERATE)) {
			return pos_enum.contains(v);
		}
		else if (match_mode.equals(POS_ANY)) {
			return true;
		}
		else if (match_mode.equals(POS_ENUMERATE_NEG_ENUMERATE)) {
			return
			pos_enum.contains(v) &&
			!neg_enum.contains(v);
		}
		else if (match_mode.equals(POS_ANY_NEG_ENUMERATE)) {
			return !neg_enum.contains(v);
		}
		else {
			//should not happen
			ErrorDump.errorHere(this);
			return false;
		}
	}

	public static lxManyStringsValueDef generate(String att_name,
			List<String> poss,
			List<String> negs)
	{
		int pos_total = poss.size();
		Set<String> pos_groups = new HashSet<String>();
		for (String v : poss) {
			pos_groups.add(v);
		}
		Set<String> neg_groups = new HashSet<String>();
		for (String v : negs) {
			neg_groups.add(v);

			if (pos_groups.contains(v)) {
				//contradiction in value examples
				return null;
			}
		}

		if (neg_groups.size()>0) {
			if (pos_groups.size()==0) {
				return
				new lxManyStringsValueDef(att_name,
						POS_ANY_NEG_ENUMERATE,
						null,
						neg_groups);
			}

			if (neg_groups.size()>5) {
				//too many negative groups
				return null;
			}

			//if prel<=0.34 then there is much more positive
			//examples than positive groups e.g. 1/3, 1/4, 1/5, 2/6
			float prel = (float) pos_groups.size() / (float) pos_total;
			if (prel<=0.34) {
				return
				new lxManyStringsValueDef(att_name,
						POS_ENUMERATE_NEG_ENUMERATE,
						pos_groups,
						neg_groups);
			}

			return
			new lxManyStringsValueDef(att_name,
					POS_ANY_NEG_ENUMERATE,
					null,
					neg_groups);
		}


		if (pos_total==0) {
			return
			new lxManyStringsValueDef(att_name,
					POS_ANY,
					null,
					null);
		}

		//well, we should have enough
		//example data to guess

		//if prel<=0.34 then there is much more positive
		//examples than positive groups e.g. 1/3, 1/4, 1/5, 2/6
		//float prel = (float) pos_groups.size() / (float) pos_total;

		float prel2 = 0.1f;
		if (prel2<=0.34f) {
			return
			new lxManyStringsValueDef(att_name,
					POS_ENUMERATE,
					pos_groups,
					null);
		}

		return
		new lxManyStringsValueDef(att_name,
				POS_ANY,
				null,
				null);
	}



	public String toString() {
		StringBuffer sb = new StringBuffer();

		if (match_mode.equals(POS_ANY) ||
				match_mode.equals(POS_ANY_NEG_ENUMERATE)) {
			sb.append("*");
		}
		else {
			sb.append("+{");
			boolean first = true;
			for (String v : pos_enum) {
				if (!first) sb.append(","); else first = false;
				sb.append(v);
			}
			sb.append("}");
		}

		if (match_mode.equals(POS_ENUMERATE_NEG_ENUMERATE) ||
				match_mode.equals(POS_ANY_NEG_ENUMERATE)) {
			sb.append("-{");
			boolean first = true;
			for (String v : neg_enum) {
				if (!first) sb.append(","); else first = false;
				sb.append(v);
			}
			sb.append("}");
		}

		return sb.toString();
	}

	public String getAttName() {
		return att_name;
	}

	/*
  public static void main(String[] args) {
    {
      List pp = new LinkedList();
      pp.add("text1");
      List nn = new LinkedList();
      lxManyStringsValueDef d = generate(pp, nn);
      System.err.println("d1= "+d); //pos any
    }
    {
      List pp = new LinkedList();
      pp.add("text1");
      pp.add("text1");
      pp.add("text1");
      List nn = new LinkedList();
      lxManyStringsValueDef d = generate(pp, nn);
      System.err.println("d2= "+d); //pos enum
    }
    {
      List pp = new LinkedList();
      pp.add("text1");
      pp.add("text1");
      pp.add("text1");
      pp.add("text2");
      List nn = new LinkedList();
      lxManyStringsValueDef d = generate(pp, nn);
      System.err.println("d3= "+d); //pos any
    }
    {
      List pp = new LinkedList();
      List nn = new LinkedList();
      pp.add("text1");
      lxManyStringsValueDef d = generate(pp, nn);
      System.err.println("d4= "+d); //pos any
    }
    {
      List pp = new LinkedList();
      pp.add("text1");
      List nn = new LinkedList();
      nn.add("text2");
      lxManyStringsValueDef d = generate(pp, nn);
      System.err.println("d5= "+d); //pos any, neg enum
    }
    {
      List pp = new LinkedList();
      pp.add("text1");
      pp.add("text1");
      pp.add("text1");
      List nn = new LinkedList();
      nn.add("text2");
      nn.add("text3");
      nn.add("text4");
      lxManyStringsValueDef d = generate(pp, nn);
      System.err.println("d6= "+d); //pos enum, neg enum
    }
    {
      List pp = new LinkedList();
      pp.add("text1");
      pp.add("text1");
      pp.add("text1");
      pp.add("text2");
      List nn = new LinkedList();
      nn.add("text5");
      lxManyStringsValueDef d = generate(pp, nn);
      System.err.println("d7= "+d); //pos any, neg enum
    }
    {
      List pp = new LinkedList();
      pp.add("mambo");
      List nn = new LinkedList();
      nn.add("mambo");
      lxManyStringsValueDef d = generate(pp, nn);
      System.err.println("d8= "+d); //null
    }
  }
	 */

} // lxManyStringsValueDef
