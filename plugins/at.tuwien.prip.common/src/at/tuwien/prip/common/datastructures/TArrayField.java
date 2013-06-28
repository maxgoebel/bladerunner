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

import gnu.trove.THashMap;
import gnu.trove.TObjectHashingStrategy;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Data structure for two-dimensional relation tuples.
 * 
 * Essentially, this is a wrapper class for a two-dimensional
 * hashmap (two index keys) --- build using the gnu.trove 
 * hashmap implemenation.
 * 
 * @author max
 *
 * @param <S>
 * @param <T>
 * @param <U>
 */
public class TArrayField <S,T,U>{

	private HashMap2<S, T, U> map;

	private final U DEFAULT_VALUE;

	public TArrayField(U defaultValue) {
		map = new HashMap2<S, T, U>(null, null, null);
		DEFAULT_VALUE = defaultValue;
	}

	public List<T> getColumns () {
		return map.getAllSecondKeys();
	}

	public List<S> getRows () {
		return map.getAllFirstKeys();
	}

	public void addRow (S rowID, List<T> rowCols, List<U> rowVals) {
		assert rowCols.size()==rowVals.size();
		
		for (int i=0; i<rowCols.size(); i++) {
			
			T col = rowCols.get(i);
			U value = rowVals.get(i);
			
			if (col==null || value==null) 
				throw new IllegalArgumentException (
						"Error inserting given data into TArrayField");
			map.put(rowID, col, value);
		}
	}

	public U getValue (S dim1, T dim2) {
		U result = map.get(dim1, dim2);
		if (result==null)
			return DEFAULT_VALUE;
		return result;
	}


	class HashMap2<K1,K2,V>
	implements Map2<K1,K2,V>
	{

		private class KeyPair extends Pair<K1,K2> {

			private static final long serialVersionUID = 4461204205338254694L;

			public KeyPair(K1 a, K2 b) {
				super(a, b);
			}

			@SuppressWarnings("unchecked")
			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof Pair)) return false;
				Pair p = (Pair) obj;

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

//		public HashMap2() {
//		this(null, null);
//		}

//		public HashMap2(TObjectHashingStrategy<K1> key1HashingStrategy,
//		TObjectHashingStrategy<K2> key2HashingStrategy)
//		{
//		this(key1HashingStrategy, key2HashingStrategy, null);
//		}

		public HashMap2(TObjectHashingStrategy<K1> key1HashingStrategy,
				TObjectHashingStrategy<K2> key2HashingStrategy,
				TObjectHashingStrategy<V> valHashingStrategy)
		{
			this.map = new THashMap<KeyPair,V>();
			this.key1HashingStrategy = key1HashingStrategy;
			this.key2HashingStrategy = key2HashingStrategy;
			this.valHashingStrategy = valHashingStrategy;
		}

//		public HashMap2(Map2<? extends K1, ? extends K2, ? extends V> t) {
//		this();
//		putAll(t);
//		}

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
					 if (!contains(i.next())) {
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
				 Iterator<?> e = iterator();
				 for (int i=0; e.hasNext(); i++)
					 result[i] = e.next();
				 return result;
			 }

			 @SuppressWarnings({ "unchecked", "hiding" })
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
				 Iterator<?> i = iterator();
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

		 @Override
		 public boolean equals(Object obj) {
			 if (!(obj.getClass().equals(HashMap2.class))) return false;
			 HashMap2<?,?,?> o = (HashMap2<?,?,?>) obj;

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
	}
	
	static class CompareUtils {

	    /**
	     * null-safe equals method
	     */
	    public static <T> boolean eq(T o1, T o2) {
	        if (o1==null && o2==null) return true;
	        if (o1==null || o2==null) return false;
	        return o1==o2 || o1.equals(o2);
	    }
	    public static <T> boolean eq(T o1, T o2, TObjectHashingStrategy<T> s) {
	        if (o1==null && o2==null) return true;
	        if (o1==null || o2==null) return false;
	        return
	            o1==o2 ||
	            (s==null ? o1.equals(o2) : s.equals(o1, o2));
	    }

	    @SuppressWarnings("unchecked")
	    public static int compareTo(Comparable o1, Comparable o2) {
	        if (o1==null && o2==null) return 0;
	        if (o1!=null && o2!=null) {
	            return o1.compareTo(o2);
	        }
	        if (o1==null) return -1;
	        if (o2==null) return 1;
	        return 0;
	    }


	    public static <T1,T2> int hashCode2(T1 o1, T2 o2) {
	        if (o1==null) {
	            if (o2==null)
	                return 0;
	            return o2.hashCode();
	        }
	        else
	            if (o2==null)
	                return o1.hashCode();
	            else
	                return o1.hashCode()^o2.hashCode();
	    }

	    public static <T1,T2> int hashCode(T1 o1, T2 o2, TObjectHashingStrategy<T1> s1, TObjectHashingStrategy<T2> s2) {
	        if (o1==null) {
	            if (o2==null)
	                return 0;
	            return (s2==null ? o2.hashCode() : s2.computeHashCode(o2));
	        }
	        else
	            if (o2==null)
	                return
	                    (s1==null ? o1.hashCode() : s1.computeHashCode(o1));
	            else
	                return
	                    (s1==null ? o1.hashCode() : s1.computeHashCode(o1)) ^
	                    (s2==null ? o2.hashCode() : s2.computeHashCode(o2));
	    }

	    public static <T1,T2,T3> int hashCode(T1 o1, T2 o2, T3 o3)
	    {
	        if (o1==null)
	            if (o2==null) {
	            	if (o3==null)
	            		return 0;
	            	return o3.hashCode();
	            }
	            else
	                if (o3==null)
	                    return o2.hashCode();
	                else
	                    return o2.hashCode()^o3.hashCode();
	        else
	            if (o2==null) {
	                if (o3==null)
	                	return o1.hashCode();
	                return o1.hashCode()^o3.hashCode();
	            }
	            else
	                if (o3==null)
	                    return o1.hashCode()^o2.hashCode();
	                else
	                    return o1.hashCode()^o2.hashCode()^o3.hashCode();
	    }

	    public static <T1,T2,T3> int
	        hashCode(T1 o1, T2 o2, T3 o3,
	                 TObjectHashingStrategy<T1> s1,
	                 TObjectHashingStrategy<T2> s2,
	                 TObjectHashingStrategy<T3> s3)
	    {
	        if (o1==null)
	            if (o2==null) {
	                if (o3==null)
	                    return 0;
	                return (s3==null ? o3.hashCode() : s3.computeHashCode(o3));
	            }
	            else
	                if (o3==null)
	                    return
	                        (s2==null ? o2.hashCode() : s2.computeHashCode(o2));
	                else
	                    return
	                        (s2==null ? o2.hashCode() : s2.computeHashCode(o2)) ^
	                        (s3==null ? o3.hashCode() : s3.computeHashCode(o3));
	        else
	            if (o2==null) {
	                if (o3==null)
	                    return
	                        (s1==null ? o1.hashCode() : s1.computeHashCode(o1));

	                return
	                (s1==null ? o1.hashCode() : s1.computeHashCode(o1)) ^
	                (s3==null ? o3.hashCode() : s3.computeHashCode(o3));
	            }
	            else
	                if (o3==null)
	                    return
	                        (s1==null ? o1.hashCode() : s1.computeHashCode(o1)) ^
	                        (s2==null ? o2.hashCode() : s2.computeHashCode(o2));
	                else
	                    return
	                        (s1==null ? o1.hashCode() : s1.computeHashCode(o1)) ^
	                        (s2==null ? o2.hashCode() : s2.computeHashCode(o2)) ^
	                        (s3==null ? o3.hashCode() : s3.computeHashCode(o3));
	    }

	}

}
