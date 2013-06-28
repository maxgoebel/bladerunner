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
 * ROSet.java
 *
 * readonly set
 *
 * @author Michal Ceresna
 * @version 1.0
 */
import java.util.*;


public class ROCollection<T>
    implements Collection<T>
{

    private final Collection<T> l;

    public ROCollection(Collection<T> l) {
      this.l = l;
    }

    public boolean add(T o) {
        throw new RuntimeException("read only collection");
    }

    public boolean addAll(Collection<? extends T> c) {
        throw new RuntimeException("read only collection");
    }

    public void clear() {
        throw new RuntimeException("read only collection");
    }

    public boolean remove(Object o) {
        throw new RuntimeException("read only collection");
    }

    public boolean removeAll(Collection<?> c) {
        throw new RuntimeException("read only collection");
    }

    public boolean retainAll(Collection<?> c) {
        throw new RuntimeException("read only collection");
    }

    public ROCollection(Set<T> l) {
        this.l = l;
    }

    public boolean contains(Object o) {
        return l.contains(o);
    }

    public boolean containsAll(Collection< ? > c) {
        return l.contains(c);
    }

    public boolean equals(Object o) {
        return l.equals(o);
    }

    public int hashCode() {
        return l.hashCode();
    }

    public boolean isEmpty() {
        return l.isEmpty();
    }

    public Iterator<T> iterator() {
        return new ROIterator<T>(l.iterator());
    }

    public int size() {
        return l.size();
    }

    public Object[] toArray() {
        return l.toArray();
    }

    public <W> W[] toArray(W[] a) {
        return l.toArray(a);
    }

    public LinkedHashSet<T> toLinkedHashSet() {
        return new LinkedHashSet<T>(l);
    }

    public LinkedList<T> toLinkedList() {
        return new LinkedList<T>(l);
    }

}
