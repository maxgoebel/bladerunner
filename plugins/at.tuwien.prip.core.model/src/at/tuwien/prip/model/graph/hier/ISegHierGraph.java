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
import at.tuwien.prip.model.graph.ISegmentGraph;

/**
 * 
 * ISegHierGraph.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jan 5, 2012
 */
public interface ISegHierGraph extends IHierGraph<DocNode, DocEdge>
{
	@Override
	public ISegmentGraph getBaseGraph();

}
