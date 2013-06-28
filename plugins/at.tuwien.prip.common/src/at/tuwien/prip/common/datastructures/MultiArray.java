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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Fast multi-dimenstional array
 *
 * Implementation of a multidimensional array.
 *
 * Example of use:
 * Create a 3x5x7 MultiArray    :  a = new MultiArray(new int[] {3,5,7})
 * Set element (i,j,k) to value :  a.set(new int[] {i,j,k},value)
 * Get element (i,j,k)          :  a.get(new int[] {i,j,k})
 */
public class MultiArray<T> {

	protected T[] data;                // data, one-dimensional array
	protected int[]    dimensions;     // dimensions.length = number of dimensions
	// dimensions[i] = the size of dimension i
	protected int[]    factors;        // factors for computing positions

	@SuppressWarnings("unchecked")
	public MultiArray (Class<T> c, int[] arg) {
		dimensions  = new int[arg.length];
		factors     = new int[arg.length];
		int product = 1;
		for (int i=arg.length-1; i>=0; --i) {
			dimensions[i] = arg[i];
			factors[i]    = product;
			product *= dimensions[i];
		}
		data = (T[]) Array.newInstance(c, product);
	}

	public void set (int[] indices, T object) {
		data[getOffset(indices)] = object;
	}

	public T get (int[] indices) {
		return data[getOffset(indices)];
	}

	protected int getOffset (int[] indices) {
		if (indices.length != dimensions.length)
			throw new IllegalArgumentException("Wrong number of indices");
		int offset = 0;
		for (int i=0; i<dimensions.length; ++i) {
			if (indices[i] < 0 || indices[i] >= dimensions[i])
				throw new IndexOutOfBoundsException();
			offset += factors[i] * indices[i];
		}
		return offset;
	}

	public Iterator<int[]> createIterator() {
		return new AA();
	}

	class AA implements Iterator<int[]> {

		int[] tuple;

		public AA() {
			findNext();
		}

		private void findNext() {
			if (tuple==null) {
				if (data.length>0) {
					tuple = new int[dimensions.length];
					Arrays.fill(tuple, 0);
				}
			}
			else {
				for (int i=0; i<dimensions.length; i++) {
					if (tuple[i]<dimensions[i]-1) {
						tuple[i]++;
						return;
					}
					tuple[i] = 0;
				}
				tuple = null;
			}
		}

		public boolean hasNext() {
			return tuple!=null;
		}

		public int[] next() {
			int[] curr = tuple.clone();
			findNext();
			return curr;
		}

		public void remove() {
			throw new RuntimeException("not implemented");
		}

	}

}
