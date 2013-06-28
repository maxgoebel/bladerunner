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

Provides a nicer constructor for a TreeMap.
Example:
<PRE>
   FinalMap<String,Integer> f=new FinalMap(
     "a",1,
     "b",2,
     "c",3);
   System.out.println(f.get("b"));
   --> 2
</PRE>
*/
public class FinalMap<T1 extends Comparable<?>,T2> extends java.util.TreeMap<T1,T2>{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/** Constructs a FinalMap from an array that contains key/value sequences */
  @SuppressWarnings("unchecked")
  public FinalMap(Object... a) {
    super();
    for(int i=0;i<a.length-1;i+=2) {
      if(containsKey((T1)a[i])) throw new RuntimeException("Duplicate key in FinalMap: "+a[i]);
      put((T1)a[i],(T2)a[i+1]);
    }
  }

  /** Test routine */
  public static void main(String[] args) {
    FinalMap<String,Integer> f=new FinalMap<String,Integer>("a",1,"b",2);
    System.out.println(f.get("b"));
  }
}
