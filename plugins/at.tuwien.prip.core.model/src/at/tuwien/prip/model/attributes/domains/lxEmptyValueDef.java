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

import java.util.List;

import at.tuwien.prip.common.log.ErrorDump;

/**
 * lxEmptyValueDef.java
 *
 * value is empty string
 *
 * Created: Wed Aug 13 17:40:21 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class lxEmptyValueDef
  implements lxValueDef
{

  private static final String PRESENT = "present";
  private static final String NOT_PRESENT = "not_present";

  private final String match_mode;
  private final String attName;

  private lxEmptyValueDef(String match_mode, String attName) {
    this.match_mode = match_mode;
    this.attName = attName;
  }

  public boolean matches(String v) {
    if (match_mode.equals(PRESENT)) {
      return true;
    }
    else if (match_mode.equals(NOT_PRESENT)) {
      return false;
    }
    else {
      //should not happen
      ErrorDump.errorHere(this);
      return false;
    }
  }

  public static lxEmptyValueDef generate(String att_name,
                                         List<String> poss,
                                         List<String> negs)
  {
    if (!poss.isEmpty() && negs.isEmpty()) {
      return
        new lxEmptyValueDef(PRESENT, att_name);
    }
    else if (poss.isEmpty() && !negs.isEmpty()) {
      return
        new lxEmptyValueDef(NOT_PRESENT, att_name);
    }
    else {
      return null;
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();

    if (match_mode.equals(PRESENT)) {
      sb.append("*");
    }
    else if (match_mode.equals(NOT_PRESENT)) {
      sb.append("{}");
    }
    else {
      //should not happen
        ErrorDump.errorHere(this);
    }

    return sb.toString();
  }

public String getAttName() {
    return attName;
}

  /*
  public static void main(String[] args) {
    {
      List pp = new LinkedList();
      pp.add("text1");
      pp.add("text2");
      List nn = new LinkedList();
      lxEmptyValueDef d = generate(pp, nn);
      System.err.println("d1= "+d); //present
    }
    {
      List pp = new LinkedList();
      List nn = new LinkedList();
      nn.add("text1");
      nn.add("text2");
      lxEmptyValueDef d = generate(pp, nn);
      System.err.println("d1= "+d); //not present
    }
    {
      List pp = new LinkedList();
      pp.add("text1");
      List nn = new LinkedList();
      nn.add("text2");
      lxEmptyValueDef d = generate(pp, nn);
      System.err.println("d1= "+d); //null
    }
  }
  */

} // lxEmptyValueDef
