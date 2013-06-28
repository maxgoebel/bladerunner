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

import gnu.trove.TObjectHashingStrategy;
import gnu.trove.TObjectIdentityHashingStrategy;


/**
 * 
 * SparseMatrix.java
 *
 *
 * A sparse matrix implementation.
 *
 * Created: May 16, 2009 10:51:04 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class SparseMatrix2<K1,K2,V> extends HashMap2<K1,K2,V> {
	
//	protected int[] dims;
//	protected double[] values;
	
	public final TObjectHashingStrategy<K1> key1HashingStrategy = new TObjectIdentityHashingStrategy<K1>();
	public final TObjectHashingStrategy<K2> key2HashingStrategy = new TObjectIdentityHashingStrategy<K2>();
	public final TObjectHashingStrategy<V> doubleHashingStrategy = new TObjectIdentityHashingStrategy<V>();
			
	final V DEFAULT;

	/**
	 * 
	 * Constructor.
	 * 
	 * @param default value.
	 */
	public SparseMatrix2 (V defaultVal) {
		super (null,null,null);
//		key1HashingStrategy,key2HashingStrategy,doubleHashingStrategy);
		this.DEFAULT = defaultVal;
	}
	
	/**
	 * 
	 * Enter a value.
	 * 
	 * @param a
	 * @param b
	 * @param val
	 */
	public void setValue (K1 a, K2 b, V val) {
		put(a, b, val);
	}
	
	/**
	 * 
	 * Retrieve a value.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public V getValue (K1 a, K2 b) {
		if (this.containsKey(a,b) && this.get(a,b)!=null) {
			return get(a, b);
		}
		return DEFAULT; //not found
	}
	
}//SparseMatrix
