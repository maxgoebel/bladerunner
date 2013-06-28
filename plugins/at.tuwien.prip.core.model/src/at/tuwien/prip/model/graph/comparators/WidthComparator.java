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

import java.util.Comparator;

import at.tuwien.prip.model.document.segments.GenericSegment;

/**
 * 
 * WidthComparator.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Sep 6, 2011
 */
public class WidthComparator implements Comparator<GenericSegment>
{
	public int compare(GenericSegment obj1, GenericSegment obj2)
	{
		// sorts in x order
		double x1 = obj1.getX2() - obj1.getX1();
		double x2 = obj2.getX2() - obj2.getX1();

		return (int) (x1 - x2);
	}

	public boolean equals(Object obj)
	{
		return obj.equals(this);
	}
}
