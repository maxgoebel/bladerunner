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

import java.util.HashMap;
import java.util.List;

import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.model.graph.DocEdge;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.utils.DocGraphUtils;

/**
 * DocHyperGraph.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jan 18, 2012
 */
public class DocHyperGraph extends HyperGraph<DocNode, DocEdge>
implements ISegHierGraph
{

	/**
	 * Add a set of nodes to this hyper graph and recompute
	 * hierarchical inclusions.
	 * 
	 * @param nodes
	 */
	public void addNodes (List<DocNode> nodes) 
	{
		nodes.addAll(nodes);
		for (DocNode n : nodes) 
		{
			if (!this.hNodes.contains(n))
			{
				this.addNode(n);
			}
		}
		recomputeInclusions();
	}
	
	/**
	 * 
	 * @param edges
	 */
	public void addEdges (List<DocEdge> edges) 
	{
		edges.addAll(edges);
	}
	
	/**
	 * Recomputes all hierarchical edges via node 
	 * inclusion detection. Runs quadratic in |nodes|.
	 */
	private void recomputeInclusions ()
	{
		//clear existing inclusions
		this.contractionMap = new HashMap<HyperNode,HyperNode>();
		this.expansionMap = new HashMapList<HyperNode,HyperNode>();
		
		//check all node permutations
		for (HyperNode n1 : hNodes) 
		{
			for (HyperNode n2 : hNodes)
			{
				if (n1.equals(n2)) { continue; }
				
				DocNode dn1 = node2hyperNodeMap.reverseGet(n1);
				DocNode dn2 = node2hyperNodeMap.reverseGet(n1);
				
				if (n1==null || n2==null) {
					
				}
				if (DocGraphUtils.contains(dn2, dn1)) 
				{
					contractionMap.put(n1, n2);
					expansionMap.putmore(n2, n1);
				}
				else if (DocGraphUtils.contains(dn1, dn2)) 
				{
					contractionMap.put(n2, n1);
					expansionMap.putmore(n1, n2);
				}
			}
		}
	}

	public ISegmentGraph getBaseGraph() {
		return null;
	}
	
}
