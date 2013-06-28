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
package at.tuwien.prip.model.attributes.html;

import java.util.*;

/**
 * HTMLNegTagDefSet.java
 *
 * immutable (constant) set of tags
 * expressed via negation and closed
 * word assumption
 * (all not enumerated tags belong to this set)
 *
 * Created: Sun Jul 27 17:56:05 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class HTMLNegTagDefSet
  implements HTMLTagDefSet
{

  private final LinkedHashSet<HTMLTagDef> neg_tags;

  public HTMLNegTagDefSet(HTMLTagDef[] neg_tags) {
    this.neg_tags = new LinkedHashSet<HTMLTagDef>();
    for (int i=0; i<neg_tags.length; i++) {
      this.neg_tags.add(neg_tags[i]);
    }
  }

  public boolean contains(String tag_name) {
    for (HTMLTagDef t : neg_tags) {
      if (t.getName().equals(tag_name)) {
        return false;
      }
    }
    return true;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("![");
    Iterator<HTMLTagDef> it = neg_tags.iterator();
    while (it.hasNext()) {
      HTMLTagDef t = it.next();
      sb.append(t.toString());
      if (it.hasNext()) {
        sb.append(",");
      }
    }
    sb.append("]");
    return sb.toString();
  }

} // HTMLNegTagDefSet
