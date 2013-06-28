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
import java.util.Iterator;

public class Tuple<T> {

	/* a string index to access column names */
	protected BidiMap<String,Integer> index;

	/* the tuple values */
	protected ArrayList<TupleEntry> array;

	/**
	 * 
	 * Constructor.
	 * 
	 * @param index
	 * @param array
	 */
	public Tuple(String[] index, T[] array) {
		assert index.length==array.length;

		this.array = new ArrayList<TupleEntry>();
		this.index = new BidiMap<String, Integer>();
		for (int i=0; i<index.length; i++) {
			this.array.add(i, new TupleEntry(index[i], array[i]));
			this.index.put(index[i], i);
		}
	}

	/**
	 * Add a column to this tuple.
	 * 
	 * @param description
	 * @param value
	 */
	public void addColumn (String description, T value) {
		index.put(description, index.size()+1); //add to index
		array.add(new TupleEntry(description,value));
	}

	/**
	 * Returns the length, i.e. the number of objects.
	 *
	 * @return number of objects
	 */
	public int length() {
		return array.size();
	}

	/**
	 * Returns the length, i.e. the number of objects.
	 *
	 * @return number of objects
	 */
	public int size() {
		return length();
	}

	/**
	 * Returns the i-th object.
	 *
	 * @param i
	 *                   index number
	 * @return object at this index number
	 */
	public T get(int i) {
		return get(index.reverseGet(i));
	}

	public T get(String index) {
		return array.get(this.index.get(index)).getValue();
	}

	/**
	 * Sets the i-th object.
	 *
	 * @param i
	 *                   index number
	 * @param o
	 *                   object to be set at this index number
	 */
	public void set(int i, T o) {
		array.set(i,new TupleEntry(index.reverseGet(i),o));
	}


	public boolean equals(Object o) {
		if (!(o.getClass().equals(Tuple.class))) {
			return false;
		}
		Tuple<?> t = (Tuple<?>) o;
		if (t.length() != length())
			return false;
		for (int i = 0; i < length(); i++) {
			Object o1 = get(i);
			Object o2 = get(i);
			if (o1 == null && o2 == null)
				continue;
			if (o1 == null || o2 == null || !get(i).equals(t.get(i)))
				return false;
		}
		return true;
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("Tuple[");
		for (int i = 0; i < length(); i++) {
			buf.append(get(i));
			if (i < length() - 1)
				buf.append(",");
		}
		buf.append("]");
		return buf.toString();
	}

	/**
	 * @return the index
	 */
	public BidiMap<String, Integer> getIndex() {
		return index;
	}

	/**
	 * Remap this tuple according to the given index.
	 * 
	 * @param index2
	 */
	public void arrangeBy(BidiMap<String,Integer> index) {

		assert index.size()==this.index.size();

		while (true) {
			Iterator<String> it = index.keySetA().iterator();
			String current, next;
			int i1, i2;
			do {
				current = it.next();
				i1 = index.get(current);
				i2 = this.index.get(current);
			} while (it.hasNext() && i1==i2);

			if (i1==i2) return; //nothing to do --> ONLY EXIT POINT

			do {

				next = this.index.reverseGet(i1);
				this.index.remove(next, i2); //make space
				
				String tmp = this.index.reverseGet(i1);
				this.index.remove(tmp, i1);
				
				this.index.put(current, i2); //move new value into emtpy slot

				next = tmp;
				i1 = index.get(next);

			} while (i1!=i2);

//			if (next!=null) {
//				this.index.put(next, index.get(next));
//			}
		}
	}

	/**
	 * 
	 * Internal subclass
	 * 
	 */
	class TupleEntry {

		protected String description;

		protected T value;

		public TupleEntry(String description, T value ) {
			this.description = description;
			this.value = value;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @return the value
		 */
		public T getValue() {
			return value;
		}
	}


}
