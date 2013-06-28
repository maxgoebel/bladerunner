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

public class HeightComparator implements Comparator<GenericSegment>
{
	public int compare(GenericSegment obj1, GenericSegment obj2)
	{
		// sorts in height order
		double y1 = obj1.getY2() - obj1.getY1();
		double y2 = obj2.getY2() - obj2.getY1();

		return (int) (y1 - y2);
	}

	public boolean equals(Object obj)
	{
		return obj.equals(this);
	}
}
