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

import at.tuwien.prip.model.graph.DocEdge;

/**
 * DocEdgeComparator.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Jul 5, 2011
 */
public class DocEdgeComparator implements Comparator<DocEdge> 
{

	@Override
	public int compare(DocEdge o1, DocEdge o2) {
		if (o1.getLength()==o2.getLength()) {
			return 0;
		} else if (o1.getLength()<o2.getLength()) {
			return -1;
		}
		return 1;
	}

}
