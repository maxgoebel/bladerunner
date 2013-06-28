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

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import at.tuwien.prip.common.datastructures.Map2;
import at.tuwien.prip.common.datastructures.Pair;

public class ListUtils  {

	public static <A,B> Pair<ArrayList<ArrayList<A>>, ArrayList<ArrayList<B>>>
	unzip(List<List<Pair<A,B>>> listListPair)
	{
		ArrayList<ArrayList<A>> listListFirst = new ArrayList<ArrayList<A>>(listListPair.size());
		ArrayList<ArrayList<B>> listListSecond = new ArrayList<ArrayList<B>>(listListPair.size());
		for (List<Pair<A, B>> listPair: listListPair) {
			ArrayList<A> listFirst = new ArrayList<A>(listPair.size());
			ArrayList<B> listSecond = new ArrayList<B>(listPair.size());
			for (Pair<A, B> pair : listPair) {
				listFirst.add(pair.getFirst());
				listSecond.add(pair.getSecond());
			}
			listListFirst.add(listFirst);
			listListSecond.add(listSecond);
		}

		return new Pair<ArrayList<ArrayList<A>>, ArrayList<ArrayList<B>>>(
				listListFirst, listListSecond);
	}

	public static void unique (Collection<?> list) {
		Collection<Object> copyList = new LinkedList<Object> ();
		Iterator<?> it = list.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (!copyList.contains(obj)) {
				copyList.add(obj);
			} else {
				it.remove();
			}
		}

	}
	
	public static int max (List<Integer> list) {
		int max = -1000;
		for (Integer i : list) {
			if (i>max) {
				max = i;
			}
		}
		return max;
	}

	public static Set<String> toSet(String[] strings) {
		HashSet<String> lhs = new LinkedHashSet<String>();
		for (int i=0; i<strings.length; i++) {
			lhs.add(strings[i]);
		}
		return lhs;
	}

	public static List<String> toList(String[] strings) {
		List<String> l = new LinkedList<String>();
		for (int i=0; i<strings.length; i++) {
			l.add(strings[i]);
		}
		return l;
	}

	public static List<Object> toList(Object[] objs) {
		List<Object> l = new LinkedList<Object>();
		for (int i=0; i<objs.length; i++) {
			l.add(objs[i]);
		}
		return l;
	}

	public static List<File> toList(File[] files) {
		List<File> l = new LinkedList<File>();
		for (int i=0; i<files.length; i++) {
			l.add(files[i]);
		}
		return l;
	}

	public static int[] range(int from, int to) {
		assert from<=to;
		int len = to-from+1;
		int[] l = new int[len];
		for (int k=0; k<len; k++) {
			l[k]=k+from;
		}
		return l;
	}
	public static List<Integer> rangeList(int from, int to) {
		List<Integer> l = new LinkedList<Integer>();
		for (int k=from; k<=to; k++) {
			l.add(k);
		}
		return l;
	}

	public static List<Integer> toList(int[] integers) {
		List<Integer> l = new LinkedList<Integer>();
		for (int i=0; i<integers.length; i++) {
			l.add(integers[i]);
		}
		return l;
	}

	public static <C> List<C> toList (C[] objs, Class<C> c) {
		List<C> l = new LinkedList<C>();
		for (int i=0; i<objs.length; i++) {
			l.add(objs[i]);
		}
		return l;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Collection<T> l, Class<T> c) {
		T[] a = (T[]) Array.newInstance(c, l.size());
		return l.toArray(a);
	}

	public static <T> List<T> reverse(List<T> list) {
		List<T> result = new LinkedList<T>();
		for (int i=list.size()-1; i>=0; i--) {
			result.add(list.get(i));
		}
		return result;
	}

	public static <T> List<T> union(Collection<T> s1, Collection<T> s2) {
		List<T> u = new LinkedList<T>();
		if (s1!=null) u.addAll(s1);
		if (s2!=null) u.addAll(s2);
		return u;
	}

	public static <T> List<T> union(Collection<T> ... s) {

		List<T> u = new LinkedList<T>();
		for (Collection<T> collection : s) {
			u.addAll(collection);
		}
		return u;
	}

	public static <A,B> List<A> projectFirst(Collection<Pair<A,B>> abs) {
		List<A> as = new LinkedList<A>();
		for (Pair<A, B> ab : abs) {
			as.add(ab.getFirst());
		}
		return as;
	}

	public static <A,B> List<B> projectSecond(Collection<Pair<A,B>> abs) {
		List<B> bs = new LinkedList<B>();
		for (Pair<A, B> ab : abs) {
			bs.add(ab.getSecond());
		}
		return bs;
	}

	public static <A,B> List<A> map2projectFirst(Collection<Map2.KeyPair2<A,B>> abs) {
		List<A> as = new LinkedList<A>();
		for (Map2.KeyPair2<A, B> ab : abs) {
			as.add(ab.getKey1());
		}
		return as;
	}

	public static <A,B> List<B> map2projectSecond(Collection<Map2.KeyPair2<A,B>> abs) {
		List<B> bs = new LinkedList<B>();
		for (Map2.KeyPair2<A, B> ab : abs) {
			bs.add(ab.getKey2());
		}
		return bs;
	}

	public static <A,B,C> List<B> map2secondFromFirst(Map2<A,B,C> map, A key1) {
		List<B> bs = new LinkedList<B>();
		for (Map2.KeyPair2<A,B> ab : map.keySet()) {
			if (ab.getKey1().equals(key1)) {
				bs.add(ab.getKey2());
			}
		}
		return bs;
	}

	public static <A> List<A> flatten(Collection<List<A>> lla) {
		List<A> ret = new LinkedList<A>();
		Iterator<List<A>> it = lla.iterator();
		while (it.hasNext()) {
			List<A> la = (List<A>) it.next();
			for (A a : la) {
				if (!ret.contains(a)) ret.add(a);
			}
		}
		return ret;
	}

	public static <A> Set<A> union(Collection<? extends Collection<A>> lla) {
		Set<A> ret = new LinkedHashSet<A>();
		for (Collection<A> la : lla) {
			ret.addAll(la);
		}
		return ret;
	}

	public static <T> boolean equalAsSets(Collection<T> c1, Collection<T> c2)
	{
		HashSet<T> s1 = new HashSet<T>(c1);
		HashSet<T> s2 = new HashSet<T>(c2);
		return s1.equals(s2);
	}

}
