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

/**
 * ROList.java
 *
 * readonly list
 *
 * Created: Thu Mar 20 11:07:49 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
import java.util.*;


public class ROList<T>
    implements Iterable<T>
{

  private final List<T> l;

  public ROList(List<T> l) {
      if (l==null)
          this.l = new LinkedList<T>();
      else
          this.l = l;
  }

  public ROList() {
    this.l = new LinkedList<T>();
  }

  public boolean contains(T o) {
    return l.contains(o);
  }

  public boolean containsAll(Collection< ? > c) {
    return l.contains(c);
  }

  public boolean equals(Object o) {
    return l.equals(o);
  }

  public T get(int index) {
    return l.get(index);
  }

  public int hashCode() {
    return l.hashCode();
  }

  public int indexOf(T o) {
    return l.indexOf(o);
  }

  public boolean isEmpty() {
    return l.isEmpty();
  }

  public Iterator<T> iterator() {
    return new ROIterator<T>(l.iterator());
  }

  public int lastIndexOf(T o) {
    return l.lastIndexOf(o);
  }

  public int size() {
    return l.size();
  }

  public List<T> subList(int fromIndex,
                         int toIndex)
  {
    return l.subList(fromIndex, toIndex);
  }

  public Object[] toArray() {
    return l.toArray();
  }

  public T[] toArray(T[] a) {
    return l.toArray(a);
  }

  public LinkedList<T> toLinkedList() {
    return new LinkedList<T>(l);
  }

} // ROList
