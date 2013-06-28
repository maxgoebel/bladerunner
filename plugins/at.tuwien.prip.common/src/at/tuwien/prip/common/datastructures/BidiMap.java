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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bi-directional hashmap
 */
public class BidiMap<A,B>
{

    Map<A,B> m1 = new HashMap<A,B>();
    Map<B,A> m2 = new HashMap<B,A>();

    public void clear() {
        m1.clear();
        m2.clear();
    }

    public Set<A> keySetA() {
    	return m1.keySet();
    }
    
    public Set<B> keySetB() {
    	return m2.keySet();
    }
    
    public boolean contains(A a, B b) {
        B bb = m1.get(a);
        return
            b!=null &&
            b.equals(bb);
    }
    
    public boolean containsKey(A a) {
    	return m1.containsKey(a);
    }
    
    public boolean containsValue(B b) {
    	return m2.containsKey(b);
    }

    public B get(A key) {
        return m1.get(key);
    }
    public A reverseGet(B key) {
        return m2.get(key);
    }

    public boolean isEmpty() {
        return m1.isEmpty();
    }

    public void put(A a, B b) {
        if (m1.containsKey(a) ||
            m2.containsKey(b))
            throw new RuntimeException("already exists");

        m1.put(a, b);
        m2.put(b, a);
    }

    public void remove(A a, B b) {
        m1.remove(a);
        m2.remove(b);
    }

    public int size() {
        return m1.size();
    }

    public Set<Map.Entry<A,B>> values() {
        return m1.entrySet();
    }

}
