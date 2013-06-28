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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import at.tuwien.prip.common.datastructures.BidiMap;
import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.common.datastructures.TreeMap;
import at.tuwien.prip.model.graph.DocumentConstants;
import at.tuwien.prip.model.graph.base.BaseEdge;
import at.tuwien.prip.model.graph.base.BaseNode;
import at.tuwien.prip.model.graph.base.IGraph;
import at.tuwien.prip.model.graph.hier.IHierGraph;

public abstract class LevelGraph<S extends BaseNode, T extends BaseEdge<S>> 
implements IHierGraph<S,T>
{
	protected List<S> nodes;

	protected List<T> edges;
	
	protected int defaultLevel = DocumentConstants.WORD_LEVEL;//TEXTLINE_LEVEL;
	
	/** **/
	protected BidiMap<Integer, IGraphLevel<S, T>> stack;

	protected Map<S, Integer> node2LevelMap;

	protected Map<S,S> contractionCache;
	protected MapList<S,S> contractionFamily;

	TreeMap<S> treeMap = null;

	public LevelGraph() 
	{
		this.stack = new BidiMap<Integer, IGraphLevel<S, T>>();
		this.node2LevelMap = new HashMap<S, Integer>();
		this.nodes = new ArrayList<S>();
		this.edges = new ArrayList<T>();

		this.contractionCache = new HashMap<S,S>();
		this.contractionFamily = new HashMapList<S,S>();
	}
	
	
	@Override
	public int getSize() 
	{
		return getNodes().size();
	}
	
	@Override
	public void addEdge(T edge)
	{
		this.edges.add(edge);
	}
	
	@Override
	public void addNode(S node) 
	{
		if (!nodes.contains(node))
		{
			nodes.add(node);
		}
	}
	
	@Override
	public void addNodes(List<S> nodes) 
	{
		for (S n : nodes) 
		{
			addNode(n);
		}
	}
	
	@Override
	public List<S> getNodes() {
		return nodes;
	}

	@Override
	public List<T> getEdgesFrom(S n)
	{
		List<T> retVal = new ArrayList<T>();
		for (T o : edges)
		{
			T e = o;
			if (e.getFrom() == n)
				retVal.add(e);
		}
		return retVal;
	}

	@Override
	public List<T> getEdgesTo(S n)
	{
		List<T> retVal = new ArrayList<T>();
		for (T o : edges)
		{
			T e = o;
			if (e.getTo() == n)
				retVal.add(e);
		}
		return retVal;
	}
	
	@Override
	public T getEdgeBetween(S from, S to)
	{
		for (T e : getEdgesFrom(from))
		{
			if (e.getTo().equals(to)) {
				return e;
			}
		}
		for (T e : getEdgesFrom(to))
		{
			if (e.getTo().equals(from)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Return all the nodes in all levels of this stack.
	 * @return
	 */
	@Override
	public List<S> getAllNodes() 
	{
		List<S> result = new ArrayList<S>();
		Iterator<Integer> it = getAvailableLevelIterator();
		while(it.hasNext()) 
		{
			result.addAll(getLevel(it.next()).getGraph().getNodes());
		}
		return result;
	}


	/**
	 * Expand a single node into the lower level.
	 * 
	 * @param a, the node to expand
	 * @return
	 */
	@Override
	public List<S> expandNode (S a) 
	{
		List<S> result = new ArrayList<S>();

		int levelIdx = node2LevelMap.get(a);
		IGraphLevel<S, T> level = stack.get(levelIdx);
		List<T> edges = level.getExpansionMap().get(a);
		if (edges!=null) {
			for (T edge : edges) {
				result.add(edge.getTo());
			}
		}
		return result;
	}

	/**
	 * Contract a single node into the higher level.
	 * 
	 * @param a, the node to contract
	 * @return
	 */
	@Override
	public S contractNode (S a) 
	{
		int levelIdx = node2LevelMap.get(a);
		IGraphLevel<S, T> level = stack.get(levelIdx);
		if (level==null)  return null;
		T b = level.getContractionMap().get(a);
		if (b==null) {
			return null;
		}
		return (b.getFrom()==a)?b.getTo():b.getFrom();
	}
	
	/**
	 * 
	 */
	@Override
	public IGraph<S,T> getBaseGraph() 
	{
		IGraphLevel<S,T> level = getLevel(defaultLevel);
		if (level==null)
		{
			if (getAvailableLevelIterator().hasNext()) 
			{
				int i = getAvailableLevelIterator().next();
				level = getLevel(i);
				if (level==null) {
					return null;
				}
			} else {
				return null;
			}
		}
		return level.getGraph();
	}

	@Override
	public List<S> getContractionFamily(S a) 
	{
		return getContractionFamily().get(a);
	}

	@Override
	public List<T> getEdges() 
	{
		return edges;
	}

	@Override
	public List<T> getEdgesFromTo(S n)
	{
		List<T> retVal = new ArrayList<T>();
		for (T o : edges)
		{
			T e = o;
			if (e.getFrom() == n || e.getTo() == n)
				retVal.add(e);
		}
		return retVal;
	}
	
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

	@Override
	public  IHierGraph<S,T> extractNeighSubGraph(S n)
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
	public abstract IHierGraph<S,T> getSubGraph(List<S> nodes);
	
	@Override
	public S getSeedNode() 
	{
		return getBaseGraph().getSeedNode();
	}

	@Override
	public T getSeedEdge() 
	{
		return getBaseGraph().getSeedEdge();
	}
	
	@Override
	public List<List<S>> getConnectedComponents()
	{
		return getBaseGraph().getConnectedComponents();
	}
	
	/**
	 * Construct adjacency matrix from this graph.
	 * 
	 * @return, a vector list representation of the adjacency matrix
	 */
	@Override
	public Vector<Vector<Integer>> asAdjacencyMatrix () 
	{
		Vector<Vector<Integer>> a = new Vector<Vector<Integer>>();

		for (S n : nodes) 
		{
			Vector<Integer> row = new Vector<Integer>();
			for (S m : nodes)
			{
				if (n.equals(m)) {
					row.add(0);
				} else {
					if (getEdgeBetween(n, m)!=null) {
						row.add(1);
					} else {
						row.add(0);
					}
				}
			}
			a.add(row);
		}
		return a;
	}

	/**
	 * Construct adjacency matrix from this graph.
	 * 
	 * @return, an integer array representation of the adjacency matrix
	 */
	@Override
	public int[][] asAdjacencyMatrixArr() 
	{
		int[][] a = new int[nodes.size()][nodes.size()];

		for (int i=0; i<nodes.size(); i++) 
		{
			for (int j=0; j<nodes.size(); j++) 
			{
				if (i==j) {
					a[i][j]=0;
				} else {
					if (getEdgeBetween(nodes.get(i), nodes.get(j))!=null) {
						a[i][j]=1;
					} else {
						a[i][j]=0;
					}
				}
			}
		}
		return a;
	}

	
	/**
	 * Return all nodes in a tree structure reflecting
	 * the segmentation hierarchy.
	 * @return
	 */
	public TreeMap<S> getAllNodesAsTree () 
	{
		if (treeMap!=null) {
			return treeMap;
		}
		TreeMap<S> result = new TreeMap<S>();

		Queue<S> open = new LinkedList<S>();
		List<S> rootNodes = getAllNodes();//getAllRootNodes();
		for (S root : rootNodes) {
			open.add(root);
			result.addAsRoot(root);
		}

		while (!open.isEmpty()) {
			S next = open.remove();
			List<S> expansionList = expandNode(next);
			for (S expNode : expansionList) {
				result.addChildBelow(expNode, next);
				open.add(expNode);
			}
		}

		treeMap = result;
		return result;
	}

	/**
	 * Get all the root nodes in this hierarchical stack.
	 * 
	 * @return
	 */
	public List<S> getAllRootNodes () 
	{
		List<S> result = new ArrayList<S>();
		List<S> visited = new ArrayList<S>();
		for (S node : getAllNodes()) {
			if (visited.contains(node)) {
				continue;
			}
			visited.add(node);

			S parent = null;
			do {
				parent = contractionCache.get(node);
				if (parent!=null) {
					visited.add(parent);
					node = parent;
				}
			} while (parent!=null);

			if (!result.contains(node)) {
				result.add(node);			
			}
		}
		return result;
	}

	/**
	 * Return all the nodes in all levels of this stack.
	 * @return
	 */
	public List<T> getAllEdges()
	{
		List<T> result = new ArrayList<T>();
		Iterator<Integer> it = getAvailableLevelIterator();
		while(it.hasNext()) {
			result.addAll(getLevel(it.next()).getGraph().getEdges());
		}
		return result;
	}

	/**
	 * 
	 * @param n
	 * @return
	 */
	public boolean containsNode (S n)
	{
		return getAllNodes().contains(n);
	}

	/**
	 * Expand the current level into a lower-level representation.
	 * 
	 * @param currentLevel
	 * @return
	 */
	public IGraph<S,T> expandLevel(IGraphLevel<S,T> currentLevel) 
	{
		IGraph<S,T> result = null;
		int lowerLevel = getLowerLevel(currentLevel);
		if (lowerLevel>=0) {
			result = stack.get(lowerLevel).getGraph();
		}
		return result;
	}

	/**
	 * Contract the current level into a higher-level representation.
	 * 
	 * @param currentLevel
	 * @return
	 */
	public IGraph<S,T> contractLevel(IGraphLevel<S,T> currentLevel) 
	{
		IGraph<S,T> result = null;
		int higherLevel = getHigherLevel(currentLevel);
		if (higherLevel>=0) {
			result = stack.get(higherLevel).getGraph();
		}
		return result;
	}

	/**
	 * Add a level to the stack
	 * 
	 * @param level
	 */
	public void addLevel (IGraphLevel<S,T> level) 
	{
		this.nodes.addAll(level.getGraph().getNodes());
		this.edges.addAll(level.getGraph().getEdges());

		if (!stack.keySetB().contains(level.getGraph()) && 
				!stack.keySetA().contains(level.getLevel())) 
		{
			stack.put(level.getLevel(), level);

			//clear node to level map
			for (S d : node2LevelMap.keySet())
			{
				if (node2LevelMap.get(d)==level.getLevel()) {
					node2LevelMap.remove(d);
				}
			}
			for(S n : level.getGraph().getNodes()) 
			{
				node2LevelMap.put(n, level.getLevel());				
			}
		}	

		treeMap = null; //clear tree cache
	}

	/**
	 * remove a level from the stack
	 * @param level
	 */
	public void removeLevel (IGraphLevel<S,T> level) 
	{
		if (stack.get(level.getLevel())!=null) {
			stack.remove(level.getLevel(),level);
		}
	}

	/**
	 * 
	 * @return
	 */
	public Iterator<Integer> getAvailableLevelIterator () 
	{
		return stack.keySetA().iterator();
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public IGraphLevel<S, T> getLevel (int index)
	{
		return stack.get(index);
	}
	
	/**
	 * 
	 * @param level
	 * @return
	 */
	protected int getHigherLevel(IGraphLevel<S,T> level) 
	{
		int result = -1; //does not exist
		Set<Integer> levels = stack.keySetA();
		List<Integer> orderedList = new ArrayList<Integer>(levels);
		Collections.sort(orderedList);

		for (int i=0; i<orderedList.size()-1; i++) 
		{
			if (orderedList.get(i)==level.getLevel()) 
			{
				result = orderedList.get(i+1);
				break;
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param level
	 * @return
	 */
	protected int getLowerLevel(IGraphLevel<S,T> level) 
	{
		int result = -1; //does not exist
		Set<Integer> levels = stack.keySetA();
		List<Integer> orderedList = new ArrayList<Integer>(levels);
		Collections.sort(orderedList);

		for (int i=1; i<orderedList.size(); i++) 
		{
			if (orderedList.get(i)==level.getLevel()) 
			{
				result = orderedList.get(i-1);
				break;
			}
		}
		return result;
	}
	
	public Map<S, S> getContractionCache() 
	{
		return contractionCache;
	}

	private MapList<S, S> getContractionFamily()
	{
		return contractionFamily;
	}

	public TreeMap<S> getTreeMap()
	{
		if (treeMap==null) {
			this.treeMap = getAllNodesAsTree();
		}
		return treeMap;
	}


	public int getDefaultLevel() 
	{
		return defaultLevel;
	}

	public void setDefaultLevel(int defaultLevel) 
	{
		this.defaultLevel = defaultLevel;
	}
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public boolean containsEdge (T e) 
	{
		return getAllEdges().contains(e);
	}
	
}
