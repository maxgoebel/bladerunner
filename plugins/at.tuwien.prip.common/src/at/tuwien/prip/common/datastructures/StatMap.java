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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.common.utils.MathUtils;


/**
 * 
 * StatMap.java
 * 
 * 
 * Convenience class to record statistical observations
 * of a given variable.
 * Keeps an integer count over occurrences of a key.
 * 
 * 
 * Created: Apr 27, 2009 4:29:42 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class StatMap<T>  {

	private HashMap<T,Integer> map;
	
	public StatMap() {
		map = new HashMap<T, Integer>();
	}
	
	public void addKey (T key) {
		map.put(key, 1);
	}
	
	public void increment (T key) {
		if (!map.containsKey(key)) {
			this.addKey(key);
		} else {
			int cnt = map.get(key);
			map.put(key, cnt+1);
		}
	}
	
	public void decrement (T key) {
		if (!map.containsKey(key)) {
			//do nothing
		} else {
			int cnt = map.get(key);
			map.put(key, cnt-1);
		}
	}
	
	public boolean containsKey(T key) {
		return map.containsKey(key);
	}
	
	/**
	 * 
	 * @param deviance
	 * @return
	 */
	public List<T> getSignificant (double deviance) 
	{
		List<Double> population = 
			MathUtils._Convert.convertInt2DoubleList(map.values());
		
		List<T> result =  new LinkedList<T>();
		for (T key : map.keySet()) {
			double x = (double) map.get(key);//Double.parseDouble(""+get(key));
			double z = MathUtils._Statistics.getZScore(x, population);	
			if (/*Math.abs*/(z)>deviance) {
//			if (x>4) {
				result.add(key);
			}
		}
		return result;
	}

	public Set<T> getKeys () {
		return map.keySet();
	}
	
	/**
	 * 
	 * @return
	 */
	public List<T> getNullValue () {
		List<T> result =  new LinkedList<T>();
		for (T key : map.keySet()) {
			if (map.get(key)==0) {
				result.add(key);
			}
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public List<T> getNonNullValue () {
		List<T> result =  new LinkedList<T>();
		for (T key : map.keySet()) {
			if (map.get(key)!=0) {
				result.add(key);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	public List<T> getByCount (int n) {
		List<T> result =  new LinkedList<T>();
		for (T key : map.keySet()) {
			if (map.get(key)==n) {
				result.add(key);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	public List<T> getByFrequency (int n) {
		List<T> result = new LinkedList<T>();
		List<Integer> llist = new ArrayList<Integer>(map.values());
		Collections.sort(llist);
		Collections.reverse(llist);
		ListUtils.unique(llist);
		for (int freq : llist) {
			for (T key : map.keySet()) {
				if (map.get(key)==freq) {
					result.add(key);
				}
			}
		}
		return result.subList(0,n);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<T> getByStartFrequency () {
		List<T> result = new LinkedList<T>();
		List<Integer> llist = new ArrayList<Integer>(map.values());
		Collections.sort(llist);
		Collections.reverse(llist);
		ListUtils.unique(llist);
		for (int freq : llist) {
			for (T key : map.keySet()) {
				if (map.get(key)==freq) {
					result.add(key);
				}
			}
		}
		return result;
	}
	
	public void union (StatMap<T> other) {
		for (T key : other.getKeys()) {
			for (int i=0; i<other.getCount(key); i++) {
				increment(key);
			}
		}
	}
//	public List<T> getMostFrequentPercentage (double perc) {
//		List<T> result = new LinkedList<T>();
//		List<Integer> llist = new ArrayList<Integer>(map.values());
//		Collections.sort(llist);
//		Collections.reverse(llist);
//		ListUtils.unique(llist);
//		
//		for (int freq : llist) {
//			for (T key : map.keySet()) {
//				if (map.get(key)==freq) {
//					result.add(key);
//				}
//			}
//		}
//		return result;
//	}
	
	public int getCount (T key) {
		return map.get(key);
	}
	
}//StatMap
