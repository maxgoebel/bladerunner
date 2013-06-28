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
package at.tuwien.prip.model.graph.hier.level;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.model.graph.base.BaseEdge;
import at.tuwien.prip.model.graph.base.BaseGraph;
import at.tuwien.prip.model.graph.base.BaseNode;
import at.tuwien.prip.model.graph.base.IGraph;
import at.tuwien.prip.model.graph.hier.IHierGraph;

/**
 * BaseLevelGraph.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jan 7, 2012
 */
public class BaseLevelGraph extends LevelGraph<BaseNode, BaseEdge<BaseNode>>
{
	@Override
	public IGraph<BaseNode, BaseEdge<BaseNode>> getSubGraph(BaseNode[] neighNodes) 
	{
		return getSubGraph(ListUtils.toList(neighNodes, BaseNode.class));
	}
	
	/**
	 * Get the subtree made up from a list of given nodes.
	 * 
	 * @param nodes
	 * @return
	 */
	public IHierGraph<BaseNode,BaseEdge<BaseNode>> getSubGraph(List<BaseNode> nodes)
	{
		LevelGraph<BaseNode,BaseEdge<BaseNode>> result = new BaseLevelGraph();

		Stack<BaseNode> open = new Stack<BaseNode>();
		List<BaseNode> visited = new ArrayList<BaseNode>();
		MapList<Integer,BaseNode> level2NodeMap = new HashMapList<Integer, BaseNode>();

		open.addAll(nodes);
		while (!open.isEmpty()) 
		{
			BaseNode node = open.pop();
			visited.add(node);
			int level = node2LevelMap.get(node);
			level2NodeMap.putmore(level, node);

			//check contraction and expansion nodes
			BaseNode above = contractNode(node);
			if (above!=null && !visited.contains(above) && !open.contains(above)) {
				open.add(above);
			}
			List<BaseNode> belows = expandNode(node);
			for (BaseNode below : belows)
			{
				if (below!=null && !visited.contains(below) && !open.contains(below)) {
					open.add(below);
				}	
			}
		}

		for (int key : level2NodeMap.keySet()) 
		{
			BaseGraph<BaseNode, BaseEdge<BaseNode>> dg = 
				new BaseGraph<BaseNode, BaseEdge<BaseNode>>();
			List<BaseNode> levelNodes = level2NodeMap.get(key);
			dg.setNodes(levelNodes);

			for (BaseEdge<BaseNode> o : getAllEdges())
			{
				BaseEdge<BaseNode> e = o;
				if (levelNodes.contains(e.getFrom()) && levelNodes.contains(e.getTo())) {
					dg.getEdges().add(e);
				}
			}
			
			IGraphLevel<BaseNode, BaseEdge<BaseNode>> level = null;
			level = new SimpleLevel();
			level.setGraph(dg);
		}

		result.contractionCache = new HashMap<BaseNode, BaseNode>(contractionCache);
		result.contractionFamily = new HashMapList<BaseNode, BaseNode>();
		for (BaseNode node : result.getAllNodes()) 
		{
			List<BaseNode> conFam = contractionFamily.get(node);
			if (conFam!=null && conFam.size()>0) {
				result.contractionFamily.putmore(node, conFam);
			}
		}
		return result;
	}
	
	@Override
	public void removeNode(BaseNode node) {
		if (this.nodes.contains(node))
		{
			this.nodes.remove(node);
		}
	}
	
	@Override
	public IGraph<BaseNode, BaseEdge<BaseNode>> deepCopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getDimensions() {

		return null;
	}

	@Override
	public String serializeText() {
		// TODO Auto-generated method stub
		return null;
	}

}
