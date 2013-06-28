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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.tuwien.prip.common.datastructures.BidiMap;
import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.model.graph.base.BaseEdge;
import at.tuwien.prip.model.graph.base.BaseGraph;
import at.tuwien.prip.model.graph.base.BaseNode;
import at.tuwien.prip.model.graph.base.IGraph;

/**
 * HyperGraph.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jan 8, 2012
 */
public class HyperGraph<S extends BaseNode, T extends BaseEdge<S>>
extends BaseGraph<S, T> 
implements IHierGraph<S, T>
{

	protected List<HyperNode> hNodes;

	protected List<HyperEdge> hEdges;

	protected Map<HyperNode, HyperNode> contractionMap;

	protected MapList<HyperNode, HyperNode> expansionMap;

	protected BidiMap<S, HyperNode> node2hyperNodeMap;

	/**
	 * Constructor.
	 */
	public HyperGraph() 
	{
		this.hNodes = new ArrayList<HyperNode>();
		this.hEdges = new ArrayList<HyperEdge>();

		this.contractionMap = new HashMap<HyperNode,HyperNode>();
		this.expansionMap = new HashMapList<HyperNode,HyperNode>();

		this.node2hyperNodeMap = new BidiMap<S, HyperNode>();
	}

	/**
	 * 
	 * @param n
	 * @return
	 */
	public HyperNode contractNode (HyperNode n) 
	{
		return contractionMap.get(n);
	}

	/**
	 * 
	 * @param n
	 * @return
	 */
	public List<HyperNode> expandNode (HyperNode n) 
	{
		return expansionMap.get(n);
	}

	/**
	 * Add a single node to this hyper graph.
	 * 
	 * @param n
	 */
	public void addNode(S n)
	{
		HyperNode hn = node2hyperNodeMap.get(n);
		if (hn==null)
		{
			hn = new HyperNode(n);
			node2hyperNodeMap.put(n, hn);
		}

		if (hn!=null && !hNodes.contains(hn)) 
		{
			addHyperNode(hn);
		}
	}

	/**
	 * Add a single hyper node. If necessary also
	 * adds respective group nodes.
	 * 
	 * @param node
	 */
	public void addHyperNode(HyperNode node)
	{
		if (!hNodes.contains(node)) 
		{
			hNodes.add(node);
		}
		for (HyperNode n : node.getNodes())
		{
			if (!hNodes.contains(n)) 
			{
				hNodes.add(n);
				contractionMap.put(n, node);
				expansionMap.putmore(node, n);
			}
		}
	}

	/**
	 * Add a single hyper edge. If necessary also
	 * adds respective source and target nodes.
	 * 
	 * @param edge
	 */
	public void addHyperEdge(HyperEdge edge)
	{
		if (!hEdges.contains(edge)) 
		{
			hEdges.add(edge);
		}

		if (!hEdges.contains(edge.getFrom()))
		{
			addHyperNode(edge.getFrom());
		}

		if (!hEdges.contains(edge.getTo()))
		{
			addHyperNode(edge.getTo());
		}	
	}

	/**
	 * Add a node as a hyper node. Creates a new hyper
	 * node instance and adds the group as its children.
	 * 
	 * @param n
	 * @param group
	 */
	public void addAsHyperNode(S n, List<S> group)
	{
		HyperNode hn = node2hyperNodeMap.get(n);
		if (hn==null)
		{
			hn = new HyperNode(n);
			node2hyperNodeMap.put(n, hn);
		}

		//check if already exists, if so replace
		if (hNodes.contains(hn)) 
		{
			HyperNode contraction = contractNode(hn);
			if (contraction!=null)
			{
				contraction.nodes.remove(hn);
				contraction.nodes.add(hn);
			}

			hNodes.remove(hn);
		}		
		addHyperNode(hn);
	}

	/**
	 * 
	 * @param edge
	 */
	public void addAsHyperEdge(T edge)
	{
		HyperNode hNodeFrom = node2hyperNodeMap.get(edge.getFrom());
		HyperNode hNodeTo = node2hyperNodeMap.get(edge.getTo());
		if (hNodeFrom!=null && hNodeTo!=null) 
		{
			HyperEdge hEdge = new HyperEdge(hNodeFrom, hNodeTo);
			hEdge.setRelation(edge.getRelation());
			addHyperEdge(hEdge);
		}		
	}

	public List<HyperNode> getHNodes() 
	{
		return hNodes;
	}

	public List<HyperEdge> getHEdges() 
	{
		return hEdges;
	}

	public List<S> getNodes() 
	{
		return nodes;
	}

	public List<T> getEdges() 
	{
		return edges;
	}

	/**********************************************************
	 * A hyper node.
	 */
	public class HyperNode extends BaseNode
	{
		private List<HyperNode> nodes = new ArrayList<HyperNode>();

		/**
		 * Constructor.
		 * 
		 * @param node
		 */
		public HyperNode(S node) 
		{
			super(node);
		}

		public List<HyperNode> getNodes() {
			return nodes;
		}

		protected void setNodes(List<HyperNode> nodes)
		{
			this.nodes = nodes;
		}
	}

	/**********************************************************
	 * A hyper edge. 
	 */
	public class HyperEdge extends BaseEdge<HyperNode> 
	{

		/**
		 * Constructor.
		 * 
		 * @param from
		 * @param to
		 */
		public HyperEdge(HyperNode from, HyperNode to)
		{
			this.from = from;
			this.to = to;
		}
	}

	/***********************************************************
	 * Interface IHierGraph
	 */
	@Override
	public List<S> getAllNodes() 
	{
		return nodes;
	}

	/**
	 * 
	 */
	@Override
	public IGraph<S, T> getBaseGraph() 
	{
		//in this implementation, we return all leaves
		List<S> subNodes = new ArrayList<S>();
		for (HyperNode hn : hNodes) 
		{
			if (hn.nodes.size()==0) //leaf node
			{
				subNodes.add(node2hyperNodeMap.reverseGet(hn));
			}
		}

		List<T> subEdges = new ArrayList<T>();
		for (T edge : edges)
		{
			if (subNodes.contains(edge.getFrom()) ||
					subNodes.contains(edge.getTo()))
			{
				subEdges.add(edge);
			}
		}
		
		//create a new sub graph
		BaseGraph<S, T> bg = new BaseGraph<S, T>();
		bg.setNodes(subNodes);
		bg.setEdges(subEdges);
		return bg;
	}

	/**
	 * This corresponds to a contract + expand operation.
	 * See morphological closure (dilation + erosion).
	 */
	@Override
	public List<S> getContractionFamily(S a) 
	{
		//contract
		HyperNode hn = node2hyperNodeMap.get(a);
		if (hn==null) {
			return null;
		}
		HyperNode conHn = contractNode(hn);
		if (conHn==null) {
			return null;
		}

		//expand
		List<HyperNode> expHns = expandNode(hn);
		List<S> result = new ArrayList<S>();
		for (HyperNode expHn : expHns)
		{
			result.add(node2hyperNodeMap.reverseGet(expHn));
		}
		return result;
	}

	/**
	 * 
	 */
	@Override
	public S contractNode(S a) 
	{
		HyperNode hn = node2hyperNodeMap.get(a);
		if (hn==null) {
			return null;
		}
		HyperNode conHn = contractNode(hn);
		if (conHn==null) {
			return null;
		}
		return node2hyperNodeMap.reverseGet(conHn);
	}

	/**
	 * 
	 */
	@Override
	public List<S> expandNode(S a) 
	{
		HyperNode hn = node2hyperNodeMap.get(a);
		if (hn==null) {
			return null;
		}
		List<HyperNode> expHns = expandNode(hn);
		if (expHns==null) {
			return null;
		}
		List<S> result = new ArrayList<S>();
		for (HyperNode expHn : expHns)
		{
			result.add(node2hyperNodeMap.reverseGet(expHn));
		}
		return result;
	}
	
	/**
	 * 
	 */
	@Override
	public IHierGraph<S,T> extractNeighSubGraph(T e)
	{
		List<S> neighNodes = new ArrayList<S>();
		S n = e.getFrom();
		if (!neighNodes.contains(n)) {
			neighNodes.add(n); //add self
		}
		List<T> edges = getEdgesFromTo(n);
		for (T ed : edges) 
		{
			if (!neighNodes.contains(ed.getFrom())) {
				neighNodes.add(ed.getFrom());
			}
			if (!neighNodes.contains(ed.getTo())) {
				neighNodes.add(ed.getTo());
			}
		}
		n = e.getTo();
		if (!neighNodes.contains(n)) 
		{
			neighNodes.add(n); //add self
		}
		edges = getEdgesFromTo(n);
		for (T ed : edges) 
		{
			if (!neighNodes.contains(ed.getFrom())) {
				neighNodes.add(ed.getFrom());
			}
			if (!neighNodes.contains(ed.getTo())) {
				neighNodes.add(ed.getTo());
			}
		}

		return getSubGraph(neighNodes);
	}
	
	/**
	 * 
	 */
	@Override
	public IHierGraph<S,T> extractNeighSubGraph(S n) 
	{
		List<S> neighNodes = new ArrayList<S>();
		//int maxLength = Math.max(getDimensions().height, getDimensions().width);
		neighNodes.add(n);
		List<T> edges = getEdgesFromTo(n);
		for (T e : edges) {
//			if (e.getLength()>=maxLength) {
//				continue;
//			}
			if (!neighNodes.contains(e.getFrom())) {
				neighNodes.add(e.getFrom());
			}
			if (!neighNodes.contains(e.getTo())) {
				neighNodes.add(e.getTo());
			}
		}
		return getSubGraph(neighNodes);
	}

	
	@Override
	public IHierGraph<S, T> getSubGraph(List<S> neighNodes) 
	{
		HyperGraph<S,T> result = new HyperGraph<S,T>();
		result.setNodes(neighNodes);
		return result;
	}
}
