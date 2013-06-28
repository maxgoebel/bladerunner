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
package at.tuwien.prip.common.datastructures;

import java.util.Iterator;

import at.tuwien.prip.common.log.ErrorDump;



/**
 * ROIterator.java
 *
 * readonly iterator
 *
 * Created: Tue May 20 12:51:42 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class ROIterator<T>
  implements Iterator<T>
{

  private final Iterator<T> it;

  public ROIterator(Iterator<T> it) {
    this.it = it;
  }

  public T next() {
    return it.next();
  }

  public boolean hasNext() {
    return it.hasNext();
  }

  public void remove() {
    //should not be called
    ErrorDump.errorHere(this);
  }

} // ROIterator
