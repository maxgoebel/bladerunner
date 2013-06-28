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
 * XDocNodeComparator.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 9, 2011
 */
public class XDocNodeComparator implements Comparator<DocNode> {

	@Override
	public int compare(DocNode o1, DocNode o2) {
		// sorts in x order
		double x1 = o1.getSegX1();
		double x2 = o2.getSegX1();

		return (int) (x1 - x2);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj.equals(this);
	}
	
}
