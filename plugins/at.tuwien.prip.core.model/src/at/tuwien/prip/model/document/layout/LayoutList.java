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
package at.tuwien.prip.model.document.layout;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.graph.DocNode;


/**
 * LayoutList.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Aug 31, 2011
 */
public class LayoutList extends LayoutObject
{

	private LayoutObject header;
	
	private List<LayoutObject> items;
	
	/**
	 * Constructor.
	 */
	public LayoutList() {
		items = new ArrayList<LayoutObject>();
	}
	
	public double getAverageLineSpacing () {
		double prev = -1;
		double sum = 0;
		int i=0;
		for (DocNode o : elements)
		{
			double curr = o.getBoundingBox().getCenterY();
			if (prev<0) {
				prev = curr;
			} else {
				sum += curr - prev;
				i++;
			}
		}
		if (i>0) {
			return sum/i;
		}
		else return 0;
	}

	public void setItems(List<LayoutObject> items) {
		this.items = items;
	}

	public List<LayoutObject> getItems() {
		return items;
	}

	public void setHeader(LayoutObject header) {
		this.header = header;
	}

	public LayoutObject getHeader() {
		return header;
	}
}
