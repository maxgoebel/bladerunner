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

import at.tuwien.prip.model.document.segments.TextSegment;
import at.tuwien.prip.model.utils.Utils;

/**
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class XYTextComparator implements Comparator<TextSegment>
{
	public int compare(TextSegment obj1, TextSegment obj2)
	{
		// changed from GenericSegment in order to allow
		// a tolerance on font size 7.08.08
		// double x1 = obj1.getX(), y1 = obj1.getY();
		// double x2 = obj2.getX(), y2 = obj2.getY();
		float x1 = obj1.getX1(), y1 = obj1.getY1();
		float x2 = obj2.getX1(), y2 = obj2.getY1();
		//if (y1 == y2)
		// modified 8.11.06
		float tolerance = (float)(obj1.getFontSize() + obj2.getFontSize()) * Utils.sameLineTolerance; // 10% of avg
		if (Utils.within(y1, y2, tolerance))
		{
			return (int) (x1 - x2);
		} 
		else
		{
			return (int) (y2 - y1);
			// return (int) (y1 - y2);
		}
	}

	public boolean equals(Object obj)
	{
		return obj.equals(this);
	}
}
