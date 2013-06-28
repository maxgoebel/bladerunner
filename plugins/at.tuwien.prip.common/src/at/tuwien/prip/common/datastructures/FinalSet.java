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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Set;

import at.tuwien.prip.common.utils.D;
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

This class provides a very simple container implementation with zero overhead.
A FinalSet bases on a sorted, unmodifiable array. The constructor
can either be called with a sorted unmodifiable array (default constructor)
or with an array that can be cloned and sorted beforehand if desired.
Example:
<PRE>
   FinalSet<String> f=new FinalSet("a","b","c");
   // equivalently:
   //   FinalSet<String> f=new FinalSet(new String[]{"a","b","c"});
   //   FinalSet<String> f=new FinalSet(SHALLNOTBECLONED,ISSORTED,"a","b","c");
   System.out.println(f.get(1));
   --> b
</PRE>
*/
public class FinalSet<T extends Comparable<?>> extends AbstractList<T> implements Set<T>{
  /** Holds the data, must be sorted */
  public T[] data;
  /** Constructs a FinalSet from an array, clones and sorts the array if indicated. */
  @SuppressWarnings("unchecked")
  public FinalSet(boolean clone,T... a) {
    if(clone) {
      Comparable[] b=new Comparable[a.length];
      System.arraycopy(a,0,b,0,a.length);
      a=(T[])b;
    }
    Arrays.sort(a);
    data=a;
  }
  /** Constructs a FinalSet from an array that does not need to be cloned */
  public FinalSet(T... a) {
    this(false,a);
  }
  /** Tells whether x is in the container */
  public boolean contains(T x) {
    return(Arrays.binarySearch(data,x)>=0);
  }
  /** Returns the position in the array or -1 */
  public int indexOf(T x) {
    int r=Arrays.binarySearch(data,x);
    return(r>=0?r:-1);
  }
  /** Returns the element at position i*/
  public T get(int i) {
    return(data[i]);
  }

  /** Returns the number of elements in this FinalSet */
  public int size() {
    return(data.length);
  }

  /** Test routine */
  public static void main(String[] args) {
    FinalSet<String> f=new FinalSet<String>("b","a","c");
    D.p(f.get(1));
  }
}
