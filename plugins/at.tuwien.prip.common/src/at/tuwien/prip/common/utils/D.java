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
import java.io.*;
import java.util.*;
/**This class is part of the
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

  This class provides convenience methods for Input/Output.
  Allows to do basic I/O with easy procedure calls
  -- nearly like in normal programming languages.
  Furthermore, the class provides basic set operations for EnumSets, NULL-safe
  comparisons and adding to maps.<BR>
  Example:
   <PRE>
      D.p("This is an easy way to write a string");
      // And this is an easy way to read one:
      String s=D.r();

      // Here is a cool way to print something inline
      computeProduct(factor1,(Integer)D.p(factor2));

      // Here are some tricks with enums
      enum T {a,b,c};
      EnumSet&lt;T> i=D.intersection(EnumSet.of(T.a,T.b),EnumSet.of(T.b,T.c));
      EnumSet&lt;T> u=D.union(EnumSet.of(T.a,T.b),EnumSet.of(T.b,T.c));

      // Here is how to compare things, even if they are NULL
      D.compare(object1, object2);

      // Here is how to add something to maps that contain lists
      Map&lt;String,List&lt;String>> string2list=new TreeMap&lt;String,List&lt;String>>();
      D.addKeyValue(string2list,"key","new list element",ArrayList.class);
      // now, the map contains "key" -> [ "new list element" ]
      D.addKeyValue(string2list,"key","again a new list element",ArrayList.class);
      // now, the map contains "key" -> [ "new list element", "again a new list element" ]

      // Here is how to add something to maps that contain integers
      Map&lt;String,Integer> string2list=new TreeMap&lt;String,Integer>();
      D.addKeyValue(string2list,"key",7); // map now contains "key" -> 7
      D.addKeyValue(string2list,"key",3); // map now contains "key" -> 10

   </PRE>
 */
public class D {
  /** Indentation margin. All methods indent their output by indent spaces */
  public static int indent=0;
  /** Prints <indent> spaces */
  public static void i(){
    for(int i=0;i<indent;i++) System.out.print(" ");
  }
  /** Prints a new line  */
  public static void p() {
    System.out.println("");
  }
  /** Prints some Objects */
  public static void p(Object... a) {
    i();
    if(a==null) {
      System.out.print("null-array");
      return;
    }
    for(Object o : a) System.out.print(o+" ");
    System.out.println("");
  }

  /** Prints an Object */
  public static Object p(Object o) {
    i();
    System.out.println(o);
    return(o);
  }

  /** Prints a String */
  public static String p(String o) {
    i();
    System.out.println(o);
    return(o);
  }

  /** Prints an array of integers*/
  public static int[] p(int[] a) {
    i();
    if(a==null) System.out.print("null-array");
    else for(int i=0;i<a.length;i++)
      System.out.print(a[i]+", ");
    System.out.println("");
    return(a);
  }
  /** Prints an array of doubles*/
  public static double[] p(double[] a) {
    i();
    if(a==null) System.out.print("null-array");
    else for(int i=0;i<a.length;i++)
      System.out.print(a[i]+", ");
    System.out.println("");
    return(a);
  }
  /** Reads a line from the keyboard */
  public static String r() {
    String s="";
    i();
    try {
      s=new BufferedReader(new InputStreamReader(System.in)).readLine();
    } catch(Exception whocares) {}
    return(s);
  }
  /** Reads a long from the keyboard */
  public static long readLong(String question) {
    System.out.print(question);
    return(Long.parseLong(D.r()));
  }

  /** Waits for a number of milliseconds */
  public static void waitMS(long milliseconds) {
    long current=System.currentTimeMillis();
    while(System.currentTimeMillis()<current+milliseconds) {
      // wait
    }
  }
  /** Reads a double from the keyboard */
  public static double readDouble(String question) {
    System.out.print(question);
    return(Double.parseDouble(D.r()));
  }
  /** Returns the intersection of two enumsets */
  public static <E extends Enum<E>> EnumSet<E> intersection(EnumSet<E> s1,EnumSet<E> s2) {
    // We have to clone, since retainAll modifies the set
    EnumSet<E> s=s1.clone();
    s.retainAll(s2);
    // I tried coding this for arbitrary sets, but it failed because
    // the interface Cloneable does not make sure that the clone-method
    // is visible (!)
    return(s);
  }
  /** Returns the union of two enumsets */
  public static <E extends Enum<E>> EnumSet<E> union(EnumSet<E> s1,EnumSet<E> s2) {
    EnumSet<E> s=s1.clone();
    s.addAll(s2);
    return(s);
  }
  /** Tells whether the intersection is non-empty */
  public static <E extends Enum<E>> boolean containsOneOf(EnumSet<E> s1,EnumSet<E> s2) {
    return(!intersection(s1,s2).isEmpty());
  }
  /** Exits with error code 0 */
  public static void exit() {
    System.exit(0);
  }
  /** Writes a line to a writer. Yes, this is possible */
  public static void writeln(Writer out, Object s) throws IOException {
    out.write(s.toString());
    out.write("\n");
  }

  /** Writes a line silently to a writer. Yes, this is possible */
  public static void swriteln(Writer out, Object s)  {
    try {
    out.write(s.toString());
    out.write("\n");
    }catch(Exception e) {}
  }

  /** Executes a command */
  public static void execute(String cmd, File folder) throws Exception {
    Process p=Runtime.getRuntime().exec(cmd,null,folder);
    BufferedReader bri=new BufferedReader(new InputStreamReader(p.getInputStream ()));
    BufferedReader bre=new BufferedReader(new InputStreamReader(p.getErrorStream ()));
    String s1,s2=null;
    while(null!=(s1=bri.readLine()) || null!=(s2=bre.readLine())) {
      if(s1!=null) System.out.println(s1);
      if(s2!=null) System.err.println(s2);
    }
    p.waitFor();
  }

  /** Given a map that maps to collections, adds a new key/value pair or introduced the key*/
  @SuppressWarnings("unchecked")
  public static <K,V,C extends Collection<V>, L extends Collection> void addKeyValue(Map<K,C> map, K key, V value, Class<L> collectionType) {
    C coll=map.get(key);
    if(coll==null) {
      try {
        map.put(key, coll=(C)collectionType.newInstance());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    coll.add(value);
  }

  /** Given a map that maps to integers, adds a new key/value pair or introduced the key*/
  public static <K> void addKeyValue(Map<K,Integer> map, K key, int value) {
    Integer coll=map.get(key);
    if(coll==null) {
        map.put(key, value);
        return;

      }

    map.put(key,coll+value);
  }

  /** Returns true if two things are equal, including NULL */
  public static <E> boolean equal(E s1, E s2) {
    if(s1==s2) return(true);
    if(s1==null) return(false);
    if(s2==null) return(false);
    return(s1.equals(s2));
  }

  /** Compares two things, including NULL */
  public static <E extends Comparable<E>> int compare(E s1, E s2) {
    if(s1==s2) return(0);
    if(s1==null) return(-1);
    if(s2==null) return(1);
    return(s1.compareTo(s2));
  }

  /** Compares pairs of comparable things (a1,a2,b1,b2,...), including NULL */
  @SuppressWarnings("unchecked")
  public static int comparePairs(Object... o) {
    for(int i=0;i<o.length;i+=2) {
      int c=compare((Comparable)o[i],(Comparable)o[i+1]);
      if(c!=0) return(c);
    }
    return(0);
  }

  /** Compares pairs of comparable things (a1,a2,b1,b2,...) for equality, including NULL */
  public static boolean equalPairs(Object... o) {
    for(int i=0;i<o.length;i+=2) {
      if(!equal(o[i],o[i+1])) return(false);
    }
    return(true);
  }

  /** Returns the index of a thing in an array or -1*/
  public static int indexOf(Object o, Object[] os) {
    for(int i=0;i<os.length;i++) {
      if(os[i].equals(o)) return(i);
    }
    return(-1);
  }

  /** Returns the String rep of an array */
  public static String toString(Object[] os) {
    StringBuilder b=new StringBuilder();
    for (int i = 0; i < os.length-1; i++) {
      b.append(os[i]).append(", ");
    }
    if(os.length>0) b.append(os[os.length-1]);
    return(b.toString());
  }
}
