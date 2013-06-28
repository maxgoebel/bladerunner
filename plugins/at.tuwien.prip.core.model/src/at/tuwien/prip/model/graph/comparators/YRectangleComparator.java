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
package at.tuwien.prip.model.graph.comparators;

import java.awt.Rectangle;
import java.util.Comparator;

/**
 * YRectangleComparator.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Sep 12, 2011
 */
public class YRectangleComparator implements Comparator<Rectangle> {

	@Override
	public int compare(Rectangle o1, Rectangle o2) {
		// sorts in y order
		double y1 = o1.getMinY();
		double y2 = o2.getMinY();

		return (int) (y2 - y1);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj.equals(this);
	}

}
