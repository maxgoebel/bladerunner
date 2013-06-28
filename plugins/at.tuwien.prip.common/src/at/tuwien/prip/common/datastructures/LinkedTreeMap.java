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

import java.util.Iterator;
import java.util.LinkedList;

public class LinkedTreeMap<T>
extends LinkedList<TreeMap<T>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LinkedTreeMap() {
		super();
	}
	
	public void addToEnd (TreeMap<T> next) {
		add(next);
//		TreeMap<T> last = (TreeMap<T>) get(size()-1);
		
		/* connect */
//		last.connectTo(next);
	}
	
	public void insertAfterIndex (int index, TreeMap<T> after) {
		
	}
	
	public void insertBeforeIndex (int index, TreeMap<T> prev) {
		
	}

	public TreeMap<T> removeFromEnd () {
		return null;
	}

	@SuppressWarnings("unchecked")
	public void addChildTo(T parent, T child) {
		if (size()==0) {
			add(new TreeMap<T>());
		}
		Iterator it = iterator();
		while (it.hasNext()) {
			TreeMap<T> tree =
				(TreeMap<T>) it.next();
//			try {
				tree.addChildBelow(parent, child);
//			} catch (PatternTreeAccessException e) {
//				TreeMap<T> tree2 = new TreeMap<T>();
//				try {
//					tree2.addChildTo(parent, child);
//					add(tree2);
//				} catch (PatternTreeAccessException e1) {
//					// this cannot happen: safely ignore...
//				}
//			}
		}
	}
}
