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
package at.tuwien.prip.model.graph.hier;

import at.tuwien.prip.model.graph.DocEdge;
import at.tuwien.prip.model.graph.DocNode;

/**
 * DocEdgeHier.java
 * 
 * A hierarchical document edge.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: May 10, 2011
 * 
 */
public abstract class DocEdgeHier extends DocEdge {

	boolean isActive = false;
	
	public DocEdgeHier(DocNode nodeFrom, DocNode nodeTo) {
		super(nodeFrom, nodeTo);
	}
	
	public void activate () {
		isActive = true;
	}
	
	public void deactivate () {
		isActive = false;
	}

}
