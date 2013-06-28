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
package at.tuwien.prip.model.graph;

import at.tuwien.prip.model.document.segments.GenericSegment;

public class Pixel extends GenericSegment {

	private int val = -1;
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public Pixel(int x, int y) {
		super(x, y, 1, 1);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public Pixel(int x, int y, int val) 
	{
		super(x, y, 1, 1);
		this.val = val;
	}
	
	public int getVal() {
		return val;
	}
	
	public void setVal(int val) {
		this.val = val;
	}
}
