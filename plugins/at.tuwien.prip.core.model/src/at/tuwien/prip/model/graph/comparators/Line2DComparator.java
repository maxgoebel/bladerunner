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

import java.awt.geom.Line2D;
import java.util.Comparator;

/**
 * 
 * Line2DComparator.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Jul 6, 2011
 */
public class Line2DComparator implements Comparator<Line2D> 
{

	@Override
	public int compare(Line2D o1, Line2D o2) {
		double l1 = o1.getP1().distance(o1.getP2());
		double l2 = o2.getP1().distance(o2.getP2());
		if (l1<l2){
			return -1;
		} else if (l1==l2) {
			return 0;
		}
		return 1;
	}

}
