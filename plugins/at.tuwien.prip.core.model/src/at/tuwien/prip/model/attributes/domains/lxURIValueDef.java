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
 * lxURIValueDef.java
 *
 * value is uri
 *
 * Created: Wed Aug 13 13:59:28 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class lxURIValueDef
implements lxValueDef
{

	private static final String EXACT = "exact";
	private static final String EMAIL = "email";
	private static final String LINK = "link";
	private static final String ANY = "any";

	private final String match_mode;
	private final String att_name;

	/**
	 * enumeration of allowed positive values
	 * (if positive-definition part is exact
	 * null otherwise)
	 */
	private final Set<String> pos_enum;

	private lxURIValueDef(String att_name,
			String match_mode,
			Set<String> pos_enum)
	{
		this.match_mode = match_mode;
		this.att_name = att_name;
		this.pos_enum = pos_enum;
	}

	public boolean matches(String v) {
		if (match_mode.equals(ANY)) {
			return true;
		}
		else if (match_mode.equals(EXACT)) {
			return pos_enum.contains(v);
		}
		else if (match_mode.equals(EMAIL)) {
			return isEmail(v);
		}
		else if (match_mode.equals(LINK)) {
			return isLink(v);
		}
		else {
			//should not happen
			ErrorDump.errorHere(this);
			return false;
		}
	}

	public static lxURIValueDef generate(String att_name,
			List<String> poss,
			List<String> negs)
	{

		boolean pos_all_email = !poss.isEmpty();
		boolean pos_all_link = !poss.isEmpty();
		boolean pos_some_email = false;
		boolean pos_some_link = false;
		Set<String> pos_groups = new HashSet<String>();
		for (String v : poss) {
			pos_groups.add(v);
			if (!isEmail(v)) { pos_all_email = false; }
			else { pos_some_email = true; }

			if (!isLink(v)) { pos_all_link = false; }
			else { pos_some_link = true; }
		}
		boolean neg_all_email = !negs.isEmpty();
		boolean neg_all_link = !negs.isEmpty();
		boolean neg_some_email = false;
		boolean neg_some_link = false;
		for (String v : negs) {
			if (!isEmail(v)) { neg_all_email = false; }
			else { neg_some_email = true; }

			if (!isLink(v)) { neg_all_link = false; }
			else { neg_some_link = true; }

			if (pos_groups.contains(v)) {
				//contradiction in value examples
				return null;
			}
		}

		if (poss.size()>=3 && pos_groups.size()==1) {
			return new lxURIValueDef(att_name, EXACT, pos_groups);
		}
		else if (pos_all_email && !neg_some_email) {
			return new lxURIValueDef(att_name, EMAIL, null);
		}
		else if (pos_all_link && !neg_some_link) {
			return new lxURIValueDef(att_name, LINK, null);
		}
		else if (!pos_some_email && neg_all_email) {
			return new lxURIValueDef(att_name, LINK, null);
		}
		else if (!pos_some_link && neg_all_link) {
			return new lxURIValueDef(att_name, EMAIL, null);
		}
		else {
			if (negs.isEmpty()) {
				return new lxURIValueDef(att_name, ANY, null);
			}
			return null;
		}
	}

	private static boolean isEmail(String v) {
		return v.indexOf('@')>=0;
	}

	private static boolean isLink(String v) {
		return v.indexOf('@')<0;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();

		if (match_mode.equals(ANY)) {
			sb.append("*");
		}
		else if (match_mode.equals(EXACT)) {
			sb.append("{");
			sb.append(pos_enum.iterator().next());
			sb.append("}");
		}
		else if (match_mode.equals(EMAIL)) {
			sb.append("EMAIL");
		}
		else if (match_mode.equals(LINK)) {
			sb.append("LINK");
		}
		else {
			//should not happen
			ErrorDump.errorHere(this);
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
      pp.add("http://www.google.com");
      pp.add("http://www.google.com");
      pp.add("http://www.google.com");
      pp.add("ceresna@dbai");
      List nn = new LinkedList();
      lxURIValueDef d = generate(pp, nn);
      System.err.println("d1= "+d); //any
    }
    {
      List pp = new LinkedList();
      pp.add("http://www.google.com");
      pp.add("http://www.google.com");
      pp.add("http://www.google.com");
      pp.add("http://www.dbai.tuwien.ac.at");
      pp.add("http://www.google.com");
      List nn = new LinkedList();
      lxURIValueDef d = generate(pp, nn);
      System.err.println("d2= "+d); //link
    }
    {
      List pp = new LinkedList();
      pp.add("ceresna@dbai");
      pp.add("baumgart@dbai");
      pp.add("ceresna@dbai");
      List nn = new LinkedList();
      lxURIValueDef d = generate(pp, nn);
      System.err.println("d3= "+d); //email
    }
    {
      List pp = new LinkedList();
      List nn = new LinkedList();
      nn.add("ceresna@dbai");
      nn.add("ceresna@dbai");
      nn.add("ceresna@dbai");
      lxURIValueDef d = generate(pp, nn);
      System.err.println("d4= "+d); //link
    }
    {
      List pp = new LinkedList();
      pp.add("http://www.google.com");
      List nn = new LinkedList();
      nn.add("ceresna@dbai");
      nn.add("ceresna@dbai");
      nn.add("ceresna@dbai");
      lxURIValueDef d = generate(pp, nn);
      System.err.println("d5= "+d); //link
    }
    {
      List pp = new LinkedList();
      List nn = new LinkedList();
      nn.add("http://www.google.com");
      nn.add("http://www.google.com");
      nn.add("http://www.google.com");
      lxURIValueDef d = generate(pp, nn);
      System.err.println("d6= "+d); //email
    }
    {
      List pp = new LinkedList();
      pp.add("ceresna@dbai");
      List nn = new LinkedList();
      nn.add("http://www.google.com");
      nn.add("http://www.google.com");
      nn.add("http://www.google.com");
      lxURIValueDef d = generate(pp, nn);
      System.err.println("d6= "+d); //email
    }
    {
      List pp = new LinkedList();
      pp.add("http://www.google.com");
      pp.add("http://www.google.com");
      pp.add("http://www.google.com");
      List nn = new LinkedList();
      lxURIValueDef d = generate(pp, nn);
      System.err.println("d6= "+d); //exact
    }
    {
      List pp = new LinkedList();
      pp.add("http://www.google.com");
      pp.add("ceresna@dbai");
      List nn = new LinkedList();
      nn.add("baumgart@dbai");
      lxURIValueDef d = generate(pp, nn);
      System.err.println("d7= "+d); //null
    }
    {
      List pp = new LinkedList();
      pp.add("ceresna@dbai");
      List nn = new LinkedList();
      nn.add("ceresna@dbai");
      lxURIValueDef d = generate(pp, nn);
      System.err.println("d8= "+d); //null
    }
  }
	 */

} // lxURIValueDef
