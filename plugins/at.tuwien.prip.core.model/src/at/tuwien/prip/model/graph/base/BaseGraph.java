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
package at.tuwien.prip.model.graph.base;

import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.MapList;

/**
 * BaseGraph.java
 *
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jan 5, 2012
 */
public class BaseGraph<S extends BaseNode, T extends BaseEdge<S>>
implements IGraph<S, T>
{
	protected List<S> nodes;
	protected List<T> edges;

	protected MapList<S, T> edgeFromCache;
	protected MapList<S, T> edgeToCache;

	/**
	 * Constructor.
	 */
	public BaseGraph()
	{
		nodes = new CopyOnWriteArrayList<S>();//Collections.synchronizedList(new ArrayList<S>());// new ArrayList<S>();
		edges = new CopyOnWriteArrayList<T>();//Collections.synchronizedList(new ArrayList<T>());//new ArrayList<T>();

		/* edge cache */
		edgeFromCache = new HashMapList<S, T>();
		edgeToCache = new HashMapList<S, T>();
	}

	@Override
	public int getSize() {
		return nodes.size();
	}

	@Override
	public void addEdge(T edge)
	{
//		synchronized (edges)
//		{
			edgeFromCache.putmore(edge.getFrom(), edge);
			edgeToCache.putmore(edge.getTo(), edge);
			this.edges.add(edge);
//		}
//
//		synchronized (nodes)
//		{
			if (!nodes.contains(edge.getFrom())) {
				nodes.add(edge.getFrom());
			}
			if (!nodes.contains(edge.getTo())) {
				nodes.add(edge.getTo());
			}
//		}
	}

	/**
	 * Add a node to this graph.
	 *
	 * @param node
	 */
	@Override
	public void addNode(S node)
	{
//		synchronized (this.nodes)
//		{
			if (!this.nodes.contains(node))
			{
				this.nodes.add(node);
			}
//		}
	}

	public void removeNode(S node)
	{
//		synchronized (this.nodes)
//		{
			if (this.nodes.contains(node))
			{
				this.nodes.remove(node);
			}
//		}
	}

	@Override
	public void addNodes(List<S> nodes)
	{
//		synchronized (this.nodes) {
			for (S node : nodes) {
				addNode(node);
			}
//		}
	}

	@Override
	public List<S> getNodes() {
		return nodes;
	}
	@Override
	public List<T> getEdges() {
		return edges;
	}

	public List<T> getEdgesFrom2 (S n)
	{
		return edgeFromCache.get(n);
	}

	@Override
	public List<T> getEdgesFrom(S node)
	{
		List<T> retVal = new ArrayList<T>();

		//shortcut
		if (edgeFromCache.size()>0)
		{
			retVal = getEdgesFrom2(node);
		}

		if (retVal!=null && retVal.size()>0) {
			return retVal;
		}
		retVal = new ArrayList<T>();

//		synchronized (edges)
//		{
			for (Object o : edges)
			{
				@SuppressWarnings("unchecked")
				T e = (T)o;
				if (e.getFrom()==node || e.getTo()==node)
					retVal.add(e);
			}
//		}

		return retVal;
	}

	public List<T> getEdgesTo2 (S n)
	{
		return edgeToCache.get(n);
	}

	@Override
	public List<T> getEdgesTo(S n)
	{
		List<T> retVal = new ArrayList<T>();

		//shortcut
		if (edgeToCache.size()>0)
		{
			retVal = getEdgesTo2(n);
		}
		if (retVal!=null) {
			return retVal;
		}
		retVal = new ArrayList<T>();

//		synchronized (edges)
//		{
			for (Object o : edges)
			{
				@SuppressWarnings("unchecked")
				T e = (T) o;
				if (e.getTo() == n)
					retVal.add(e);
			}
//		}

		return retVal;
	}

	@Override
	public List<T> getEdgesFromTo(S n)
	{
		List<T> retVal = new ArrayList<T>();
		List<T> edgesCopy;
//		synchronized (edges)
//		{
			edgesCopy = new ArrayList<T>(edges);
//		}
		for (T o : edgesCopy)
		{
			T e = o;
			if (e.getFrom() == n || e.getTo() == n)
				retVal.add(e);
		}
		return retVal;
	}

	@Override
	public T getEdgeBetween(S from, S to)
	{
		List<T> edges = new ArrayList<T>(getEdgesFrom(from));
		for (T e : edges) {
			if (e.getTo().equals(to)) {
				return e;
			}
		}
		edges = new ArrayList<T>(getEdgesFrom(to));
		for (T e : edges) {
			if (e.getTo().equals(from)) {
				return e;
			}
		}
		return null;
	}

	@Override
	public IGraph<S, T> extractNeighSubGraph(T e)
	{
		List<S> neighNodes = new ArrayList<S>();
		S n = e.getFrom();
		if (!neighNodes.contains(n)) {
			neighNodes.add(n); //add self
		}
		List<T> edges = new ArrayList<T>(getEdgesFromTo(n));
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
		if (!neighNodes.contains(n)) {
			neighNodes.add(n); //add self
		}
		edges = new ArrayList<T>(getEdgesFromTo(n));
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
	public IGraph<S, T> extractNeighSubGraph(S n)
	{
		List<S> neighNodes = new ArrayList<S>();
		neighNodes.add(n);
		List<T> edges = new ArrayList<T>(getEdgesFromTo(n));
		for (T e : edges)
		{
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
	public S getSeedNode()
	{
		List<S> edges = getNodes();
		Random rand = new Random();
		return edges.get(rand.nextInt(edges.size()));
	}

	@Override
	public T getSeedEdge()
	{
		List<T> edges = getEdges();
		Random rand = new Random();
		return edges.get(rand.nextInt(edges.size()));
	}

	@Override
	public IGraph<S, T> getSubGraph(List<S> neighNodes)
	{
		BaseGraph<S, T> result = new BaseGraph<S, T>();
		result.nodes.addAll(neighNodes);

//		synchronized (edges)
//		{
			for (T o : edges)
			{
				T e = (T)o;
				if (neighNodes.contains(e.getFrom()) && neighNodes.contains(e.getTo())) {
					result.edges.add(e);
				}
			}
//		}s
		return result;
	}

	@Override
	public IGraph<S, T> getSubGraph(S[] neighNodes) {
		List<S> nodes = new ArrayList<S>();
		for (S n : neighNodes)
		{
			nodes.add(n);
		}
		return getSubGraph(nodes);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IGraph<S, T> deepCopy()
	{
		BaseGraph<S, T> retVal = new BaseGraph<S,T>();
		retVal.edgeFromCache =  new HashMapList<S, T>();
		retVal.edgeToCache = new HashMapList<S, T>();

		// HashMap from nodes to newly cloned nodes...
		HashMap<S, S> nhm = new HashMap<S, S>();

//		synchronized (nodes)
//		{
			for (S n : nodes)
			{
				S cln = (S) n.clone();

				retVal.getNodes().add(cln);
				nhm.put(n, cln);
			}
//		}

//		synchronized (edges)
//		{
			for (T e : edges)
			{
				T cae = (T)e.clone();
				cae.setFrom(nhm.get(cae.getFrom()));
				cae.setTo(nhm.get(cae.getTo()));
				retVal.getEdges().add(cae);

				/* the edge cache */
				retVal.edgeFromCache.putmore(cae.getFrom(), cae);
				retVal.edgeToCache.putmore(cae.getTo(), cae);
			}
//		}

		return retVal;
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

//		synchronized (nodes)
//		{
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
//		}
		return a;
	}

	/**
	 * Construct adjacency matrix from this graph.
	 *
	 * @return, an int array representation of the adjacency matrix
	 */
	@Override
	public int[][] asAdjacencyMatrixArr()
	{
		int[][] a = new int[nodes.size()][nodes.size()];

		for (int i=0; i<nodes.size(); i++) {
			for (int j=0; j<nodes.size(); j++) {
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

	@Override
	public List<List<S>> getConnectedComponents()
	{
		List<List<S>> result = new ArrayList<List<S>>();
		List<S> visited = new ArrayList<S>();

		for (S node : getNodes())
		{
			if (visited.contains(node)) continue;

			visited.add(node);

			//start a new component
			List<S> component = new ArrayList<S>();

			Queue<S> queue = new ArrayDeque<S>();
			queue.add(node);

			while (!queue.isEmpty())
			{
				S head = queue.remove();
				for (T edge : getEdgesFromTo(head)) {
					S to = null;
					if (edge.getFrom().equals(head)) {
						to = edge.getTo();
					} else {
						to = edge.getFrom();
					}
					if (!visited.contains(to)) {
						visited.add(to);
						queue.add(to);
						component.add(to);
					}
				}
			}

			result.add(component);
		}

		return result;
	}

	public void setNodes(List<S> nodes)
	{
//		synchronized (nodes)
//		{
			this.nodes = nodes;
//		}
	}

	public void setEdges(List<T> edges)
	{
//		synchronized (edges)
//		{
			this.edges = edges;
//		}
	}

	@Override
	public Rectangle getDimensions()
	{
		Rectangle result = new Rectangle();
		for (S node : getNodes())
		{
//			if (node instanceof AgentState)
//			{
//				AgentState state = (AgentState) node;
//				Rectangle bounds = state.getBounds();
//				result = result.union(bounds);
//			}
		}
		return result;
	}

	@Override
	public String serializeText() {
		return null;
	}

}
