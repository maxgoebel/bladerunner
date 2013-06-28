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
 * lxEnumValueDef.java
 *
 * value is from a known list (domain)
 *
 * Created: Wed Aug 13 15:53:38 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class lxEnumValueDef
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

	private lxEnumValueDef(String match_mode,
			String att_name,
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

	/**
	 * note that it may happen (html standard violation)
	 * that there will be a value in poss or in negs
	 * which is not listed in values_spectrum
	 */
	public static lxEnumValueDef generate(String att_name,
			Set<String> domain,
			List<String> poss,
			List<String> negs)
	{
		Set<String> domain_new = new HashSet<String>();
		Set<String> pos_groups = new HashSet<String>();
		for (String v : poss) {
			pos_groups.add(v);
			if (!domain.contains(v)) domain_new.add(v);
		}
		Set<String> neg_groups = new HashSet<String>();
		for (String v : negs) {
			neg_groups.add(v);
			if (!domain.contains(v)) domain_new.add(v);

			if (pos_groups.contains(v)) {
				//contradiction in value examples
				return null;
			}
		}

		if (neg_groups.size()>0) {
			if (pos_groups.size()==0) {
				return
				new lxEnumValueDef(att_name,
						POS_ANY_NEG_ENUMERATE,
						null,
						neg_groups);
			}

			//if nrel>=0.7 then the negative examples cover
			//large part of the domain
			float nrel =
				(float) neg_groups.size() /
				(float) (domain.size()+domain_new.size());
			if (nrel>=0.7f) {
				//enumerate the positive examples
				return
				new lxEnumValueDef(att_name,
						POS_ENUMERATE,
						pos_groups,
						null);
			}

			//if prel<=0.4 then there is few positive groups
			//relative to the domain size e.g.
			//1/3, 1/4, 2/5, 2/6, 2/7, 3/8, 3/9, 4/10, ...
			float prel =
				(float) pos_groups.size() /
				(float) (domain.size()+domain_new.size());
			if (pos_groups.size()==1 || //for 2 value domains
					prel<=0.4f) {
				return
				new lxEnumValueDef(att_name,
						POS_ENUMERATE_NEG_ENUMERATE,
						pos_groups,
						neg_groups);
			}
			return
			new lxEnumValueDef(att_name,
					POS_ANY_NEG_ENUMERATE,
					null,
					neg_groups);
		}

		if (pos_groups.size()==0) {
			return
			new lxEnumValueDef(att_name,
					POS_ANY,
					null,
					null);
		}
//		if prel<=0.4 then there is few positive groups
//		relative to the domain size e.g.
//		1/3, 1/4, 2/5, 2/6, 2/7, 3/8, 3/9, 4/10, ...
		float prel =
			(float) pos_groups.size() /
			(float) (domain.size()+domain_new.size());
		if (pos_groups.size()==1 || //for 2 value domains
				prel<=0.4f) {
			return
			new lxEnumValueDef(att_name,
					POS_ENUMERATE,
					pos_groups,
					null);
		}

		return
		new lxEnumValueDef(att_name,
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

//	public boolean test(lxValueDef other) {

//	if (other instanceof lxEnumValueDef==false)
//	return false;



//	return false;
//	}

	/*
  public static void main(String[] args) {
    Set domain = new HashSet();
    domain.add("text"); domain.add("password"); domain.add("checkbox");
    domain.add("button"); domain.add("combo"); domain.add("jumbo");
    domain.add("mambo");

    {
      List pp = new LinkedList();
      pp.add("text");
      List nn = new LinkedList();
      lxEnumValueDef d = generate(domain, pp, nn);
      System.err.println("d1= "+d); //pos enum
    }
    {
      List pp = new LinkedList();
      pp.add("text");
      pp.add("combo");
      List nn = new LinkedList();
      lxEnumValueDef d = generate(domain, pp, nn);
      System.err.println("d2= "+d); //pos enum
    }
    {
      List pp = new LinkedList();
      pp.add("text");
      pp.add("combo");
      pp.add("password");
      List nn = new LinkedList();
      lxEnumValueDef d = generate(domain, pp, nn);
      System.err.println("d3= "+d); //pos any
    }
    {
      List pp = new LinkedList();
      pp.add("text");
      pp.add("combo");
      pp.add("password-new");
      List nn = new LinkedList();
      lxEnumValueDef d = generate(domain, pp, nn);
      System.err.println("d4= "+d); //pos enum
    }
    {
      List pp = new LinkedList();
      List nn = new LinkedList();
      nn.add("text");
      nn.add("combo");
      lxEnumValueDef d = generate(domain, pp, nn);
      System.err.println("d5= "+d); //pos any, neg enum
    }
    {
      List pp = new LinkedList();
      pp.add("mambo");
      List nn = new LinkedList();
      nn.add("text");
      nn.add("password");
      nn.add("checkbox");
      nn.add("button");
      nn.add("combo");
      lxEnumValueDef d = generate(domain, pp, nn);
      System.err.println("d6= "+d); //pos enum
    }
    {
      List pp = new LinkedList();
      pp.add("text");
      pp.add("combo");
      List nn = new LinkedList();
      nn.add("button");
      nn.add("password");
      lxEnumValueDef d = generate(domain, pp, nn);
      System.err.println("d6= "+d); //pos enum, neg enum
    }
    {
      List pp = new LinkedList();
      pp.add("mambo");
      pp.add("text");
      pp.add("password");
      pp.add("checkbox");
      pp.add("button");
      List nn = new LinkedList();
      nn.add("combo");
      lxEnumValueDef d = generate(domain, pp, nn);
      System.err.println("d7= "+d); //pos any, neg enum
    }
    {
      List pp = new LinkedList();
      pp.add("mambo");
      List nn = new LinkedList();
      nn.add("mambo");
      lxEnumValueDef d = generate(domain, pp, nn);
      System.err.println("d8= "+d); //null
    }
  }
	 */

} // lxEnumValueDef
