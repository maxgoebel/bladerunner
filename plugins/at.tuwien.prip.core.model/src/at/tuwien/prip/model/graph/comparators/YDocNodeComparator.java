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

import at.tuwien.prip.model.graph.DocNode;



/**
 * YDocNodeComparator.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 9, 2011
 */
public class YDocNodeComparator implements Comparator<DocNode> {

	@Override
	public int compare(DocNode o1, DocNode o2) {
		// sorts in y order
		double y1 = o1.getSegY1();
		double y2 = o2.getSegY1();

		return (int) (y2 - y1);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj.equals(this);
	}

}
