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
 * HTMLEnumTagDefSet.java
 *
 * immutable (constant) set of tags
 * specified by enumeration
 *
 * Created: Sun Jul 27 17:56:05 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class HTMLEnumTagDefSet
  implements HTMLTagDefSet
{

  private final LinkedHashSet<HTMLTagDef> tags;

  public HTMLEnumTagDefSet(HTMLTagDef[] tags) {
    this.tags = new LinkedHashSet<HTMLTagDef>();
    for (int i=0; i<tags.length; i++) {
      this.tags.add(tags[i]);
    }
  }

  public boolean contains(String tag_name) {
    for (HTMLTagDef t : tags) {
      if (t.getName().equals(tag_name)) {
        return true;
      }
    }
    return false;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    Iterator<HTMLTagDef> it = tags.iterator();
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

} // HTMLEnumTagDefSet
