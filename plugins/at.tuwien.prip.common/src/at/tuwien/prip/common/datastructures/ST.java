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

/*************************************************************************
 *  Compilation:  javac ST.java
 *  Execution:    java ST
 *  
 *  Symbol table implementation using Java's java.util.TreeMap library.
 *  Does not allow duplicates.
 *
 *  % java ST
 *  128.112.136.11
 *  208.216.181.15
 *  null
 *  
 *  Copyright © 2007, Robert Sedgewick and Kevin Wayne. 
 *  Last updated: Thu May 3 08:56:22 EDT 2007.
 *
 *************************************************************************/

import java.util.Set;
import java.util.TreeMap;
import java.util.Iterator;

public class ST<Key extends Comparable<Key>, Val> implements Iterable<Key> {
    private TreeMap<Key, Val> st;

    public ST() {
        st = new TreeMap<Key, Val>();
    }

    public void put(Key key, Val val) {
        if (val == null) st.remove(key);
        else             st.put(key, val);
    }
    public Val get(Key key)             { return st.get(key);            }
    public Val remove(Key key)          { return st.remove(key);         }
    public boolean contains(Key key)    { return st.containsKey(key);    }
    public int size()                   { return st.size();              }
    public Iterator<Key> iterator()     { return st.keySet().iterator(); }

    public Set<Key> getIndices () {
    	return st.keySet();
    }
    
   /***********************************************************************
    * Test routine.
    **********************************************************************/
    public static void main(String[] args) {
        ST<String, String> st = new ST<String, String>();

        // insert some key-value
        st.put("www.cs.princeton.edu", "128.112.136.11");
        st.put("www.princeton.edu",    "128.112.128.15");
        st.put("www.yale.edu",         "130.132.143.21");
        st.put("www.amazon.com",       "208.216.181.15");
        st.put("www.simpsons.com",     "209.052.165.60");

        // search for IP addresses given URL
        System.out.println(st.get("www.cs.princeton.edu"));
        System.out.println(st.get("www.amazon.com"));
        System.out.println(st.get("www.amazon.edu"));
        System.out.println();

        // print out all key-value pairs
        for (String s : st)
            System.out.println(s + " " + st.get(s));
    }

}
