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

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

/**
 * Rank.java
 *
 * A simple data structure to hold ordered elements.
 *
 * Created: Jul 8, 2009 9:09:50 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Rank<T extends Comparable<T>> {

	protected Vector<T> elements;
	private Iterator<T> iterator;
	
	public Rank() {
		this.elements = new Vector<T>();
	}
	
	public Rank(T element) {
		this();
		addElement(element);
	}
	
	public Rank(Vector<T> elements) {
		this.elements = elements;
		ranKAscending();
	}
	
	public T next () {
		if (this.iterator==null) {
			this.iterator = this.elements.iterator();
		}
		if (this.iterator.hasNext()) {
			return this.iterator.next();
		}
		return null;
	}
	
	public void addElement (T t) {
		elements.add(t);
	}
	
	public void ranKAscending () {
		Collections.sort(elements);
		this.iterator = this.elements.iterator();
	}
	
	public void rankDescending () {
		ranKAscending();
		Collections.reverse(elements);
		this.iterator = this.elements.iterator();
	}
	
	public void shuffle () {
		Collections.shuffle(elements);
		this.iterator = this.elements.iterator();
	}
	
}
