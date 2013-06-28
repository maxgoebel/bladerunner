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
///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package at.tuwien.prip.common.datastructures;

import gnu.trove.THashMap;
import gnu.trove.TObjectHashingStrategy;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.tuwien.prip.common.utils.CompareUtils;

/**
 * An implementation of the Map interface which uses an open addressed
 * hash table to store its contents.
 *
 * Created: Sun Nov  4 08:52:45 2001
 *
 * @author Eric D. Friedman
 * @version $Id: THashMap.java,v 1.23 2006/11/21 18:19:35 robeden Exp $
 */
public class HashMap2<K1,K2,V>
    implements Map2<K1,K2,V>
{

    public class KeyPair extends Pair<K1,K2> {

        private static final long serialVersionUID = 4461204205338254694L;

        public KeyPair(K1 a, K2 b) {
            super(a, b);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Pair)) return false;
            Pair<K1,K2> p = (Pair<K1,K2>) obj;

            if (!CompareUtils.eq(a, (K1) p.a, key1HashingStrategy))
                return false;

            if (!CompareUtils.eq(b, (K2) p.b, key2HashingStrategy))
                return false;

            //all tests passed
            return true;
        }

        @Override
        public int hashCode() {
            return
                CompareUtils.
                hashCode(a, b,
                         key1HashingStrategy,
                         key2HashingStrategy);
        }

        @Override
        public String toString() {
            return String.format("%s,%s",a,b);
        }
    }

    private final THashMap<KeyPair,V> map;
    private final TObjectHashingStrategy<K1> key1HashingStrategy;
    private final TObjectHashingStrategy<K2> key2HashingStrategy;
    private final TObjectHashingStrategy<V> valHashingStrategy;

//    public HashMap2() {
//        this(null, null);
//    }

//    public HashMap2(TObjectHashingStrategy<K1> key1HashingStrategy,
//                    TObjectHashingStrategy<K2> key2HashingStrategy)
//    {
//        this(key1HashingStrategy, key2HashingStrategy, null);
//    }

    public HashMap2(TObjectHashingStrategy<K1> key1HashingStrategy,
                    TObjectHashingStrategy<K2> key2HashingStrategy,
                    TObjectHashingStrategy<V> valHashingStrategy)
    {
        this.map = new THashMap<KeyPair,V>();
        this.key1HashingStrategy = key1HashingStrategy;
        this.key2HashingStrategy = key2HashingStrategy;
        this.valHashingStrategy = valHashingStrategy;
    }

//    public HashMap2(Map2<? extends K1, ? extends K2, ? extends V> t) {
//        this();
//        putAll(t);
//    }

    public HashMap2(Map2<? extends K1, ? extends K2, ? extends V> t,
                    TObjectHashingStrategy<K1> key1HashingStrategy,
                    TObjectHashingStrategy<K2> key2HashingStrategy,
                    TObjectHashingStrategy<V> valHashingStrategy)
    {
        this(key1HashingStrategy, key2HashingStrategy, valHashingStrategy);
        putAll(t);
    }

    public HashMap2(HashMap2<K1,K2,V> t)
    {
        this(t.key1HashingStrategy, t.key2HashingStrategy, t.valHashingStrategy);
        putAll(t);
    }

    private KeyPair p(K1 key1, K2 key2) {
        return new KeyPair(key1, key2);
    }

    public boolean containsKey(K1 key1, K2 key2) {
        return map.containsKey(p(key1, key2));
    }

    public V get(K1 key1, K2 key2) {
        return map.get(p(key1, key2));
    }

    public V put(K1 key1, K2 key2, V value) {
        return map.put(p(key1, key2), value);
    }

    public void putAll(Map2<? extends K1, ? extends K2, ? extends V> t) {
        for (Map2.Entry2<? extends K1, ? extends K2, ? extends V> e : t.entrySet()) {
            map.put(p(e.getKey1(), e.getKey2()), e.getValue());
        }
    }

    public V remove(K1 key1, K2 key2) {
        return map.remove(p(key1, key2));
    }

    public void clear() {
        map.clear();
    }

    public boolean containsValue(V value) {
        return map.containsValue(value);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

    public Collection<V> values() {
        return map.values();
    }

    public Set<Map2.Entry2<K1, K2, V>> entrySet() {
        return new Entry2View();
    }

    /**
     * a view onto the entries of the map.
     */
    protected class Entry2View extends MapBackedView<Map2.Entry2<K1,K2,V>> {

        @Override
        public boolean containsElement(Map2.Entry2<K1, K2, V> entry) {
            V val = get(entry.getKey1(), entry.getKey2());
            V entryValue = entry.getValue();
            return CompareUtils.eq(val, entryValue, valHashingStrategy);
        }

        @Override
        public boolean removeElement(Map2.Entry2<K1, K2, V> entry) {
            V val = get(entry.getKey1(), entry.getKey2());
            V entryValue = entry.getValue();
            if (CompareUtils.eq(val, entryValue, valHashingStrategy)) {
                HashMap2.this.remove(entry.getKey1(), entry.getKey2());
                return true;
            }
            return false;
        }

        @Override
        public Iterator<Map2.Entry2<K1, K2, V>> iterator() {
            return new Entry2Iterator();
        }

        protected class Entry2Iterator implements Iterator<Map2.Entry2<K1, K2, V>> {

            private final Iterator<Map.Entry<KeyPair, V>> it = map.entrySet().iterator();

            public Entry2Iterator() {
            }

            public boolean hasNext() {
                return it.hasNext();
            }

            public Map2.Entry2<K1, K2, V> next() {
                final Map.Entry<KeyPair, V> e = it.next();
                return new SimpleEntry2(e);
            }

            public void remove() {
                it.remove();
            }
        }

    }

    protected class SimpleEntry2 implements Map2.Entry2<K1,K2,V> {

        private final Map.Entry<KeyPair,V> e;

        SimpleEntry2(Map.Entry<KeyPair,V> e) {
            this.e = e;
        }

        public K1 getKey1() {
            return e.getKey().getFirst();
        }

        public K2 getKey2() {
            return e.getKey().getSecond();
        }

        public KeyPair2<K1, K2> getKeyPair() {
            return new SimpleKeyPair2(e.getKey());
        }

        public V getValue() {
            return e.getValue();
        }

        public V setValue(V newValue) {
            return e.setValue(newValue);
        }

        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (!(o instanceof Map2.Entry2))
                return false;
            Map2.Entry2<K1,K2,V> e = (Map2.Entry2)o;

            K1 ka1 = getKey1();
            K1 kb1 = e.getKey1();
            if (!CompareUtils.eq(ka1, kb1, key1HashingStrategy))
                return false;

            K2 ka2 = getKey2();
            K2 kb2 = e.getKey2();
            if (!CompareUtils.eq(ka2, kb2, key2HashingStrategy))
                return false;

            V v1 = getValue();
            V v2 = e.getValue();
            if (!CompareUtils.eq(v1, v2, valHashingStrategy))
                return false;

            return true;
        }

        public int hashCode() {
            K1 k1 = getKey1();
            K2 k2 = getKey2();
            V v = getValue();

            return
                CompareUtils.
                hashCode(k1, k2, v,
                         key1HashingStrategy,
                         key2HashingStrategy,
                         valHashingStrategy);
        }

        public String toString() {
            return getKey1() + "," + getKey2() + "=" + getValue();
        }
    }

    private abstract class MapBackedView<E> extends AbstractSet<E>
        implements Set<E> {

        public abstract Iterator<E> iterator();

        public abstract boolean removeElement(E key);

        public abstract boolean containsElement(E key);

        @SuppressWarnings("unchecked")
        public boolean contains(Object key) {
            return containsElement((E) key);
        }

        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            return removeElement((E) o);
        }

        public boolean containsAll(Collection<?> collection) {
            for (Iterator<?> i = collection.iterator(); i.hasNext();) {
                if (! contains(i.next())) {
                    return false;
                }
            }
            return true;
        }

        public void clear() {
            HashMap2.this.clear();
        }

        public boolean add(E obj) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return HashMap2.this.size();
        }

        public Object[] toArray() {
            Object[] result = new Object[size()];
            Iterator<E> e = iterator();
            for (int i=0; e.hasNext(); i++)
                result[i] = e.next();
            return result;
        }

        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            int size = size();
            if (a.length < size)
                a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);

            Iterator<E> it = iterator();
            Object[] result = a;
            for (int i=0; i<size; i++) {
                result[i] = it.next();
            }

            if (a.length > size) {
                a[size] = null;
            }

            return a;
        }

        public boolean isEmpty() {
            return HashMap2.this.isEmpty();
        }

        public boolean addAll(Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> collection) {
            boolean changed = false;
            Iterator<E> i = iterator();
            while (i.hasNext()) {
                if (! collection.contains(i.next())) {
                    i.remove();
                    changed = true;
                }
            }
            return changed;
        }
    }

    public Set<KeyPair2<K1, K2>> keySet() {
        return new Key2View();
    }

    protected class Key2View extends MapBackedView<Map2.KeyPair2<K1,K2>> {

        @Override
        public boolean containsElement(Map2.KeyPair2<K1,K2> key) {
            return map.containsKey(p(key.getKey1(), key.getKey2()));
        }

        @Override
        public boolean removeElement(Map2.KeyPair2<K1,K2> key) {
            KeyPair pr =  p(key.getKey1(), key.getKey2());
            if (map.containsKey(pr)) {
                map.remove(pr);
                return true;
            }
            return false;
        }

        @Override
        public Iterator<Map2.KeyPair2<K1,K2>> iterator() {
            return new Key2Iterator();
        }

        protected class Key2Iterator implements Iterator<Map2.KeyPair2<K1,K2>> {

            private final Iterator<KeyPair> it = map.keySet().iterator();

            public Key2Iterator() {
            }

            public boolean hasNext() {
                return it.hasNext();
            }

            public Map2.KeyPair2<K1,K2> next() {
                final KeyPair p = it.next();
                return new SimpleKeyPair2(p);
            }

            public void remove() {
                it.remove();
            }
        }
    }

    protected class SimpleKeyPair2 implements Map2.KeyPair2<K1,K2> {

        private final KeyPair p;

        SimpleKeyPair2(KeyPair p) {
            this.p = p;
        }

        public K1 getKey1() {
            return p.getFirst();
        }

        public K2 getKey2() {
            return p.getSecond();
        }

        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (!(o instanceof Map2.KeyPair2))
                return false;
            Map2.KeyPair2<K1,K2> e = (Map2.KeyPair2)o;

            K1 ka1 = getKey1();
            K1 kb1 = e.getKey1();
            if (!CompareUtils.eq(ka1, kb1, key1HashingStrategy))
                return false;

            K2 ka2 = getKey2();
            K2 kb2 = e.getKey2();
            if (!CompareUtils.eq(ka2, kb2, key2HashingStrategy))
                return false;

            return true;
        }

        public int hashCode() {
            K1 k1 = getKey1();
            K2 k2 = getKey2();
            return
                CompareUtils.
                hashCode(k1, k2,
                         key1HashingStrategy,
                         key2HashingStrategy);
        }

        public String toString() {
            return getKey1() + "," + getKey2();
        }
    }

    public List<V> getFirstProjection(K1 key1) {
        LinkedList<V> ret = new LinkedList<V>();

        for (Map2.Entry2<K1, K2, V> e : entrySet()) {
            if (CompareUtils.eq(key1, e.getKey1(), key1HashingStrategy))
                ret.add(e.getValue());
        }

        return ret;
    }

    public List<V> getSecondProjection(K2 key2) {
        LinkedList<V> ret = new LinkedList<V>();

        for (Map2.Entry2<K1, K2, V> e : entrySet()) {
            if (CompareUtils.eq(key2, e.getKey2(), key2HashingStrategy))
                ret.add(e.getValue());
        }

        return ret;
    }

    public List<K1> getAllFirstKeys() {
        LinkedList<K1> ret = new LinkedList<K1>();

        for (Map2.KeyPair2<K1, K2> key : keySet()) {
            K1 k1 = key.getKey1();
            if (!ret.contains(k1)) ret.add(k1);
        }

        return ret;
    }

    public List<K2> getAllSecondKeys() {
        LinkedList<K2> ret = new LinkedList<K2>();

        for (Map2.KeyPair2<K1, K2> key : keySet()) {
            K2 k2 = key.getKey2();
            if (!ret.contains(k2)) ret.add(k2);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HashMap2<?, ?, ?>)) return false;
        HashMap2<K1,K2,V> o = (HashMap2<K1,K2,V> ) obj;

        return map.equals(o.map);
    }

	public List<K2> getSecondFromFirstKeys(K1 key1) {
        LinkedList<K2> ret = new LinkedList<K2>();

        for (Map2.KeyPair2<K1, K2> key : keySet()) {
        	if (key.getKey1().equals(key1)) {
        		 K2 k2 = key.getKey2();
                 if (!ret.contains(k2)) ret.add(k2);	
        	}
        }

        return ret;
	}

} // THashMap2
