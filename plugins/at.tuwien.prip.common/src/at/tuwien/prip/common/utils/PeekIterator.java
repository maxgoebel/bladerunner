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
package at.tuwien.prip.common.utils;

import java.util.*;
import java.io.*;

/** This class is part of the
<A HREF=http://www.mpi-inf.mpg.de/~suchanek/downloads/javatools target=_blank>
          Java Tools
</A> by <A HREF=http://www.mpi-inf.mpg.de/~suchanek target=_blank>
          Fabian M. Suchanek</A>
  You may use this class if (1) it is not for commercial purposes,
  (2) you give credit to the author and (3) you use the class at your own risk.
  If you use the class for scientific purposes, please cite our paper
  "Combining Linguistic and Statistical Analysis to Extract Relations from Web Documents"
  (<A HREF=http://www.mpi-inf.mpg.de/~suchanek/publications/kdd2006.pdf target=_blank>pdf</A>,
  <A HREF=http://www.mpi-inf.mpg.de/~suchanek/publications/kdd2006.bib target=_blank>bib</A>,
  <A HREF=http://www.mpi-inf.mpg.de/~suchanek/publications/kdd2006.ppt target=_blank>ppt</A>
  ). If you would like to use the class for commercial purposes, please contact
  <A HREF=http://www.mpi-inf.de/~suchanek>Fabian M. Suchanek</A><P>

This class provides an Iterator that can look ahead. With the method peek(), you
can retrieve the next element without advancing the iterator.<BR>
Example:
<PRE>
    PeekIterator i=new SimplePeekIterator(1,2,3,4);
    i.peek();
    ---> 1
    i.peek();
    ---> 1
    i.next();
    ---> 1
    i.peek();
    ---> 2

</PRE>
The class is also suited to create an Interator by overriding. The only method that
needs top be overwritten is "internalNext()".<BR>
Example:
<PRE>
    // An iterator over the numbers 0,1,2
    PeekIterator it=new PeekIterator() {
      int counter=0;
      // Returns null if there are no more elements
      protected Integer internalNext() throws Exception {
        if(counter==3) return(null);
        return(counter++);
      }
    };

    for(Integer i : it) D.p(i);

    --->
         0
         1
         2
</PRE>
*/
public abstract class PeekIterator<T> implements Iterator<T>, Iterable<T>, Closeable {
  /** Holds the next element (to be peeked)*/
  public T next=null;
  /** TRUE if next has received its first value */
  public boolean initialized=false;

  /** TRUE if there are more elements to get with getNext */
  public final boolean hasNext() {
    if(!initialized) next=internalSilentNext();
    if(next==null) close();
    return(next!=null);
  }

  /** Wraps the Exceptions of internalNext into runtimeExceptions */
  protected final T internalSilentNext() {
    try {
      T next=internalNext();
      initialized=true;
      return(next);
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** Returns the next or NULL if no next element is available*/
  protected abstract T internalNext() throws Exception;

  /** Returns the next element and advances. Overwrite internalNext instead! */
  public T next() {
    if(hasNext()) {
      T returnMe=next;
      next=internalSilentNext();
      return(returnMe);
    }
    throw new NoSuchElementException();
  }

  /** Returns the next element and advances. Overwrite internalNext instead! */
  public T nextOrNull() {
    if(hasNext()) return(next());
    return(null);
  }
  /** Removes the current element, if supported by the underlying iterator*/
  public void remove() {
    throw new UnsupportedOperationException();
  }

  /** returns the next element without advancing*/
  public T peek() {
    if(hasNext()) return(next);
    throw new NoSuchElementException();
  }

  /** returns this*/
  public Iterator<T> iterator() {
    return this;
  }

  /** Closes the underlying resource */
  public void close() {
  }

  /** A PeekIterator that can iterate over another iterator or over a list of elements*/
  public static class SimplePeekIterator<T> extends PeekIterator<T> {
    /** Wrapped iterator */
    public Iterator<T> iterator;
    /** Returns the next or NULL if no next element is available. To be overwritten */
    protected T internalNext() throws Exception {
      if(!iterator.hasNext()) return(null);
      return(iterator.next());
    }
    /** Constructs a PeekIterator from another Iterator */
    public SimplePeekIterator(Iterator<T> i) {
      this.iterator=i;
    }

    /** Constructs a PeekIterator from an Iteratable (e.g. a list)*/
    public SimplePeekIterator(Iterable<T> i) {
      this(i.iterator());
    }

    /** Constructs a PeekIterator for a given list of elements */
    public SimplePeekIterator(T... elements) {
      this(Arrays.asList(elements));
    }
    /** Removes the current element, if supported by the underlying iterator*/
    public void remove() {
      iterator.remove();
    }
  }

  /** test routine*/
  public static void main(String[] args) throws Exception {
    PeekIterator<Integer> it=new SimplePeekIterator<Integer>(1,2,3,4);
    D.p(it.peek());
    D.p(it.peek());
    D.p(it.next());
    D.p(it.peek());
    D.p(it.peek());
  }

}
